# Inelegibilidade projetada como lesão permanente

Status: correção de projeção integrada em `main`; task permanece aberta para aceite visual e validação temporal/playback. Investigação original em 08/11/2026 durante revisão independente de PN-03. Problema independente da mensagem de folga; não ampliar silenciosamente o recorte da task de escalação.

Contrato de campos, regras temporais e aceite: [spec de disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md).

## Matriz executada após integração `fd4aa24f`

Em 08/11/2026, `AuditAvailabilityMatrix.java` executou o JAR integrado SHA256 `02f328fb24ba0f59f43da46b5b3f32373facfb931ec9b21d9ff95dc71c5cdf87`. Fonte/log `availability-matrix.txt` em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-integrated-1881dwvb`. Cada cenário recria Fixture com atletas reais; datas/campos alterados somente nesses objetos descartáveis.

| Cenário | Host | Projeção observada |
| --- | --- | --- |
| Saudável com partida | Sem contusão, elegível | Sem contusão, prazo contratual preservado; controle |
| Saudável sem partida atual | Sem contusão | Contusão até `Long.MAX_VALUE`; contexto inexistente |
| Contusão real de 10 dias | Prazo médico real e inelegível | Prazo substituído por `Long.MAX_VALUE` |
| Contrato vencido há um dia | Sem contusão; contrato passado; inelegível | Lesão fictícia e prazo contratual futuro de um dia |
| Suspensão na competição atual | Sem contusão; registro de suspensão ativo | Lesão fictícia; consulta de suspensão da competição projetada retorna falso |

O problema médico encobre uma perda contratual/contextual: `VisualModel` chama `a(Math.max(1,contractDays),true)`, que projeta contrato vencido como vigente. Não se trata de renovação no host; o dado errado é do cliente. Retirar somente a lesão fictícia sem corrigir contrato e suspensão pode liberar indevidamente filtros nativos.

`NativeRestDayWindowTest` também passou novamente no artefato integrado, com janela real. Captura `rest-day-window/rest-next.png` foi inspecionada e ainda mostra cruzes em todos os atletas e fim de contusão em 17/08/292278994, ao lado da mensagem de folga já corrigida. Nenhuma reprodução de save de usuário é alegada.

Dependência confirmada por outro probe: `AuditCompetitionIdentity.java`/`competition-identity.txt` criou competições homônimas com IDs 101/202 e tipos 1/3. `VisualModel` as fundiu por nome: mesma referência, ambos tipo 3 e ID -1. Fonte é o mapa `Map<String,f.a>` de `rebuild`. Esse teste sintético confirma a possibilidade de colisão; ainda falta medir quais nomes reais colidem nas carreiras locais. Não reconstruir punições associando apenas nome ou tipo.

## Evidência

`GameAdapter.java:264` publica `eligible = fixture != null && jogador.a(fixture,false)`; a cópia por técnico faz o mesmo em `:211`. `VisualModel.java:77` transforma qualquer `eligible=false` em `player.d(Long.MAX_VALUE)`. O setter representa fim da contusão, não o motivo geral de inelegibilidade (`analysis/decompiled/best/F.java:319–325`).

## Correção implementada

`GameAdapter` agora publica `injuryUntilMillis`, `contractEndMillis` e registros de suspensão para profissionais, junto aos campos já existentes da academia. `VisualModel` aplica somente o prazo médico recebido, preserva a data exata do contrato e reidrata cartões na referência de competição do snapshot. Removida a criação de contusão a partir de `eligible=false` e a extensão artificial de contrato com `Math.max(1, contractDays)`. O caminho de fallback de `NativeClient` também deixou de transformar inelegibilidade em lesão.

O teste `AvailabilityProjectionTest` cobre prazos reais de lesão, contrato expirado, registros de suspensão profissional reidratados e isolamento do estado autoritativo do host. `python tools/build_adapter.py --test` passou; a execução também passou nos demais testes listados pelo script.

Ainda requer janela nativa real para confirmar ícones/detalhes, transição folga→jogo, competições homônimas e consulta da suspensão contra fixture reconstruído; matriz de playback/reconexão e proteção contra vazamento de lesão futura também continuam abertas. Não declarar aceite completo desta PN apenas com teste de campos.

O nativo `F.a(partida,false)` também rejeita por suspensão e contrato, além de contusão (`best/F.java:1055–1072`). Numa folga não há sequer partida de referência. Portanto, esse booleano não pode alimentar o campo de lesão.

Probe independente `AuditEligibility.java`, log `result-eligibility.txt`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`: Fixture com clube/atletas reais, jogador saudável `fo()=0`, `fP()=false`, data sem partida. Após snapshot e `VisualModel`, o jogador apresentou `fo()=9223372036854775807`, `fP()=true`. Processo terminou com código 0. Nenhum save existente foi acessado, nem fonte alterado.

Evidência gráfica complementar: `C:/Users/Julio/AppData/Local/Temp/brasfoot-review-rest-day-o7akoyd5/repeat/rest-next.png`, capturada ao executar o JAR da branch `impl/escalacao-folga`, SHA256 `0afdda1f2f26f8b2f09f05981e8b2ad80c1c39acaf4a58a029a716ab7d10dd09`. A tabela mostra cruzes em todos os jogadores e o detalhe “Fim contusão: 17/08/292278994”. O recorte da branch não introduziu essa linha; o probe reproduziu a causa também na principal.

## Impacto e contrato requerido

A interface inventa lesões para atletas saudáveis e mascara suspensão/contrato como contusão. Pode alterar filtros e apresentação do editor na projeção, embora a validação final permaneça no host. Não há evidência de gravação dessa lesão artificial no save do host.

Separar no snapshot o fim real de lesão, o motivo de indisponibilidade e a elegibilidade para uma partida específica. A projeção deve preservar estado médico nativo e manter bloqueio esportivo/contratual por mecanismo próprio, sem usar uma lesão fictícia como atalho. Ausência de jogo deve ser representada como ausência de contexto, sem tornar atleta lesionado nem presumir que ele está elegível para a próxima partida.

Critérios: saudável em folga continua saudável; lesionado tem prazo real; suspensão depende da competição; contrato vencido não vira contusão; tabela/detalhe/editor distinguem os casos; mudança de data remove o bloqueio apenas quando o host confirmar; snapshots e detalhes durante playback não revelam lesões futuras. Testar cliente real e motor do host separadamente; confirmar que um atleta bloqueado não se torna escalável por remover a lesão artificial. Persistência de host não deve receber dados médicos da projeção cliente. Testar pacote combinado após integração.

## Evidência complementar em 09/11/2026

Na revisão posterior à integração, `AvailabilityProjectionTest` passou nos limites temporais definidos pela comparação nativa: lesão com prazo igual à data global já não está ativa; contrato com fim igual à data segue elegível; contrato vencido um milissegundo antes é inelegível. O mesmo teste alterou artificialmente os prazos finais após o snapshot de referência e confirmou que `SessionHost.preserveAvailability` conserva lesão, contrato e contexto pré-jogo nos dados do técnico e no elenco visual durante playback.

Essa ocultação dura até o playback terminar; ainda não foi observado um evento real de lesão durante uma partida nem a apresentação do prazo médico no minuto correspondente. Permanecem pendentes a janela nativa atualizada, transição folga→partida no JAR, retomada real do host e a suíte de sessão após o rebuild atual.

## Continuação em 09/11/2026 — playback e fronteiras temporais

Adicionada proteção em `SessionHost.presentedGame`: enquanto o playback está ativo, os campos `injuryUntilMillis`, `contractEndMillis`, `suspensions`, `eligible` e `eligibilityContext` publicados em técnicos/elencos visuais vêm do snapshot pré-jogo. A projeção não altera objetos autoritativos e o snapshot final, após o playback, retorna ao estado do host.

`AvailabilityProjectionTest` passou as fronteiras nativas: lesão que expira exatamente na data global está recuperada; contrato que termina nessa data permanece elegível; contrato vencido um milissegundo antes não é elegível. O teste também altera os prazos finais depois do snapshot e verifica que o playback continua exibindo os valores/contexto iniciais.

`python tools/build_adapter.py --test`, `python tools/tests/test_session.py`, testes HTTP do JAR para papéis e substituição, e verificação ZIP passaram. A janela `NativeRestDayWindowTest` executou os asserts de saúde/contexto, mas falhou depois em clique Robot; repetição falhou em outro botão. Essa parte gráfica não está certificada por esta rodada. O teste de sessão posterior usou host headless e cliente de texto. O cliente Swing não foi aberto novamente após a instrução do usuário. Ainda falta validar um evento real de lesão durante playback e retomada real com o novo recorte.
