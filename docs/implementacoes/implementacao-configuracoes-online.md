# Implementação das configurações online

Referência: [especificação completa](../especificacao/spec-configuracoes-online.md). Status: entrega atual resolvida por aceite do usuário; ver [registro do encerramento](../resolvidos/task-configuracoes-online.md). A cobertura das cinco etapas da especificação completa permanece parcial, conforme as pendências abaixo.

## Disponível

O snapshot autenticado publica `sessionOptions`, com as 22 chaves, revisão independente, valores efetivos, pendências, capacidades, motivos de indisponibilidade, limites da instalação, países nativos e os 21 tipos de competição de `fB.fy`. `POST /session-options` aceita `optionsRevision` e `changes` em formulário; apenas o técnico host pode alterar valores. Validação estrita rejeita chaves JSON duplicadas, tipos incorretos, paletas inválidas e IDs desconhecidos, antes de qualquer mutação.

O manifesto persiste o candidato antes da confirmação. Uma falha de armazenamento conserva o estado confirmado e interrompe o relógio. Retomadas restauram revisão e pendências; manifestos antigos recebem valores ausentes das preferências do host ou dos padrões nativos. Mapas de competição incompletos recebem os padrões faltantes. Preferências dos participantes não entram nessa precedência.

| Chaves habilitadas | Consumidor e efeito |
| --- | --- |
| `humanSpeedIndex`, `nonHumanSpeedIndex` | `best.aq.rz/rA`, sem cópia das tabelas. `PlaybackClock` agenda um minuto de `MatchPlayback` por tarefa, com cancelamento e geração sob o monitor do host |
| `openLineupAtHalfTime` | Setter nativo `VerJanelaSubs` e abertura automática em `NativeClient.accept`; alterações aguardam `NEXT_HALF_TIME`, são persistidas e ativadas uma vez. A barreira Pronto permanece |
| `soundsEnabled` | `Options.setUsaSons`, `NativeMatchPanel.muted` e `a.dV.qb`; controla também o checkbox Áudio original. Testes gráficos usam silêncio explícito |
| `humanScoreColorsEnabled`, `humanScoreColorIndex`, `highlightHomeEvents` | Setters nativos; renderização original de `b.aG` nos placares e eventos |
| `playerListColorsEnabled`, `playerListPalette` | Setters nativos e renderer original `b.aH`; cinco cores aplicadas atomicamente |
| `windowThemeIndex` | Armazenamento em `est.Options`; cores originais `best.aq.E` nas janelas abertas e futuras |
| `autoRenewExpiredContracts` | Pending changes activate before `CareerEngine.prepare` at the next contract evaluation; expired contracts renew for the native 180-day term |

A tela usa o layout original `a.fB.mJ`, com listeners online próprios. O construtor de `fB` não é executado: ele instala listeners que escrevem `options.bcf` durante a própria inicialização dos seletores. A construção do painel usa a infraestrutura de serialização do Java 8 para inicializar `JPanel`, seguida apenas do método nativo de layout. Não há chamada de `c.a.vJ`. Esse caminho depende da distribuição Java 8 já exigida pelo projeto e tem teste gráfico específico.

Desde a revisão de 7 de novembro de 2026, o acesso usa **Opções → Preferências** e **F3**, sem o botão adicional Configurações. O editor da paleta também usa o painel nativo com confirmação online. Ver [implementação do acesso nativo e validação do JAR](implementacao-opcoes-nativas-online.md).

Os participantes consultam a mesma tela, com controles, amostras, tabelas e setas sem edição. Envios em andamento bloqueiam novos comandos. Erros recarregam o estado confirmado; conflitos não repetem a escolha antiga. A atualização programática não envia comandos. Checkpoints obrigatórios são explicados na tela.

Sem `--tick-ms`, o host usa as velocidades da sessão; o cliente não impõe 1000 ms e `tools/host.py` não impõe 500 ms. Uma sobrescrita explícita de 5 a 10000 ms publica `CLI_OVERRIDE` e impede alterações de velocidade, preservando as demais capacidades. Os limites de velocidade vêm de `c.a.vL()`.

Datas sem humanos passam pelo mesmo relógio e retomam o segundo tempo automaticamente. Resultados de IA continuam sendo calculados pelo motor original uma única vez. A apresentação filtra eventos por tempo e reconstrói o placar a partir dos gols já apresentados, ocultando conclusão, disputa de pênaltis e estatísticas finais. Durante essa apresentação, `game.matches` usa os placares projetados e `visualClubs`/escalações de IA usam dados anteriores à simulação. Consultas de gestão e visão geral ficam indisponíveis até o término da data, evitando exposição de classificações e estatísticas futuras.

## Ainda sem integração

Estas chaves constam do catálogo e da persistência, mas não estão editáveis na interface nem na API; o endpoint retorna `409 OPTION_NOT_SUPPORTED`, com motivo explícito:

| Grupo | Pendência para habilitação |
| --- | --- |
| `autoSaveMode` | Integrar contador nativo de três jogos e início do ano; deduplicar salvamento adicional com checkpoints obrigatórios |
| `showCoachChanges`, `contractWarningMode`, `showPlayerAuctions` | Integrar notificações nativas e seus destinatários, preservando processamento autoritativo e privacidade |
| `showNonHumanPenalties` | Apresentar a disputa autoritativa sem recalcular seu resultado |
| `groupStateCompetitions`, `hideNonHumanNationalLeagues`, `hideStateRegionalOutsideBrazil`, `hideRegionalsWithoutHumanHistory`, `watchedLeagueCountryIds`, `nonHumanCompetitionPlayback` | Integrar a projeção de filtros com a precedência de `best.s` e `best.f`, o observador e o histórico regional. Não alterar a quantidade de jogos simulados |

O armazenamento dos países foi identificado: `b.A` lê `c.a.SR.N()`, `ex(row)` alterna `best.Z.jr()/B(boolean)`, e a identidade é o campo nativo `pais` de `best.Z`. O catálogo publica essa identidade e `jf()` como rótulo; o setter permanece bloqueado enquanto a projeção dos filtros não estiver integrada.

Não considerar esta revisão como conclusão da especificação. Ainda falta validar o percurso completo dessas doze chaves antes de empacotar uma entrega completa.

## Validação

- `python tools/tests/test_session_options.py`: catálogo, tabelas reais, validação estrita, revisão e idempotência, pendências e cancelamento, retomada, falha de persistência, autorização, convergência, sobrescrita CLI, layout nativo sem gravação local e projeção temporal de IA. Executor controlável verifica tarefas antigas sem depender de sleeps.
- `python tools/build_adapter.py --test`: bytecode original, escalações, coordenação e transferências.
- `python tools/tests/test_session.py`: sessão HTTP, dois clientes de terminal, relógio, intervalo e save final.
- `python tools/tests/test_native_session.py`: dois clientes Swing, resultados compartilhados, áudio e ausência de simulação local.
- `python tools/tests/test_career_session.py`: passou com quatro clientes Swing, duas rodadas, barreira do quarto técnico, resultados compartilhados, retomada e recuperação de rodada interrompida. A restrição indevida de 36 jogadores foi removida e o custo dos snapshots foi corrigido; ver [investigação do teste](../resolvidos/task-teste-carreira.md).


## PN-05: renovacao automatica

`autoRenewExpiredContracts` agora pode ser editada pelo host. A alteracao fica pendente ate `NEXT_CONTRACT_EVALUATION`, persiste no manifesto e ativa antes da preparacao da data seguinte. O hook autoritativo renova contratos expirados por 180 dias usando `CareerEngine.prepare`; a inicializacao deixa de forcar a opcao ligada. Os testes cobrem opcao desligada e ligada, ativacao no limite e persistencia. Avisos, leiloes, auto-save, penaltis e filtros continuam indisponiveis ate seus consumidores serem integrados.
