# Índices locais da Procura e de Times

Status: parcial em `main`; seleção após ordenação, validação de preço, listagem/preço/condição de empréstimo e atributos individuais projetados. Fonte/completude dos candidatos e validação de interface/retomada permanecem abertas. Diagnóstico original em 08/11/2026.

## Caminho rastreado

`NativeTransferSearch` instancia `a.gx`, cujo `nD()` chama `af.D(false)`; a busca percorre o índice global `c.a.SR.O()`, não o elenco do comprador (`analysis/decompiled/a/gx_0.java:177–184`; `best/af.java:58–91`). A tela Times usa `c.a.SR.P()` para montar sua árvore (`a/iA.java:350–360`).

`VisualModel` cria um estado nativo vazio. Para cada clube visível, tenta construir `new ah(e.t)` a partir de seu `.ban`; esse construtor registra o clube em P e os profissionais originais do arquivo em O (`best/ah.java:1315–1380`; `best/f_0.java:382–407`). Depois, `VisualModel.updateClubs` limpa `club.kc()` e reconstrói o elenco usando os dados do host. Reaproveita referências antigas quando o identificador coincide; jogadores ausentes são retirados do elenco e novos recebem `new F()`.

O construtor vazio de F e seu setter `n(ah)` não registram o jogador em O (`best/F.java:111,238–241`). O adaptador não reconcilia explicitamente O ao substituir esses elencos. Portanto, retirar/incluir referências em kc não mantém por si só o índice consumido pela Procura: um jogador removido pode continuar no índice; um novo objeto projetado pode não entrar nele. A manifestação exata depende das coincidências entre IDs do arquivo e do host, sem caso real executado nesta rodada.

No fallback sem `.ban`, `new ah()` também não registra o clube em P. Além disso, `GameAdapter.snapshot` só inclui em `visualClubs` clubes dos técnicos, adversários próximos e partidas correntes; não o universo completo da carreira. O mapa cliente acumula clubes já vistos, sem contrato explícito de catálogo completo ou expiração. Não afirmar que a Procura está sempre vazia: os construtores dos assets possuem os registros descritos acima.

`CareerManagement.view` possui uma consulta separada de mercado sobre todos os clubes do host. As telas nativas `NativeTransferSearch.open` e `NativeTransferMarket.open` não a consultam ao montar candidatos. A disponibilidade dessa API não certifica cobertura das duas telas. Os testes auxiliares de propostas/preços/listeners não validam identidade ou completude dos candidatos exibidos.

## Requisitos e validação pendentes

### Lista de venda e preço pedido não chegam às telas nativas

O comando `list` modifica `F.c(forSale)` e `F.ag(amount)` no host; a consulta separada `CareerManagement.playerRow` publica `forSale` e `askingPrice`. Esses campos não existem em `GameAdapter.visualClub` nem são aplicados em `VisualModel.updateClubs`. Não confundir `value`/fk com o preço pedido fl: são campos distintos (`best/F.java:267–285,413–419`).

O consumidor nativo depende exatamente desses dados: `iA.sG` habilita Comprar por `F.ft()` e bloqueia negociados por empréstimo `F.gl()` (`a/iA.java:539–548`); `NativeTransferMarket.purchase` calcula o pedido a partir de fl e só usa fk como fallback. `af.i` também filtra a opção de venda por ft (`best/af.java:125–190`). O campo de empréstimo não é projetado em visualClub, embora exista no DTO de gestão. Assim, uma alteração autoritativa da lista/preço ou condição de empréstimo não é propagada explicitamente aos objetos usados pelas telas; valores do asset/default não certificam o estado da carreira. A validação do host continua necessária e pode recusar um comando feito com informação visual obsoleta.

Correção integrada: `GameAdapter.visualClub` publica `forSale`, `askingPrice` e `loanAvailable`; `VisualModel` aplica esses valores no objeto nativo em vez de confiar no asset. `AvailabilityProjectionTest` verifica lista, preço pedido e disponibilidade de empréstimo em snapshot e projeção. Falta exercitar filtros/botões e atualização por outro cliente após listar/deslistar.

Aceite adicional: listar/deslistar, alterar preço mantendo o valor estimado, conferir habilitação/realce/filtro e proposta em outro cliente, renovar a consulta e retomar o save. Validar também atleta emprestado originado de save compatível; empréstimos novos continuam indisponíveis no online.

### Seleção após ordenar: conversão duplicada

Há outro desvio estático independente da origem dos candidatos: `NativeTransferSearch.offerSelected` obtém a linha selecionada, converte-a com `convertRowIndexToModel` e passa o resultado a `JTable.getValueAt`. Esse método da tabela recebe índice visual e aplica a conversão do sorter; a expressão atual compõe a permutação duas vezes. O painel nativo instala `TableRowSorter` nos dois modos (`a/gx_0.java:230–255,282–297`); seu modelo retorna o F da linha (`b/d_0.java:51–55`).

Contracaso lógico: ordem visual para modelo `[1,2,0]`; selecionar a primeira linha deveria obter modelo 1, mas a expressão atual pede à tabela a linha visual 1 e obtém modelo 2. A confirmação pode assim mostrar outro atleta. Uma linha que permanece na mesma posição pode ocultar o problema. Uma troca simples `[1,0]` também o revela: selecionar a linha 0 deveria obter modelo 1, mas obtém modelo 0. Incluir ordenação identidade, troca de dois itens e ciclo de três no aceite futuro. Não foi executado teste Swing nesta etapa.

Usar uma única conversão: índice visual com `table.getValueAt`, ou índice convertido com `table.getModel().getValueAt`. Conferir também o atleta no diálogo e no comando final. O host valida ID e vendedor atual (`CareerManagement.java:48–49`), mas isso não identifica qual linha o usuário pretendia selecionar; não declarar que essa validação corrige o erro de seleção.

Correção integrada: `NativeTransferSearch` agora centraliza a leitura da linha visual pela própria `JTable`; o detalhe selecionado usa a mesma rota. `NativeTransferSearchTest` cria uma tabela ordenada em ordem inversa e confirma que a seleção visual retorna a linha correspondente. Ainda falta exercitar a janela nativa e confirmar o atleta exibido no diálogo/comando.

### Normalização do valor da proposta

Na mesma rota, `parseNativePrice` faz `Long.parseLong(entered)*1000L` antes de verificar faixa de int (`NativeTransferSearch.java:65–69`). A multiplicação long pode transbordar sem NumberFormatException. Contracaso aritmético: a entrada `18446744073709552` cabe em long, mas multiplicada por 1000 com sinal de 64 bits resulta em 384, que passa pela verificação de faixa atual. O pedido gerado usa um valor diferente do texto digitado. Isso não contorna saldo/aceitação do host e não comprova uma transferência real; é um desvio da validação do formulário.

Confirmado apenas por leitura da expressão e cálculo Python da aritmética de 64 bits, sem executar Java/jogo. Validar antes de multiplicar ou usar multiplicação com detecção de overflow, incluindo teto normal em milhares e entradas long que fazem wrap para positivo. `NativeTransferSearchTest` cobre 2147484, mas não esse caso de long. Além disso, o botão Enviar é desabilitado antes do parse no listener; uma rejeição síncrona do valor só mostra a mensagem e não o reabilita no catch. O callback de falha só existe depois de uma submissão válida. Acrescentar correção do valor e nova tentativa na mesma janela ao aceite futuro.

Correção integrada para preço: `parseNativePrice` usa multiplicação com detecção de overflow; o teste inclui o valor longo que antes podia retornar preço incorreto. Erros síncronos reabilitam Enviar, permitindo corrigir e tentar novamente na mesma janela. Definir uma fonte autoritativa de pesquisa/catálogo e atualizar os modelos nativos com IDs estáveis, sem depender dos elencos estáticos dos arquivos de apresentação. Se forem mantidos índices locais, reconciliar clube, elenco e índices globais como uma única projeção; não conservar atleta removido nem omitir objeto novo. Distinguir universo pesquisável de clubes carregados para apresentar a rodada. Respeitar os filtros nativos e a visibilidade prevista, sem expor resultados futuros durante playback.

Aceite futuro: buscar atleta de clube fora da rodada; incluir promovido/contratado; excluir dispensado ou vendido do clube anterior; conferir jogador, vendedor e ID da proposta; repetir após reconexão/retomada e em fallback sem asset. Verificar dois clientes e os modos de [habilidades individuais](task-habilidades-individuais-online.md). A proposta deve continuar validada pelo host; reparar a lista visual não substitui validação da transação.

Relacionados: [compra de listado](task-compra-listados-online.md), [paridade](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação, reprodução dinâmica e aceite continuam pendentes.
