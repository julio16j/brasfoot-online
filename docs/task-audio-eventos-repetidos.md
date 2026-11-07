# Áudio repetido ou sem evento correspondente

Status: aberta, aguardando investigação e reprodução. Relato registrado em 6 de novembro de 2026. Esta etapa é documental; nenhuma correção foi implementada.

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
