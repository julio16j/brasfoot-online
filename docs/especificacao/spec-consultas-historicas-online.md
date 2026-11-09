# Consultas históricas da carreira online

Status: requisitos abertos de PN-11; consumidores rastreados estaticamente em 08/11/2026. Sem execução de jogo, janela ou teste. Não considerar toda consulta integrada por seu botão nativo estar preservado.

## Fontes nativas identificadas

| Acesso | Consumidor e origem dos dados | Lacuna atual |
| --- | --- | --- |
| HY / Retrospecto | `a.hw.sg`: histórico do clube `ah.le()` (aj por temporada/competição), nomes de técnicos por `SR.L()/al.lO()`, catálogo de país/competição | Snapshot não publica esses históricos/catálogos; projetar elenco não os reconstrói |
| História / Últimas trocas de técnicos | `eg.dp(1)` → `a.hc`, decompilado hc_0: até 100 registros de `SR.bn()` | Lista bn não transportada; datas/técnicos/clubes não derivam do elenco atual |
| Histórico de transferências | `eg.dp(0)` → mesmo painel: `SR.bo()` (ap), filtro do clube ou até 200 registros gerais | Histórico bo não transportado; gestão online contém ofertas, objeto distinto da transação histórica |
| Histórico de confrontos | `eg.rh` → `a.al`, decompilado al_0: agregados `SR.bd()` e jogos em `SR.R()`, clubes de SR.P | Cliente tem calendário de uma data e catálogo parcial; agregados bd ausentes |
| Ranking de técnicos | `eg.ro` → `a.gX`: países de SR.N, técnicos de SR.L, pontos/reputação e histórico do técnico | Projeção registra técnicos humanos e técnicos originados dos assets carregados; não representa o ranking global autoritativo |
| Campeões | `eg.rl` → `a.R`: catálogo at.a, temporadas/vencedores em at.mn | Competições sintéticas da rodada não carregam o histórico mn |
| Artilheiros | `eg.rk` → `a.O`, decompilado o_0: atletas de SR.O, contadores gy/gz e histórico de aposentados SR.bJ | Índice de atletas e históricos incompletos; ver tasks de índices/estatísticas |
| Assistências | `eg.rj` → `a.Y`, decompilado y_0: atletas de SR.O, filtro de país via SR.N, gs/gu | Catálogo/índice e registros individuais eT não representam a carreira do host |
| Troféus do técnico | `eg.rx` → best.S.e → `a.dg`: títulos al.cT e históricos de competições mn | Snapshot não transporta títulos/históricos; não reconstruir títulos pelo clube atual |
| Galeria de troféus do clube | `eg.rw` → best.S.v → `a.di`: títulos ah.cT com temporada/tipo/competição | Títulos cT não transportados pelo visualClub |
| Time da Rodada / Ano | `eg.do(0/1)` → best.S.a → `a.iu`: b_0.zN/zI, jogadores e clubes associados; zM para destaque anual | Dados de seleções/premiação e catálogos nativos não projetados |
| Bola de Ouro / quadro por posição | `eg.rg` → `a.H`, decompilado h_0: b_0.ZW/fj(0–6)/zG e catálogo país/divisão | Não equivale ao quadro de classificação nem à galeria de títulos; dados/objetos b_0 ausentes |

Referências: `analysis/decompiled/a/hw.java:142–225`, `a/hc_0.java:45–105`, `a/al_0.java:80–201`, `a/gX.java:108–158`; chamadas em `a/eg_0.java:1537–1581,1673–1684`. A nomenclatura de arquivos do decompilador distingue classes que colidem por maiúsculas no Windows; os destinos acima foram conferidos pelos tipos usados e pelo comentário Renamed.

`GameAdapter`, `VisualModel` e `CareerViews` não transportam essas coleções históricas. `/overview` oferece calendário do recorte atual e tabelas descobertas pelos confrontos; não há endpoint de históricos/ranking. O campo seasonStats do jogador tampouco substitui histórico de clube/técnico. São fatos de contrato, sem afirmar que todas as janelas foram abertas ou que todo dataset atual está vazio.

Refinamento do inventário anterior: a.H/h_0 exibe Goleiro/Lateral/Zagueiro/Meia/Volante/Atacante e Bola de Ouro, usando estatísticas de avaliação, não classificação por pontos (`a/h_0.java:70–78,160–209`; `f/b_0.java:221–249`). zL apenas ordena ZW, não sorteia um prêmio nesse método. O caminho da Galeria usa o handler sintético eg.r→rw→S.v→di, enquanto o item Bola de Ouro chama rg→H. Preservar essa distinção antes de implementar ou certificar telas por seus nomes. `di.pD` contém rotina auxiliar que adicionaria títulos, mas não foi encontrada chamada no construtor/fluxo rastreado; não tratá-la como mutação executada ao consultar a galeria.

Alguns consumidores exigem objetos nativos válidos na construção: a.H.mK desreferencia o b_0 recebido, obtido por club.kF. A projeção atual não cria o catálogo SR.N de países/ligas que kF consulta. Uma janela com handler mantido pode assim depender de dados ausentes antes de chegar ao modelo. Registrar e tratar indisponibilidade explícita; uma tabela vazia não é o único resultado possível. Não foi aberta a janela para reproduzir uma exceção nesta etapa.

## Consulta autoritativa e apresentação

Consultar o host por tipo, IDs de clube/técnico/competição, temporada e cursor/limite. Preservar recortes nativos e ordem; explicitar limite/paginação quando a rede exigir. Não carregar toda a história em cada snapshot de 250 ms. Manter nomes para apresentação e IDs para identidade: clubes/técnicos homônimos, renomeados ou já removidos precisam continuar reconhecíveis no registro histórico.

Separar transação concluída, oferta pendente, troca de técnico e agregado de confronto. Um registro ap contém data, valor e IDs de origem/destino; o jogador é referência transitória reconstituível por identidade (`best/ap.java`). Não usar o clube atual do atleta para preencher automaticamente o vendedor de uma transferência antiga. O estado do host é a origem dessas relações.

Reutilizar painéis nativos com modelos de consulta ou projeções isoladas suficientes para os consumidores, sem substituir o estado global que sustenta a edição de escalação. Uma consulta histórica não deve criar treino, renovar contrato, gerar leilão ou mover técnico. Contexto e objetos compartilhados da UI precisam de revisão própria antes de abrir um painel a partir de dados parciais.

## Visibilidade e atualização

O host deve definir quais recortes são públicos e quais pertencem ao técnico. Notificações privadas e propostas ainda não resolvidas não entram no histórico público por conveniência de serialização. Durante a apresentação de partidas IA, manter a restrição já existente para consultas que revelariam o resultado futuro; não publicar um ranking pós-data quando o placar daquela data ainda está oculto. Quando necessário, servir versão anterior identificada ou bloquear a consulta com motivo claro.

Identificar a versão/temporada da consulta. Atualização ou reabertura deve mostrar o estado confirmado; preservar seleção por ID ao ordenar/recarregar. Distinguir histórico sem registros de dados indisponíveis ou recorte ainda não carregado, sem exibir informação estática do asset como se fosse da sessão.

## Ações de gestão não são consultas

O popup de técnicos também possui `eg.rn` → `a.ip`, com adicionar técnico e aposentar/remover desempregado (`a/ip_0.java:66–106`). Esses handlers mudam a lista local de técnicos; não são cobertura de leitura. `RoundCoordinator` e tokens da sessão possuem participantes definidos na criação, sem protocolo para essas mudanças nativas posteriores.

Enquanto não houver contrato de participação dinâmica, bloquear explicitamente esses callbacks mutáveis nos popups preservados. Para integração futura, definir autoridade, registro/retirada de participante, token, vínculo ao clube, barreiras e save/retomada; não apenas sincronizar a lista M. Acessibilidade gráfica dos itens e efeito completo ainda não foram executados nesta etapa.

## Aceite futuro

Comparar cada consulta com a fonte nativa no host, incluindo duas temporadas, técnico que trocou de clube, transferência seguida de outra venda e confronto entre clubes fora da rodada. Conferir catálogo/filtros/ordem/limite, dados ausentes, reconexão e mudança de versão da consulta; dois clientes devem ver os mesmos registros autorizados. Nenhuma consulta pode modificar o motor ou revelar resultado IA antecipado. Testar bloqueio das ações de gestão ainda sem suporte.

Relacionados: [estatísticas dos jogadores](../task-estatisticas-jogadores-online.md), [empregos](../task-empregos-tecnicos-online.md), [calendários](../task-calendarios.md), [avisos](spec-notificacoes-leiloes-online.md) e [backlog](../task-paridade-nativa.md). A matriz é um inventário de fontes; não certifica efeito ou cobertura dinâmica das galerias, prêmios e rankings.
