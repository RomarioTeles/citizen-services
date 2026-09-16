# Busca de Serviços por Nome - Design

## Arquitetura

| Componente | Responsabilidade | Localização |
|-----------|----------------|----------|
| ServiceRepository | Adicionar método de query de busca | service/repository/ServiceRepository.java |
| ServiceApplicationService | Implementar caso de uso de busca | service/application/ServiceApplicationService.java |
| ServiceController | Adicionar endpoint de busca | service/api/ServiceController.java |

## Modelo de Dados

**CitizenService (existing)**

| Campo | Tipo | Buscável |
|-------|------|------------|
| id | Long | No |
| name | String | Yes |
| description | String | Yes |
| active | boolean | Filter only active |
| createdAt | Instant | No |
| updatedAt | Instant | No |

## Mudanças na API

| Endpoint | Método | Parâmetros de Request | Response |
|----------|--------|----------------|----------|
| /api/v1/services | GET | search (opcional), page, size | ServicePageResponse |

## Detalhes de Implementação

**Classes a Criar**

- None (uses existing CitizenService)

**Classes a Modificar**

1. `ServiceRepository`
   - Adicionar método: `Page<CitizenService> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable)`

2. `ServiceApplicationService`
   - Adicionar método: `search(String search, Pageable pageable)` com @Transactional(readOnly = true)
   - Usar padrão de busca `%search%` com toLowerCase()

3. `ServiceController`
   - Adicionar método de busca: `search(String search, Pageable pageable)` com @GetMapping
   - Tratar search null/empty (retorna todos)

**Migrations**

- None (search uses existing columns)

## Estratégia de Testes

- **Unit**: ServiceApplicationService.search()
- **Integration**: ServiceRepository.findByNameContainingOrDescriptionContaining()
- **E2E**: ServiceController.searchServices()

## Considerações de Segurança

- Requires authentication (USER or ADMIN role)
- No SQL injection (Spring Data JPA uses prepared statements)
- No XSS (Response is JSON, no HTML rendering)

## Considerações de Performance

- Expected response time: < 500ms
- Pagination required to avoid large result sets
- Database index recommended on name and description columns