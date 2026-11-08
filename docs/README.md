# Documentação do Brasfoot Online

Revisado em 8 de novembro de 2026. Este índice separa o estado implementado, o planejamento ainda aberto e o histórico de problemas corrigidos.

| Documento | Conteúdo e estado |
| --- | --- |
| [Mapa da implementação](mapa-implementacao.md) | Funcionalidades presentes, código responsável, testes e limites conhecidos |
| [Integração de 08/11](integracao-main-2026-11-08.md) | Escopo combinado, revisão, verificações e pacote da principal |
| [Progresso de implementação](estado-agente-implementacao.md) | Entregas em branches e checkpoint de parada solicitado pelo usuário |
| [Implementação da folga](implementacao-escalacao-folga.md) | Coerência dos acessos; automação e planejamento antecipado pendentes |
| [Implementação do auxiliar](implementacao-treino-auxiliar-completo.md) | Comando autoritativo, confirmação, persistência e treino nativo |
| [Auditoria do agente de requisitos](estado-agente-requisitos.md) | Rodada de 08/11/2026: inventário nativo, evidências, limitações e retomada |
| [Paridade nativa](task-paridade-nativa.md) | Backlog aberto PN-01 a PN-12, prioridades e dependências |
| [Treino do auxiliar](task-treino-auxiliar-online.md) | Implementação e validação técnica registradas; limites e aceite explícitos |
| [Juniores nativos](task-juniores-nativos-online.md) | Divergência de projeção reproduzida em carreira isolada; callbacks da academia ainda offline |
| [Lesões na projeção](task-projecao-lesoes-online.md) | Bug confirmado: inelegibilidade/folga vira contusão até prazo fictício |
| [Validação da gestão](validacao-gestao-nativa.md) | Execuções independentes de modelo/comando direto e matriz do que ainda falta |
| [Revisão independente do treino](revisao-independente-treino.md) | JAR da branch e janela real verificados; bloqueio provisório, sem integração à principal |
| [Revisão independente da folga](revisao-independente-folga.md) | Execuções e capturas do JAR da branch; primeira falha de Robot preservada e repetição aprovada |
| [Revisão independente do auxiliar online](revisao-independente-auxiliar-online.md) | Motor e retomada aprovados; defeito com editor aberto corrigido e revalidado no JAR identificado, com limites explícitos |
| [Carreira multiplayer](multiplayer.md) | Como funciona a sessão, preparação, partidas, rede e recuperação |
| [Implementação das configurações](implementacao-configuracoes-online.md) | Dez opções habilitadas e doze ainda sem integração; protocolo e validação |
| [Especificações](especificacao/README.md) | Requisitos e critérios de aceite em arquivos `spec-...` |
| [Especificação das configurações](especificacao/spec-configuracoes-online.md) | Requisitos completos; implementação parcial |
| [Investigação dos calendários](task-calendarios.md) | Pendências de geração de ligas, folgas, navegação e cobertura internacional |
| [Investigação da escalação](task-escalacao.md) | Pendências da interface em datas sem partida do clube |
| [Configurações online e velocidade](resolvidos/task-configuracoes-online.md) | Entrega atual resolvida por aceite do usuário; áudio e acesso nativo permanecem em tasks próprias |
| [Áudio repetido ou fora de contexto](task-audio-eventos-repetidos.md) | Aberta: reproduzir, identificar a origem dos disparos e corrigir duplicações |
| [Botão nativo Opções](task-opcoes-nativas-online.md) | Implementada; [registro e validação](implementacao-opcoes-nativas-online.md), aguardando teste do usuário |
| [Problemas resolvidos](resolvidos/README.md) | Diagnósticos arquivados com correção e evidência de validação |

Especificações ficam em `especificacao/` com prefixo `spec-`. Investigações usam o prefixo `task-`; as abertas permanecem na raiz de `docs/`. Uma implementação parcial permanece nesta pasta. Uma task vai para `resolvidos/` quando o problema específico foi corrigido e validado; o registro deve informar o escopo resolvido e apontar as pendências relacionadas. Ao mover um documento, atualizar os links que apontam para ele.

O fluxo de investigação, especificação, implementação, validação, rebuild e reabertura está documentado na skill [brasfoot-task-spec](../skills/brasfoot-task-spec/SKILL.md).
