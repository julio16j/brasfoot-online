# Especificação técnica: configurações nativas na sessão online

Data: 6 de novembro de 2026. Status: entrega atual resolvida por aceite do usuário; cobertura do catálogo completo ainda parcial. Ver [aceite](../resolvidos/task-configuracoes-online.md) e [progresso e pendências](../implementacao-configuracoes-online.md).

## Objetivo e escopo

**A ideia central é propagar as funções e configurações nativas do Brasfoot para o modo multiplayer.** Cada opção da tela original deve acionar sua função nativa correspondente, com execução autoritativa no host e propagação dos valores e efeitos aos participantes. Preservar as regras, os significados e os comportamentos do jogo original é requisito da implementação.

O adaptador multiplayer deve conectar os controles às funções existentes, transportar comandos e sincronizar os resultados. A integração inclui tanto as alterações feitas pelo host quanto os efeitos que o motor nativo produz ao longo da carreira. Reutilizar a aparência da tela ou atualizar um valor local, sem executar e compartilhar a função correspondente, não atende a este objetivo.

Fazer o relógio autoritativo respeitar as velocidades nativas escolhidas pelo host. Eliminar o intervalo obrigatório de 1000 ms da interface online e manter todos os participantes no mesmo estado. O diagnóstico e as evidências estão em [task-configuracoes-online.md](../resolvidos/task-configuracoes-online.md).

Inclui todas as configurações disponíveis na tela nativa de opções (`a.fB`): simulação, gestão, salvamento, apresentação, som, cores e filtros. Todas seguem edição exclusiva do host, consulta pelos participantes, sincronização e persistência. Controles ainda sem integração permanecem sem edição, inclusive no host, até a entrega do efeito correspondente.

Esta especificação fica em `docs/especificacao/spec-configuracoes-online.md` e abrange todas as configurações. As seções de relógio continuam detalhando a velocidade; o contrato comum abaixo se aplica ao restante das opções. O progresso e os limites da implementação estão em [implementacao-configuracoes-online.md](../implementacao-configuracoes-online.md).

## Regras de negócio

0. Reutilizar as funções nativas e seus pontos de aplicação no motor. Antes de criar lógica equivalente no adaptador, verificar se a função original pode ser chamada no host e ter seu resultado propagado. Toda adaptação necessária por causa da rede deve declarar o comportamento nativo preservado e a diferença introduzida.
1. Somente o técnico identificado como host pelo servidor pode alterar qualquer configuração desta tela, incluindo som e aparência. Não há exceção de preferência local nesta proposta.
2. A velocidade com humanos vale quando pelo menos um clube humano joga na data, independentemente do clube do espectador.
3. A velocidade sem humanos vale quando nenhum clube humano joga na data. O intervalo do jogo é retomado automaticamente nesse caso.
4. Cada tick avança exatamente um minuto pelo fluxo de `MatchPlayback`. Não pular minutos nem simular no cliente.
5. Uma alteração não reinicia a partida, não altera minutos já processados e não libera uma pausa do intervalo.
6. O cliente exibe o valor confirmado pelo servidor. Opções locais de participantes nunca sobrescrevem as opções da sessão.

## Catálogo completo da tela de opções

Inventário baseado em `analysis/decompiled/a/fB.java`, `est/Options.java` e na tela enviada. Os nomes de protocolo abaixo são propostos; os nomes nativos indicam propriedades existentes, não garantem que seus efeitos já estejam integrados ao online.

| Controle nativo | Chave de protocolo / tipo | Propriedade nativa | Efeito e momento de aplicação |
| --- | --- | --- | --- |
| Salvar automaticamente | `autoSaveMode`: inteiro 0–3 | `autoSalvar` | 0: não usar auto-save; 1: a cada 3 jogos; 2: a cada jogo; 3: início do ano. Aplicar a partir do próximo limite seguro de salvamento |
| Velocidade do Jogo | `humanSpeedIndex`: inteiro 0–5 | `velocidade` | Próximo tick agendado, conforme seção do relógio |
| Velocidade sem jogos humanos | `nonHumanSpeedIndex`: inteiro 0–5 | `velocidadeNH` | Próximo tick elegível sem humanos |
| Disputas de pênaltis sem humanos | `showNonHumanPenalties`: booleano | `verDecisaoPenNaoHumano` (0/1) | Próxima disputa; apresentação do resultado decidido no host |
| Mudanças de técnicos | `showCoachChanges`: booleano | `verMudancaTecnicos` (0/1) | Próxima notificação; não impedir mudanças do motor |
| Janela de substituição no intervalo | `openLineupAtHalfTime`: booleano | `verJanelaSubs` (0/1) | Próximo intervalo; controla abertura automática, mantendo a barreira de Pronto |
| Aviso de término de contrato | `contractWarningMode`: inteiro 0–2 | `avisoTerminoContrato` | 0: não; 1: após término; 2: faltando um mês. Aplicar na próxima avaliação de avisos |
| Ver leilão de jogadores | `showPlayerAuctions`: booleano | `verLeiloes` | Próximo evento de leilão; não eliminar eventos econômicos do motor |
| Atualizar automaticamente contratos encerrados | `autoRenewExpiredContracts`: booleano | `autoRenovaContrato` | Próxima avaliação de contratos; sem renovação retroativa |
| Cor nos placares humanos | `humanScoreColorsEnabled`: booleano | `usaCorPlacar` | Reapresentar placares nos clientes |
| Cor selecionada para placares | `humanScoreColorIndex`: inteiro 0–2 | `corPlacar` | Reapresentar placares usando a paleta original |
| Sons nos eventos dos jogos | `soundsEnabled`: booleano | `usaSons` | Próximos eventos; ao desligar, interromper áudio em andamento |
| Destaque nos eventos do time da casa | `highlightHomeEvents`: booleano | `negritoCasa` | Reapresentar eventos existentes e futuros |
| Tema de cores das janelas | `windowThemeIndex`: inteiro 0–4 | `corTema` | Atualizar janelas abertas e novas sem reconstruir estado de jogo |
| Usar cores na lista de jogadores | `playerListColorsEnabled`: booleano | `usaCoresLista` | Reapresentar listas |
| Botão cores / editor de paleta | `playerListPalette`: cinco strings `#RRGGBB` | `coresLista` | Validar e aplicar as cinco cores atomicamente |
| Ver todos os estaduais agrupados | `groupStateCompetitions`: booleano | `verEstaduaisAgrupados` | Reagrupar apresentação mantendo identidade das partidas |
| Não mostrar rodadas das ligas nacionais não humanas | `hideNonHumanNationalLeagues`: booleano | `ignoraLigas` | Atualizar seleção dos jogos apresentados |
| Fora do Brasil não mostrar estaduais/regionais | `hideStateRegionalOutsideBrazil`: booleano | `ignoraEstadual` | Atualizar seleção considerando o clube do observador, sob regra definida pelo host |
| Não mostrar regionais que não tiveram humanos | `hideRegionalsWithoutHumanHistory`: booleano | `regionaisSemHumanos` em `Options` | Marcado corresponde a 0; desmarcado a 1. Consultar histórico autoritativo exigido pela regra nativa |
| Visualizar partidas das ligas mesmo sem times humanos: tabela Ver / País | `watchedLeagueCountryIds`: conjunto de IDs de país | Seleção da tabela nativa `JG`, modelo `A`, operação `ex` | Publicar catálogo de países e adaptar o armazenamento nativo após rastrear o modelo; nunca usar posição visual da linha como identidade |
| Competições sem times humanos: Ver partidas / Ver só resultados | `nonHumanCompetitionPlayback`: mapa de ID nativo para booleano | `verJint`, 21 posições na versão inspecionada | `true`: ver partidas; `false`: só resultados. As setas alteram o mesmo mapa |

A tabela Ver / País pode estar vazia conforme a carreira. O estado deve publicar catálogo e seleção vazios sem permitir IDs inexistentes. Identificar o armazenamento dessa tabela no motor é requisito de implementação antes de liberar sua edição; não inventar um setter com base apenas no rótulo.

Os IDs de `nonHumanCompetitionPlayback` vêm de `fB.fy(index)`. Publicar o catálogo completo dessa versão, com ID e rótulo, em vez de codificar apenas as linhas visíveis da captura. Validar chaves pela versão do catálogo e preencher itens ausentes na migração pelos padrões nativos. Ordem das listas e tradução do rótulo não alteram a identidade.

Booleanos devem ser JSON `true`/`false`, com conversão explícita para os inteiros nativos 0/1 quando necessário. Cores não admitem código executável, nomes de classes ou objetos Java serializados. IDs de país são únicos e ordenados na serialização; mapas usam IDs estáveis. Valores fora das faixas são rejeitados sem aplicar parte da alteração.

## Semântica comum de aplicação

### Autoridade e apresentação

Todas as opções, inclusive áudio e tema, são controladas pelo host por decisão de produto desta especificação. Desabilitar também as amostras de cores clicáveis, tabelas editáveis, setas, diálogos secundários e atalhos nos participantes. O botão de fechar e a consulta das listas continuam disponíveis.

Um registro de adaptadores deve declarar, por chave: validação, importação nativa, exportação nativa, aplicação no servidor, aplicação nos clientes e limite seguro de ativação. Não confiar apenas em chamar setters: `corTema` e `regionaisSemHumanos`, por exemplo, têm armazenamento em `Options` que exige atenção além de `a.SR`. O estado da sessão é a referência; a projeção local serve ao desenho da interface e não deve gravar preferências pessoais no disco dos participantes.

O controle “Áudio original” já existente fora da tela deve seguir `soundsEnabled`: editável pelo host e somente leitura para os demais. A rotina de projeção não pode reativar som ignorando a opção recebida. Testes que silenciam áudio podem usar sobrescrita explicitamente identificada no ambiente de teste.

### Opções com aplicação adiada

Guardar mudanças que afetem callbacks de gestão ou decisões em andamento em `pendingValues`, com o limite de ativação publicado por chave em `pendingApplyAt`. Exemplos: `NEXT_CONTRACT_EVALUATION`, `NEXT_PENALTY_SHOOTOUT`, `NEXT_HALF_TIME` e `NEXT_SAFE_CHECKPOINT`. Mostrar valor efetivo e alteração pendente na tela. Se o limite ainda não ocorreu ao retomar, conservar a pendência; ativar uma vez no próximo limite correspondente e incrementar a revisão de opções.

A ativação deve acontecer antes do callback relevante, sob a sincronização do host, e ser persistida com o checkpoint correspondente. Falha interrompe o avanço conforme a política de armazenamento. Cancelar uma pendência enviando o valor efetivo também incrementa a revisão. Estados anteriores já processados não são reexecutados.

### Salvamento

“Não usar auto-save” desliga apenas salvamentos adicionais pedidos pela opção nativa. Checkpoints de recuperação, persistência de comandos e proteções obrigatórias do online continuam ativos e devem ser explicados na tela. Não permitir que uma opção de apresentação de auto-save elimine a capacidade de retomar a sessão.

Rastrear no motor a contagem usada para “a cada 3 jogos” e o ponto de início do ano antes de implementar o gatilho. Reutilizar esses eventos no host, deduplicando com checkpoints já gerados no mesmo limite. Não contar ticks, consultas ou partidas individuais de IA como se fossem esse gatilho sem confirmar a semântica nativa. Persistir o contador necessário para que uma retomada não reinicie a periodicidade.

### Filtros e notificações

Filtros alteram apenas apresentação. Não remover competições, partidas, efeitos econômicos ou jogos do calendário. Garantir que partidas de qualquer clube humano permaneçam acessíveis ao técnico responsável. Aplicar as condições nativas de exceção e precedência, verificadas no motor; manter a chave sem suporte até esse rastreamento estar concluído. Não definir precedência arbitrária entre país selecionado, competição selecionada e flags de ocultação.

Uma regra compartilhada pode produzir listas diferentes conforme o clube observado, como “fora do Brasil”; isso não representa configuração divergente. Identificar o observador explicitamente na projeção. Histórico de participação humana em regionais vem do host, não de inferência a partir da rodada atual.

Mudanças de técnicos, contratos e leilões são processados uma única vez no motor e enviados aos destinatários apropriados. A opção global determina se a apresentação ocorre; não transmite dados privados de gestão a outros técnicos. Pênaltis sem humanos usam o resultado autoritativo, sem recalcular a disputa nos clientes. Desabilitar a abertura automática da escalação no intervalo não elimina o direito de abri-la manualmente nem altera a barreira do segundo tempo.

### Capacidades e compatibilidade

Publicar catálogos, limites nativos efetivamente disponíveis e motivos de indisponibilidade por chave, além de `supportedKeys`. A tela nativa limita algumas velocidades conforme a instalação; respeitar os limites que o host suporta. Uma chave reconhecida mas não integrada retorna `OPTION_NOT_SUPPORTED`, mesmo se enviada diretamente por requisição. Migrações de schema adicionam chaves sem apagar configurações válidas existentes.

## Conversão de velocidade

Usar os índices nativos, com validação de faixa entre 0 e 5. Consultar as tabelas originais `best.aq.rz` e `best.aq.rA`, verificando sua acessibilidade na compilação. Se for necessário encapsular uma cópia, um teste deve compará-la às tabelas do bytecode original.

| Índice | Rótulo | Com humanos (ms) | Sem humanos (ms) |
| --- | --- | ---: | ---: |
| 0 | Lento | 2000 | 1200 |
| 1 | Normal | 1200 | 700 |
| 2 | Rápido | 700 | 300 |
| 3 | Bem rápido | 450 | 100 |
| 4 | Super rápido | 220 | 70 |
| 5 | Ultra rápido | 100 | 30 |

## Componentes propostos

| Componente | Responsabilidade |
| --- | --- |
| `SessionOptions` (novo) | Estado imutável de todas as opções, revisão, validação e resolução dos intervalos |
| `PlaybackClock` (novo) | Agendamento de um único tick pendente, cancelamento e proteção contra tarefa obsoleta |
| `SessionHost` | Autorização, atualização transacional das opções, publicação e persistência |
| `NativeClient` | Importação inicial das opções do host e integração da tela nativa com a API |
| `MatchPlayback` | Avanço do motor e apresentação temporal consistente dos jogos sem humanos |

Os nomes dos componentes novos são propostos; preservar as responsabilidades se forem incorporados a classes existentes.

## Estado publicado

Adicionar ao snapshot autenticado de `/state` e às respostas de comandos:

```json
{
  "sessionOptions": {
    "schemaVersion": 1,
    "revision": 3,
    "values": { "humanSpeedIndex": 4, "nonHumanSpeedIndex": 4 },
    "pendingValues": {},
    "supportedKeys": ["humanSpeedIndex", "nonHumanSpeedIndex"],
    "canEdit": true,
    "effectiveTickMillis": 220,
    "speedSource": "NATIVE",
    "tickOverrideMillis": null,
    "dateHasHumanMatch": true
  }
}
```

`canEdit` é calculado por destinatário a partir da identidade autenticada. `values` contém os valores efetivos de todas as chaves do catálogo abaixo; o exemplo foi reduzido às velocidades. `pendingValues` contém mudanças já persistidas que aguardam um limite seguro de aplicação. `supportedKeys` enumera as opções já integradas nesta versão; uma chave ausente permanece somente leitura. `effectiveTickMillis` descreve a data corrente, inclusive durante a pausa; não significa que o relógio esteja rodando. `speedSource` aceita `NATIVE` ou `CLI_OVERRIDE`. A revisão pertence ao conjunto inteiro de opções e não altera a revisão da escalação ou o número da rodada.

## API de alteração

Adicionar `POST /session-options`, usando `Authorization: Bearer` e formulário, conforme o transporte de `SessionApi`:

```text
optionsRevision=3&changes={"humanSpeedIndex":4,"soundsEnabled":true}
```

Os campos `optionsRevision` e `changes` são obrigatórios. `changes` é um objeto JSON codificado como valor do formulário pela `SessionApi`, com pelo menos uma chave do catálogo. Não aceitar `canEdit`, intervalo efetivo ou identificação do host enviados pelo cliente. Rejeitar campos desconhecidos e duplicados, inclusive chaves JSON duplicadas. Validar a alteração inteira antes de aplicar qualquer chave. O servidor deve comparar a identidade autenticada à identidade do host; possuir um token válido de participante é insuficiente.

| HTTP | Condição |
| --- | --- |
| 200 | Alteração persistida; resposta contém o snapshot completo confirmado |
| 400 | Campos ausentes, desconhecidos, duplicados, tipos ou valores inválidos |
| 401 | Token ausente ou inválido |
| 403 | Técnico autenticado sem autoridade de host |
| 409 | Revisão desatualizada, opção sem integração ou alteração de velocidade sob sobrescrita CLI |
| 503 | Sessão em falha de armazenamento ou erro ao persistir a alteração |

Erro deve conter `error` e um `code` estável, como `OPTIONS_REVISION_CONFLICT` ou `OPTION_NOT_SUPPORTED`. Em conflito de revisão, incluir as opções atuais para atualizar a tela. Reenvio com revisão atual e valores já vigentes ou já pendentes retorna 200 sem incrementar a revisão. Uma alteração efetiva ou pendente incrementa a revisão em uma unidade por comando. O endpoint não exige revisão de escalação ou número da rodada, pois as opções valem para toda a sessão.

## Aplicação e persistência

Executar autorização, comparação de revisão e aplicação sob o monitor do host, o mesmo que serializa ticks e comandos atuais.

1. Construir e validar o estado candidato sem alterar o estado vigente.
2. Invalidar o agendamento pendente, preservando o minuto atual.
3. Persistir o candidato no manifesto com escrita temporária e substituição atômica, aproveitando o mecanismo existente.
4. Instalar o candidato como estado vigente, atualizar os índices nativos no motor do host e reagendar se a partida puder avançar.
5. Responder com o snapshot confirmado.

Se a gravação falhar, manter as opções anteriores, marcar `storageFailed`, deixar o relógio parado e responder 503. Não comunicar sucesso nem publicar o candidato como confirmado. Se uma etapa posterior à gravação falhar, manter o candidato persistido como referência de recuperação e parar a sessão com erro explícito.

Salvar `schemaVersion`, `revision`, `values` e `pendingValues` em `session.json`. Não persistir `canEdit`, classificação da data ou intervalo calculado. A sobrescrita CLI pertence à execução de diagnóstico, não às preferências persistidas; uma retomada sem o argumento usa os índices salvos.

Precedência de carregamento por chave: opções da sessão retomada; caso ausentes, opções persistidas da instalação do host; caso não existam, padrão da classe nativa `est.Options` (índice 4 para ambas as velocidades). Registrar a migração de chaves ausentes. Valores persistidos inválidos devem causar erro de carregamento identificável, sem conversão silenciosa. Importar opções antes de iniciar o relógio. Não carregar preferências da instalação de um participante.

## Agendamento

Substituir `scheduleWithFixedDelay` de intervalo fixo por uma tarefa de execução única, mantendo `ScheduledFuture` e um contador `generation`.

```text
reconfigureClock():                  // sob o monitor do host
    generation++
    cancelar future pendente, sem interromper tick em execução
    se estado permite avanço:
        agendar runTick(generation) após intervalo efetivo

runTick(expectedGeneration):         // adquirir o mesmo monitor
    se geração mudou ou estado não permite avanço: retornar
    executar exatamente um tick
    tratar intervalo, conclusão e persistência
    se estado ainda permite avanço:
        agendar próximo tick usando intervalo efetivo atual
```

Uma tarefa antiga já disparada deve conferir a geração depois de adquirir o monitor. Nunca manter duas cadeias de agendamento. Se uma alteração chegar enquanto um tick executa, ela será aplicada depois desse tick e antes dos seguintes.

Em `PREPARING`, `HALF_TIME`, `FINISHED`, `FAILED` ou `storageFailed`, não avançar o motor. Na retomada autorizada do segundo tempo, agendar usando o valor corrente. Para datas sem humanos, tratar o intervalo internamente, sem barreira de Pronto, e continuar no ritmo sem humanos. Invalidar tarefas ao trocar de data ou encerrar o host.

O intervalo é contado após o processamento anterior, como no comportamento nativo. Não recuperar atraso com uma sequência de ticks imediatos. A consulta de estado do cliente pode continuar em 250 ms nesta entrega: ela não precisa exibir cada minuto em velocidades superiores à taxa de atualização.

## Datas sem humanos e eventos futuros

O laço imediato de `kickoffIfReady` deve ser substituído pelo relógio configurado. Como `MatchPlayback.begin()` resolve jogos da IA antecipadamente, não basta diminuir ou aumentar a espera: snapshots não podem revelar resultado final e eventos futuros no início.

Separar resultado calculado de estado apresentado. Para jogos da IA já resolvidos, construir uma projeção por minuto a partir dos eventos e ocultar placares finais, estatísticas finais e marcação de conclusão até o momento apropriado. Auditar também `game.matches`, `visualClubs` e demais consultas capazes de expor informação adiantada. Campos sem reconstrução temporal confiável devem ficar indisponíveis durante a apresentação, em vez de exibir estatísticas finais como se fossem atuais.

Não modificar os resultados calculados para produzir a animação. Preservar execução única dos efeitos de fim da data em `CareerEngine.completeDate`, gravação e avanço de calendário. A compatibilidade de `nonHumanSpeedIndex` só estará concluída quando essa projeção estiver validada.

## Tela nativa e CLI

- No host, substituir a gravação local de todos os controles de opções por envio ao endpoint; bloquear novos envios enquanto houver solicitação pendente.
- Nos participantes, seletores permanecem visíveis e desabilitados com indicação “Configurações controladas pelo host”. Remover caminhos alternativos de escrita por listeners e teclado.
- Aplicar snapshots à tela com flag de atualização programática, evitando que `setSelectedIndex` dispare comandos.
- Em erro, restaurar os valores confirmados; em conflito, atualizar valores e revisão sem repetir automaticamente uma escolha antiga.
- Retirar o argumento obrigatório de 1000 ms em `NativeClient`. Em `tools/host.py` e `SessionHost`, ausência de `--tick-ms` significa usar opções nativas, sem padrão independente de 500 ms.
- Preservar `--tick-ms` explícito para testes, com faixa atual de 5 a 10000 ms. Enquanto ativo, publicar `CLI_OVERRIDE`, mostrar o intervalo real e desabilitar edição dos seletores de velocidade para todos, inclusive o host. As demais opções suportadas continuam editáveis pelo host.

## Plano de validação

| Nível | Cenário | Resultado esperado |
| --- | --- | --- |
| Unidade | Seis índices, duas tabelas e índices fora da faixa | Conversão exata; rejeição sem mutação |
| Unidade | Relógio controlável, mudança de velocidade e tarefa antiga | Um minuto por tick; geração antiga ignorada; somente um futuro ativo |
| Unidade | Pausa, retomada, troca de data e encerramento | Nenhum avanço indevido; retomada com intervalo vigente |
| API | Host, participante, token inválido e revisão antiga | Códigos e estado conforme contrato |
| API | Alteração idempotente e falha de persistência | Revisão estável no primeiro caso; pausa e ausência de confirmação no segundo |
| Persistência | Retomada nova e migração de manifesto antigo | Precedência definida e índices restaurados |
| Interface | Host altera; participante observa; janela já aberta e reconexão | Valores convergem sem escrita local ou ciclo de comandos |
| Integração | Partida humana em Super rápido | Intervalo programado de 220 ms; mesmas decisões e eventos entre clientes |
| Integração | Apenas outro técnico joga | Usa velocidade com humanos |
| Integração | Nenhum humano joga | Usa 70 ms em Super rápido; intervalo automático; ausência de informação futura |
| Integração | Mudança durante jogo e intervalo | Sem tick duplicado, minuto pulado ou liberação da pausa |

Usar relógio e executor controláveis nos testes de agendamento; não depender de sleeps para provar concorrência. Medir duração real separadamente, registrando processamento e espera, sem impor uma duração exata em máquinas diferentes. Executar os testes existentes de sessão, carreira e interface afetados antes de empacotar.

## Entregas e aceite final

1. Catálogo completo, adaptadores, schema, carregamento, endpoint, autorização e persistência com testes.
2. Integração de todos os controles da tela, modo somente leitura dos participantes, aplicação de som, tema e cores em janelas abertas.
3. Relógio ajustável, apresentação temporal sem humanos e regressão de conclusão da data.
4. Filtros por país, competição, estaduais e regionais, com precedência nativa rastreada e preservação dos jogos simulados.
5. Auto-save adicional, contratos, notificações, leilões, apresentação dos pênaltis e abertura automática no intervalo, com ativação nos limites seguros.

A entrega completa exige as cinco etapas. “Super rápido” deve controlar de fato o relógio do servidor, os participantes devem consultar os valores efetivos do host e a retomada deve preservar todas as opções e pendências. Nenhuma opção deve aparecer editável sem produzir seu efeito anunciado. Esta especificação não altera o código do jogo.

## Testes adicionais para o catálogo completo

| Grupo | Verificações obrigatórias |
| --- | --- |
| Inventário | Cada controle de `a.fB`, incluindo editor de cores, tabela de países e setas de competições, tem chave e adaptador ou motivo explícito de indisponibilidade |
| Contrato | Tipos inválidos, paleta com tamanho errado, IDs desconhecidos e mudanças mistas inválidas não causam aplicação parcial; participante recebe 403 para qualquer chave |
| Sincronização | Alterações em cada grupo chegam a janelas abertas, entrada tardia e reconexão; atualização programática não dispara requisição ou gravação local |
| Som e aparência | Som desligado para áudio em andamento e próximos eventos; temas, paleta e destaque aparecem nos clientes sem reconstruir partidas ou perder seleção |
| Gestão | Renovação, aviso, mudança de técnico e leilão são avaliados no limite correto; nenhuma ação financeira é duplicada; notificações respeitam o destinatário |
| Intervalo e pênaltis | Opção de janela muda apenas abertura automática; Pronto continua necessário; apresentação de disputa não recalcula o resultado |
| Filtros | Seleção de países e tipos, flags de ocultação e agrupamento seguem precedência nativa; dois técnicos de países distintos mantêm acesso aos próprios jogos; quantidade simulada não depende do filtro |
| Salvamento | Quatro modos, contador de periodicidade, início do ano, retomada e falha de gravação; checkpoints obrigatórios permanecem ativos em todos os modos |
| Pendências | Alteração, cancelamento, ativação e retomada antes/depois do limite seguro preservam valores e revisão, sem reprocessar efeitos |
| Compatibilidade | Manifesto antigo recebe chaves ausentes; manifesto inválido falha claramente; capacidades impedem seleção sem suporte na interface e API |

Antes de liberar cada chave, rastrear seus consumidores no motor original e verificar o efeito no online; o inventário de setters, isoladamente, não comprova integração funcional. Registrar na revisão de implementação quais consumidores e testes sustentam cada adaptador.

O aceite de cada função exige demonstrar o percurso completo: **controle nativo → comando autorizado do host → execução da função nativa → estado autoritativo → propagação aos participantes → efeito observável equivalente no multiplayer**. Para funções de apresentação, o host distribui a configuração e os dados necessários, e cada cliente aplica a apresentação nativa sem executar novamente a simulação ou decisões de gestão.
