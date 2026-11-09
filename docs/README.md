# Documentação do Brasfoot Online

Revisado em 8 de novembro de 2026. Este índice separa o estado implementado, o planejamento ainda aberto e o histórico de problemas corrigidos.

| Documento | Conteúdo e estado |
| --- | --- |
| [Mapa da implementação](mapa-implementacao.md) | Funcionalidades presentes, código responsável, testes e limites conhecidos |
| [Integração de 08/11](integracao-main-2026-11-08.md) | Escopo combinado, revisão, verificações e pacote da principal |
| [Progresso de implementação](estado-agente-implementacao.md) | Entregas em branches e checkpoint de parada solicitado pelo usuário |
| [Implementação da folga](implementacao-escalacao-folga.md) | Coerência dos acessos; automação e planejamento antecipado pendentes |
| [Implementação do auxiliar](implementacao-treino-auxiliar-completo.md) | Comando autoritativo, confirmação, persistência e treino nativo |
| [Implementação PN-02: países das ligas](implementacao-calendario-paises.md) | Geração das ligas dos países escolhidos e criação internacional, validada em branch |
| [Auditoria do agente de requisitos](estado-agente-requisitos.md) | Rodada de 08/11/2026: inventário nativo, evidências, limitações e retomada |
| [Paridade nativa](task-paridade-nativa.md) | Backlog aberto PN-01 a PN-12, prioridades e dependências |
| [Treino do auxiliar](task-treino-auxiliar-online.md) | Implementação e validação técnica registradas; limites e aceite explícitos |
| [Juniores nativos](task-juniores-nativos-online.md) | Divergência de projeção reproduzida em carreira isolada; callbacks da academia ainda offline |
| [Lesões na projeção](task-projecao-lesoes-online.md) | Bug confirmado: inelegibilidade/folga vira contusão até prazo fictício |
| [Finanças e crédito bancário](task-financas-nativas-online.md) | Complemento PN-09: capacidade nativa inventariada; painel contábil e operações bancárias sem integração |
| [Compra de listado e recusa do atleta](task-compra-listados-online.md) | Complemento PN-09: atalho de compra direta não verifica a aceitação nativa; diagnóstico estático |
| [Táticas salvas e padrão](task-taticas-salvas-online.md) | Complemento PN-11: biblioteca local sem persistência online; escalação padrão bloqueada |
| [Habilidades individuais](task-habilidades-individuais-online.md) | Complemento PN-09/PN-11: modo cliente fixado em força geral; atributos e filtros sem projeção completa |
| [Índices da Procura e de Times](task-indices-mercado-online.md) | Complemento PN-09/PN-11: candidatos dependem de índices registrados pelos assets, sem reconciliação completa com o host |
| [Seleção no menu de gestão](task-selecao-gestao-online.md) | Complemento PN-09/PN-11: conversão duplicada, ID contextual ignorado e retirada com intenção inicial invertida |
| [Preços do estádio](task-precos-estadio-online.md) | Complemento PN-09: confirmação nativa desviada e rascunho compartilhado com snapshots |
| [Resultado de disputa de pênaltis](task-resultado-penaltis-online.md) | Complemento PN-05/PN-11: nomes de campos do host e do painel divergem; resultado não aplicado ao objeto nativo |
| [Ajustes durante a partida](task-ajustes-partida-online.md) | Complemento PN-09/PN-11/PN-12: expulsões, banco e validação integral de substituições múltiplas |
| [Estatísticas dos jogadores](task-estatisticas-jogadores-online.md) | Complemento PN-11: colunas nativas dependem de campos e históricos que o snapshot parcial não transporta |
| [Renovação de contratos](task-renovacao-contratos-online.md) | Complemento PN-09: salário e contraproposta ausentes do formulário; cálculo e limites requerem comparação nativa |
| [Nacionalidade do técnico](task-nacionalidade-tecnico-online.md) | Complemento PN-11: escolha do seletor não chega ao host e nacionalidade não é projetada nos perfis |
| [Empregos e troca de clube](task-empregos-tecnicos-online.md) | Complemento PN-09/PN-11: suporte ao desempregado não cobre convites ao técnico empregado; persistência dos convites indefinida |
| [Validação da gestão](validacao-gestao-nativa.md) | Execuções independentes de modelo/comando direto e matriz do que ainda falta |
| [Revisão independente do treino](revisao-independente-treino.md) | JAR da branch e janela real verificados; bloqueio provisório, sem integração à principal |
| [Revisão independente da folga](revisao-independente-folga.md) | Execuções e capturas do JAR da branch; primeira falha de Robot preservada e repetição aprovada |
| [Revisão independente do auxiliar online](revisao-independente-auxiliar-online.md) | Motor e retomada aprovados; defeito com editor aberto corrigido e revalidado no JAR identificado, com limites explícitos |
| [Carreira multiplayer](multiplayer.md) | Como funciona a sessão, preparação, partidas, rede e recuperação |
| [Implementação das configurações](implementacao-configuracoes-online.md) | Dez opções habilitadas e doze ainda sem integração; protocolo e validação |
| [Especificações](especificacao/README.md) | Requisitos e critérios de aceite em arquivos `spec-...` |
| [Especificação das configurações](especificacao/spec-configuracoes-online.md) | Requisitos completos; implementação parcial |
| [Investigação dos calendários](task-calendarios.md) | Diagnóstico original e pendências de folgas, navegação e cobertura internacional |
| [Investigação da escalação](task-escalacao.md) | Pendências da interface em datas sem partida do clube |
| [Configurações online e velocidade](resolvidos/task-configuracoes-online.md) | Entrega atual resolvida por aceite do usuário; áudio e acesso nativo permanecem em tasks próprias |
| [Áudio repetido ou fora de contexto](task-audio-eventos-repetidos.md) | Aberta: reproduzir, identificar a origem dos disparos e corrigir duplicações |
| [Botão nativo Opções](task-opcoes-nativas-online.md) | Implementada; [registro e validação](implementacao-opcoes-nativas-online.md), aguardando teste do usuário |
| [Problemas resolvidos](resolvidos/README.md) | Diagnósticos arquivados com correção e evidência de validação |

Especificações ficam em `especificacao/` com prefixo `spec-`. Investigações usam o prefixo `task-`; as abertas permanecem na raiz de `docs/`. Uma implementação parcial permanece nesta pasta. Uma task vai para `resolvidos/` quando o problema específico foi corrigido e validado; o registro deve informar o escopo resolvido e apontar as pendências relacionadas. Ao mover um documento, atualizar os links que apontam para ele.

O fluxo de investigação, especificação, implementação, validação, rebuild e reabertura está documentado na skill [brasfoot-task-spec](../skills/brasfoot-task-spec/SKILL.md).
