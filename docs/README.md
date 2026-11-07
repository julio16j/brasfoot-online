# Documentação do Brasfoot Online

Revisado em 6 de novembro de 2026. Este índice separa o estado implementado, o planejamento ainda aberto e o histórico de problemas corrigidos.

| Documento | Conteúdo e estado |
| --- | --- |
| [Mapa da implementação](mapa-implementacao.md) | Funcionalidades presentes, código responsável, testes e limites conhecidos |
| [Carreira multiplayer](multiplayer.md) | Como funciona a sessão, preparação, partidas, rede e recuperação |
| [Implementação das configurações](implementacao-configuracoes-online.md) | Dez opções habilitadas e doze ainda sem integração; protocolo e validação |
| [Especificações](especificacao/README.md) | Requisitos e critérios de aceite em arquivos `spec-...` |
| [Especificação das configurações](especificacao/spec-configuracoes-online.md) | Requisitos completos; implementação parcial |
| [Investigação dos calendários](task-calendarios.md) | Pendências de geração de ligas, folgas, navegação e cobertura internacional |
| [Investigação da escalação](task-escalacao.md) | Pendências da interface em datas sem partida do clube |
| [Configurações online e velocidade](resolvidos/task-configuracoes-online.md) | Entrega atual resolvida por aceite do usuário; áudio e acesso nativo permanecem em tasks próprias |
| [Áudio repetido ou fora de contexto](task-audio-eventos-repetidos.md) | Aberta: reproduzir, identificar a origem dos disparos e corrigir duplicações |
| [Botão nativo Opções](task-opcoes-nativas-online.md) | Aberta: remover Configurações e integrar os controles nativos ao host |
| [Problemas resolvidos](resolvidos/README.md) | Diagnósticos arquivados com correção e evidência de validação |

Especificações ficam em `especificacao/` com prefixo `spec-`. Investigações usam o prefixo `task-`; as abertas permanecem na raiz de `docs/`. Uma implementação parcial permanece nesta pasta. Uma task vai para `resolvidos/` quando o problema específico foi corrigido e validado; o registro deve informar o escopo resolvido e apontar as pendências relacionadas. Ao mover um documento, atualizar os links que apontam para ele.

O fluxo de investigação, especificação, implementação, validação, rebuild e reabertura está documentado na skill [brasfoot-task-spec](../skills/brasfoot-task-spec/SKILL.md).
