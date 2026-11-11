# Academia pelo acesso Times

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: aberta 09/11/2026. Achado durante a investigação da Procura solicitada pelo usuário. É um acesso secundário distinto do botão Juniores do dashboard já integrado; não invalida os testes daquele percurso.

## Reprodução e alcance

Em carreira descartável do Corinthians, com os 32 arquivos da cópia identificada na [investigação](../../evidencias/calendario-pesquisa-2026-11-09/README.md), o auxiliar abriu **NativeTransferMarket.open → Times → Academia (HW)**. A janela resultante é `a.ds`, com sete linhas de juniores. Foram lidos controles e listeners, sem clicar em promoção, dispensa, apelido ou compra:

| Controle | Estado observado | Listener instalado |
| --- | --- | --- |
| Promover / FQ | Visível e habilitado | a.dv |
| Dispensar / FP | Visível e habilitado | a.dw |
| Definir apelido / FO | Visível e habilitado | a.dy |
| Propor compra / FN | Oculto nessa consulta do próprio clube | a.dx |

Compilação do auxiliar e processo saíram com código 0; zero chamadas ao callback Submit. As duas janelas foram fechadas pelo auxiliar. Não houve confirmação, transferência, promoção ou gravação de save do usuário. É teste do wrapper/painéis originais, sem cliente conectado por HTTP nesse cenário. [Resultado](../../evidencias/calendario-pesquisa-2026-11-09/academia-via-times.json) e [auxiliar](../../evidencias/calendario-pesquisa-2026-11-09/AuditMarketAcademy.java.txt).

## Causa rastreada

Registro histórico do diagnóstico inicial: `openSelected` já passa (seller,buyer) e HW usa `uk`, mas naquele estado seller não tinha `ky()` nem havia fonte remota. A consulta host-backed descrita na atualização de 11/11 fecha essa lacuna de conteúdo juvenil nesta entrada. A Procura também usa o fluxo online de Times descrito na task relacionada.

O adaptador `NativeYouth` encaminha Promover ao host e bloqueia FP/FO/FN. Nenhuma dessas proteções é instalada quando a.ds é criada diretamente pelo Times. A fonte nativa de pN/pM/pP confirma chamadas de mutação local; a execução desta investigação comprova os controles/listeners preservados, sem demonstrar uma mutação concluída por eles.

## Requisito e aceite

Reutilizar o [contrato da academia](../../especificacao/spec-juniores-nativos-online.md) em todos os pontos de entrada. Própria base pode usar promoção autoritativa existente; dispensa/apelido/compra ficam explicitamente indisponíveis até seu contrato online. Base de outro clube deve manter somente consulta autorizada, sem promover como se pertencesse ao técnico atual. Se faltar catálogo autoritativo do clube visitado, informar a limitação e não preencher com atletas do asset.

Validar dashboard Juniores, Times→Academia e Procura→Times→Academia, próprio clube e adversário, modos de busca geral/individual, reconexão e atualização da janela aberta. Controles sem integração não devem reter callbacks por clique/atalho. Na promoção suportada, conferir ID, propriedade, fase/revisão, checkpoint, segundo cliente e recusa sem efeito local. Preservar rascunho de escalação e índices globais. O recorte só encerra após exercício dos acessos secundários no JAR entregue.

Relacionados: [academia principal resolvida](../../resolvidos/task-juniores-nativos-online.md), [Procura](task-indices-mercado-online.md), [índices](../../README.md) e [backlog](task-paridade-nativa.md). Implementação pendente.

## Continuação de implementação

`NativeTransferMarket` agora substitui o listener nativo de Academia e abre o wrapper online `NativeYouth`. A tabela mantém o visitante selecionado como clube consultado; promoção só fica ativa quando esse clube é o do técnico local e usa o comando `/management` já existente. Dispensa, apelido e compra seguem sem callback e permanecem bloqueados. A atualização de snapshots mantém o botão de promoção bloqueado em janelas somente de consulta.

`python tools/build_adapter.py --test` passou após a alteração, em modo headless. Não foi aberta janela do Brasfoot nesta etapa. O caminho foi verificado por leitura do bytecode de `a.iA.qY` (campo `uk` = clube visitado; `ul` = comprador) e compilação; ainda falta exercitar os três acessos na interface/JAR e conferir a atualização com a janela aberta. Esta task permanece aberta até essa validação.
### Correção estática dos papéis de `a.iA` — 10/11/2026

Correcao estatica dos papeis de a.iA e nuance do HW (11/11/2026): o construtor grava o primeiro ah em uk e o segundo em ul; mK usa uk no cabecalho/elenco e qY cria ds_0 com (ul,uk) = (viewer,clube visitado). O worktree atual passa (seller,buyer), portanto orienta a janela corretamente; o handler HW usa academyClub(panel)=uk e buyer como viewer. Contudo, seller e shell: projectRows so inclui os profissionais retornados pela pesquisa e nao popula ky(), entao nao fornece a base juvenil autoritativa. Ao completar o fluxo, obter elenco e juniores pelo host por clubId; a promocao continua permitida somente para o proprio clube. Fonte estatica em a/iA.java, a/ds_0.java, b/N.java e NativeTransferMarket/NativeTransferSearch; sem interacao.

### Dependência de seleção coerente na entrada pela Procura — 10/11/2026

A entrada em Times a partir de um resultado também requer coerência entre atleta pesquisado e linha selecionada. `a.iA.s(F)` altera o detalhe (`yK`) apenas; a JTable já foi montada do elenco de `uk` e seleciona a primeira linha. Embora HW seja independente dos comandos de transferência, o estado de seleção deve permanecer coerente ao abrir/fechar a Academia e ao voltar para Times. Cobrir detalhe, linha selecionada, contexto exibido e retorno da janela em um mesmo fluxo; ver também a [spec de pesquisa](../../especificacao/spec-pesquisa-jogadores-online.md).
### Precisao do alvo e da base consultada via Procura (10/11/2026)

Estado histórico anterior à consulta remota: com (seller,buyer), `academyClub(panel)=uk` passava o vendedor e `NativeYouth` recebia buyer como viewer, conforme `qY()->ds_0(ul,uk)`, mas `projectRows` não preenchia `ky()`. A lacuna foi fechada no worktree por `/club-academy`, conforme a atualização abaixo; continuam necessárias a leitura somente por ID e as mutações bloqueadas para clube alheio. Fonte estática em `NativeTransferSearch.projectRows`, `NativeTransferMarket.show`, `a.iA.qY`, `a.ds_0` e `b.N`.
### Origem autoritativa dos juniores do clube visitado (10/11/2026)

GameAdapter.snapshot inclui youthRow em visualClub, mas visualClubs e montado apenas para tecnicos humanos, adversarios do proximo fixture, participantes da data atual e clubes em partida corrente; nao e catalogo mundial. CareerManagement.snapshot publica youth somente de own, e CareerViews.snapshot (/overview) nao publica bases juvenis. A busca global pode retornar atleta de qualquer clube, enquanto projectRows materializa seller como shell e descarta ate uma eventual lista juvenil que estivesse em outro visualClub. Logo nao ha fonte atual que garanta ky completo do vendedor consultado. O contrato futuro precisa resolver clubId no host e fornecer youthRow autorizado, com ID, todos os campos usados por b.N/b.ai_0 e revisao/data; nao carregar o .ban local nem assumir que visualClubs cobre todos os clubes. Fontes: GameAdapter.snapshot/visualClub, CareerManagement.snapshot, CareerViews.snapshot, SessionHost /state,/management,/overview, NativeTransferSearch.projectRows; estatico.

### Atualização da implementação: consulta host-backed da base visitante (11/11/2026)

O worktree agora contém `POST /club-academy`: `CareerManagement.academy(clubId)` resolve o clube por ID e serializa `club.ky()` via `playerRow`; `NativeClient.queryNativeAcademy` faz a consulta assíncrona; `NativeTransferMarket` confirma o `clubId`, projeta cada júnior em `F`, popula `visited.ky()` e abre `NativeYouth` em modo somente leitura para clube alheio. Isso fecha a lacuna anterior de `ky()` vazio no fluxo com cliente conectado, desde que o endpoint seja alcançado. Promoção continua limitada no host por `own(coachId)` e `ownedPlayer(..., true)`, que exige que o jogador pertença à base do clube autenticado.

Ainda há lacunas antes do aceite: `queryRevision` e `clubId` agora são conferidos pelo cliente antes de instalar a resposta e o handler do host serializa consulta/revisão no mesmo monitor de comandos e `tick()`. Depois de aberta, porém, a janela não conserva a revisão consultada nem é atualizada pelo polling; o shell continua parcial para elenco profissional/árvore/fixtures. Como a projeção de `visited.ky()` foi isolada em objeto de academia novo em `NativeTransferMarket`, não contamina o shell compartilhado, embora seja necessário validar campos/ordem da base projetada. Aceite ainda requer atualização/indicação de obsolescência de janela aberta, múltiplas academias, falha/reconexão e JAR distribuído. Inspeção de código apenas nesta revisão; testes e jogo não executados.

Registro histórico supersedido pela checagem abaixo: a revisão daquele parágrafo dizia que o callback não validava o token, mas o HEAD atual já implementa essa comparação.

Conferência dos campos da tabela nativa (11/11/2026): `b.N` ordena os objetos de `club.ky()` e `b.ai_0` renderiza posição, nacionalidade/bandeira, nome, idade, lado, desenvolvimento (`F.go()`), CPE (`F.fD()`), características (`F.getCr1/getCr2`), valor (`F.fk()`) e salário (`F.fj()`). `CareerManagement.playerRow` contém os campos-base necessários e `academyPlayer` restaura os getters utilizados por esses renderers. Portanto há suporte estático para equivalência dos dados de linha da Academia; recursos de bandeira/ícones e ordenação por mesmos valores não foram executados. Isso não prova paridade da ficha/detalhe de jogador ou de seu histórico.

Complemento do comparador: `b.o_0` desempata ordenação de juniores por força (`F.fi()`), após posição, respeitando o sentido nativo controlado pelo estado estático `N.vG()`. `academyPlayer` restaura posição com `setPosicao` e força com `ad`, que são os mesmos getters consumidos pelo comparador. A ordem projetada tem equivalência estrutural de campos; a direção inicial do estado estático e a ordem visual final não foram exercitadas.
Complemento de frescor (11/11/2026): `queryNativeAcademy` captura a revisão atual, envia `queryRevision` no POST e rejeita a resposta se `latest.round.revision` mudou antes do callback; `academySnapshotCurrent` também exige o token. O endpoint executa sob `SessionHost.handleCommand` sincronizado, portanto não há janela entre a leitura de `club.ky()` e a revisão retornada enquanto a requisição é servida. A tarefa pendente é manter a janela já aberta sincronizada com revisões posteriores; não repetir como defeito a ausência de validação inicial do token.
