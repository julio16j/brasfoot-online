# Estado atual: amistoso completo com transmissão compartilhada

O protótipo já executa uma partida até o fim com o motor original: preparação simultânea, confirmação de todos os técnicos, início, dois tempos, acréscimos, placar e eventos compartilhados. O host grava o resultado final no formato Kryo da carreira. Os clientes fazem consultas periódicas à mesma instância do host; não simulam partidas localmente.

A escalação foi corrigida após rastrear as posições internas: slots 1–25 representam o campo (11 posições ocupadas), enquanto 26–36 representam até 11 reservas. Os reservas não entram na lista de jogadores ativos no início. O protótipo usa 4-4-2 e oferece seleção automática ou manual.

`MatchPlayback` usa `best.I.V()` para inicializar a partida humana e `best.I.q(minuto, tempo)` para cada minuto, como a tela original. Os pênaltis que dependiam da janela `a.gb` são resolvidos no host com a mesma fórmula de probabilidade observada nessa janela. Não se muda o indicador de técnico humano para executar a simulação.

As fases são PREPARING → WAITING_FOR_ENGINE → RUNNING → FINISHED, ou FAILED em caso de falha. Um relógio no servidor avança todas as partidas dessa fase; relógio e lances são acessíveis a todos os jogadores autenticados. Alterações e novos Prontos são recusados depois do início. Uma falha de motor interrompe a transmissão com estado explícito; uma falha de armazenamento pausa comandos e preserva checkpoints anteriores.

Validação: 33 condições no adaptador, incluindo posições, reservas, intervalo, fim e pênalti convertido/perdido. O teste HTTP executa dois clientes reais de terminal, confirma que ambos mostram o fim e o mesmo placar, confere gols contra o registro de eventos e recarrega o save final em outra JVM. Evidência: `analysis/http-test-result.json`.

Limites: somente amistosos de demonstração foram validados. Não há avanço de calendário, classificação de campeonato, prorrogação, decisão por pênaltis de mata-mata, substituições comandadas pelo cliente ou retomada no meio da partida. O save final pode ser lido novamente sem simulação duplicada. A interface é textual no terminal. Os detalhes abaixo registram a investigação anterior e sua evolução.

---

# Investigacao do multiplayer

Analise estatica em 2026-11-04. Na etapa inicial, nenhum bytecode foi alterado e a interface do jogo não foi iniciada.

## Mapa confirmado

Caminhos relativos a analysis/decompiled/. Nomes terminados em _0 podem ter sido renomeados pelo CFR; consultar comentarios Renamed from antes de escrever um adaptador.

| Arquivo / metodo | Responsabilidade observada |
| --- | --- |
| best/h2.java main | Entrada; inicializa Swing e abre a interface. |
| c/a.java SR, afQ | Objetos globais best.f e best.ay. |
| c/a.java ae(String) | Salva ambos em sequencia com Kryo em sav/<nome>.s22, grava metadados .info e backup .sbck. |
| c/a.java c(String,boolean) | Carrega ambos e reconstroi campos transitorios e interface. |
| best/f_0.java M(), L() | Listas de tecnicos; ainda falta diferenciar com precisao as duas listas. Nome original da classe: best.f. |
| best/al.java | Tecnico: nome, indicador humano, clubes e ID retornado por lT(). |
| best/ah.java | Clube/time, vinculado ao tecnico e as partidas. |
| best/I.java hc(), hd() | Times envolvidos em uma partida. |
| a/bv_0.java om(), or() | Cadastro de tecnicos humanos, vincula nome e nacionalidade ao clube. |
| a/bz.java og() | Botao Bn (Jogar) ligado ao listener a.bc_0 (original a.bC). |
| a/bc_0.java actionPerformed | Chama bz.oh(). |
| a/bz.java oh() | Valida 11 titulares e chama W(true), oi(). |
| a/bz.java oi() | Atualiza escalacao do clube e partida, marca I(true), fecha janela e chama c.a.SR.ap(). |
| best/f_0.java ap() | Consulta pendencias humanas com ay(); abre interface ou encaminha para best.s.dx() / at(). |
| best/f_0.java ay() | Percorre partidas verificando clubes humanos e indicador kf(); semantica exata desse indicador pendente. |
| best/s_0.java | Coordenacao das janelas Swing; nome original best.S. |

Nao foram encontradas referencias a Socket ou ServerSocket nos fontes recuperados. A busca nao prova ausencia de toda forma de rede.

## Proposta inicial (histórico)

Uma unica instancia autoritativa deve manter a carreira e processar as decisoes. Clientes enviam comandos com sessao, tecnico e revisao da carreira; o servidor valida a posse do time e aplica comandos em sequencia. Escalacoes ficam prontas antes de avancar a rodada uma unica vez.

Primeiro recorte: dois tecnicos, envio de escalacao, estado pronto e uma rodada. Transferencias simultaneas, substituicoes durante partidas e reconexao ficam para etapas seguintes.

O estado global e alterado diretamente pelas telas. Ha Random e Collections.shuffle: executar simulacoes independentes nao garante resultados iguais. O formato de save ajuda a estudar snapshots, mas carregar a carreira tambem abre interface; funcionamento sem ambiente grafico ainda nao foi demonstrado.

O protocolo deve usar comandos com campos explicitos e validacao, sem desserializar objetos Java/Kryo enviados diretamente por clientes.

## Proximos experimentos

1. Rastrear best.s.dw()/dx(), best.f.at(), ah.kf() e ah.I(boolean) para confirmar preparacao e execucao.
2. Criar carreira de teste com dois tecnicos e observar um ciclo completo. A pasta copiada possui apenas sav/info.txt, sem carreira salva.
3. Preparar adaptador que use as classes originais do JAR. Nesta etapa foi encontrado JRE Java 8, mas nenhum JDK no PATH; compilacao do adaptador exigira um compilador.
4. Validar leitura do estado e aplicacao de escalacao em processo local antes de adicionar rede.

## Validacao e limites

3.025 arquivos copiados e comparados por SHA-256: nenhuma divergencia. CFR 0.152 verificado contra checksum publicado pelo autor e executado com as quatro dependencias internas extraidas. Gerados 1.038 fontes. O summary.txt aponta um metodo de best.M (best/m_0.java) que nao pode ser totalmente estruturado.

Ainda nao houve recompilacao, teste funcional do jogo ou implementacao de multiplayer. Nenhuma alteracao de registro/licenciamento foi feita.

## Implementação do primeiro protótipo

O adaptador agora compila contra o JAR original, sem recompilar os fontes recuperados. GameAdapter lê o estado e aplica escalações diretamente nas listas do clube e da partida, reproduzindo a parte relevante de bz.oi(), sem chamar ap() ou abrir janelas.

Confirmado: ah.kf() retorna o indicador nv e ah.I(boolean) o altera. best.f.ay() procura o primeiro clube humano cujo indicador está falso. best.f.as() e best.I.V() contêm caminhos que reinicializam esses indicadores. best.s.dw() decide se as partidas devem ser exibidas e best.s.dx() organiza sua apresentação por competição; não são uma barreira de rede.

L() contém a lista geral dos técnicos e M() os humanos: ah.b(String,int) adiciona o técnico nas duas listas. A demonstração monta objetos em memória para teste, com dados de dois arquivos .ban, sem executar o cadastro de carreira pela interface. Ela atribui IDs e contratos de teste; não é uma temporada completa.

RoundCoordinator implementa preparação simultânea: revisões independentes por técnico, edições permitidas até Pronto, retirada de Pronto antes do fechamento e bloqueio único quando todos confirmam. Não chama o motor: o estado final é WAITING_FOR_ENGINE.

SessionHost cria a sessão, vincula tokens a técnicos, serializa os comandos e grava snapshots Kryo dos dois objetos originais no host. O cliente não escolhe qual técnico alterar depois de autenticado. Os checkpoints são escritos antes de confirmar o comando; falha de armazenamento pausa os novos comandos. O snapshot anterior permanece preservado. O protocolo usa formulários com campos explícitos, nunca objetos Java vindos dos clientes.

GameAdapter.readLocalSave lê diretamente os dois objetos do arquivo, sem executar c.a.c(), que abre a interface. O carregamento de um checkpoint da demonstração em outra JVM foi validado; uma carreira real e seu ciclo completo ainda precisam de validação. Pronto é reiniciado ao reabrir uma carreira; tokens e conexões não são restaurados.

Testes: 21 verificações do adaptador e um fluxo HTTP com dois clientes, confirmação concorrente e seis checkpoints locais. A simulação da rodada, substituições, transferências, finanças, interface gráfica e acesso entre redes externas ainda não foram integrados ou validados.

## Interface original e áudio — 2026-11-05

NativeClient conecta a sessão gráfica e mantém um modelo apenas visual em uma JVM separada do host. As propriedades dos jogadores exibidos são projetadas do snapshot do host, incluindo força, condição, posição e habilidades. Não há download ou desserialização de objetos Java dos clientes.

NativeMatchPanel estende a.dV e usa seu layout, renderizadores, escudos, descrição dos lances e dk(int) de áudio. Os métodos de relógio/simulação e a navegação local de partida são desativados. A interface projeta o relógio e o placar autoritativos e traduz eventos recebidos para best.A apenas para apresentação. Eventos são deduplicados por partida e ID. Sons antigos não são repetidos na conexão inicial; a transição entre tempos detecta o intervalo mesmo se a consulta não capturar a fase curta HALF_TIME.

NativeLineup usa a.bz, incluindo o campo e o arrastar e soltar originais. O botão e o atalho locais de Jogar são redirecionados para o envio HTTP ao host. As posições reais 1–25 e os reservas 26–36 são enviados explicitamente, junto das quatro configurações táticas. O host valida tudo antes de alterar seus objetos. Isso permite usar formações diferentes do 4-4-2 inicial. Controles sem integração no servidor ficam desativados.

O lançador padrão é Brasfoot-Online.cmd, que executa dist/brasfoot-online.jar. O pacote ZIP inclui classes, dependências e recursos; não inclui o EXE nem saves. O host é um processo filho com ciclo de vida vinculado à janela gráfica. Fechar o host encerra seu processo filho; o convidado mantém apenas seu cliente.

OriginalLauncher adiciona um menu Online à inicialização original em uma JVM separada. É um caminho experimental, não o padrão: o acesso a java.util.prefs.WindowsPreferences ficou bloqueado tentando criar a chave do Registro dentro deste ambiente. A inicialização por painéis diretos não precisa desse acesso e foi validada. Não houve alteração de registro/licenciamento.

Validação gráfica: dois clientes chegaram ao mesmo placar usando as telas originais e com GO (relógio local) parado. O envio pela tela original preservou as opções táticas no host. O fluxo de criação pelo botão gráfico foi validado tanto nas classes locais quanto no JAR do pacote extraído. Os sete WAVs abriram canais de áudio. As capturas são dos componentes reais renderizados, sem simulação visual por imagem.
