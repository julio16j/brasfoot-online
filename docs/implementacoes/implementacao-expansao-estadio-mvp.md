# Expansão nativa do estádio — produto mínimo

Status: implementado e validado na branch `impl/expansao-estadio-mvp`, commit `c8a283e0`; integrado à principal por merge local `23c3e355`. Worktree `F:/Projects/Brasfoot-online-impl-expansao-estadio-mvp`, base `b37ff985`.

## Mudanças

`NativeStadium` dimensiona o diálogo pelo conteúdo nativo de 600×330, deixando o botão `BY` em y=290…320 visível; a janela `a.cq` também usa `pack()`. O título da janela de estádio foi corrigido. `NativeClient` valida `CONSTRUCTION_STARTED` e repassa rejeições; após falha, o botão `Cv` volta a habilitar para nova tentativa. `CareerManagement.rollback` remove somente o projeto criado pelo comando que falhou e restaura saldo e lançamento contábil de construção quando o checkpoint falha.

## Matriz de validação da branch

| Cenário | Execução | Resultado e evidência |
| --- | --- | --- |
| Regressão geral | `python tools/build_adapter.py --test` | Aprovada na branch após correção visual; teste de rollback específico repetido após sua inclusão |
| Motor: cotação, débito e rejeições | `CareerManagementStadiumTest` com carreira real | Aprovou preço nativo, débito único, projeto, duplicata, limite e rollback de saldo/projeto/lançamento; log `stadium-test.log` na worktree |
| Janela Swing real | `NativeStadiumLayoutTest` | Aprovou botão visível, clicável, título e abertura de `a.cq`; capturas `analysis/stadium-mvp-validation/stadium-main.png` e `stadium-construction.png` inspecionadas |
| Artefato da branch | `python tools/package_client.py`; `NativeStadiumLayoutTest` com `dist/brasfoot-online.jar` | Pacote gerado, ZIP de 80.295.998 bytes; teste gráfico do JAR aprovado e capturas separadas em `analysis/stadium-mvp-validation-packaged/` |

Limites: a captura original do Augsburg não informa se havia obra ativa, condição em que o motor oculta legitimamente `BY`. O teste gráfico exercitou Corinthians sem obra; ainda falta repetir especificamente a tela com obra ativa, saldo insuficiente, falha de rede/checkpoint via HTTP e conclusão de obra após avanço. A matriz do pacote principal combinado será registrada após integração. Não encerrar a task antes desses critérios.

Após o merge, `python tools/build_adapter.py` e `CareerManagementStadiumTest` passaram na principal; o teste de rollback confirmou saldo, obra e lançamento contábil. O ZIP da principal já tinha alteração local antes da integração (SHA-256 `37cfb5b5e5898bc9c8d032f69abf2519091b0b09365aff5889cf5960c367a5f6`); não foi sobrescrito por um empacotamento combinado. O pacote da branch foi gerado e testado separadamente, sem assumir que substitui o ZIP da principal.
