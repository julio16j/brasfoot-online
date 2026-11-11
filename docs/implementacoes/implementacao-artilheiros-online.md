# Implementação parcial — consulta de artilheiros

Estado: integrada na `main` em 10/11/2026. Relacionada à [task de estatísticas dos jogadores](../tasks/sem-prioridade/task-estatisticas-jogadores-online.md) e à [spec de consultas históricas](../especificacao/spec-consultas-historicas-online.md).

`CareerViews.snapshot` publica `scorers`, ordenado por gols decrescentes e jogos crescentes e limitado a 401 registros, conforme o consumidor nativo `a.o_0`. A lista percorre o índice profissional completo do host (`SR.O`) e inclui os registros aposentados de `SR.bJ`. Jogadores ativos carregam `playerId`; registros aposentados preservam o comportamento nativo sem identidade/clube. A aba Artilheiros da janela Carreira exibe Jogador, Clube, Gols e Jogos.

`CareerCountrySelectionTest` cria um artilheiro ativo em clube fora do técnico autenticado e um aposentado, e verifica população e ordem. O dado só sai pela consulta de overview, submetida à barreira de privacidade do host durante playback. `python tools/build_adapter.py --test` passou; o JAR/ZIP foram reconstruídos e `zip.testzip()` retornou `None` para 22.735 entradas. SHA-256 do ZIP: `63dfa21f9df58d61d9c267b66ee11d6d3d9ade98acd25c5670d1829481780ef9`.

Limites: isto cobre o ranking de gols/jogos, não assistências, médias, avaliações H, rankings por competição nem ficha de carreira; esses recortes e os consumidores nativos completos permanecem na spec. Colisões entre aposentados homônimos seguem a representação nativa sem ID.
