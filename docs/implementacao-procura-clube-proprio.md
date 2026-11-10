# Navegação da Procura para o próprio clube

`NativeTransferSearch.offerSelected` agora encaminha jogadores do elenco do usuário para `Times`, igual aos jogadores de outros clubes. Um agente livre permanece sem navegação, sem receber o aviso enganoso de que precisa pertencer a outro clube. `NativeTransferMarket.openSelected` só exige que exista um clube vendedor; as ações de compra, proposta e empréstimo continuam validando que o jogador pertence a outro clube antes de enviar mutações.

`NativeTransferMarketTest` cobre a decisão de navegação para jogador próprio e agente livre. `python tools/build_adapter.py --test-transfer` passou, incluindo busca/mercado, negociação de IA e empréstimos; `python tools/package_client.py` atualizou o JAR e o ZIP distribuído.
