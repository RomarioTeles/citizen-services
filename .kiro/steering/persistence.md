# Citizen Services - Persistência

## Banco de Dados

### Tecnologia

- **PostgreSQL 16** como banco de dados relacional
- Conexão via JDBC
- Pool de conexões: HikariCP (padrão do Spring Boot)

### Configuração

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/citizenservices}
    username: ${DB_USERNAME:citizen}
    password: ${DB_PASSWORD:citizen}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
```

### Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| DB_URL | `jdbc:postgresql://localhost:5432/citizenservices` | URL de conexão JDBC |
| DB_USERNAME | `citizen` | Username para conexão |
| DB_PASSWORD | `citizen` | Password para conexão |

## Migrations com Flyway

### Configuração

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Padrão de Nomenclatura

```
V{version}__{description}.sql
```

Exemplos:
- `V1__baseline.sql`
- `V2__create_services_table.sql`

### Boas Práticas

1. **Sempre adicionar migrations novas** - NUNCA alterar migrations existentes
2. **Migrations devem ser idempotentes** quando possível
3. **Usar nomes descritivos** para facilitar entendimento
4. **Incluir both DDL e DML** na mesma migration se necessário
5. **Testar migrations em ambiente limpo** antes de deploy

## Entidades JPA

### Padrão de Entidade

```java
@Entity
@Table(name = "services")
public class CitizenService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "active", nullable = false)
    private boolean active;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // getters/setters...
}
```

### Convenções

- **Entidades**: Nome no singular, `@Entity` no topo
- **Table name**: Nome em plural (`@Table(name = "services")`)
- **IDs**: `Long` com `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- **Campos não-nulos**: `nullable = false`
- **Timestamps**: `createdAt` (immutable) e `updatedAt` (mutável)
- **Dates**: Usar `Instant` (UTC)

## Repositories

### Interface Base

```java
public interface ServiceRepository extends JpaRepository<CitizenService, Long> {
    // Métodos customizados podem ser adicionados aqui
}
```

### Padrões de Consulta

1. **Métodos derivados** (method name query):
   ```java
   Optional<CitizenService> findByName(String name);
   List<CitizenService> findByActiveTrue();
   ```

2. **Consultas customizadas** com `@Query`:
   ```java
   @Query("SELECT s FROM CitizenService s WHERE s.name LIKE %:name%")
   Page<CitizenService> findByNameContaining(@Param("name") String name, Pageable pageable);
   ```

### Boas Práticas

- Manter repositories como interfaces (implementação via Spring Data JPA)
- Usar `Optional` para retornos de busca por ID
- Usar `Page<T>` para listagens paginadas
- Não expor `Iterable` diretamente (pode causar N+1 queries)

## Transações

### Padrão de Uso

```java
@Service
public class ServiceApplicationService {
    
    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        // Operações de escrita
    }
    
    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        // Operações de leitura
    }
}
```

### Regras

- **Métodos que escrevem**: `@Transactional`
- **Métodos que só leem**: `@Transactional(readOnly = true)`
- **Camada de aplicação** gerencia transações
- **Não anotar controllers** com `@Transactional`

## State of the Art

### O que está implementado

- `CitizenService` entity com CRUD completo
- Flyway migrations para schema management
- Repository pattern com Spring Data JPA
- Transações gerenciadas via Spring

### O que não está implementado (sugestões futuras)

- Cache com Redis
- Soft delete (coluna `deleted_at`)
- Versionamento de entidades
- Audit logging (quem criou/alterou e quando)