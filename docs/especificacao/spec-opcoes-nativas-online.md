# Especificação: Opções nativas como acesso às configurações online

Investigação: 6 de novembro de 2026. Revisão: 7 de novembro de 2026. Status: implementada, aguardando validação do usuário. Ver [implementação e validação](../implementacoes/implementacao-opcoes-nativas-online.md). O texto abaixo preserva o contrato e o desenho técnico da correção.

Origem: [task de Opções nativas](../tasks/sem-prioridade/task-opcoes-nativas-online.md). Contrato base: [spec das configurações online](spec-configuracoes-online.md). A [entrega anterior](../resolvidos/task-configuracoes-online.md) continua aceita quanto às opções suportadas e ao efeito de velocidade.

## Problema confirmado

Há dois percursos de edição. O adicional, criado em `NativeClient.gameScreen`, abre `NativeSessionOptions` e chama `sendOptions`; o nativo, preservado por `NativePreparation`, abre a janela original e modifica apenas o estado e as preferências do processo cliente. `SessionOptions.project` reaplica os snapshots do host sobre esse processo, explicando a replicação em um sentido e a ausência de confirmação no sentido inverso.

O botão **Opções** não abre diretamente o painel de configurações: ele abre um menu nativo que contém **Preferências**. A solução deve preservar esse percurso, seu ícone e os controles originais. Renomear o botão adicional para Opções não atende ao pedido.

## Evidências e percurso original

Os arquivos em `analysis/decompiled` são evidência local do motor incluído no projeto. Alguns nomes foram renomeados pelo descompilador, como `eg_0`, `s_0`, `fc_0` e `ax_0`; confirmar os nomes de bytecode usados pela distribuição antes de criar acessos reflexivos novos.

| Ponto | Evidência e implicação |
| --- | --- |
| `a/eg_0.java`, `mJ` e `mH` | `HX` é o botão Opções, com `/aicons/icon01.png`; recebe o listener `a.ew` |
| `a/ew.java`, `actionPerformed` | Exibe o popup `Hy` ancorado no botão; não abre `fB` diretamente |
| `a/eg_0.java`, `rb` | Constrói Preferências com `/aicons/iconsetup.png`, acelerador F3 e listener `fc_0` |
| `a/fc_0.java` e `eg_0.rv` | Preferências chama `rv()`, que chama `best.s_0.a(Iy)` |
| `a/ez_0.java` e configuração de teclado em `eg_0` | O caminho de teclado também chama `s_0.a(Iy)`; é outra entrada para a mesma função |
| `best/s_0.java`, `a(JTable)` | Cria `new fB(dialog, table)` em janela modal, sem redimensionamento e sem decoração, de 867 × 620 |
| `a/fB.java`, construtor e `mK` | Instala listeners antes de selecionar os valores iniciais; seleções podem executar callbacks durante a construção |
| `a/fW.java` e `fB.c(Object)` | Os combos chamam setters de `c.a.vM()` e depois `c.a.vJ()`; não enviam comando HTTP |
| `est/Options.java` e `c/a.java`, `vJ` | Setters atualizam o `a.SR` local; `vJ` grava `options.bcf` no diretório do jogo daquele processo |
| `fB.nH` e `a/fC.java` | Fechar a tela pelo botão nativo também grava preferências |
| `fB.qQ`, `a/ax_0.java` e `a/aY.java` | Editor nativo da paleta possui cinco amostras; confirmar chama setters locais e `vJ` |
| `NativePreparation.create` e `disconnect` | O adaptador salva e reinstala o listener de `HX`; não há roteamento específico para Preferências. O popup `Hy` é um campo separado e seus itens não são necessariamente percorridos por `getComponents()` do painel |
| `NativeClient.gameScreen`, `sendOptions`, `accept` | O botão adicional chega ao endpoint; o snapshot confirmado é projetado de volta nas propriedades locais |

Consequência: construir o `fB` completo e remover os listeners depois é insuficiente para garantir ausência de gravação local. Apenas observar setters locais ou comparar preferências após fechar também é insuficiente: o estado pode ter sido alterado antes da confirmação do host.

## Escopo e invariantes

1. Remover o botão adicional Configurações. Manter o botão nativo **Opções → Preferências** e o atalho nativo F3 como entradas da mesma função online.
2. Usar o painel original `a.fB`, seus rótulos, ícones e organização. Usar também o editor nativo de paleta; não apresentar uma sequência substituta de cinco seletores de cores.
3. Preservar as dez chaves já suportadas, as doze indisponíveis, os limites publicados pelo host e `CLI_OVERRIDE`. A task não implementa os consumidores faltantes do catálogo completo.
4. O host continua a única autoridade, inclusive para áudio, tema e cores. Participantes podem consultar Preferências; fechar e consultar listas não são operações de edição.
5. Interações não devem escrever `options.bcf`, modificar o motor local como confirmação da escolha nem chamar o navegador nativo para abrir uma segunda tela offline.
6. Abrir ou fechar Preferências não pausa o motor, libera Pronto, altera escalação ou reinicia uma partida.
7. A correção do áudio repetido pertence à [task de áudio](../tasks/sem-prioridade/task-audio-eventos-repetidos.md). Manter `soundsEnabled` coerente sem declarar esse defeito corrigido por esta entrega.

## Desenho proposto

### Entrada nativa e controlador único

Em `NativePreparation`, localizar o popup `Hy` e o item Preferências construídos pelo jogo. Preservar o botão `HX` e sua abertura do menu. Substituir apenas o callback de Preferências por uma ação online, encaminhada ao cliente pelo mecanismo de menu existente ou por callback dedicado.

Identificar Preferências pelo contrato da versão inspecionada, conferindo seu rótulo/ícone e o item esperado; não depender apenas da posição visual no popup. Não reinstalar o callback nativo de abertura da janela depois do roteamento. Auditar os itens do popup separado para impedir caminhos de escrita offline: manter ações de outros domínios já integradas e bloquear ações locais inseguras sem implementar novas funções fora desta task.

Criar em `NativeClient` uma única operação de abertura, por exemplo `openNativeOptions()`. Preferências e F3 chamam essa operação; ela usa `latest.sessionOptions` e não passa por `/overview` ou `/management`, que podem estar indisponíveis durante partidas. Uma janela já aberta é trazida à frente em vez de duplicada.

Restabelecer F3 uma única vez no escopo da janela online conectada, sem reativar em bloco os mapas de teclado nativos removidos por `disconnect`. O acelerador do menu e o binding da janela não podem abrir duas instâncias para uma tecla. F3 mantém o acesso durante a partida, no intervalo e quando o painel de preparação com `HX` não está visível, sem criar outro botão adicional.

### Construção da tela sem efeitos antecipados

Refatorar e reutilizar `NativeSessionOptions`, que já instancia o `JPanel` nativo e executa somente `fB.mJ` antes de ligar os controles ao protocolo. Esse mecanismo evita o construtor que dispara gravação local. Não trocar por `new a.fB(...)` seguido de remoção tardia de listeners.

Conservar esse caminho isolado no adaptador, com mensagem de erro explícita se a versão do Java 8 ou o painel esperado estiverem indisponíveis. Não abrir a tela offline como fallback. Reutilizar os recursos nativos para preencher as opções de controles, respeitando os valores e limites confirmados pelo host.

Apresentar a janela como Preferências/Opções, com dimensões, modalização e decoração do caminho original, acomodando uma indicação discreta de autoridade, valores pendentes e indisponibilidade. A modalização Swing não deve bloquear a execução do servidor nem impedir processamento assíncrono dos snapshots. O painel e suas tabelas devem manter seleção e rolagem quando uma atualização não exigir reconstrução.

O botão `vm` e o fechamento da janela apenas fecham a apresentação; não executar `fB.nH`. Todos os listeners de edição devem pertencer ao adaptador online. Isso inclui combos, checkboxes, amostras de tema e placar, editor de paleta, tabelas, setas e teclas.

### Paleta nativa

Reutilizar o painel `ax_0` identificado pelo descompilador. Seu construtor preenche amostras e instala listeners; a escrita foi identificada no callback de confirmação, não no preenchimento inicial. Substituir o listener do botão `zn` antes de permitir interação, impedindo a chamada de `nH`/`vJ`.

As cinco amostras são um rascunho do diálogo. Preservar o botão nativo de restauração de padrão (`zm`) e o seletor de cor das amostras, garantindo que cancelar a seleção não introduza uma cor nula. Confirmar envia uma única alteração `playerListPalette` com cinco strings `#RRGGBB`; fechar ou cancelar não envia nem altera o estado efetivo. Não chamar setters locais para simular confirmação. Participantes e requisições em andamento não podem editar o rascunho ou confirmá-lo.

Conservar a revisão de origem do rascunho. Se um snapshot posterior alterar a paleta ou a revisão durante a edição, sinalizar a divergência e exigir nova edição a partir do confirmado; não reenviar silenciosamente um rascunho antigo com uma revisão nova. A política deve ter teste de conflito específico.

### Envio e projeção

Manter `sendOptions` como transporte único, associado à revisão do snapshot confirmado. Não adicionar endpoint nem esquema de persistência. O corpo continua a ser `optionsRevision` e `changes` em formulário para `POST /session-options`.

Antes do envio, validar a possibilidade de edição e construir valores de protocolo, sem executar o setter nativo de escrita local. Bloquear novas alterações enquanto o pedido está em andamento. A seleção temporária de um controle não é confirmação: efetivo e pendente vêm da resposta ou do snapshot do host.

`accept` continua a aplicar `SessionOptions.project` e a atualizar uma janela aberta com uma flag de projeção programática. Essa atualização não produz requisição nem gravação local. Manter a revisão de opções independente da rodada e da escalação.

Em conflito, atualizar valores/revisão e descartar a escolha antiga sem retry automático. Em erro de validação, suporte ou rede, restaurar a última confirmação conhecida e informar a falha. Fechar a janela com um pedido em andamento não cancela implicitamente um comando já aceito pelo host; sua resposta atualiza a sessão e deve aparecer ao reabrir. Respostas de uma sessão anterior não podem ser aplicadas a uma nova conexão.

O participante vê os valores e os motivos de bloqueio, mas nenhum caminho de interação envia alterações. A API continua a rejeitar edição por participante, mesmo se a interface estiver incorreta.

## Componentes e alterações previstas

| Componente | Alteração |
| --- | --- |
| `NativePreparation` | Interceptar Preferências no popup nativo, preservar a navegação de Opções e impedir o callback offline |
| `NativeClient` | Remover o botão adicional; centralizar abertura, F3, vida útil do diálogo e atualização pela sessão |
| `NativeSessionOptions` | Reutilizar o painel nativo com shell compatível com o original; manter bindings online e integrar a paleta nativa |
| Adaptador de paleta, se necessário | Isolar rascunho, confirmação e descarte do editor original sem setters offline |
| `SessionOptions`, `SessionHost` e `PlaybackClock` | Reutilizar o contrato e os efeitos existentes; não modificar regras do motor para resolver um acesso de interface |
| Testes e documentação | Cobrir o percurso de entrada real, atualizar o mapa e distinguir suporte entregue de opções ainda bloqueadas |

## Plano de validação e aceite

| Cenário | Evidência exigida |
| --- | --- |
| Entrada nativa | Acionar `HX`, selecionar o item nativo Preferências e obter um painel `a.fB` conectado ao protocolo. Nenhum botão adicional Configurações existe |
| F3 e repetição de abertura | Preferências e F3 chegam ao mesmo controlador; uma janela por sessão, inclusive após reconstrução da preparação |
| Host altera velocidade | Interagir com `Jp`/`Jq` nativos e verificar comando, resposta, `effectiveTickMillis` e execução dos minutos, sem sobrescrita CLI no teste de funcionamento normal |
| Dez opções suportadas | Exercitar cada binding apropriado, verificando efeito real ou pendência no limite definido pela spec base; não apenas setter local |
| Participante | Controles e subdiálogos somente leitura; nenhum comando pela UI; requisição direta segue rejeitada |
| Paleta | Cinco amostras, padrão nativo, cancelamento, confirmação atômica e conflito de rascunho; sem escrita ou mutação local antecipada |
| Atualização e erros | Snapshot não dispara comandos; conflito e falha restauram o confirmado; reabrir após resposta pendente mostra o valor do host |
| Persistência | Entrada tardia, reconexão e retomada conservam os valores confirmados e as pendências |
| Escrita offline | Verificar ausência de criação ou alteração de `options.bcf` ao abrir, editar, confirmar e fechar, no host gráfico e participante; cobrir também teclas e subdiálogos |
| Partida em curso | Abrir Preferências não pausa o relógio nem libera o intervalo; acesso por F3 funciona sem consultar gestão |
| Pacote | Reconstruir com `python tools/package_client.py` e validar o percurso pelo JAR aberto por `Brasfoot-Online.cmd` |

Estender `NativeSessionOptionsTest` para o percurso real de `NativePreparation`; o teste atual instancia o adaptador diretamente e não certifica `HX → Hy → Preferências`. Executar as suítes de opções, sessão e interface afetadas, incluindo regressão de carreira se o roteamento da preparação for alterado. Não usar sleeps para provar idempotência de callbacks ou contagem de comandos.

O aceite final exige **Opções nativas → Preferências → controle nativo → comando autorizado → estado e efeito no host → atualização dos clientes**, com o botão adicional removido. Após testes e rebuild, manter a task aguardando validação do usuário até que esse percurso seja confirmado. Esta spec não encerra as tasks de áudio ou das opções ainda sem integração.
