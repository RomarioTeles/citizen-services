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
- Prometheus
- Grafana

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

Build and start the full application (requires `--profile infra` to include PostgreSQL, Prometheus and Grafana):

```bash
docker compose --profile infra up --build
```

Run with dev profile:

```bash
SPRING_PROFILES_ACTIVE=dev docker compose --profile infra up --build
```

The application will be available at `http://localhost:8080`.

---

## localhost vs container networking

When running outside Docker, the application connects to PostgreSQL via:

```
jdbc:postgresql://localhost:5432/citizenservices
```

When running inside Docker Compose, containers communicate through Docker's internal network. In this context, `localhost` refers to the container itself — not another container. The correct hostname is the **service name** defined in `docker-compose.yml`.

This is why:
- `DB_URL` uses `postgres` as the hostname
- Prometheus scrapes the application via `app:8080`, not `localhost:8080`

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

## Security

The application uses Spring Security with HTTP Basic authentication.

### Protected endpoints

| Endpoint | Access |
|---|---|
| `GET /actuator/health` | Public |
| `GET /actuator/health/liveness` | Public |
| `GET /actuator/health/readiness` | Public |
| Swagger UI / OpenAPI docs | Public |
| `POST /api/v1/services` | Authenticated |
| `GET /api/v1/services/**` | Authenticated |
| `GET /actuator/info` | Authenticated |
| `GET /actuator/metrics` | Authenticated |
| `GET /actuator/prometheus` | Authenticated |

### Development credentials

Configured via environment variables:

```
SECURITY_USERNAME=dev
SECURITY_PASSWORD=dev-password
```

Defaults are for local development only. Do not use in production.

### Using the Swagger Authorize button

1. Open http://localhost:8080/swagger-ui.html
2. Click the **Authorize** button (top right)
3. Enter `Username` and `Password` (default: `dev` / `dev-password`)
4. Click **Authorize**, then **Close**
5. All subsequent requests from Swagger UI will include `Authorization: Basic ...`
6. To test unauthenticated behavior, click **Authorize** → **Logout**

### 401 vs 403

- **401 Unauthorized** — request has no credentials or credentials are invalid
- **403 Forbidden** — request is authenticated but lacks permission for the resource

### API stateless

The application is configured as stateless (`SessionCreationPolicy.STATELESS`). No HTTP session is created or used. Each request must carry its own credentials.

> JWT and OAuth2 will be implemented in a future increment.

---

## Observability

### URLs

| Service | URL |
|---|---|
| Application | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Actuator | http://localhost:8080/actuator |
| Prometheus scrape | http://localhost:8080/actuator/prometheus |
| Prometheus UI | http://localhost:9090 |
| Grafana | http://localhost:3000 |

---

### Architecture

```
            Citizen Services
                   │
                   ▼
              Actuator
                   │
     ┌─────────────┼──────────────┐
     ▼             ▼              ▼
  Health        Metrics         Info
     │             │
┌────┴────┐        │
▼         ▼        ▼
Liveness Readiness Micrometer
              │
              ▼
     Prometheus Registry
              │
              ▼ /actuator/prometheus
         Prometheus
              │
              ▼
           Grafana
```

Metrics are produced by **Micrometer** and exported in the Prometheus scrape format via `/actuator/prometheus`.
**Prometheus** collects (scrapes) those metrics every 15 seconds.
**Grafana** connects to Prometheus as a data source and visualizes the metrics in dashboards.

---

### API health endpoint

```
GET /api/v1/health
```

Custom endpoint implemented in `HealthController`. Returns a simple JSON response produced by the application itself.

Expected response:

```json
{ "status": "UP" }
```

### Actuator health endpoint

```
GET /actuator/health
```

Reports the overall health of the application. Used by infrastructure tools (load balancers, orchestrators).

Expected response:

```json
{ "status": "UP" }
```

With `dev` profile, response includes component details:

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### Liveness

```
GET /actuator/health/liveness
```

**"Is the application alive?"**

Indicates whether the JVM process is running and the application context is healthy.
Liveness does **not** depend on external services such as PostgreSQL — a database outage does not
mean the application process itself is broken. If liveness fails, the orchestrator should restart the container.

Expected response:

```json
{ "status": "UP" }
```

### Readiness

```
GET /actuator/health/readiness
```

**"Is the application ready to receive traffic?"**

Indicates whether the application is ready to handle requests. Includes the database health check (`db`).
If PostgreSQL is unavailable, readiness returns `DOWN` and the orchestrator should stop routing traffic
to this instance — without restarting it, since the process itself is still alive.

Expected response when database is available:

```json
{ "status": "UP" }
```

### Metrics

```
GET /actuator/metrics
```

Lists all available metric names produced by Micrometer.

```
GET /actuator/metrics/{metric.name}
```

Returns details for a specific metric. Examples:

| Endpoint | Description |
|---|---|
| `/actuator/metrics/http.server.requests` | HTTP request counts, durations, status codes |
| `/actuator/metrics/jvm.memory.used` | JVM heap and non-heap memory usage |
| `/actuator/metrics/jvm.threads.live` | Number of live JVM threads |
| `/actuator/metrics/system.cpu.usage` | System CPU usage |
| `/actuator/metrics/hikaricp.connections.active` | Active HikariCP datasource connections |

### Prometheus

```
GET /actuator/prometheus
```

Exposes all Micrometer metrics in the Prometheus text format. Scraped automatically by the Prometheus container every 15 seconds.

Business metrics exported:

| Prometheus metric | Description |
|---|---|
| `citizen_services_registrations_total` | Total services successfully created |
| `citizen_services_consulted_total` | Total successful lookups by ID |
| `citizen_services_not_found_total` | Total lookups by ID that returned 404 |

### Grafana

Grafana is available at `http://localhost:3000` (credentials: `admin` / `admin`).

The **Citizen Services** dashboard is provisioned automatically and includes:

| Panel | Metric |
|---|---|
| Serviços Cadastrados | `citizen_services_registrations_total` |
| Consultas por ID | `citizen_services_consulted_total` |
| Consultas Não Encontradas | `citizen_services_not_found_total` |
| Memória JVM heap | `jvm_memory_used_bytes` |
| Requisições HTTP | `http_server_requests_seconds_count` |
| Conexões HikariCP | `hikaricp_connections_active/idle` |
| Threads JVM | `jvm_threads_live/daemon` |

The Prometheus data source is also provisioned automatically — no manual configuration required.

### Info

```
GET /actuator/info
```

Returns application metadata configured via `info.*` properties.

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
├── api/
│   ├── dto/
│   │   └── ApiErrorResponse.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── config/
│   └── OpenApiConfig.java
├── controller/
│   └── HealthController.java
├── infrastructure/
│   └── observability/
│       ├── CorrelationIdFilter.java
│       └── HttpRequestLoggingFilter.java
└── service/
    ├── api/
    │   ├── dto/
    │   │   ├── CreateServiceRequest.java
    │   │   ├── ServicePageResponse.java
    │   │   ├── ServiceResponse.java
    │   │   └── UpdateServiceRequest.java
    │   └── ServiceController.java
    ├── application/
    │   ├── exception/
    │   │   └── ServiceNotFoundException.java
    │   ├── metrics/
    │   │   └── ServiceMetrics.java
    │   └── ServiceApplicationService.java
    ├── domain/
    │   └── CitizenService.java
    └── repository/
        └── ServiceRepository.java

src/main/resources/
├── application.yaml
├── application-dev.yaml
└── db/
    └── migration/
        ├── V1__baseline.sql
        └── V2__create_services_table.sql

prometheus/
└── prometheus.yml

grafana/
├── provisioning/
│   ├── datasources/
│   │   └── prometheus.yml
│   └── dashboards/
│       └── provider.yml
└── dashboards/
    └── citizen-services.json
```

---

## Domain

- `CitizenService` — represents a service offered to citizens (e.g. document reissuance, benefit inquiry).
