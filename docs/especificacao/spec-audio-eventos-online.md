# Áudio de eventos e transições online

Status: requisitos abertos para PN-04; correcoes estaticas integradas em 09/11/2026; reproducao e validacao audivel pendentes. [Task e evidencias](../task-audio-eventos-repetidos.md). Nenhuma nova execucao sonora foi realizada.

## Referência nativa rastreada

`c.a.vH()` associa os índices 0–6 aos arquivos intervalo, fimjogo, gol1, goladv, penalty, expulsao e contusao (`analysis/decompiled/c/a.java:58–67`). `a.dV` guarda um Clip por índice: `dk` interrompe esse Clip, volta ao início e o inicia; índices diferentes são objetos distintos. `qb()` fecha todos (`a/dV.java:422–434,561–586`). Isso permite inferir concorrência entre efeitos diferentes, mas não comprova sobreposição audível no dispositivo do usuário.

Eventos de gol/expulsão/contusão no nativo são condicionados à presença humana do wrapper `components.v.ei()`. O gol usa 2 quando o clube do evento é humano e 3 caso contrário (`a/dV.java:739–767`). O online usa `supportedClub` para escolher 2/3 e não consulta `ei()`. São duas diferenças: escopo da partida e perspectiva do gol. Não confundir a partida em destaque com a partida que originou o som.

Pênalti não é sempre um efeito único: `a.dV` toca 4 ao iniciar o fluxo especial e abre o diálogo nativo; `a.gb` resolve depois e toca som de resultado (`a/dV.java:739–749,803–815`; `a/gb.java:109–184`). O online resolve antecipadamente em `MatchPlayback.resolvePenalty` e emite a cobrança e resultado a partir do evento resolvido. Preservar composição legítima e documentar a adaptação temporal, sem deduplicar simplesmente pelo índice do som.

O intervalo nativo `qg()` vincula a emissão de 0 ao ramo da janela de substituições (`a/dV.java:655–676`); fim de jogo tem guarda `GU` e emite 1 (`:438–448`). O online emite transições pela mudança de status. A política sonora para opção de janela desligada e datas somente IA precisa ser definida com essa diferença explícita, sem presumir que o setter de janela já resolve todo o áudio.

## Identidade e consumo de eventos

O host deve continuar sendo a origem dos eventos. Associar emissão a sessão/rodada, partida e ID estável do evento, com efeito/fase quando houver composição. `MatchPlayback.snapshot` usa índice original na lista como ID e omite eventos tipo 8; conservar esses IDs em snapshots cumulativos sem renumerar a cada filtro. O índice visual de uma partida não deve ser a identidade durável do evento.

Publicar também ID do clube e do jogador envolvidos, mantendo nomes apenas para apresentação. Atualmente os eventos transportam esses participantes por nome; `NativeMatchPanel` escolhe o lado comparando o nome do mandante e encontra o primeiro jogador homônimo no elenco. Isso não preserva identidade quando há homônimos, clube renomeado ou participante ausente; não converter clube desconhecido/null automaticamente em visitante. O recálculo do placar temporário IA em `MatchPlayback.snapshot` também compara nome do clube, portanto deve usar a mesma identidade autoritativa. Esses são riscos condicionais rastreados por código, sem caso real executado nesta etapa.

O consumidor marca como vistos os eventos recebidos, mesmo quando o som está desligado. Ativar som não reproduz o histórico anterior. Primeira carga, reconexão e reconstrução estabelecem baseline do que já aconteceu, incluindo estado de intervalo/fim, sem executar efeitos passados. Atualização de opções, repetição do snapshot e redesenho não consomem eventos novamente.

Múltiplos eventos distintos no mesmo minuto podem legitimamente emitir o mesmo índice; uma regra global “um som por minuto/índice” perderia eventos. O diagnóstico deve registrar identidade e origem, sem tokens, para separar repetição indevida de efeitos diferentes.

## Transições e baseline

| Sequência observada | Regra requerida |
| --- | --- |
| Primeira carga já no segundo tempo | Marcar intervalo anterior como passado; não emitir histórico |
| Segundo tempo recebido inicialmente → fim | Emitir fim uma vez; não anexar intervalo atrasado |
| Final repetido | Nenhuma emissão adicional |
| Primeira carga já finalizada | Exibir resultado; não tocar retrospectivamente intervalo/fim |
| Primeiro tempo → intervalo observado | Emitir a transição conforme política sonora da sessão, no máximo uma vez |
| Retomada do segundo tempo | Não repetir intervalo já consumido |
| Falha após entrada no segundo tempo | Não inventar transição de intervalo porque half=2 |
| Troca de rodada | Novo contexto próprio, sem reutilizar IDs apenas por posição visual |

Uma passagem real de primeiro para segundo tempo que chegue sem snapshot intermediário de intervalo precisa de regra explícita de sincronização. Pode ser apresentada como a primeira observação dessa fronteira, mas não deve transformar uma mudança posterior para FINISHED/FAILED em descoberta tardia do intervalo quando o baseline já era segundo tempo.

## Escopo e perspectiva

Integrado no painel: preservar o escopo humano de `components.v.ei()` e escolher o som de gol conforme `event.cu().jZ()`, como no consumidor nativo. A apresentacao do placar continua vinculada ao clube acompanhado; essa perspectiva nao determina a origem nem o tipo do som.

## Critérios de aceite futuros

Cobrir a matriz de transições, eventos duplicados e distintos, dois clubes humanos, humano×IA e somente IA, folga, reconexão e reconstrução, som desligado/ligado e pênalti convertido/perdido. Contagem de solicitações verifica consumo; teste de linha/Clip e reprodução audível verificam o efeito real e eventual sobreposição, quando execução do jogo voltar a estar autorizada. Logs devem distinguir evento, som, minuto/tempo, sequência e clube de origem.

Nenhuma alteração de áudio pode acelerar o relógio, gerar eventos ou recalcular o pênalti no cliente. Testes de WAV decodificado ou Clip aberto não certificam sincronização. A task segue aberta até corrigir e validar o relato pertinente no artefato distribuído.


## Correcoes estaticas PN-04

O primeiro snapshot em `HALF_TIME`, segundo tempo ou `FINISHED` estabelece baseline e nao emite transicoes anteriores. A mudanca posterior para `FINISHED` nao emite um intervalo tardio. Eventos de gol, expulsao e contusao so solicitam som quando o wrapper nativo marca presenca humana; tipos de gol usam a flag humana do clube do evento. A transicao de intervalo respeita `VerJanelaSubs` e o fim emite uma vez para a data com jogo humano.

A projecao inclui `clubId` e `playerId` quando disponiveis. O painel resolve o clube por ID e nunca assume que identidade desconhecida seja o visitante; snapshots legados sem IDs continuam usando nomes. Essas mudancas passaram por compilacao, sem execucao de janela/Clip ou reproducao sonora. A matriz de transicoes, reconstrucao, reconexao, efeitos compostos e validacao audivel permanecem como aceite futuro.
