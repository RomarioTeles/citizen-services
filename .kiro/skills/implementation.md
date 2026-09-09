# Skill: Implementação Baseada em Specification

## Quando Usar

Esta skill deve ser usada quando:

- Uma spec foi criada e aprovada em `spec.md`
- As tasks foram definidas em `spec.md`
- O design técnico foi aprovado
- Está na hora de escrever código

## O que Fazer

### 1. Ler:

- requirements.md: O que precisa ser feito
- design.md: Como será feito
- tasks.md: Lista de tarefas

**Regra crítica**: Se tasks não estiverem claras ou requirements incompletos, **pare** e use `/spec requirements` ou `/spec design` para refinar.

### 2. Seguir task.md

Para cada task em `task.md`:

1. Leia a task com cuidado
2. Entenda o que precisa ser feito
3. Implemente seguindo padrões do projeto
4. Marque a task como feita quando concluída

### 3. Padrões do Projeto

Consulte os Steering files em `.kiro/steering/`:

- `architecture.md`: Padrões de camadas e estrutura
- `api.md`: Padrões de endpoint, DTOs, status codes
- `persistence.md`: Padrões de banco de dados, Flyway, JPA
- `testing.md`: Padrões de testes, naming, AAA
- `security.md`: Padrões de autenticação, autorização
- `observability.md`: Padrões de logs e métricas

### 4. Seguir Padrões de Código

#### Naming Conventions

- **Entities**: Singular (`CitizenService`)
- **Repositories**: `XxxRepository`
- **Application Services**: `XxxApplicationService`
- **Controllers**: `XxxController`
- **DTOs**: `CreateXxxRequest`, `XxxResponse`
- **Exceptions**: `XxxException`
- **Metrics**: `XxxMetrics`

#### Package Structure

```
service/
├── domain/        # Entidades
├── application/   # Application services
├── api/           # Controllers e DTOs
└── repository/    # Repositories
```

#### Code Style

- Use `record` para DTOs
- Use `Instant` para timestamps
- Use `Optional` para buscas
- Use `Page<T>` para listagens
- Use constructor injection
- Use `@Valid` em request DTOs

### 5. Padrões Específicos por Área

#### API

- Versioned: `/api/v1/`
- JSON format
- HTTP methods: GET, POST, PUT, PATCH, DELETE
- Status codes: 200, 201, 204, 400, 401, 403, 404

#### Persistence

- PostgreSQL 16
- Flyway migrations (V{version}__{description}.sql)
- Spring Data JPA
- Transactions em application layer

#### Security

- HTTP Basic authentication
- Roles: USER, ADMIN
- @PreAuthorize em application services
- Stateless sessions

#### Observability

- ECS JSON logs
- Correlation ID via X-Correlation-Id
- Micrometer + Prometheus metrics
- Actuator health endpoints

### 6. Padrões de Teste

#### Naming

```
should{ExpectedBehavior}When{Condition}
```

Exemplos:

- `shouldCreateServiceAndReturn201`
- `shouldReturn404WhenServiceNotFound`
- `shouldSearchCaseInsensitive`

#### AAA Pattern

```java
@Test
void shouldDoXWhenY() {
    // Given - preparação
    // When - ação
    // Then - verificação
}
```

### 7. Padrões de Logs e Métricas

#### Logs

```java
log.atInfo()
    .addKeyValue("entityId", entity.getId())
    .addKeyValue("action", "created")
    .log("Entity created");
```

#### Métricas

```java
Counter.builder("citizen_services_new_feature_total")
    .description("Total new features created")
    .register(registry)
    .increment();
```

### 8. Tratamento de Exceções

```java
public class NewEntityNotFoundException extends RuntimeException {
    public NewEntityNotFoundException(Long id) {
        super("New entity not found: " + id);
    }
}
```

### 9. Atualizar spec.md

**Regra crítica**: Se durante a implementação surgir uma mudança que contradiz o design, **atualize spec.md antes de alterar o código**.

## O que Evitar

1. **Ignorar requirements**
   - Bad: "Fiz diferente porque achei melhor"
   - Good: "Atualizar spec.md antes de alterar"

2. **Ignorar padrões**
   - Bad: "Não vou usar record aqui"
   - Good: "Sigo os padrões do projeto"

3. **Ignorar testes**
   - Bad: "Vou escrever testes depois"
   - Good: "Testes são parte da implementação"

4. **Merge sem validação**
   - Bad: "Vou merge direto"
   - Good: "Validar contra requirements"

## Comandos do Kiro

Use estes comandos para iniciar:

- `/spec implementation` - Implementar feature
- `/spec tests` - Escrever/Atualizar testes
- `/spec validation` - Validar contra requirements

## Resultado Esperado

- Código implementado seguindo spec.md
- Classes criadas e/ou modificadas
- Migrations criadas (se necessário)
- Padrões do projeto seguidos
- Código limpo e testável

## Próximos Passos

Após implementação:

1. Escrever testes
2. Rodar build e testes
3. Validar contra requirements
4. Atualizar spec.md