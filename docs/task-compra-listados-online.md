# Compra de listado ignora recusa nativa do jogador

Status: aberta, P2 proposta, complemento de PN-09; desvio de paridade confirmado por inspeção em 08/11/2026. Sem execução do jogo, testes ou nova carreira, conforme restrição atual. Não afirmar ocorrência observada em uma transferência do usuário.

## Comparação de caminhos

A compra direta nativa de atleta listado (`analysis/decompiled/best/l.java:552–572`, método `b(F,ah)`) verifica: jogador listado, outro clube, saldo, vagas e **aceitação do jogador** por `l.d(F,ah)`. Se essa última regra falha, retorna código 4, sem chamar a transação `F.a`.

No online, `CareerManagement.execute` chama `validateTransfer` e, para vendedor IA com `nativeListedPurchase(...)` verdadeiro, transfere imediatamente pelo preço pedido (`src/brasfoot/CareerManagement.java:47–55`). O helper verifica apenas `listed && amount >= asking` (`:135`). `validateTransfer` cobre propriedade/lista do vendedor, empréstimo ativo, elenco/goleiro do vendedor, vagas e saldo; não chama `best.l.d`. `transfer` chama diretamente `player.a(...)` e ajusta saldos, sem decisão de aceitação (`:141–155`).

O ramo de proposta negociada usa outra regra, `best.l.a`, com recusa e contraproposta, mas não é percorrido nessa compra direta. Aceitação do vendedor IA/preço alcançado e aceitação do atleta são condições diferentes na implementação nativa.

## Contracaso derivado do código

`l.d` pode retornar falso: comprador com `gg()!=0`, atleta com `gh()==0`, vendedor com reputação >=4 e comprador com reputação <4 e divisão diferente de 1 (`best/l.java:647–674`). Nessas condições, supondo listado, preço e demais validações satisfeitos, o nativo retorna 4; o online entra no atalho e chama a transferência. Esse é um contracaso lógico da inspeção, não uma execução ou seleção de atleta específico da base.

Não foi constatada regra equivalente dentro de `F.a` que restabeleça essa decisão antes de mover o atleta; esse método é a transação aplicada depois da validação dos fluxos nativos.

## Cobertura existente e requisitos

`CareerManagementOfferTest.java:7–10` testa o helper de listado/preço e outras conversões de negociação; não fornece clube/jogador ao helper e, portanto, não pode testar a aceitação nativa. Seus resultados históricos permanecem válidos para o escopo que exercitam; nenhum teste foi repetido agora.

A compra direta deve respeitar a recusa nativa do atleta, manter mensagem correspondente e não mover elenco/saldo/contrato/histórico em recusa. Preservar compra de listado aceito e negociação dos não listados/abaixo do preço. Não chamar um método que já movimenta o atleta e depois aplicar a transação novamente no adaptador.

Critérios futuros: listado recusado versus aceito, preço exato/acima/abaixo, vagas e saldo, pedido direto e tela nativa, rejeição sem alteração parcial, checkpoint e retomada, identidade e saldos finais. Cenário entre vendedores humanos tem contrato próprio de consentimento e não foi certificado por este diagnóstico. Task permanece aberta até implementação e validação pertinente quando autorizadas.
