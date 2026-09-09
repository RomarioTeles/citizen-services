# Citizen Services - Observabilidade

## Estrutura de Logs

### Formato

- **Elastic Common Schema (ECS)** JSON
- Saída via stdout (sem configuração extra)
- Loggers: SLF4J com Logback (padrão do Spring Boot)

### Configuração

```yaml
logging:
  structured:
    format:
      console: ecs
  include-application-name: true
```

### Estrutura de Log

Cada log entry inclui:

```json
{
  "@timestamp": "2024-01-01T00:00:00.000Z",
  "log.level": "INFO",
  "message": "HTTP request completed",
  "service.name": "citizen-services",
  "correlationId": "abc-123-def",
  "method": "GET",
  "path": "/api/v1/services/1",
  "status": 200,
  "durationMs": 45
}
```

### Correlation ID

Cada requisição recebe um correlation ID único:

- Se header `X-Correlation-Id` presente: usa-o
- Caso contrário: gera UUID v4

**Propagação:**
- Header de resposta: `X-Correlation-Id: <id>`
- MDC (Mapped Diagnostic Context): `correlationId`
- Disponível em todos os logs da requisição

### MDC Key

```java
MDC.put("correlationId", correlationId);  // key: "correlationId"
```

## Pipeline de Logs

```
Citizen Services (app)
    ↓ stdout (ECS JSON)
Docker GELF driver
    ↓ UDP 12201
Logstash
    ↓ parse JSON + HTTP
Elasticsearch
    ↓ index: citizen-services-logs-YYYY.MM.dd
Kibana
    ↓ web interface
```

## Métricas

### Micrometer + Prometheus

#### Métricas Automáticas

- JVM memory, threads, GC
- HTTP request counts/durations
- HikariCP connections
- Spring context metrics

#### Métricas Customizadas

```java
@Service
public class ServiceMetrics {
    
    private final Counter created;
    private final Counter consulted;
    private final Counter notFound;
    
    public ServiceMetrics(MeterRegistry registry) {
        created = Counter.builder("citizen_services_registrations_total")
            .description("Total services created")
            .register(registry);
        
        consulted = Counter.builder("citizen_services_consulted_total")
            .description("Total services consulted")
            .register(registry);
        
        notFound = Counter.builder("citizen_services_not_found_total")
            .description("Total services not found")
            .register(registry);
    }
    
    public void incrementCreated() { created.increment(); }
    public void incrementConsulted() { consulted.increment(); }
    public void incrementNotFound() { notFound.increment(); }
}
```

### Endpoints de Métricas

| Endpoint | Descrição |
|----------|-----------|
| `/actuator/metrics` | Lista de todas as métricas disponíveis |
| `/actuator/metrics/{name}` | Detalhes da métrica específica |
| `/actuator/prometheus` | Métricas em formato Prometheus |

### Métricas de Interesse

#### HTTP Requests

```
http_server_requests_seconds_count{method="GET",uri="/api/v1/services/{id}",status="200"}
http_server_requests_seconds_sum{method="GET",uri="/api/v1/services/{id}",status="200"}
http_server_requests_seconds_max{method="GET",uri="/api/v1/services/{id}",status="200"}
```

#### JVM

```
jvm_memory_used_bytes{area="heap"}
jvm_memory_used_bytes{area="nonheap"}
jvm_threads_live{state="runnable"}
jvm_threads_daemon
```

#### HikariCP

```
hikaricp_connections_active{id="HikariPool-1"}
hikaricp_connections_idle{id="HikariPool-1"}
hikaricp_connections_pending{id="HikariPool-1"}
hikaricp_connections_max{id="HikariPool-1"}
hikaricp_connections_min{id="HikariPool-1"}
```

#### Custom

```
citizen_services_registrations_total
citizen_services_consulted_total
citizen_services_not_found_total
```

### Grafana Dashboard

Dashboard provisionado automaticamente com:

- Services Registrados
- Consultas por ID
- Consultas Não Encontradas
- Memória JVM heap
- Requisições HTTP
- Conexões HikariCP
- Threads JVM

## Boas Práticas

### 1. Logs

- Use `log.atLevel()` com adKeyValue (não `@Slf4j` com `+`)
- Include correlationId em todos os logs
- Use `setCause(ex)` para exceptions
- Avoid logging sensitive data (passwords, tokens)

### 2. Métricas

- Use `Counter` para contagens (sem estados)
- Use `Timer` para durações
- Use `Gauge` para valores instantâneos (memory, queue size)
- Sempre inclua tags (method, status, uri)

### 3. Traces

- Use correlationId para rastrear requisições
- Considare usar Spring Cloud Sleuth (já incluído via Actuator)

## State of the Art

### O que está implementado

- ECS JSON logs via stdout
- Correlation ID via filter
- Custom metrics (registrations, consulted, not_found)
- Actuator health endpoints
- Prometheus metrics export
- Grafana dashboard provisionado

### O que não está implementado (sugestões futuras)

- Distributed tracing (Jaeger, Zipkin)
- Log aggregation (ELK stack já configurado, mas ainda needs tuning)
- Metrics alerting (Prometheus alertmanager)
- SLO/SLI definitions