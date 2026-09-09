# Citizen Services - Spec-Driven Development (SDD)

## Visão Geral

O SDD (Spec-Driven Development) é um processo para garantir rastreabilidade entre requisitos e implementação.

**Princípio fundamental**: Mudanças relevantes de negócio, novas funcionalidades ou mudanças de arquitetura devem ser precedidas por uma specification clara.

### Quando usar SDD

**Use SDD para:**
- Novas funcionalidades significativas
- Mudanças de comportamento do sistema
- Modificações na arquitetura
- Introdução de novos domínios ou entidades

**Não use SDD para:**
- Correções de bugs triviais
- Typos ou correções de formatação
- Pequenas refatorações sem mudança de comportamento
- Ajustes de configuração sem impacto relevante

---

## Estrutura da Specification

Todas as specs ficam em:

```
.kiro/specs/<feature-name>/
├── requirements.md   # O que o sistema deve fazer
├── design.md         # Como a solução será implementada
└── tasks.md          # O que precisa ser feito
```

### Estrutura dos Arquivos

#### `requirements.md`

Contém **apenas o que** o sistema deve fazer:
- Descrição do problema/need
- Funcionalidades esperadas
- Acceptance criteria (quando está done)
- Assumptions e dependencies

**O que não incluir:**
- Detalhes de implementação
- Escolhas de tecnologia
- Estrutura de código

#### `design.md`

Contém **como** a solução será implementada:
- Architecture (componentes, responsabilidades, localização)
- API changes (endpoints, DTOs, status codes)
- Implementation details (classes a criar/modificar)
- Testing strategy (unit, integration, e2e)
- Security and performance considerations
- Database changes (migrations, se necessário)

**O que não incluir:**
- Código detalhado (isso vem na implementation)
- Decisões arbitrárias sem justificativa

#### `tasks.md`

Contém **o que precisa ser feito**:
- Lista de tarefas (checklist)
- Classes a criar
- Classes a modificar
- Migrations (se necessário)
- Testes a escrever

**O que evitar:**
- Tarefas genéricas ("fazer código")
- Tarefas muito grandes

---

## Processo SDD

### Etapas do Fluxo

```
1. Requirements (requirements.md)
   ↓
2. Review/Approval (humano ou time)
   ↓
3. Design (design.md)
   ↓
4. Review/Approval (humano ou time)
   ↓
5. Tasks (tasks.md)
   ↓
6. Implementation (code)
   ↓
7. Tests (spec ou tests.md)
   ↓
8. Validation (spec ou validation.md)
   ↓
9. Review (qualidade técnica)
   ↓
10. Merge (PR)
```

### Detalhamento das Etapas

| Etapa | Responsabilidade |
|-------|-----------------|
| **Requirements** | "O que o sistema deve fazer?" - Descrever comportamento esperado, acceptance criteria |
| **Design** | "Como a solução será implementada?" - Architecture, componentes, APIs, testes |
| **Tasks** | "O que precisa ser feito?" - Lista de tarefas concretas de implementação |
| **Implementation** | Transformar o design aprovado em código |
| **Tests** | Comprovar o comportamento esperado |
| **Validation** | Verificar se a implementação atende aos requirements |
| **Review** | Verificar qualidade técnica e aderência à specification |

---

## Responsabilidade de Cada Etapa

### 1. Requirements

**Objetivo**: Definir **o que** o sistema deve fazer, do ponto de vista do negócio.

**Conteúdo:**
- Descrição do problema/need
- Funcionalidades esperadas
- Acceptance criteria (quando está done)
- Assumptions e dependencies

**O que não incluir:**
- Detalhes de implementação
- Escolhas de tecnologia
- Estrutura de código

### 2. Review/Approval (Requirements)

**Objetivo**: Validar que os requirements são claros, completos e alinhados com o negócio.

**O que verificar:**
- Requirements são testáveis?
- Acceptance criteria são específicos?
- Dependencies são realistas?
- Valor de negócio está claro?

**Ação esperada:**
- Aprovar e seguir para design
- Rejeitar e solicitar ajustes
- Pedir mais informações

### 3. Design

**Objetivo**: Definir **como** a solução será implementada.

**Conteúdo:**
- Architecture (componentes, responsabilidades, localização)
- API changes (endpoints, DTOs, status codes)
- Implementation details (classes a criar/modificar)
- Testing strategy (unit, integration, e2e)
- Security and performance considerations
- Database changes (migrations, se necessário)

**O que não incluir:**
- Código detalhado (isso vem na implementation)
- Decisões arbitrárias sem justificativa

### 4. Review/Approval (Design)

**Objetivo**: Validar que o design é técnico viável e segue os padrões do projeto.

**O que verificar:**
- Design segue os padrões do projeto (consultar `.kiro/steering/`)?
- Testes são factíveis?
- Segurança e performance foram consideradas?
- Sem sobre-engineering?

**Ação esperada:**
- Aprovar e seguir para tasks
- Sugerir ajustes
- Rejeitar se não segue padrões

### 5. Tasks

**Objetivo**: Decompor o design em tarefas concretas e executáveis.

**Conteúdo:**
- Lista de tarefas (checklist)
- Classes a criar
- Classes a modificar
- Migrations (se necessário)
- Testes a escrever

**O que evitar:**
- Tarefas genéricas ("fazer código")
- Tarefas muito grandes

### 6. Implementation

**Objetivo**: Transformar o design aprovado em código.

**Regras:**
- Seguir as tasks definidas
- Seguir padrões do projeto
- Escrever testes
- Adicionar logs/métricas se necessário

### 7. Tests

**Objetivo**: Comprovar que a implementação funciona conforme esperado.

**Conteúdo:**
- Testes unitários, integration, e2e
- Resultados dos testes (passed/failed)
- Cobertura de testes

### 8. Validation

**Objetivo**: Verificar se a implementação atende aos requirements.

**Conteúdo:**
- Checklist de requirements atendidos
- Checklist de design seguido
- Resultados dos testes
- Observações relevantes

### 9. Review (Final)

**Objetivo**: Verificar qualidade técnica e aderência à specification antes do merge.

**O que verificar:**
- Código limpo e following padrões
- Testes completos e passando
- Documentation atualizada
- Security review
- Performance aceitável

---

## Regra para Mudanças Durante Implementação

**Se durante a implementation for descoberta uma necessidade que contradiz ou altera significativamente a specification:**

1. **Interromper** a implementação daquela decisão
2. **Explicar** a divergência (por que a spec precisa ser alterada)
3. **Propor** a alteração na specification
4. **Atualizar** a specification após decisão/aprovação
5. **Somente então** continuar a implementação

**Para pequenas decisões técnicas** que não alterem requirements ou arquitetura (ex: escolha de nome de variável, estrutura interna de método), o agente pode prosseguir e ** registrar a decisão no commit** ou em comentário no código.

---

## Versionamento e Evolução

### Specification é versionada com código

As specifications fazem parte do repositório Git e devem evoluir junto com a implementação.

**Regras:**
- Uma feature implementada deve ter seus arquivos de spec no repo
- Uma alteração significativa no comportamento deve resultar na atualização correspondente da spec
- Specs antigas devem ser mantidas (não deletadas) para histórico

### Não transforme spec em documentação burocrática

As specs devem conter **somente informações úteis** para:
- Definir comportamento
- Orientar decisões
- Orientar implementação
- Permitir validação

**Evite documentar:**
- Detalhes que já podem ser obtidos diretamente do código
- Decisões triviais sem impacto
- Especificações que não serão usadas para validação

---

## Exemplo Real: Busca de Serviços por Nome

### Passo 1: Requirements

```
User: "Quero adicionar busca de serviços por nome"

Spec: .kiro/specs/service-search-by-name/
```

**Criar `requirements.md`:**
```markdown
# Service Search by Name - Requirements

## Summary
Add ability to search services by name or description using a query parameter.

## Functional Requirements
- [ ] FR-01: GET /api/v1/services?search=<term> returns matching services
- [ ] FR-02: Search is case-insensitive
- [ ] FR-03: Partial match supported in name or description
- [ ] FR-04: Only active services are returned
- [ ] FR-05: Results are paginated

## Acceptance Criteria
- User can search services by typing a term
- Search matches name OR description (partial)
- Search is case-insensitive (e.g., "cpf" matches "CPF")
- Only active services are returned
- Pagination applies
```

### Passo 2: Review/Approval (Requirements)

**Review por desenvolvedor sênior:**
- Requirements claros e testáveis? ✅
- Acceptance criteria específicos? ✅
- Dependencies realistas? ✅
- **Status**: Aprovado → seguir para design

### Passo 3: Design

**Criar `design.md`:**
```markdown
# Service Search by Name - Design

## Architecture
| Component | Responsibility | Location |
|-----------|----------------|----------|
| ServiceRepository | Add search query method | service/repository/ServiceRepository.java |
| ServiceApplicationService | Implement search use case | service/application/ServiceApplicationService.java |
| ServiceController | Add search endpoint | service/api/ServiceController.java |

## Implementation Details
- Classes to Modify: ServiceRepository, ServiceApplicationService, ServiceController
- Migrations: None (uses existing columns)
- Testing Strategy: Unit + Integration + E2E
```

### Passo 4: Review/Approval (Design)

**Review por desenvolvedor sênior:**
- Design segue padrões do projeto? ✅
- Testes factíveis? ✅
- Sem sobre-engineering? ✅
- **Status**: Aprovado → seguir para tasks

### Passo 5: Tasks

**Criar `tasks.md`:**
```markdown
# Service Search by Name - Tasks

- [ ] T-01: Update ServiceRepository - add search method
- [ ] T-02: Update ServiceApplicationService - add search method
- [ ] T-03: Update ServiceController - add search endpoint
- [ ] T-04: Write unit tests for search
- [ ] T-05: Write integration tests for repository query
- [ ] T-06: Write E2E tests for search endpoint
```

### Passo 6-10: Implementation, Tests, Validation, Review, Merge

- Seguir as tasks em `tasks.md`
- Escrever testes
- Atualizar specs com resultados
- Finalizar code review
- Merge quando validado

---

## Estado Atual

### O que está documentado

- Projeto Citizen Services
- Arquitetura modular monolítica
- Padrões de desenvolvimento Java/Spring
- Persistência com PostgreSQL/Flyway
- Testes com JUnit 5 e Spring Test
- Segurança com HTTP Basic
- API/REST com Spring MVC e OpenAPI

### Próximas features

- (A ser definido com usuário)

## Notas Importantes

1. **SDD não é burocracia**: É um guia para rastreabilidade e consistência
2. **Adaptable**: Adjust process to project needs
3. **Continuous**: Specs são atualizados conforme evoluem
4. **Collaborative**: Specs são revisadas pelo time
5. **Practical**: Use SDD apenas quando trouxer benefício real