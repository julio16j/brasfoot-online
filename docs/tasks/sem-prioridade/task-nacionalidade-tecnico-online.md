# Nacionalidade do técnico na criação e projeção

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: transporte/aplicação/projeção comitados em `fa6d53fa`; aceite aberto. Bloqueio de arranque frio identificado por inspeção estática em 10/11/2026. Complemento PN-11. Nenhum jogo/teste foi executado nesta revisão.

Diagnóstico histórico de 09/11: CoachChoice/host não transmitiam lE e a criação substituía a nacionalidade pelo país do clube. A spec foi criada após esse diagnóstico. O commit `fa6d53fa` captura, transmite, aplica e projeta nacionalidade, mas a validação no host recém-lançado contém o bloqueio descrito abaixo; não considerar a correção aceita/validada.

## Revisão estática do diff concorrente — 10/11/2026

O fluxo alterado em `NativeClubSelection` captura `coach.lE()` por escolha; `NativeClient` passa os códigos em `--coach-nationalities`; `SessionHost` lê o argumento; `CareerEngine.startAssets` aplica cada valor; `GameAdapter` e `CareerViews` publicam o campo; `VisualModel` usa `coach.cg(...)`. `CareerCountrySelectionTest` acrescenta verificações de valor explícito, snapshot, `/overview`, código desconhecido e cardinalidade. Não executei esse teste nem alterei esses fontes.

**Bloqueio de arranque frio, confirmado pelo fluxo estático:** `SessionHost` inicia uma JVM filha e chama `CareerEngine.createByAssets(..., nationalities)`. Esse método chama `validateNationalities` antes de `initializeWorld(root)`. A validação lê `best.n.cY()`, que em `best/n_0.java:29–30,52–66,155–157` retorna a lista estática `cF`, inicialmente vazia; `cU()` é quem a preenche e o caminho de host só a chama dentro de `initializeWorld`, depois da validação. Como a UI agora sempre passa `--coach-nationalities`, o novo host rejeita todos os códigos como desconhecidos antes de construir a carreira, salvo se algum caminho oculto inicializar o catálogo primeiro. A busca estática dos chamadores de `cU()` não encontrou inicialização por `SessionHost`/agente. O teste novo mascara o caso: seus cenários iniciais chamam CareerEngine e `initializeWorld`, que enchem `cF`, antes do caso de nacionalidade explícita na mesma JVM.

Resultado: transporte e projeção estão presentes no diff, mas a criação multiplayer pelo launcher estática e previsivelmente bloqueada. O teste de arranque em processo/JVM independente precisa começar com catálogo não inicializado; após corrigir a ordem, ainda faltam seleção visível, segundo cliente, reconexão/save e efeito nos convites de emprego. A carreira comum sem argumento explícito mantém o fallback histórico para país do clube.

**Diagnóstico histórico de 09/11, superado pelo commit `fa6d53fa`:** o seletor original `a.bV` contém uma seleção de nacionalidade BD, distinta do país/competição/clube escolhidos. `or()` passa o país selecionado, convertido por `best.n.H`, a `club.b(nome,pais)`; esse método grava o valor em `al.cg` (`analysis/decompiled/a/bv_0.java:106–109,234–239`; `best/ah.java:2019–2035`). `al.lE()` é o getter dessa nacionalidade.

Antes de `fa6d53fa`, `NativeClubSelection.CoachChoice` conservava apenas ID/nome/asset do clube e nome do técnico, e `CareerEngine.startAssets` usava o país do clube. No HEAD atual a escolha viaja por `--coach-nationalities` e é aplicada; o bloqueio de arranque frio acima ocorre antes da criação, então ainda não há aceite observável. Não foi criado um técnico real para reproduzir agora.

Antes de `fa6d53fa`, o snapshot de técnicos não incluía lE e `VisualModel` não aplicava a nacionalidade. No HEAD atual, `GameAdapter`/`CareerViews` publicam o campo e `VisualModel` usa `coach.cg(...)`; essa projeção permanece sem validação por causa do bloqueio no host. Não confundir o país do técnico com country do clube ou dos jogadores.

A nacionalidade participa de regra esportiva/gestão: `best.ay.b(al,false)`, chamada por `CareerManagement.jobs`, consulta `SR.o(al.lE())` como uma das origens dos convites, além do país associado por bz (`analysis/decompiled/best/ay.java:200–235`). Há guardas adicionais por reputação e histórico e busca em outros países; portanto, não concluir que toda lista de emprego muda, mas uma nacionalidade aplicada incorretamente pode alterar a seleção de convites. Isso é consequência condicional do código, sem ofertas reais comparadas.

Requisito: transportar a nacionalidade selecionada como dado próprio, validar o país usando o catálogo nativo e preservar a ordem/identidade dos técnicos na criação. O ID do país deve ser o código nativo, não o índice visual ordenado do combo. Publicar e projetar a nacionalidade autoritativa; definir fallback explícito para criações/manifestos antigos, sem sobrescrever uma escolha válida.

Aceite futuro: técnico brasileiro em clube estrangeiro, estrangeiro em clube brasileiro, dois técnicos com nacionalidades distintas, índice visual versus código do país, reinício/reconexão/save e perfil nativo exibido em outro cliente. IDs e nomes dos clubes devem continuar corretos; a correção não substitui a investigação de [calendários](../../resolvidos/task-calendarios.md).

## Correção do bloqueio de arranque frio — 11/11/2026

`CareerEngine.createByAssets(..., nationalities)` agora chama `initializeWorld(root)` antes de `validateNationalities`, garantindo que `best.n.cY()` leia o catálogo nativo carregado por `best.n.cU()`. A validação de códigos desconhecidos e da cardinalidade foi preservada. O código `29` passa a ser verificado contra o catálogo correto, em vez de uma lista estática ainda vazia.

Validação: `python tools/build_adapter.py --test` passou, incluindo `CareerCountrySelectionTest` (nacionalidade explícita, código desconhecido, cardinalidade, seleção de clubes e calendários). O cenário de nacionalidade explícita desse teste não começa em JVM fria, mas a alteração corrige a ordem identificada no caminho de `SessionHost`, que cria o host em processo novo. `python tools/tests/test_career_country_selection.py --jar dist/brasfoot-online.jar` não iniciou porque o executor do ambiente falhou ao criar o processo. O teste sem `--jar` também não pôde ser executado pelo mesmo erro.

Pacote reconstruído com sucesso por `python tools/package_client.py`: `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip` (80.291.984 bytes). O aceite da seleção gráfica e do início de sessão pelo launcher permanece pendente de execução manual; uma instância já aberta precisa ser fechada e reaberta para carregar o novo JAR.

Relacionado: [backlog](task-paridade-nativa.md) e [mapa](../../mapa-implementacao.md). O bloqueio identificado foi corrigido e empacotado; aceite visual/launcher ainda pendente.
