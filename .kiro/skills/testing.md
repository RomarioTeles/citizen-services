# Skill: Criação e Atualização de Testes

## Quando Usar

Esta skill deve ser usada quando:

- Nova feature está sendo implementada
- Código existente precisa ser testado
- Testes precisam ser atualizados após mudanças
- Código está sendo refatorado

## O que Fazer

### 1. Entender o Código a Testar

- Leia o código que será testado
- Entenda os comportamentos esperados
- Identifique casos extremos e erros

### 2. Tipos de Teste

#### Unit Tests

**O que testar:**
- Lógica de negócio da classe
- Métodos públicos da classe
- Comportamento em diferentes cenários

**O que mockar:**
- Dependências (repositories, services externos)
- External calls

**Não mockar:**
- Classes puras ( DTOs, value objects)
- Classes com pouca lógica

#### Integration Tests

**O que testar:**
- Integração com Spring context
- Repository com banco de dados
- Transações
- Persistence

**O que usar:**
- `@SpringBootTest`
- `@DataJpaTest` (para repositories)
- `TestConfiguration` para mocks seletivos

#### E2E Tests

**O que testar:**
- Endpoints HTTP completos
- Security
- Validation
- Responses JSON

**O que usar:**
- `@SpringBootTest`
- `MockMvc`
- `SecurityMockMvcRequestPostProcessors.httpBasic()`

### 3. Padrão de Teste (AAA)

```java
@Test
void shouldDoXWhenY() {
    // Given - preparação
    // When - ação
    // Then - verificação
}
```

### 4. Naming Conventions

```
should{ExpectedBehavior}When{Condition}
```

Exemplos:

```java
void shouldCreateServiceAndReturn201()
void shouldReturn404WhenServiceNotFound()
void shouldReturn400WhenNameIsBlank()
void shouldUpdateServiceWhenValidRequest()
void shouldDeactivateServiceWhenActivateEndpointCalled()
```

### 5. Padrões por Camada

#### Entity Tests

```java
class CitizenServiceTest {
    
    @Test
    void shouldCreateWithValidData() {
        // Given
        String name = "Emissão de certidão";
        String description = "Solicitação de emissão";
        
        // When
        CitizenService service = new CitizenService(name, description);
        
        // Then
        assertThat(service.getName()).isEqualTo(name);
        assertThat(service.getDescription()).isEqualTo(description);
        assertThat(service.isActive()).isTrue();
    }
    
    @Test
    void shouldUpdateName() {
        // Given
        CitizenService service = new CitizenService("Original", "Desc");
        
        // When
        service.setName("Updated");
        
        // Then
        assertThat(service.getName()).isEqualTo("Updated");
    }
}
```

#### Repository Tests

```java
@SpringBootTest
class ServiceRepositoryTest {
    
    @Autowired
    private ServiceRepository repository;
    
    @Test
    void shouldSaveAndFind() {
        // Given
        CitizenService service = new CitizenService("Test", "Desc");
        
        // When
        CitizenService saved = repository.save(service);
        CitizenService found = repository.findById(saved.getId()).get();
        
        // Then
        assertThat(found).isEqualTo(saved);
    }
    
    @Test
    void shouldFindAllWithPagination() {
        // Given
        repository.save(new CitizenService("Service 1", "Desc"));
        repository.save(new CitizenService("Service 2", "Desc"));
        
        // When
        Page<CitizenService> page = repository.findAll(PageRequest.of(0, 1));
        
        // Then
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
```

#### Application Service Tests

```java
@ExtendWith(MockExtension.class)
class ServiceApplicationServiceTest {
    
    @Mock
    private ServiceRepository repository;
    
    @Mock
    private ServiceMetrics metrics;
    
    private ServiceApplicationService service;
    
    @BeforeEach
    void setUp() {
        service = new ServiceApplicationService(repository, metrics);
    }
    
    @Test
    void shouldCreateService() {
        // Given
        CreateServiceRequest request = new CreateServiceRequest("Test", "Desc");
        CitizenService saved = new CitizenService("Test", "Desc");
        saved.setId(1L);
        when(repository.save(any())).thenReturn(saved);
        
        // When
        ServiceResponse response = service.create(request);
        
        // Then
        assertThat(response.name()).isEqualTo("Test");
        verify(repository).save(any());
        verify(metrics).incrementCreated();
    }
    
    @Test
    void shouldReturn404WhenNotFound() {
        // Given
        when(repository.findById(any())).thenReturn(Optional.empty());
        
        // When / Then
        assertThatThrownBy(() -> service.findById(999L))
            .isInstanceOf(ServiceNotFoundException.class);
        
        verify(metrics).incrementNotFound();
    }
}
```

#### Controller Tests (E2E)

```java
@SpringBootTest
class ServiceControllerTest {
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }
    
    @Test
    void shouldCreateServiceAndReturn201() throws Exception {
        mockMvc.perform(authAdmin(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Emissão de certidão",
                      "description": "Solicitação de emissão"
                    }
                    """)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Emissão de certidão"))
            .andExpect(jsonPath("$.description").value("Solicitação de emissão"));
    }
    
    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(authAdmin(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "description": "Desc"
                    }
                    """)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").value("não pode ser vazio"));
    }
    
    private static MockHttpServletRequestBuilder authAdmin(MockHttpServletRequestBuilder builder) {
        return builder.with(httpBasic("admin", "admin-password"));
    }
}
```

### 6. Validations

```java
@Test
void shouldValidateNotBlankName() {
    // Given
    CreateServiceRequest request = new CreateServiceRequest("", "Desc");
    
    // When / Then
    assertThatThrownBy(() -> new CreateServiceRequest("", "Desc"))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### 7. Error Cases

```java
@Test
void shouldReturn401WhenNotAuthenticated() throws Exception {
    mockMvc.perform(get("/api/v1/services/1"))
        .andExpect(status().isUnauthorized());
}

@Test
void shouldReturn403WhenUserLacksPermission() throws Exception {
    mockMvc.perform(post("/api/v1/services")
            .with(httpBasic("dev", "dev-password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"name":"Test","description":"Desc"}"""))
        .andExpect(status().isForbidden());
}

@Test
void shouldReturn404WhenIdNotFound() throws Exception {
    mockMvc.perform(auth(get("/api/v1/services/99999")))
        .andExpect(status().isNotFound());
}
```

### 8. Metrics

```java
@Test
void shouldIncrementMetricsOnCreate() {
    // Given
    when(repository.save(any())).thenReturn(savedService);
    
    // When
    service.create(request);
    
    // Then
    verify(metrics).incrementCreated();
}

@Test
void shouldIncrementNotFoundMetricWhenNotFound() {
    // Given
    when(repository.findById(any())).thenReturn(Optional.empty());
    
    // When / Then
    assertThatThrownBy(() -> service.findById(1L))
        .isInstanceOf(ServiceNotFoundException.class);
    
    verify(metrics).incrementNotFound();
}
```

### 9. Transactions

```java
@Test
@Transactional
void shouldRollbackOnException() {
    // Given
    when(repository.save(any())).thenThrow(new RuntimeException("Test error"));
    
    // When / Then
    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(RuntimeException.class);
    
    // Then - entity should not be saved
    assertThat(repository.count()).isEqualTo(0);
}
```

### 10. Coverage

- **Unit tests**: 100% coverage de lógica de negócio
- **Integration tests**: Cobrir todas as integrações críticas
- **E2E tests**: Cobrir fluxos principais do usuário

## O que Evitar

1. **Testes que apenas chamam métodos**
   - Bad: `void shouldCallMethod() { service.method(); }`
   - Good: `void shouldReturnExpectedResult() { assertThat(service.method()).isEqualTo(expected); }`

2. **Testes que não validam**
   - Bad: `void test1() { service.method(); }`
   - Good: `void shouldDoX() { assertThat(...).isEqualTo(...); }`

3. **Testes que dependem de ordem**
   - Cada teste deve ser independente
   - Usar `@BeforeEach` para preparação

4. **Testes que testam múltiplas coisas**
   - Um teste = um comportamento
   - Se necessário, quebre em múltiplos testes

5. **Testes lentos**
   - Unit tests devem ser rápidos (ms)
   - Integration tests podem ser mais lentos (segundos)

## Ferramentas

- Use `@ExtendWith(MockExtension.class)` para Mockito
- Use `@SpringBootTest` para testes com contexto
- Use `MockMvc` para testes HTTP
- Use `SecurityMockMvcRequestPostProcessors` para auth

## Comandos do Kiro

Use estes comandos para iniciar:

- `#Tests escrever testes para busca por nome`
- `#Testing criar testes para nova feature`
- `#QA como testar a nova funcionalidade?`

## Resultado Esperado

- Testes unitários, integration e E2E
- Testes seguindo padrões do projeto
- Testes com naming claro
- Testes que validam comportamento

## Próximos Passos

Após testes escritos:

1. Rodar testes e verificar se passam
2. Atualizar tasks.md
3. Atualizar tests.md com resultados
4. Validar coverage