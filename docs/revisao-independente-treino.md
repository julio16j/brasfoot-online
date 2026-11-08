# Revisão independente do treino — Agente de requisitos

Data: 08/11/2026. Worktree lida: `F:/Projects/Brasfoot-online-impl-treino`, branch `impl/treino-auxiliar-indisponivel`. Sem edição nessa worktree nem no relatório do Agente 2. O documento preexistente `revisao-entrega-1.md` pertence a revisão anterior; os resultados abaixo foram executados novamente por este agente.

## Artefato e resultado

Uma cópia do `dist/brasfoot-online.jar` estável foi isolada em `C:/Users/Julio/AppData/Local/Temp/brasfoot-review-window-kt3yy3pe/delivery.jar`. SHA256: `9f1937d5d7b3d2ad7e7911f7612d5380c7074c2c7f2c4ea9fc5069c666044ae6`. Essa identificação delimita o recorte antes de novas alterações do agente.

`brasfoot.NativeTrainingTest` executado contra essa cópia passou: auxiliar ativo/inativo preservado, callback local removido e rádios encaminhados. Um probe externo compilado em pasta temporária chamou `NativeTraining.open`, abriu a janela real e inspecionou o componente efetivo: diálogo 429 × 293, painel 413 × 254, caixa MY em (35,198), 348 × 24, totalmente contida e desabilitada. Não recompilou nem alterou a implementação revisada.

Captura `window.png` inspecionada: título Treino, dois focos e texto “Auxiliar técnico (indisponível online)” integralmente visíveis. A primeira captura estava encoberta por outra janela e foi descartada como evidência visual; repetição colocou apenas o diálogo de auditoria em primeiro plano e capturou após pintura. O probe fechou somente seu próprio diálogo. Fontes e logs: `AuditWindow.java`, `result.txt`, `window-result.txt` no diretório temporário acima. Houve correção de erro de compilação do probe (`ah` não oferece `setNome`); não era defeito do produto.

## Alcance do aceite técnico

O corte visual anterior foi corrigido no recorte testado e o bloqueio provisório está tecnicamente validado neste painel real. Não foi exercitado o percurso completo de criação de sessão pelo launcher nem clique de rede contra host; esse teste não certifica o auxiliar completo, processamento semanal online ou integração à principal. O JAR da branch não inclui automaticamente mudanças locais de Opções da pasta principal. Task PN-01 permanece aberta para o [contrato completo](especificacao/spec-treino-auxiliar-online.md).
