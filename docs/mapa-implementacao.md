# Mapa da implementação

Revisado em 6 de novembro de 2026, com base no código desta pasta de trabalho e nas validações registradas. Este mapa descreve os fontes; não afirma que os pacotes já existentes em `dist/` foram reconstruídos com essas alterações.

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
| Calendário e temporadas | Host avança datas; motor original mantém competições e efeitos pós-jogo | [CareerEngine](../src/brasfoot/CareerEngine.java) | Duas rodadas consecutivas aprovadas nesta revisão. Existe teste de várias temporadas em `test_career.py`; limitações internacionais permanecem na investigação de calendários |
| Consultas da carreira | Calendário, resultados, classificações, finanças e elenco do técnico | [CareerViews](../src/brasfoot/CareerViews.java), [NativeCareerDialogs](../src/brasfoot/NativeCareerDialogs.java) | Consultas de gestão e visão geral ficam indisponíveis durante a apresentação da data para evitar exposição de resultados futuros. Filtros e navegação ainda têm pendências |
| Gestão por técnico | Comandos de renovação manual, promoção da base, lista de transferências, venda e busca de emprego | [CareerManagement](../src/brasfoot/CareerManagement.java), [NativeCareerDialogs](../src/brasfoot/NativeCareerDialogs.java) | Fluxos presentes no protocolo; não houve certificação individual de todas essas ações nesta revisão |
| Mercado e propostas | Telas nativas Procura e Times; compras e negociação com IA, contrapropostas e propostas pendentes entre técnicos humanos | [NativeTransferSearch](../src/brasfoot/NativeTransferSearch.java), [NativeTransferMarket](../src/brasfoot/NativeTransferMarket.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | Testes de busca, compra, negociação e contraproposta aprovados por `build_adapter.py --test`; empréstimos permanecem desabilitados |
| Treino | Escolha do foco na tela nativa encaminhada ao comando `training-focus` do host | [NativeTraining](../src/brasfoot/NativeTraining.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | Integração presente; sem teste específico de ponta a ponta registrado nesta revisão |
| Estádio | Telas e comandos de preços e expansão encaminhados ao host; orçamento obtido pelo painel original | [NativeStadium](../src/brasfoot/NativeStadium.java), [CareerManagement](../src/brasfoot/CareerManagement.java) | Parcialmente validado: obra e débito de expansão ainda precisam de confirmação completa; não tratar como correção concluída |
| Persistência e recuperação | Checkpoints Kryo e manifesto no host; retomada em outra JVM; data interrompida volta à preparação | [SessionHost](../src/brasfoot/SessionHost.java), [GameAdapter](../src/brasfoot/GameAdapter.java) | Salvamento, retomada e interrupção aprovados com quatro clientes. Tokens precisam ser emitidos novamente ao reiniciar o host |
| Configurações da sessão | Catálogo de 22 opções, revisão, autorização do host, validação, persistência, capacidades e pendências; dez opções editáveis | [SessionOptions](../src/brasfoot/SessionOptions.java), [NativeSessionOptions](../src/brasfoot/NativeSessionOptions.java), [OptionsJson](../src/brasfoot/OptionsJson.java) | Suíte específica aprovada. As doze opções sem integração permanecem bloqueadas na interface e na API |
| Relógio e apresentação da IA | Velocidades nativas com e sem humanos, tarefa única por minuto, proteção contra tarefa antiga e intervalo automático sem humanos | [PlaybackClock](../src/brasfoot/PlaybackClock.java), [MatchPlayback](../src/brasfoot/MatchPlayback.java), [SessionHost](../src/brasfoot/SessionHost.java) | Tabelas originais, executor controlável e projeção temporal de IA aprovados; resultados futuros e estatísticas finais são ocultados durante a apresentação |
| Desempenho dos snapshots | Dados anteriores de IA reutilizados, projeção linear dos placares, cache por sequência/checkpoint, envio de `/state` fora do monitor e controle de atualizações Swing pendentes | [GameAdapter](../src/brasfoot/GameAdapter.java), [SessionHost](../src/brasfoot/SessionHost.java), [NativeClient](../src/brasfoot/NativeClient.java) | Carreira com quatro clientes aprovada sem ampliar o timeout; caso de 388 partidas e 776 clubes documentado no histórico resolvido |

## Configurações: entregue e pendente

As dez chaves habilitadas são `humanSpeedIndex`, `nonHumanSpeedIndex`, `openLineupAtHalfTime`, `soundsEnabled`, `humanScoreColorsEnabled`, `humanScoreColorIndex`, `highlightHomeEvents`, `windowThemeIndex`, `playerListColorsEnabled` e `playerListPalette`.

O host edita; participantes consultam. `--tick-ms` explícito é uma sobrescrita de diagnóstico, publica `CLI_OVERRIDE` e bloqueia alterações de velocidade. Sem ele, valem os índices da sessão. Checkpoints de recuperação continuam obrigatórios.

Ainda faltam: auto-save adicional; avisos e callbacks nativos de gestão; configuração de renovação automática; apresentação de disputas de pênaltis sem humanos; agrupamento e filtros por país, competição, estaduais e histórico regional. O [registro da implementação](implementacao-configuracoes-online.md) lista as doze chaves e o motivo de bloqueio. A [especificação](especificacao/spec-configuracoes-online.md) mantém os critérios de aceite completos.

## Problemas abertos

| Tema | Estado e referência |
| --- | --- |
| Geração das ligas e criação estrangeira | Seleção de países aplicada depois da geração por IDs; criação por nome limitada ao Brasil. Ver [calendários](task-calendarios.md) |
| Datas sem humanos | O relógio e o intervalo automático estão integrados, mas ainda há barreira inicial de Pronto e avanço pelo host. Isso não equivale à automação completa das folgas |
| Escalação em folga | Próximo confronto pode aparecer enquanto não há partida atual; acessos à escalação não explicam o bloqueio de forma consistente. Ver [escalação](task-escalacao.md) |
| Navegação do calendário | Catálogo de ligas, escopo sem clube, ordenação cronológica e atualização de janelas ainda precisam das correções propostas em [calendários](task-calendarios.md) |
| Outras integrações | Doze configurações, empréstimos e validação completa da expansão do estádio continuam pendentes |
| Acesso às opções | [Entrega atual das configurações resolvida por aceite do usuário](resolvidos/task-configuracoes-online.md). A [task de Opções nativas](task-opcoes-nativas-online.md) permanece aberta e acompanha a remoção do botão adicional e o envio das alterações pelo botão original |
| Áudio dos eventos | [Task aberta](task-audio-eventos-repetidos.md): usuário relata efeitos sem evento aparente e possível repetição; falta reproduzir e identificar a causa |

## Evidência de validação

Na revisão de 6 de novembro de 2026 passaram:

- `python tools/build_adapter.py --test`: regras do adaptador, coordenação, elenco grande e mercado nativo.
- `python tools/tests/test_session_options.py`: contrato, autorização, revisão, idempotência, falha de gravação, retomada, pendências, tela sem gravação local e projeção de IA.
- `python tools/tests/test_session.py`: clientes de terminal, preparação, intervalo, resultados e save final.
- `python tools/tests/test_native_session.py`: dois clientes Swing, resultados compartilhados, áudio e simulação local desativada.
- `python tools/tests/test_career_session.py`: quatro clientes Swing, duas rodadas, barreiras, retomada e recuperação de interrupção.

Essas execuções já foram concluídas durante as correções; não foram repetidas apenas para reorganizar a documentação. `test_native_create.py` e `test_career.py` são coberturas disponíveis, sem nova execução nesta revisão. Os relatórios e capturas locais ficam em `analysis/`; os documentos em [resolvidos](resolvidos/README.md) preservam as conclusões no repositório.
