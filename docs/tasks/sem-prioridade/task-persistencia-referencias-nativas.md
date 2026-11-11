# Referências nativas após salvar e retomar

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: implementação parcial integrada em `main` no commit `a8d399b5`, aceite global e compatibilidade legada abertos. Complementa PN-12 (disciplina) e PN-11 (histórico); não revoga as validações anteriores de outros campos Kryo.

Contrato: [spec de referências nativas](../../especificacao/spec-persistencia-referencias-nativas.md), com identidade estável, preparação/reconexão, checkpoint, compatibilidade e critérios em nova JVM. A fatia implementada e suas evidências estão no [registro](../../implementacoes/implementacao-persistencia-referencias.md).

Evidência estática preservada no repositório: [parser, extrações e hashes](../../evidencias/persistencia-referencias-2026-11-09/README.md), sem depender exclusivamente da pasta temporária. Não se trata de prova de execução em JVM.

## Implementação integrada e alcance — revisão estática de 10/11/2026

O registro [implementação parcial](../../implementacoes/implementacao-persistencia-referencias.md) documenta o commit e os testes executados pelo implementador. Neste turno conferi o diff e os consumidores no código, sem executar JVM, testes ou jogo:

- `GameAdapter.saveLocal` chama `NativeReferencePersistence.prepare` antes do Kryo; `readLocalSave` restaura os globais e chama `reconnect` antes de construir o adaptador.
- `prepare` atribui IDs às competições sem ID na agenda, rejeita IDs duplicados, copia identidade/categoria para `H` e `ap`, sem habilitar serializeTransient global nem alterar IDs públicos dos atletas.
- `reconnect` religa suspensões à competição canônica por ID e registros `ap` ao profissional/júnior indexado por categoria e ID. Duplicatas encontradas no estado carregado produzem erro; a integração converte essa falha em `IOException`.
- `NativeReferencePersistenceTest` declara round-trip em duas JVMs, repetido duas vezes, cobrindo contadores/bloqueio, referência canônica da competição, histórico do atleta e estabilidade do ID. O relatório de implementação afirma que esse teste, a suíte do adaptador, `test_session.py` e o ZIP empacotado passaram; isso é evidência registrada pelo implementador, não repetição independente nesta revisão.

Continuam abertos os critérios de legados sem ID, referências não resolvidas, atletas removidos/aposentados, duas competições homônimas, múltiplas temporadas, H usado por avaliações/rankings, visões históricas reais e retomada interrompida. `reconnect` não rejeita explicitamente cada referência individual sem correspondência: a política de compatibilidade e a visibilidade dessas lacunas precisam permanecer documentadas. Não classificar a task inteira como resolvida pelo round-trip sintético.

Consulta de campeões relacionada, rastreada em 10/11/2026: `best.i_0` (em `at.mn`) persiste temporada, IDs do campeão/vice e seus técnicos; para o artilheiro da competição guarda nome, ID do clube e posição (`analysis/decompiled/best/i_0.java:20–120`). Esses vínculos não fazem parte do caso atual de `NativeReferencePersistence`, pois são campos escalares persistidos e não referências transitórias; a lacuna é transportá-los com o catálogo/histórico `mn` e manter a apresentação quando uma entidade já não estiver no catálogo corrente. A spec [de consultas históricas](../../especificacao/spec-consultas-historicas-online.md) descreve esse contrato. Não ampliar `reconnect` ou o serializer para esses IDs sem necessidade: validar a projeção histórica separadamente.

Galerias de títulos, também rastreadas em 10/11: `best.ao` nas listas `ah.cT()`/`al.cT()` persiste temporada, ID de clube e tipo/subcódigo, mas mantém somente uma referência `transient` à competição e não um ID persistente dela (`best/ao.java:9–50`). Os hooks nativos `g.b.Ag/Af` e o `NativeReferencePersistence` atual não percorrem essas listas; depois do load, os contadores podem existir sem o objeto necessário para nomear/identificar a competição. Esse caso não é corrigido por transportar `i_0.mn` e não deve ser “reparado” associando pelo tipo/subcódigo. Definir identidade estável para o troféu ou representar explicitamente o vínculo não resolvido, com migração e aceite próprio. Ver [spec PN-11](../../especificacao/spec-consultas-historicas-online.md); nenhum save com título foi exercitado.

## Diferença entre os fluxos de gravação

O salvamento nativo `c.a.ae` chama `g.b.Ag()` e `Ai()` antes de escrever os dois objetos Kryo. O carregamento nativo chama `Ah()`, `Af()` e `Aj()` depois de ler (`analysis/decompiled/c/a.java:102–109,180–185`). São preparação/reconexão de referências, não apenas navegação Swing.

Diagnóstico histórico anterior ao commit `a8d399b5`: `GameAdapter.saveLocal` apenas instanciava Kryo e escrevia estado/afQ; `readLocalSave` lia, chamava `loaded.G()` e restabelecia globais/Pronto sem reconectar H/ap (`src/brasfoot/GameAdapter.java`, métodos saveLocal/readLocalSave; `best/f_0.java:330–335`). A omissão foi corrigida parcialmente pela implementação datada acima; os limites restantes não são prova de que a correção esteja ausente.

## Disciplina por competição

`best.H` (`best/h_0.java`) possui referência transient Y para a competição e ID persistente ft. Seu construtor guarda Y, mas deixa ft=-1. Ag enumera competições, atribui IDs e chama H.gY para copiar o ID da referência; Af reconecta H.m(competição) pelo ID após carregar (`g/b.java:16–82`).

O consumidor `F.c(at)` consulta H por identidade da referência: `F.h(at)` só encontra registro quando H.gS()==at; caso contrário cria outro H, inicialmente sem cartões (`best/F.java:1170–1173,2087–2098`). Antes do commit `a8d399b5`, a omissão de preparação/reconexão podia deixar os contadores serializados sem associação à competição corrente e afetar a elegibilidade após retomada. O hook atual reconecta quando encontra competição com o ID salvo; continuam sem contrato os registros sem ID, sem competição correspondente e contextos homônimos/temporadas distintas. Não houve suspensão real executada/recarregada nesta revisão.

Revisão estática do caminho de falha em 10/11: `prepare()` numera somente objetos de competição encontrados em `state.R()` e atualiza cada H apenas se sua referência Y aponta a uma competição com ID válido; se Y existir sem ID, grava `ft=-1`, e se Y for nulo mantém o valor existente (`NativeReferencePersistence.java:18–30`). `reconnect()` indexa somente as competições da agenda e, para H cujo `gX()` não encontra correspondência, simplesmente não chama `m()` nem reporta erro (`:32–42`). O carregamento do save, portanto, não garante a integridade dessas relações apesar de ter completado `reconnect()`: consultas por `F.g(at)` retornam null e elegibilidade por H pode criar um registro vazio via `F.h(at)`. Isso afeta contadores disciplinares e candidatos/avaliações de ranking de formas distintas. Definir política explícita para H órfão (rejeitar/avisar/preservar como dado histórico destacado), inclusive competição retirada da agenda; evidência do fluxo, sem reproduzir save órfão.

Essa pendência é do estado autoritativo persistido; é distinta de projetar corretamente a suspensão no cliente. Corrigir VisualModel não recompõe uma referência já perdida no host.

O alcance de H não é apenas disciplinar: o registro também guarda contadores e soma/quantidade de avaliações (`H.i/F/gZ`). `at.mz` usa `F.g(this)` e essas avaliações para montar os melhores da competição; `F.g` também compara H.gS por identidade e retorna null quando não encontra. Assim, a mesma falta de reconexão pode retirar candidatos do ranking após retomada, mesmo com números escalares preservados. É consequência estática do consumidor, sem galeria/ranking executado nesta investigação.

## Histórico de transferências

`best.ap` tem referência transient U para F e campos persistentes ei/ej. O setter a(F) apenas guarda U. No fluxo nativo, Ai atribui IDs/tipos aos profissionais e juniores e chama ap.mb() para copiar a identidade; Aj chama ap.me() para reencontrar o atleta (`g/b.java:102–130`; `best/ap.java:18–23,39–45,77–119`).

Antes de `a8d399b5`, o adaptador não executava Ai/Aj. No HEAD atual, `prepare` chama `ap.mb()` antes do save e `reconnect` religa registros de transferência ao atleta profissional/júnior encontrado pelo ID persistido e categoria. Se o registro não tiver ID utilizável ou o atleta não estiver nas listas restauradas, não é reconectado; `a.hc` filtra transações por `ap.x()!=null`, então a consulta histórica pode omitir esse registro após retomada. A aposentadoria normal retira o atleta de `SR.O()` e arquiva apenas nome/gols/jogos, sem ID (`best/F.java:1711–1746`; `best/b_0.java:16–27`), tornando impossível o lookup atual por `ap.me()` depois de carregar esse estado. Mais adiante, `prepare` chama `ap.mb()` mesmo com `U==null`; esse método grava `ei=-1` e apaga o ID salvo não resolvido (`best/ap.java:77–86`). Assim uma segunda gravação após a primeira retomada pode descartar definitivamente essa identidade. O teste registrado usa relações válidas; aposentadoria/round-trip não foi exercitado. Não afirmar perda do saldo ou da movimentação de elenco: são campos e consumidores diferentes.

Requisito derivado: ao não resolver `ap`, preservar sua identidade persistida e reter um snapshot de apresentação (nome do atleta e IDs/nome já gravados da transação) ou expor explicitamente a lacuna. Não sobrescrever a referência com `-1` durante save; não associar por nome/homônimo nem inventar jogador ativo. Definir a solução compatível com o formato legado e exercitar aposentadoria antes/depois de duas retomadas.

## Evidência estática no Kryo distribuído

Leitura de ZIP/class com Python, sem carregar Java: `FieldSerializerConfig.<init>` escreve serializeTransient=false nos offsets 25–26; copyTransient=true é outra configuração e não habilita gravação. O construtor de Kryo cria essa configuração, FieldSerializer a clona e seus métodos write/read consultam isSerializeTransient antes do bloco de campos transitórios. O adaptador não instala serializer específico nem altera essa configuração.

Artefatos inspecionados:

- Nativo `analysis/brasfoot.jar` e `dist/lib/brasfoot.jar`: SHA-256 `096c9b3d2f6b4cb1feb220ec1b6d3c6f150df1eac92a17546cc804e35d4a5d0d`.
- `dist/lib/kryo-4.0.2.jar`: SHA-256 `f32aceb5b2eb39be6f339da722c3bb043df163c38d51522acbb77ee4d6c4d362`.
- Parser e extrações: `C:/Users/Julio/AppData/Local/Temp/brasfoot-static-persistence-xndt87va`, arquivos classread.py, serializer-config.json, Kryo.json e FieldSerializer.json. São evidências de leitura binária, não resultados de roundtrip.

`CareerProbe.stableSnapshot` remove eligible do comparativo antes/depois e o snapshot não contém os registros de histórico; seu sucesso não certifica essas relações. Os probes anteriores de obra, treino e empréstimo continuam válidos no escopo exercitado. Não foram repetidos agora.

## Requisitos e aceite pendentes

### Não tratar todo campo transient como a mesma falha

F.eo e al.nV são referências de clube com IDs bW e getters que buscam o clube quando a referência está nula (`best/F.java:220–242`; `best/al.java:67–81`). k.cg também possui reconstrução em seu consumidor. B.dH, referência do projeto de estádio, não é transient, compatível com a validação anterior de identidade da obra. H.gS e ap.x apenas retornam a referência: os reparos ficam nos hooks descritos acima. Essa diferença delimita o achado.

Conferência estática complementar: a galeria `a.ay` verifica `at.mD/mE` e chama `mz/my` para reconstruir caches tV/tW ausentes (`a/ay_0.java:382–393`). A perda do cache não equivale à perda dos registros de origem; reconstruir tV depende de H corretamente reconectado. Em `I.q(int,int)`, go nulo gera novo motor `new b(this)` (`best/I.java:998–1006`). O host já define recuperação de partida interrompida a partir da preparação no manifesto; não exigir serializar esse motor como se a retomada prometesse continuar o mesmo minuto. Estes rastreamentos não certificam todos os consumidores de cache/motor e não comprovam uma exceção por campo nulo.

Não habilitar serialização global de transient como solução genérica: isso pode incluir caches, componentes e estado de execução que o fluxo nativo deliberadamente recompõe ou descarta.

Preparar e reconectar as referências necessárias, preservando IDs estáveis do protocolo online. Não copiar Ai indiscriminadamente: ele renumera jogadores pela ordem das listas e isso pode invalidar ofertas/checkpoints/comandos que usam IDs persistentes. Separar identidade de jogador de índice de serialização e manter vínculos de competição, inclusive nomes iguais e mudança de temporada.

Aceite futuro: suspensão por cartões com bloqueio antes/depois de duas retomadas; duas competições distintas; estatística H de cada contexto; transferência com histórico consultável após outra venda e retomada; profissional/junior/aposentado quando aplicável; oferta pendente mantendo jogador correto; IDs e referências consistentes em nova JVM. Verificar manifesto/save coerentes e rejeição de recuperação inválida. Nenhum desses cenários foi executado nesta etapa.

Relacionados: [disponibilidade](../../especificacao/spec-disponibilidade-jogadores-online.md), [consultas históricas](../../especificacao/spec-consultas-historicas-online.md), [paridade](task-paridade-nativa.md) e [mapa](../../mapa-implementacao.md). Preparação/reconexão básica integrada; aceite dos cenários restantes e compatibilidade dinâmica pendentes.

## Implementação parcial — 09/11/2026

`NativeReferencePersistence` prepara antes do save os IDs de competições que ainda não tinham identidade, associa `H` ao ID da competição e grava em `ap` o ID e a categoria profissional/júnior do atleta. No load, depois de restaurar os globais, reconecta `H` à instância canônica da agenda e `ap` ao jogador correspondente. Não ativa serialização global de campos transient e não renumera IDs públicos de jogadores.

`NativeReferencePersistenceTest` criou carreira pelo motor, adicionou registro disciplinar e histórico de transferência de fixture, salvou e recarregou em dois processos JVM independentes. Passou preservando competição, contadores, bloqueio disciplinar e identidade do atleta. O caso está em `python tools/build_adapter.py --test`. Permanecem fora desta fatia: registros legados sem ID, jogadores removidos/aposentados, integração com a galeria/rankings nativos e troca de temporada com novas competições. Esta task permanece aberta.
