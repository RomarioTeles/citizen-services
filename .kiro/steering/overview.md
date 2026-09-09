# Citizen Services - Visão Geral do Projeto

## Tecnologias Principais

- **Java 21** como linguagem de programação
- **Spring Boot 4.1.0** / Spring Framework 7.x
- **Spring MVC** para web
- **Spring Data JPA / Hibernate** para persistência
- **Flyway** para migrations de banco de dados
- **PostgreSQL 16** como banco de dados
- **Spring Security** com HTTP Basic authentication
- **Micrometer** com Prometheus para métricas
- **Elasticsearch, Kibana, Logstash** para observabilidade de logs
- **Maven** como ferramenta de build

## Estrutura do Projeto

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
├── service/
│   ├── api/           # Controllers e DTOs da API
│   ├── application/   # Casos de uso e lógica de negócio
│   ├── domain/        # Entidades e regras de domínio
│   └── repository/    # Interface de persistência
├── infrastructure/    # Implementações técnicas
│   ├── observability/ # Filtragem de logs e correlation IDs
│   └── security/      # Configuração de segurança
└── ...
```

## Padrões de Design

- **Modular Monolith**: Estrutura modular para permitir evolução incremental
- **Layered Architecture**: Separação clara (domain, application, api, infrastructure)
- **Repository Pattern**: Persistência abstraída
- **DTO Pattern**: Transferência de dados via DTOs
- **Dependency Injection**: Constructor injection

## Estado da Aplicação

### O que existe hoje

- Entidade `CitizenService` com CRUD completo
- Authentication HTTP Basic
- Authorization baseada em roles (USER/ADMIN)
- Métricas customizadas (registrations, consulted, not_found)
- Paginação de listagens
- Validção de dados com Bean Validation
- Swagger/OpenAPI 3.0 (via SpringDoc)
- Sistema de logs estruturados (ECS)
- Health checks (actuator)
- Metrics export (Prometheus)

### O que ainda não existe

- JWT/OAuth2 (planejado para futuro incremento)
- CQRS ou Event Sourcing
- Microservices (current planning is modular monolith)
- Feature flags
- Circuit breakers

## Padrões de Desenvolvimento

- **Tests first**: Escrever testes antes ou junto com código
- **Code review**: Todas as mudanças devem ser revisadas
- **Spec-driven**: Features complexas devem ter spec.md
- **Documentation**: Padrões documentados em `.kiro/steering/`

## Próximos Passos Sugeridos

O projeto está preparado para receber novas features seguindo o fluxo SDD (Spec-Driven Development). Cada nova funcionalidade deve:

1. Ter uma spec.md clara
2. Seguir os padrões arquiteturais existentes
3. Incluir testes apropriados
4. Manter ou melhorar a observabilidade

## Fluxo SDD

Para features complexas, use o fluxo:

**Requirement → Design → Tasks → Implementation → Tests → Validation**

Consulte `.kiro/README.md` para mais detalhes.