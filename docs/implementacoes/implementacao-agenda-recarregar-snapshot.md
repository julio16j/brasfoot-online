# Agenda visitante com revisao e recarga

Recorte da task-calendarios.md e spec-calendarios-ligas-online.md. Base 02201e02, branch impl/agenda-recarregar-snapshot. NativeAgenda conserva queryRevision da resposta e recebe as revisoes aplicadas por NativeClient. Uma revisao diferente marca a consulta aberta como desatualizada. Atualizar executa AgendaQuery para o mesmo clubId, sem fechar a sessao; a nova tabela conserva competitionKey selecionada quando valida. Competicao ausente recebe mensagem explicita antes de Mostrar todos, sem selecionar outra competicao arbitrariamente.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Revisao posterior com janela aberta | NativeAgendaRefreshWindowTest Swing | Aviso de dados desatualizados |
| Recarga valida | Mesmo teste | Linhas substituidas e filtro preservado |
| Erro de rede e excecao sincrona | Mesmo teste | Ultima tabela preservada, botao permite nova tentativa |
| Resposta de revisao antiga | Mesmo teste | Resultado recusado, linhas anteriores preservadas |
| Competicao removida | Mesmo teste | Mensagem explicita e Mostrar todos |
| Resposta depois de fechar | Mesmo teste | Ignorada, sem reabrir janela |
| Times/Academia | NativeTransferMarketAcademyWindowTest | Entrada Agenda e acoes juvenis preservadas |

[Captura inspecionada](../evidencias/agenda-recarregar-2026-11-10/stale.png): aviso e botao legiveis sem corte. Testes executados em classes e no JAR da branch; pacote gerado para validacao. O callback de producao continua em queryNativeClubAgenda, que verifica clube e revisao do latest antes de publicar; a janela tambem rejeita revisao divergente da ultima observada. Mock controlado valida estados da janela, nao substitui aceite multicliente por rede real. Dashboard/gestao modal ampla, preservacao de linha de fixture e consulta historica continuam pendencias distintas. Autorrevisao: estado por janela, remo??o no windowClosed, atualizacoes de Swing na EDT e tabela antiga substituida so apos validacao/construcao bem-sucedidas; sem mutacao de carreira. Documentos alheios preservados.

Retomada em 11/11/2026 na main, base 23c3e355; validacoes anteriores da worktree sao historicas. Revalidacao na pasta principal pendente.
