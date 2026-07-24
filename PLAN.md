# Sport-Courtify: Roadmap for a First Spring Boot CV Project

## Context

This is a brand-new, essentially untouched Spring Initializr skeleton (`sport-courtify`, one commit "init project") that the user wants to grow into their first Java Spring Boot project — a sport court/venue booking system — to put on their CV. The goal is dual-purpose: a genuine learning exercise in Spring Boot (user has general backend experience but is new to Spring specifically) **and** a polished, demo-able portfolio piece.

Current state confirmed by reading the repo:
- `pom.xml`: Spring Boot **4.1.0** parent, Java **21**, `groupId` = `com.sport-courtify` (**invalid** — hyphens aren't allowed in Maven group IDs/Java package segments), `artifactId` = `demo`. Only dependencies: `spring-boot-starter-webmvc` and `spring-boot-starter-webmvc-test`. Empty `<name>`, `<description>`, `<licenses>`, `<developers>`, `<scm>` blocks.
- One bare `DemoApplication.java` (package `com.sport_courtify.demo`) and one empty `contextLoads()` test.
- `application.properties` has just `spring.application.name=demo`.
- No `.gitignore`, no README (only auto-generated `HELP.md`), no database, no Docker, no CI, no domain code of any kind.

User's answers when scoping this:
- **Experience**: comfortable with backend dev generally, new to Spring Boot idioms specifically — plan should focus on Spring conventions, not backend fundamentals.
- **v1 scope**: "court/venue booking core loop" — users, venues/courts, availability, create/cancel bookings, with correct double-booking prevention. Auth/roles, payments, notifications, reviews are explicitly **out of v1**, deferred to a later phase.
- **Purpose**: both learning and CV polish equally — justifies covering solid Spring/JPA/testing breadth *and* investing in README/Docker/CI polish.

This plan is a **staged roadmap**, not a one-shot change — it's meant to be worked through incrementally (likely across multiple sessions), each phase producing something runnable.

---

## Phase 0 — Housekeeping & Foundation

- Fix `pom.xml`: `groupId` → `com.sportcourtify` (no hyphen), `artifactId` → `sport-courtify`, fill in `<name>`/`<description>`, drop the empty `<licenses>`/`<developers>`/`<scm>` blocks until there's a real LICENSE.
- Rename Java package `com.sport_courtify.demo` → `com.sportcourtify`; rename `DemoApplication` → `SportCourtifyApplication`, `DemoApplicationTests` → `SportCourtifyApplicationTests`.
- Update `application.properties`: `spring.application.name=sport-courtify`.
- Add a proper Maven/Spring `.gitignore` (`target/`, `.idea/`, `*.iml`, `.env`, etc.).
- Delete `HELP.md` (Spring Initializr boilerplate — will be replaced by a real README in Phase 6).
- **Done when**: `./mvnw clean verify` passes, package is `com.sportcourtify`, `.gitignore` in place, changes committed.

## Phase 1 — Domain Model & Persistence Skeleton

Add to `pom.xml`: `spring-boot-starter-data-jpa`, `org.postgresql:postgresql` (runtime), `org.flywaydb:flyway-core` + `flyway-database-postgresql`, `org.springframework.boot:spring-boot-flyway` (Spring Boot 4.1's autoconfigure is split per-integration — this module is required separately from `flyway-core` or Flyway silently never runs), and Lombok (`org.projectlombok:lombok`, optional).

All entities have `createdAt`/`updatedAt` timestamps, populated automatically via a shared `Auditable` `@MappedSuperclass` (`com.sportcourtify.common.Auditable`, using Spring Data JPA's `@CreatedDate`/`@LastModifiedDate` + `@EnableJpaAuditing` in `common/config/JpaAuditingConfig`) rather than duplicating `@PrePersist`/`@PreUpdate` boilerplate per entity.

Domain entities (package-by-feature: `venue/`, `court/`, `user/`, `booking/`), each extending `Auditable`:
- **User** — `id`, `fullName`, `email` (unique), `phone`, + `createdAt`/`updatedAt`.
- **Venue** — `id`, `name`, `address`, `city`, `openTime`, `closeTime`, + `createdAt`/`updatedAt`.
- **Court** — `id`, `venue` (`@ManyToOne`), `name`, `sportType` (enum, `@Enumerated(STRING)`), `pricePerHour`, `active`, + `createdAt`/`updatedAt`.
- **Booking** — `id`, `court` (`@ManyToOne`), `user` (`@ManyToOne`), `startTime`/`endTime` (`LocalDateTime`), `status` (enum: `CONFIRMED`/`CANCELLED`), `cancelledAt`, + `createdAt`/`updatedAt`.

Use unidirectional `@ManyToOne` (Court→Venue, Booking→Court/User) for v1 simplicity. Write `V1__init_schema.sql` as the first Flyway migration (every table gets `created_at`/`updated_at` columns); use `spring.jpa.hibernate.ddl-auto=validate` once schema stabilizes (not `update`).

Add `docker-compose.yml` with a Postgres service for local dev (mapped to host port **5433**, not 5432, to avoid clashing with a locally-installed Postgres).

- **Done when**: app boots against local Postgres via docker-compose, Flyway migration applies cleanly, empty `JpaRepository` interfaces exist for each entity, all entities carry audited `createdAt`/`updatedAt`.

## Phase 2 — CRUD for Venue / Court / User ✅ Done

- Standard REST CRUD (`GET/POST/PUT/DELETE`) for Venue, Court, User via DTOs (never expose entities directly) → Service → Controller layering.
- Add `spring-boot-starter-validation`; annotate request DTOs (`@NotBlank`, `@Email`, etc.).
- **Done when**: venues, courts (nested under a venue), and users can be created/listed/fetched/updated/deleted via curl/Postman/Swagger.

Implementation notes:
- `venue/`, `court/`, `court/dto/` etc. hold `*Request`/`*Response` records, a `*Service`, and a `*Controller`; entities are never returned directly.
- Courts are nested under venues: `/api/venues/{venueId}/courts[/{courtId}]`; the service verifies the court actually belongs to the given venue (404 otherwise), not just that the court id exists.
- Added `common/exception/ResourceNotFoundException` (`@ResponseStatus(NOT_FOUND)`) as an interim 404 mechanism — Phase 4's `@RestControllerAdvice` will formalize the error body shape (Spring Boot's default error attributes already return `{timestamp, status, error, path}`, confirmed via manual curl testing).
- **Build gotcha hit and fixed**: on this project's JDK 25 toolchain, Lombok's implicit classpath-based annotation-processor discovery silently stopped applying once enough cross-package source files were added (`cannot find symbol: method builder()/getX()` for entities, even though Phase 1's smaller file set compiled fine). Fix: pin Lombok explicitly via `<annotationProcessorPaths>` in the `maven-compiler-plugin` config in `pom.xml`. If this class of error resurfaces, check that config first before suspecting the entity/DTO code.

## Phase 3 — Booking Creation with Conflict Logic (the core of the project)

- `BookingService.createBooking(...)`: validate court/user exist and `startTime < endTime`, run an overlap-check repository query (`existing.start < new.end AND existing.end > new.start`, excluding cancelled bookings), throw a `BookingConflictException` → HTTP 409 on conflict, else persist as `CONFIRMED`.
- `cancelBooking(id)` — soft cancel (status → `CANCELLED`), not a hard delete.
- Think through edge cases explicitly: back-to-back bookings must be allowed (no overlap), cancelled bookings must not block new ones.
- Note for the README later: naive check-then-insert has a TOCTOU race under concurrent requests; real fixes are a DB-level exclusion/unique constraint or `SELECT ... FOR UPDATE`. Worth flagging as a known limitation/stretch item rather than silently ignoring — a good interview talking point either way.
- **Done when**: manual testing confirms overlapping bookings are rejected (409) and legitimate non-overlapping/adjacent bookings succeed.

## Phase 4 — Validation & Global Error Handling

- `@RestControllerAdvice` `GlobalExceptionHandler` mapping a small exception hierarchy (`ResourceNotFoundException` → 404, `BookingConflictException` → 409, `MethodArgumentNotValidException` → 400) to a consistent JSON error body (`timestamp`, `status`, `error`, `message`, `path`).
- **Done when**: every endpoint returns clean structured errors instead of stack traces/generic 500s.

## Phase 5 — Testing Pass

- **Service unit tests** (JUnit 5 + Mockito) — highest priority: `BookingServiceTest` covering overlap/adjacent/cancelled-booking/different-court scenarios.
- **`@DataJpaTest`** for the conflict-check repository query (catches JPQL bugs Mockito can't).
- **`@WebMvcTest` + MockMvc** for controllers (status codes, validation errors), service layer mocked.
- **A few `@SpringBootTest` + Testcontainers (Postgres)** integration tests for the booking happy path and the 409 conflict path — strong CV signal, keep to a handful of scenarios, not exhaustive coverage.
- **Done when**: `./mvnw test` runs a meaningful suite, especially around the conflict rule.

## Phase 6 — CV Polish

- `README.md`: setup instructions (docker-compose + `./mvnw spring-boot:run`), API examples (curl or `.http` file), architecture overview, link/screenshot of Swagger UI, a "design decisions & known limitations" section (mentions the TOCTOU race note from Phase 3 — turns a gap into a signal of awareness).
- Add `springdoc-openapi-starter-webmvc-ui` for Swagger UI at `/swagger-ui.html`.
- `LICENSE` (MIT is standard for portfolio repos).
- `.github/workflows/ci.yml` running `./mvnw verify` on push — cheap, strong signal.
- Clean commit history going forward (one logical change per phase/commit).
- **Done when**: a stranger could clone the repo, read the README, run `docker-compose up` + `./mvnw spring-boot:run`, and exercise the API within a few minutes.

## Phase 7 (Stretch, explicitly post-v1) — Auth & Deployment

- Spring Security + JWT: login/register, protect booking-mutation endpoints to the owning user, basic roles (`USER`, `VENUE_OWNER`/`ADMIN`). Will likely mean splitting `User` into profile + credentials, or adding a separate `Account` entity.
- Deploy a live demo (Render/Railway/Fly.io free tier + managed Postgres, e.g. Neon/Supabase) and link it in the README — single highest-impact CV item since it lets a reviewer poke at it with no setup.
- Optional further stretch: DB-level exclusion constraint or pessimistic locking for the booking race condition, pagination/filtering, a minimal frontend page hitting the API.

---

## Explicitly deferred from v1 (call out in README roadmap section, don't build now)

Security/auth, payments, email/notifications, reviews/ratings, caching, message queues.

## Verification approach

Each phase should end in something concretely runnable/checkable:
- Phase 0–1: `./mvnw clean verify`, app boots, `docker-compose up` gives a working DB, Flyway migration applies.
- Phase 2–4: manual exercise via curl/Postman/Swagger UI covering CRUD + booking conflict + error responses.
- Phase 5: `./mvnw test` green, with the conflict-rule tests specifically inspected for coverage of edge cases.
- Phase 6: fresh clone + README-only instructions successfully bring the app up end-to-end.
