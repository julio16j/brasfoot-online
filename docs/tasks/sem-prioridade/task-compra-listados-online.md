# Compra de listado ignora recusa nativa do jogador

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: aberta complemento de PN-09; desvio de paridade confirmado por inspeção em 08/11/2026. Sem execução do jogo, testes ou nova carreira, conforme restrição atual. Não afirmar ocorrência observada em uma transferência do usuário.

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
## Guardas adicionais no vendedor — diferença de paridade estática — 10/11/2026

A revisão também encontrou um bloqueio online que não aparece no caminho nativo de compra direta. `CareerManagement.execute("offer")` chama `validateTransfer`, e `validateSeller` rejeita vendedor com `kc().size() <= 11` e venda do único goleiro (`CareerManagement.java:185–191`). No método nativo `best.l.b(F,ah)` (`analysis/decompiled/best/l.java:552–572`), a compra listada verifica estado listado, clube comprador diferente, saldo do comprador, limite de 35 no elenco comprador e `l.d` (aceitação do atleta); não consulta quantidade de profissionais nem quantidade de goleiros do vendedor. A transação `F.a(ah,int,boolean,boolean,boolean)` move o atleta entre listas e aplica finanças/histórico, sem esses dois preflights (`analysis/decompiled/best/F.java:2546–2614`). O handler nativo habilita Comprar para atleta listado que não esteja emprestado, sem guarda visível para esses limites de elenco do vendedor (`a/iA.java:sG`).

Assim, mesmo após alinhar a aceitação de `l.d`, o online pode recusar uma compra listada que o nativo aceita quando o vendedor tem exatamente 11 profissionais ou somente um goleiro. É divergência demonstrada pela leitura das condições, ainda não reproduzida com carreira/atleta concreto. Registrar esses cenários no aceite comparativo; decidir a regra desejada antes de alterar validações, pois os bloqueios online podem ter sido introduzidos como proteção de consistência e remover limites pode afetar outras ações que reutilizam `validateSeller`.
## Mensagens de recusa na interface — comparação estática — 10/11/2026

O painel nativo `a.iA.oM()` converte os códigos de `best.l.b` em mensagens locais sob o título “Compra”: não listado, compra realizada, jogador do próprio time, limite do elenco comprador, recusa do atleta e saldo insuficiente (`analysis/decompiled/a/iA.java:229–240`). No wrapper, o botão Comprar substitui esse listener por `NativeTransferMarket.purchase`; rejeições HTTP de `/management` passam pelo callback genérico de `NativeClient.command`/`showError`, que abre aviso sob “Brasfoot Online” com a mensagem técnica do host e escreve “Não foi possível concluir: ...” no status global (`NativeClient.java:96,394–400`). As mensagens de validação em `CareerManagement` são em inglês e incluem condições de propriedade, elenco mínimo/goleiro, vaga e saldo. Portanto, mesmo quando uma condição coincide, o texto/contexto visual de recusa não tem paridade com `a.iA.oM()`.

Critério futuro: traduzir cada recusa relevante para o resultado nativo e apresentá-la no contexto “Compra”, sem reportar erro genérico da sessão nem deixar estado parcial. Para condição que não tem código de recusa nativo, manter o requisito de política online explicitamente identificado em vez de mapear para uma mensagem nativa falsa. O caminho de sucesso deve continuar atualizando a janela/estado conforme a transação confirmada. Esta diferença foi inferida do roteamento e strings de código; não houve clique nem compra executados.

## Oferta a outro técnico ignora a elegibilidade prévia do jogador — complemento estático

O formulário nativo de proposta é aberto por `a.iA.sA()` somente se `best.l.d(player,buyer)` retornar verdadeiro; caso contrário, informa que o jogador não deseja atuar naquele time e não cria proposta. No wrapper, `NativeTransferMarket.makeOffer()` valida apenas que o atleta tenha vendedor diferente do comprador e abre o formulário. `/management?action=offer` também cria um registro `PENDING` para vendedor humano sem testar `best.l.d`; a resposta futura do vendedor é a única barreira, e não reproduz a recusa antecipada da interface nativa. Aplique a mesma elegibilidade antes de abrir/submeter a proposta, preservando a autorização do técnico vendedor como etapa separada de consentimento. Fonte: `analysis/decompiled/a/iA.java:sA`, `analysis/decompiled/best/l.java:d`, `src/brasfoot/NativeTransferMarket.makeOffer`, `CareerManagement.execute`; evidência estática, sem proposta enviada.

O mesmo handler nativo ainda bloqueia a proposta se `a.vL()` for falso, mostrando a mensagem de edição não registrada. `CareerEngine.initializeWorld()` carrega a preferência de registro por `best.h2.mg()`, mas `NativeTransferSearch.showOffer/makeOffer()` não verifica `a.vL()` e o endpoint também aceita a ação. A paridade depende da edição/configuração carregada no host; não presumir edição registrada em toda sessão. Incluir ambos os estados no aceite e manter a regra de registro separada da recusa `best.l.d`. Fontes: `a.iA.sA`, `a.vL`, `CareerEngine.initializeWorld`, `NativeTransferSearch.showOffer/makeOffer`, `CareerManagement.execute`; inspeção estática.

## Consistência do formulário e do limite de elenco — inspeção estática

`a.iA.sA()` também bloqueia a abertura do formulário quando o clube comprador já tem 35 profissionais. `NativeTransferMarket.makeOffer()` não reproduz essa guarda; o endpoint `offer` só a encontra indiretamente em `validateTransfer`, depois de a janela de proposta já ter sido aberta e submetida. Preservar o limite nativo no ponto de entrada e validar novamente no host para evitar divergência entre interface e autoridade.

Mensagens também divergem: `a.iA.sA()` explica a edição não registrada, recusa do atleta ou limite de elenco em diálogos locais; a rota wrapper deixa erros HTTP passarem pelo aviso genérico “Brasfoot Online”. O aceite da proposta deve cobrir bloqueios no formulário e no host, textos/títulos nativos adequados, nova tentativa após erro de preço, jogador IDêntico entre linha/detalhe/comando, vendedor IA e humano, e nenhuma alteração parcial quando houver rejeição. Isso complementa a ausência de `best.l.d` já registrada acima; não se deve tratar o aceite do técnico vendedor como substituto da elegibilidade prévia do atleta.

Escopo da regra: quando o vendedor é IA, a negociação usa `best.l.a` e mantém recusa/contraproposta nativas; o atalho de compra de listado é caminho separado. Para vendedor humano, consentimento pendente é extensão multiplayer, mas só deve nascer depois das mesmas precondições visíveis que a tela nativa exige. Evidência de leitura estática, sem enviar oferta nem abrir o jogo.
