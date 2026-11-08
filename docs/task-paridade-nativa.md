# Paridade com o Brasfoot nativo local

Status: aberta; primeira rodada documental em 08/11/2026. Nenhuma funcionalidade implementada nesta rodada. Requisitos: [spec](especificacao/spec-paridade-nativa.md). Evidências, inventário e retomada: [relatório do agente](estado-agente-requisitos.md).

## Objetivo e prioridade

Comprovar os fluxos da versão 22–23 incluída no repositório desde a interface até o efeito no motor autoritativo, persistência e retorno aos participantes. P1 significa impacto na continuidade ou confiabilidade da carreira; P2, capacidade nativa faltante ou certificação necessária; P3, investigação de áreas ainda sem contrato. As prioridades abaixo são propostas de diagnóstico, não autorização para implementar nesta execução.

| ID | Prioridade / classificação | Trabalho e evidência | Dependências e aceite |
| --- | --- | --- | --- |
| PN-01 | Implementado; validação técnica e limites registrados | [Auxiliar online](implementacao-treino-auxiliar-completo.md), [task](task-treino-auxiliar-online.md) | Comando, projeção, persistência e motor cobertos; aceite de uso e limites permanecem explícitos |
| PN-02 | P1 / bug confirmado e integração parcial | Geração por países ocorre antes da seleção final, barreiras de folga e navegação; reutilizar [calendários](task-calendarios.md) | Validar criação por IDs e países mistos antes de automatizar datas; preservar jogo de outro técnico |
| PN-03 | Parcial: coerência da interface implementada | [Escalação na folga](task-escalacao.md), [entrega](implementacao-escalacao-folga.md) | Três acessos e mensagens cobertos; automação/planejamento antecipado continuam pendentes |
| PN-04 | P1 / investigação pendente | [Áudio repetido](task-audio-eventos-repetidos.md) | Reproduzir com identidade dos eventos; teste anterior de disparos não certifica deduplicação audível |
| PN-05 | P2 / parcial | Doze opções bloqueadas, incluindo avisos, leilão e filtros; [registro existente](implementacao-configuracoes-online.md) | Integrar consumidor efetivo e destinatários; não apenas habilitar setters |
| PN-06 | P2 / ausente no adaptador | Capitão, faltas, escanteios e falso 9 bloqueados em `NativeLineup.java:23`; [contrato inicial](especificacao/spec-papeis-escalacao-online.md) | IDs estáveis, autorização por clube, elegibilidade, efeito no motor e recuperação |
| PN-07 | P2 / ausente no adaptador | Empréstimos desabilitados em `NativeTransferMarket.java:25`; [contrato e probe](especificacao/spec-emprestimos-online.md) | Modelo e Kryo testados; proposta/aceite humano, salários e fluxo normal ainda pendentes |
| PN-08 | P1 / bug confirmado de projeção e suporte parcial | [Juniores nativos](task-juniores-nativos-online.md): base do cliente diverge do host; academia mantém callbacks offline. Juniores também não selecionáveis no editor (`NativeLineup.java:45,53`) | Integrar academia e identidades; separar promoção de uso direto da base e bloquear mutações locais sem suporte |
| PN-09 | P2 / presente sem validação individual completa | Preços/obra de estádio, treino manual, renovação, promoção, venda e emprego; [matriz e execução](validacao-gestao-nativa.md) | Obra testada no comando direto e consumidor com Kryo; faltam interface/HTTP e demais ações |
| PN-10 | P2 / parcial por configuração explícita | Seleções e regionais desligados em `CareerEngine.java:42`; avisos de técnicos e renovação configurados em `:43` | Investigar callbacks locais e decisões multiplayer; não confundir desligamento de regionais com ausência de todos os estaduais |
| PN-11 | P3 / investigação pendente | Histórico, estatísticas individuais, táticas salvas/padrão, editor e demais menus nativos | Enumerar callbacks locais e separar consulta de mutação; não declarar ausência apenas por busca textual |
| PN-12 | P1 / bug confirmado por execução | [Lesões fictícias na projeção](task-projecao-lesoes-online.md): folga/inelegibilidade vira contusão até Long.MAX_VALUE | Separar estado médico de elegibilidade contextual, sem tornar jogador bloqueado escalável |

## Validação e encerramento

Nesta rodada houve inspeção estática, leitura das tasks abertas/resolvidas e das evidências históricas. Não houve nova execução de partida, rebuild ou teste do launcher. O caminho de uso principal é `Brasfoot-Online.cmd` → `dist/brasfoot-online.jar`; testes sobre `build/classes` não certificam esse pacote.

Manter esta task aberta enquanto o inventário tiver lacunas ou critérios sem evidência. A conclusão da rodada documental não significa paridade alcançada. O aceite do acesso nativo de Opções permanece na [task existente](task-opcoes-nativas-online.md), sem duplicação ou encerramento por esta auditoria.
