# Estado inicial do botão de empréstimo em Times

`NativeTransferMarket.show` habilitava o botão Empréstimo incondicionalmente depois de instalar o callback do host, substituindo o estado definido pela janela nativa para o jogador selecionado. O wrapper agora preserva o estado nativo: o botão só fica habilitado para jogador de outro clube que não esteja emprestado e tenha disponibilidade de empréstimo. A validação do callback continua conferindo essas condições antes de enviar.

`NativePlayerSearchWindowTest` abre Times por duplo clique e Enter e compara o estado inicial do botão com o atleta exibido e seu clube. `python tools/build_adapter.py --test-transfer` e o teste gráfico passaram. O JAR e o ZIP foram reconstruídos.
