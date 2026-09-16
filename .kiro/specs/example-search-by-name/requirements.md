# Busca de Serviços por Nome - Requisitos

## Resumo

Adicionar capacidade de buscar serviços por nome ou descrição usando um parâmetro de query. Isso permite que os cidadãos encontrem facilmente os serviços de que precisam sem navegar por todos os serviços.

## Requisitos Funcionais

- [ ] **FR-01**: GET /api/v1/services?search=<term> returns matching services
- [ ] **FR-02**: Search is case-insensitive
- [ ] **FR-03**: Partial match supported in name or description
- [ ] **FR-04**: Only active services are returned
- [ ] **FR-05**: Results are paginated

## Critérios de Aceitação

- [ ] User can search services by typing a term
- [ ] Search matches name OR description (partial)
- [ ] Search is case-insensitive (e.g., "cpf" matches "CPF")
- [ ] Only active services are returned
- [ ] Pagination applies (page, size, total)
- [ ] Response time is acceptable (< 500ms)
- [ ] Auth required (401 if not authenticated)

## Suposições

- Database is available and connected
- User is authenticated (via HTTP Basic)
- Existing CitizenService entity can be queried
- PostgreSQL database supports LIKE queries with wildcards

## Dependências

- Existing CitizenService entity
- Existing ServiceController, ServiceApplicationService, ServiceRepository
- No external dependencies required