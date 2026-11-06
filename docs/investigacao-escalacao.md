# Investigação dos botões de escalação desabilitados

Data: 6 de novembro de 2026. Escopo: análise da captura enviada, rastreamento do código local e consulta aos resultados já existentes da investigação dos calendários. Não houve alteração de código, execução de nova carreira ou acesso ao save da captura.

## Conclusão

O código permite exatamente a combinação mostrada: **Escalação** e **Autoescalar** desabilitados, **Pronto** habilitado e painel exibindo um próximo adversário. Isso acontece quando o clube não tem partida na **data global atual**, mesmo que tenha um próximo jogo no calendário.

Há uma inconsistência confirmada entre apresentação e ações: o painel usa o próximo confronto como alternativa à partida atual, mas a edição aceita somente a partida atual. O botão nativo **Escalar Time** fica habilitado nesse painel e pode não fazer nada ao clicar.

A causa específica da sessão da captura ainda depende de consultar seu snapshot. A imagem não contém a data global nem `hasMatch`; portanto, ausência de partida atual é uma explicação sustentada pelo código, mas não uma observação direta do estado daquela sessão. Não há evidência de que seja um defeito exclusivo do Barcelona ou da liga espanhola.

## Caminho no código

| Etapa | Evidência | Consequência |
| --- | --- | --- |
| Partida atual | `GameAdapter.java:42`, `match(ah)`, procura o clube apenas em `state.S()` | Um jogo futuro não conta como partida atual |
| Estado enviado | `GameAdapter.java:195` publica `hasMatch = match != null` | O servidor informa se o clube joga nesta data |
| Próximo confronto | `GameAdapter.java:54`, `upcoming(ah)`, percorre o calendário a partir de `state.J()`; o snapshot publica `upcoming` em `:225` | Pode existir próximo confronto com `hasMatch=false` |
| Painel de preparação | `VisualModel.java:110`, `preparationMatch`, retorna a partida atual ou o próximo confronto; `NativeClient.java:174` usa esse resultado | O painel completo aparece mesmo numa folga do clube |
| Botões do topo | `NativeClient.java:260–268`, `accept`, exige fase `PREPARING`, técnico sem Pronto, editor fechado e `hasMatch=true` | Sem jogo atual, Escalação e Autoescalar ficam desabilitados |
| Botão do painel | `NativePreparation.java:31` habilita `HU` e encaminha para `openLineup` | Escalar Time não acompanha a regra dos botões do topo |
| Abertura do editor | `NativeClient.java:304`, `openLineup`, exige `visual.ownMatch(coachId)`; se nulo, retorna sem mensagem | Clicar em Escalar Time numa folga não abre o editor |
| Validação do host | `GameAdapter.java:66–70`, `lineupWithOptions`, rejeita ausência de partida atual com `No current match for coach` | Habilitar apenas o botão no cliente não resolve |

Além disso, `suggestion` e a elegibilidade dos jogadores usam a partida atual (`GameAdapter.java:157–175` e `:206–209`). Numa folga, não basta enviar uma escalação calculada para o confronto futuro: é necessário definir suporte a planejamento no host.

## Leitura da captura

- O próximo adversário exibido, Espanyol, não demonstra que a partida ocorre na data atual: o painel admite `upcoming`.
- O texto **Pronto**, em vez de **Retirar Pronto**, é compatível com técnico ainda não confirmado. Isso enfraquece a hipótese de bloqueio por já estar pronto, supondo que a interface esteja atualizada.
- O painel visível é compatível com `PREPARING`. Nessa fase, Pronto continua disponível sem partida própria quando não há editor aberto (`NativeClient.java:266`).
- A mensagem inferior “Selecione um técnico disponível e clique Entrar” é inadequada para uma tela já dentro da sessão e não explica o bloqueio. Não deve ser usada como indicador de que o clube joga nesta data.

## Relação com o calendário global

### Evidência adicional enviada pelo usuário

Após confirmar **Pronto**, o usuário relatou que apareceram muitos jogos, mas nenhum do Barcelona. A segunda captura mostra a janela identificada como Barcelona, data global **23/01/2022**, **2/2 prontos** e partidas de campeonatos estaduais brasileiros (Acreano, Alagoano e Amazonense, entre os trechos visíveis). A tela já está na apresentação das partidas, com controles de preparação indisponíveis.

Isso reforça a explicação de que a preparação anterior era de uma data global sem partida do Barcelona: confirmar Pronto liberou o processamento dos jogos daquela data, e não necessariamente o próximo confronto mostrado no painel do clube. A ausência do Barcelona na lista inteira é relato do usuário; a captura mostra apenas parte da lista. O snapshot anterior continua necessário para comprovar diretamente `hasMatch=false`.

Há também uma explicação para o volume de partidas exibidas: em `NativeClient.java:166–168`, quando `visual.ownMatch(coachId)` é nulo, `rebuildRound` inclui todas as partidas de `visual.matches()` na tela. Quando existe partida própria, inclui a própria e as demais de competição com nome igual. Portanto, numa folga, a apresentação amplia seu escopo para todos os jogos da data, sem uma indicação clara de que o clube não participa.

Os estaduais brasileiros numa sessão do Barcelona são compatíveis com o problema já documentado de geração de competições antes da aplicação dos países escolhidos. A captura, isoladamente, não revela quais clubes ou países foram selecionados para os dois técnicos e não comprova a configuração de criação dessa sessão.

Consequência para a correção: além de explicar a folga na preparação, a tela de partidas deve informar que o clube não joga naquela data e identificar o escopo da lista. O fluxo atual permite aguardar a conclusão e o avanço pelo host até uma data com partida própria; automatizar datas sem humanos deve preservar eventuais partidas do outro técnico e as decisões de gestão.

A [investigação dos calendários](investigacao-calendarios.md) já registrou muitas datas sem jogos humanos. O resultado local existente em `analysis/calendar-investigation/results.json` contém, para Barcelona, **152 datas simuladas**, das quais **96 sem partida humana**. Esse resultado é de outra execução; não identifica a data ou o adversário da captura.

`GameAdapter.ready` (`:145–148`) aceita Pronto sem escalação quando não há partida atual. `SessionHost.kickoffIfReady` (`:94–101`) acelera datas exclusivas da IA depois da barreira de Pronto. Isso explica por que a ação Pronto pode estar liberada com a escalação bloqueada. Se outro técnico tiver partida na data, a sessão ainda precisa processar esse jogo normalmente.

## Como confirmar na sessão afetada

Consultar o snapshot autenticado que o cliente já recebe e registrar:

1. `round.phase` e a entrada do técnico em `round.coaches` (`ready`).
2. `game.date`, a entrada do técnico em `game.coaches` (`coachId`, `clubId`, `hasMatch`) e `game.matches`.
3. A entrada correspondente em `game.upcoming`.
4. Se existe janela de escalação aberta, inclusive atrás da janela principal.

O caso principal fica confirmado com `PREPARING`, `ready=false`, `hasMatch=false`, nenhum jogo do clube em `game.matches` e próximo confronto em `game.upcoming`. Se `hasMatch=true`, investigar editor aberto e sincronização do snapshot antes de atribuir a falha ao calendário. Não registrar tokens de autenticação no relatório.

Reprodução dirigida: numa carreira descartável por IDs com Barcelona, localizar uma data sem partida do clube e com próximo confronto disponível. Verificar os dois botões do topo desabilitados, Pronto disponível e Escalar Time sem abrir o editor. Avançar pelo fluxo normal até uma data com jogo do clube e verificar que a edição passa a abrir. Esse roteiro ainda não foi executado nesta investigação.

## Correção proposta e critérios de aceite

1. **Tornar a folga explícita.** Mostrar “Sem partida nesta data” junto à data global, identificando o adversário como próximo confronto. Aplicar a mesma regra aos três acessos de escalação, com explicação visível para o bloqueio; evitar retorno silencioso de `openLineup`.
2. **Tratar datas exclusivas da IA.** Seguir o planejamento de calendários para reduzir confirmações sem decisão humana, preservando processamento, checkpoints e paradas de gestão. Uma folga de um técnico não autoriza pular a partida de outro.
3. **Decidir se haverá escalação antecipada.** Se desejada, implementar um plano persistido no host, separado da escalação da partida atual, com revalidação de lesões, suspensões, elenco e competição na data do jogo. Não trocar simplesmente `ownMatch` por `preparationMatch`: o servidor continuará rejeitando a operação atual.

Na correção, cobrir: folga com próximo confronto; data com jogo e edição liberada; técnico já pronto e retirada de Pronto; editor aberto/fechado; sessão mista em que apenas outro técnico joga; ausência de próximo confronto. Confirmar que os três acessos apresentam o mesmo estado e que nenhum botão habilitado retorna silenciosamente. Os testes atuais de interface exercitam a escalação com partida disponível; não bastam para validar esse cenário de folga.

## Limites da verificação

Esta entrega documenta um defeito de coerência da interface confirmado por inspeção do código e uma hipótese forte para a captura. Não afirma reprodução gráfica nem confirmação do snapshot da sessão afetada. Os números do calendário são evidência prévia, consultada nesta etapa; nenhum novo teste foi executado porque a alteração é apenas documental.
