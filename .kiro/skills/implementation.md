# Skill: Implementação Baseada em Specification

## Quando Usar

Esta skill deve ser usada quando:

- Uma spec foi criada e aprovada (requirements.md, design.md e tasks.md)
- As tasks foram definidas em `tasks.md`
- O design técnico foi aprovado
- Está na hora de escrever código

**Importante**: Leia primeiro o `sdd.md` para entender o processo completo antes de começar a implementar.

## O que Fazer

### 1. Ler os arquivos da spec

- **requirements.md**: O que precisa ser feito
- **design.md**: Como será feito
- **tasks.md**: Lista de tarefas de implementação

**Regra crítica**: Se tasks não estiverem claras ou requirements incompletos, **pare** e use a skill `requirements.md` para refinar.

### 2. Seguir tasks.md

Para cada task em `tasks.md`:

1. Leia a task com cuidado
2. Entenda o que precisa ser feito
3. Implemente seguindo padrões do projeto
4. Marque a task como feita quando concluída

### 3. Padrões do Projeto

Consulte os Steering files em `.kiro/steering/`:

- `overview.md`: Visão geral do projeto
- `api.md`: Padrões de endpoint, DTOs, status codes
- `persistence.md`: Padrões de banco de dados, Flyway, JPA
- `security.md`: Padrões de autenticação e autorização
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

---

## Regra para Mudanças Durante Implementação

**Se durante a implementação for descoberta uma necessidade que contradiz ou altera significativamente a specification:**

1. **Interromper** a implementação daquela decisão
2. **Explicar** a divergência (por que a spec precisa ser alterada)
3. **Propor** a alteração na specification
4. **Atualizar** a spec (requirements.md e/ou design.md) após decisão/aprovação
5. **Somente então** continuar a implementação

**Para pequenas decisões técnicas** que não alterem requirements ou arquitetura (ex: escolha de nome de variável, estrutura interna de método), o agente pode prosseguir e **registrar a decisão no commit** ou em comentário no código.

## O que Evitar

1. **Ignorar requirements**
   - Bad: "Fiz diferente porque achei melhor"
   - Good: "Atualizar requirements.md antes de alterar"

2. **Ignorar design**
   - Bad: "Mudei a estrutura porque sim"
   - Good: "Seguir design.md"

3. **Ignorar tasks**
   - Bad: "Pulei algumas tasks"
   - Good: "Seguir tasks.md"

4. **Ignorar padrões**
   - Bad: "Não vou usar record aqui"
   - Good: "Sigo os padrões do projeto"

5. **Ignorar testes**
   - Bad: "Vou escrever testes depois"
   - Good: "Testes são parte da implementação"

6. **Merge sem validação**
   - Bad: "Vou merge direto"
   - Good: "Validar contra requirements"

## Resultado Esperado

- Código implementado seguindo requirements.md e design.md
- Classes criadas e/ou modificadas
- Migrations criadas (se necessário)
- Padrões do projeto seguidos
- Código limpo e testável
- Tasks marcadas como feitas em tasks.md

## Próximos Passos

Após implementação:

1. Escrever testes
2. Rodar build e testes (`./mvnw test`)
3. Atualizar tasks.md com status
4. Escrever/Atualizar tests.md com resultados
5. Validar contra requirements (verificar acceptance criteria)