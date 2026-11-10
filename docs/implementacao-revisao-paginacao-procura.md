# Revisão obrigatória nas páginas de continuação da Procura

`SessionHost` agora exige `queryRevision` quando `/players/search` recebe offset diferente de zero. A primeira página continua sem precisar de token e retorna a revisão; páginas subsequentes devem reenviá-la. O host compara a revisão antes de executar a busca e preserva a resposta `SEARCH_STALE` para tokens obsoletos.

`NativePlayerSearchTest` confirma primeira página, continuação com a revisão, rejeição da continuação sem revisão e rejeição de revisão antiga. `python tools/build_adapter.py` compilou e o teste HTTP passou contra o JAR empacotado, com 49.809 profissionais no fixture. O ZIP contém 22.735 entradas, `zip.testzip()` retornou `None`; SHA-256 `b8988048c2ad0b19a42c4a54f36981d69577487b8897ea352e80891893157dbd`.
