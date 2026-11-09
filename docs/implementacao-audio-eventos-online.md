# ImplementaÃ§Ã£o PN-04 â€” Ã¡udio de eventos e transiÃ§Ãµes

Data: 09/11/2026. Origem: [task de Ã¡udio](task-audio-eventos-repetidos.md) e [spec](especificacao/spec-audio-eventos-online.md).

## Estado e isolamento

Implementado e validado parcialmente na branch `impl/audio-eventos-v2`; nÃ£o integrado, sem commit, merge, push ou publicaÃ§Ã£o.

- Worktree: `F:/Projects/Brasfoot-online-impl-audio-v2`
- Commit-base: `68874d081879f519a39b6496ac91711dcc82bde0`
- Escopo: baseline sem efeitos histÃ³ricos; escopo de partidas humanas; identidade de partidas, clubes e jogadores; transiÃ§Ãµes de intervalo/fim; efeitos compostos de pÃªnalti.
- DependÃªncias copiadas para investigaÃ§Ã£o/teste: `analysis/brasfoot.jar`, `analysis/lib/*.jar`, `analysis/decompiled/`, `tools/ecj-3.26.0.jar`. Nenhum save de usuÃ¡rio foi copiado.

## MudanÃ§as efetivas

`MatchPlayback.snapshot` conserva o Ã­ndice original da partida/evento e publica IDs de clube, participante principal e segundo participante, alÃ©m de flags explÃ­citas de clube humano. Placar temporÃ¡rio de jogos de IA usa identidade do clube.

`NativeMatchPanel` resolve clube/jogadores por ID, descarta a associaÃ§Ã£o de evento com IDs ausentes/desconhecidos em vez de atribuÃ­-la ao visitante, e inclui o segundo jogador nos eventos nativos de substituiÃ§Ã£o. O painel consome eventos do primeiro snapshot como baseline silencioso e usa a chave partida+evento para deduplicar snapshots cumulativos. Gols/cartÃµes/contusÃµes sÃ³ solicitam Ã¡udio quando o wrapper original indica jogo com tÃ©cnico humano; o som 2/3 de gol segue a flag nativa do clube autor do evento.

TransiÃ§Ã£o observada ao intervalo respeita `getVerJanelaSubs()==1`; um primeiro estado jÃ¡ em segundo tempo/fim nÃ£o gera intervalo retrospectivo. Fim toca uma vez por entrada em `FINISHED`, apenas se houver jogo humano. Eventos pÃªnalti convertidos/perdidos preservam os efeitos 4 + resultado, com resultado atrasado em 2 s como no diÃ¡logo `a.gb`; se o fim chegar durante essa composiÃ§Ã£o, o apito final aguarda o resultado. Silenciar/cerrar o cliente cancela timers pendentes. A reproduÃ§Ã£o de som desligado marca o evento como consumido, sem tocar apÃ³s ativaÃ§Ã£o posterior.

## Matriz de execuÃ§Ã£o e evidÃªncia

| CenÃ¡rio | ExecuÃ§Ã£o/resultado | EvidÃªncia |
| --- | --- | --- |
| Baseline em segundo tempo/fim com gol histÃ³rico; snapshots repetidos; transiÃ§Ã£o FAILED | `python tools/build_adapter.py --test`: PASS em `NativeAudioProjectionTest`, incluindo silÃªncio histÃ³rico, fim uma vez e ausÃªncia de intervalo/fim inventados apÃ³s falha | Log final do build; novo teste incluÃ­do no script. |
| Identidade de evento, homÃ´nimos, gol humano/IA e duas equipes humanas | Mesmo teste PASS: evento resolve ID exato apesar de nome homÃ´nimo; projeÃ§Ã£o carrega IDs e flags humanas; 2/3 acompanha o clube que marcou | `NativeAudioProjectionTest`; eventos sintÃ©ticos sobre elencos nativos reais. |
| Lista mista com partida humana + IA, ID de evento igual em partidas diferentes | Mesmo teste PASS: evento IA nÃ£o toca ao lado de jogo humano; evento humano com mesmo Ã­ndice do evento IA Ã© processado pela identidade partida+evento | `NativeAudioProjectionTest`, dois fixtures nativos; partidas exclusivas de IA ficam silenciosas. |
| OpÃ§Ã£o de som e de intervalo | Mesmo teste PASS: estado da janela nativa controla Ã¡udio de intervalo; som desligado consome evento e habilitÃ¡-lo depois nÃ£o reproduz histÃ³rico | `NativeAudioProjectionTest`, painel mudo e sondagem dos pedidos/plays. |
| PÃªnalti marcado, resultado e apito final | Teste de projeÃ§Ã£o PASS para pares 4+2 e 4+3; teste Swing abriu saÃ­da para cobranÃ§a, resultado apÃ³s 2 s e apito final em seguida | `analysis/native-match-preview.png`; `NativeUiSmoke` terminou PASS. Os trÃªs `Clip`s abriram na saÃ­da de Ã¡udio em sequÃªncia; nÃ£o houve captura por microfone nem avaliaÃ§Ã£o auditiva humana. |
| JAR/ZIP distribuÃ­veis e sessÃ£o grÃ¡fica | `python tools/package_client.py`; `NativeAudioProjectionTest` e `NativeUiSmoke` executados com `dist/brasfoot-online.jar` Ã  frente no classpath; `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar` PASS com host/guest grÃ¡ficos | `analysis/native-create-result.json`, captura e resultado finais; ZIP passou `testzip` e nÃ£o contÃ©m harnesses de teste. |
| Dois clientes de painel Swing na sessÃ£o | `python tools/tests/test_native_session.py`: PASS, dois painÃ©is nativos, placar igual, `audioTriggersVerified`, simulaÃ§Ã£o local desativada | `analysis/native-test-result.json`, `analysis/native-player-1.png`, `analysis/native-player-2.png`. Nesse roteiro `NativeClient` estÃ¡ no modo de teste e silencia Ã¡udio fÃ­sico. |

Artefato final apÃ³s o Ãºltimo cÃ³digo: `dist/brasfoot-online.jar` (226.624 bytes), SHA-256 `CDE8ED381FF1ED2182079CF73EEBF9D6867A829FCCC12FF3D3AB293B72A49C50`; `dist/Brasfoot-Online-Teste.zip` 80.128.511 bytes, SHA-256 `9A2C8AC99AC7F0D17AB81997CD713FE97EB8DB9406A9E8FBAC027C88978C15EE`. ZIP `testzip` retornou sem erros (22.735 entradas), um JAR de cliente e nenhum harness de teste. `git -c core.whitespace=cr-at-eol diff --check` aprovado apÃ³s a implementaÃ§Ã£o; repetir apÃ³s a documentaÃ§Ã£o final.

## Revisão e integração na main em 09/11/2026

Delta adaptado sobre a `main`, preservando os campos de competição do PN-12, a validação de substituição e as mudanças recentes de carreira/juniores. Validação local: `python tools/build_adapter.py --test` passou, incluindo `NativeAudioProjectionTest`; `NativeUiSmoke` passou com painel e editor Swing originais; `NativeAudioProjectionTest` passou com `dist/brasfoot-online.jar` à frente do classpath; `python tools/tests/test_native_session.py --jar dist/brasfoot-online.jar` passou com dois painéis nativos, placar igual e gatilhos de áudio; ZIP íntegro (`testzip=None`, 22.735 entradas, sem harness). O pacote foi regenerado nesta revisão. A task permanece aberta apenas para os limites específicos descritos abaixo; esta fatia de baseline, identidade, escopo humano, transições e composição de pênalti está integrada. Não foi feito push.
## Limites e retomada

A execuÃ§Ã£o acÃºstica confirmou linhas/`Clip`s disponÃ­veis e chamada real ao `start()` pelo painel que carrega as classes do JAR; nÃ£o mediu Ã¡udio fÃ­sico nem obteve avaliaÃ§Ã£o auditiva externa. A task pede ainda aceite com `Brasfoot-Online.cmd` em uso normal. Faltam casos especÃ­ficos de cartÃµes/contusÃµes e reconexÃ£o durante evento, opÃ§Ã£o de som desligada/ligada no meio de uma cobranÃ§a, e comparaÃ§Ã£o completa de pÃªnalti com execuÃ§Ã£o nativa sob usuÃ¡rios reais. O teste de cliente em sessÃ£o usa `testing=true`, portanto nÃ£o valida volume/acÃºstica multi-cliente. Manter task aberta atÃ© esses critÃ©rios pertinentes serem avaliados; nÃ£o declarar correÃ§Ã£o total do relato nem paridade sonora completa.
