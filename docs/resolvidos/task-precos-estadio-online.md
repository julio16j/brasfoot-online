# Preços do estádio: confirmação e rascunho

Status: aberta, P2 proposta, complemento PN-09. Diagnóstico estático de 08/11/2026, sem executar jogo, janela ou testes. Não altera a evidência histórica aprovada para obra de expansão.

## Fluxo nativo e adaptação

No painel `a.cl`, marcar “Usar sempre os preços sugeridos” altera `stadium.dV()` localmente e habilita/desabilita os spinners (`analysis/decompiled/a/cl_0.java:111–143`; listener `co_0`). O botão Ca, com ícone `iconpreco2.png`, chama `oD()` para preencher os valores sugeridos; o botão X/BZ chama `oF()` para aplicar os valores manuais ao estádio e fecha (`cp_0`, `cm_0`; `cl_0.java:199–200,225–228,234–247`). Ca não é o ponto nativo de confirmação manual.

`NativeStadium.open` substitui o listener de Ca para enviar `stadium-settings`; mantém o checkbox e o X com seus handlers originais. Consequências estáticas: editar preços e fechar por X aplica apenas no objeto cliente, sem comando ao host; clicar no ícone de sugestões agora envia comando, em vez de apenas preencher o rascunho como no nativo. Não há nesse adaptador contrato explícito de aplicar/cancelar valores por fechamento da janela. Não afirmar que o usuário clicou em qualquer desses caminhos.

## Snapshot concorrente ao rascunho

O checkbox escreve diretamente em `club.ev().dV()`; o envio lê esse mesmo getter, em vez de ler a seleção Cb ou um rascunho separado (`NativeStadium.java:26`). Enquanto o diálogo modal está aberto, o cliente continua recebendo snapshots: `NativeClient.accept` chama a projeção quando o editor de escalação não está aberto, e `VisualModel.updateClubs` reaplica `stadium.o(suggestedPrices)` do host (`VisualModel.java:93`). A modalidade Swing não suspende esse fluxo de atualização.

Intercalação possível por inspeção: host tem sugeridos=true; usuário desmarca; snapshot reaplica true no modelo compartilhado; usuário altera os spinners e clica Ca; o pedido carrega suggestedPrices=true, embora o checkbox visível esteja desmarcado. O host ignora os preços manuais quando true (`CareerManagement`, ramo stadium-settings). É um contracaso de ordem de eventos, sem reprodução temporal executada nesta rodada. O teste de obra não cobre esse rascunho.

O envio desabilita Ca e só o reabilita no callback de sucesso; a assinatura `NativeStadium.Submit` não recebe callback de erro. `NativeClient.command` pode mostrar erro sem reabilitar esse botão. Conferir na implementação futura os caminhos de falha e repetição; não chamar timeout ou rejeição HTTP de bug de orçamento sem diagnóstico.

## Contrato necessário e aceite futuro

Definir a confirmação e o fechamento mantendo o painel nativo: o rascunho precisa guardar modo e quatro preços independentemente do estado confirmado. O botão de sugestões deve preencher os valores, com uma ação clara para confirmar no host; se houver mudança intencional de semântica, torná-la identificável na interface. Fechar/cancelar não deve produzir aparência de gravação apenas local. Projetar o estado confirmado após sucesso; preservar ou restaurar o rascunho e liberar controles após erro.

Aceite: alterar valores e fechar por X e pela moldura; alternar sugerido/manual e receber snapshot antes de confirmar; preencher sugestões; limites por setor; erro de autorização/revisão/gravação sem controles presos; dois clientes; reconexão/save/retomada; comprovar a receita no motor com os valores confirmados. Não confundir sugeridos por competição com tabela de preços manuais persistida. O contexto do próximo confronto em folga ainda requer validação, pois `upcoming` não publica competitionType.

Relacionados: [matriz de gestão](validacao-gestao-nativa.md), [finanças](task-financas-nativas-online.md), [backlog](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.



## Resolucao do fluxo de precos ? 09/11/2026

`NativeStadium` agora mantem os quatro precos e a opcao sugerida em rascunho proprio. Ca conserva a funcao original de preencher valores sugeridos; o X confirma a mudanca pelo host, enquanto fechar pela moldura descarta o rascunho. Snapshots nao substituem os valores editados. Apos resposta de sucesso, o modelo confirmado atualiza; rejeicao HTTP libera o controle e mantem a tela aberta. `CareerManagement.stadiumSettingsRollback` restaura modo e precos se a persistencia falhar.

Validacao: `NativeStadiumWindowTest` abriu o painel nativo real, cotou expansao e enviou preco manual confirmado contra `SessionHost` HTTP; `NativeStadiumPricesTest` verificou isolamento do rascunho; `test_stadium_transactions.py` aprovou checkpoint, conflito Ready (409), persistencia e retomada para precos e obra; `CareerManagementStadiumTest` aprovou cotacao nativa, debito, rejeicao de duplicacao e limites.

Limites PN-09 restantes: saldo insuficiente e rollback por falha de armazenamento de obra/preco, mudanca concorrente em segundo cliente, confirmacao completa das sugestoes por competencia e receita de jogo apos precos manuais ainda nao foram exercitados neste recorte.
