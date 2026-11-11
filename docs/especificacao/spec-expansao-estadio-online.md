# Expansão nativa do estádio online

Status: task concluida em 11/11/2026 apos confirmacao do usuario de que a expansao funciona. Esta spec preserva criterios adicionais de robustez ainda parciais. Task: [expansao do estadio](../resolvidos/task-expansao-estadio-online.md).

Preservar o painel nativo de estádio `a.cl` e a janela nativa de expansão `a.cq`. O botão de expansão deve ficar visível e acessível no diálogo. A confirmação deve enviar a intenção ao host, que calcula custo e prazo pelas regras nativas, valida saldo e ausência de obra duplicada, registra e persiste uma única obra. O cliente deve mostrar o estado confirmado, inclusive após reconexão e conclusão. O título do estádio deve usar texto legível.

## Estado encontrado no código

`NativeStadium.openWithPriceSubmit()` instancia `a.cl`, substitui o listener do botão `BY` e abre `a.cq` por `openConstruction()`. A janela principal recebe tamanho fixo de 598×328, e a de construção, 438×360; ambas impedem redimensionamento. O botão `Cv` envia `action=stadium-expand` e os quatro valores `seats0`…`seats3` lidos dos spinners `CA`…`CD`. O título da janela principal contém literalmente `EstÃ¡dio â€” ` no fonte, compatível com a corrupção visível na captura. O código liga o botão, mas a imagem não mostra seu acesso: medir posição, área útil, visibilidade e sobreposição de `BY` no pacote atual antes de decidir a correção de layout.

O host em `CareerManagement` exige clube com estádio, rejeita obra já presente em `SR.bw()`, valores negativos ou soma zero, usa `NativeStadium.quote()` para os limites, custo e prazo nativos, confere saldo, acrescenta um `best.B` e debita uma vez. A resposta inclui `CONSTRUCTION_STARTED`, custo, prazo e assentos. `GameAdapter` publica `stadiumExpansions` com clube, prazo e assentos; `VisualModel` reconstrói essas obras e a capacidade do estádio para o cliente. Esses caminhos estão presentes no código; a captura não certifica sua execução.

## Regras de interface e autoridade

1. O botão nativo de expansão deve ser visível, clicável e identificável no painel de estádio em tamanhos e temas suportados. A janela de setores deve usar o componente nativo `a.cq` e mostrar os valores e o orçamento que o nativo calcula.
2. A edição local dos quatro setores é um rascunho. Só o host autoriza a obra; uma resposta de sucesso atualiza obra, saldo e capacidade projetada dos clientes. Uma falha mantém a janela utilizável, preserva o rascunho e mostra a causa sem sugerir que a obra foi criada.
3. A obra pertence ao estádio compartilhado. Clube sem estádio, saldo insuficiente, zero assentos, limite por setor e obra duplicada devem falhar sem débito nem segundo projeto. O host deve registrar projeto e débito no mesmo checkpoint; se o armazenamento falhar, verificar rollback antes de afirmar atomicidade.
4. Enquanto a obra está em andamento, exibir seu prazo e impedir nova expansão do mesmo estádio. Na conclusão nativa, aplicar a capacidade uma única vez. Após reconexão e retomada, a tela deve refletir projeto, saldo, capacidade e preço confirmados.
5. O título da janela deve apresentar “Estádio — <clube>” com caracteres corretos; nomes de clubes e de estádios devem vir do estado projetado, sem corromper a localização.

## Critérios de aceite observáveis

- Abrir **Estádio** pela preparação, localizar `BY` e abrir `a.cq` sem perder controles por corte ou sobreposição; conferir também a janela no tema usado na captura do Augsburg.
- Simular uma cotação válida, confirmar uma vez e verificar custo, prazo e quatro quantidades no host, saldo debitado, projeto em andamento e igualdade entre host e clientes após atualizar/reabrir a janela.
- Repetir a confirmação, pedir zero assentos, exceder limite e usar saldo insuficiente; cada rejeição mantém saldo, projeto e revisão coerentes. Exercitar falha de checkpoint separadamente.
- Salvar e retomar durante a obra; avançar até a data de conclusão e conferir capacidade final sem segundo débito. Validar o resultado no JAR distribuído pelo launcher.

Pendente de investigação: a posição real de `BY` em `a.cl`, o tratamento do callback quando `stadium-expand` falha, e a regra exata de conclusão em `best.B`/avanço de data. Esses itens não impedem especificar o comportamento; impedem declarar o defeito resolvido.

### Falha de checkpoint: diagnóstico histórico e validação restante

O diagnóstico abaixo descreve o estado anterior ao merge `23c3e355`. A implementação atual acrescenta rollback de `stadium-expand`; o teste direto de `CareerManagementStadiumTest` passou na branch e na principal. A falha real de `persist()` pela rota HTTP ainda não foi exercitada, por isso a exigência de aceite permanece aberta.

`SessionHost` obtém `management.rollback(coach,data)` antes de executar `/management` e chama o callback se `persist()` falhar. `CareerManagement.execute(stadium-expand)` já acrescenta `best.B` a `SR.bw()` e debita o clube, mas `CareerManagement.rollback()` só trata treino, preços e promoção; para expansão retorna um callback vazio. `ah.w(cost,7)` sempre reduz o saldo `nb`; se houver `nx` e o clube for humano (`jZ()`), também chama `C.i(cost,7)`, que incrementa o lançamento contábil `dQ`. Portanto, na falha de gravação, o host pode conservar projeto, saldo reduzido e, quando esse ramo se aplica, lançamento em memória apesar de responder erro e bloquear comandos. O aceite exige restaurar somente o projeto criado, o saldo e o lançamento contábil quando alterado, além de manter revisão/estado coerente antes de permitir retomada ou nova tentativa; testar o cenário com falha de checkpoint controlada. `best.B.ex()` só aplica os assentos na conclusão e zera os quatro incrementos, razão para validar conclusão única separadamente. Achado por leitura de `SessionHost`, `CareerManagement` e fontes nativas `ah/C/B`, sem executar o jogo.

## Refinamento estático para implementação — 11/11/2026

No nativo `a.cl_0.mH()`, `BY` é colocado em `(60,290,170,30)` dentro de um painel com fundo em `(0,0,600,330)`. `NativeStadium` aplica 598×328 ao **diálogo externo**, cujo conteúdo útil é menor após bordas e barra de título. Isso fornece uma causa plausível e concreta para o corte visto na captura; conferir `getContentPane().getSize()`, limites reais de `BY` e visibilidade antes de corrigir o tamanho por `pack()`/dimensão do conteúdo. O nativo `a.cl_0.oB()` também oculta `BY` quando `SR.bw()` contém obra para o mesmo estádio e exibe o prazo em `zh`. A imagem, por si, não distingue corte de obra ativa. A implementação deve cobrir ambos: botão visível quando não há obra; prazo visível quando há obra. Em `a.cq_0`, o botão de confirmar `Cv` começa oculto e aparece depois da cotação por `Cu`; preservar essa sequência nativa.
