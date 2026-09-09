# SDD (Spec-Driven Development) - Projeto Citizen Services

## Visão Geral

Este projeto utiliza Spec-Driven Development para garantir rastreabilidade e consistência entre requisitos e implementação.

**Princípio fundamental**: Mudanças relevantes de negócio, novas funcionalidades ou mudanças de arquitetura devem ser precedidas por uma specification clara.

O fluxo deve ser:

**Requirement → Design → Tasks → Implementation → Tests → Validation → Review → Merge**

Se durante a implementação surgir uma mudança que contradiga a specification, o agente **deve** identificar isso e propor a atualização da specification antes de alterar o código.

---

## Estrutura `.kiro/`

```
.kiro/
├── README.md          # Este arquivo - guia rápido de uso do SDD
├── steering/          # Contexto e regras do projeto
│   ├── overview.md    # Visão geral do projeto, tecnologias
│   ├── api.md         # Padrões de API/REST
│   ├── persistence.md # Padrões de banco de dados
│   ├── security.md    # Padrões de segurança
│   ├── observability.md # Padrões de logs e métricas
│   └── sdd.md         # Processo SDD (não repetir em skills)
└── skills/            # Workflows para desenvolvimento
    ├── requirements.md    # Análise e refinamento de requirements
    └── implementation.md  # Implementação baseada em spec

specs/                 # Specs de features (criadas durante desenvolvimento)
└── <feature-name>/
    └── spec.md      # Requisitos + Design + Tasks em um único arquivo
```

---

## Quando Usar SDD

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

## Processo SDD (Resumo)

```
1. Requirements (spec.md)
   ↓
2. Review/Approval (humano ou time)
   ↓
3. Design (spec.md)
   ↓
4. Review/Approval (humano ou time)
   ↓
5. Tasks (spec.md)
   ↓
6. Implementation (code)
   ↓
7. Tests (spec.md)
   ↓
8. Validation (spec.md)
   ↓
9. Review (qualidade técnica)
   ↓
10. Merge (PR)
```

**Regra crítica**: Se durante a implementação surgir uma mudança que contradiz o design, **atualize spec.md antes de alterar o código**.

---

## Estrutura do `spec.md`

```markdown
# <Feature Name> - Spec

## Requirements
O que o sistema deve fazer, do ponto de vista do negócio.

## Design
Como a solução será implementada.

### Architecture
- Components: Tabela com responsabilidades
- Data Model: Entidades e campos
- API Changes: Endpoints e métodos

### Implementation Details
- Classes to Create: Lista de novas classes
- Classes to Modify: Lista de modificações
- Migrations: Mudanças no banco de dados

### Testing Strategy
- Unit tests: O que testar
- Integration tests: O que testar
- E2E tests: O que testar

### Security Considerations
- Roles e permissions
- Validations

### Performance Considerations
- Expected response time
- Pagination

## Tasks
Lista de tarefas para implementação.

- [ ] Create entity class
- [ ] Create repository interface
- [ ] Create DTOs
- [ ] Update ServiceApplicationService
- [ ] Create API endpoints
- [ ] Add Flyway migration (if needed)
```

---

## Padrões do Projeto

Consulte os arquivos em `.kiro/steering/` para:

- **overview.md**: Visão geral do projeto, tecnologias, estrutura
- **api.md**: Padrões de API/REST (endpoints, DTOs, status codes, validação, pagination)
- **persistence.md**: Padrões de banco de dados (PostgreSQL, Flyway, JPA, migrations)
- **security.md**: Padrões de segurança (HTTP Basic, roles, autorização, configuração)
- **observability.md**: Padrões de logs (ECS) e métricas (Micrometer/Prometheus)
- **sdd.md**: Processo SDD completo (recomendado ler antes de começar)

---

## Workflows/Skills Disponíveis

| Skill | Responsabilidade |
|-------|-----------------|
| `requirements.md` | Análise e refinamento de requirements (perguntas de clarificação, MoSCoW, acceptance criteria) |
| `implementation.md` | Implementação baseada em spec (padrões de código, classes, testes, logs, métricas) |

---

## Exemplo Rápido: Busca por Nome

```
1. Criar spec
   mkdir .kiro/specs/service-search-by-name
   touch .kiro/specs/service-search-by-name/spec.md

2. Preencher spec.md
   - Requirements: O que o sistema deve fazer
   - Design: Como será implementado
   - Tasks: Lista de tarefas

3. Sugerir review com o time

4. Implementar seguindo tasks

5. Escrever testes

6. Validar contra requirements

7. Merge quando validado
```

---

## Estado Atual

### Steering Criado

- ✅ `overview.md` - Visão geral do projeto
- ✅ `api.md` - Padrões de API/REST
- ✅ `persistence.md` - Padrões de banco de dados
- ✅ `security.md` - Padrões de segurança
- ✅ `observability.md` - Padrões de logs e métricas
- ✅ `sdd.md` - Processo SDD completo

### Skills Criada

- ✅ `requirements.md` - Análise de requirements
- ✅ `implementation.md` - Implementação baseada em spec

---

## Notas Importantes

1. **SDD não é burocracia**: É um guia para rastreabilidade e consistência
2. **Adaptable**: Adjust process to project needs
3. **Specs são vivos**: Atualizem conforme evoluem
4. **Rastreabilidade**: Requirements → Design → Implementation → Tests
5. **Validação**: Sempre validar contra requirements antes de merge
6. **Practical**: Use SDD apenas quando trouxer benefício real