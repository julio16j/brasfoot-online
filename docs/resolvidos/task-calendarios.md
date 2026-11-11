# Investigação dos calendários de ligas

Status: concluida em 11/11/2026 - telas nativas Calendario/Agenda e Tabelas/Rodadas integradas as consultas do host, validadas visualmente e no JAR distribuido. Requisitos mais amplos de avanco, fases e retomada continuam na spec e nao fazem parte deste encerramento.

[Spec de implementação e critérios de aceite](../especificacao/spec-calendarios-ligas-online.md).

Prioridade: Alta para o produto mínimo, conforme orientação de 11/11/2026. Classificações antigas neste documento são históricas.

Registro histórico de implementação em 09/11/2026: a geração seletiva por países escolhidos foi integrada e validada para o requisito vigente naquela data. [Registro](../implementacoes/implementacao-calendario-paises.md). A matriz cobriu países isolados, Argentina e Brasil + Espanha. Em 10/11/2026, o requisito mudou para permitir somente clubes de seis países e gerar essas seis ligas em toda carreira nova. Revisão estática posterior do fonte encontrou a allowlist `[29,97,65,104,3,72]` aplicada ao seletor e à geração; CAL-01 está integrado no código. `startAssets()` também gera competições após ativar os seis IDs, então CAL-02 está integrado no código. Não executei jogo nem testes nesta revisão: fixtures reais das seis ligas, comportamento visual e pacote distribuído permanecem sem validação atual. O registro histórico não comprova esse requisito posterior. Barreiras/avanço de datas sem humanos, consultas e ordenação do calendário, transição de temporada e saves existentes seguem como trabalhos separados.

Revalidação estática do fonte: `CareerEngine.LEAGUE_COUNTRIES` contém `[29,97,65,104,3,72]`; `initializeClubSelection()` ativa esse conjunto antes de criar as competições mostradas no seletor nativo. `NativeClubSelection.open()` usa `a.bV`, cujo catálogo de países vem de `SR.N()`, de modo que o filtro também alcança o seletor original. No início da carreira, `startAssets()` resolve cada asset e rejeita país fora da allowlist, depois volta a ativar os seis IDs e gera as competições. Os caminhos por nome, ID e asset convergem nesse validador do host. A quarta opção brasileira preserva o ramo nativo de estaduais em `a.bv_0.cX()`; a confirmação de como aparece exige execução visual, não feita nesta revisão. Inspeção estática; sem abrir jogo nem rodar testes.

**Regra de interface atualizada pelo usuário (11/11/2026):** os botões online atuais **Pronto** e **Próxima rodada** podem permanecer. Sua existência não constitui pendência de paridade nem exige substituição por controles nativos. Preservar a preparação e os acessos nativos de calendário, reutilizando suas funções e componentes. `NativeClubSelection` embute `a.bV` e `NativePreparation` reutiliza `a.eg`; completar esses acessos sem construir uma tela substituta. A permissão de manter os dois botões não valida automaticamente seu comportamento: autoridade do host, barreiras, sincronização e avanço correto continuam obrigatórios. Esta orientação corrige a interpretação anterior que exigia substituir esses comandos online. Revisão apenas documental.

Confirmação dos IDs do allowlist: `best.n_0.cU()` cria os países com `id=P-index`; `arquivos/default_lang.xml` do `Brasfoot-Online/lib/brasfoot.jar` no ZIP distribuído atual rotula P3=Alemanha, P29=Brasil, P65=Espanha, P72=França, P97=Inglaterra e P104=Itália. Os IDs `[29,97,65,104,3,72]` correspondem, portanto, aos seis nomes exigidos. Leitura do membro ZIP/JAR/XML, sem iniciar o motor.

Data: 6 de novembro de 2026. Escopo: código local, motor original incluído no projeto e carreiras descartáveis. Nenhum save existente foi carregado ou alterado. Esta etapa acrescenta diagnóstico e planejamento; não muda regras do jogo.

Complemento estático de 08/11/2026: [marcação de amistosos na carreira](../especificacao/spec-amistosos-carreira-online.md). O acesso nativo depende de datas futuras do host, ausentes da projeção de uma única data; o modo avulso `--demo` não equivale a agendamento na carreira. Contrato e critérios próprios registrados, sem executar o jogo nem alterar o planejamento de geração de ligas abaixo.

## Resultado principal

No diagnóstico histórico de 09/11, os cenários isolados geraram a liga doméstica do clube e avançaram até janeiro de 2023. Essa evidência não cobre as cinco ligas estrangeiras na mesma carreira. A revisão estática atual do fonte mostra que CAL-02 foi integrado: `startAssets()` ativa os seis países antes de gerar competições. Ainda falta confirmar por execução que fixtures existem para cada uma das seis ligas em uma carreira com clube humano brasileiro. As limitações de navegação/apresentação e avanço de datas permanecem questões separadas.

Os calendários são os do Brasfoot 22–23 local. Esta investigação não compara datas com calendários reais atuais.

## Evidência das simulações

Contagem capturada imediatamente após a criação. A coluna de jogos contabiliza a liga nacional, sem somar copas, estaduais ou continentais. Todos os cenários concluídos processaram 152 datas globais até a primeira troca de ano.

| País / cenário | Clube | Jogos de liga inicialmente visíveis | Datas simuladas sem jogo humano | Resultado |
| --- | --- | ---: | ---: | --- |
| Brasil | Corinthians | 38 | 75 | Avançou a 2023 |
| Espanha | Real Madrid | 38 | 98 | Avançou a 2023 |
| Espanha | Barcelona | 38 | 96 | Avançou a 2023 |
| Inglaterra | Liverpool | 38 | 90 | Avançou a 2023 |
| Alemanha | Bayern de Munique | 34 | 100 | Avançou a 2023 |
| Itália | Juventus | 38 | 98 | Avançou a 2023 |
| França | Paris Saint Germain | 34 | 94 | Avançou a 2023 |
| Argentina | Boca Juniors | 29 | 105 | Avançou a 2023 |
| Brasil + Espanha | Corinthians + Real Madrid | 38 por clube | 67 | Avançou a 2023 |

Em todos esses cenários históricos, havia 43 países com competições geradas; os indicadores de países habilitados continham somente o país escolhido ou os dois da sessão mista. As classificações iniciais retornaram uma tabela por clube estrangeiro e duas para o Corinthians, que também disputa o Paulista. Essas contagens registram geração do país selecionado e não demonstram fixtures nos outros cinco países numa carreira única. Também não certificam os regulamentos: em particular, os 34 jogos da França devem ser confrontados com a configuração local de participantes antes de decidir se há necessidade de atualização da base.

A primeira rodada do diagnóstico usou os nomes `Bayern München` e `Paris Saint-Germain`, ausentes no catálogo local. Essas duas tentativas falharam na resolução do nome, antes de testar suas carreiras. A seleção foi corrigida para os nomes locais `Bayern de Munique` e `Paris Saint Germain`. Não interpretar essas falhas do diagnóstico como erro do calendário ou do seletor por IDs. O script já usa os nomes corrigidos.

## Como reproduzir

Com Java 8 e os artefatos locais de compilação disponíveis:

```powershell
python tools/build_adapter.py
python tools/investigate_calendars.py
```

O diagnóstico compila sua própria classe em `analysis/calendar-investigation/`, cria uma JVM por cenário e usa o mesmo caminho por IDs empregado pela interface gráfica. Registra países habilitados, países com competições já geradas, jogos inicialmente visíveis por competição, tabelas e avanço até a primeira troca de ano. Simula as partidas com `MatchPlayback` e `CareerEngine.nextDate`, sem abrir o servidor HTTP ou clientes gráficos.

Resultados brutos: `analysis/calendar-investigation/results.json` e `case-*.log`; os cenários repetidos com nomes corrigidos estão em `results-additional.json` e `case-additional-*.log`. Também é possível passar nomes locais como argumentos para executar somente alguns cenários. A pasta `analysis/` é ignorada pelo Git; a tabela acima preserva a evidência principal no repositório. Os números de datas sem jogos humanos podem variar com os resultados e a permanência do técnico no clube. Jogos de fases futuras de copas podem ser criados durante a temporada; sua ausência na captura inicial não demonstra perda de partidas.

## Problemas confirmados e limitações

### 1. Histórico superado: países escolhidos eram aplicados depois da geração

Este diagnóstico e as evidências de execução abaixo descrevem uma revisão anterior do fluxo. O HEAD atual foi reestruturado; `create(int[])` e `createByAssets()` reinicializam o mundo antes de `startAssets()`. A ordem antiga permanece apenas como histórico. A revisão estática atual mostra que `startAssets()` habilita os seis países permitidos; a geração das fixtures ainda precisa de validação de execução. Ver CAL-01/02 e a [spec atual](../especificacao/spec-calendarios-ligas-online.md).

Em `src/brasfoot/CareerEngine.java`:

1. `create(File, int[], String[])` chama `initialize(root, true)`.
2. `initialize` marca todos os países de `bF` como habilitados e chama `best.p.dg()`.
3. `dg()` instancia clubes e gera competições; no motor descompilado, `best/p_0.java`, chama `w(bF)`, que cria as ligas de cada país habilitado com `Z.Ar()` e as adiciona a `SR.N()`.
4. Só depois, `start` altera os indicadores de `bF` para os países dos clubes humanos. Não desfaz as competições já criadas.

Evidência de execução: tanto com somente Corinthians quanto com somente Real Madrid, `SR.N()` contém **43 países**, embora `bF` indique apenas um país habilitado. Os indicadores de configuração e o mundo já gerado discordam. Não se deve remover indiscriminadamente todos os clubes estrangeiros: eles também podem ser necessários às competições continentais.

Consequência observada: calendário global inclui muitos jogos sem humanos. No cenário Corinthians foram 75 de 152 datas simuladas; no Real Madrid, 98 de 152. A simulação confirma o excesso de datas, mas não mede seu custo total na interface ou na rede.

Proposta histórica anterior ao requisito atualizado: separar descoberta dos clubes de criação da carreira, resolver IDs/países antes de `dg()` e gerar o mundo uma única vez, mantendo participantes estrangeiros necessários ao motor. A decisão antiga de deixar ligas sem humanos como configuração do host foi superada pelo pedido posterior: carreira nova deve sempre gerar as seis ligas `[29,97,65,104,3,72]`, independentemente dos clubes humanos. Ver CAL-01/02 e a [spec atual](../especificacao/spec-calendarios-ligas-online.md); não tratar essa pergunta histórica como decisão pendente.

### 2. Histórico superado: criação por nome aceitava somente clubes brasileiros

Este diagnóstico não descreve o HEAD atual. `CareerEngine.create(File, String[], String[])` resolve nomes no catálogo e encaminha os assets a `startAssets()`, onde o host rejeita clubes cujo país não pertence à allowlist de seis IDs. A mesma validação é compartilhada pelos caminhos de ID e asset. A verificação é estática e ainda não foi executada; ver CAL-01 e a [spec atual](../especificacao/spec-calendarios-ligas-online.md).

`CareerEngine.create(File, String[], String[])` usa `initialize(root, false)` e exige `candidate.getPais() == 29` na busca. O parâmetro `--clubs` de `SessionHost` utiliza essa sobrecarga. Assim, tentar criar uma carreira estrangeira por esse caminho resulta em `Unknown club`, mesmo que o clube exista no catálogo.

A interface gráfica passa `--club-ids` e usa outra sobrecarga. Portanto, essa restrição explica falhas em CLI e automações, mas não prova que a seleção gráfica estrangeira esteja quebrada.

Implementação proposta: adotar IDs como identidade principal; se mantida a busca por nome, aceitar país e detectar ambiguidades. Cobrir os dois pontos de entrada.

### 3. Datas sem jogo humano ainda dependem da barreira de Pronto — prioridade alta

`RoundCoordinator` inclui todos os técnicos na barreira. `SessionHost.kickoffIfReady()` acelera uma data sem humanos somente depois de chegar a `WAITING_FOR_ENGINE`. Ao terminar, deixa ao host o avanço para a próxima data. `GameAdapter.ready` permite confirmar Pronto sem jogo, mas isso ainda exige a confirmação de todos.

Logo, acelerar a simulação não elimina a sequência de confirmações e avanços em datas que só interessam à IA. Em sessões de países distintos, também é necessário distinguir quem tem jogo e quem está de folga.

Implementação proposta: identificar participantes necessários por data; processar datas exclusivas da IA no host até um ponto relevante de parada. Definir esses pontos antes de automatizar: partidas humanas, decisões de gestão que precisem de intervenção e fim de temporada. Preservar efeitos pós-jogo e checkpoints. Não pular apenas o índice do calendário.

### 4. Calendário e classificação dependem dos jogos já materializados do clube — prioridade média

`CareerViews.snapshot` percorre `SR.R()` e inclui apenas jogos do clube do técnico. Descobre classificações pelas instâncias de `f.s` presentes nesses jogos. Não oferece catálogo de ligas, filtro por país ou visão de outro campeonato.

Efeitos: uma liga fora dos jogos desse clube não pode ser consultada; uma competição sem confronto ainda materializado pode não aparecer. Técnicos sem clube recebem todos os jogos no calendário por causa de `club == null`, mudando silenciosamente o escopo da tela. As classificações também seguem esse percurso global nesse caso.

Implementação proposta: separar consultas de calendário e classificações, listar competições diretamente de seus registros e oferecer filtros por clube, país, competição e temporada. Explicar na tela quando próximos confrontos dependem da definição de uma fase.

### 5. Ordenação de datas e atualização da janela — prioridade média

O calendário envia apenas `day.f()`, uma data formatada, e `NativeCareerDialogs.table` cria um `DefaultTableModel` sem tipo de coluna com ordenação automática. Ao clicar na coluna Data, a ordenação usa os textos e pode colocar `01/12` antes de `02/01`. A ordem inicial vem do calendário nativo; o problema surge na reordenação da tabela.

Complemento estático de 08/11: o mesmo helper constrói as classificações e não sobrescreve getColumnClass. Assim, o sorter não recebe o tipo numérico de posição/pontos/jogos/saldo e pode ordenar pelas representações textuais (por exemplo 1, 10, 2). A ordenação inicial por posição em `CareerViews` é explícita e não comprova a ordenação interativa por coluna. Preservar tipos/comparadores numéricos, sinal do saldo e valores ausentes; acrescentar números de um/dois/três dígitos ao aceite. Sem execução de janela nesta etapa.

Além disso, a janela de carreira é montada com uma consulta pontual a `/overview`; não há atualização periódica dessa tabela enquanto o calendário avança. O usuário precisa reabri-la para obter novo estado.

Implementação proposta: enviar data ISO ou timestamp e identificadores estáveis de partida/competição, mantendo a formatação na apresentação. Usar comparador temporal e atualizar a janela após mudança de estado.

### 6. Cobertura anterior não validava ligas estrangeiras — prioridade alta

`tests/brasfoot/CareerProbe.java` inicia com Corinthians, Palmeiras, Santos e São Paulo pela sobrecarga de nomes. `tools/tests/test_career.py` executa esse cenário por vários anos e retomada. Essa cobertura é útil para o Brasil, mas não comprova o fluxo por IDs nem uma carreira com países distintos.

O diagnóstico novo é exploratório: gera relatórios, registra exceções e limita a duração. Não substitui uma suíte de regressão com expectativas por liga e falha automática quando as invariantes forem violadas.

## Ordem proposta de implementação

Sequência de planejamento anterior ao requisito atualizado e à decomposição CAL-01…CAL-07 abaixo; critérios sobre Argentina ou configuração variável de países estão superados. A política atual é fixa para novas carreiras: somente clubes de `[29,97,65,104,3,72]` podem ser escolhidos e as seis ligas domésticas devem ser geradas em cada carreira.

| Etapa | Entrega | Critério de aceite |
| --- | --- | --- |
| 1 | Corrigir allowlist e geração fixa por IDs | Novas escolhas limitadas a `[29,97,65,104,3,72]`; cada carreira nova gera ligas domésticas desses seis países independentemente dos clubes humanos; participantes continentais necessários continuam disponíveis |
| 2 | Automatizar datas exclusivas da IA e tratar folgas | Sessão não exige Pronto de todos em datas exclusivas da IA; para nos pontos definidos; nenhum jogo ou efeito pós-jogo é duplicado ou perdido |
| 3 | Separar calendário, catálogo de competições e classificações | Técnico consegue consultar sua liga e outras ligas habilitadas; estado sem clube tem escopo explícito; copas indicam fases pendentes |
| 4 | Corrigir datas e atualizar a janela | Ordenação cronológica correta entre meses e anos; resultados e próximas fases aparecem sem reabrir a tela |
| 5 | Ampliar criação e regressão dos seis países | CLI e interface aplicam a mesma allowlist e política fixa; duas temporadas e retomada passam para ligas representativas e sessão mista; ID de país fora do allowlist é rejeitado para clube humano novo |

O plano histórico incluía Argentina como país de geração; isso não vale para a política atual. A matriz alvo deve cobrir os seis países permitidos, uma divisão inferior, carreira humana apenas brasileira com seis ligas, carreira mista e rejeição de país humano externo. Verificar fixtures por competição/rodada/data, confrontos duplicados, conflitos por clube/data, pontuação, progressão de copas, promoção/rebaixamento e consistência após save/reload. Sem usar o cenário argentino histórico como expectativa de nova carreira.

## Limites e decisões para o planejamento

- Manter o calendário nativo do Brasfoot é o escopo inicial. Migrar para temporadas europeias agosto–maio seria uma mudança de regras e exige investigação própria; o avanço em janeiro, isoladamente, não prova um defeito do adaptador.
- Não há evidência suficiente para declarar todos os países disponíveis funcionais ou quebrados. Os cenários exercitam clubes específicos e a primeira troca de ano, sem a cobertura completa de rede, interface, várias temporadas ou retomada estrangeira.
- Corrigir a criação afeta carreiras novas. Saves existentes já contêm o mundo gerado; decidir separadamente entre manter compatibilidade e oferecer migração validada. Não remover competições de saves existentes como efeito incidental da correção.
- Prioridade recomendada: corrigir a inicialização e o fluxo de datas antes de ampliar a interface ou reescrever regras de campeonatos.
### Filas de decisão de seleção nacional no fechamento da data (11/11/2026)

`CareerEngine.completeDate()` termina chamando `SR.q(0)`, que despacha eventos agendados por técnico/data via `best.a.f(0)`. Nessa fila, `cSempregado` chama `best.a.b(true)`, que calcula opções para técnicos humanos elegíveis e tenta abrir `best.s_0.a(...,1)`; o código `cO` despacha `best.a.k()`, que cria o diálogo de convocação para técnicos de seleção. `best.a.q()` agenda `cSempregado` como código 8 para seleções habilitadas e, em condição estadual, agenda também o código 7 que despacha `cO`; não presumir que o evento de convocação seja agendado para toda seleção/data. `best.a.f()` limpa a fila logo após o dispatch e `best.s_0` está neutralizado no host; portanto esses prompts não chegam à UI, não são transferidos à gestão online e não ficam pendentes para retomar do save pós-data. Antes de avançar datas automaticamente, reter e publicar eventos realmente destinados a técnicos humanos, permitir decidir/recusar no fluxo online com persistência/retomada, ou definir política de resolução explícita; não consumir a fila e perder o prompt. Tratar separadamente a troca anual de clube, que usa modo 0 do diálogo, destas ofertas/convocações de seleção (modo 1). Evidência estática em `CareerEngine.completeDate`, `best.f_0.at`, `best.a.q/f/k/b`, `best.ay.k/b`, `best.s_0` e `HostUiAgent`; sem executar o jogo.

Complemento estático de 10/11/2026 — assistência omitida no payload de playback: `MatchPlayback.snapshot()` ignora explicitamente eventos `best.A` cujo tipo é 8 (`src/brasfoot/MatchPlayback.java`, loop de `I.hE()`). No modelo nativo `best.a_0.er()`, o mesmo tipo 8 gera a anotação “(A: nome do assistente)”, que `b.as_0` renderiza porque aceita eventos abaixo de 90 (`analysis/decompiled/best/a_0.java`, `b/as_0.java`). `dq.nc()` fornece a lista completa de eventos do fixture ao renderer do retrospecto. Logo o payload de playback corrente, mesmo quando inclui placar, público/renda, estatísticas e demais eventos, não basta para reconstruir retrospecto fiel: incluir tipo 8 no DTO histórico por fixture. Não alterar a lista do playback ao vivo sem avaliar seu contrato próprio; este achado é específico da consulta retrospectiva. Leitura estática, sem execução de janela.

## Decomposição atual das tarefas do pedido — revisão estática 10/11/2026

A sequência abaixo atualiza a ordem genérica acima e substitui Argentina como alvo de nova seleção. Os IDs são rótulos de acompanhamento deste documento, sem representar tickets externos.

| ID | Trabalho | Estado estático no fonte/pacote | Critério verificável |
| --- | --- | --- | --- |
| CAL-01 Catálogo permitido | Integrado no fonte; não executado nesta revisão. `initializeClubSelection()` restringe `SR.N()` aos seis IDs; `startAssets()` rejeita país não permitido para nomes, IDs e assets | Validar visualmente país→divisão→clube nos seis países e rejeição no host para cada caminho de entrada (incluindo chamada direta/CLI). Preservar nacionalidades de técnicos, clubes IA e regra nativa da quarta opção brasileira para estaduais. |
| CAL-02 Geração em carreira nova | Integrado no fonte, fixtures não revalidadas nesta revisão. `startAssets()` ativa `[29,97,65,104,3,72]` e chama `generateCompetitions()` independentemente dos países humanos | Validar carreira com clube humano apenas brasileiro e confirmar ligas domésticas e fixtures nos seis países; conferir combinações de técnicos e nenhuma liga doméstica nova fora do allowlist. No Brasil, preservar a quarta divisão/estaduais conforme `isJogaEstadual`; contar ligas nacionais (tipo 1, datas tipo 1) separadamente de estaduais (tipo 3, datas tipo 3), sem considerar estaduais como substitutos das seis ligas. Auditar à parte os gates de Copa do Brasil/regionais (`bk`, `isJogaRegionais`, `isNovoFormatoCopa`) e não inferir presença/fases por flags sem conferir fixtures. Confirmar que clubes deslocados para estaduais permanecem em `SR.P()` e profissionais em `SR.O()`; não retirar clubes IA nem alterar saves legados. |
| CAL-03 Avanço integrado | Parcial: todos os técnicos ainda confirmam Pronto em PREPARING e todos confirmam o intervalo, mesmo sem fixture próprio. O nativo `IgnoraLigas` avalia tabelas anexadas à data e participantes da tabela-base em playoffs `f.z`; não equivale a `hasMatch=false` nem ao conjunto de técnicos com partida naquele dia. `CareerEngine.completeDate` não chama esse caminho nativo | Aceite online da spec: datas só de IA iniciam/fecham sem confirmação de técnicos não participantes; em datas mistas e no intervalo, aguardam apenas técnicos com fixture e permitem seus ajustes. Comparar esse comportamento com a preferência `IgnoraLigas` e decidir se ela altera a regra online; não presumir equivalência. Confirmar fixtures, hooks pós-data, checkpoints e retomada uma vez, sem travar em preparação/intervalo. Não remover fixtures nacionais do playback por causa da preferência de ocultação. |
| CAL-04 Agenda por clube | Parcial: `/club-agenda` consulta por `clubId` e envia data/status, mas DTO/tabela remotos não conservam identidade por linha nem reproduzem `a.L` | Comparar clube humano e IA fora da data corrente por IDs; fornecer identidade durável de fixture (ID próprio, se disponível, ou chave composta competição/data/mandante/visitante validada como única); completar ou indicar indisponibilidade para sete colunas, escudos, placar normal/pênaltis, público/renda; implementar filtro de competição, seleção do próximo jogo e atualização após avanço. Preservar amistosos/copas reais na agenda do clube. |
| CAL-05 Tabelas e rodadas | Pendente para navegação de qualquer liga: `/overview` filtra tabelas pelas partidas do clube do técnico, enquanto o seletor nativo parte das competições anexadas às datas globais e acrescenta objetos por caminhos explícitos (regionais, Copa do Brasil e competições internacionais, conforme categoria/flags). Assim, item de catálogo e fase disponível não significam necessariamente fixture já materializada; a extração online também achata `f.s.yQ()` e pode repetir posições por grupo. | Selecionar competição/grupo/fase por identidade estável sem depender do clube autenticado; consultar fixtures e tabela autoritativos para qualquer país/competição existente nos seis países, mantendo grupos separados, ordem e IDs dos participantes nativos. Transportar separadamente a data global, o ordinal da rodada na tabela/fase e a chave do fixture: `best.I.hM()` é índice de data, não rodada, e `I` não possui ID nativo próprio. Cobrir o formato especial 68/8: só 64 participantes entram nos grupos. Hoje, se outro clube faz a liga aparecer em `/overview`, os quatro restantes podem surgir com `f.s.Q(club) == -1` e estatísticas zeradas; se o clube humano estiver entre eles, `/overview` pode omitir a tabela porque seus jogos são da chave `f.z`. A consulta global deve manter os 64 classificados nas linhas de grupo e representar à parte os demais participantes e suas chaves/fixtures. Acompanhar efeitos de fim de época/descenso sem pressupor que esses quatro sejam automaticamente rebaixados ou imunes. Se a chave selecionada não existir, mostrar indisponibilidade/estado vazio explícito, sem fallback silencioso para a primeira competição. Não incluir amistosos como liga nem inventar fixture/fase não sorteada; distinguir objetos listados, fases pendentes e partidas materializadas. Saves legados consultam somente competições já existentes. |
| CAL-06 Temporada e compatibilidade | Caminho estático compatível, aceite multiépoca pendente: `CareerEngine.nextDate()` chama `SR.V()` no fim do calendário; o ramo anual `f_0.aa()` percorre os países já presentes em `SR.N()`, chama `Z.ee()` e regenera as rodadas das tabelas domésticas existentes | Avançar a virada de temporada em carreira de clube brasileiro e confirmar fixtures/tabelas dos seis países sem duplicação; conferir promoção/rebaixamento, sessão mista, checkpoint/retomada e preservar saves legados sem regenerar ou apagar competições existentes. A leitura estática não prova a composição resultante nem round-trip. |
| CAL-07 Artefato | ZIP atual de 80.291.987 bytes e JAR interno de 395.713 bytes conferidos estaticamente; `CareerEngine`, `NativeClubSelection`, `NativeTransferSearch` e `NativePlayerSearch` no JAR correspondem byte a byte a `build/classes` | Hashes ZIP/JAR e classes relevantes anotados na revisão de 10/11. Isso confirma correspondência do pacote com a build nesses componentes, não fixtures nem comportamento visual/runtime. Verificar pelo launcher quando a validação de execução for autorizada. |

Evidência adicional para CAL-03: `NativeClient.render` mostra “Sem partida nesta data. Confirme Pronto para aguardar os jogos dos demais clubes” em `PREPARING`; `RoundCoordinator.ready` só inicia quando todos os coaches estão prontos. `SessionHost.startPlayback` evolui a data e pula o intervalo quando não há jogo humano, mas isso ocorre depois da barreira inicial. Portanto o código automatiza o relógio durante playback sem resolver a confirmação inicial do técnico sem jogo. A confirmação manual é comportamento atual observado no código, não recomendação de produto. Fontes: `NativeClient`, `RoundCoordinator.ready`, `SessionHost.startPlayback/tick`, `MatchPlayback.begin`; leitura estática.

Distinguidores de aceite: (a) flags de países ativos e tabelas geradas, (b) fixtures por país/competição/data, (c) capacidade de avançar a carreira, e (d) possibilidade de navegar e ver os dados em UI. Uma dessas evidências não substitui as outras. O código-fonte satisfaz estaticamente os passos de CAL-01/02; a revisão do pacote distribuído e a validação de execução continuam pendentes. Ver [spec dos seis calendários](../especificacao/spec-calendarios-ligas-online.md).
Precisão do aceite CAL-02: divisões domésticas são tipo 1 e suas rodadas são ligadas a índices tipo 1 da lista global `SR.R()`; `best.I` registra cada fixture naquela data global. Os seis países devem ser conferidos por competição/rodada/data e por clube/data. Não esperar seis sequências independentes nem inferir ausência de conflito de liga/copa a partir do compartilhamento da linha do tempo. A nova ativação aumenta os jogos agrupados em datas globais; confirmar também folgas e fixtures de copas/seleções sem sobreposição por clube.
Fronteira funcional da allowlist: os seis IDs limitam seleção de clubes e geração de ligas domésticas novas; não são uma allowlist para seleções de técnicos/jogadores nem para remover clubes IA do mundo. `best.p_0.dg()` trata ligas domésticas e geração de torneios continentais/seleções por flags distintos. Ao validar CAL-01/02, verificar que a filtragem não desliga copas internacionais ou seleções previstas pelas opções da sessão; listar esses torneios separadamente das seis ligas domésticas.

Revisão estática complementar (10/11/2026): distingui a matriz histórica de geração seletiva do requisito posterior de seis ligas simultâneas em toda carreira nova. A fonte agora aplica os seis IDs no seletor e na geração, e `CareerCountrySelectionTest` contém expectativas para esse conjunto; fixtures globais em carreira só brasileira ainda precisam de execução para confirmação. CAL-01/02 refletem integração estática, não aceite concluído. Nenhum jogo, motor, teste ou build foi iniciado nesta revisão.

Revisão estática CAL-04/05 (10/11/2026): `HR` em Times já substitui o listener e consulta `/club-agenda` pelo `clubId` visitado; o DTO envia `date/dateMillis/status`, mas não IDs por fixture/competição/clubes, e a janela remota continua resumida a seis colunas. Corrigi diagnósticos que ainda descreviam HR como consulta local ou `/overview` e corrigi o resumo de Agenda que dizia faltar data/status. A Agenda do dashboard segue `/overview` do técnico autenticado; Tabelas/Rodadas globais seguem sem seletor autoritativo por competição/fase. Links relativos verificados; nenhuma janela, motor, teste ou build foi executado.



### Per-date volume at playback kickoff - static inspection 10/11/2026

`GameAdapter.currentMatches()` passes the complete set of date fixtures to `MatchPlayback`. `SessionHost.kickoffIfReady()` calls `begin()` once; this method runs `best.I.V()` for every game and immediately finishes those that do not involve a human club. Only human matches receive minute-by-minute advancement. Enabling the six leagues can increase the AI batch at kickoff on some dates, but does not create a clock per AI game. The code does not establish duration, hang risk, or an operational limit. Measure `begin()` separately from synchronization wait and human playback when validating CAL-02/CAL-03; count fixtures per date and verify each fixture and date aggregation runs once. No execution in this review. See the [calendar specification](../especificacao/spec-calendarios-ligas-online.md#synchronous-scope-at-date-kickoff-static-reading).


### Auditoria estática da cobertura de fixtures e conflitos - 10/11/2026

A leitura estática dos testes mostra que `CareerCountrySelectionTest` foi atualizado para exigir os seis países, testar Argentina recusada e caminhos por asset/nome/ID; essa cobertura não foi executada nesta revisão e ainda deve ser conferida quanto a fixtures globais por país e invariantes clube/data. `tools/tests/test_career_country_selection.py` verifica que cada técnico tem partida atual ou próxima, sem inventariar todos os jogos de cada data. `tools/investigate_calendars.py` registra partidas por competição no calendário do técnico e avança datas, mas não coleta uma chave global de fixture nem rejeita um clube repetido na mesma data. Nenhum desses arquivos, sem execução, comprova ausência de conflito ou fixtures das seis ligas numa carreira com técnico só brasileiro.

Para o aceite, a coleta futura deve percorrer cada data em `SR.R()` e registrar ID da competição, tipo, ID da data e IDs dos dois clubes; conferir identidade/unicidade de fixture, no máximo uma partida por clube/data, as seis ligas nacionais tipo 1 e a separação de estaduais/copas/seleções. Exigir fixture existente por liga e rodada, não somente país habilitado ou tabela presente. Esta conclusão vem de leitura de código/testes; nada foi executado. A distinção foi confirmada no próprio `tools/investigate_calendars.py`: `enabledCountries` são flags `O.iC()`, `calendarDays` é apenas a quantidade de datas, e `views` agrega rótulos de competição vindos de `CareerViews.snapshot(coach)` para clubes humanos. O laço percorre `game.currentMatches()` somente para preparar suspensões/elegibilidade antes de simular; não serializa as partidas globais para o relatório. Assim, o número de datas, flags e resumo do técnico não certificam identidade, rodada ou conflito de nenhum fixture.


### CAL-03: distinguir data de IA de data sem fixtures

Precisão estática (10/11/2026): `RoundCoordinator.ready()` só passa a `WAITING_FOR_ENGINE` quando todos os técnicos cadastrados estão prontos; a rota não verifica se cada técnico tem fixture nessa data. `NativeClient` mostra instrução de Pronto quando `own(snapshot).hasMatch` é falso. Depois da barreira, `SessionHost.kickoffIfReady()` chama `MatchPlayback.begin()` para `game.currentMatches()`; se a lista estiver vazia, `begin()` lança `No matches on current date`, e o host registra a fase como `FAILED`. São casos distintos: data com partidas de IA, mas sem jogo humano, deve avançar sem a confirmação de técnicos ausentes; data realmente vazia precisa de política de salto/fechamento que preserve hooks/checkpoint e não deve chamar o caminho normal de playback. A leitura não prova que o calendário gerado tenha datas vazias. Critério de aceite: cobrir ambos os estados, datas mistas e intervalo, verificar um fechamento/avanço por data e provar que nenhum técnico sem fixture bloqueia a sessão. Fontes: `NativeClient.render`, `RoundCoordinator.ready/halftimeReady`, `SessionHost.kickoffIfReady/tick`, `GameAdapter.currentMatches`, `MatchPlayback.begin/tick`; sem executar carreira/testes nesta revisão.

### CAL-05: classificação geral versus vagas de fase seguinte

Complemento estático de 10/11/2026: em `f.s.yP()`, uma liga com grupos e `classificaPeloGeral=false` seleciona os primeiros `Zd` participantes de cada `G.gR()` para montar a fase seguinte (com exceções específicas por tipo/formato e pela opção de melhores terceiros). Quando a flag está ativa, esse ramo por grupos é ignorado: a rotina seleciona os primeiros `Zd` de `Zb` e aplica o arranjo de chave correspondente ao tamanho/formato. `f.s.yR()` também calcula quantidade/classificação (`Zd` ou `Zd * Zj`, com acréscimos condicionais de melhores terceiros), portanto não é seguro derivar vagas multiplicando posições sem ler o regulamento efetivo. A tabela de classificação, a posição local no grupo, a ordem geral e a seleção de vagas são dados distintos. CAL-05 deve manter os resultados nativos em separado; o teste de formato agrupado deve comparar linhas exibidas e classificados da fase seguinte para `classificaPeloGeral` ligado/desligado, sem substituir a seleção do motor por uma ordenação do cliente. Leitura de `f.s.yP/yR/zy`, `f.s.zl`, `ay_0.i`; somente código, sem executar geração ou jogo.

Precisão adicional das zonas de tabela (10/11/2026): `ay_0.o(s)` divide `getnRebaixados()` pelo número de grupos quando `s.zc()` é verdadeiro, e só então marca os últimos clubes de cada grupo; a divisão inteira pode deixar vagas residuais sem marcação. Aplica também o limite `zh()` quando `zg()` está ativo. Sem grupos, exclui do conjunto de rebaixados os clubes já contidos em `wK`; em liga doméstica, `Bw()` sobrepõe outra zona a parte dos rebaixados. Na renderização, `ay_0.a(e)` grava um código singular em `components.e.dF` e aplica precedência entre classificação, rebaixamento e listas de zonas, com códigos compostos nas sobreposições. Aceite CAL-05 deve comparar os códigos/indicadores por `clubId` com o host em formatos agrupados e não agrupados, incluindo total de rebaixados não divisível pelos grupos e sobreposições. Não presumir que as ligas nacionais atuais usem grupos nem implementar o cálculo online por faixas genéricas. Fonte estática: `a.ay_0.o/a`, `f.s.getnRebaixados/zc/zg/zh/Bw`, `components.e.dF`; sem executar jogo.

### Playoffs de divisão na agenda global

Complemento estático de 10/11/2026: `f.b_0` monta playoffs de acesso/rebaixamento como chaves `f.z` (1099/1098); `f.g_0.b()` agenda suas partidas pela mesma `best.a.a(tipo,false)` usada no calendário, com tipo 1. Ida e eventual volta usam os índices retornados para datas globais tipo 1. Essas partidas pertencem à fase eliminatória, não à tabela regular `f.s`; CAL-04 deve listá-las na agenda do clube com a identidade/fase própria e CAL-05 deve consultá-las sob a chave selecionada, sem incorporá-las à classificação da liga. Fontes: `f.b_0.BQ/BP/a`, `f.g_0.b`, `best.a.a`, `best.I`; somente estático.

Precisão sobre fase pendente: `f.b_0.mC()` anuncia os playoffs de acesso (`BK`) e de rebaixamento (`BM`) mesmo antes das chaves `agA`/`agB` serem materializadas; nesse momento cada opção recebe `[null]`. `ay_0.cN/nm/nn()` limpa a classificação corrente e exibe “Ainda não definido”. CAL-05 deve modelar cada opção como fase pendente, sem inventar confronto; após `BQ()` ou `BP()/a(b_0)` montar a chave, a identidade da fase aponta para os fixtures reais. Fontes estáticas: `f.b_0.BK/BQ/BM/BP/a/mC`, `components.y`, `a.ay_0.cN/nm/nn`.

Não enumerar fases de uma divisão apenas por `mB()`: ele retorna somente um objeto, priorizando `agB` > `agA` e, sem essas chaves, segue os fallbacks próprios de tabela/fase de `YL` (inclusive formatos especiais). `mC()` pode listar os dois playoffs separadamente quando as regras `BK/BM` estão ativas. `ay_0.cC()` abre inicialmente a referência `mB()[0]` e seleciona a mesma referência dentro do combo `mC()`; portanto, com ambas as chaves materializadas, inicia em `agB` (rebaixamento), mas mantém `agA` selecionável. O aceite de CAL-05 deve conferir esse padrão e garantir que o catálogo/acesso por identidade não omita o playoff de acesso quando `mB()` expõe o de rebaixamento. Fontes: `f.b_0.mB/mC`, `a.ay_0.cC`; somente estático.

Vazamento estático de Rodadas ao trocar fase: `nu()` só popula `vU` quando vazia; `i(s)` limpa a lista para tabela `f.s`, mas `nm()` não a limpa ao abrir uma chave `f.z` e `nn()` também não a zera ao mostrar “Ainda não definido”. A aba Estatísticas chama `nu()` sem validar a identidade cacheada contra `wV`, logo após abrir Rodadas numa liga, selecionar playoff/fase pendente e voltar à aba, a lista antiga pode permanecer. As listas de marcadores não exibem esse mesmo caminho: `i(s)` chama `j(s)`, que limpa e recalcula as zonas antes de renderizar classificação; o achado fica restrito ao cache de Rodadas. CAL-05 deve limpar/recarregar Rodadas por identidade da fase e cobrir tabela→`agA`, tabela→`agB`, troca de chaves e chave→`[null]`→retorno, além de conferir as zonas ao retornar a `f.s`. Leitura estática de `a.ay_0.nu/i/nm/a(z)/nn/cO/j(s)`, `vU/wV/vN`; nenhuma janela foi executada.

Limite condicional da classificação em grupos: a lógica `f.s` reconhece `Zj==17`, mas `ay_0.i(s)` tem apenas rótulos A–P e, se `wA` estiver ativo, tenta indexar um rótulo por grupo sem guarda. Assim, 17 grupos após `wA=true` podem exceder o array; `wA` é ativado nos tipos 4/6/7/9 e persiste ao trocar de competição. A leitura prova o limite, não que uma competição dos seis países use esse formato. Verificar os `nGrupos` efetivos antes de afirmar exposição; se houver 17 grupos no catálogo alvo, CAL-05 deve cobrir abertura direta e após competição que ativa `wA`, sem falha nem rótulo duplicado. Fontes: `f.s.yO/yR`, `a.ay_0.i/wA`; estático.

## Evidência visual enviada pelo usuário — 11/11/2026

As imagens `Calendario-nativo-vazia.png` e `Tabela-nativa-vazia.png` mostram, respectivamente, o Calendário do Augsburg sem linhas apesar do próximo confronto indicado na preparação e a janela Tabelas com “Alemanha - Bundesliga” selecionada, mas sem classificação nem rodadas. As capturas confirmam o sintoma visual; não identificam sozinhas a causa nem provam ausência de fixtures no host. Aceite do produto mínimo: os painéis nativos de Agenda/Calendário e Tabelas/Rodadas exibem os fixtures, a classificação e as rodadas reais da carreira, inclusive das seis ligas habilitadas. Os botões online Pronto e Próxima rodada podem permanecer.

## Implementação e validação do recorte de duas telas — 11/11/2026

O acesso nativo HR abre o Calendário original `a.L`; HS abre a tela original `a.ay`. O cliente consulta `/calendar-catalog` no host a cada abertura, com revisão do snapshot, fixtures globais, classificações das seis ligas habilitadas, participantes e rodadas agrupadas por data. A projeção preserva os IDs/referências nativos para que o filtro da Agenda e o seletor de competição e Rodadas usem os componentes do Brasfoot. O diálogo Tabelas foi ampliado para expor integralmente o controle Rodadas que ficava cortado.

Validação gráfica registrada em [implementação](../implementacoes/implementacao-calendario-tabelas-nativas.md): carreira nova apenas com Corinthians; fixtures em seis países; 75 datas de calendário; 53 jogos do Corinthians em 53 datas; Calendário nativo com as datas distintas; Bundesliga com 18 participantes; acesso ao controle Rodadas e tabela nativa de jogos com fixtures. O teste `NativeCareerCalendarViewsTest` passou e gerou capturas. Reabrir as janelas consulta uma revisão nova; retomada/avanço foram conectados pelo mesmo caminho, mas não exercitados neste teste visual.

Permanece fora deste recorte e aberto na spec: retrospecto detalhado por duplo clique, fases eliminatórias/placeholders, códigos visuais de zonas comparados ao host, dados após avanço/retomada em execução, automação de datas só de IA, validação multitemporada e saves legados. Não declarar a spec inteira concluída com base nesta entrega.
