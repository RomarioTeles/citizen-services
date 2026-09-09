# Skill: Análise e Refinamento de Requirements

## Quando Usar

Esta skill deve ser usada quando:

- Requirements iniciais precisam ser mais específicos
- Há ambiguidade nos requirements atuais
- Requirements precisam ser validados com o usuário/time
- Requirements precisam ser priorizados

## O que Fazer

### 1. Entender o Contexto

- Qual é o problema ou需求 a ser resolvido?
- Quem são os stakeholders?
- Qual é o impacto no sistema existente?
- Há dependências de outras features?

### 2. Fazer Perguntas de Clarificação

Pergunte sobre:

- **Comportamento esperado**: "Quando X acontecer, o sistema deve Y?"
- **Cenários extremos**: "E se Z acontecer? O que o sistema deve fazer?"
- **Casos de erro**: "Como erros devem ser tratados?"
- **Validações**: "Quais são as regras de validação?"
- **Performance**: "Quais são as expectativas de performance?"
- **Segurança**: "Quais são os requisitos de segurança?"
- **Integrações**: "O sistema precisa se integrar com algo externo?"

### 3. Identificar Requirements Missing

Verifique se faltam:

- **Functional requirements**: O que o sistema deve fazer
- **Non-functional requirements**: Performance, segurança, etc.
- **Acceptance criteria**: Como saber que está done?
- **Assumptions**: O que está sendo assumido
- **Dependencies**: O que precisa estar pronto antes

### 4. Priorizar Requirements

Use MoSCoW:

- **Must have**: Requirements essenciais para MVP
- **Should have**: Important mas não crítico
- **Could have**: Nice to have
- **Won't have**: For future iterations

### 5. Adicionar Examples e Use Cases

Para cada requirement, adicione:

- **Request example**: JSON de exemplo
- **Response example**: JSON esperado
- **Error example**: Como erros são tratados
- **Use case**: Cenário de uso

### 6. Validar com o Usuário/Time

- Apresente requirements refinados
- Pergunte: "Isso cobre o que você precisa?"
- Pergunte: "Qual é a prioridade?"
- Obter **aprovação** antes de seguir para design

## Perguntas Comuns para Clarificação

### Para Features de API

- Qual endpoint deve ser usado?
- Quais parâmetros são necessários?
- Qual é o comportamento de paginação?
- Quais são os códigos de status esperados?
- Quais validações devem ser feitas?

### Para Features de Domain

- Quais são as entidades envolvidas?
- Quais são os relacionamentos?
- Quais regras de negócio devem ser aplicadas?
- Como dados devem ser persistidos?

### Para Features de Segurança

- Quais roles devem ter acesso?
- Quais validações de segurança são necessárias?
- Audit logging é necessário?

### Para Features de Performance

- Qual é o volume esperado de requisições?
- Qual é o tempo de resposta aceitável?
- Paging é necessário?

## O que Evitar

1. **Assumir comportamentos**
   - Bad: "Vou assumir que X deve acontecer"
   - Good: "Para confirmar, X deve acontecer?"

2. **Fazer perguntas demais**
   - Priorize requirements críticos
   - Perguntas de clarificação devem ser concisas

3. **Ignorar non-functional requirements**
   - Performance, security, scalability são requirements também

4. **Não documentar decisões**
   - Anote todas as decisões e porquês

5. **Saltar approval**
   - Requisitos precisam ser aprovados antes de seguir para design

## Resultado Esperado

- Requirements claros e específicos
- Acceptance criteria definidos
- Examples de requests/responses
- Priorização MoSCoW
- Perguntas de clarificação respondidas
- **Aprovação do time/usuário**

## Próximos Passos

Após refinamento e aprovação:

1. Documentar requirements em `spec.md`
2. Criar design técnico em `spec.md`
3. Definir tasks de implementação em `spec.md`
4. Implementar seguindo spec.md