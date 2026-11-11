# Implementação parcial — ranking de assistências

Estado: integrada na `main` em 10/11/2026. Relacionada à [task de estatísticas dos jogadores](../tasks/sem-prioridade/task-estatisticas-jogadores-online.md) e à [spec de consultas históricas](../especificacao/spec-consultas-historicas-online.md).

`CareerViews.snapshot` publica `assists` com os atletas profissionais ativos de `SR.O()` que têm ao menos uma assistência na temporada corrente. Usa `F.gs()` (assistências da temporada) e `F.gu()` (jogos), ordena assistências decrescentes e jogos crescentes e limita a 401 linhas, espelhando `a.y_0.mT()` e `a.z_0`. A aba Assistências da janela Carreira mostra Jogador, Clube, Assistências e Jogos. A consulta continua servida pelo overview do host, já protegido contra leitura durante a apresentação dos jogos de IA.

`CareerCountrySelectionTest` credita uma assistência através do evento nativo `F.a(8, clube, competição)` a um jogador de clube remoto ao técnico e confere sua presença e ordenação no ranking. `python tools/build_adapter.py --test` passou; o JAR/ZIP foram reconstruídos e `zip.testzip()` retornou `None` para 22.735 entradas. SHA-256 do ZIP: `17cd1bb472801b14c8e22b766221fc65409e6e03a6e496cffaa539add961e2dc`.

Limites: o ranking mostra assistências da temporada corrente e não tem filtro por país na tela online. A projeção do cliente ainda não transporta os registros `eT/eU` completos, portanto isso não fecha histórico por clube/competição, temporadas passadas, médias, avaliações nem a ficha detalhada do atleta; esses contratos permanecem na spec.
