# Citizen Services - Arquitetura

## Visão Geral

O projeto Citizen Services segue uma arquitetura **Modular Monolith** com separação por camadas (Layered Architecture). Esta abordagem permite evolução incremental e facilita a manutenção do código.

## Estrutura de Camadas

```
src/main/java/io/github/romarioteles/citizenservices/
├── api/               # Camada de apresentação (DTOs, exceptions)
├── config/            # Configurações do Spring
├── controller/        # Controllers REST (entry points)
├── service/           # Domínio da aplicação
│   ├── api/          # Controllers, DTOs da API
│   ├── application/  # Casos de uso, lógica de negócio
│   ├── domain/       # Entidades, value objects, regras
│   └── repository/   # Interfaces de persistência
└── infrastructure/   # Implementações técnicas
    ├── observability/ # Logs, métricas, correlation IDs
    └── security/     # Autenticação e autorização
```

### Responsabilidades por Camada

| Camada | Responsabilidade | Elementos |
|--------|-----------------|----------|
| **api** | Formatação de dados (request/response) | DTOs, ApiErrorResponse |
| **controller** | Receber requisições HTTP e delegar | Controllers, @RestController |
| **application** | Coordenação de casos de uso | ApplicationService, UseCases |
| **domain** | Lógica de negócio e regras | Entities, Value Objects, Domain Services |
| **repository** | Abstração de persistência | Repository interfaces |
| **infrastructure** | Implementações técnicas | JPA entities, filters, security config |

## Padrões Arquiteturais

### Modular Monolith

- **Módulos independentes**: Cada feature tem sua estrutura organizada
- **Dependências claras**: Módulos dependem apenas do necessário
- **Evitando acoplamento**: Interfaces em vez de implementações concretas

### Layered Architecture

- **Separação clara**: Cada camada tem responsabilidade única
- **Dependência apontando para dentro**: Camadas externas dependem de internals
- **Facilita testes**: Cada camada pode ser testada isoladamente

## Padrões de Implementação

### Repository Pattern

```java
// Interface (no domain/application)
public interface ServiceRepository extends JpaRepository<CitizenService, Long> {
    Page<CitizenService> findByNameContaining(String name, Pageable pageable);
}

// Implementação (Spring Data JPA)
// Não é necessário implementar manualmente
```

### DTO Pattern

- **Request DTOs**: Validados com Bean Validation annotations
- **Response DTOs**: Formatados para a API (sem expor entidades internas)
- **Isolamento**: Mudanças internas não afetam a API pública

### Dependency Injection

- **Constructor injection**: Única forma permitida
- **Interface-based**: Programar para interfaces, não implementações
- **Sem @Autowired fields**: Evita acoplamento oculto

## Conexões entre Camadas

```
┌─────────────────┐
│   Controller    │ (HTTP layer)
└────────┬────────┘
         │
         │ 1. Delega para Application Service
         ▼
┌─────────────────┐
│ Application     │ (Business coordination)
│   Service       │
└────────┬────────┘
         │
         │ 2. Usa Repository para dados
         ▼
┌─────────────────┐
│   Repository    │ (Data access abstraction)
└────────┬────────┘
         │
         │ 3. JPA/Hibernate consulta DB
         ▼
┌─────────────────┐
│   Database      │ (PostgreSQL)
└─────────────────┘
```

## Padrões de Transação

- **Camada de aplicação gerencia transações**: `@Transactional` em métodos da ApplicationService
- **Leitura vs Escrita**: `@Transactional(readOnly = true)` para consultas
- **Não expor transações**: Controllers NUNCA usam `@Transactional`

**Detalhes**: Consulte [persistence.md](persistence.md) para padrões de transação e [api.md](api.md) para validação.

## Escalabilidade

### Atual (Modular Monolith)

- **Single instance**: Uma instância da aplicação
- **Database connection pooling**: HikariCP configurado
- **Caching futuro**: Redis planejado para futuro incremento

### Planejado

- **Redis cache**: Para consultas frequentes
- **Rate limiting**: Nginx ou Spring Cloud Gateway
- **Async processing**: Spring Batch ou message queues para operações longas

## Padrões de Teste

- **Unit**: Testes isolados de classes (sem Spring context)
- **Integration**: Testes com Spring context, mas sem HTTP
- **E2E**: Testes completos via HTTP (mockMvc ou TestRestTemplate)

## Referências Detalhadas

Esta seção define os padrões que são referenciados em outras partes da arquitetura:

### API/REST

- **Versionamento**: `/api/v1/` no path
- **HTTP methods**: GET, POST, PUT, PATCH, DELETE
- **Status codes**: 200, 201, 204, 400, 401, 403, 404, 500
- **Pagination**: Pageable com page, size, sort
- **Content-Type**: application/json
- **Validação**: Bean Validation com `@Valid`, `@NotBlank`, `@Size`

**Detalhes**: Consulte [api.md](api.md) para padrões completos de API.

### Segurança

- **HTTP Basic Authentication**: Headers `Authorization: Basic <base64>`
- **Stateless**: Nenhuma sessão é criada (`SessionCreationPolicy.STATELESS`)
- **Role-based Authorization**: `@PreAuthorize("hasRole('ADMIN')")`
- **CSRF Disabled**: Para APIs REST stateless

**Detalhes**: Consulte [security.md](security.md) para padrões completos de segurança.

### Observabilidade

- **Logs ECS JSON**: Formato estruturado para ELK stack
- **Correlation ID**: Header `X-Correlation-Id` propagado automaticamente
- **Metrics Micrometer**: Counter, Timer, Gauge com tags (method, status, uri)
- **Actuator**: Health checks e info endpoints

**Detalhes**: Consulte [observability.md](observability.md) para padrões completos.

### Persistência

- **PostgreSQL 16**: Banco relacional
- **Flyway migrations**: Versionamento de schema (V{version}__{description}.sql)
- **JPA/Hibernate**: ORM com lazy loading configurado
- **ddl-auto: validate**: NÃO use create/update em produção
- **Transações**: Gerenciadas na camada de aplicação

**Detalhes**: Consulte [persistence.md](persistence.md) para padrões completos de persistência.

## Conclusão

Esta arquitetura fornece uma base sólida para evolução incremental do projeto, permitindo:
- Fácil manutenção e entendimento do código
- Testes automatizados em todos os níveis
- Observabilidade completa
- Segurança por padrão
- Facilidade para futuro escalonamento ou split em microservices

Para implementações específicas, sempre consulte os arquivos detalhados em `.kiro/steering/`:
- [api.md](api.md): Padrões de API/REST
- [security.md](security.md): Padrões de segurança
- [observability.md](observability.md): Padrões de logs e métricas
- [persistence.md](persistence.md): Padrões de banco de dados
- [overview.md](overview.md): Visão geral do projeto
