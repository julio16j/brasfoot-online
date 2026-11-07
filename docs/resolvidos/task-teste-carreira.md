# Investigação do teste de carreira com quatro clientes

Data: 6 de novembro de 2026. Status: causas corrigidas e regressão completa aprovada.

Registro de problema resolvido. Consulte o [mapa da implementação atual](../mapa-implementacao.md) para o estado das demais funcionalidades.

## Restrição indevida do elenco

`GameAdapter.lineupWithOptions` rejeitava qualquer clube com mais de 36 jogadores. Os clubes utilizados pelo teste têm os seguintes elencos:

| Clube | Jogadores |
| --- | ---: |
| Corinthians | 34 |
| Palmeiras | 27 |
| Santos | 37 |
| São Paulo | 41 |

Assim, os clientes de Santos e São Paulo recebiam `Roster above supported 36 slots`. O teste aguardava o primeiro cliente terminar por até 60 segundos e não examinava de imediato os clientes que já haviam falhado. Os técnicos restantes aguardavam a barreira Pronto, mascarando o erro original como timeout.

O número 36 não é o tamanho máximo do elenco. Na tela original `a.bz`, `U(boolean)` consulta todos os jogadores elegíveis de `club.kc()`. As células 1–25 representam posições no campo e 26–36 representam os onze lugares do banco. A extração de `NativeLineup` já transforma a identidade do jogador em índice do elenco, independentemente do número da célula.

A correção remove apenas o limite sobre o tamanho total do elenco. Permanecem as validações de onze titulares, até onze reservas, índices válidos e distintos, elegibilidade, goleiro e posições do campo. Jogadores não selecionados permanecem no elenco, com posição `-1`.

`LargeRosterLineupTest` reproduziu a exceção antes da correção e passou depois dela: elenco com 42 jogadores, titular no índice 40, reserva no índice 41, preservação do elenco completo e rejeição de escolhas inválidas sem alteração parcial. Esse teste integra `python tools/build_adapter.py --test`.

## Lentidão depois da preparação

Depois de remover a restrição, os quatro técnicos confirmaram Pronto, mas a rodada ainda ultrapassava o timeout. O diagnóstico passou a mostrar fase, minuto, sequência e logs de todos os clientes. A primeira data contém 388 partidas e 776 clubes; o snapshot inicial tem aproximadamente 6,8 MB.

A projeção temporal de IA adicionada na implementação das configurações fazia buscas repetidas entre listas de clubes e partidas e reconstruía dados finais dos jogadores de IA para depois substituí-los pelos dados anteriores à partida. Além disso, o envio de `/state` mantinha o monitor autoritativo durante serialização e escrita na conexão, disputando o mesmo monitor usado pelo relógio. O cliente podia acumular atualizações pendentes na fila Swing.

Foram feitas as seguintes alterações:

- Clubes de IA são indexados uma vez antes da simulação. Seus dados e suas escalações anteriores à partida são reutilizados diretamente por `GameAdapter.snapshot`, preservando a ocultação de informação futura.
- A projeção dos placares percorre as partidas uma vez, usando a ordem comum dos snapshots, sem procurar cada partida nas outras listas.
- Snapshots de jogo e playback são reutilizados enquanto sequência e checkpoint permanecem iguais. Comandos persistidos, ticks e trocas de data invalidam essa reutilização.
- `/state` captura o snapshot sob o monitor e envia os dados copiados fora dele. Uma conexão lenta deixa de bloquear o avanço do motor durante a escrita.
- O cliente admite apenas uma atualização periódica pendente na fila Swing. Os snapshots continuam cumulativos, incluindo os eventos necessários para atualizar a apresentação.

O timeout dos clientes permanece em 60 segundos. O teste agora inspeciona todos os processos enquanto espera e informa imediatamente um cliente encerrado com erro.

## Resultado

`tools/tests/test_career_session.py` passou com os quatro elencos originais: preparação, editor de escalação e painel de partida nativos; duas rodadas consecutivas; barreira do quarto técnico; resultados iguais; salvamento e retomada em outra JVM; recuperação de uma rodada interrompida voltando à preparação.

Relatório local: `analysis/career-session-result.json`. Capturas: `analysis/career-preparation-1.png` e `analysis/career-game-1.png`.
