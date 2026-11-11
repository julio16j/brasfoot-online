# PN-04 — áudio nativo de eventos e transições

Estado: recorte de projeção integrado na `main`; task aberta para aceite acústico normal, reconexão e casos restantes. Requisitos em [spec de áudio](../especificacao/spec-audio-eventos-online.md) e pendências em [task PN-04](../tasks/sem-prioridade/task-audio-eventos-repetidos.md).

## Implementação presente

`MatchPlayback` transmite identidade de partidas, clubes e participantes e indica quais clubes têm técnico humano. `NativeMatchPanel` trata a primeira resposta como baseline silencioso, deduplica por partida e evento, respeita o escopo humano e associa eventos por ID com fallback para snapshots antigos. Intervalo e fim são tocados uma vez nas transições observadas. Eventos de pênalti preservam som de cobrança e resultado com o atraso nativo; o apito final espera essa composição. Silenciar ou fechar cancela timers pendentes.

## Validação registrada

- `python tools/build_adapter.py --test`: passou com `NativeAudioProjectionTest`.
- `NativeUiSmoke`: painel original, transições e cobrança/resultado/apito por `Clip` passaram.
- `NativeAudioProjectionTest` com `dist/brasfoot-online.jar` à frente do classpath: passou.
- `python tools/tests/test_native_session.py --jar dist/brasfoot-online.jar`: dois painéis, placares consistentes e gatilhos de áudio passaram.
- ZIP validado com `testzip=None`, 22.735 entradas e sem harnesses. O hash do pacote muda a cada rebuild; consulte o artefato atual.

## Pendências

Os testes observam requisições/execução de `Clip`, não fazem avaliação auditiva ou medição de saída física. Ainda faltam o aceite de áudio no launcher em uso normal, casos específicos de cartões/lesões, reconexão no meio do evento e opção de som alterada durante composição de pênalti. Os testes de cliente em sessão silenciam áudio físico.
