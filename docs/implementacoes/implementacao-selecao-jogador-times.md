# Sincronizar a linha selecionada em Times

Ao abrir `Times` a partir da Procura, o detalhe mostrava o atleta pesquisado, mas a tabela de elenco mantinha a seleção inicial do primeiro jogador do clube. `NativeTransferMarket` agora localiza o ID do atleta no modelo da tabela, converte a linha para o índice visível e seleciona a mesma linha antes de atualizar o detalhe.

`NativePlayerSearchWindowTest` valida na interface Swing real que o ID da linha destacada coincide com o ID do atleta no detalhe. Passaram `python tools/build_adapter.py --test-transfer` e o teste gráfico completo (49.809 profissionais, busca e navegação por duplo clique/Enter, sem envio de transação).
