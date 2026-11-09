# Áudio repetido ou sem evento correspondente

Status: aberta, aguardando investigação e reprodução. Relato registrado em 6 de novembro de 2026. Esta etapa é documental; nenhuma correção foi implementada.

Contrato consolidado após investigação: [spec de áudio](especificacao/spec-audio-eventos-online.md). Status atual: dois desvios de solicitação de emissão já reproduzidos nos probes abaixo; causa total do relato e reprodução audível ainda não certificadas. Na retomada após integração `fd4aa24f`, a inspeção do código confirmou que o consumo de `NativeMatchPanel` mantém os ramos investigados. A nova restrição do usuário a análise por código impediu novas execuções sonoras; não apresentar os probes anteriores como repetidos no JAR integrado.

## Checkpoint de investigação executada — 08/11/2026

O agente de requisitos reproduziu dois comportamentos no painel nativo online real, por snapshots controlados, em execução headless e **muted** (sem reprodução física). Probe externo `AuditAudio.java`, log `result-audio.txt`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`, sobre o JAR nativo e `build/classes` da principal. Nenhum fonte/teste/pacote foi alterado.

1. Criar painel e apresentar como primeiro snapshot `SECOND_HALF`, half=2, sem eventos; depois `FINISHED`, half=2, também sem eventos. `emittedSounds()` foi `[0,1]`: intervalo e fim juntos, apesar de a entrada ter ocorrido depois do intervalo. Repetir o snapshot final manteve `[0,1]`, portanto não houve duplicação por snapshot idêntico nesse cenário.
2. Em partida com os dois clubes marcados como IA, o wrapper nativo retornou `ei()=false`. Após snapshot inicial sem evento e outro com gol, `emittedSounds()` registrou `[2]` (o clube coincidia com supportedClub padrão do probe). O painel online solicita som sem consultar o escopo humano do wrapper.

Causas delimitadas: `NativeMatchPanel.project` inicializa `intervalSounded=false`, não o ajusta ao primeiro snapshot e o testa na próxima mudança de status com half=2; assim, entrada/reconstrução no segundo tempo pode emitir som histórico de intervalo ao terminar. No nativo, os sons de gol/expulsão/contusão são condicionados a `components.v.ei()` (`analysis/decompiled/a/dV.java:739–767`), que representa presença de clube humano (`components/v.java:25–29,63–65`). O online percorre todos os jogos exibidos e emite sem essa condição (`NativeMatchPanel.java:88`). Em folga, a lista pode incluir todos os jogos da data (`NativeClient.rebuildRound`), ampliando esse desvio de escopo.

Esses fatos são confirmados no pedido de emissão do painel e no rastreamento nativo; **não** comprovam ainda que sejam a única causa do relato do usuário nem ausência/presença de sobreposição audível real. `muted(true)` impede chamar o dispositivo, mas o registrador interno ainda conta a intenção. Não declarar testes de som físico aprovados a partir desse probe.

Correção futura deve estabelecer baseline de transições ao primeiro snapshot/reconexão e preservar o escopo nativo das partidas humanas, documentando se haverá adaptação deliberada por clube apoiado. Testar entrada no primeiro tempo, intervalo, segundo tempo e fim; painel reconstruído; snapshots repetidos; jogos somente IA e mistura de técnicos. Emissão composta legítima de pênalti permanece a comparar com o consumidor de resolução nativo antes de eliminar chamadas.

## Relato e comportamento esperado

O usuário relata sons em momentos nos quais aparentemente não aconteceu nada e a impressão de que o mesmo áudio toca mais de uma vez. O caminho de uso informado nesta sessão é `Brasfoot-Online.cmd`, após o rebuild do pacote.

O áudio deve corresponder aos eventos e transições previstos pelo jogo nativo, no contexto de apresentação correto. Receber novamente um snapshot ou atualizar a interface não deve repetir sons já processados. Não eliminar efeitos legítimos: um evento pode ter mais de um efeito sonoro quando esse for o comportamento original.

## Evidência inicial e pontos de investigação

- Relato do usuário confirmado como problema a investigar; ainda não há captura temporal ou reprodução controlada que determine a causa.
- `src/brasfoot/NativeMatchPanel.java`: `project` percorre os eventos das partidas apresentadas, usa `received` para deduplicação, chama `sound` e emite sons nas transições de intervalo e conclusão. Verificar identidade dos eventos, escopo das partidas, reentrada e reconstrução do painel.
- `src/brasfoot/NativeClient.java`: `accept`, `rebuildRound` e aplicação de `soundsEnabled` podem afetar a vida útil do painel e o estado de áudio. Investigar sem assumir que qualquer um desses caminhos já seja a causa.
- Rastrear os callbacks e a execução de áudio em `a.dV` no motor original. Comparar a regra nativa de quais partidas devem gerar sons com a projeção online.
- O teste anterior de disparos de áudio não certifica ausência de duplicações nem correspondência temporal no cenário relatado.

## Trabalho previsto

Reproduzir com áudio habilitado e registrar rodada, partida, ID e tipo do evento, minuto, sequência do snapshot e efeito emitido, sem tokens. Distinguir som de outra partida, repetição de evento, efeito legítimo composto, atraso de apresentação e reprodução sobreposta. Verificar snapshots repetidos, reconexão, reconstrução da tela, troca de data e transições de intervalo e fim.

Corrigir a causa preservando os efeitos nativos e a configuração autoritativa de som. A instrumentação de diagnóstico não deve modificar resultados ou ritmo da partida.

## Critérios de aceite

1. Os efeitos sonoros correspondem aos eventos e ao escopo de apresentação definido pelo comportamento nativo; não há disparos sem origem identificável.
2. Repetir o mesmo snapshot não produz efeitos adicionais. Entrada tardia, reconexão e atualização ou reconstrução da tela não reproduzem eventos históricos indevidamente.
3. Intervalo e fim de jogo não geram disparos duplicados; efeitos compostos legítimos são preservados.
4. Som desativado interrompe reprodução e impede novos efeitos, sem reinício por simples atualização do estado.
5. Testes cobrem essas invariantes e o cenário de múltiplas partidas. Confirmar também reprodução audível no cliente, pois contar chamadas não comprova ausência de sobreposição real.
6. Após a correção, executar os testes afetados, gerar JAR e ZIP com `python tools/package_client.py` e validar pelo launcher. Manter a task aberta enquanto houver validação pendente do relato.

Referências: [implementação das configurações](implementacao-configuracoes-online.md), [spec das configurações](especificacao/spec-configuracoes-online.md).
