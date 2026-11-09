# Estatísticas dos jogadores nas consultas nativas

Status: aberta, P2 proposta; complemento PN-11. Investigação estática de 08/11/2026, sem execução de jogo ou testes. Não altera as evidências históricas de placar/estatísticas coletivas do playback.

## Contrato publicado versus colunas consumidas

`GameAdapter.visualClub` publica `seasonStats` com apenas os campos fh e fi de F; `VisualModel.updateClubs` aplica somente esses dois campos (`src/brasfoot/GameAdapter.java:263`; `VisualModel.java:81`). O nome desse objeto JSON não comprova cobertura de todas as estatísticas da temporada.

O painel principal usa o modelo `b.Q` (decompilado `b/q_0.java`) e o renderer `b.aH`. Os consumidores possuem origens diferentes:

| Coluna nativa | Origem em F | Situação na projeção |
| --- | --- | --- |
| G | gA(), campo fn | Sem campo explícito correspondente no snapshot visual |
| GC | gy(), campo fh | Campo fh enviado em seasonStats |
| A | gs(), soma cD dos registros k da temporada corrente em eT | Registros eT não transportados |
| NM | gn(), média dos registros de avaliações eU com valor >=2; F() guarda cache ab | Registros eU e média autoritativa não transportados |

Referências: `analysis/decompiled/b/q_0.java:41–43`, `b/aH.java:105–135,175–190`; `best/F.java:2641–2666,2701–2713,2838–2852`. A tabela conserva as siglas da interface; não inferir que fn, fh e fi sejam intercambiáveis apenas pelo nome de um getter ou do DTO. Em particular, enviar fh/fi não preenche a origem eT/eU usada para assistências/média.

`new F()` inicia essas coleções vazias; objetos recuperados dos assets podem ter dados de origem distinta. As atualizações posteriores não reconciliam os registros omitidos com o host. O código permite concluir ausência de transporte autoritativo desses valores, sem alegar a leitura de zeros ou médias incorretas numa janela executada agora.

O detalhe selecionado reforça essa dependência: `a.eg.qt` usa `F.gw()` para jogos/gols/assistências agregados e não somente fh/fi (`analysis/decompiled/a/eg_0.java:511–540`). `gw()` soma seis contadores de todos os registros k em eT (`best/F.java:2791–2809`), enquanto `gs()` filtra a temporada. Assim, a consulta agregada e a coluna da temporada precisam de contratos distintos; não copiar o mesmo número para ambas sem respeitar o recorte.

## Requisitos e validação pendentes

Inventariar as colunas e consultas históricas separadamente: agregado da temporada, competição, clube e histórico de avaliações não são o mesmo dado. Publicar agregados autoritativos suficientes para o painel e, quando houver navegação detalhada, registros com IDs/contextos apropriados. Evitar reconstruir histórico completo a partir de gols/eventos visíveis ou usar nomes como identidade. Garantir que renderizadores e comparadores de ordenação consumam valores coerentes.

Preservar a regra de ocultação de estatísticas futuras das partidas IA durante a apresentação. O cache de visualClubs congelados tem essa finalidade; corrigir os campos faltantes não deve publicar retrospectivamente resultados finais antes do momento permitido.

Aceite futuro: atleta com gol, assistência e avaliação conhecidos; comparar cada coluna com o host, ordenar, trocar competição e temporada, transferir clube mantendo o histórico correto, jogador novo/promovido, reconexão e save/retomada; testar visibilidade antes/depois do fim da apresentação IA. Nenhum desses cenários foi executado nesta etapa.

Relacionados: [índices de jogadores](task-indices-mercado-online.md), [habilidades individuais](task-habilidades-individuais-online.md), [paridade](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
