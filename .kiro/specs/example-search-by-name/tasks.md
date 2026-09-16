# Busca de Serviços por Nome - Tarefas

## Implementação

- [ ] **T-01**: Update ServiceRepository - adicionar método de busca
  - Arquivo: `src/main/java/io/github/romarioteles/citizenservices/service/repository/ServiceRepository.java`
  - Adicionar: `Page<CitizenService> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable)`

- [ ] **T-02**: Update ServiceApplicationService - adicionar método de busca
  - Arquivo: `src/main/java/io/github/romarioteles/citizenservices/service/application/ServiceApplicationService.java`
  - Adicionar: método `search(String search, Pageable pageable)`
  - Incluir: @Transactional(readOnly = true)
  - Incluir: Usar padrão de busca `%search%` com toLowerCase()

- [ ] **T-03**: Update ServiceController - adicionar endpoint de busca
  - Arquivo: `src/main/java/io/github/romarioteles/citizenservices/service/api/ServiceController.java`
  - Adicionar: método `search(String search, Pageable pageable)`
  - Incluir: @GetMapping (conditional com findAll)

## Testes

- [ ] **T-04**: Escrever testes unitários para busca
  - shouldSearchServices
  - shouldSearchCaseInsensitive
  - shouldReturnEmptyWhenNoMatch

- [ ] **T-05**: Escrever testes de integração para query do repository
  - shouldFindByNameOrDescription

- [ ] **T-06**: Escrever testes E2E para endpoint de busca
  - shouldSearchServicesAndReturnResults
  - shouldSearchCaseInsensitive
  - shouldReturnEmptyWhenNoMatch
  - shouldReturn401WhenNotAuthenticated

## Validação

- [ ] **T-07**: Executar todos os testes e verificar se passam
- [ ] **T-08**: Verificar se requisitos são atendidos
- [ ] **T-09**: Verificar se design foi seguido