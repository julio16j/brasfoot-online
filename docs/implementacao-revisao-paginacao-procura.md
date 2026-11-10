# Revisão obrigatória nas páginas de continuação da Procura

`SessionHost` agora exige `queryRevision` quando `/players/search` recebe offset diferente de zero. A primeira página continua sem precisar de token e retorna a revisão; páginas subsequentes devem reenviá-la. O host compara a revisão antes de executar a busca e preserva a resposta `SEARCH_STALE` para tokens obsoletos.

`NativePlayerSearchTest` confirma primeira página, continuação com a revisão, rejeição da continuação sem revisão e rejeição de revisão antiga. `python tools/build_adapter.py` compilou e o teste HTTP passou contra o JAR empacotado, com 49.809 profissionais no fixture. O ZIP contém 22.735 entradas, `zip.testzip()` retornou `None`; SHA-256 corrente `2b67284a235daa0c4b818dec816f75649384c6e85f020c37fee3c30422b981cb`.

Também foi fechado o estado sem checkpoint: a janela online recusa abrir Procura quando `storageFailed` está ativo e `/players/search` responde 503 `STORAGE_FAILED`, evitando publicar IDs que o host não consegue gravar. `NativePlayerSearchTest` ativa esse estado no host descartável e confirma a rejeição; o teste passou contra o JAR atualizado.
