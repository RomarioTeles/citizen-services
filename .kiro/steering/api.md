# Citizen Services - API/REST

## Estrutura da API

### Versão

- **Versão atual**: v1
- **Path**: `/api/v1/`
- **Formato**: JSON

### Content-Type

- **Request**: `application/json`
- **Response**: `application/json`

## Padrão de Endpoints

### Resource Naming

- **Plural**: `services` (não `service`)
- **CamelCase**: Não use underscores

### HTTP Methods

| Method | Purpose | Idempotent | Safe |
|--------|---------|------------|------|
| GET | Read | Yes | Yes |
| POST | Create | No | No |
| PUT | Update (full) | Yes | No |
| PATCH | Update (partial) | No | No |
| DELETE | Delete | Yes | No |

### Status Codes

| Code | Description | Example |
|------|-------------|---------|
| 200 OK | Request success | GET /services/1 |
| 201 Created | Resource created | POST /services |
| 204 No Content | Delete success | DELETE /services/1 |
| 400 Bad Request | Validation error | POST /services (invalid data) |
| 401 Unauthorized | No authentication | GET /services/1 (no auth) |
| 403 Forbidden | Insufficient permissions | POST /services (USER role) |
| 404 Not Found | Resource not found | GET /services/99999 |
| 422 Unprocessable Entity | Validation failed | PUT /services (invalid JSON) |
| 500 Internal Server Error | Unexpected error | System error |

## Request/Response DTOs

### Padrão de Request

```java
public record CreateServiceRequest(
    @NotBlank
    @Size(max = 150)
    String name,
    
    @Size(max = 500)
    String description
) {}
```

### Padrão de Response

```java
public record ServiceResponse(
    Long id,
    String name,
    String description,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {
    public static ServiceResponse from(CitizenService service) {
        return new ServiceResponse(
            service.getId(),
            service.getName(),
            service.getDescription(),
            service.isActive(),
            service.getCreatedAt(),
            service.getUpdatedAt()
        );
    }
}
```

### Padrão de Error Response

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, null);
    }

    public static ApiErrorResponse ofValidation(int status, String error, String message, String path,
                                                 Map<String, String> fieldErrors) {
        return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, fieldErrors);
    }
}
```

## Pagination

### Padrão de Query Params

| Param | Default | Max | Description |
|-------|---------|-----|-------------|
| `page` | 0 | - | Page number (0-indexed) |
| `size` | 10 | 100 | Page size |
| `sort` | - | - | Sort criteria (format: `field,asc/desc`) |

### Response Pagination

```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5
}
```

## Validations

### Annotations

| Annotation | Description |
|------------|-------------|
| `@NotBlank` | String not null and not blank |
| `@Size(min=, max=)` | String/Collection size |
| `@Email` | Valid email format |
| `@Min/@Max` | Numeric range |

## API Design Rules

### 1. Resources are nouns, not verbs

**Bad:**
```
POST /api/v1/services/create
POST /api/v1/services/update/1
```

**Good:**
```
POST /api/v1/services (create)
PUT /api/v1/services/1 (update)
```

### 2. Use HTTP methods correctly

- GET: Read only (no side effects)
- POST: Create
- PUT: Update (replace entire resource)
- PATCH: Update (partial update)
- DELETE: Delete

### 3. Version API

- Include version in path: `/api/v1/`

### 4. Use HATEOAS when applicable

- Not currently implemented
- Consider for future evolution

### 5. Idempotency

- PUT and DELETE should be idempotent
- POST is not idempotent (use for create only)

### 6. Error Handling

- Return proper status codes
- Include error message
- Include field errors for validation

### 7. Security

- All endpoints (except health) require auth
- Use HTTPS in production

## Current Endpoints

| Endpoint | Method | Auth | Request Body | Response Body |
|----------|--------|------|--------------|---------------|
| `/api/v1/health` | GET | No | - | `{"status": "UP"}` |
| `/api/v1/services` | POST | Yes (ADMIN) | `CreateServiceRequest` | `ServiceResponse` |
| `/api/v1/services` | GET | Yes | - | `ServicePageResponse` |
| `/api/v1/services/{id}` | GET | Yes | - | `ServiceResponse` |
| `/api/v1/services/{id}` | PUT | Yes (ADMIN) | `UpdateServiceRequest` | `ServiceResponse` |
| `/api/v1/services/{id}/activate` | PATCH | Yes (ADMIN) | - | `ServiceResponse` |
| `/api/v1/services/{id}` | DELETE | Yes (ADMIN) | - | 204 No Content |

## Swagger/OpenAPI

### URL

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Configuration

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Citizen Services API")
            .description("API da plataforma de serviços digitais ao cidadão")
            .version("0.0.1-SNAPSHOT"))
        .components(new Components()
            .addSecuritySchemes("basicAuth", new SecurityScheme()
                .type(HTTP)
                .scheme("basic")))
        .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
}
```

## Best Practices Summary

1. **RESTful**: Use HTTP methods correctly
2. **Versioned**: Include version in path
3. **Consistent**: Follow naming and structure patterns
4. **Validated**: All inputs validated
5. **Secure**: Auth required for all non-health endpoints
6. **Documented**: OpenAPI spec available
7. **Paginated**: Lists support pagination
8. **Idempotent**: PUT/DELETE idempotent
9. **Error-aware**: Proper error responses
10. **Observable**: Correlation IDs in all requests