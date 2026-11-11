# Corrigir refresh da Procura com habilidades individuais

O callback da consulta online escolhia o builder da tabela pelo modo invertido. A janela nativa usa `nE()` para o modo geral e `nF()` para habilidades individuais; `runSearch` agora faz esse despacho explicitamente e rejeita modos inválidos.

`NativeTransferSearchTest` cobre o mapeamento. `NativePlayerSearchWindowTest` abre a Procura real nos dois modos e, no modo individual, valida as 19 colunas antes e depois da consulta remota, a ausência do filtro geral de força e a ordenação da coluna de carreira pelo comparador nativo. `python tools/build_adapter.py --test-transfer` passou; o teste Swing passou com as classes compiladas e contra o JAR do ZIP. O pacote contém 22.735 entradas, `testzip()` retorna `None`, SHA-256 `dc604472f97fe1f00607841afa0a7dd0f336c9758bf76001a76736d6192660c9`.
