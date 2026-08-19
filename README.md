# Citizen Services Platform

Platform for digital citizen services, built as a modular monolith designed to evolve incrementally.

---

## Technologies

- Java 21
- Spring Boot 4.x / Spring Framework 7.x
- Spring MVC
- Spring Boot Actuator (Micrometer)
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

## Running with Docker

Build and start the application:

```bash
docker compose up --build
```

Run with dev profile:

```bash
SPRING_PROFILES_ACTIVE=dev docker compose up --build
```

The application will be available at `http://localhost:8080`.

To also start PostgreSQL (for future use):

```bash
docker compose --profile infra up --build
```

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | HTTP port the application listens on |
| `APP_NAME` | `citizen-services` | Application name |
| `SPRING_PROFILES_ACTIVE` | _(none)_ | Active Spring profiles (e.g. `dev`) |

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

## Project structure

```
src/main/java/io/github/romarioteles/citizenservices/
├── CitizenServicesApplication.java
└── controller/
    └── HealthController.java

src/main/resources/
├── application.yaml         # Common configuration for all environments
└── application-dev.yaml     # Development-specific configuration
```

Packages `config`, `service`, `repository`, `domain`, and `infrastructure` will be added incrementally as features are implemented.
