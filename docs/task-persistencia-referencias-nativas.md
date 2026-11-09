# Referências nativas após salvar e retomar

Status: aberta, P1 proposta; diagnóstico estático de 09/11/2026. Nenhum jogo, JVM, save ou teste do produto executado nesta investigação. Complementa PN-12 (disciplina) e PN-11 (histórico); não revoga as validações anteriores de outros campos Kryo.

## Diferença entre os fluxos de gravação

O salvamento nativo `c.a.ae` chama `g.b.Ag()` e `Ai()` antes de escrever os dois objetos Kryo. O carregamento nativo chama `Ah()`, `Af()` e `Aj()` depois de ler (`analysis/decompiled/c/a.java:102–109,180–185`). São preparação/reconexão de referências, não apenas navegação Swing.

`GameAdapter.saveLocal` apenas instancia Kryo e escreve estado/afQ. `readLocalSave` lê, chama `loaded.G()` e restabelece globais/Pronto; G inicializa cinco coleções transitórias de trabalho, sem reconectar registros H/ap (`src/brasfoot/GameAdapter.java`, métodos saveLocal/readLocalSave; `best/f_0.java:330–335`). Omissão confirmada na fonte atual, inclusive após as mudanças concorrentes de 09/11.

## Disciplina por competição

`best.H` (`best/h_0.java`) possui referência transient Y para a competição e ID persistente ft. Seu construtor guarda Y, mas deixa ft=-1. Ag enumera competições, atribui IDs e chama H.gY para copiar o ID da referência; Af reconecta H.m(competição) pelo ID após carregar (`g/b.java:16–82`).

O consumidor `F.c(at)` consulta H por identidade da referência: `F.h(at)` só encontra registro quando H.gS()==at; caso contrário cria outro H, inicialmente sem cartões (`best/F.java:1170–1173,2087–2098`). Portanto, se Y não é serializado nem reconectado, os contadores antigos continuam no objeto, mas deixam de ser encontrados para a competição corrente. Isso pode retirar o bloqueio de suspensão ao consultar a elegibilidade após retomada. Trata-se de consequência lógica do contrato e do serializer verificado abaixo, sem suspensão real executada/recarregada nesta etapa.

Essa pendência é do estado autoritativo persistido; é distinta de projetar corretamente a suspensão no cliente. Corrigir VisualModel não recompõe uma referência já perdida no host.

## Histórico de transferências

`best.ap` tem referência transient U para F e campos persistentes ei/ej. O setter a(F) apenas guarda U. No fluxo nativo, Ai atribui IDs/tipos aos profissionais e juniores e chama ap.mb() para copiar a identidade; Aj chama ap.me() para reencontrar o atleta (`g/b.java:102–130`; `best/ap.java:18–23,39–45,77–119`).

O adaptador não executa esses passos. Um novo registro de transferência com U preenchido e ei=-1 não tem essa referência preservada pelo serializer padrão; registros antigos com ID também não são reconectados por readLocalSave. `a.hc` filtra transações por ap.x()!=null, de modo que o registro pode persistir como dados, mas desaparecer da consulta nativa após retomada. Não afirmar que houve perda do saldo ou da movimentação de elenco: esses são campos e consumidores diferentes.

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

Outros campos transitórios, como caches de avaliação em at.tV/tW e motor de minuto em I.go, têm responsabilidades distintas e ainda exigem rastreamento antes de qualquer diagnóstico. Não habilitar serialização global de transient como solução genérica: isso pode incluir caches, componentes e estado de execução que o fluxo nativo deliberadamente recompõe ou descarta.

Preparar e reconectar as referências necessárias, preservando IDs estáveis do protocolo online. Não copiar Ai indiscriminadamente: ele renumera jogadores pela ordem das listas e isso pode invalidar ofertas/checkpoints/comandos que usam IDs persistentes. Separar identidade de jogador de índice de serialização e manter vínculos de competição, inclusive nomes iguais e mudança de temporada.

Aceite futuro: suspensão por cartões com bloqueio antes/depois de duas retomadas; duas competições distintas; estatística H de cada contexto; transferência com histórico consultável após outra venda e retomada; profissional/junior/aposentado quando aplicável; oferta pendente mantendo jogador correto; IDs e referências consistentes em nova JVM. Verificar manifesto/save coerentes e rejeição de recuperação inválida. Nenhum desses cenários foi executado nesta etapa.

Relacionados: [disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md), [consultas históricas](especificacao/spec-consultas-historicas-online.md), [paridade](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
