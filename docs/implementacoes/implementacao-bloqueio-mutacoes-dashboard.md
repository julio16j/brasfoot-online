# Bloquear mutacoes locais sem contrato no dashboard

Recorte PN-11 da task-paridade-nativa.md e especificacao/spec-consultas-historicas-online.md. Base 5a791774, branch impl/bloqueio-mutacoes-dashboard. NativePreparation bloqueia o editor local de cores HT e retira listeners e acelerador do item Adicionar/remover tecnicos do popup Hz. Demais consultas do popup continuam habilitadas. Participacao dinamica e sincronizacao de cores permanecem sem implementacao; PN-11 segue aberta.

| Cenario | Execucao | Esperado e observado | Evidencia |
| --- | --- | --- | --- |
| Editar cores | Janela real, doClick HT | Controle desabilitado, zero listeners | NativeDashboardMutationWindowTest |
| Alterar participantes | Abrir Tecnicos e doClick no item | Item desabilitado sem listener/atalho, roster inalterado | NativeDashboardMutationWindowTest |
| Consultas de tecnicos | Abrir popup | Consultas preservadas | [Captura inspecionada](../evidencias/dashboard-mutacoes-2026-11-10/dashboard.png) |

Compilacao e empacotamento por tools/package_client.py aprovados. Teste grafico aprovado com classes e com JAR distribuido antes de build/classes. Captura inspecionada: popup legivel e item bloqueado; viewport da fixture nativa mantem seu layout. Nao se alterou host/motor/persistencia, pois o recorte remove callbacks locais sem contrato. Autorrevisao: somente NativePreparation, harness, evidencia, registro e ZIP da entrega; mudancas de documentacao preexistentes na principal preservadas. Task e mapa amplos na principal possuem edicoes alheias e nao foram incluidos no commit.
