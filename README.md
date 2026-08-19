# Citizen Services Platform

Platform for digital citizen services, built as a modular monolith designed to evolve incrementally.

---

## Technologies

- Java 21
- Spring Boot 4.x / Spring Framework 7.x
- Spring MVC
- Spring Boot Actuator (Micrometer)
- Spring Data JPA / Hibernate
- Flyway
- PostgreSQL
- Maven
- JUnit 5
- Docker

---

## Prerequisites

- Java 21+
- Maven 3.9+ (or use the included `./mvnw`)
- Docker and Docker Compose (for containerized execution)

---

## Running locally

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

Requires a running PostgreSQL instance. See [Starting PostgreSQL](#starting-postgresql).

## Running with dev profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Running via environment variable

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

---

## Running tests

```bash
./mvnw test
```

---

## Building

```bash
./mvnw clean package
```

The JAR will be generated at `target/citizen-services-0.0.1-SNAPSHOT.jar`.

---

## Starting PostgreSQL

Start only the PostgreSQL container:

```bash
docker compose --profile infra up -d postgres
```

Check status:

```bash
docker compose ps
```

Stop and remove containers (data is preserved in the volume):

```bash
docker compose --profile infra down
```

Stop and remove containers **and data**:

```bash
docker compose --profile infra down -v
```

### About the volume

PostgreSQL data is stored in the `postgres_data` Docker volume. This means data persists across container restarts. Use `-v` only when you want to reset the database completely.

---

## Running with Docker

Build and start the full application (requires `--profile infra` to include PostgreSQL):

```bash
docker compose --profile infra up --build
```

Run with dev profile:

```bash
SPRING_PROFILES_ACTIVE=dev docker compose --profile infra up --build
```

The application will be available at `http://localhost:8080`.

---

## localhost vs postgres (container networking)

When running outside Docker, the application connects to PostgreSQL via:

```
jdbc:postgresql://localhost:5432/citizenservices
```

When running inside Docker Compose, containers communicate through Docker's internal network. In this context, `localhost` refers to the container itself — not the PostgreSQL container. The correct hostname is the **service name** defined in `docker-compose.yml`:

```
jdbc:postgresql://postgres:5432/citizenservices
```

This is why `DB_URL` is overridden in the `app` service environment to use `postgres` as the hostname.

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | HTTP port the application listens on |
| `APP_NAME` | `citizen-services` | Application name |
| `SPRING_PROFILES_ACTIVE` | _(none)_ | Active Spring profiles (e.g. `dev`) |
| `DB_URL` | `jdbc:postgresql://localhost:5432/citizenservices` | JDBC connection URL |
| `DB_USERNAME` | `citizen` | Database username |
| `DB_PASSWORD` | `citizen` | Database password |
| `POSTGRES_DB` | `citizenservices` | PostgreSQL database name (Docker) |
| `POSTGRES_USER` | `citizen` | PostgreSQL username (Docker) |
| `POSTGRES_PASSWORD` | `citizen` | PostgreSQL password (Docker) |

---

## Health endpoints

### API health endpoint

```
GET /api/v1/health
```

Custom endpoint implemented in `HealthController`. Used in this phase to study Spring MVC.
Returns a simple JSON response produced by the application itself.

Expected response:

```json
{ "status": "UP" }
```

### Actuator health endpoint

```
GET /actuator/health
```

Operational endpoint provided by Spring Boot Actuator. Reports the health of the application
and its dependencies (database, disk space, etc.) as they are added in future increments.
This is the endpoint that should be used by infrastructure tools (load balancers, orchestrators).

Expected response:

```json
{ "status": "UP" }
```

With `dev` profile, response includes component details:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", ... },
    "diskSpace": { "status": "UP", ... },
    "ping": { "status": "UP" }
  }
}
```

---

## Profiles

| Profile | Description |
|---|---|
| _(none)_ | Default configuration. Safe for all environments. |
| `dev` | Development configuration. Enables DEBUG logging and detailed error/health responses. |

---

## Database migrations

Migrations are managed by Flyway and located at:

```
src/main/resources/db/migration/
```

Naming convention:

```
V{version}__{description}.sql
```

Flyway runs automatically on application startup and records applied migrations in the `flyway_schema_history` table.

---

## Project structure

```
src/main/java/io/github/romarioteles/citizenservices/
├── CitizenServicesApplication.java
└── controller/
    └── HealthController.java

src/main/resources/
├── application.yaml            # Common configuration for all environments
├── application-dev.yaml        # Development-specific configuration
└── db/
    └── migration/
        └── V1__baseline.sql    # Initial Flyway migration
```

Packages `config`, `service`, `repository`, `domain`, and `infrastructure` will be added incrementally as features are implemented.
