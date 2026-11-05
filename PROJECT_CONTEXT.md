# Movies / Plex Movie Library Project Context

> This document is an AI-oriented description of the repository. It is intended to be given to another AI assistant as project context before asking it to explain, modify, debug, or review the code.
>
> **Important:** Configuration files may contain environment-specific credentials or test values. Do not copy secrets into issues, prompts, commits, or documentation. Treat the runtime configuration and the Liquibase changelog as the authoritative sources when they disagree with explanatory documentation.

## 1. Project identity

| Property | Value |
|---|---|
| Maven coordinates | `com.example:movies:0.0.1-SNAPSHOT` |
| Application type | Spring Boot REST backend |
| Domain | Movie-library management |
| Language | Java |
| Build tool | Maven |
| Database | PostgreSQL |
| Source migration database | SQLite Plex database files |
| External service | OMDb API |
| Authentication | OAuth2 Resource Server with Keycloak JWTs |
| API description | OpenAPI 3.1 |
| Frontend | None; this repository exposes HTTP/JSON APIs only |

The application is a single Maven module. Its main source package is:

```text
src/main/java/com/example/movies
```

The main class is `com.example.movies.PlexApplication`.

## 2. What the application does

The application stores and manages movies and related media metadata. Authenticated users can read movies, while administrators can create, update, delete, enrich, and migrate movie data.

The application has three major responsibilities:

1. **Movie REST API** — CRUD, filtering, sorting, pagination, and movie enrichment.
2. **PostgreSQL persistence** — JPA/Hibernate entities and repositories, with Liquibase-managed schema migrations.
3. **Plex-to-PostgreSQL migration** — Spring Batch reads bundled or configured SQLite Plex databases, converts records to domain objects, and writes them to PostgreSQL.

## 3. Technology stack

The main dependencies are declared in `pom.xml`.

- Spring Boot 4.1.1
- Spring Web / REST
- Spring Data JPA and Hibernate
- PostgreSQL JDBC driver
- SQLite JDBC driver
- Liquibase
- Spring Batch
- Spring Security OAuth2 Resource Server
- Spring Actuator
- Jakarta Bean Validation
- Jackson
- MapStruct 1.6.3
- Lombok
- Ehcache/JCache/Hibernate JCache dependencies
- OpenAPI Generator Maven plugin 7.25.0
- JUnit Jupiter and Spring Boot Test

### Java version

The Maven properties mention Java 25, while the compiler plugin uses release 21. The effective compilation target appears to be Java 21. Keep this discrepancy in mind when changing language features or diagnosing build failures.

## 4. Repository layout

```text
.
├── pom.xml
├── api/
│   └── movies-api.yaml                 # OpenAPI 3.1 contract
├── src/
│   ├── main/
│   │   ├── java/com/example/movies/
│   │   │   ├── PlexApplication.java   # Spring Boot entry point
│   │   │   ├── config/                # Security, migration, Jackson, application beans
│   │   │   ├── constants/             # Shared constants and messages
│   │   │   ├── controller/            # HTTP REST controllers
│   │   │   ├── converter/             # JWT role conversion
│   │   │   ├── exceptions/             # Domain exceptions and error handlers
│   │   │   ├── external_api/           # OMDb client and API adapter
│   │   │   ├── listener/               # Spring Batch listeners
│   │   │   ├── mapper/                 # MapStruct entity/DTO mappings
│   │   │   ├── model/                  # JPA entities, enums, API models
│   │   │   ├── page_filters/           # In-memory filtering, sorting, pagination
│   │   │   ├── processor/              # SQLite-to-domain conversion
│   │   │   ├── repository/             # Spring Data repositories
│   │   │   └── service/                # Service interfaces and implementations
│   │   └── resources/
│   │       ├── application.yaml        # Default runtime configuration
│   │       ├── application-local.yaml  # Local profile configuration
│   │       ├── db/changelog/           # Liquibase schema changelogs
│   │       ├── *.db                    # SQLite migration inputs
│   │       └── *.md                    # Database and Liquibase notes
│   └── test/
│       ├── java/                       # Unit tests
│       └── resources/                  # Test configuration
└── target/                             # Generated/build output; not primary source
```

`target/` contains compiled classes, generated OpenAPI code, generated MapStruct code, copied resources, and Maven status files. It should generally be treated as build output rather than hand-edited source.

## 5. Architectural style and request flow

The project follows a layered architecture:

```text
HTTP request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL / JPA
```

DTO and entity conversion is performed through MapStruct mappers. Lombok commonly provides constructor injection through `@RequiredArgsConstructor`.

### Normal movie read

1. `MovieController` receives a request under `/api/movies`.
2. Spring Security checks the JWT and method-level role requirements.
3. `MovieServiceImpl` performs the use-case logic.
4. `MovieRepository` loads entities.
5. `MovieMapper` maps entities to internal DTOs and API response models.
6. Jackson serializes the response as JSON.

Important files:

- `src/main/java/com/example/movies/controller/MovieController.java`
- `src/main/java/com/example/movies/service/impl/MovieServiceImpl.java`
- `src/main/java/com/example/movies/repository/MovieRepository.java`
- `src/main/java/com/example/movies/mapper/MovieMapper.java`

## 6. REST API

The main controller is `MovieController`, with base path `/api/movies`.

| Method | Path | Role | Purpose |
|---|---|---|---|
| `GET` | `/api/movies/{id}` | `USER` | Retrieve one movie |
| `GET` | `/api/movies` | `USER` | List, filter, sort, and paginate movies |
| `POST` | `/api/movies` | `ADMIN` | Create a movie |
| `PUT` | `/api/movies/{id}` | `ADMIN` | Fully update a movie |
| `PATCH` | `/api/movies/{id}` | `ADMIN` | Partially update a movie |
| `DELETE` | `/api/movies/{id}` | `ADMIN` | Delete a movie |
| `PUT` | `/api/movies/{id}/enrich` | `ADMIN` | Enrich a movie from OMDb |
| `GET` | `/api/movies/migrate/movies` | `ADMIN` | Start the SQLite-to-PostgreSQL migration |

The list endpoint uses these parameters:

```text
page    default 0
size    default 10, minimum 5
filter  optional
sort    default "id,ASC"
```

The OpenAPI contract is `api/movies-api.yaml`. Maven generates API interfaces and models under `target/generated-sources/openapi`; the hand-written controller is still the runtime implementation to inspect when behavior matters.

## 7. Movie list behavior

`MovieServiceImpl.getAllMoviesForPage` currently loads all movies, maps them to DTOs, and delegates filtering, sorting, and pagination to `MoviePageProcessor`.

Current characteristics:

- Filtering checks title containment.
- Filtering and pagination occur in memory rather than in the database.
- The complete movie dataset is loaded before paging.
- The non-empty `PageImpl` total count is based on page content instead of consistently representing the total filtered dataset.
- Invalid sort syntax may raise `IllegalArgumentException`.

These are important considerations for performance, correctness, and future refactoring.

## 8. Create, update, delete, and enrichment behavior

### Create

`MovieServiceImpl.createMovie`:

1. Converts the request to an internal DTO.
2. Checks for duplicates by loading and comparing existing movies.
3. Resolves or creates related genres, awards, languages, and people.
4. Converts and saves the `MovieEntity`.

### Update

The service supports full replacement with `PUT` and partial modification with `PATCH`. The mapper is used to apply request data and to map the resulting entity back to the API response.

### Delete

The service loads the movie and deletes it through the repository. Missing records are converted into the project’s not-found error response.

### OMDb enrichment

The enrichment flow is:

```text
MovieController
    → MovieServiceImpl
    → OmdbClient
    → OMDb API
    → MovieMapper.patch
    → persisted MovieEntity
```

The OMDb request uses an IMDb ID or title. OMDb data can update ratings, duration, genres, awards, people, languages, and other metadata. External failures are translated into project exceptions and `ProblemDetail` responses.

Relevant files:

- `src/main/java/com/example/movies/external_api/OmdbApi.java`
- `src/main/java/com/example/movies/external_api/OmdbClient.java`
- `src/main/java/com/example/movies/mapper/MovieMapper.java`

## 9. Domain model and persistence

`MediaItemEntity` is the base JPA entity. It uses joined inheritance with:

- Base table: `media_item`
- Discriminator column: `item_type`

`MovieEntity` extends `MediaItemEntity`:

- Table: `movie`
- Discriminator value: `MOVIE`
- Additional fields include duration and year

The broader model also contains:

- `SeriesEntity`
- `SeasonEntity`
- `EpisodeEntity`
- `AwardEntity`
- `GenreEntity`
- `LanguageEntity`
- `PersonEntity`

`MediaItemEntity` has many-to-many relationships with awards, genres, languages, and people.

Repositories are in `src/main/java/com/example/movies/repository`. `MovieRepository` provides standard CRUD plus title/IMDb-ID query methods and an `findAllImdbs()` JPQL query used by migration deduplication.

## 10. Database schema management

PostgreSQL is the runtime database. Hibernate schema generation is disabled, so Liquibase owns schema creation and evolution.

Primary files:

- `src/main/resources/db/changelog/db.changelog-master.xml`
- `src/main/resources/db/changelog/05-01-changelog.xml`

The changelog creates tables for media items, movies, series, seasons, episodes, awards, genres, languages, people, relationship/join tables, foreign keys, and unique constraints.

Additional database notes are in:

- `src/main/resources/dbConcepts.md`
- `src/main/resources/dbStructure.md`
- `src/main/resources/dbConnectionExplained.md`
- `src/main/resources/liquibase.md`
- `src/main/resources/usefulQueries.md`

If these notes differ from the actual runtime behavior, prioritize Liquibase changelogs and JPA annotations.

## 11. SQLite migration pipeline

The migration uses Spring Batch to read Plex-oriented SQLite data and write movies to PostgreSQL.

```text
SQLite database files
    ↓
JdbcCursorItemReader
    ↓
SqliteMovieProcessor
    ↓
MovieService.createMovie
    ↓
PostgreSQL
```

Main files:

- `src/main/java/com/example/movies/config/SqliteMigrationConfig.java`
- `src/main/java/com/example/movies/processor/SqliteMovieProcessor.java`
- `src/main/java/com/example/movies/listener/MovieListener.java`
- `src/main/java/com/example/movies/service/impl/MigrationServiceImpl.java`
- `src/main/java/com/example/movies/constants/StringBundle.java`

The batch job reads in chunks of 100. The listener preloads existing IMDb IDs and the processor avoids duplicates during migration.

### Portability limitation

The configured migration paths are hard-coded to a macOS filesystem path belonging to another development environment. The migration endpoint will not work in the current Windows checkout unless those paths are changed to portable configuration or valid local paths.

## 12. Security

`SecurityConfig` configures an OAuth2 JWT resource server:

- CSRF is disabled.
- `/actuator/health` is public.
- Other requests require authentication.
- Method-level security is enabled.
- Controller methods use `USER` and `ADMIN` roles.

`RolesConverter` reads `realm_access.roles`, uppercases each role, and prefixes it with `ROLE_`. For example, token role `admin` becomes `ROLE_ADMIN`.

The JWT issuer is configured in `src/main/resources/application.yaml`. Do not hard-code or disclose real tokens, credentials, or API keys in project context.

## 13. Error handling

`GlobalExceptionHandler` returns Spring `ProblemDetail` responses for:

- Validation and parsing errors (`400`)
- Authentication failures (`401`)
- Authorization failures (`403`)
- Missing entities (`404`)
- Duplicate entities (`409`)
- External timeout/access failures (`502`)
- External API errors
- Unhandled server errors (`500`)

The security filter chain separately handles missing or invalid authentication and produces an unauthorized `ProblemDetail`.

## 14. Runtime configuration

The default configuration is in `src/main/resources/application.yaml`.

It includes:

- Application name `plex`
- PostgreSQL datasource settings
- Keycloak issuer
- Liquibase changelog
- JPA/Hibernate settings
- Disabled automatic Spring Batch job startup
- Actuator health exposure
- OMDb base URL, API key placeholder, and timeouts
- HTTP port `8080`

The OMDb API key is intended to come from the `OMDB_API_KEY` environment variable. The test profile contains a literal test value; treat it as non-production and avoid reproducing it in prompts or documentation.

`application-local.yaml` currently contains little or no local override configuration. `src/test/resources/application-test.yml` contains test-specific application, security, OMDb, and actuator values but does not define a test datasource.

## 15. Testing

The primary test is:

```text
src/test/java/com/example/movies/service/MovieServiceImplTest.java
```

It is a Mockito/JUnit Jupiter unit test covering:

- Movie lookup success and failure
- Creation and duplicate creation
- Delete
- Full update
- Partial update
- Pagination delegation
- Invalid sort handling
- OMDb enrichment

Test helper:

```text
src/test/java/com/example/movies/utils/Helper.java
```

No controller integration tests, security integration tests, migration tests, or database integration tests were identified.

## 16. Build and run commands

Run tests:

```bash
mvn test
```

Clean and test:

```bash
mvn clean test
```

Package:

```bash
mvn package
```

Run from Maven:

```bash
mvn spring-boot:run
```

Run the packaged application:

```bash
java -jar target/movies-0.0.1-SNAPSHOT.jar
```

The OpenAPI generator runs during Maven’s `generate` phase. A normal Maven build may therefore regenerate files under `target/generated-sources/openapi`.

## 17. Important project characteristics and risks

1. The repository is API-only; there is no frontend.
2. The list endpoint performs in-memory filtering and pagination, which may not scale.
3. The list endpoint’s page total may be semantically incorrect for non-empty pages.
4. SQLite migration paths are not portable and currently point to a macOS location.
5. Java configuration declares version 25 while compilation targets version 21.
6. Database credentials are present directly in default configuration and should be externalized for shared or production environments.
7. The OMDb key is environment-based by default, but test configuration includes a literal test value.
8. Database explanatory markdown and the Liquibase schema may not be perfectly aligned.
9. No explicit `@Transactional` annotations were identified.
10. Cache-related dependencies exist, but no active cache annotations were identified.
11. Migration is admin-protected but exposed through a `GET` endpoint.
12. The OpenAPI specification and controller are separate artifacts; generated code is not necessarily the complete source of runtime behavior.

## 18. How an AI assistant should work with this repository

When modifying this project:

1. Inspect the relevant controller, service, mapper, repository, entity, configuration, and test files before editing.
2. Preserve the controller → service → repository layering.
3. Reuse existing DTOs, mappers, exceptions, and response conventions.
4. Treat Liquibase changelogs as authoritative for schema changes.
5. Update the OpenAPI contract when an HTTP endpoint or schema changes.
6. Add or update unit tests for service behavior and add integration coverage when changing HTTP, security, database, or migration behavior.
7. Do not commit credentials, API keys, tokens, local filesystem paths, or generated build output.
8. Validate with the smallest relevant Maven command, normally `mvn test`, and use `mvn clean test` or `mvn package` when generated sources or build configuration are involved.

## 19. Reusable AI project-description template

Use the following template when asking another AI to understand this repository:

```text
You are working on the following project:

Project name:
<name>

Purpose:
<what the system does and who uses it>

Application type:
<backend, frontend, CLI, library, monolith, service, etc.>

Technology stack:
<language, framework, build tool, database, external services>

Repository structure:
<important directories and what each one contains>

Entry points:
<main classes, commands, routes, or startup scripts>

Architecture:
<layers and request/data flow>

Main domain objects:
<entities, DTOs, relationships, and important business concepts>

API:
<base paths, endpoints, authentication, request/response conventions>

Persistence:
<database, repositories, migrations, schema ownership>

External integrations:
<services, clients, authentication, timeouts, failure behavior>

Configuration:
<profiles, environment variables, ports, important runtime assumptions>

Testing:
<test locations, test style, coverage gaps, commands>

Build and run:
<exact commands>

Known limitations and risks:
<performance, portability, security, correctness, or design concerns>

Instructions for the AI:
<files to inspect first, conventions to preserve, and validation expected>

Task:
<the requested change or question>
```

## 20. Short context prompt

For a shorter AI prompt, provide this file together with:

```text
Read PROJECT_CONTEXT.md first. Use it as repository context, but verify details against the current source before making claims or changes. Preserve existing architecture and conventions, avoid secrets and generated files, update directly related documentation/tests, and validate the result with the appropriate Maven command.
```
