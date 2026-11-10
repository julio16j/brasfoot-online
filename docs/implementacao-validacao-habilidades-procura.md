# Validar habilidades no DTO e na projeção da Procura

O contrato de busca preserva aliases históricos ambíguos (`skillSpeed`/`skillHeading`) para não afetar outras telas. Acrescentei uma validação por identidade de jogador, comparando os sete campos semânticos do DTO do host e do `F` projetado com os getters nativos `gK`, `gN`, `gO`, `gP`, `gJ`, `gL` e `gM`.

O teste percorre a primeira página completa (1.000 profissionais) e reporta o ID do jogador em qualquer divergência. `python tools/build_adapter.py --test-transfer` passou, assim como `NativePlayerSearchTest` com as classes compiladas e com o JAR do pacote. O ZIP contém 22.735 entradas, `testzip()` retorna `None`, SHA-256 `9f8d20fb94392d72151eb329e27615f23276864afd68ac9ee0080758e8b46179`.
