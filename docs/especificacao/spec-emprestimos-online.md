# Empréstimos de jogadores online

Status: requisitos para PN-07, ainda sem implementação online. Revisado em 08/11/2026. [Backlog](../tasks/sem-prioridade/task-paridade-nativa.md); [relatório da auditoria](../estado-agente-requisitos.md).

## Regra local e evidência

O botão nativo MJ de Times chama `best.l.a(jogador,clube)` (`analysis/decompiled/a/iA.java:217–226`); o adaptador o desabilita em `NativeTransferMarket.java:25`. O consumidor nativo (`best/l.java:520–550`) rejeita indisponibilidade para empréstimo, mesmo clube, elenco com 35 profissionais, quatro recebidos já ativos e recusa do jogador; sucesso chama `F.q(destino)`.

`F.q` cria registro `components.t`, guardando jogador e origem, e efetua a movimentação com os flags de empréstimo (`best/F.java:2782–2789`). Na decompilação o registro é `components/t_0.java`: prazo de **366 dias** desde `SR.bc()`, vencido apenas quando prazo `<` data corrente; `tN()` devolve à origem se houver espaço (origem humana com `kw()<35`). Retorno bem-sucedido e remoção do registro são operações distintas; quem chama deve executar ambas. O processamento normal de `best.f.ax()` chama `au()`, que tenta devolver vencidos e cria avisos quando a origem está cheia (`best/f_0.java:1545–1580,1635–1644`). Não apagar o registro quando a devolução falhar.

A listagem como emprestável usa `F.g(boolean)`; o painel original limita a soma de listados e cedidos a dez (`a/eg_0.java:843–859`). Há acessos nativos para cancelar, adquirir em definitivo e consultar cedidos/recebidos (`a/eg_0.java:863–905`, `a/bs_0.java:90–165`). Preservar diferenças entre esses fluxos.

Probe independente real do JAR: `AuditLoans.java`, `result-loans.txt`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`. Passaram rejeição de indisponível e próprio clube; empréstimo aceito; movimentação/flag/registro; prazo exato de 366 dias; ausência de débito inicial no tomador nesse fluxo; save e leitura Kryo; devolução e retirada do registro. Execução usa clubes reais numa Fixture em memória, reputação reduzida para tornar a aceitação determinística e limite de elenco ajustado somente nessa Fixture. Não certifica rede, interface, salário durante o empréstimo, nem processamento automático de vencimento.

## Adaptação multiplayer proposta

O host guarda contratos e aplica as transações nativas. A interface deve enviar IDs estáveis e intenção de listar, propor empréstimo, responder ou solicitar retorno. Não usar compra com preço zero no contrato existente: empréstimo tem origem, prazo e flags próprios. Snapshot de gestão deve publicar origem, destino, jogador, prazo e estado de retorno, evitando depender dos registros locais vazios de `VisualModel`.

Para origem humana, proposta exige aceite do técnico de origem e revalidação dos dois clubes, do jogador, disponibilidade, vagas e revisão. Para origem IA, aplicar o consumidor local e expor o resultado nativo. Essa política de consentimento humano é proposta multiplayer, não comportamento do motor offline certificado. Confirmar o contrato de cancelamento solicitado por origem ou destino antes de habilitar ambos os acessos. Propostas pendentes ainda não movimentam o jogador e devem ser persistidas com status próprio.

Comandos usam a fase/revisão de gestão e invalidam Pronto dos participantes afetados. Repetição não cria outro registro nem cobra valores de novo. Retorno por prazo precisa usar o ciclo nativo existente e respeitar falta de vagas; avisos devem chegar aos técnicos corretos. Compra definitiva de emprestado fica explicitamente indisponível até integrar o fluxo específico de origem e saldo, sem deixar o callback offline ativo.

## Aceite e pendências

Validar limites com consultas e pedidos diretos; aprovação/recusa entre humanos; IA; concorrência de propostas; clube/jogador transferido; saldo e responsabilidades salariais; prazo e igualdade na data-limite; retorno com origem cheia e reexecução posterior; save/retomada em outra JVM; ofertas e contratos recuperados do mesmo checkpoint. Verificar listas nativas em dois clientes, controles sem callback local e pacote distribuído após integração.

Ainda requer levantamento executado: quem paga salários e como aparecem os lançamentos; cancelamento nos dois sentidos; compra definitiva; múltiplos empréstimos e quatro recebidos; avisos e efeitos sobre escalação/atletas já usados. Não tratar os resultados do probe de modelo como aceite desses cenários. A implementação pode entregar recortes, mantendo cada fluxo restante explicitamente bloqueado e PN-07 aberta.

## Paridade da entrada pelo painel Times — inspeção estática

O consumidor do empréstimo à IA está alinhado: o painel nativo habilita `MJ` conforme `F.fz()` (desde que o atleta não esteja emprestado e o contexto não seja o clube do técnico); o wrapper instala a mesma ação remota e o host revalida vendedor, disponibilidade e flags antes de chamar `best.l.a(player,buyer)`. Esse consumidor verifica também elenco do destino, quatro contratos ativos e aceitação do atleta. A mensagem nativa fala em limite de 30, mas o código efetivamente testa `kc().size() >= 35`; registrar essa diferença entre texto e regra, sem reproduzi-la como outro limite.

O defeito histórico de habilitação inicial foi corrigido: `NativeTransferMarket.show()` não força mais `loan.setEnabled(true)` e preserva o estado calculado por `a.iA.sG()` após a seleção do atleta. O registro da correção, teste Swing e rebuild está em [Estado inicial do botão de empréstimo](../implementacoes/implementacao-estado-emprestimo-times.md). Ao trocar de linha, `sG()` reaplica a regra. Isso fecha a divergência de enabled no código local e no JAR/ZIP citados pelo registro; não certifica o pacote produzido depois nem a mensagem contextual da ação.

Para vendedor humano, `loan-request` registra consentimento pendente; isso é uma extensão multiplayer deliberada e não a decisão IA do motor local. A entrada visual permanece condicionada a `fz()` e clube de origem diferente; aceite/rejeição pertence ao técnico de origem e não pode mover o jogador antes do aceite. O wrapper mostra confirmação genérica depois do envio e rejeições HTTP passam por aviso genérico do cliente, em vez das mensagens/títulos locais de `a.iA.sz()`; mapear resultados nativos e manter a confirmação no contexto Empréstimo faz parte do aceite de interface. Evidência estática de 10/11/2026, sem executar o jogo nesta revisão.

Fontes: `analysis/decompiled/a/iA.java:sG/sz`, `analysis/decompiled/best/l.java:a(F,ah)`, `src/brasfoot/NativeTransferMarket.show/requestLoan`, `src/brasfoot/CareerManagement.execute/respond/nativeLoanRefusal`, `NativeClient.command/showError`. Não repetir como falha a validação dos limites da IA, que já delega ao consumidor nativo.

Lacuna de apresentação das solicitações humanas: a rota `loan-request` cria um contrato pendente `kind=LOAN`; o host persiste esse estado e revalida origem, destino, disponibilidade e resultado de `best.l.a` quando o vendedor aceita. Porém `NativeCareerDialogs` preenche a lista “Proposta” apenas com ID e nome do jogador e `describe()` também não expõe `kind`, clubes, valor ou situação. A mesma ação `respond` responde à compra e ao empréstimo; para pedido de empréstimo, o técnico vendedor não tem indicação de que aceitar moverá o atleta sem preço. Critério: identificar tipo, clube solicitante, jogador e status na lista/detalhes, confirmar explicitamente o efeito de aceitar empréstimo e manter a distinção de transferência; testar pedido pendente, aceite, recusa, expiração e recuperação após checkpoint. O comando atual mantém consentimento e transação separados corretamente, portanto a lacuna é de compreensão/apresentação, não autorização. Fontes `CareerManagement.snapshot/execute/respond`, `NativeCareerDialogs.managementPanel`; inspeção estática.

## Complemento estático — folha, retorno e cancelamento

Pesquisa posterior somente por código, sob a restrição de não abrir o jogo:

- `ah.kK()` soma salários de `kc` e `ky` sem excluir cedidos/recebidos; `kJ()` debita essa soma por `f(long)` (`analysis/decompiled/best/ah.java:1685–1689,1716–1732`). `F.a` move o atleta da lista profissional de origem para a do destino (`best/F.java:2607–2614`). Portanto, no fluxo nativo rastreado o salário compõe a folha do clube que o recebe, não uma divisão percentual configurável. O encadeamento e os lançamentos reais de uma data de folha ainda precisam de validação executada futura.
- `F.q(destino)` e `F.r(origem)` usam o mesmo consumidor de movimentação com flags diferentes. Em `F.a`, entrada como empréstimo define contrato de 365 dias; retorno cai no ramo de 180 dias, a partir da data corrente (`best/F.java:2547–2590,2782–2789`). O prazo do registro de empréstimo continua 366 dias. Não impor “restaurar exatamente o contrato anterior” como se fosse a regra local; essa diferença deve ser preservada/documentada antes de qualquer adaptação.
- `components.H` (decompilado `components/h_0.java:41–74,89–95`) constrói linhas de recebidos e cedidos com o mesmo botão de cancelamento, chamando `a.bs.a(registro,tipo)`. Essa ação usa `tN()` e só remove o registro quando a devolução funciona. Há caminho nativo de cancelamento nos dois sentidos; autorização multiplayer precisa restringir aos técnicos envolvidos, em vez de aceitar clube arbitrário no pedido.
- `best.f.au()` retenta vencidos; quando não há vaga na origem humana, conserva o registro e gera aviso uma vez por meio de flag Po/tQ (`best/f_0.java:1545–1575`). O host online precisa transportar o estado pendente/aviso, não encerrar visualmente o contrato apenas pelo vencimento.

Esses pontos refinam o levantamento anterior, sem substituir os testes de folha, cancelamento bidirecional, origem cheia e retomada. Nenhuma nova execução ocorreu para obtê-los.
