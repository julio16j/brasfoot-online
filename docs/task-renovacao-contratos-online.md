# Renovação manual: salário, prazo e confirmação

Status: aberta, P2 proposta, complemento PN-09. Comparação estática em 08/11/2026, sem jogo ou testes executados. Preservar a distinção entre renovação manual e renovação automática na preparação.

## O que está presente

Artefato nativo inspecionado: `dist/lib/brasfoot.jar`, SHA-256 `096c9b3d2f6b4cb1feb220ec1b6d3c6f150df1eac92a17546cc804e35d4a5d0d`. A identificação é do JAR nativo lido, não de novo pacote online gerado.

`CareerManagement` aceita durações 180/365/730/1095 e salário opcional; valida propriedade, calcula mínimo, retira da lista de venda, redefine preço pedido, estende contrato e aplica salário. As durações, vetores percentuais e efeitos c(false)/fm()/a(dias,false)/ae coincidem estruturalmente com o painel nativo `a.fy` (`analysis/decompiled/a/fy_0.java:87–130`). Isso é correspondência de código, sem certificação funcional nova.

O `fR()<0` usado no argumento de renovação online é inalcançável porque o getter trunca para zero; ainda assim, o caminho efetivo false corresponde ao argumento da renovação manual nativa. Não classificar por isso uma extensão de prazo vencido como divergência exclusiva do online. O `CareerEngine.prepare` que renova automaticamente por 180 a partir de hoje é outra regra, vinculada à opção ainda não configurável.

## Diferenças do contrato

A janela nativa permite propor salário e confirma a contraproposta quando abaixo do mínimo. `NativeCareerDialogs` envia apenas playerId/days para renew; o campo genérico de valor é usado por transferência/lista/venda, sem salário no comando de renovação. Assim, o host escolhe automaticamente o mínimo e não há proposta/contraproposta salarial nesse formulário, embora a API aceite salary. A [seleção contextual](task-selecao-gestao-online.md) também precisa ser corrigida antes de certificar a ação sobre o jogador escolhido.

Os limites percentuais não são numericamente idênticos: no nativo a expressão percentual divide inteiros por 100 antes de Math.round; no online divide por 100f e arredonda o resultado. Exemplo lógico de salário 150 e redução 1%: nativo mínimo 149; online mínimo 148. Leitura binária estática de `dist/lib/brasfoot.jar`, classe `a/fy`, método `dr(I)Z`, confirmou `imul`/`idiv`/`i2f`/`Math.round(F)I` nos offsets 70/73/74/75 e 99/102/103/104. Nenhuma JVM ou código do jogo foi carregado/executado; o parser Python leu os bytes do ZIP/class. A primeira versão do parser tinha erro próprio no avanço do cursor de interfaces e não extraiu o método; foi corrigida antes de obter essa evidência. Evitar chamar a reprodução estrutural dos vetores de equivalência exata do cálculo.

No limite superior, o fluxo nativo rE rejeita a proposta inicial >=rF (25% do valor); a contraproposta pode ser limitada a rF e aceita pelo usuário. O online aceita salário até o limite inclusivo sem essa etapa de confirmação. Definir se essa diferença de interação é adaptação aceita ou se o painel nativo deve ser integrado. O método nativo ds não é a condição de entrada usada por rE.

## Requisitos e aceite pendentes

Integrar o formulário salarial e confirmação ao host, com atleta/contrato atuais e cálculo nativo verificado. A proposta e eventual contraproposta devem ser identificáveis; campos de transferência não devem parecer salário de renovação. Confirmar prazo e salário efetivos após checkpoint; erro/revisão obsoleta não pode deixar alteração parcial nem controles presos.

Aceite futuro: as quatro durações; menos de 60 versus 60 dias; salário fracionando percentual; mínimo abaixo/igual/acima; teto, recusa e contraproposta; vencido versus ativo; retirada da lista e preço; dois clientes; save/retomada e rejeição sem mutação. Separar contrato estendido do prazo anterior de renovação a partir de hoje. Nenhum desses cenários foi executado agora.

Relacionados: [gestão](validacao-gestao-nativa.md), [configurações](especificacao/spec-configuracoes-online.md), [disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md) e [paridade](task-paridade-nativa.md). Implementação e validação dinâmica pendentes.
