# PN-12 — projeção de disponibilidade médica

Estado: correções de projeção e ocultação durante playback integradas em `main`; aceite global permanece aberto. Requisitos: [spec de disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md). Histórico: [task PN-12](task-projecao-lesoes-online.md).

## Implementação presente

- `GameAdapter` publica prazo real de lesão, prazo exato de contrato, cartões por competição e contexto de elegibilidade da partida. Sem jogo atual, o contexto é `null`.
- `CompetitionIdentity` e `VisualModel` mantêm suspensões ligadas à referência canônica da competição, inclusive para competições homônimas com IDs/tipos diferentes e remontagem JSON.
- `VisualModel`/fallback `NativeClient` não convertem `eligible=false` em contusão nem prolongam contrato vencido no cliente.
- Durante playback, `SessionHost` conserva os campos médicos, disciplinares e de elegibilidade do snapshot pré-jogo nos dados do técnico e nos elencos visuais. Escalações e estado autoritativo do host continuam a ser lidos do jogo atual. Ao fim do playback o estado final volta a ser publicado.

## Validação executada nesta revisão

- `python tools/build_adapter.py --test`: PASS, incluindo lesão e contrato na fronteira temporal, suspensão por competição, remontagem, isolamento do host e preservação do estado médico pré-jogo durante playback.
- `python tools/tests/test_session.py`: PASS em host headless com clientes de texto; barreira, playback, resultado final, checkpoints e retomada do save verificados.
- `python tools/tests/test_special_roles.py --jar dist/brasfoot-online.jar`: PASS.
- `python tools/tests/test_live_lineup_atomicity.py --jar dist/brasfoot-online.jar`: PASS.
- `python tools/tests/test_native_session.py --jar dist/brasfoot-online.jar`: PASS, dois painéis de teste ocultos e placar igual; não foi repetido após a instrução para evitar interface do jogo.
- `NativeRestDayWindowTest` no classpath com o JAR atual: os asserts novos de saúde na folga e contexto nulo passaram. O restante do teste falhou num clique Robot do editor (em duas execuções, em botões diferentes); portanto a validação completa da janela não passou nesta rodada.
- `dist/Brasfoot-Online-Teste.zip`: `testzip=None`, 22.735 entradas. JAR SHA-256 `ECD1E84B633A988B96BE58C917862713E36BCCE2020100EC764B39BD4B5DA4C3`; ZIP SHA-256 `29B7089147BFCD1CA0ACD311AA743471F435AEF5D39E792FBE78388DB9A239A9`.

## Pendências

O teste de playback injeta mudança de prazo em snapshots de fixture; ainda não mede uma lesão real no minuto em que ocorre. O guard mantém os dados pré-jogo até o término do playback, então a UI não recebe recuperação/injúria no instante do evento. Também falta a janela completa de folga/dia de jogo com interação confiável e retomada real do host após encerramento. O pacote foi gerado após a mudança de código e não houve push.
