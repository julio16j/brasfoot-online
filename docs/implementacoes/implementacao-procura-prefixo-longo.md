# Remover teto artificial do filtro de nome

A consulta online rejeitava prefixos com mais de 120 caracteres, apesar do predicado nativo não impor esse limite. `NativePlayerSearch` agora encaminha o texto para `best.af.setNome` sem truncar ou recusar por comprimento; a comparação permanece a mesma do motor.

`NativePlayerSearchTest` confirma que um prefixo de 121 caracteres retorna zero resultados sem rejeição, tanto pela API interna quanto pelo endpoint HTTP do JAR distribuído. O ZIP contém 22.735 entradas e `testzip()` não encontrou erro.


Limite residual de transporte, identificado em auditoria estática posterior: o teste endpoint de 121 caracteres não remove o limite de 8192 bytes em `SessionHost.form()`. Como `SessionApi.call()` URL-encoda em UTF-8, valores longos podem ultrapassar o limite antes de chegar a `NativePlayerSearch`, especialmente com caracteres multibyte e campos da continuação paginada. A correção descrita nesta nota removeu o teto artificial do predicado; não prova suporte HTTP ilimitado nem equivalência em prefixos extremos. Ver task/spec da Procura para critério.
