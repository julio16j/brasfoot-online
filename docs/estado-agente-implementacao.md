# Estado do agente de implementação

## Produto mínimo — expansão do estádio — 11/11/2026

Agente 2 acionado pelo usuário para implementar expansão do estádio e calendário/tabelas nativos. Primeira entrega: [task de expansão](resolvidos/task-expansao-estadio-online.md), [spec](especificacao/spec-expansao-estadio-online.md). Branch `impl/expansao-estadio-mvp`, worktree `F:/Projects/Brasfoot-online-impl-expansao-estadio-mvp`, base `b37ff985de5778af0aab7203c1fe21d867c0f5fd`. O código de `NativeStadium` e `CareerManagement` na principal coincidia byte a byte com a base dessa branch; dependências ignoradas locais foram copiadas separadamente. Commit da entrega `c8a283e0`, merge local na principal `23c3e355`. Corrigidos acesso ao botão nativo, título, falha de confirmação e rollback de obra/saldo/lançamento. Suíte `build_adapter.py --test`, teste dirigido de motor, Swing real e JAR empacotado da branch passaram; capturas inspecionadas em `analysis/stadium-mvp-validation/` da worktree. Após merge, build e teste de motor passaram na principal. Ver [matriz e limites](implementacoes/implementacao-expansao-estadio-mvp.md). O ZIP da principal já tinha alteração local do usuário e não foi sobrescrito; reconstrução do pacote combinado permanece pendente. Próxima frente: calendário/tabelas nativos em branch separada.

## Entrega 1 — 08/11/2026

Origem: PN-01 e alternativa de bloqueio do critério 1 de docs/tasks/sem-prioridade/task-treino-auxiliar-online.md, seguindo docs/especificacao/spec-paridade-nativa.md.
Status: implementado e validado na branch; não integrado e aguardando aceite visual.
Branch: impl/treino-auxiliar-indisponivel.
Worktree: F:/Projects/Brasfoot-online-impl-treino.
Commit-base: f3200ada55db25b80f80fb0f48e049bc2e6e82ba.
Sem commits, merge, push ou deploy nesta rodada.

## Base e contexto

Status, branches e worktrees inspecionados antes da criação. Nenhum fonte modificado da principal foi transportado; NativeTraining e dependências relevantes são iguais à base commitada. As alterações locais de Opções e o ZIP da principal foram preservados. O pacote desta branch usa a base commitada mais o bloqueio do auxiliar.

Documentos não commitados copiados como contexto: docs/tasks/sem-prioridade/task-treino-auxiliar-online.md, docs/tasks/sem-prioridade/task-paridade-nativa.md, docs/especificacao/spec-paridade-nativa.md e docs/estado-agente-requisitos.md. Os três últimos permanecem sem edição; a task recebeu apenas um parágrafo adicional. Não commitar conteúdo alheio como autoria desta entrega.
Dependências ignoradas copiadas: analysis/brasfoot.jar, analysis/lib/*.jar e tools/ecj-3.26.0.jar. Nenhum save, relatório de análise ou preferência local copiado. Recursos de jogo vieram do checkout.

## Entrega e validação

NativeTraining remove o listener nativo de MY, desabilita a caixa com explicação e preserva valor inicial. Os rádios manuais continuam enviando training-focus ao host. NativeTrainingTest testa o painel original com auxiliar ativo/inativo, ausência de callback e de mutação, explicação e envio dos dois focos. Teste incluído em tools/build_adapter.py --test.

python tools/build_adapter.py --test: aprovado (teste novo, adaptador, coordenação, elenco grande e mercado).
Teste NativeTrainingTest sobre dist/brasfoot-online.jar: aprovado. Primeira tentativa direta de Java falhou pela interpretação de -D no PowerShell; repetição com aspas passou.
git diff --check: aprovado após normalização de quebras de linha.
python tools/package_client.py: concluído com sucesso. dist/brasfoot-online.jar, dist/host-ui-agent.jar e dist/Brasfoot-Online-Teste.zip (80.098.230 bytes) atualizados apenas na worktree.

Registro detalhado: F:/Projects/Brasfoot-online-impl-treino/docs/implementacao-treino-auxiliar-online.md. Índice e mapa da worktree atualizados. Mudanças próprias: NativeTraining.java, NativeTrainingTest.java, build_adapter.py, registro de implementação, acréscimos no índice/mapa/task e pacote gerado. Documentos de contexto identificados acima permanecem sem commit.

## Limites e retomada

Task aberta; auxiliar completo e paridade não concluídos. Sem teste visual interativo do launcher. Não foi criado comando, revisão ou persistência novos. Efeito semanal, precedência do auxiliar, sincronização e retomada de sua configuração precisam de contrato e validação antes de habilitar. Próximo passo: aceite visual do recorte e levantamento do consumidor semanal para habilitação completa. Diff revisado; pacote concluído. Não aplicar à main automaticamente.


## Retomada 08/11/2026 — correção visual e entrega 2

Entrega 1: corrigido corte com pack(). Janela real medida em 429x293, painel 413x254, MY x35 y198 348x24 totalmente visível. NativeTrainingWindowTest abre o modal de produção e usa Robot para clicar auxiliar, ambos focos e fechar pelo botão nativo. Passou com capturas em F:/Projects/Brasfoot-online-impl-treino/analysis/validation-training-window/. Captura training-secondary.png inspecionada: caixa/explicação completas, controles nativos visíveis. Primeira execução capturou editor sobreposto; corrigida apenas a ativação da janela do teste com alwaysOnTop. Não conta como evidência aprovada essa primeira captura.

Entrega 2 iniciada: PN-03, critério 1 de docs/tasks/sem-prioridade/task-escalacao.md (coerência da folga), sem escalação antecipada nem automação de datas. Branch impl/escalacao-folga, worktree F:/Projects/Brasfoot-online-impl-folga, base f3200ada55db25b80f80fb0f48e049bc2e6e82ba. Nenhum fonte local da main transportado; dependências ignoradas copiadas como na entrega 1. Docs/task-escalacao.md já está na base. Estado: implementação iniciada. Critérios selecionados: explicar folga junto à data, identificar próximo confronto como consulta, uniformizar os três acessos com fase/Pronto/editor, evitar retorno silencioso e explicar escopo de jogos em folga. Testar folga com/sem próximo confronto, dia de jogo, Pronto/retirada e editor. Nenhuma alteração à autoridade do host.

Pergunta concreta enviada ao agente 1 sobre consumidor semanal e precedência do auxiliar. Ele está preparando contrato; avançar PN-03 enquanto isso, sem aguardar ocioso.

Entrega 1 reempacotada após pack: ZIP 80.102.210 bytes. NativeTrainingWindowTest passou também a partir do JAR; revisão independente do agente 1 confirmou janela e captura (docs/revisao-independente-treino.md na principal).

Entrega 3 iniciada: integração completa PN-01 conforme nova docs/especificacao/spec-treino-auxiliar-online.md. Branch impl/treino-auxiliar-online, worktree F:/Projects/Brasfoot-online-impl-auxiliar, base f3200ada55db25b80f80fb0f48e049bc2e6e82ba. Criada após executar teste gráfico PN-03; pacote e regressão de dois clientes da entrega 2 ainda em conclusão. Base de código commitada, sem alterações locais de Opções. Dependências ignoradas copiadas como nas anteriores; cinco documentos do agente 1 transportados como contexto não commitado: task-treino-auxiliar-online.md, especificacao/spec-treino-auxiliar-online.md, especificacao/spec-paridade-nativa.md, task-paridade-nativa.md e estado-agente-requisitos.md. Alterações da entrega 1 (pack e remoção de callbacks) serão incorporadas à implementação do mesmo controle, sem copiar conteúdo alheio. Escopo: comando próprio autorizado/revisionado, snapshot/projeção, painel confirmado com erro/pendência, persistência, processamento semanal nativo sem segundo timer. Nenhum commit/merge/push.

Entrega 2 concluída como checkpoint: build_adapter.py --test passou; teste gráfico NativeRestDayWindowTest passou com Robot e capturas inspecionadas em analysis/validation-rest-day (folga próxima, sem próximo, dia de jogo, editor, Pronto/retirada e escopo de jogos). Repetido sobre JAR empacotado: passou em analysis/validation-rest-day-jar. Regressão test_native_session.py com host e dois clientes passou (placar comum 1x2, timer local parado). package_client.py concluído: ZIP 80.102.481 bytes, JAR/agente na worktree. Registro e matriz completos em docs/implementacoes/implementacao-escalacao-folga.md da worktree. Critério 1 implementado/validado; critérios 2/3 (automação/planejamento) pendentes; nenhuma integração. Teste gráfico usa transições de snapshots de fixture controlada; não confundir com reprodução do save original ou envio Pronto por rede. Continuidade: entrega 3 do auxiliar já em implementação.

## Checkpoint da ordem de parada — 08/11/2026

Recebida ordem explícita: concluir somente a entrega atual do auxiliar e parar, sem novas tasks/branches. A branch impl/projecao-lesoes e worktree F:/Projects/Brasfoot-online-impl-lesoes haviam acabado de ser criadas antes da mensagem. Conferidas: base f3200ada55db25b80f80fb0f48e049bc2e6e82ba, git status limpo, nenhum contexto copiado, nenhuma implementação. Permanecem reservadas e sem uso. PN-12 e juniores ficam adiados para revisão/instrução posterior.

Entrega 3: fontes e testes completos passaram. TrainingStateTest cobre pendência/erro/projeção/novo clube; TrainingEngineTest cobre 8 combinações nativas, 4 datas normais, 2 fronteiras semanais, acumuladores e retomada separada sem replay; test_training_session.py cobre dois NativeClient reais (janelas sequenciais), comandos por Robot, valores opostos, reabertura, conflito real com restauração de controles, revisão/booleanos/token/autoridade/fase, falha de gravação e nova JVM. Capturas host-window e guest-window inspecionadas: caixa, dois focos e explicação integralmente visíveis. Um teste gráfico falhou por ativação; corrigido harness com ativação de barra de título e condição isActive, sem ampliar timeout. Repetição integral passou.

package_client.py concluído na worktree auxiliar: dist/brasfoot-online.jar, dist/host-ui-agent.jar, dist/Brasfoot-Online-Teste.zip (80.109.797 bytes). SHA256 JAR: 8b04cc6c7cd4a23148d240c61ecc2f799b933fbf4e7673fd98f83c308ca6dc3c. SHA256 ZIP: 6957e20491cfe3e8c71082405634ba068eb4673d1dcaf5bd28aa39fd1c86f550. CRC do ZIP e igualdade de JAR/agente internos com dist: aprovados. Links do registro, task e spec de treino: aprovados. git diff --check nas três entregas: aprovado, preservando linhas originais e normalizando só inserções para evitar ruído no diff.

Neste instante, test_training_session.py --packaged está executando o mesmo percurso contra JAR/agente de dist, com evidências isoladas em analysis/validation-training-session-jar. Aguardar resultado final e depois parar. Sem integração à main. Não iniciar PN-12.

Revisão independente detectou P1 antes do encerramento: com editor displayable, accept pulava projeção completa e confirmava Treino usando valores antigos. Correção no mesmo recorte: NativeClient.accept chama VisualModel.updateTraining(snapshot) nessa condição, alterando somente os dois campos de treino, sem tocar rascunho do elenco. Delta final de produção em relação à cópia intermediária transportada pelo agente principal: NativeClient.java e VisualModel.java. TrainingClientWindowTest passou a abrir editor real antes do modal, verificar ambos campos confirmados no VisualModel e identidade/slot de um rascunho local. Primeira extensão confundiu editor modeless com aviso de erro; corrigido filtro do harness para janela modal. Revalidação em andamento; hashes anteriores não representam o artefato final corrigido.

Matriz final da entrega 3 em F:/Projects/Brasfoot-online-impl-auxiliar/docs/implementacoes/implementacao-treino-auxiliar-completo.md. Cenário editor/Treino corrigido e validado: roteiro completo test_training_session.py passou com editor real e rascunho local sentinela, sem substituir jogador/slot; agente 1 confirmou por probe independente sobre build/classes. Fontes finais de produção estáveis. Novo package_client.py em andamento; repetir --packaged antes de parar. A integração combinada de fontes locais é conduzida separadamente pelo agente principal e não equivale à validação do pacote combinado por este agente.

Inventário de autoria para revisão:
- Entrega 1: src/brasfoot/NativeTraining.java, tests/brasfoot/NativeTrainingTest.java, tests/brasfoot/NativeTrainingWindowTest.java, inserção no tools/build_adapter.py; docs/implementacao-treino-auxiliar-online.md e acréscimos no índice/mapa/task; dist gerado. Contexto alheio já discriminado acima. Esta entrega de bloqueio foi superada funcionalmente pela entrega 3; não integrar sua versão de NativeTraining sobre o auxiliar completo.
- Entrega 2: src/brasfoot/NativeClient.java e NativePreparation.java, tests/brasfoot/NativeRestDayWindowTest.java, docs/implementacoes/implementacao-escalacao-folga.md, acréscimos em docs/tasks/sem-prioridade/task-escalacao.md, índice/mapa e dist gerado. Nenhum documento não commitado foi copiado da main; requisitos já presentes na base.
- Entrega 3: seis fontes (CareerManagement.java, GameAdapter.java, NativeClient.java, NativeTraining.java, SessionHost.java, VisualModel.java), três novos testes (TrainingClientWindowTest.java, TrainingEngineTest.java, TrainingStateTest.java), tools/tests/test_training_session.py, docs/implementacoes/implementacao-treino-auxiliar-completo.md, acréscimos no índice/mapa/task e dist gerado. Cinco documentos copiados do agente 1 são contexto, sem commit; só a task recebeu acréscimo próprio, identificado em seção separada.
- Todos os arquivos estão como alterações locais nas respectivas worktrees, sem commits desta rodada; branches ainda apontam à base comum. git merge isoladamente não transportaria esses diffs. Revisar/importar os arquivos e acréscimos próprios, preservando documentos/fontes alheios da main.

## Estado final — parado por solicitação do usuário, 08/11/2026

A entrega 3 está concluída como implementação local validada. python tools/tests/test_training_session.py --packaged terminou PASS após o fix do editor aberto, incluindo três janelas reais de NativeClient, Robot, configurações opostas, confirmação host/painel/projeção, identidade/slot do rascunho preservados, conflito com restauração, reabertura, reconexão/retomada em outra JVM, booleanos/autenticação/autoridade/revisão/fase e erro de armazenamento com rollback. Evidências finais: F:/Projects/Brasfoot-online-impl-auxiliar/analysis/validation-training-session-jar/result.json, logs host-window/guest-window/resumed-window e respectivas capturas. Captura final host-window/training-confirmed.png foi inspecionada com editor ao fundo e painel íntegro.

Artefato FINAL corrigido: JAR SHA256 18a9dbf8c5c182b235368f4a2cc2a22570b1e5cc3dacf42aa3ade9b41e51011d. ZIP 80.110.486 bytes, SHA256 3be4510a7d7d4f140bc8d894466067091f4357135f95604126c6208f0ffb3bd5. CRC e identidade de JAR/agente dentro do ZIP aprovados. TrainingStateTest via JAR passou. build_adapter.py --test passou após fix. O teste de motor normal/retomada e a revisão independente estão descritos acima; não repetidos sem necessidade após mudança somente de projeção com editor. Agente 1 confirmou o fix no JAR final com probe de snapshot/editor/identidade/slot e TrainingStateTest (docs/revisao-independente-auxiliar-online.md na principal).

Nenhum processo de teste desta rodada ficou ativo: scripts encerraram seus hosts/clientes no finally; desktop/Robot liberados ao agente principal. Não haverá nova execução nem nova tarefa sem retomada autorizada. Não use os hashes intermediários acima para integrar a correção final.

Branches/worktrees preservadas, todas na base f3200ada55db25b80f80fb0f48e049bc2e6e82ba, com diffs locais sem commits:
1. impl/treino-auxiliar-indisponivel — F:/Projects/Brasfoot-online-impl-treino: bloqueio/layout validados; superado funcionalmente pelo auxiliar completo.
2. impl/escalacao-folga — F:/Projects/Brasfoot-online-impl-folga: critério 1 PN-03 implementado/validado, pacote próprio; automação e planejamento antecipado pendentes.
3. impl/treino-auxiliar-online — F:/Projects/Brasfoot-online-impl-auxiliar: entrega atual concluída, pacote final validado; revisão/integracão combinadas a cargo do principal.
4. impl/projecao-lesoes — F:/Projects/Brasfoot-online-impl-lesoes: criada antes da ordem de parada, limpa, sem cópia de contexto ou implementação. PN-12 adiado.

Pendências fora do recorte: integração das entregas com alterações locais de Opções e validação do pacote combinado (agente principal), lesões/contratos/suspensões projetados, juniores nativos, áudio e demais itens do backlog. Não declarar paridade geral concluída. Este agente não fez merge/push/deploy nem alterou fontes da main; somente este relatório central foi escrito na principal. Contexto alheio versus autoria própria discriminados no inventário acima.


## Atualização pelo agente principal — integração de 08/11/2026

Agentes parados no checkpoint solicitado. Fontes das entregas de folga e auxiliar combinados com o trabalho local de Opções; pacote principal reconstruído e validação integrada aprovada. Esta atualização posterior não altera o alcance das evidências históricas acima. Ver [registro integrado](integracao-main-2026-11-08.md) para cenários, hashes e limites. Retomada dos agentes somente com nova instrução do usuário.

## Retomada autorizada — PN-12, 09/11/2026

O usuário retomou o trabalho e autorizou continuar sem parar nos checkpoints. Entrega PN-12 iniciada na branch `impl/projecao-lesoes-v2`, worktree `F:/Projects/Brasfoot-online-impl-projecao-lesoes-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Nenhum fonte de produção ou documento de implementação foi copiado da main; specs/task commitadas serviram de contexto. Dependências ignoradas copiadas: `analysis/brasfoot.jar`, `analysis/lib/*.jar` e `tools/ecj-3.26.0.jar`. Sem saves/preferências.

Implementação validada localmente: prazos exatos de lesão/contrato, contexto separado de disponibilidade, cartões ligados à identidade canônica de competição e ausência de contusão fictícia em folga. Matriz `AvailabilityProjectionTest` passou com remessa/reconstrução de snapshot, competições homônimas, suspensão contextual e bloqueio nativo por contrato. `python tools/build_adapter.py --test` passou. `NativeRestDayWindowTest` abriu janela real e capturou atleta saudável sem cruz/data absurda; primeira tentativa teve falha intermitente de clique e a repetição integral passou. `python tools/tests/test_native_session.py` passou com dois clientes. `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar` passou com o pacote JAR. JAR SHA-256 `5075bc75ca8956df950303cba386e10d9f1d164dbba967b9c1d47dfc2e4031bb`; ZIP `04857b4229d0a5211b392b5bf16c2c34ec3d5ca3f875a98181bc2bfd2843eab0`.

Registro, task e mapa atualizados na branch. Não integrado; sem commit, merge, push ou deploy. Limites restantes: carreira real com reconexão após encerramento do host e comparação gráfica da igualdade exata dos prazos. Próximo passo é concluir essa revisão, então seguir backlog autorizado em branch separada, sem incorporar alterações à main automaticamente.

## Retomada autorizada pelo usuário — 09/11/2026

As mensagens posteriores que determinaram parada/checkpoint eram instruções anteriores; o usuário retomou expressamente (“Pode continuar o trabalho, não precisa parar nos checkpoints”). Segue o backlog sem pausa entre entregas validadas. As afirmações de parada em seções históricas acima não representam o estado operacional atual. Não integrar nem fazer commit sem pedido explícito.

### PN-08 — academia nativa de juniores

Branch `impl/juniores-nativos-v2`, worktree `F:/Projects/Brasfoot-online-impl-juniores-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Projeção de `ky()` inclui campos reais usados pela tabela, identidade estável, dados médicos/contratuais e suspensão; academia nativa encaminha promoção pelo ID ao host. Dispensar, compra externa e apelido ficam bloqueados sem contrato online. `python tools/build_adapter.py --test` passou incluindo `YouthProjectionTest`; `NativeYouthWindowTest` validou academia Swing/Robot e captura; `python tools/package_client.py` gerou JAR SHA-256 `A4F1452C0E3472CD48AE79B91DFAC91E8775A0D6921A3C000A1E9AF18F84BEAB` e ZIP (80.146.017 bytes) SHA-256 `340FD3D9FD5DC1C270DBB25C7A5B441187FCBFA807380AD3F49BBC196C5158BB`. `python tools/tests/test_youth_session.py` passou com janela real, promoção autoritativa/checkpoint 1→2 e segundo cliente projetando o ID promovido (17931; host 2063, observador 2064; 7 juniores→6, 35 profissionais). Captura mais recente `F:/Projects/Brasfoot-online-impl-juniores-v2/analysis/youth-session-academy-1794236434793308800.png` inspecionada. Registro: `docs/implementacoes/implementacao-juniores-nativos-online.md` na branch.

PN-08 continua aberta: falta injeção de falha de armazenamento pelo fluxo de promoção, reinício/reconexão do host, atualização de janela já aberta, conflito com editor/Pronto e teste combinado após eventual integração. Uso direto na escalação, aposentadoria geradora de base e compra externa são capacidades separadas. Sem commit, integração, push ou deploy. `git diff --check` foi aprovado após as edições documentais.

### Próxima entrega — PN-04 áudio de eventos

Escopo escolhido por ter critérios explícitos na task/spec: baseline de status ao primeiro snapshot/reconexão, escopo de emissão conforme jogos com técnico humano e deduplicação sem perder efeitos compostos legítimos. Branch criada antes de editar fonte: `impl/audio-eventos-v2`; worktree `F:/Projects/Brasfoot-online-impl-audio-v2`; base `68874d081879f519a39b6496ac91711dcc82bde0`. Dependências locais copiadas de `analysis/brasfoot.jar`, `analysis/lib/*.jar` e `tools/ecj-3.26.0.jar`; nenhum save copiado. Trabalho em andamento; ler task/spec e validar áudio real além da contagem de chamadas, sem tratar probes muted antigos como nova reprodução audível.

PN-04 foi parcialmente implementada e validada nessa branch. `python tools/build_adapter.py --test` passou; a matriz cobre baseline histórico, transições, eventos cumulativos, nomes homônimos, identidade partida+evento, gols humano/IA, partidas mistas, opção de intervalo/áudio e composição de pênalti. `NativeUiSmoke` executou janela Swing com som real habilitado pelo `Clip`: gol/intervalo/fim; cobrança/resultado e apito final abriram seus Clips em sequência, com resultados e fim espaçados em 2 s. JAR/ZIP foram reconstruídos; `NativeAudioProjectionTest` e `NativeUiSmoke` executados com as classes de produção carregadas de `dist/brasfoot-online.jar`; `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar` e `python tools/tests/test_native_session.py` passaram com dois painéis. JAR (226.624 bytes) SHA-256 `CDE8ED381FF1ED2182079CF73EEBF9D6867A829FCCC12FF3D3AB293B72A49C50`; ZIP (80.128.511 bytes) SHA-256 `9A2C8AC99AC7F0D17AB81997CD713FE97EB8DB9406A9E8FBAC027C88978C15EE`; `testzip` sem erros. Registro e matriz em `docs/implementacoes/implementacao-audio-eventos-online.md` na branch.

PN-04 segue aberta: o dispositivo de saída aceitou os Clips e chamadas do cliente empacotado, mas não houve gravação por microfone/avaliação auditiva humana no launcher normal. Faltam confirmar casos de cartões/contusões, reconexão durante evento e som desligado no meio de cobrança, além de teste acústico e aceite do relato. Sem commit, integração, push ou deploy.

### Próxima entrega — PN-09, mutação atômica da escalação ao vivo

Selecionado recorte delimitado de `docs/tasks/sem-prioridade/task-ajustes-partida-online.md`: pré-validar elegibilidade e o pedido completo de `/live-lineup` (pares de substituição, banco e tática) antes de mutar qualquer objeto do motor; rejeição precisa manter elenco, banco, evento, contador e revisão sem efeitos parciais. Quantidade de ativos após expulsão/interface de equipe reduzida fica fora desta branch, conforme regras separadas ainda pendentes de integração gráfica. Branch criada antes de editar fonte: `impl/ajustes-partida-live-v2`; worktree `F:/Projects/Brasfoot-online-impl-ajustes-live-v2`; base `68874d081879f519a39b6496ac91711dcc82bde0`. Dependências locais copiadas: `analysis/brasfoot.jar`, `analysis/lib/*.jar`, `analysis/decompiled` e `tools/ecj-3.26.0.jar`; nenhum save copiado.

PN-09 parcial concluída localmente: `GameAdapter.liveLineup` pré-valida onze ativos, slots/goleiro, táticas, identidades, banco atual, elegibilidade, reentrada, contador e pareamento de papéis antes de aplicar a mesma lista de pares. `MatchPlayback.validateSubstitution` centraliza a validação sem mutação. `LiveLineupAtomicityTest` passou com engine/clubes nativos; rejeições verificam fingerprint de jogadores, match, eventos, contador e revisão da rodada. `python tools/build_adapter.py --test` passou. `python tools/tests/test_live_lineup_atomicity.py` passou contra host HTTP real: pedido multi-troca inválido retornou 400 sem alterar `round`, `game`, `playback` ou checkpoint; troca legal posterior foi aplicada. `python tools/package_client.py` concluído: JAR SHA-256 `2c8070ab87ee28a0d082ae30c8db3e94f03345636a9eb75c5481e3780711b442` (226.196 bytes), agente `08da1ab32e028d780724a210d01e0ef612e46dc9d261feaec45c96833a7e95a9`, ZIP (80.128.126 bytes) SHA-256 `1c736dff1b964a4460a142a83dc096025a0d8de7e8d15311c5fc2b237fb660bf`; CRC sem erros, 22.735 entradas e harness excluído. Registro `docs/implementacoes/implementacao-ajustes-partida-live.md`, task e mapa atualizados na branch. Sem commit, merge ou integração.

PN-09/task continua aberta: não cobre expulsões nem equipes reduzidas de dez/nove, interface de reposicionamento, goleiro expulso, rollback de checkpoint após sucesso do motor, retomada/editor aberto, alterações clínicas concorrentes, teste gráfico no JAR nem aceite humano. O teste HTTP verifica rejeição pré-mutação e caminho válido, não falha de persistência posterior. Branch pronta para revisão, mas não deve ser aplicada à principal automaticamente.

### Próxima entrega — PN-06, papéis especiais da escalação

PN-09 parcial foi concluída e validada; prossigo para o próximo recorte de código independente. A spec identifica consumidores nativos diretos de faltas, escanteios e capitão, enquanto falso 9 atualmente não tem consumidor esportivo comprovado no JAR. Próxima branch será `impl/papeis-escalacao-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`, worktree `F:/Projects/Brasfoot-online-impl-papeis-v2`. Escopo inicial: rastrear transporte de IDs estáveis e setters/consumidores reais; habilitar apenas papéis com consumidor comprovado, mantendo falso 9 condicionado à evidência e bloqueado se não houver efeito de jogo. Registrar antes de editar fontes. Sem commit, merge ou integração automática.

## Atualização de continuidade — 09/11/2026

O status PN-06 abaixo substitui o plano anterior de entrega. Branch `impl/papeis-escalacao-v2`, worktree `F:/Projects/Brasfoot-online-impl-papeis-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Faltas, capitão e escanteios habilitados durante preparação com IDs estáveis; host valida clube, identidade, elegibilidade e revisão atomicamente, snapshot publica IDs e `VisualModel` reconstrói referências. Falso 9 permanece bloqueado; papéis não podem ser editados durante a partida.

`python tools/build_adapter.py --test` passou incluindo teste de domínio e janela Swing original, com atleta de índice alto e símbolos inspecionados em `analysis/special-roles-lineup.png`. `python tools/tests/test_special_roles.py` passou no host HTTP; inválido retorna 400 sem mudança. Repetido contra JAR, revisão obsoleta retorna 409, persistência/retomada em outra JVM preserva papéis; `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar` passou com host e clientes gráficos. ZIP com CRC íntegro, 22.735 entradas, sem harnesses; JAR interno confere.

JAR SHA-256 `d527011dd4a79a25bc5ced051472d8bfa6e51320ef1efaf648aadf3c94f321ab` (226.720 bytes); agente `8653e09aca40c75c357a9a17e55d56fffd71067c96a3293904325a2dd972a91b`; ZIP (80.128.635 bytes) SHA-256 `3a9ec50ab1f8ffd063d03ddeeea1c7cee34ebb4309743d2fb5b281bc696ec208`. Registro, spec, mapa e índice atualizados na branch.

PN-06 segue parcial: consumidores de faltas/escanteios confirmados no JAR por leitura, não por evento dinâmico forçado; efeito do capitão/falso 9, transferência, aceitação de dois técnicos e retomada gráfica seguem abertos. Sem commits, merge ou integração.

### Próxima entrega — PN-02, calendário e seleção de países

O recorte P1 disponível é criação da carreira por IDs/países e preservação das partidas de outros técnicos ao planejar datas. Próxima branch planejada: `impl/calendario-paises-v2`, worktree `F:/Projects/Brasfoot-online-impl-calendario-paises-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Revisar a task de calendários e registrar escopo exato antes de editar fontes. Sem commit, merge ou integração automática.

### PN-02 — escopo registrado antes da implementação

A próxima branch fica limitada à etapa 1 da task de calendários: resolver clubes por IDs ou nomes locais antes da geração de competições, habilitar os países escolhidos antes de `best.p.dg()` e gerar o mundo uma vez, sem remover os clubes/participantes estrangeiros registrados pelo motor. A verificação será Brasil único, Espanha única, Alemanha única, Brasil+Espanha e CLI por nome estrangeiro, comparando países habilitados/ligas existentes e calendários por técnico. Datas automáticas, navegação, consultas, mudança de temporada e migração de saves ficam fora. Branch `impl/calendario-paises-v2`, worktree `F:/Projects/Brasfoot-online-impl-calendario-paises-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Criar e registrar antes de alterar fontes. Sem commit/merge.


## PN-02 ? pa?ses selecionados antes da gera??o das ligas ? 09/11/2026

Status: recorte implementado, validado e empacotado na branch; n?o integrado ? main. Branch `impl/calendario-paises-v2`; worktree `F:/Projects/Brasfoot-online-impl-calendario-paises-v2`; base `68874d081879f519a39b6496ac91711dcc82bde0`. O escopo foi registrado previamente neste relat?rio: sele??o antes de gerar competi??es, gera??o apenas das ligas dos pa?ses dos t?cnicos, reten??o dos clubes estrangeiros e resolu??o por nomes/assets; sem datas autom?ticas, navega??o do calend?rio, transi??o de temporada ou migra??o de saves.

`CareerEngine.createByAssets` valida IDs est?veis dos assets, inicia mundo sem competi??es, habilita os pa?ses selecionados e gera competi??es uma vez. `NativeClient` envia refer?ncias `club.jY()` em `--club-assets`; `SessionHost` usa o caminho dedicado. Nomes estrangeiros s?o aceitos com detec??o de ambiguidade/desconhecido. `--club-ids` e a API antiga continuam suportados, resolvendo assets antes da gera??o final.

Valida??o aprovada: `python tools/build_adapter.py --test` (inclui `CareerCountrySelectionTest`); `python tools/investigate_calendars.py Corinthians 'Real Madrid' 'Corinthians|Real Madrid'`; matriz adicional Alemanha/Bayern, Inglaterra/Liverpool, It?lia/Juventus, Fran?a/Paris Saint Germain e Argentina/Boca Juniors ? cada cen?rio habilitou somente o respectivo pa?s e avan?ou entre 120?129 datas; `python tools/tests/test_career_country_selection.py` e repeti??o com `--jar dist/brasfoot-online.jar`; `python tools/tests/test_career_session.py` (quatro t?cnicos, duas rodadas, barreira, resultado e recupera??o). Teste direto tamb?m cobre reten??o dos demais clubes, calend?rios dos selecionados, combina??o Brasil/Espanha, caminho legado de IDs e nome desconhecido. O teste de CLI/JAR cobre nome estrangeiro e sele??o por assets, mas n?o automatiza a intera??o gr?fica do seletor nativo.

`python tools/package_client.py` concluiu. JAR SHA-256 `086ebb87df01b2fa535f1c4ee6918b17510810f6e1d1693d25a677280ac905a1`; ZIP `dist/Brasfoot-Online-Teste.zip`, 80.131.643 bytes, SHA-256 `0f028fde4f2c04fb5185c57e190ceab8f47e8e2f9a93dfd477c5fa6b5efc40d2`. CRC ?ntegro, 22.735 entradas, nenhum harness/teste empacotado; teste de carreira passou contra o pacote.

Registro de branch: [implementa??o PN-02](F:/Projects/Brasfoot-online-impl-calendario-paises-v2/docs/implementacao-calendario-paises.md). Limites: motor/base local 22?23, sem duas temporadas estrangeiras ou save/reload internacional; caminho legado por IDs gera uma vez para descobrir IDs e outra para carreira final; folgas continuam com barreira e avan?o manual j? existentes. Atualizados task, mapa, ?ndice e backlog apenas na worktree, mais este relat?rio central na main conforme fluxo local. Nenhuma integra??o/commit/push.

Continuidade: pr?ximo recorte independente PN-07 (empr?stimos), conforme spec existente. A implementa??o deve manter PN-07 aberta enquanto consentimento humano, folha/sal?rios, retorno em origem cheia, concorr?ncia e retomada n?o estiverem cobertos; reduzir o primeiro escopo a capacidades j? especificadas e confirmar no motor, cliente, host e save.


## PN-07 ? empr?stimos online ? escopo registrado antes de alterar fontes ? 09/11/2026

Branch `impl/emprestimos-online-v2`, worktree `F:/Projects/Brasfoot-online-impl-emprestimos-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Recorte inicial da [spec de empr?stimos](especificacao/spec-emprestimos-online.md): fluxo host-autoritativo para listar como emprest?vel, propor empr?stimo a clube IA, consumir a transa??o nativa com valida??es/rejei??es, projetar contrato no estado e garantir persist?ncia/retomada. Preservar a opera??o nativa e a folha salarial; bloqueios/callbacks para negocia??o entre humanos, cancelamento, compra definitiva e retorno por prazo ficam expl?citos at? serem cobertos. O escopo ser? refinado apenas se as APIs existentes revelarem crit?rio indispens?vel sem contrato; nenhuma altera??o de fonte ainda feita.


### PN-07 ? atualiza??o de progresso

A implementa??o parcial est? em andamento na branch/worktree acima. `CareerManagement` agora aceita `action=loan` para um jogador marcado como emprest?vel de clube IA, invoca `best.l.a`, valida a cria??o do contrato nativo e devolve os dados observ?veis de origem, destino, vencimento e estado de retorno pendente. O snapshot de gest?o tamb?m lista contratos que envolvem o clube. O teste direto `CareerManagementLoanTest` cria carreira real, usa um atleta IA marcado como emprest?vel na fixture, executa o consumidor e verifica mudan?a de elenco, flag de empr?stimo, manuten??o salarial, contrato/due date e proje??o do clube tomador. `python tools/build_adapter.py --test-transfer` passou junto das tr?s regress?es de mercado existentes e do teste novo.

O limite de dez para publicar jogadores foi rastreado para a soma de atletas pr?prios marcados e empr?stimos cedidos pelo clube, conforme `ah.kr()` + `state.f(own)`. A UI de Times continua bloqueada e sem callback online; n?o declarar este fluxo integrado/validado visualmente. Tamb?m n?o h? teste HTTP de gest?o/rollback de checkpoint/retomada para a contrata??o, save/reload espec?fico ou teste de limite. A autoriza??o s? permite origem IA neste recorte; propostas/aceite entre humanos, retorno/cancelamento, processamento normal de vencimento, compra definitiva, consumidores de aviso e confirma??o visual seguem fora. PN-07 permanece parcial e aberta.

?ltimo resultado conhecido: `python tools/build_adapter.py --test-transfer` aprovado em 09/11/2026. Pr?ximo trabalho: encaminhar o bot?o nativo Empr?stimo para o comando autenticado do host, integrar listagem do pr?prio clube com confirma??o e proje??o, cobrir HTTP, persist?ncia ap?s restart e o caso de rejei??o/limites em duas sess?es; manter controles de retorno/compra bloqueados. Atualizar o pacote apenas depois desses fluxos fecharem, ent?o recalcular hashes. Nenhum commit, merge, push ou deploy.


### Integracao concluida ? PN-02 e PN-07

PN-02 integrada na `main` pelos commits `2de0c48b` e `0bf079f4`. PN-07 concluida no escopo registrado e integrada por fast-forward; implementacao `f53551c6`, merge de sincronizacao `a71a8a79`. A branch PN-07 incorporou primeiro a main atualizada.

PN-07 inclui listagem e retirada, pedido a clube IA/humano, aceite/recusa, devolucao pelo contrato nativo, projecao dos contratos e recuperacao da disponibilidade e propostas. Validacoes aprovadas: `python tools/build_adapter.py --test-transfer`, `python tools/tests/test_career_loans_http.py` (save/restart em outro processo) e `python tools/build_adapter.py --test`. Compra definitiva e avisos online continuam indisponiveis; folha salarial em datas de pagamento, retorno com origem cheia e confirmacao visual entre clientes ainda precisam de cobertura dinamica.

A branch PN-02 original ja foi incorporada antes deste ciclo; a sincronizacao PN-07 trouxe os commits dessa integracao sem sobrescrever este relatorio, que continua como alteracao local nao commitada. Proxima tarefa: PN-08, juniores nativos (P1), iniciada apos a validacao do pacote combinado.

Pacote combinado reconstruido em 09/11/2026: JAR SHA-256 `6ba6e9ae3cf3c436b8f9ad8b464058dac9b6bb8bed2e04b7d65cd228bded55a1`; ZIP 80.138.406 bytes, SHA-256 `935eb2805d73cea26d57e9bbb57b282f472bc3a6352d5be63eab89b9dddbbad9`. `build_adapter.py --test`, teste PN-07 HTTP contra JAR empacotado e teste PN-02 de selecao internacional contra o JAR passaram. O ZIP atualizado foi gerado e sera registrado junto dos documentos integrados.

PN-08 tambem foi integrada na `main` no commit `98e7f008`, apos sincronizacao com PN-02/PN-07. `build_adapter.py --test`, promocao pelo cliente empacotado, segundo cliente e retomada do host em novo processo passaram. A evidencia inclui 7 juniores antes, 6 depois, 35 profissionais; ID promovido `17931` persistiu no elenco e nao voltou a academia. `NativeYouthWindowTest` isolado capturou tabela/controles, mas nao encontrou o dialogo de confirmacao Robot; isso consta como limite do harness. Pacote final PN-02/07/08: JAR SHA-256 `3ac92c4f75de2ebb2fadfdc88e8eb48651dc42650d99d0e8aa4f9822f9e1aa51`; ZIP 80.157.343 bytes, SHA-256 `79697fe3315e09b50cbf812dd773403d2dc49eb86d485589feee72e51853b4e4`. Restam coberturas PN-08 de falha HTTP de armazenamento, janela aberta e conflito com Pronto/editor; nao declare a tarefa integralmente fechada.


### PN-08 ? fechamento do recorte da academia, 09/11/2026

A implementacao e a validacao do problema especifico da projecao/promocao da academia foram concluidas e integradas na `main` (commit `98e7f008` como base, com complemento local em andamento). A janela aberta agora atualiza apos snapshot e a identidade selecionada e preservada quando ainda existe. Promocao durante editor aberto preserva o rascunho ate fechar; `Pronto` bloqueia alteracao concorrente. Falha de armazenamento HTTP retorna 503, desfaz a promocao em memoria e mantem o manifesto anterior. Registro task arquivado em `resolvidos/task-juniores-nativos-online.md`; uso de junior diretamente na escalacao permanece outro escopo.

Validacoes deste fechamento: `python tools/build_adapter.py --test`; `python tools/tests/test_youth_transactions.py`; `NativeYouthWindowTest`; `python tools/tests/test_youth_session.py`; `python tools/tests/test_career_loans_http.py --packaged`; `python tools/tests/test_career_country_selection.py --jar dist/brasfoot-online.jar`. Pacote: JAR SHA-256 `bcf94b7939651ba8854d98ad961fe26f36ff67c66d33ad6728c38d62eacf17cd`; ZIP 80.159.743 bytes, SHA-256 `5794efc0e8871ffe3dedd51cb048d155b585c8d35ed0f41183248cba7215e5d6`.


### PN-09 ? obra de est?dio, 09/11/2026

Avan?ado o recorte de obra de est?dio sem encerrar PN-09. Adicionados `CareerManagementStadiumTest`, `NativeStadiumWindowTest` e `tools/tests/test_stadium_transactions.py`. A carreira real cotou 500 lugares em 350.000, debitou exatamente uma vez e rejeitou obra concorrente/limite nativo sem muta??o. A janela original `a.cl`/`a.cq` calculou a cota??o e enviou a sele??o de lugares pelo comando do host. O fluxo HTTP aumentou checkpoint em uma unidade, rejeitou duplica??o sem mudar saldo/projeto/checkpoint e recuperou o projeto com identidade do clube ap?s iniciar outro processo host. A matriz PN-09 foi atualizada; pendem saldo insuficiente, rollback injetado, conclus?o por avan?o normal e pacote do cliente nesta a??o.

Pacote PN-09 rebuild de 09/11/2026: JAR SHA-256 `76de994e4a947b8a90b36c2a4ca9bf6e8e1d205e02f0d954b0f97c346a416256`; ZIP 80.169.147 bytes, SHA-256 `6bcf7d2c061a6c1e92811b91e9bfaba20e7ca5d096ce154b78adbca3d8d0f477`. Transacao de est?dio validada pelo host empacotado; PN-08 (dois clientes/retomada e rollback), PN-07 e PN-02 passaram novamente no pacote final.

PN-09 avan?ou novamente: confirma??o manual de pre?os integrada com rascunho isolado, sele??o de modo, rejei??o Ready, persist?ncia e retomada. `NativeStadiumWindowTest`, `NativeStadiumPricesTest` e `test_stadium_transactions.py` passaram. Task espec?fica reaberta em `docs/tasks/sem-prioridade/task-precos-estadio-online.md` ap?s novo relato do usu?rio; matriz PN-09 continua aberta para saldo insuficiente, rollback storage, receita de jogo e demais a??es.

Pacote PN-09/08/07/02 reconstru?do em 09/11/2026: JAR SHA-256 `092b43b24a66de424f06cc09159d67fa34f5c2fbb3b31045d70404d1d785a64b`; ZIP 80.177.173 bytes, SHA-256 `9440229cb403d2d2cb19cb3bbdf2a1ae52ef349a1576ce8fb45074b509b120d9`. Testes empacotados de est?dio, academia (2 clientes/retomada/rollback), empr?stimos e sele??o internacional passaram.

Hash final apos o ciclo de validacao PN-09/08/07/02: JAR SHA-256 `428209869024435a504cc0943ac6b283a926de9f692cf914f02d171e55ee8ff3`; ZIP 80.177.171 bytes, SHA-256 `8adb19ef448bb16f6a1353b1ebf9801a25c2b0e8990e59d487e1827053acd693`. O ZIP final foi exercitado por `test_stadium_transactions.py`, `test_youth_session.py`, `test_youth_transactions.py`, `test_career_loans_http.py --packaged` e `test_career_country_selection.py --jar dist/brasfoot-online.jar`, todos aprovados.

## Retomada ativa ? 10/11/2026

Instru??o de continuidade retomada ap?s releitura do perfil atualizado. Main atual 5a791774; entregas anteriores j? integradas s?o hist?rico, n?o base antiga de 08/11. Sem novos merges/push. Pr?ximo recorte PN-11: bloquear muta??es locais de cores e adicionar/remover t?cnicos no dashboard, preservando consultas. Branch impl/bloqueio-mutacoes-dashboard; worktree F:/Projects/Brasfoot-online-impl-mutacoes-dashboard; base 5a791774. Fonte da main sem mudan?as locais; spec-consultas-historicas-online e task-paridade-nativa locais usadas como contexto de requisitos, sem incorporar altera??es alheias. Crit?rios: HT sem a??o local, item de participantes sem listener/atalho, demais consultas do popup dispon?veis; build, janela real e captura, pacote isolado e autorrevis?o. Status: investiga??o iniciada.

### PN-11 integrado; proximo recorte iniciado - 10/11/2026

3880233b integrado por fast-forward na main. Pacote principal reconstruido; teste NativeDashboardMutationWindowTest aprovado com JAR principal primeiro no classpath. Captura inspecionada em docs/evidencias/dashboard-mutacoes-2026-11-10/dashboard.png. Entrega bloqueia callbacks locais HT/participantes, preserva consultas. Sem push.

Proximo recorte da spec-calendarios-ligas-online.md: ocultar publico/renda e penaltis precomputados dos fixtures IA durante playback, preservar desconhecido distinto de zero e adaptar consumidor. Branch impl/playback-dados-pos-jogo, worktree F:/Projects/Brasfoot-online-impl-playback-dados; base main apos rebuild do dashboard (ver HEAD da worktree). Nenhuma mudanca local de fonte transportada; spec local da main usada apenas como requisitos. Criterios: valores ocultos durante apresentacao e liberados FINISHED, consumidor tolera indisponibilidade, regressao NonHumanPlaybackTest, pacote e autorrevisao.

### Playback integrado e nova investigacao - 10/11/2026

3fd70b5c integrado na main; 52194348 recompila pacote principal. NonHumanPlaybackTest e NativeHiddenAttendanceWindowTest aprovados no JAR principal. Evidencia de placeholder inspecionada; limites HTTP/multicliente documentados no registro especifico. Proximo recorte: disponibilidade e motivo da Procura publicados pelo host e usados no dashboard e abertura da janela. Branch impl/disponibilidade-procura; worktree F:/Projects/Brasfoot-online-impl-disponibilidade-procura; base 52194348. Requisitos da spec-pesquisa-jogadores-online.md e relatorio de requisitos local. Fonte base igual a main; nenhuma alteracao local alheia transportada. Escopo: playback/armazenamento/clube, snapshot, consumidor e rejeicao antes de consulta; preservar guardas do endpoint.

### Disponibilidade integrada; tamanho do snapshot - 10/11/2026

4ad0aca3 integrado em main; pacote principal reconstruido e testes NativePlayerSearchTest e NativeDashboardMutationWindowTest aprovados com JAR principal. Motivos do produtor state e guardas HTTP testados; GET state da fixture excedeu 8 MiB e nao foi contado como aprovado.

Diagnostico independente em carreira nova real, um Corinthians: state 12.375.653 bytes UTF-8; game.visualClubs 12.006.405, game.matches 108.788, playback 241.530. O limite fixo de SessionApi e 8 MiB. Proximo recorte em impl/snapshot-carreira-grande, worktree F:/Projects/Brasfoot-online-impl-snapshot-grande, base 4ad0aca3: permitir snapshot legitimo com limite explicito, testar resposta HTTP maior que 8 MiB e rejeicao acima do novo teto. Nao reduzir clubes/candidatos silenciosamente. Contexto: spec-calendarios-ligas-online e snapshot global; nenhum fonte local alheio transportado.

### Transporte integrado e proximo recorte - 10/11/2026

Branch impl/snapshot-carreira-grande commit 8925c98: testes SessionApiResponseLimitTest e NativePlayerSearchTest aprovados com JAR da branch e JAR principal. Integracao teve conflito apenas no ZIP recompilado da main; resolvido reconstruindo artefato com fontes combinados. Merge local registrado no git log. Teto de 32 MiB, GET state real agora recebido; sem push/deploy.

Proximo recorte PN-02, spec-calendarios-ligas-online.md: toda carreira nova deve gerar as seis ligas [29,97,65,104,3,72] e rejeitar clube humano fora do conjunto, mantendo mundo/nacionalidades e saves legados. Branch impl/seis-ligas-carreira, worktree F:/Projects/Brasfoot-online-impl-seis-ligas; base merge principal apos transporte (ver HEAD da worktree). Spec local da main usada como requisitos; fonte sem alteracoes locais transportadas. Investigar bootstrap do seletor e gerar fixtures por pais com um clube brasileiro, validar fronteiras por asset/nome/ID, janela e pacote antes de integrar.

### Seis ligas integradas; proxima capacidade de Agenda - 10/11/2026

2fd5a122 integrado em main. CareerCountrySelectionTest completo aprovado no JAR da branch (incluindo seis humanos, asset/nome/ID proibidos e nacionalidade independente). NativeSixCountrySelectionWindowTest e NativePlayerSearchTest aprovados no JAR principal; universo 63.258 profissionais e snapshot HTTP recebido. Pacote principal em geracao no momento de iniciar investigacao seguinte. Captura do seletor inspecionada. Demais criterios da spec permanecem abertos.

Novo recorte: publico/renda confirmados na Agenda do dashboard e Times; desconhecido ou nao positivo deve aparecer vazio. Branch impl/agenda-publico-renda, worktree F:/Projects/Brasfoot-online-impl-agenda-financeiro, base 2fd5a122. Requisitos da spec-calendarios-ligas-online.md atual da principal. Nenhuma mudanca local de fonte transportada; task/spec amplas da principal continuam preservadas. Testar DTO agendado/disputado, valores nulos/zero/positivos e janelas; preservar barreira HTTP de apresentacao.

### Agenda financeiro integrada; proximo transporte de penaltis - 10/11/2026

Branch impl/agenda-publico-renda commit 4bdbac7 integrada por merge local; registros em git log. CareerAgendaFinancialWindowTest e NativeTransferMarketAcademyWindowTest aprovados com JAR da branch; captura inspecionada. Pacote principal em geracao, verificacao integrada pendente antes de concluir recorte. ZIP da branch gerado para validacao, sem adiciona-lo ao commit de fontes para evitar conflito de recompilacao; artefato principal sera registrado depois do rebuild combinado.

Proxima branch impl/resultado-penaltis-projecao, worktree F:/Projects/Brasfoot-online-impl-penaltis-projecao, base merge atual da main apos Agenda (ver HEAD da worktree). Recorte da task-resultado-penaltis-online.md e spec-calendarios-ligas-online.md: contrato de resultado final ja decidido pelo host, penaltis e vencedor por ID, consumidor nativo e texto na Agenda; proteger dados IA antes da liberacao. Algoritmo humano de disputa e sequencia de cobrancas continuam pendencias explicitamente fora. Fonte sem mudancas locais alheias; task/spec locais da principal usadas como requisitos sem commit.

### Projecao de penaltis integrada; filtro da Agenda - 10/11/2026

Branch impl/resultado-penaltis-projecao commit aa464b7 integrada por merge local (ver git log). NativePenaltyResultWindowTest, NonHumanPlaybackTest e CareerAgendaFinancialWindowTest aprovados no JAR da branch; captura inspecionada. Fonte transporta decisao final por IDs sem alterar algoritmo esportivo. Rebuild principal em andamento; revalidacao do JAR principal pendente.

Novo recorte PN-02/PN-11: filtro por competitionKey nas agendas, com opcoes de cada janela independentes, incluindo Mostrar todos. Branch impl/agenda-filtro-competicao; worktree F:/Projects/Brasfoot-online-impl-agenda-filtro; base merge atual da main apos penaltis. Spec-calendarios-ligas-online.md local da principal usada como contexto, sem transportar fontes alheios. Publicar identidade da competicao tambem em clubAgenda e reutilizar tabela/filtro nas duas entradas. Testar competicoes com nomes iguais e keys distintas, ordenacao cronologica, independencia de janelas e entrada Times.

### Filtro integrado; frescor da Agenda iniciado - 10/11/2026

0a4016e integrado por merge local; testes de filtro Swing, campos HTTP e entrada Times/Academia aprovados no JAR da branch. ZIP da branch valido (22.735 entradas, CRC sem erro), pacote principal em geracao.

Candidato examinado: automatizar datas so IA. A spec exige nao saltar intervencoes de gestao, mas o contrato atual nao identifica no snapshot as decisoes que devem parar essa automacao; RoundCoordinator tambem trata todos os tecnicos como barreira. Nao alterar politica de pausas por inferencia. Dependencia registrada para delimitacao das intervencoes obrigatorias; continuar outro item independente.

Novo recorte: Agenda visitante aberta deve sinalizar revisao obsoleta e permitir Atualizar, preservando competitionKey valida e mantendo ultima tabela em erro. Branch impl/agenda-recarregar-snapshot; worktree F:/Projects/Brasfoot-online-impl-agenda-recarregar; base merge atual da main apos filtro (ver HEAD). Requisito spec-calendarios-ligas-online.md e queryNativeClubAgenda existente. Fonte sem alteracoes alheias transportadas. Implementar janela, observacao da revisao, recarga e casos sucesso/erro/fechamento em teste real; dashboard modal amplo permanece contrato distinto.

## Retomada em main na pasta unica - 11/11/2026

Perfil atualizado relido: trabalho direto na main em F:/Projects/Brasfoot-online. Base 23c3e355. Ha reorganizacao documental extensa, alteracoes dos perfis, relatorios, README, ZIP e ajuste local em CareerEngine (validacao da nacionalidade apos inicializacao); todos preexistentes e preservados. Recorte pendente da Agenda sera transportado da antiga worktree somente por patch de fontes/teste revisado e arquivos novos proprios. git apply --check aprovado; NativeClient tem complemento de estadio integrado desde a base antiga, preservado pelo patch. Registros novos seguem docs/implementacoes. Nenhuma nova branch/worktree sera criada.


### Fechamento do transporte e limpeza - 11/11/2026

Agenda visitante com revisao e recarga transportada para a main em `NativeAgenda`, integrada a `NativeClient`, `NativeTransferMarket` e `NativeCareerDialogs`. Testes `NativeAgendaRefreshWindowTest` e `NativeTransferMarketAcademyWindowTest` aprovados com `dist/brasfoot-online.jar` primeiro no classpath; ZIP com 22.735 entradas, CRC sem erro. JAR SHA-256 `6a87ae692c29b738a76868a75dfe033dbb3f3cb83fe59f05c989e0a7eeb25428`; ZIP 80.306.274 bytes, SHA-256 `cc79827503b37742f55cb84264a4b26f28c3d6f5cd4734a038264d6b40b9648b`. Captura anterior da interface em `docs/evidencias/agenda-recarregar-2026-11-10/stale.png`, inspecionada.

As 22 pastas auxiliares foram inventariadas e preservadas em `build/preservacao-worktrees-auxiliares-2026-11-11.zip` (133 arquivos; CRC/SHA verificados; SHA-256 `52aebca79845f3df3db7321c47fa8e43e682e07223f4d0090f1321f33ce46b50`), depois removidas de `F:/Projects`; `git worktree list` confirma somente `F:/Projects/Brasfoot-online`. Nenhuma branch foi excluida. Artefatos locais de documentacao, ajuste preexistente de `CareerEngine.java` e arquivos auxiliares da pasta principal foram preservados.

Proxima frente: `task-calendarios`, com evidencia visual de Calendario/Tabelas vazio; distinguir fixtures no host, DTO e renderizacao nativa. Em seguida continuar `task-expansao-estadio-online` ate cobrir falha HTTP/checkpoint, obra ativa/conclusao e pacote combinado.


### PN-02 retomada no calendario nativo - 11/11/2026

Iniciada a investigacao pela captura Calendario/Tabelas vazios. Confirmado no codigo que `CareerViews.clubAgenda` percorre todas as datas e fixtures no host; `VisualModel.rebuild` limpa o calendario local e cria somente a data sintetica corrente; `a.L` monta suas linhas percorrendo `c.a.SR.R()` e `a.ay_0` tambem consulta esse registro local. O endpoint `/club-agenda` sozinho nao alimenta esses consumidores. Nao implementar tela substituta; falta projetar fixtures/dados de tabela autoritativos nos controles nativos, isolando a data corrente e estado usado pelo playback. Proximo passo: provar em carreira criada que o host tem Bundesliga fixtures e tabela, rastrear inicializacao dos botoes Agenda/Tabelas nativos e definir adaptador que preserve selecao, consulta-only e modelo corrente. Nenhuma mudanca de calendario foi feita ainda.

### Calendário e Tabelas nativos implementados — 11/11/2026

No trabalho direto em `F:/Projects/Brasfoot-online` e branch main, `CareerViews.calendarCatalog`/`GET /calendar-catalog` publicam fixtures, tabelas, rodadas e clubes; `VisualModel.projectCareerCalendar` monta a projeção nativa; HR e HS abrem os componentes originais `a.L`/`a.ay`. Ajustado o índice de data no construtor `best.I` e a altura do diálogo para expor Rodadas. `NativeCareerCalendarViewsTest` passou com Corinthians: seis países, 75 datas, 53 jogos do clube em datas distintas, 18 clubes na Bundesliga, Agenda e Rodadas populadas. Capturas inspecionadas e preservadas em `docs/evidencias/calendario-tabelas-nativas-2026-11-11/`. Rebuild executado por `python tools/package_client.py`; `NativeCareerCalendarViewsTest` passou usando `dist/brasfoot-online.jar` antes das classes locais. ZIP com 22.735 entradas e CRC íntegro; JAR principal de 426.137 bytes, SHA-256 `2ac77902160261ffea8012b8ca6f07267685ce0f1853541619e7c48bc4eb8afa`; ZIP de 80.321.653 bytes, SHA-256 `cc127f789de1734fd4071c38338df938bb4bf49992fcf6dcb4f848d6629bc75a`.