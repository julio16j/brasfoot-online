# Frescor da Academia visitante em Times

Entrega parcial da [task Academia via Times](task-academia-via-times-online.md), conforme a [spec de juniores](especificacao/spec-juniores-nativos-online.md).

A janela visitante conserva a revis?o recebida por `/club-academy`. Quando o cliente aplica um snapshot posterior com revis?o diferente, NativeYouth mostra uma indica??o persistente de dados desatualizados e impede promo??o nessa janela. Reabrir a Academia executa nova consulta. A academia pr?pria continua usando a atualiza??o existente da proje??o.

Valida??o: build de transfer?ncia aprovado; pacote reconstru?do com tools/package_client.py; NativeTransferMarketAcademyWindowTest aprovado usando dist/brasfoot-online.jar antes de build/classes no classpath. O cen?rio verifica indica??o ap?s mudan?a de revis?o, consulta visitante sem muta??o e promo??o pr?pria com atualiza??o de linhas. Permanecem abertos os demais crit?rios da task, incluindo reconex?o e m?ltiplas academias.
