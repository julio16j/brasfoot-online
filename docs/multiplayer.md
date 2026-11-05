# Carreira multiplayer

## Visão geral

A carreira online usa uma única instância autoritativa do Brasfoot. O host cria e mantém a carreira, executa partidas, avança o calendário e grava saves. Cada participante controla um técnico e um clube. É possível escolher de 1 a 16 técnicos na tela nativa de seleção de país, divisão e clube.

Clientes compartilham calendário, partidas, placares e eventos. A interface usa painéis e recursos visuais originais, com os comandos online conectados ao host.

## Escalação nativa durante a partida

Qualquer participante pode clicar no nome de um clube ou em uma partida da lista para abrir a tela nativa de escalação. Clubes de outros técnicos abrem somente para consulta; o técnico responsável abre a mesma tela editável. O host valida cada alteração e o limite de substituições. Abrir ou fechar os detalhes não pausa o relógio.

Quando o primeiro tempo termina, o relógio pausa e a tela editável do próprio clube abre automaticamente para cada técnico. Depois dos ajustes, cada um confirma **Pronto para o 2º tempo**. O host libera a segunda etapa somente quando todos confirmam.

## Fluxo da carreira

1. O host escolhe **Carreira**. Na tela nativa, seleciona um clube e informa o nome do técnico; pode adicionar até 16 técnicos/clubes.
2. O host compartilha o endereço e a porta. Os demais jogadores buscam os técnicos disponíveis e entram em vagas livres.
3. Cada técnico prepara sua escalação e confirma Pronto. A rodada só começa quando todos confirmam.
4. O host executa as partidas da data. Abrir a tela de detalhes ou o seletor de substituições não pausa o relógio.
5. Ao fim do primeiro tempo, o relógio compartilhado para. Cada técnico pode ajustar seu próprio clube e confirma **Pronto para o 2º tempo**; o jogo continua quando todos confirmam.
6. Ao terminar todas as partidas da data, o host avança o calendário. O motor original aplica os efeitos pós-jogo e cria a temporada seguinte ao fim da temporada atual.

Cada técnico pode alterar a escalação até confirmar Pronto. Durante os tempos e no intervalo, o técnico pode substituir jogadores do próprio clube dentro do limite restante. O servidor valida a posse dos atletas, a elegibilidade, os jogadores em campo/no banco e a quantidade de substituições; cada alteração é compartilhada e salva. A gestão online inclui renovação de contratos, promoção da base, lista de transferências, propostas entre clubes humanos, venda para clubes da IA e busca de emprego. A interface também apresenta calendário, resultados, classificação, finanças e elenco.

## Saves e recuperação

Checkpoints Kryo ficam em `sessions/<id-da-sessão>/` no computador do host. Use **Retomar carreira** para selecionar a pasta da sessão. Uma partida interrompida volta à preparação a partir do checkpoint seguro, sem repetir resultados nem efeitos pós-jogo.

Os tokens não são restaurados ao reiniciar o host; cada participante deve entrar novamente e receber outro token. Enquanto o host continua ativo, **Reconectar com token** permite recuperar a conexão. Fechar a janela do host encerra o servidor, mas preserva os checkpoints; fechar um cliente convidado não encerra a sessão.

## Rede e limitações

O host precisa manter o Brasfoot Online aberto e aceitar conexões TCP na porta escolhida. Para conexões fora da rede local, endereço e porta devem estar acessíveis pela configuração de rede do host. A carreira e os saves ficam no computador do host; não há armazenamento central na nuvem.

Na criação da carreira, o host consulta o estado de registro pelas preferências oficiais do Brasfoot no Windows. O multiplayer não altera nem ignora a licença. Se as preferências não puderem ser lidas ou a cópia não estiver registrada, o seletor nativo mantém as restrições da versão não registrada, incluindo iniciar somente pela última divisão.

Só estão disponíveis na interface online as ações de carreira que possuem comando correspondente no servidor. Se uma gravação de checkpoint falhar, o host pausa novos comandos para preservar o último estado salvo com sucesso.

## Cobertura automatizada

`tools/test_career_session.py` cobre quatro clientes, telas originais de preparação e escalação, barreira do quarto técnico, resultados compartilhados, avanço de datas, retomada de checkpoint e recuperação de rodada interrompida. `tools/test_career.py` cobre o avanço do calendário do motor original por várias temporadas.
