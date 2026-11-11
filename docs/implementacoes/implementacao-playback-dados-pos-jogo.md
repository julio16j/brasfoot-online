# Dados pos-jogo durante apresentacao de partidas IA

Recorte da spec-calendarios-ligas-online.md: MatchPlayback.snapshot publica attendance, attendanceBySection, revenue e penaltis como null enquanto o fixture IA de carreira esta sob a barreira de apresentacao. financialDataAvailable indica disponibilidade. Em FINISHED retorna os valores nativos. O consumidor NativeMatchPanel aceita null e mostra -- no cabecalho e na lista, sem transformar indisponibilidade em publico zero na interface.

| Cenario | Execucao | Resultado observado |
| --- | --- | --- |
| Inicio e 85 ticks IA | NonHumanPlaybackTest | Financeiros e penaltis nulos, gols/eventos temporais, estatisticas ocultas |
| Fim normal por ticks | NonHumanPlaybackTest | Valores nativos liberados, resultado precomputado preservado |
| Consumidor de null e posterior 42 | NativeHiddenAttendanceWindowTest, Swing real | Cabecalho/lista mostram --; depois ambos exibem 42 |
| Apresentacao visual | Captura Robot inspecionada | Placeholder legivel no cabecalho e estadio/lista |

[Evidencia](../evidencias/playback-dados-2026-11-10/hidden-attendance.png). Base 6d927a7a, branch impl/playback-dados-pos-jogo. Compilacao e testes aprovados nas classes; pacote reconstruido e testes do JAR registrados no relatorio central. Mudanca fica no produtor comum de playback usado por state e respostas de comandos. Nao foi certificado nesta rodada o transporte HTTP completo em dois clientes nem a matriz carreira/amistoso humano; a spec ampla permanece aberta. Autorrevisao confirma que fontes nao alteram o I do host durante a serializacao, apenas a representacao DTO; o consumidor escreve somente sua projecao local. Captura mostra truncamento nativo da frase de estatisticas na largura existente, sem impacto no placeholder de publico.
