# Integração do auxiliar de treino

Atualização do agente principal: esta entrega foi incorporada ao conjunto validado da principal em 08/11/2026. O relato abaixo preserva a execução isolada na branch; consulte [validação integrada e artefato combinado](integracao-main-2026-11-08.md).

Entrega 3 — 08/11/2026. Branch impl/treino-auxiliar-online; worktree F:/Projects/Brasfoot-online-impl-auxiliar; base f3200ada55db25b80f80fb0f48e049bc2e6e82ba. Não integrada à main. Requisitos: [spec](especificacao/spec-treino-auxiliar-online.md), [task](task-treino-auxiliar-online.md). Esta entrega substitui o bloqueio provisório somente nesta branch.

CareerManagement aceita training-assistant com enabled booleano estrito, separado de training-focus, sempre para o clube do técnico autenticado. SessionHost usa fase/revisão/Pronto da gestão e checkpoint Kryo existente. Falha ao gravar restaura os dois campos de treino e invalida cache; host permanece pausado por falha de armazenamento. A revisão de gestão pode ter avançado em memória antes da falha; não é confirmada como durável. O manifesto anterior continua sendo a referência de retomada.

GameAdapter publica trainingAssistant ao lado de trainingFocus; VisualModel projeta ambos. NativeTraining é um diálogo nativo com callbacks locais removidos: envia intenção, desabilita os três controles enquanto pendente e mostra valores confirmados. Snapshots não disparam comandos. Erro/conflito busca estado atual, restaura os controles e mostra erro. Troca de clube fecha a janela; reabertura consulta o clube atual. pack respeita painel e explicação da precedência. Não foi adicionado timer semanal ou chamada de aT pelo comando.

## Matriz de validação executada

| Critério | Execução | Esperado / observado | Evidência |
| --- | --- | --- | --- |
| Dois técnicos e autoridade | test_training_session.py e TrainingClientWindowTest | Host falso/foco1, participante verdadeiro/foco0; cada um só altera próprio clube; outro token vê combinações | analysis/validation-training-session/result.json |
| Janela nativa e layout | Robot em NativeClient real, com host HTTP | Caixa, rádios, explicação completos; cliques aguardam confirmação; reabertura preserva valores | host-window/training-confirmed.png e guest-window/training-confirmed.png, inspecionadas |
| Erro real e revisão | Harness força revisão obsoleta no instante da intenção | Host rejeita; cliente busca estado, restaura seleção e interação; aviso aparece | Logs TrainingClientWindowTest com conflict restoration |
| Pendência e projeção | TrainingStateTest | Sem setter local/duplo envio; atualização não encerra pendência; callback de erro reabilita | Saída PASS |
| Valores, autenticação e fase | API: bool inválido/ausente, token inválido, revisão antiga, Pronto, RUNNING | 400/401/409; sem alteração do checkpoint nas rejeições de validação | result.json e script |
| Persistência/reconexão | Reinício do host em JVM distinta e cliente novo | Mesma combinação dos dois técnicos | resumed-window/training-confirmed.png e log |
| Erro de armazenamento | Manifesto descartável substituído por diretório apenas no teste | 503, trainingAssistant anterior preservado, host pausado; nova JVM retoma manifesto anterior | result.json, host-errors-*.log |
| Precedência nativa | TrainingEngineTest, JAR nativo, 8 combinações auxiliar/foco/ciclo | Ramo normal preserva auxiliar; ciclo3 força secundário | Saída PASS |
| Fronteira semanal real | Quatro datas de carreira por MatchPlayback e CareerEngine.nextDate | Duas filas semanais consumidas, ciclo calculado conforme aj pendentes e ganhos internos mudaram | analysis/validation-training-engine/weekly.s22 e weekly-cycle.txt |
| Sem processamento duplicado | tick em partida concluída e retomada do checkpoint em outra JVM | Ciclo e acumuladores não repetidos | TrainingEngineTest modo de retomada: PASS |
| Troca de clube/fase | TrainingStateTest com novo clube | Janela antiga fecha; fase bloqueia controles sem comando | Saída PASS |
| Regressão | build_adapter.py --test | Adaptador, coordenação, elenco e mercado passaram | Saídas PASS |

O teste gráfico teve uma falha de ativação de janela: clique inicial não chegou ao controle e a espera por confirmação expirou. O harness passou a ativar explicitamente a barra de título da própria janela e verificar isActive antes de clicar; repetição completa passou. Não houve ampliação de timeout nem alteração de produção para esconder a falha. Capturas de falha são diagnósticas e não evidência de aprovação.

## Pacote e limites

`python tools/package_client.py` concluído após a correção com editor aberto. Artefatos somente nesta worktree: `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip` (80.110.486 bytes). JAR SHA256 `18a9dbf8c5c182b235368f4a2cc2a22570b1e5cc3dacf42aa3ade9b41e51011d`; ZIP SHA256 `3be4510a7d7d4f140bc8d894466067091f4357135f95604126c6208f0ffb3bd5`. `TrainingStateTest` passou sobre esse JAR. Repetição final `python tools/tests/test_training_session.py --packaged` concluída: PASS em todos os cenários, incluindo editor aberto/rascunho, conflito, duas configurações opostas, reabertura, nova JVM, validação/rejeições e falha de armazenamento. Evidências em `analysis/validation-training-session-jar/result.json` e capturas correspondentes; host-window/training-confirmed.png inspecionada após a correção. Não inclui mudanças locais de Opções nem PN-03 de outra branch. Sem commit/merge/push/deploy.

Os testes cobrem processamento semanal em carreira descartável e uso de estados opostos por dois clientes reais em sequência. Não certificam todas as idades, limites de habilidade ou todas as transições de emprego reais: a troca de clube foi exercitada no ciclo do diálogo, preservando o contrato de gestão já existente. O ganho usa regras nativas e pode não produzir aumento inteiro em cada semana. Não encerrar a task de paridade geral por este recorte.

## Correção da revisão concorrente com editor

Antes de encerrar a entrega, o agente de requisitos reproduziu snapshot confirmado que não era projetado no Treino enquanto o editor de escalação permanecia aberto. Corrigido NativeClient.accept: nessa condição usa VisualModel.updateTraining, que atualiza somente os dois valores de treino em clubes já existentes e preserva objetos e posições do rascunho. Não houve nova task ou ampliação para lesões/juniores.

TrainingClientWindowTest agora abre editor real antes do modal e verifica: configuração confirmada no host, painel e VisualModel; mesmo jogador/slot do rascunho após confirmação; conflito real sem fechar editor; reabertura e retomada. A primeira adaptação do teste tratou o editor modeless como aviso de erro; o filtro foi corrigido para exigir aviso modal, sem alterar timeout. Este é erro do harness e não confirmação de falha adicional no produto. Os três clientes/janelas do roteiro já passaram nesse cenário; as demais verificações API e o novo empacotamento também passaram, conforme os hashes finais acima. O pacote anterior não deve ser integrado como versão corrigida.
