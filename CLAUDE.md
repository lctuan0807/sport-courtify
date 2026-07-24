# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

`sport-courtify` — a sport court/venue booking system built with Spring Boot, Java 21. It is an early-stage learning + portfolio project; see `PLAN.md` for the full staged roadmap (domain model, phases, and design decisions) being built toward. Update `PLAN.md` as phases complete or scope changes — it is the source of truth for what's built vs. planned.

## Commands

- Build + run tests: `./mvnw clean verify`
- Run the app: `./mvnw spring-boot:run`
- Run a single test class: `./mvnw test -Dtest=ClassName`
- Run a single test method: `./mvnw test -Dtest=ClassName#methodName`

## Architecture

- Java 21, Spring Boot 4.1.0 (parent POM `spring-boot-starter-parent`), Maven build. Root package: `com.sportcourtify`.
- Configuration lives in `src/main/resources/application.yml` (YAML, not `.properties`).
- `PLAN.md` specifies a **package-by-feature** layout as domain code is added (e.g. `venue/`, `court/`, `user/`, `booking/`, each with its own entity/repository/service/controller/dto), plus a shared `common/` package for cross-cutting exception handling and config — follow this convention rather than a package-by-layer (`controller/`, `service/`, `repository/`) split when adding new domain code.
- Every entity extends `com.sportcourtify.common.Auditable` (a `@MappedSuperclass`) to get `createdAt`/`updatedAt`, populated automatically via Spring Data JPA auditing (`@EnableJpaAuditing` in `common/config/JpaAuditingConfig`) — don't add manual `@PrePersist`/`@PreUpdate` timestamp handling to new entities, just extend `Auditable`.
- Local Postgres runs via `docker-compose.yml` on host port **5433** (not 5432 — reserved to avoid clashing with a locally-installed Postgres instance); `application.yml` datasource URL matches.
- Schema is managed by Flyway migrations under `src/main/resources/db/migration`; `spring.jpa.hibernate.ddl-auto=validate`, so entity changes require a matching migration, not just editing entity code. Note: Spring Boot 4.1's autoconfigure is split per-integration — `flyway-core` alone does not trigger migrations, `org.springframework.boot:spring-boot-flyway` must also be on the classpath.
- Core business rule to be aware of once booking logic exists: a court cannot have two non-cancelled bookings with overlapping time ranges — this conflict check belongs in the service layer, not the controller.
