# Paginação completa da Procura

O host limitava `offset` a 50.000, embora `NativeClient` continue carregando resultados em lotes de mil. Isso rejeitava a página seguinte quando a população excedia 51.000 profissionais. O limite do parâmetro agora é o tamanho atual de `SR.O()`; a validação posterior ainda rejeita offsets além da quantidade filtrada, e o tamanho da página permanece limitado a mil.

`NativePlayerSearchTest` acrescenta 1.300 profissionais temporários ao fixture para ultrapassar 51.000, verifica páginas nos offsets 50.000 e 51.000, confirma a última página e testa offset além do total. A mesma continuação HTTP passou contra `dist/brasfoot-online.jar`. O ZIP contém 22.735 entradas e `testzip()` não encontrou erro.
