# Filtros e agrupamento da apresentação de partidas

Status: implementacao parcial em 11/11/2026. Os seis controles alteram o recorte host-backed; testes e aceite visual ainda nao foram executados. A spec permanece aberta para equivalencia nativa, retomada e cenarios de borda.

## Separar geração, simulação e apresentação

Seleção de países para gerar a carreira (`best.O.iC/v`), país marcado para acompanhar (`best.Z.jr/B`) e recorte de jogos apresentados são estados diferentes. O seletor de ligas acompanhado não cria/remove competições do motor. Não usar o mesmo ID/flag para carregar países, filtrar uma consulta e escolher a apresentação da data.

O nativo faz escolhas no fluxo `best.f.ap` com bH, opções e tipos; ainda processa o caminho de pós-jogo quando a apresentação é dispensada. O online já calcula jogos IA e os apresenta posteriormente; implementar o filtro sobre a apresentação deve preservar placares, efeitos pós-data e a autoridade do host. Ausência da janela não significa ausência de jogo simulado.

## Consumidores e precedência identificados

- `best.s.dw/b(at)` sempre preservam apresentação quando há humano no recorte verificado; para não humanos consultam verJint pelo índice `f.ae(tipo,região)`. Tipo amistoso sem humano recebe tratamento próprio. Não filtrar um jogo humano somente porque a chave da competição está desmarcada.
- `f.ap`, tipos 1/3/10: `ignoraLigas` consulta `s.dG` (liga/fase com humano); `ignoraEstadual` consulta `s.J`, que considera os técnicos humanos de SR.M, clubes atuais e anteriores quando desempregados. `s.J` não recebe um viewer individual. Aplicar a mesma regra a um cliente específico exige adaptação explícita.
- Regionais tipo 10: `aq()` conta regionais existentes; `s.dH` relaciona estado/região e vínculos atuais/anteriores. Outra guarda usa `!ar()` e `Options.regionaisSemHumanos==0`. `ar()` verifica clube humano atual na composição regional; não foi encontrado um contador histórico durável nesse método. O rótulo da opção não autoriza inventar tal contador.
- Ligas nacionais: `s.dI` monta grupos a partir de países com humano `Z.ei()` ou marcados `Z.jr()`, quando há liga em jogo `Z.ej()`. Há fallback de país no estado global. `Z.jv()` significa presença da copa nacional na data, não país acompanhado.
- Estaduais: `s.dE` agrupa quando verEstaduaisAgrupados=true; no modo separado prioriza estados com humanos e presença na data, com fallback nativo. Agrupamento exige identidade da competição/estado/divisão, sem fundir objetos só pelo nome.
- O `NativeClient.rebuildRound` atual escolhe a competição do técnico por nome e usa todos os jogos quando ele está sem partida. Não implementa a cadeia nativa acima. Um setter em Options ou uma marca em Z não altera esse recorte por si só.

Referências: `analysis/decompiled/best/f_0.java:1382–1483,2994–3029`; classe lowercase best.s em `analysis/decompiled/best/S.java`/`s.java` conforme acesso no filesystem Windows, métodos dw/b/dE/dG/dH/J/dI; `best/Z.java:1145–1151,1219–1254`; `best/w.java:549–586`; `src/brasfoot/NativeClient.java:189–195`. Distinguir essa classe de best.S, navegador decompilado em s_0.java e desativado no host.

## Índices do catálogo de competições

`f.ae(tipo,região)` usa o seguinte mapeamento para verJint; não usar tipo diretamente como índice:

| Tipo nativo / condição | Índice |
| --- | --- |
| 2 / copa nacional | 8 |
| 3 / estadual | 9 |
| 4 / continental principal | região |
| 6 / região 0 ou 1 | 6 ou 7 |
| 12 | 14 |
| 10 / regional | 10 |
| 8 | 12 |
| 11 | 13 |
| 15 | 15 |
| 7 / região 7, demais | 11, 16 |
| 9 | 17 |
| 14 | 18 |
| 5 | 19 |
| 13 | 20 |

O método inicia índice em zero para casos não tratados, inclusive tipo 1. Preservar a composição de guardas do chamador antes de concluir que isso representa o controle internacional 0 sobre toda liga; o país acompanhado e presença humana são outros estágios. Validar combinações reais de tipo/região contra os rótulos publicados, sem preencher identidade por nome.

## Contrato multiplayer

O host edita os filtros e publica valores confirmados e IDs de país/competição. Definir se o conjunto apresentado segue todos os humanos como no nativo local ou uma adaptação por observador. Em ambos, garantir acesso aos próprios confrontos, e que ausência de um humano não bloqueie jogo de outro. Clientes não executam novamente as decisões de simulação para produzir o recorte.

Separar identidade de rodada/data, competição e partida de suas posições nas listas. Mudar filtro, agrupar/desagrupar e recarregar janela não pode repetir sons/eventos, reiniciar relógio nem reconstruir rascunho de escalação. O host pode fornecer metadados suficientes para filtrar, mas deve manter a ocultação do resultado final IA até o momento apropriado.

Decisoes desta entrega: alteracoes afetam imediatamente a apresentacao atual e todos os clientes recebem o recorte confirmado pelo host; simulacao e fixtures nao mudam. Se nenhum grupo passar nos filtros, mantem-se a partida propria; sem partida propria, exibe-se um fixture de fallback. Para regionais sem humanos, o codigo usa presenca humana atual no campeonato, nao contador de historico duravel; isso e uma adaptacao explicita. Cenarios de tecnico sem clube/ultimo vinculo e demais criterios continuam abertos.

## Aceite futuro

Dois técnicos em países/competições diferentes; data só IA; folga com jogo de outro humano; país acompanhado sem humano; estadual agrupado/separado; técnico desempregado com clube anterior; tipos/regiões do mapa acima; alteração repetida sem repetir evento; reconexão/retomada com filtros mantidos. Comparar o conjunto de partidas simuladas antes/depois e provar que o filtro altera só a apresentação prevista. Nenhum desses critérios foi executado nesta etapa.

Relacionados: [configurações](spec-configuracoes-online.md), [consultas históricas](spec-consultas-historicas-online.md), [áudio](spec-audio-eventos-online.md), [calendários](../resolvidos/task-calendarios.md) e [disponibilidade](spec-disponibilidade-jogadores-online.md).



## Entrega parcial de 11/11/2026

A interface permite editar paises acompanhados e mover competicoes entre "Ver partidas" e "Ver so resultados". As seis chaves sao validadas, persistidas e publicadas pelo host. Mudancas atualizam a lista nativa na data corrente sem reconstruir o motor. Competicoes com confronto humano sao sempre preservadas; "so resultados" omite eventos e sons de competicoes sem humanos, mantendo o resultado do host. O agrupamento estadual usa o setter nativo. O recorte de partidas nao altera `GameAdapter.currentMatches()` nem os efeitos pos-data.

Compilacao e empacotamento foram executados. Testes e validacao visual ficam para o usuario nesta rodada. Ver [registro de implementacao](../implementacoes/implementacao-filtros-apresentacao.md).
