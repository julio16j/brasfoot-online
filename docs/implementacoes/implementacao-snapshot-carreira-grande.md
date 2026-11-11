# Receber snapshots globais de carreira

Base 4ad0aca3, branch impl/snapshot-carreira-grande. SessionApi usa teto explicito de 32 MiB em vez de 8 MiB e rejeita Content-Length acima dele antes da leitura; o contador do fluxo tambem protege respostas sem comprimento declarado. Nenhum filtro de clube/jogador foi introduzido. [Task](../resolvidos/task-snapshot-carreira-grande.md), [spec](../especificacao/spec-snapshot-carreira-grande.md).

| Cenario | Execucao | Observado |
| --- | --- | --- |
| JSON de 9 MiB | SessionApiResponseLimitTest HTTP | Campo recebido integralmente |
| Content-Length de 33 MiB | Mesmo teste | Rejeitado com motivo do teto |
| Chunked de 33 MiB | Mesmo teste | Leitura interrompida no teto antes do parse |
| Carreira global real | NativePlayerSearchTest GET /state autenticado | Snapshot completo recebido, busca e rejeicoes existentes aprovadas |

Compilacao e testes de classes aprovados; pacote e testes do JAR registrados no relatorio central. Mudanca de transporte sem layout, autorizacao, motor ou persistencia alterados. Autorrevisao: limite fixo continua finito e vale para todas as respostas, erro informa o teto; Content-Length nao substitui a verificacao durante a leitura. Matriz de 16 tecnicos e desempenho de rede lenta nao certificados nesta rodada.
