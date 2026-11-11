# Procura exige técnico associado a um clube

O endpoint autenticado `/players/search` permitia consultar a população profissional inteira mesmo quando o técnico não tinha clube, embora a interface nativa não ofereça a Procura nesse estado. `CareerManagement.searchPlayers` agora verifica a associação antes de normalizar IDs ou percorrer e serializar os jogadores.

`NativePlayerSearchTest` remove temporariamente o clube do técnico autenticado e confirma que o host rejeita a consulta com a mensagem de contexto ausente; depois restaura a associação do fixture. O teste passou contra `dist/brasfoot-online.jar`. O pacote foi reconstruído e o ZIP mantém 22.735 entradas sem erro de integridade.
