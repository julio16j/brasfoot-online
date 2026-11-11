# Disponibilidade da Procura no snapshot

Recorte da spec-pesquisa-jogadores-online.md e task-indices-mercado-online.md. Base 52194348; branch impl/disponibilidade-procura. SessionHost.state publica playerSearch.available/reason para armazenamento pausado, apresentacao ativa e tecnico sem clube. O dashboard aplica esse estado ao botao Procura e tooltip; abertura e consulta tambem verificam o motivo antes de enviar a requisicao. Guardas autoritativas do endpoint permanecem.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Disponivel | NativePlayerSearchTest, produtor state | available=true, reason=null |
| Playback ativo | Produtor state e endpoint HTTP | Motivo publicado e busca HTTP recusada |
| Armazenamento / sem clube | Produtor state e endpoint HTTP | Motivo especifico e rejeicao preservados |
| Botao bloqueado e reabilitado | NativeDashboardMutationWindowTest, Swing real | Desabilita com tooltip, reabilita sem tooltip |

[Captura inspecionada](../evidencias/disponibilidade-procura-2026-11-10/dashboard.png): Procura desabilitada com os demais controles preservados. Testes acima aprovados em classes compiladas. O primeiro teste GET /state da fixture global falhou com Resposta muito grande (limite de 8 MiB de SessionApi); resultado registrado, nao considerado aprovado. Os testes do produtor comum foram usados para verificar o contrato, mantendo HTTP no endpoint de busca. O problema de transporte segue como proximo recorte independente; nao foi ampliado o limite nesta entrega. Matriz completa de mudanca de fase entre dois clientes permanece pendente; spec ampla continua aberta. Pacote reconstruido por tools/package_client.py; aceite do JAR anotado no relatorio central.
