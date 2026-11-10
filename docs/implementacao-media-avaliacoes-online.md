# Implementação parcial — média de avaliações dos jogadores

Estado: integrada na `main` em 10/11/2026. Relacionada à [task de estatísticas dos jogadores](task-estatisticas-jogadores-online.md) e à [spec de consultas históricas](especificacao/spec-consultas-historicas-online.md).

`GameAdapter` calcula `careerAverageRating` no host a partir dos registros nativos `best.J` de avaliações, somando apenas notas iguais ou maiores que 2 e dividindo pela quantidade válida, conforme `F.gn()`. O valor agregado segue em cada jogador dos clubes presentes em `visualClubs`. `VisualModel` reaplica esse número no cache nativo `F.F()` do objeto cliente, permitindo que consumidores de elenco usem a média correta sem enviar referências a partidas ainda não projetadas.

`CareerCountrySelectionTest` verifica três notas (7, 9 e 1), o resultado 8 no cache nativo, o campo no snapshot do host e o valor após reconstruir o modelo do cliente. `python tools/build_adapter.py --test` passou. `python tools/package_client.py` atualizou o JAR e o ZIP; `zip.testzip()` retornou `None` para 22.735 entradas. SHA-256 do ZIP: `0801de473688a2f4ceafd0f2f5c95acc3922d08bc717638d786549ace73211b7`.

Limites: o agregado não carrega `eU` nem identidade fixture/temporada de cada nota. A tela de histórico por clube/temporada, links para retrospectos e médias filtradas por contexto permanecem pendentes; detalhes em [task](task-estatisticas-jogadores-online.md) e [spec](especificacao/spec-consultas-historicas-online.md).
