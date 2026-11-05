# Brasfoot Online — teste com a interface original

O amistoso ABC × Corinthians agora pode ser jogado até o fim usando as **telas originais de escalação e partida do Brasfoot**, com escudos, uniformes, campo, placar, lances e efeitos de áudio. O host cria a sessão, executa uma única simulação e mantém o save. Todos os clientes acompanham o mesmo estado.

A integração é carregada por um lançador próprio que usa as classes e os recursos originais do jogo. O arquivo bf22-23.exe da instalação original não é modificado.

## Baixar e jogar o teste

1. Baixe [Brasfoot-Online-Teste.zip](dist/Brasfoot-Online-Teste.zip?raw=true) (na pagina do arquivo, use **Download raw file** se necessario).
2. Extraia o ZIP inteiro em uma pasta no Windows. Nao execute de dentro do ZIP.
3. Tenha **Java 8** instalado e abra **Iniciar.cmd** na pasta extraida. Nao precisa de Python nem de compilar o projeto.
4. Um jogador clica **Criar sessao** e compartilha seu IP e a porta. O outro informa **IP:8212**, busca os tecnicos e entra no disponivel.
5. Os dois enviam suas escalacoes e confirmam **Pronto**.

Para testar no mesmo computador, abra duas janelas e use **127.0.0.1:8212** na segunda. Entre computadores, a porta TCP 8212 do host precisa estar acessivel; entre redes diferentes, pode ser necessario configurar o roteador ou usar uma VPN de rede local. O repositorio no GitHub distribui o pacote; ele nao hospeda a partida.

O ZIP inclui o cliente, suas bibliotecas e os recursos necessarios ao teste. Os saves sao criados localmente pelo host e nao sao publicados. Se baixar o repositorio inteiro com **Code > Download ZIP**, extraia tambem o pacote dentro de **dist/** e execute o **Iniciar.cmd** dele.

## Testar neste computador

1. Abra **Brasfoot-Online.cmd** nesta pasta.
2. Clique **Criar sessão**. O host assume o ABC.
3. Abra Brasfoot-Online.cmd novamente.
4. Na segunda janela, informe **127.0.0.1:8212**, clique **Buscar técnicos**, selecione Corinthians e clique **Entrar**.
5. Em cada janela, use **Autoescalar** ou abra **Escalação** para arrastar jogadores no campo original e ajustar as opções táticas. Clique **Enviar escalação**.
6. Cada técnico confirma **Pronto**. A partida começa depois da confirmação dos dois e aparece nas duas janelas, com atualização automática e áudio até o fim.

Você pode alterar a escalação livremente antes do Pronto, ou retirar o Pronto enquanto a partida não começou. O botão Áudio original permite ligar/desligar os sons. O ritmo padrão da sala gráfica é um minuto de jogo por segundo, aproximadamente 100 segundos por amistoso.

Para jogar novamente, feche o host e crie outra sessão. Ao fechar o host, os outros jogadores são desconectados e os checkpoints permanecem no disco.

## Testar no computador do amigo

Copie **dist/Brasfoot-Online-Teste.zip** e extraia a pasta inteira no outro computador. Abra **Iniciar.cmd**. O pacote inclui o cliente compilado, dependências e recursos do jogo; **não precisa de Python para jogar**. Precisa de **Java 8** instalado.

O host compartilha o IP e a porta mostrados na barra inferior. No outro computador, informe IP:8212, busque os técnicos e entre no disponível. Os slots de técnicos são exclusivos. Para reconectar ao mesmo processo do host, guarde o token mostrado pelo botão Token de reconexão.

As verificações automatizadas usam dois clientes locais distintos. Para conexão entre computadores, a porta TCP 8212 precisa estar acessível. Para jogar entre redes externas, enviar apenas o IP pode não bastar: ainda depende do roteador e da rede do host. Nenhuma regra de firewall ou roteador foi alterada. O protótipo usa HTTP sem criptografia; o primeiro teste deve ser em uma rede confiável.

## O que está integrado

- Criação e entrada na sessão por interface gráfica.
- Tela original de escalação, com arrastar e soltar, posições no campo e até 11 reservas.
- Envio ao host de posições, estilo de jogo, marcação e concentração de ataques.
- Preparação simultânea e Pronto de cada técnico.
- Painel original de partida com atualização de relógio, placar, escudos, lances e estatísticas.
- Sons originais de gol, gol adversário, intervalo, fim de jogo, pênalti, expulsão e contusão.
- Save e checkpoints no computador do host.

Os relógios e comandos locais do painel de partida ficam desativados. A interface apenas projeta o estado recebido; não gera outro resultado nem abre as janelas locais de decisão de pênalti.

## Limites deste teste

O recorte validado é o amistoso de demonstração. Ainda não há campeonato completo, transferências, finanças, substituições comandadas durante a partida, prorrogação ou decisão por pênaltis de mata-mata. Capitão, batedores, falso 9 e uso de juniores ficam desativados na escalação online. Pênaltis durante o jogo são resolvidos pelo host.

O save final preserva o resultado. Retomar a simulação no meio de uma partida interrompida e avançar para outra rodada ainda não estão integrados. O pacote não inclui os saves do host.

## Save no host

Cada sessão cria sessions/<id>/, com session.json apontando para career-XXXXXX.s22. O host grava após comandos aceitos e no início, intervalo e fim da partida. O save de origem não é sobrescrito e os checkpoints anteriores ficam preservados. Não há limpeza automática de checkpoints neste protótipo.

## Desenvolvimento e validação

```powershell
python tools/build_adapter.py --test
python tools/test_session.py
python tools/test_native_session.py
python tools/test_native_create.py
python tools/package_client.py
```

- O teste do adaptador verifica 33 condições usando o bytecode original.
- O teste HTTP acompanha uma partida inteira com dois clientes de terminal e recarrega o save final.
- O teste gráfico executa dois clientes com os painéis originais, envia uma escalação pela tela nativa, altera opções táticas e confere o mesmo placar final nos dois.
- O teste de criação gráfica aciona Criar sessão, conecta o convidado e acompanha o amistoso até o fim.
- Os sete WAVs originais foram decodificados e abriram canais de saída de áudio neste computador. Os disparos de áudio foram verificados nos painéis.

Evidências e capturas ficam em analysis/native-test-result.json, analysis/native-create-result.json, analysis/native-player-1.png e analysis/native-lineup-preview.png. O pacote extraído também foi usado para validar o fluxo gráfico, sem depender das classes de desenvolvimento.

## Organização

- src/brasfoot/: adaptador, servidor, coordenador e integração Swing.
- tests/brasfoot/: testes do motor, painéis e arquivos de áudio.
- tools/: compilação, testes, cliente de terminal e empacotamento.
- dist/: JAR compilado e pacote ZIP para teste.
- game/Brasfoot22-23/: cópia da instalação local.
- analysis/decompiled/: fontes recuperados para análise.

A compilação usa Java 8 e o ECJ independente, sem recompilar o jogo. O empacotamento só é necessário depois de alterar o código. Referência do compilador: https://help.eclipse.org/latest/rtopic/org.eclipse.jdt.doc.user/tasks/task-using_batch_compiler.htm.

Existe também uma integração experimental com um menu Online na inicialização completa original, disponível por python tools/play.py --original-menu. Esse caminho lê as preferências originais no Registro do Windows e não pôde ser validado no ambiente restrito, no qual o JRE fica tentando criar a chave de preferências. O lançador padrão abre diretamente a sala e os painéis originais, caminho que foi validado.
