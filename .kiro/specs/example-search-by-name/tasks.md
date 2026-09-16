# Service Search by Name - Tasks

## Implementation

- [ ] **T-01**: Update ServiceRepository - add search method
  - File: `src/main/java/io/github/romarioteles/citizenservices/service/repository/ServiceRepository.java`
  - Add: `Page<CitizenService> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable)`

- [ ] **T-02**: Update ServiceApplicationService - add search method
  - File: `src/main/java/io/github/romarioteles/citizenservices/service/application/ServiceApplicationService.java`
  - Add: method `search(String search, Pageable pageable)`
  - Include: @Transactional(readOnly = true)
  - Include: Use search pattern `%search%` with toLowerCase()

- [ ] **T-03**: Update ServiceController - add search endpoint
  - File: `src/main/java/io/github/romarioteles/citizenservices/service/api/ServiceController.java`
  - Add: method `search(String search, Pageable pageable)`
  - Include: @GetMapping (conditional with findAll)

## Tests

- [ ] **T-04**: Write unit tests for search
  - shouldSearchServices
  - shouldSearchCaseInsensitive
  - shouldReturnEmptyWhenNoMatch

- [ ] **T-05**: Write integration tests for repository query
  - shouldFindByNameOrDescription

- [ ] **T-06**: Write E2E tests for search endpoint
  - shouldSearchServicesAndReturnResults
  - shouldSearchCaseInsensitive
  - shouldReturnEmptyWhenNoMatch
  - shouldReturn401WhenNotAuthenticated

## Validation

- [ ] **T-07**: Run all tests and verify they pass
- [ ] **T-08**: Verify requirements are met
- [ ] **T-09**: Verify design was followed
