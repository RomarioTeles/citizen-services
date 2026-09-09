# Citizen Services - Segurança

## Autenticação

### Mecanismo

- **HTTP Basic Authentication**
- Headers: `Authorization: Basic <base64(username:password)>`
- Stateful sessions NÃO são usadas (`SessionCreationPolicy.STATELESS`)

### Credenciais

#### Credenciais Padrão (Development)

| Role | Username | Password |
|------|----------|----------|
| USER | `dev` | `dev-password` |
| ADMIN | `admin` | `admin-password` |

#### Configuração

```yaml
security:
  username: ${SECURITY_USERNAME:dev}
  password: ${SECURITY_PASSWORD:dev-password}
  admin:
    username: ${SECURITY_ADMIN_USERNAME:admin}
    password: ${SECURITY_ADMIN_PASSWORD:admin-password}
```

### Configuração Spring Security

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    // HTTP Basic authentication
    // Stateless session policy
    // CSRF disabled
}
```

## Autorização

### Roles

| Role | Descrição |
|------|-----------|
| `USER` | Acesso leitura e algumas operações |
| `ADMIN` | Acesso total (CRUD completo) |

### Anotações de Autorização

```java
@PreAuthorize("hasRole('ADMIN')")
@Transactional
public ServiceResponse create(CreateServiceRequest request) {
    // Apenas ADMIN pode criar
}

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@Transactional(readOnly = true)
public ServiceResponse findById(Long id) {
    // USER e ADMIN podem ler
}
```

### Regras de Autorização por Endpoint

| Endpoint | Method | Roles Permitidas |
|----------|--------|------------------|
| `/api/v1/health` | GET | Público |
| `/api/v1/services` | POST | ADMIN |
| `/api/v1/services` | GET | USER, ADMIN |
| `/api/v1/services/{id}` | GET | USER, ADMIN |
| `/api/v1/services/{id}` | PUT | ADMIN |
| `/api/v1/services/{id}/activate` | PATCH | ADMIN |
| `/api/v1/services/{id}` | DELETE | ADMIN |
| `/actuator/health` | GET | Público |
| `/swagger-ui.html` | GET | Público |
| `/v3/api-docs/**` | GET | Público |

### Public Routes

Estes endpoints NÃO requerem autenticação:

```java
private static final String[] PUBLIC_ROUTES = {
    "/actuator/health",
    "/actuator/health/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**"
};
```

## Boas Práticas de Segurança

### 1. Sempre validar entrada

- Use `@Valid` em request DTOs
- Use annotations de validação (`@NotBlank`, `@Size`, etc.)
- Trate exceções de validação no `GlobalExceptionHandler`

### 2. Não expor dados sensíveis

- NÃO inclua senhas em responses
- NÃO logue senhas ou tokens
- Use `@JsonInclude(JsonInclude.Include.NON_NULL)` para omitir campos null

### 3. Use HTTPS em produção

- Configure SSL/TLS no load balancer ou reverse proxy
- Não use HTTP em ambiente de produção

### 4. Rate limiting

- Considere implementar rate limiting para APIs públicas
- Use Spring Cloud Gateway ou Nginx para isso

## OpenAPI/Swagger

### Autenticação na Documentação

A documentação OpenAPI está configurada com esquema de autenticação HTTP Basic:

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Citizen Services API")
            .version("0.0.1-SNAPSHOT"))
        .components(new Components()
            .addSecuritySchemes("basicAuth", new SecurityScheme()
                .type(HTTP)
                .scheme("basic")))
        .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
}
```

### Como usar o Swagger UI

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Clique no botão **Authorize**
3. Digite username e password
4. Clique **Authorize**
5. Clique **Close**

## Status Atual

### O que está implementado

- HTTP Basic Authentication
- Authorization baseada em roles
- Spring Security configuration
- OpenAPI integration com security schemes
- Secure by default (todos endpoints requerem auth exceto os explícitos)

### O que não está implementado (sugestões futuras)

- JWT authentication (stateless, mais scalability)
- OAuth2 / OpenID Connect (paraSSO e social login)
- Refresh tokens
- Account lockout (proteção contra brute force)
- Password complexity requirements
- 2FA/MFA
- Audit logging de operações sensíveis