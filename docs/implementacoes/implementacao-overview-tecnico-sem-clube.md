# Escopo do overview para técnico sem clube

Estado: integrada na `main` em 10/11/2026. Relacionada à [task de calendários](../resolvidos/task-calendarios.md) e à [spec de calendários](../especificacao/spec-calendarios-ligas-online.md).

`CareerViews.snapshot` agora inclui partidas e ligas somente quando o técnico autenticado possui clube e ele participa da partida. Para técnico desempregado, o overview continua acessível com data, saldo/folha vazios em zero e sem linhas globais de calendário ou tabelas de ligas. Isso remove o comportamento curinga causado por `club == null`.

`CareerCountrySelectionTest` separa um técnico do clube, obtém o overview e verifica que `calendar` e `tables` estão vazios. `python tools/build_adapter.py --test` passou. `python tools/package_client.py` atualizou JAR e ZIP; `zip.testzip()` retornou `None` para 22.735 entradas. SHA-256 do ZIP: `ebebb372c45c5d1dcdf2a7110a70480846c570ced7af9c4116073d4a86640ec1`.

O recorte não altera consultas explícitas por clube visitado, Tabelas/Rodadas, navegação histórica nem a geração dos calendários internacionais; esses contratos permanecem abertos nas tasks/specs relacionadas.
