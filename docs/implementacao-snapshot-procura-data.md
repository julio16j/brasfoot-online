# Validar a data em todas as páginas da Procura

O cliente já conferia revisão, modo e total ao agregar páginas, mas substituía `dateMillis` a cada resposta. Agora o contrato exige a mesma data em toda a sequência paginada; uma página divergente interrompe a consulta antes de publicar resultados.

`NativeTransferSearchTest` cobre estabelecimento de metadados na primeira página, continuação válida, alteração de data e alteração de total. `python tools/build_adapter.py --test-transfer` passou, assim como `NativePlayerSearchTest` contra o host HTTP real e os assets (49.809 jogadores), tanto com as classes compiladas quanto com o JAR do pacote. O ZIP contém 22.735 entradas, `testzip()` retorna `None`, SHA-256 `97ae355562eef0228c70bd771d23309edc5f5daf2d251c833f28773aa3867c9`.
