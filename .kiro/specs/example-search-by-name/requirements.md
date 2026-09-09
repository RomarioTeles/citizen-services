# Service Search by Name - Requirements

## Summary

Add ability to search services by name or description using a query parameter. This allows citizens to easily find the services they need without browsing all services.

## Functional Requirements

- [ ] **FR-01**: GET /api/v1/services?search=<term> returns matching services
- [ ] **FR-02**: Search is case-insensitive
- [ ] **FR-03**: Partial match supported in name or description
- [ ] **FR-04**: Only active services are returned
- [ ] **FR-05**: Results are paginated

## Acceptance Criteria

- [ ] User can search services by typing a term
- [ ] Search matches name OR description (partial)
- [ ] Search is case-insensitive (e.g., "cpf" matches "CPF")
- [ ] Only active services are returned
- [ ] Pagination applies (page, size, total)
- [ ] Response time is acceptable (< 500ms)
- [ ] Auth required (401 if not authenticated)

## Assumptions

- Database is available and connected
- User is authenticated (via HTTP Basic)
- Existing CitizenService entity can be queried
- PostgreSQL database supports LIKE queries with wildcards

## Dependencies

- Existing CitizenService entity
- Existing ServiceController, ServiceApplicationService, ServiceRepository
- No external dependencies required