# Service Search by Name - Spec

## Requirements

O que o sistema deve fazer, do ponto de vista do negócio.

- [ ] **FR-01**: GET /api/v1/services?search=<term> returns matching services
- [ ] **FR-02**: Search is case-insensitive
- [ ] **FR-03**: Partial match supported in name or description
- [ ] **FR-04**: Only active services are returned
- [ ] **FR-05**: Results are paginated

### Acceptance Criteria

- [ ] User can search services by typing a term
- [ ] Search matches name OR description (partial)
- [ ] Search is case-insensitive (e.g., "cpf" matches "CPF")
- [ ] Only active services are returned
- [ ] Pagination applies (page, size, total)
- [ ] Response time is acceptable (< 500ms)
- [ ] Auth required (401 if not authenticated)

## Design

Como a solução será implementada.

### Architecture

| Component | Responsibility | Location |
|-----------|----------------|----------|
| ServiceRepository | Add search query method | service/repository/ServiceRepository.java |
| ServiceApplicationService | Implement search use case | service/application/ServiceApplicationService.java |
| ServiceController | Add search endpoint | service/api/ServiceController.java |

### Data Model

**CitizenService (existing)**

| Field | Type | Searchable |
|-------|------|------------|
| id | Long | No |
| name | String | Yes |
| description | String | Yes |
| active | boolean | Filter only active |
| createdAt | Instant | No |
| updatedAt | Instant | No |

### API Changes

| Endpoint | Method | Request Params | Response |
|----------|--------|----------------|----------|
| /api/v1/services | GET | search (optional), page, size | ServicePageResponse |

### Implementation Details

**Classes to Create**

- None (uses existing CitizenService)

**Classes to Modify**

1. `ServiceRepository`
   - Add method: `Page<CitizenService> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable)`

2. `ServiceApplicationService`
   - Add method: `search(String search, Pageable pageable)` with @Transactional(readOnly = true)
   - Use search pattern `%search%` with toLowerCase()

3. `ServiceController`
   - Add search method: `search(String search, Pageable pageable)` with @GetMapping
   - Handle null/empty search (return all)

**Migrations**

- None (search uses existing columns)

### Testing Strategy

- **Unit**: ServiceApplicationService.search()
- **Integration**: ServiceRepository.findByNameContainingOrDescriptionContaining()
- **E2E**: ServiceController.searchServices()

### Security Considerations

- Requires authentication (USER or ADMIN role)
- No SQL injection (Spring Data JPA uses prepared statements)
- No XSS (Response is JSON, no HTML rendering)

### Performance Considerations

- Expected response time: < 500ms
- Pagination required to avoid large result sets
- Database index recommended on name and description columns

## Tasks

Lista de tarefas para implementação.

- [ ] **T-01**: Update ServiceRepository - add search method
- [ ] **T-02**: Update ServiceApplicationService - add search method
- [ ] **T-03**: Update ServiceController - add search endpoint
- [ ] **T-04**: Write unit tests for search
- [ ] **T-05**: Write integration tests for repository query
- [ ] **T-06**: Write E2E tests for search endpoint