# Mapa da implementação

## Integração de 08/11/2026

O conjunto atual incorpora Opções/Preferências/F3 e paleta nativa, coerência dos acessos de escalação nas folgas e auxiliar de treino autoritativo. O [registro de integração](integracao-main-2026-11-08.md) identifica validação, pacote e limites. As evidências históricas abaixo permanecem com suas datas; não equivalem à execução de todos os cenários nesta integração.

- [Folgas](implementacao-escalacao-folga.md): mensagens e três acessos coerentes; automação e escalação antecipada pendentes.
- [Auxiliar](implementacao-treino-auxiliar-completo.md): comando separado do foco, confirmação, snapshot, persistência e precedência semanal nativa; correção com editor aberto preserva rascunho.
- [PN-07 — empréstimos](implementacao-emprestimos-online.md): listagem, solicitação IA/humana, aceite/recusa, devolução nativa e retomada HTTP integradas; compra definitiva e avisos online permanecem limitados.
- [PN-02 — países do calendário](implementacao-calendario-paises.md): geração seletiva das ligas dos países escolhidos e criação internacional integradas na `main`; navegação por calendário continua pendente.
- [Juniores](implementacao-juniores-nativos-online.md): projeção e promoção implementadas/validadas parcialmente na branch `impl/juniores-nativos-v2`, ainda não integradas e com aceite pendente.
- [Lesões fictícias](task-projecao-lesoes-online.md), áudio e demais lacunas permanecem abertas. O lote não implementa paridade completa.

Complementos estáticos posteriores à integração: [crédito bancário/contabilidade](task-financas-nativas-online.md) sem protocolo; [compra direta de listado](task-compra-listados-online.md) omite a aceitação nativa do atleta; [biblioteca de táticas e padrão](task-taticas-salvas-online.md) sem persistência online. Esses achados não significam nova implementação ou execução de testes do jogo; o usuário restringiu a investigação atual à leitura de código.

Revisado em 6 de novembro de 2026, com base no código desta pasta de trabalho e nas validações registradas. Este mapa descreve os fontes; não afirma que os pacotes já existentes em `dist/` foram reconstruídos com essas alterações.

Coordenação de 08/11/2026: implementações do Agente 2 em branches/worktrees separadas devem indicar branch/commit e validação, sem serem classificadas como integradas à principal antes da conferência da integração. A atualização dos artefatos distribuídos também exige evidência própria. O relatório de implementação em `docs/estado-agente-implementacao.md` é mantido pelo segundo agente; a auditoria e a retomada de requisitos ficam em [estado do agente de requisitos](estado-agente-requisitos.md).

## Funcionalidades presentes

“Implementado” indica que o fluxo está conectado no código. A coluna de validação distingue os testes executados nesta revisão da cobertura disponível para outras áreas.

| Área | O que está implementado | Código principal | Validação e limites |
| --- | --- | --- | --- |
| Criação e entrada na sessão | Carreira ou amistoso; seleção nativa de clubes e nomes; de 1 a 16 técnicos; vagas exclusivas e autenticação por token | [NativeClient](../src/brasfoot/NativeClient.java), [NativeClubSelection](../src/brasfoot/NativeClubSelection.java), [SessionHost](../src/brasfoot/SessionHost.java) | Quatro clientes validados no teste de carreira; existe também `test_native_create.py`. A validação com quatro não certifica todas as combinações de 16 técnicos ou países |
| Preparação e Pronto | Revisões por técnico, edição antes de Pronto e início apenas quando todos confirmam | [RoundCoordinator](../src/brasfoot/RoundCoordinator.java), [NativePreparation](../src/brasfoot/NativePreparation.java) | Coordenação e sessão HTTP aprovadas; barreira do quarto técnico aprovada |
| Escalação nativa | Titulares, até onze reservas, posições e táticas; validação no host; elenco total pode ultrapassar 36 jogadores | [NativeLineup](../src/brasfoot/NativeLineup.java), [GameAdapter](../src/brasfoot/GameAdapter.java) | Teste com 42 jogadores, titular no índice 40 e reserva no 41; elencos reais de 37 e 41 jogadores passaram na carreira gráfica |
| Partidas compartilhadas | Motor no host; clientes exibem placares e eventos; resultado e efeitos de fim da data processados no servidor | [MatchPlayback](../src/brasfoot/MatchPlayback.java), [NativeMatchPanel](../src/brasfoot/NativeMatchPanel.java), [VisualModel](../src/brasfoot/VisualModel.java) | Sessões HTTP e Swing aprovadas; teste gráfico verifica que o timer de simulação local permanece parado |
| Alterações durante o jogo | Substituições e ajustes do próprio clube; detalhes de outros clubes somente para consulta | [NativeClient](../src/brasfoot/NativeClient.java), [NativeLineup](../src/brasfoot/NativeLineup.java), [GameAdapter](../src/brasfoot/GameAdapter.java) | Host valida jogadores, posse do clube, elegibilidade e substituições restantes; não há pausa ao abrir detalhes |
| Intervalo | Pausa com partidas humanas; ajustes e confirmação para o segundo tempo; abertura automática da escalação conforme opção do host | [SessionHost](../src/brasfoot/SessionHost.java), [RoundCoordinator](../src/brasfoot/RoundCoordinator.java), [NativeClient](../src/brasfoot/NativeClient.java) | Testes de sessão e de opções aprovados, incluindo ativação da opção no próximo intervalo |
| Calendário e temporadas | Host avança datas; motor original mantém competições e efeitos pós-jogo. Geração seletiva por países está implementada em branch PN-02 | [CareerEngine](../src/brasfoot/CareerEngine.java) | Duas rodadas consecutivas aprovadas nesta revisão. Existe teste de várias temporadas em `test_career.py`; criação seletiva e internacional validada em branch própria, sem integração; automação de folgas e navegação pendentes |
| Consultas da carreira | Calendário, resultados, classificações, finanças e elenco do técnico | [CareerViews](../src/brasfoot/CareerViews.java), [NativeCareerDialogs](../src/brasfoot/NativeCareerDialogs.java) | Consultas de gestão e visão geral ficam indisponíveis durante a apresentação da data para evitar exposição de resultados futuros. Filtros e navegação ainda têm pendências |
| Gestão por técnico | Comandos de renovação manual, promoção da base, lista de transferências, venda e busca de emprego | [CareerManagement](../src/brasfoot/CareerManagement.java), [NativeCareerDialogs](../src/brasfoot/NativeCareerDialogs.java) | Fluxos presentes no protocolo; não houve certificação individual de todas essas ações nesta revisão |
| Mercado e propostas | Telas nativas Procura e Times; compras e negociação com IA, contrapropostas e propostas pendentes entre técnicos humanos | [NativeTransferSearch](../src/brasfoot/NativeTransferSearch.java), [NativeTransferMarket](../src/brasfoot/NativeTransferMarket.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | Testes de busca, compra, negociação e contraproposta aprovados por `build_adapter.py --test`; empréstimos permanecem desabilitados |
| Treino | Foco e auxiliar na tela nativa encaminhados a comandos distintos do host, com confirmação e persistência | [NativeTraining](../src/brasfoot/NativeTraining.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | [Matriz de 08/11](implementacao-treino-auxiliar-completo.md): API, janelas reais, erro, retomada, precedência e fronteira semanal; limites explícitos |
| Estádio | Telas e comandos de preços e expansão encaminhados ao host; orçamento obtido pelo painel original | [NativeStadium](../src/brasfoot/NativeStadium.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | Parcialmente validado: obra e débito de expansão ainda precisam de confirmação completa; não tratar como correção concluída |
| Persistência e recuperação | Checkpoints Kryo e manifesto no host; retomada em outra JVM; data interrompida volta à preparação | [SessionHost](../src/brasfoot/SessionHost.java), [GameAdapter](../src/brasfoot/GameAdapter.java) | Salvamento, retomada e interrupção aprovados com quatro clientes. Tokens precisam ser emitidos novamente ao reiniciar o host |
| Configurações da sessão | Catálogo de 22 opções, revisão, autorização do host, validação, persistência, capacidades e pendências; dez opções editáveis | [SessionOptions](../src/brasfoot/SessionOptions.java), [NativeSessionOptions](../src/brasfoot/NativeSessionOptions.java), [OptionsJson](../src/brasfoot/OptionsJson.java) | Suíte específica aprovada. As doze opções sem integração permanecem bloqueadas na interface e na API |
| Relógio e apresentação da IA | Velocidades nativas com e sem humanos, tarefa única por minuto, proteção contra tarefa antiga e intervalo automático sem humanos | [PlaybackClock](../src/brasfoot/PlaybackClock.java), [MatchPlayback](../src/brasfoot/MatchPlayback.java), [SessionHost](../src/brasfoot/SessionHost.java) | Tabelas originais, executor controlável e projeção temporal de IA aprovados; resultados futuros e estatísticas finais são ocultados durante a apresentação |
| Desempenho dos snapshots | Dados anteriores de IA reutilizados, projeção linear dos placares, cache por sequência/checkpoint, envio de `/state` fora do monitor e controle de atualizações Swing pendentes | [GameAdapter](../src/brasfoot/GameAdapter.java), [SessionHost](../src/brasfoot/SessionHost.java), [NativeClient](../src/brasfoot/NativeClient.java) | Carreira com quatro clientes aprovada sem ampliar o timeout; caso de 388 partidas e 776 clubes documentado no histórico resolvido |

## Configurações: entregue e pendente

As dez chaves habilitadas são `humanSpeedIndex`, `nonHumanSpeedIndex`, `openLineupAtHalfTime`, `soundsEnabled`, `humanScoreColorsEnabled`, `humanScoreColorIndex`, `highlightHomeEvents`, `windowThemeIndex`, `playerListColorsEnabled` e `playerListPalette`.

O host edita; participantes consultam. `--tick-ms` explícito é uma sobrescrita de diagnóstico, publica `CLI_OVERRIDE` e bloqueia alterações de velocidade. Sem ele, valem os índices da sessão. Checkpoints de recuperação continuam obrigatórios.

Ainda faltam: auto-save adicional; avisos e callbacks nativos de gestão; configuração de renovação automática; apresentação de disputas de pênaltis sem humanos; agrupamento e filtros por país, competição, estaduais e histórico regional. O [registro da implementação](implementacao-configuracoes-online.md) lista as doze chaves e o motivo de bloqueio. A [especificação](especificacao/spec-configuracoes-online.md) mantém os critérios de aceite completos.

Os consumidores foram refinados em [avisos/leilões](especificacao/spec-notificacoes-leiloes-online.md) e [consultas históricas](especificacao/spec-consultas-historicas-online.md). Mensagens podem conter decisões econômicas, e leilões geram disputas/transações. Retrospectos, trocas, confrontos e rankings dependem de coleções não publicadas; manter um callback de janela não certifica dados completos nem autoridade de mutações.

## Problemas abertos

Auditoria documental adicional em 08/11/2026: o [relatório de requisitos](estado-agente-requisitos.md) rastreia as áreas do código e diferencia evidência histórica de validação nova. O [backlog de paridade](task-paridade-nativa.md) prioriza PN-01 a PN-12 e a [spec geral](especificacao/spec-paridade-nativa.md) define o contrato de certificação. Não foram repetidas suítes nem reconstruídos pacotes nesta auditoria.

Limites adicionais confirmados por inspeção: capitão e batedores bloqueados em `NativeLineup.java:23`; juniores indisponíveis no editor em `:45,53`; seleções e regionais desativados em `CareerEngine.java:42`. A promoção da base já presente não equivale ao uso direto de juniores na escalação.

PN-08 corrigiu a projecao da academia, promocao autoritativa, refresh de janela, concorrencia com editor e rollback HTTP. [Task resolvida](resolvidos/task-juniores-nativos-online.md); uso direto de juniores na escalacao continua separado.

Outro bug confirmado por execução na principal: [inelegibilidade projetada como lesão](task-projecao-lesoes-online.md), PN-12. Em folga, jogador saudável recebe fim de contusão `Long.MAX_VALUE` na projeção; a janela real mostra cruzes e data fictícia. Não há evidência de alteração médica no save autoritativo. Revisões independentes das branches de [treino](revisao-independente-treino.md) e [folga](revisao-independente-folga.md) não significam integração de seus recortes à principal.

Diagnóstico histórico anterior à integração: o treino era parcial porque a caixa do auxiliar mantinha listener local. Essa lacuna foi corrigida e validada no [auxiliar completo](implementacao-treino-auxiliar-completo.md), incorporado em `fd4aa24f`. A [task](task-treino-auxiliar-online.md) preserva histórico e limites. A validação da obra do estádio permanece delimitada na matriz de gestão.

Validação independente inicial de 08/11: [treino](especificacao/spec-treino-auxiliar-online.md) teve consumidor, precedência, quatro combinações Kryo, agenda de 52 domingos e consumo único da fila aprovados; o comando completo e o avanço online foram validados posteriormente nos registros da integração. [Estádio](validacao-gestao-nativa.md) teve orçamento/débito, rejeição de obra duplicada, identidade Kryo e conclusão pelo consumidor nativo aprovados; faltam interface, HTTP e avanço normal. Os resultados permanecem separados por cenário e momento.

| Tema | Estado e referência |
| --- | --- |
| Geração das ligas e criação estrangeira | Corrigida em branch PN-02: seleção aplicada antes de gerar ligas; nomes estrangeiros e assets estáveis suportados. [Registro e limites](implementacao-calendario-paises.md); ainda não integrado |
| Datas sem humanos | O relógio e o intervalo automático estão integrados, mas ainda há barreira inicial de Pronto e avanço pelo host. Isso não equivale à automação completa das folgas |
| Escalação em folga | Três acessos e mensagens corrigidos; automação de datas e planejamento antecipado seguem na [task](task-escalacao.md) |
| Navegação do calendário | Catálogo de ligas, escopo sem clube, ordenação cronológica e atualização de janelas ainda precisam das correções propostas em [calendários](task-calendarios.md) |
| Outras integrações | Doze configurações, empréstimos e validação completa da expansão do estádio continuam pendentes |
| Acesso às opções | Botão adicional removido; Opções → Preferências e F3 integrados ao host com painel e paleta nativos. [Fontes e JAR validados, com rebuild concluído](implementacao-opcoes-nativas-online.md); [task](task-opcoes-nativas-online.md) aguardando validação do usuário |
| Áudio dos eventos | [Task aberta](task-audio-eventos-repetidos.md): usuário relata efeitos sem evento aparente e possível repetição; falta reproduzir e identificar a causa |

## Evidência de validação

Diagnósticos estáticos adicionais: [seleção da gestão](task-selecao-gestao-online.md), [preços do estádio](task-precos-estadio-online.md), [resultado de pênaltis](task-resultado-penaltis-online.md) e [ajustes durante a partida](task-ajustes-partida-online.md). Essas pendências não foram certificadas pelas execuções históricas abaixo; as tasks registram separadamente código, contracasos e validação dinâmica futura.

Complementos estáticos de 08/11, sem nova execução do jogo: [habilidades individuais](task-habilidades-individuais-online.md) identifica modo cliente fixado em força geral e ausência dos sete atributos; [índices da Procura/Times](task-indices-mercado-online.md) rastreia a diferença entre elencos projetados e índices globais registrados pelos assets. Os testes de encaminhamento de propostas citados acima não certificam esses dois contratos. Reprodução dinâmica e integração permanecem pendentes.

Na revisão de 6 de novembro de 2026 passaram:

- `python tools/build_adapter.py --test`: regras do adaptador, coordenação, elenco grande e mercado nativo.
- `python tools/tests/test_session_options.py`: contrato, autorização, revisão, idempotência, falha de gravação, retomada, pendências, tela sem gravação local e projeção de IA.
- `python tools/tests/test_session.py`: clientes de terminal, preparação, intervalo, resultados e save final.
- `python tools/tests/test_native_session.py`: dois clientes Swing, resultados compartilhados, áudio e simulação local desativada.
- `python tools/tests/test_career_session.py`: quatro clientes Swing, duas rodadas, barreiras, retomada e recuperação de interrupção.

Essas execuções já foram concluídas durante as correções; não foram repetidas apenas para reorganizar a documentação. `test_native_create.py` e `test_career.py` são coberturas disponíveis, sem nova execução nesta revisão. Os relatórios e capturas locais ficam em `analysis/`; os documentos em [resolvidos](resolvidos/README.md) preservam as conclusões no repositório.
