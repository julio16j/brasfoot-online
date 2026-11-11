# Isolar a consulta remota da Academia

Ao abrir a academia de um clube visitado, o callback preenchia `ky()` diretamente no shell vendedor usado pela tabela de Times e pelos jogadores pesquisados. A tela de leitura agora recebe um clube temporário com o mesmo ID, nome, país e divisão; os juniores da resposta são ligados a esse objeto isolado. O shell compartilhado e seus dados em memória não são alterados.

`NativeTransferMarketAcademyWindowTest` confirma que a academia remota abre sem mutação do elenco juvenil do shell e mantém as ações de edição bloqueadas. A promoção da academia própria e a atualização da janela permaneceram aprovadas. Também passaram `python tools/build_adapter.py --test-transfer` e a execução gráfica; JAR e ZIP reconstruídos.
