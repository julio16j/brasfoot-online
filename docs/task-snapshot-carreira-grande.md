# Snapshot de carreira excede o limite do cliente

Status: correcao implementada em branch, validacao de pacote em andamento, 10/11/2026.

Durante a validacao da disponibilidade da Procura, GET /state falhou com Resposta muito grande. Reproducao independente em carreira nova com Corinthians: 12.375.653 bytes UTF-8, incluindo 12.006.405 em game.visualClubs e 241.530 em playback. SessionApi limitava toda resposta a 8 MiB. O problema ocorre sem os 1.300 jogadores sinteticos usados no teste de paginacao.

[Spec](especificacao/spec-snapshot-carreira-grande.md) e [implementacao](implementacao-snapshot-carreira-grande.md). A correcao permite 32 MiB, mantendo teto no tamanho declarado e no fluxo lido. Nao reduz o mundo projetado. Testes HTTP sinteticos (9 MiB e rejeicoes declarada/chunked) e carreira real passaram em classes compiladas. Limites: nao foi avaliado aqui o desempenho com 16 tecnicos nem transferencias lentas; a carga de snapshots e otimizacao da projecao permanecem trabalho independente.
