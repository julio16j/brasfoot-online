# Filtros de apresentacao de partidas

Status: parcial, compilado e empacotado em 11/11/2026. Testes automatizados e aceite visual nao executados a pedido do usuario. Requisitos completos em [spec](../especificacao/spec-filtros-apresentacao-online.md).

## Entregue

- Os seis controles antes bloqueados agora sao editaveis pelo host: paises acompanhados, quatro filtros estaduais/regionais/nacionais, agrupamento estadual e preferencia de partidas versus so resultados. Participantes observam apenas valores confirmados pelo host.
- A tabela de paises exibe os nomes e envia IDs estaveis. As setas movem competicoes entre exibir partidas e somente resultados; a selecao continua marcada durante a atualizacao. Valores passam por validacao, revisao, persistencia e projecao nativa em `SessionOptions`.
- `MatchPresentation` seleciona competicoes para a tela sem remover fixtures do motor. Preserva competicoes com humanos, aplica a lista de paises e filtros, com fallback para partida propria ou um fixture.
- Competicoes sem humanos em "so resultados" mantem o placar, sem feed de eventos nem sons. Alterar filtros atualiza a janela atual sem reconstruir o motor ou reiniciar a rodada.
- Agrupamento estadual reutiliza `VerEstaduaisAgrupados` e o texto nativo do painel de partidas.

## Limites conhecidos

- "Regionais sem humanos" usa presenca humana atual no recorte da competicao; a base nao fornece contador duravel de historico regional.
- Tecnico desempregado e clube anterior, ausencia de clube e precedencia completa de playoffs/competicoes ainda precisam de validacao e refinamento.
- Testes solicitados pelo usuario nao foram executados. O empacotamento atualizou `dist/brasfoot-online.jar` e `dist/Brasfoot-Online-Teste.zip`.
