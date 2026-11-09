# Seleção de jogador e intenção no menu de gestão

Status: aberta, P2 proposta, complemento PN-09/PN-11; desvios identificados por inspeção em 08/11/2026. Sem execução de jogo, janela ou teste nesta etapa.

## Menu contextual e detalhe do jogador

`NativePreparation.create` instala o popup Renovar contrato / Colocar à venda / Retirar da lista e um listener de seleção (`src/brasfoot/NativePreparation.java:45–48`). Ambos usam `table.getValueAt(table.convertRowIndexToModel(selected),0)`. A tabela nativa Iy possui sorter (`analysis/decompiled/a/eg_0.java:772–773`). O índice já convertido é novamente interpretado como linha visual por JTable, como no problema da [Procura](task-indices-mercado-online.md).

Exemplo lógico `[1,0]`: selecionar a primeira linha mostra o jogador do modelo 1; o listener encontra o modelo 0. Isso pode desalinhar o detalhe `original.s(F)` e o ID anexado ao menu. Não foi reproduzido graficamente nesta rodada.

## Intenção perdida no diálogo de gestão

O popup encaminha texto `ação:playerId` para `NativeClient.openCareerMenu`; esse método passa a string inteira a `NativeCareerDialogs.open`. No diálogo, `selectRequested` usa apenas prefixos para escolher a operação. O sufixo com ID não é interpretado nem usado para selecionar a combo de jogadores. `fill` mantém a seleção inicial da combo; o comando posterior extrai o ID dessa combo, não o ID recebido pelo menu.

Portanto, mesmo com a tabela sem ordenação e com o ID contextual correto, escolher Renovar no segundo atleta não pré-seleciona esse atleta no formulário: permanece o primeiro item do elenco até intervenção do usuário. Não há mutação automática ao abrir; o risco ocorre ao confirmar o formulário com a seleção padrão, que está visível. Não alegar execução de uma renovação indevida.

Colocar e Retirar são ambos mapeados para `list`. A combo `sale` é criada com `["true","false"]` e não recebe a intenção do menu; Retirar abre com `true`. A confirmação envia esse valor (`NativeCareerDialogs.java:43–62,73–76`), podendo listar novamente em vez de retirar se não houver ajuste manual. Essa é uma perda explícita de intenção, independente da conversão de linha.

## Contrato e aceite pendentes

O ciclo do formulário também tem limite de erro: Enviar é desabilitado antes de `Command.submit`, cuja assinatura só recebe callback de sucesso. O uso por NativeClient chama command sem callback de falha; o tratamento genérico mostra erro, mas não reabilita esse botão. Após uma rejeição remota, corrigir campos e reenviar na mesma janela não tem caminho explícito; fechar/reabrir continua sendo uma recuperação possível. Validar esse cenário separadamente da seleção e preservar o rascunho quando for seguro. Nenhuma rejeição de UI foi executada agora.

Representar operação e ID como dados separados; usar uma única conversão de linha e preservar a identidade até o formulário e o pedido ao host. Pré-selecionar o atleta pelo ID, inclusive após reordenar o elenco recebido. Se o jogador não estiver mais disponível, apresentar essa condição e atualizar a consulta, sem escolher silenciosamente outro. Definir `forSale=false` ao retirar e `true` ao colocar, usando preço pedido atual quando pertinente. Manter a confirmação explícita antes de mutação.

Aceite futuro: primeiro/segundo/último atleta; ordenação identidade, troca e ciclo de três; seleção e detalhe coerentes; menu contextual mantendo ID no formulário e no comando; retirada realmente enviada com false; alteração de elenco entre abertura e confirmação; rejeição do host sem mutar outro jogador. Nenhum desses cenários foi executado agora.

O host valida propriedade e ID em `CareerManagement.ownedPlayer`; isso impede operar atleta alheio, mas não detecta a intenção perdida quando o ID final corresponde a outro atleta válido do próprio clube. Corrigir esse fluxo não substitui a [sincronização de lista/preço](task-indices-mercado-online.md) nem a validação da transação no host.

Relacionados: [validação da gestão](validacao-gestao-nativa.md), [backlog](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
