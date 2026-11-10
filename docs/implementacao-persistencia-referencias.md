# Implementação parcial — persistência de referências nativas

Estado: integrada à `main`; task e critérios completos permanecem abertos em [persistência de referências](task-persistencia-referencias-nativas.md) e [spec](especificacao/spec-persistencia-referencias-nativas.md).

## Código

`GameAdapter.saveLocal` prepara referências selecionadas antes do Kryo. `NativeReferencePersistence`:

- atribui IDs persistentes às competições da agenda que ainda não tinham ID, preservando IDs já existentes e rejeitando duplicatas;
- grava em cada registro `H` o ID da competição referenciada;
- grava em cada registro `ap` o ID e a categoria profissional/júnior do atleta referenciado;
- depois de carregar o mundo e restaurar os globais, reconecta `H` à instância canônica da competição e `ap` ao atleta encontrado pelo par categoria/ID.

O preparo não muda IDs existentes de atletas e não ativa serialização global de campos `transient`. Referências sem chave utilizável continuam sem ser reconstruídas, evitando associá-las por nome, posição ou clube.

## Validação

`NativeReferencePersistenceTest`, incluído em `python tools/build_adapter.py --test`, cria uma carreira pelo motor, introduz registros sintéticos com relações nativas e grava/carrega em dois processos JVM novos. Repete o ciclo uma segunda vez e verifica a instância da competição, contadores disciplinares, suspensão, atleta associado ao histórico e estabilidade do ID público do jogador.

A suíte completa `python tools/build_adapter.py --test` passou. `python tools/tests/test_session.py` passou no host HTTP headless, incluindo reload do save final. `python tools/package_client.py` regenerou o JAR e o ZIP; o ZIP passou `testzip` com 22.735 entradas.

## Limites

O teste injeta relações válidas com IDs conhecidos; não cobre identificação de registros antigos sem ID, atletas removidos/aposentados, rankings/galeria, múltiplas temporadas nem distribuição com jogadores de IDs duplicados legados. Esses cenários continuam abertos na task. Não houve teste de janela nem abertura do Brasfoot nesta etapa.
