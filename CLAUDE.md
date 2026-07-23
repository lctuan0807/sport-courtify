# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

`sport-courtify` — a sport court/venue booking system built with Spring Boot, Java 21. It is an early-stage learning + portfolio project; see `PLAN.md` for the full staged roadmap (domain model, phases, and design decisions) being built toward. Update `PLAN.md` as phases complete or scope changes — it is the source of truth for what's built vs. planned.

## Commands

- **Build/run**: use the system `mvn` (Homebrew install), not `./mvnw` — the Maven wrapper is broken (`.mvn/wrapper/` is missing its properties file and jar), so `./mvnw` fails immediately with `cannot read distributionUrl property`. Regenerate it with `mvn wrapper:wrapper` if wrapper support is needed.
- Build + run tests: `mvn clean verify`
- Run the app: `mvn spring-boot:run`
- Run a single test class: `mvn test -Dtest=ClassName`
- Run a single test method: `mvn test -Dtest=ClassName#methodName`

## Architecture

- Java 21, Spring Boot 4.1.0 (parent POM `spring-boot-starter-parent`), Maven build. Root package: `com.sportcourtify`.
- Configuration lives in `src/main/resources/application.yml` (YAML, not `.properties`).
- `PLAN.md` specifies a **package-by-feature** layout as domain code is added (e.g. `venue/`, `court/`, `user/`, `booking/`, each with its own entity/repository/service/controller/dto), plus a shared `common/` package for cross-cutting exception handling and config — follow this convention rather than a package-by-layer (`controller/`, `service/`, `repository/`) split when adding new domain code.
- Core business rule to be aware of once booking logic exists: a court cannot have two non-cancelled bookings with overlapping time ranges — this conflict check belongs in the service layer, not the controller.
