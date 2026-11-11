# Documentação do Brasfoot Online

Revisado em 11 de novembro de 2026. Este índice separa o estado implementado, o planejamento ainda aberto e o histórico de problemas corrigidos.

| Documento | Conteúdo e estado |
| --- | --- |
| [Tasks por prioridade](tasks/README.md) | Calendario/Tabelas e expansao do estadio concluidos; demais tasks sem prioridade |
| [Menu inicial do multiplayer](tasks/sem-prioridade/task-menu-inicial-multiplayer.md) | Três opções centralizadas, formulários por fluxo e velocidades iniciais atualizadas; aceite visual pendente |
| [Feedback ao criar sessão](tasks/sem-prioridade/task-feedback-criacao-sessao.md) | Overlay de carregamento implementado; aceite visual pendente |
| [Checkboxes de países e nome dos checkpoints](tasks/sem-prioridade/task-checkbox-paises-e-nome-saves.md) | Modelo visual estabilizado e novos nomes implementados; limpeza dos saves existentes bloqueada pelo ambiente e aceite visual pendentes |
| [Mapa da implementação](mapa-implementacao.md) | Funcionalidades presentes, código responsável, testes e limites conhecidos |
| [Integração de 08/11](integracao-main-2026-11-08.md) | Escopo combinado, revisão, verificações e pacote da principal |
| [Progresso de implementação](estado-agente-implementacao.md) | Entregas em branches e checkpoint de parada solicitado pelo usuário |
| [Implementação da folga](implementacoes/implementacao-escalacao-folga.md) | Coerência dos acessos; automação e planejamento antecipado pendentes |
| [Implementação do auxiliar](implementacoes/implementacao-treino-auxiliar-completo.md) | Comando autoritativo, confirmação, persistência e treino nativo |
| [Implementacao dos filtros de apresentacao](implementacoes/implementacao-filtros-apresentacao.md) | Controles disponiveis no host e recorte sem alterar a simulacao; testes/aceite visual pendentes |
| [Implementação PN-07: empréstimos](implementacoes/implementacao-emprestimos-online.md) | Listagem, solicitação IA/humana, aceite/recusa, devolução, projeção e retomada HTTP; compra definitiva bloqueada |
| [Implementação PN-02: países das ligas](implementacoes/implementacao-calendario-paises.md) | Geração das ligas dos países escolhidos e criação internacional, integrada na main |
| [Ajustes de escalação ao vivo](implementacoes/implementacao-ajustes-partida-live.md) | Validação completa antes de aplicar substituições e preservação do estado em pedidos inválidos; casos de equipe reduzida e recuperação ainda abertos |
| [Auditoria do agente de requisitos](estado-agente-requisitos.md) | Rodada de 08/11/2026: inventário nativo, evidências, limitações e retomada |
| [Paridade nativa](tasks/sem-prioridade/task-paridade-nativa.md) | Backlog aberto PN-01 a PN-12, prioridades e dependências |
| [Expansao nativa do estadio - concluida](resolvidos/task-expansao-estadio-online.md) | Botao, titulo e fluxo de obra aceitos pelo usuario; criterios adicionais permanecem na spec |
| [Calendario e Tabelas nativos - recorte concluido](resolvidos/task-calendarios.md) | Consultas host-backed nas janelas nativas; requisitos adicionais na [spec](especificacao/spec-calendarios-ligas-online.md) |
| [Pedido de Procura nativa](tasks/sem-prioridade/task-indices-mercado-online.md) | Task aberta para o relato de falta de paridade com a tela nativa. Busca host-backed e catálogo por ID estão no fonte; navegação/estado, ordenação, seller shell e aceite visual/runtime continuam pendentes. [Spec](especificacao/spec-pesquisa-jogadores-online.md) |
| [Referências nativas na persistência](tasks/sem-prioridade/task-persistencia-referencias-nativas.md) | Reconexão de disciplina e histórico integrada parcialmente; legado e consumidores nativos restantes em validação. [Implementação](implementacoes/implementacao-persistencia-referencias.md) · [spec](especificacao/spec-persistencia-referencias-nativas.md) |
| [Nacionalidade do técnico](tasks/sem-prioridade/task-nacionalidade-tecnico-online.md) | Transporte/projeção comitados em `fa6d53fa`; validação pré-inicialização bloqueia o host novo e ainda falta aceite do ciclo de emprego. [Spec](especificacao/spec-nacionalidade-tecnico-online.md) |
| [Academia pelo Times](tasks/sem-prioridade/task-academia-via-times-online.md) | Consulta remota de juniores e promoção própria já integradas no worktree; revisar frescor, projeção e bloqueios na janela remota. Dispensa/apelido seguem indisponíveis; aceite no JAR pendente |
| [Treino do auxiliar](tasks/sem-prioridade/task-treino-auxiliar-online.md) | Implementação e validação técnica registradas; limites e aceite explícitos |
| [Juniores nativos](resolvidos/task-juniores-nativos-online.md) | Projecao da academia corrigida, promocao e rejeicoes validadas; uso direto na escalacao tem escopo proprio |
| [Lesões na projeção](tasks/sem-prioridade/task-projecao-lesoes-online.md) | Bug confirmado: inelegibilidade/folga vira contusão até prazo fictício |
| [Finanças e crédito bancário](tasks/sem-prioridade/task-financas-nativas-online.md) | Complemento PN-09: capacidade nativa inventariada; painel contábil e operações bancárias sem integração |
| [Compra de listado e recusa do atleta](tasks/sem-prioridade/task-compra-listados-online.md) | Complemento PN-09: atalho de compra direta não verifica a aceitação nativa; diagnóstico estático |
| [Táticas salvas e padrão](tasks/sem-prioridade/task-taticas-salvas-online.md) | Complemento PN-11: biblioteca local sem persistência online; escalação padrão bloqueada |
| [Habilidades individuais](tasks/sem-prioridade/task-habilidades-individuais-online.md) | Complemento PN-09/PN-11: modo cliente fixado em força geral; atributos e filtros sem projeção completa |
| [Índices da Procura e de Times](tasks/sem-prioridade/task-indices-mercado-online.md) | Complemento PN-09/PN-11: busca autoritativa via host, catálogo por ID e guardas de callback no fonte; ordem seller/viewer e projeção do histórico corrigidas, Academia remota disponível. Contexto/elenco/árvore seller, custo de varredura por página, paridade detalhada, ordenação Car., frescor após abertura e validação visual/runtime pendentes |
| [Seleção no menu de gestão](tasks/sem-prioridade/task-selecao-gestao-online.md) | Complemento PN-09/PN-11: identidade/ID contextual e retirada com intenção inicial invertida; erro alegado de seleção após sort descartado |
| [Precos do estadio](resolvidos/task-precos-estadio-online.md) | Edicao manual dos quatro setores validada; usuario confirmou a conclusao |
| [Resultado de disputa de pênaltis](tasks/sem-prioridade/task-resultado-penaltis-online.md) | Complemento PN-05/PN-11: nomes de campos do host e do painel divergem; resultado não aplicado ao objeto nativo |
| [Ajustes durante a partida](tasks/sem-prioridade/task-ajustes-partida-online.md) | Complemento PN-09/PN-11/PN-12: expulsões, banco e validação integral de substituições múltiplas |
| [Estatísticas dos jogadores](tasks/sem-prioridade/task-estatisticas-jogadores-online.md) | Complemento PN-11: colunas nativas dependem de campos e históricos que o snapshot parcial não transporta |
| [Renovação de contratos](tasks/sem-prioridade/task-renovacao-contratos-online.md) | Complemento PN-09: salário e contraproposta ausentes do formulário; cálculo e limites requerem comparação nativa |
| [Empregos e troca de clube](tasks/sem-prioridade/task-empregos-tecnicos-online.md) | Consulta online de desempregado integrada parcialmente; Agenda ainda expõe confirmação local via Central de Empregos; [spec](especificacao/spec-empregos-tecnicos-online.md) mantém troca, convites e autoridade abertos |
| [Referências na retomada](tasks/sem-prioridade/task-persistencia-referencias-nativas.md) | P1 proposta: preparação/reconexão nativa de disciplina e histórico omitida; evidência estática e [spec](especificacao/spec-persistencia-referencias-nativas.md) |
| [Validação da gestão](validacao-gestao-nativa.md) | Execuções independentes de modelo/comando direto e matriz do que ainda falta |
| [Revisão independente do treino](revisao-independente-treino.md) | JAR da branch e janela real verificados; bloqueio provisório, sem integração à principal |
| [Revisão independente da folga](revisao-independente-folga.md) | Execuções e capturas do JAR da branch; primeira falha de Robot preservada e repetição aprovada |
| [Revisão independente do auxiliar online](revisao-independente-auxiliar-online.md) | Motor e retomada aprovados; defeito com editor aberto corrigido e revalidado no JAR identificado, com limites explícitos |
| [Carreira multiplayer](multiplayer.md) | Como funciona a sessão, preparação, partidas, rede e recuperação |
| [Implementação das configurações](implementacoes/implementacao-configuracoes-online.md) | Dez opções habilitadas e doze ainda sem integração; protocolo e validação |
| [Especificações](especificacao/README.md) | Requisitos e critérios de aceite em arquivos `spec-...` |
| [Especificação das configurações](especificacao/spec-configuracoes-online.md) | Requisitos completos; implementação parcial |
| [Investigação da escalação](tasks/sem-prioridade/task-escalacao.md) | Pendências da interface em datas sem partida do clube |
| [Configurações online e velocidade](resolvidos/task-configuracoes-online.md) | Entrega atual resolvida por aceite do usuário; áudio e acesso nativo permanecem em tasks próprias |
| [Áudio repetido ou fora de contexto](tasks/sem-prioridade/task-audio-eventos-repetidos.md) | Aberta: reproduzir, identificar a origem dos disparos e corrigir duplicações |
| [Botão nativo Opções](tasks/sem-prioridade/task-opcoes-nativas-online.md) | Implementada; [registro e validação](implementacoes/implementacao-opcoes-nativas-online.md), aguardando teste do usuário |
| [Problemas resolvidos](resolvidos/README.md) | Diagnósticos arquivados com correção e evidência de validação |

Especificações ficam em `especificacao/` com prefixo `spec-`. Investigações usam o prefixo `task-`; as abertas ficam em `docs/tasks/sem-prioridade/` enquanto aguardam priorização. Uma implementação parcial permanece nesta pasta. Uma task vai para `resolvidos/` quando o problema específico foi corrigido e validado; o registro deve informar o escopo resolvido e apontar as pendências relacionadas. Ao mover um documento, atualizar os links que apontam para ele.

O fluxo de investigação, especificação, implementação, validação, rebuild e reabertura está documentado na skill [brasfoot-task-spec](../skills/brasfoot-task-spec/SKILL.md).
