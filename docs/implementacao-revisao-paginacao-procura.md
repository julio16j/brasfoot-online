# Revisão obrigatória nas páginas de continuação da Procura

`SessionHost` agora exige `queryRevision` quando `/players/search` recebe offset diferente de zero. A primeira página continua sem precisar de token e retorna a revisão; páginas subsequentes devem reenviá-la. O host compara a revisão antes de executar a busca e preserva a resposta `SEARCH_STALE` para tokens obsoletos.

`NativePlayerSearchTest` confirma primeira página, continuação com a revisão, rejeição da continuação sem revisão e rejeição de revisão antiga. `python tools/build_adapter.py` compilou e o teste HTTP passou contra o JAR empacotado, com 49.809 profissionais no fixture. O ZIP contém 22.735 entradas, `zip.testzip()` retornou `None`; SHA-256 corrente `a616c969d785230cd3b5d926fb656067579aecd5980ad5d54359829a09bc428e`.

Também foi fechado o estado sem checkpoint: a janela online recusa abrir Procura quando `storageFailed` está ativo e `/players/search` responde 503 `STORAGE_FAILED`, evitando publicar IDs que o host não consegue gravar. `NativePlayerSearchTest` ativa esse estado no host descartável e confirma a rejeição; o teste passou contra o JAR atualizado.

Ao completar a busca, `NativeClient` inclui `queryRevision` no resultado agregado e compara esse token com a revisão de `latest.round` no callback da EDT. Se a carreira avançou entre a última resposta HTTP e a instalação do modelo, o cliente descarta os resultados e pede nova consulta. `NativeTransferSearchTest` cobre token igual, divergente ou ausente; `NativePlayerSearchWindowTest` e `NativePlayerSearchTest` passaram no JAR atualizado.
