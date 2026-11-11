# Transportar a decisao de penaltis do host

Recorte da task-resultado-penaltis-online.md e spec-calendarios-ligas-online.md. Base c158935a, branch impl/resultado-penaltis-projecao. MatchPlayback publica penaltiesAvailable, homePenalties, awayPenalties e winnerClubId; placares negativos/partida nao disputada e fixtures IA ainda ocultos usam null/false. O consumidor NativeMatchPanel usa esses mesmos campos, restaura I.hQ/hR pelo ID do clube e limpa uma decisao anterior quando os dados passam a indisponiveis. O placar destacado mostra gols normais e penaltis em linhas separadas. CareerViews publica o mesmo contrato nas duas agendas, inclui a disputa no resultado e usa x para partidas agendadas.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Resultado final controlado 1 x 1, disputa 5 x 4 | NativePenaltyResultWindowTest | Snapshot conserva disputa/vencedor e projecao nativa recebe I.hQ/hR |
| Gols normais e agenda | Mesmo teste | Gols continuam 1 x 1, agenda 1 (5) x 1 (4) |
| Dado indisponivel apos resultado | Mesmo teste | Placar de disputa e vencedor anteriores limpos |
| Agendado e sem disputa | Mesmo teste | Sem inferir 0 x 0, valores nulos e indicador false |
| IA precomputada em ticks normais | NonHumanPlaybackTest | Disputa/vencedor ocultos antes do fim |
| UI real | Captura Robot inspecionada | 1 x 1 e Pen.: 5 x 4 legiveis em duas linhas |

[Evidencia](../evidencias/resultado-penaltis-2026-11-10/result.png). Testes acima e CareerAgendaFinancialWindowTest passaram com JAR da branch, alem de compilacao. Fixture de disputa final controlada testa contrato/apresentacao, nao equivalencia do algoritmo esportivo nem sequencia de cobrancas. Recorte nao altera a decisao rapida hoje usada pelo host; sequencia humana nativa, retomada do host em outro processo e apresentacao em dois clientes continuam pendentes. A tabela compacta de jogos continua com gols normais; detalhe destacado e Agenda apresentam a disputa. Autorrevisao: nao se somam penaltis aos gols, vencedor so referencia os clubes do fixture, desconhecido separado de zero e nenhuma mutacao de host ocorre na serializacao. Pacote da branch gerado, artefato combinado da principal sera reconstruido apos integrar. Documentos compartilhados com edicoes alheias preservados.
