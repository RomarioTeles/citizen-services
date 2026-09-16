# SDD (Spec-Driven Development) - Projeto Citizen Services

## Visão Geral

Este projeto utiliza Spec-Driven Development para garantir rastreabilidade e consistência entre requisitos e implementação.

**Princípio fundamental**: Mudanças relevantes de negócio, novas funcionalidades ou mudanças de arquitetura devem ser precedidas por uma specification clara.

O fluxo deve ser:

**Requirement → Review/Approval → Design → Review/Approval → Tasks → Implementation → Tests → Validation → Review → Merge**

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
    ├── requirements.md   # O que o sistema deve fazer
    ├── design.md         # Como a solução será implementada
    └── tasks.md          # O que precisa ser feito
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

**Regra crítica**: Se durante a implementação surgir uma mudança que contradiz o design, **atualize spec antes de alterar o código**.

---

## Estrutura dos Arquivos da Spec

### `requirements.md`

O que o sistema deve fazer:
- Summary: Descrição concisa da feature
- Functional Requirements: O que o sistema DEVE fazer
- Acceptance Criteria: Quando a feature está "done"
- Assumptions: O que está sendo assumido
- Dependencies: O que precisa estar pronto antes

**O que não incluir:** Detalhes de implementação, escolhas de tecnologia, estrutura de código

### `design.md`

Como a solução será implementada:
- Architecture: Componentes, responsabilidades, localização
- API Changes: Endpoints, DTOs, status codes
- Implementation Details: Classes a criar/modificar
- Testing Strategy: Unit, integration, e2e
- Security and Performance Considerations
- Database Changes: Migrations, se necessário

**O que não incluir:** Código detalhado (isso vem na implementation)

### `tasks.md`

O que precisa ser feito:
- Lista de tarefas (checklist)
- Classes a criar
- Classes a modificar
- Migrations (se necessário)
- Testes a escrever

**O que evitar:** Tarefas genéricas, tarefas muito grandes

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
| `testing.md` | Criação e atualização de testes (unit, integration, e2e) |

---

## Exemplo Rápido: Busca por Nome

```
1. Criar diretório da spec
   mkdir .kiro/specs/service-search-by-name

2. Criar requirements.md
   - Summary
   - Functional Requirements
   - Acceptance Criteria

3. Sugerir review com o time

4. Criar design.md
   - Architecture
   - Implementation Details
   - Testing Strategy

5. Sugerir review com o time

6. Criar tasks.md
   - Lista de tarefas

7. Implementar seguindo tasks

8. Escrever testes

9. Validar contra requirements

10. Merge quando validado
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

### Skills Criadas

- ✅ `requirements.md` - Análise de requirements
- ✅ `implementation.md` - Implementação baseada em spec
- ✅ `testing.md` - Criação e atualização de testes

---

## Notas Importantes

1. **SDD não é burocracia**: É um guia para rastreabilidade e consistência
2. **Adaptable**: Ajustar processo às necessidades do projeto
3. **Specs são vivos**: Atualizem conforme evoluem
4. **Rastreabilidade**: Requirements → Design → Implementation → Tests
5. **Validação**: Sempre validar contra requirements antes de merge
6. **Practical**: Use SDD apenas quando trouxer benefício real