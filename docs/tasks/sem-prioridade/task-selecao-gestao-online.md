# Seleção de jogador e intenção no menu de gestão

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: parcial; revisão estática de 10/11 retirou o alegado erro de seleção após sort: o helper compartilhado usa `JTable.getValueAt`, que converte linha visual ao modelo sob `RowSorter`. Intenção/ID e recuperação do formulário têm cobertura no fonte, mas não foram executados nesta revisão. Confirmação via janela nativa/HTTP permanece pendente.

## Menu contextual e detalhe do jogador

`NativePreparation.create` instala o popup Renovar contrato / Colocar à venda / Retirar da lista e um listener de seleção (`src/brasfoot/NativePreparation.java:45–48`). Ambos leem por `NativeTransferSearch.valueAtSelectedRow`; a tabela nativa Iy possui sorter (`analysis/decompiled/a/eg_0.java:772–773`). Revisão estática corrigida em 10/11: `JTable.getValueAt()` recebe coordenada visual e converte para o modelo sob `RowSorter`; os listeners de detalhe e menu usam o objeto `F` da coluna 0. A alegação anterior de atleta diferente após ordenar era incorreta. Preservar identidade como regressão, sem classificar como falha confirmada.

O teste fonte `NativeTransferSearchTest` ordena A/B/C em ordem inversa e espera C na linha visual 0, coerente com `JTable.getValueAt` converter a coordenada para o modelo. Esse teste não foi executado nesta revisão. Preservar a identidade após ordenação como regressão e verificar que detalhe, ação contextual, diálogo e ID enviado continuam alinhados.

Status revisto em 10/11: a alegada falha por seleção visual/model foi descartada após conferir a API de `JTable.getValueAt`; não corrigir helper por essa hipótese. Manter prova de identidade após ordenação como regressão não executada. A task continua aberta para validar o fluxo contextual de identidade e as demais ações nativas.

## Intenção perdida no diálogo de gestão

  popup encaminha texto `ação:playerId` para `NativeClient.openCareerMenu`; esse método passa a string inteira a `NativeCareerDialogs.open`. A implementação original usava apenas prefixos para escolher a operação e descartava o sufixo com ID.

Integração do ID contextual: `NativeCareerDialogs.selectRequested` separa ação e ID, pré-seleciona pelo ID e deixa seleção vazia se o atleta não estiver no snapshot.   envio após atualização/reconexão ainda requer aceite.



Colocar e Retirar são ambos mapeados para `list`; agora Colocar seleciona `sale=true` e Retirar seleciona `sale=false`. A confirmação ainda deve ser verificada em janela nativa.

## Contrato e aceite pendentes

  formulário agora recebe callback explícito de falha; a rejeição reabilita xnviar e preserva as seleções do diálogo. `NativeCareerDialogsTest` simula a falha e confirma botão habilitado e ID do rascunho preservado. Ainda falta confirmar rejeição remota e reenvio na janela nativa.

Representar operação e ID como dados separados; usar uma única conversão de linha e preservar a identidade até o formulário e o pedido ao host. Pré-selecionar o atleta pelo ID, inclusive após reordenar o elenco recebido. Se o jogador não estiver mais disponível, apresentar essa condição e atualizar a consulta, sem escolher silenciosamente outro. Definir `forSale=false` ao retirar e `true` ao colocar, usando preço pedido atual quando pertinente. Manter a confirmação explícita antes de mutação.

Aceite futuro: primeiro/segundo/último atleta; ordenação identidade, troca e ciclo de três; seleção e detalhe coerentes; menu contextual mantendo ID no formulário e no comando; retirada realmente enviada com false; alteração de elenco entre abertura e confirmação; rejeição do host sem mutar outro jogador. Nenhum desses cenários foi executado agora.

  host valida propriedade e ID em `CareerManagement.ownedPlayer`; isso impede operar atleta alheio, mas não detecta a intenção perdida quando o ID final corresponde a outro atleta válido do próprio clube. Corrigir esse fluxo não substitui a [sincronização de lista/preço](task-indices-mercado-online.md) nem a validação da transação no host.

Relacionados: [validação da gestão](../../validacao-gestao-nativa.md), [backlog](task-paridade-nativa.md) e [mapa](../../mapa-implementacao.md). Implementação e validação dinâmica pendentes.
