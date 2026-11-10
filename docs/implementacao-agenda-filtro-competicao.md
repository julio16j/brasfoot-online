# Filtrar a Agenda por identidade da competicao

Recorte da task-calendarios.md e spec-calendarios-ligas-online.md. Base a9104784, branch impl/agenda-filtro-competicao. As duas agendas compartilham seletor Mostrar todos/competicao; o RowFilter compara competitionKey e conserva a ordenacao cronologica da tabela. Cada painel possui selecao propria. clubAgenda agora transporta IDs de clubes, tipo/ID/chave da competicao e contexto de pais/divisao, usando o mesmo helper do overview. Rotulos acrescentam pais e divisao nacional quando presentes; codigos de estaduais nao sao rotulados como divisao.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Competi??es homonimas | NativeAgendaCompetitionFilterWindowTest Swing | IDs distintos continuam opcoes separadas |
| Filtro e ordem | Mesmo teste | Somente a chave escolhida e datas em ordem |
| Mostrar todos | Mesmo teste | Tres linhas restauradas, fonte preservada |
| Dois paineis | Mesmo teste | Filtrar um nao modifica outro |
| Competicao regional | Mesmo teste | Sem rotulo de divisao nacional para codigo regional |
| API e campos financeiros | CareerAgendaFinancialWindowTest HTTP/Swing | Identidade e valores preservados |
| Entrada Times | NativeTransferMarketAcademyWindowTest | Agenda abre e academia/promocao seguem funcionais |

[Captura inspecionada](evidencias/agenda-filtro-2026-11-10/filter.png). Compilacao, testes de classes e testes de JAR executados; pacote de branch gerado para validacao. As chaves de IDs nativos sao preservadas; o fallback historico CompetitionIdentity.instance continua limitado a sessao para competicoes sem ID, sem promessa de restaurar selecao apos restart. Nao se reconstroi calendario nem dados esportivos localmente; o filtro so recorta a consulta completa. Permanecem pendentes refresh da janela aberta, selecao automatica do proximo jogo, consulta de detalhe historico e estabilidade persistente dos casos legados sem ID. Spec ampla segue aberta. Autorrevisao confrontou filtro por chave, tabelas compartilhadas e raiz do painel de Times; documentos alheios preservados.
