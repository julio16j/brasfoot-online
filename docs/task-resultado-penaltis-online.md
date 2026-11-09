# Resultado de disputa de pênaltis na projeção

Status: aberta, P2 proposta; complemento PN-05/PN-11. Divergência de contrato identificada por inspeção em 08/11/2026, sem jogo, janela ou testes executados.

## Estado autoritativo e consumo

`MatchPlayback.tick` decide no host os pênaltis de determinadas partidas humanas empatadas, grava `I.i(int[])` e `I.u(vencedor)` antes de finalizar a data. O snapshot publica `homePenalties` e `awayPenalties` (`src/brasfoot/MatchPlayback.java:85–91,143`). Para partidas IA ocultadas durante playback, os valores são zero até o momento permitido.

`NativeMatchPanel.project` só tenta gravar o placar de pênaltis se existir a chave `penalties`, que não é publicada pelo snapshot atual (`NativeMatchPanel.java:72`). Nenhum outro consumidor em `src/brasfoot` reconcilia homePenalties/awayPenalties em `I.hQ()`; o vencedor `I.hR()` também não é transportado explicitamente. O destaque Hn é preenchido apenas com homeGoals/awayGoals. Portanto, o resultado de disputa publicado não chega ao objeto nativo por esse contrato, mesmo quando o host já o decidiu. Não afirmar captura de janela ou eliminação incorreta no motor.

O nativo distingue placar de jogo, placar da disputa e vencedor em campos próprios (`analysis/decompiled/best/I.java:1436–1453`). Renderizadores de resultados usam `hQ()` (`b/ac_0.java`, `b/an_0.java`); apenas atualizar gols normais não equivale a transportar a disputa. `CareerViews` também monta resultado textual só com hu/hw, sem complemento de pênaltis.

O teste existente `CareerProbe` verifica coerência entre hR e hQ no motor; não testa o campo consumido pelo painel ou o calendário exibido. `AdapterTest.resolvePenalty` verifica uma cobrança durante o jogo, outra situação. A opção bloqueada `showNonHumanPenalties` trata da apresentação de disputas sem humanos; não resolve por si só essa falha de projeção do resultado já decidido.

## Requisitos e aceite pendentes

### Algoritmo adaptado e capacidade nativa humana

A continuação estática encontrou a origem do sorteio rápido atual: ele reproduz o ramo de partidas somente IA de `best.I` (`analysis/decompiled/best/I.java:1107–1119`), com duas amostras 2–8 e vitória mandante quando a primeira é maior ou igual. `MatchPlayback.tick` aplica essa forma às partidas humanas em liveMatches. A condição de empate entre amostras favorece o mandante; com amostras uniformes independentes, são 28 de 49 combinações (4/7), uma propriedade aritmética da fórmula, não frequência medida em execução.

O caminho nativo de disputa apresentada usa `a.bg`: cada cobrança tem conversão de 70%, atualiza a sequência e encerra pelas condições da disputa; `nO` grava placar e vencedor (`a/bg_0.java:164–224`). `a.dV.qd` inclui confrontos humanos na fila e inclui IA conforme a opção (`a/dV.java:510–523`). Portanto, o código online não preserva esse processo de cobranças humanas: usa a resolução rápida de IA sem transportar a sequência. Isso é diferença funcional rastreada, separada da chave de JSON incompatível. Não foi certificado o fluxo visual nem medido efeito em campeonatos reais.

Definir se a adaptação rápida é requisito aceito ou se a disputa deve seguir as regras da sequência nativa no host. No segundo caso, transportar cobranças/estado/vencedor e apresentar a sequência sem permitir que o diálogo cliente sorteie novamente. A recuperação deve distinguir disputa em andamento, decisão persistida e apresentação já consumida. Não habilitar apenas a opção de exibição para anunciar paridade esportiva.

Definir um contrato único para placar normal, existência/estado da disputa, placar da disputa e vencedor por ID de clube. Aplicá-lo ao objeto de apresentação e às consultas de resultados, preservando a identidade da partida. Não inferir que houve disputa apenas por um empate ou por zeros; não modificar os gols normais para somar cobranças. O cliente apresenta a decisão do host, sem sorteio adicional.

Aceite futuro: disputa com humano, disputa só IA após liberação do resultado, empate sem disputa, jogo decidido no tempo normal, reconexão após fim e retomada; painel, calendário e vencedor coerentes com o host. Não revelar antecipadamente o resultado final da IA. O algoritmo de decisão esportiva hoje adaptado no host exige análise própria de paridade; esta task trata do transporte/apresentação e não certifica equivalência desse algoritmo ao nativo.

Relacionados: [configurações](especificacao/spec-configuracoes-online.md), [áudio](especificacao/spec-audio-eventos-online.md), [paridade](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
