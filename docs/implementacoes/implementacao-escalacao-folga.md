# Coerência da escalação em folgas

Atualização do agente principal: esta entrega foi incorporada ao conjunto validado da principal em 08/11/2026. O relato abaixo preserva a execução isolada na branch; consulte [validação integrada e artefato combinado](../integracao-main-2026-11-08.md).

Entrega 2, 08/11/2026. Branch impl/escalacao-folga; worktree F:/Projects/Brasfoot-online-impl-folga; base f3200ada55db25b80f80fb0f48e049bc2e6e82ba. Implementada e validada localmente, sem integração à main. Origem: PN-03 e critério 1 da task-escalacao.md.

NativeClient usa uma única razão de bloqueio para Escalação, Autoescalar e Escalar Time. Fase, ausência de jogo, Pronto e editor aberto são tratados de forma consistente. Abertura direta sem condições informa o motivo. Ao abrir/fechar editor, controles são atualizados imediatamente. NativePreparation.lineupAvailability altera o acesso nativo sem restaurar callbacks locais.

A data global informa Sem partida nesta data. O painel futuro é identificado como Próximo confronto e somente consulta. Sem futuro, a mensagem é explícita. Durante apresentação sem jogo próprio, o rodapé explica que a lista mostra todos os jogos da data. Pronto continua disponível para a barreira; nenhum comando do host, calendário ou simulação foi alterado.

## Critérios e evidências

| Cenário | Execução | Resultado observado | Evidência |
| --- | --- | --- | --- |
| Folga com próximo e outro técnico jogando | NativeRestDayWindowTest: calendário nativo controlado com três clubes | Três acessos bloqueados, data/consulta explícitas, abertura direta explica motivo | analysis/validation-rest-day/rest-next.png e result.json |
| Sem próximo confronto | Snapshot sem upcoming do técnico | Mensagem explícita, topo bloqueado | rest-no-next.png |
| Data com jogo | Calendário nativo avança para segundo dia da fixture | Botões do topo e nativo liberados; cliques Robot abrem editor real | match-editable.png, saída PASS |
| Editor aberto/fechado | Abrir pelo botão nativo e pelo topo; fechar | Todos bloqueados durante editor; restaurados ao fechar | NativeRestDayWindowTest |
| Pronto/retirada | Projeção controlada do estado recebido | Três acessos bloqueiam/restauram juntos; mensagem Retire o Pronto | result.json |
| Folga durante jogos alheios | Estado de apresentação projetado na janela real | Lista preservada e escopo explicado; nenhum salto de data | rest-running.png |
| Regressão online | python tools/tests/test_native_session.py | Dois clientes conectados completaram partida com mesmo placar 1x2 e timer local parado | analysis/native-test-result.json |
| Fontes e artefato | build_adapter.py --test; package_client.py; NativeRestDayWindowTest via dist JAR | Todos aprovados; ZIP 80.102.481 bytes | analysis/validation-rest-day-jar/ |

Capturas rest-next e rest-running inspecionadas pelo agente: mensagens legíveis, acesso nativo desabilitado e próximo confronto distinguido da data atual. O teste gráfico é dirigido por snapshots de uma fixture nativa controlada, incluindo transições Pronto/retirada; não afirma reprodução do save original do Barcelona nem que essas transições foram enviadas pela rede. A regressão separada usa host e dois clientes reais. Não foram alteradas regras de autorização/persistência, portanto não foi criada uma nova certificação de retomada da carreira por esta mudança de apresentação.

Limites: automação de folgas e escalação antecipada continuam fora do recorte; critérios 2/3 da task permanecem abertos. O pacote parte da base commitada, sem alterações locais de Opções e sem a correção de treino de outra branch. Não substituir pacote principal como se contivesse todas as entregas.
