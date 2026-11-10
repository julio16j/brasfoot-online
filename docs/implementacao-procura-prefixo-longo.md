# Remover teto artificial do filtro de nome

A consulta online rejeitava prefixos com mais de 120 caracteres, apesar do predicado nativo não impor esse limite. `NativePlayerSearch` agora encaminha o texto para `best.af.setNome` sem truncar ou recusar por comprimento; a comparação permanece a mesma do motor.

`NativePlayerSearchTest` confirma que um prefixo de 121 caracteres retorna zero resultados sem rejeição, tanto pela API interna quanto pelo endpoint HTTP do JAR distribuído. O ZIP contém 22.735 entradas e `testzip()` não encontrou erro.
