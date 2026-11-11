# Publico e renda confirmados na Agenda

Recorte da task-calendarios.md e spec-calendarios-ligas-online.md. Base 2fd5a122, branch impl/agenda-publico-renda. CareerViews publica attendance/revenue em clubAgenda e snapshot do tecnico: jogos agendados recebem null; disputados recebem os valores nativos hU/hN, inclusive zero conhecido. As janelas do dashboard e Times compartilham a tabela com duas novas colunas. Desconhecido e valores nao positivos aparecem vazios, conforme renderer nativo; renda positiva usa best.C.a(valor,0) e dados numericos permanecem disponiveis ao sorter. Agenda de Times agora permite redimensionamento para as colunas adicionais.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Agendado com campos nativos preenchidos | CareerAgendaFinancialWindowTest HTTP | attendance/revenue nulos |
| Disputado com 13.000 pessoas e 350.000 de renda | Mesmo teste, endpoints /club-agenda e /overview | DTOs concordam com getters nativos |
| Disputado com zero | Mesmo teste | DTO conserva zero conhecido, UI exibe vazio |
| Tabela real | Swing e captura Robot | Oito colunas, nulos vazios, renda 350 mil |

[Captura inspecionada](../evidencias/agenda-financeiro-2026-11-10/agenda.png). Teste aprovado com classes e JAR da branch; regressao Times/Academia executada no JAR. Fixture controlada valida leitura/representacao, nao simulacao da arrecadacao ou persistencia financeira. Guardas HTTP da apresentacao permanecem inalteradas; nao se alterou o motor. Autorrevisao: ambos DTOs usam o mesmo helper e ambas janelas usam a mesma tabela; comparadores cronologicos/numericos existentes preservados. Filtros de competicao, detalhe historico e penaltis permanecem pendencias distintas da spec ampla. Documentos compartilhados com edicoes alheias nao foram incluidos. Pacote da branch gerado para validacao; ZIP principal sera recompilado apos integracao.
