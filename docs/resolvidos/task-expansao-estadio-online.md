# Expansão do estádio na interface nativa

[Spec de implementação e critérios de aceite](../especificacao/spec-expansao-estadio-online.md).

Prioridade: Alta. Status: aberta; correção visual e rollback direto integrados à principal em `23c3e355`, com validações delimitadas no [registro](../implementacoes/implementacao-expansao-estadio-mvp.md). Aceite integral ainda pendente.

## Relato e evidência

Na imagem `Estádio com problema no nome e sem expansão.png`, o painel nativo do Augsburg mostra capacidade e preços, mas o acesso à expansão não está visível. O título tem caracteres incorretos. A captura comprova o resultado visual, sem determinar a causa.

## Diagnóstico inicial

`NativeStadium.openWithPriceSubmit()` cria o painel nativo `a.cl`, liga o botão `BY` à janela nativa de expansão `a.cq` e fixa o diálogo em 598×328. O host já recebe `stadium-expand`; `CareerManagement` valida saldo, obra duplicada e custo. Investigar primeiro a posição/visibilidade do botão no diálogo e a codificação do título; depois verificar o fluxo completo da obra na interface.

Refinamento estático: `a.cl_0.mH()` coloca `BY` em y=290…320; a altura 328 foi aplicada ao diálogo externo, não à área interna. Isso torna corte do botão uma hipótese forte a medir. `a.cl_0.oB()` também oculta `BY` quando há uma obra ativa e mostra seu prazo. Confirmar qual condição ocorre na captura antes da correção. `a.cq_0` mostra o botão de confirmação somente após cotação pelo controle nativo.

Diagnóstico histórico anterior a `23c3e355`: `SessionHost` chamava o rollback de `/management` após falha em `persist()`, mas `CareerManagement.rollback()` não restaurava `stadium-expand`. A correção integrada restaura projeto, saldo e lançamento contábil no rollback direto; falta exercitar a falha de checkpoint pelo caminho HTTP real. Ver [spec](../especificacao/spec-expansao-estadio-online.md).

## Aceite

Abrir a expansão pela tela nativa, visualizar setores, custo e prazo, confirmar obra e débito únicos no host, acompanhar andamento e capacidade final e preservar o estado após retomada. Rejeitar saldo insuficiente e repetição. Mostrar o título “Estádio — Augsburg” corretamente. Validar na interface e no pacote distribuído. Nenhum jogo ou teste foi executado nesta revisão.

[Spec em evolução](../especificacao/spec-expansao-estadio-online.md) · [preços já resolvidos](task-precos-estadio-online.md).
