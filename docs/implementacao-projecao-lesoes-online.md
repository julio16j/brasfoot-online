# ProjeÃ§Ã£o de disponibilidade mÃ©dica â€” PN-12

Data: 09/11/2026. Implementada e validada localmente; ainda nÃ£o integrada Ã  `main`.

Origem e critÃ©rios: [task PN-12](task-projecao-lesoes-online.md) e [spec de disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md).

Branch `impl/projecao-lesoes-v2`, worktree `F:/Projects/Brasfoot-online-impl-projecao-lesoes-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. DependÃªncias ignoradas copiadas da pasta principal: `analysis/brasfoot.jar`, `analysis/lib/*.jar` e `tools/ecj-3.26.0.jar`. Nenhum save ou preferÃªncia local foi copiado.

## MudanÃ§as

- `GameAdapter` publica `injuryUntilMillis` e o instante exato `contractEndMillis` nos dados do tÃ©cnico e nos clubes visuais. `contractDays` permanece somente para apresentaÃ§Ã£o.
- `eligibilityContext` Ã© `null` sem partida atual. Com partida, inclui identidade do confronto, lesÃ£o, suspensÃ£o, contrato vencido e o resultado nativo, sem colapsar os motivos.
- SuspensÃµes carregam cartÃµes e identidade da competiÃ§Ã£o. IDs vÃ¡lidos usam `id:<id>`; competiÃ§Ãµes sem ID recebem chave por identidade de objeto no processo do host. `MatchPlayback`, partida atual e prÃ³ximo confronto publicam a mesma chave.
- `VisualModel` monta primeiro as competiÃ§Ãµes canÃ´nicas e liga cada registro disciplinar ao mesmo objeto `best.at`, exigido pelo filtro nativo `F.h(at)`. Nomes iguais com IDs diferentes nÃ£o sÃ£o fundidos. A reconstruÃ§Ã£o sobre uma nova instÃ¢ncia cliente repete a ligaÃ§Ã£o a partir do snapshot.
- LesÃ£o e contrato sÃ£o projetados pelos prazos reais. `eligible` nÃ£o altera mais lesÃ£o nem renova contrato na projeÃ§Ã£o. `NativeClient` tambÃ©m usa os prazos exatos no caminho de modelo local.

## VerificaÃ§Ã£o executada

| CenÃ¡rio | Resultado observado | EvidÃªncia |
| --- | --- | --- |
| Jogador saudÃ¡vel em folga sem contexto | `fo() == 0`, sem cruz mÃ©dica; contexto nulo | `AvailabilityProjectionTest` e `analysis/availability-window-repeat/rest-next.png` |
| LesÃ£o e contrato com prazos exatos | Ambos preservados em milissegundos | `AvailabilityProjectionTest` |
| LesÃ£o, suspensÃ£o e contrato vencido simultÃ¢neos | TrÃªs razÃµes separadas no contexto | `AvailabilityProjectionTest` |
| CompetiÃ§Ãµes homÃ´nimas IDs 101/202 e tipos 1/3 | Objetos e tipos distintos; suspensÃ£o sÃ³ na competiÃ§Ã£o 202 | `AvailabilityProjectionTest` |
| Filtro nativo do editor | SuspensÃ£o especÃ­fica e contrato expirado continuam bloqueando seleÃ§Ã£o | `AvailabilityProjectionTest` |
| Snapshot serializado e remontado | Prazos e vÃ­nculo disciplinar preservados em nova instÃ¢ncia visual | `AvailabilityProjectionTest` |
| Tela Swing de folga | Janelas reais, datas, acessos e editor verificados com Robot; imagem inspecionada sem lesÃ£o fictÃ­cia | `analysis/availability-window-repeat/result.json` e captura `rest-next.png` |
| Partida online com dois clientes | Ambos concluÃ­ram a partida e viram o mesmo resultado | `tools/tests/test_native_session.py`: PASS, dois painÃ©is originais |
| JAR empacotado | Host grÃ¡fico e cliente convidado iniciaram e concluÃ­ram partida | `tools/tests/test_native_create.py --jar dist/brasfoot-online.jar`: PASS |
| RegressÃ£o de adaptador | `python tools/build_adapter.py --test`: PASS (availability, 33 assertions, elenco grande, mercado e negociaÃ§Ã£o) | saÃ­da do comando; JAR SHA-256 abaixo |

Uma primeira execuÃ§Ã£o do teste grÃ¡fico falhou somente no clique Robot do botÃ£o superior, depois de passar a folga e o teste de saÃºde; a repetiÃ§Ã£o completa passou. As capturas da repetiÃ§Ã£o foram inspecionadas.

JAR empacotado: `dist/brasfoot-online.jar`, 230.466 bytes, SHA-256 `5075bc75ca8956df950303cba386e10d9f1d164dbba967b9c1d47dfc2e4031bb`. Pacote: `dist/Brasfoot-Online-Teste.zip`, 80.132.300 bytes, SHA-256 `04857b4229d0a5211b392b5bf16c2c34ec3d5ca3f875a98181bc2bfd2843eab0`.

## Limites e retomada

A validaÃ§Ã£o cobre a projeÃ§Ã£o e a reconstruÃ§Ã£o de snapshot, alÃ©m de dois clientes numa partida, mas nÃ£o executa ainda a carreira real com reconexÃ£o apÃ³s encerramento do host nem percorre a igualdade exata dos dois prazos em janela. O host segue autoritativo e nenhum objeto de projeÃ§Ã£o Ã© gravado no checkpoint. Task permanece aberta para revisÃ£o e integraÃ§Ã£o; nenhum commit, merge, push ou deploy foi feito. Diff de cÃ³digo revisado; `git -c core.whitespace=cr-at-eol diff --check` passou.


## Revisão na main em 09/11/2026

O recorte foi portado e `python tools/build_adapter.py --test` passou na main, incluindo remontagem JSON, contexto nulo na folga, prazos exatos e suspensão vinculada a competições homônimas de IDs/tipos diferentes. `python tools/package_client.py` concluiu e atualizou JAR/ZIP. `python tools/tests/test_native_session.py` e `python tools/tests/test_native_session.py --jar dist/brasfoot-online.jar` passaram com dois painéis Swing originais e placares iguais em cada execução. Para restabelecer esse caminho, a validação de papéis foi alinhada à regra já documentada: um papel preexistente pode permanecer com o jogador no banco quando ele não foi alterado. Artefatos finais desta revisão: JAR SHA-256 `70BAB01F0FB8F00DDAF884AED6B2C53FAC2C6DB59A965A7E92729E7B5BBFBC4B`; ZIP SHA-256 `55D1ADDC3076145AE83733AA490FD717FA0268A00064DC84403BCE0CAC083E3F`. O arquivo de estado permanece aberto; playback visual de lesões futuras, igualdade temporal exata dos prazos e retomada após encerramento do host continuam sem validação nesta revisão. Nenhum push foi feito.