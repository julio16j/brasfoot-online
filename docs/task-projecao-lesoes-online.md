# Inelegibilidade projetada como lesão permanente

Status: bug confirmado por execução, aberto; P1 proposta. Investigação de 08/11/2026 durante revisão independente de PN-03. Problema independente da mensagem de folga; não ampliar silenciosamente o recorte da task de escalação.

Contrato de campos, regras temporais e aceite: [spec de disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md).

## Evidência

`GameAdapter.java:264` publica `eligible = fixture != null && jogador.a(fixture,false)`; a cópia por técnico faz o mesmo em `:211`. `VisualModel.java:77` transforma qualquer `eligible=false` em `player.d(Long.MAX_VALUE)`. O setter representa fim da contusão, não o motivo geral de inelegibilidade (`analysis/decompiled/best/F.java:319–325`).

O nativo `F.a(partida,false)` também rejeita por suspensão e contrato, além de contusão (`best/F.java:1055–1072`). Numa folga não há sequer partida de referência. Portanto, esse booleano não pode alimentar o campo de lesão.

Probe independente `AuditEligibility.java`, log `result-eligibility.txt`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`: Fixture com clube/atletas reais, jogador saudável `fo()=0`, `fP()=false`, data sem partida. Após snapshot e `VisualModel`, o jogador apresentou `fo()=9223372036854775807`, `fP()=true`. Processo terminou com código 0. Nenhum save existente foi acessado, nem fonte alterado.

Evidência gráfica complementar: `C:/Users/Julio/AppData/Local/Temp/brasfoot-review-rest-day-o7akoyd5/repeat/rest-next.png`, capturada ao executar o JAR da branch `impl/escalacao-folga`, SHA256 `0afdda1f2f26f8b2f09f05981e8b2ad80c1c39acaf4a58a029a716ab7d10dd09`. A tabela mostra cruzes em todos os jogadores e o detalhe “Fim contusão: 17/08/292278994”. O recorte da branch não introduziu essa linha; o probe reproduziu a causa também na principal.

## Impacto e contrato requerido

A interface inventa lesões para atletas saudáveis e mascara suspensão/contrato como contusão. Pode alterar filtros e apresentação do editor na projeção, embora a validação final permaneça no host. Não há evidência de gravação dessa lesão artificial no save do host.

Separar no snapshot o fim real de lesão, o motivo de indisponibilidade e a elegibilidade para uma partida específica. A projeção deve preservar estado médico nativo e manter bloqueio esportivo/contratual por mecanismo próprio, sem usar uma lesão fictícia como atalho. Ausência de jogo deve ser representada como ausência de contexto, sem tornar atleta lesionado nem presumir que ele está elegível para a próxima partida.

Critérios: saudável em folga continua saudável; lesionado tem prazo real; suspensão depende da competição; contrato vencido não vira contusão; tabela/detalhe/editor distinguem os casos; mudança de data remove o bloqueio apenas quando o host confirmar; snapshots e detalhes durante playback não revelam lesões futuras. Testar cliente real e motor do host separadamente; confirmar que um atleta bloqueado não se torna escalável por remover a lesão artificial. Persistência de host não deve receber dados médicos da projeção cliente. Testar pacote combinado após integração.
