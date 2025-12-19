# Repository Guidelines
## Project Structure & Module Organization
The Spring Boot app lives in `src/main/java/com/beibeijava`. Controllers expose REST APIs, services host business logic, and `mapper` interfaces wrap MyBatis SQL mappings. Domain models sit under `entity`, shared responses/utilities under `common` and `util`, with security glue in `security` and `config`. Static docs and helper scripts for the trusted-module live under `tpm`. Configuration files stay in `src/main/resources`, while integration data written by the app lands in `uploads/`. Place unit and slice tests in `src/test/java/com/beibeijava`.

jdk 21
mysql 8
shell fish



## Build, Test, and Development Commands
- `./mvnw clean install` builds the app and runs the full verification lifecycle.
- `./mvnw spring-boot:run` starts the API on port 8080; Swagger UI is at `/swagger-ui.html`.
- `./mvnw test` executes the JUnit and Spring test suites; add `-Dspring.profiles.active=dev` if you rely on a profile-specific datasource.

## Coding Style & Naming Conventions
Code targets Java 21 with four-space indentation and UTF-8 source files. Keep class names in PascalCase, beans and DTO fields in camelCase, and suffix MyBatis interfaces with `Mapper`. Prefer Lombok annotations (`@RequiredArgsConstructor`, `@Data`) to reduce boilerplate. REST endpoints should return the shared `Result<T>` wrapper and group routes by aggregate root (`/api/auth`, `/api/order`, etc.). Reuse constants from `common` rather than scattering literals.

## Testing Guidelines
Use `spring-boot-starter-test` for integration coverage and `spring-security-test` when exercising protected endpoints. Name test classes `*Tests` and arrange them to mirror the package under test. Favor `@SpringBootTest` for workflow scenarios and `@WebMvcTest` or `@DataJpaTest` for focused slices. Ensure new service logic includes at least one assertion around both success and failure branches, and document any required seeded data in the test.

## Commit & Pull Request Guidelines
Follow the existing log by writing concise, action-oriented subjects (Chinese summaries are welcome). Reference the touched module up front when it aids context, e.g., `订单: 修复取消流程`. Each PR should describe the change, list manual or automated checks, and link to any tracked issue. Include screenshots or JSON snippets for API changes so reviewers can verify payloads quickly.

## Configuration & Security Tips
Keep sensitive credentials out of `application.properties`; rely on environment overrides or a local `application-local.properties` ignored by Git. Redis, MySQL, and JWT secrets must be configured before running locally. When adding new configuration keys, document defaults in the README or Swagger description to help other agents sync their setups.
