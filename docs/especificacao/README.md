# Especificações

Especificações usam o prefixo `spec-` e descrevem requisitos e critérios de aceite. A existência do documento não significa que a implementação está concluída.

| Documento | Estado |
| --- | --- |
| [Paridade nativa](spec-paridade-nativa.md) | Requisitos gerais abertos; [inventário e evidências](../estado-agente-requisitos.md), sem certificação de paridade completa |
| [Treino semanal e auxiliar](spec-treino-auxiliar-online.md) | Contrato do consumidor nativo, precedência, protocolo e aceite; probe independente de motor e Kryo aprovado |
| [Empréstimos](spec-emprestimos-online.md) | Consumidor e persistência testados; protocolo, consentimento e cenários restantes abertos |
| [Papeis na escalacao](spec-papeis-escalacao-online.md) | Integracao da preparacao concluida; efeitos dinamicos e retomada ainda em validacao |
| [Disponibilidade dos jogadores](spec-disponibilidade-jogadores-online.md) | Contrato PN-12: prazo médico real, contrato e elegibilidade contextual separados |
| [Juniores nativos](spec-juniores-nativos-online.md) | Dashboard e consulta/promoção em Times parcialmente integrados; frescor, aceite do percurso e uso direto na escalação seguem separados |
| [Áudio de eventos](spec-audio-eventos-online.md) | Identidade, baseline, escopo e composição de efeitos; diferenças nativas e validações futuras explícitas |
| [Amistosos na carreira](spec-amistosos-carreira-online.md) | Datas e negociação nativas, disponibilidade autoritativa, termos e consentimento multiplayer ainda abertos |
| [Configurações online](spec-configuracoes-online.md) | Implementação parcial; ver [progresso e pendências](../implementacoes/implementacao-configuracoes-online.md) |
| [Avisos e leilões](spec-notificacoes-leiloes-online.md) | Consumidores de mensagens, mudanças de técnicos e disputa econômica; autoridade, persistência e interação ainda abertas |
| [Consultas históricas](spec-consultas-historicas-online.md) | Resumos de Artilheiros/Assistências no overview; ranking/trocas de técnicos, campeões/troféus, prêmios individuais e seleções da rodada/ano, retrospectos, transferências, confrontos e históricos detalhados ainda abertos |
| [Filtros da apresentacao](spec-filtros-apresentacao-online.md) | Controles e recorte host-backed parciais; aceite visual e equivalencia nativa em aberto |
| [Calendários e seis ligas](spec-calendarios-ligas-online.md) | Agenda/Calendário e Tabelas/Rodadas nativos validados; CAL adicional e avanço/retomada permanecem em aberto |
| [Expansão nativa do estádio](spec-expansao-estadio-online.md) | Prioridade alta; requisitos iniciais para tornar o acesso nativo visível, confirmar obra pelo host e corrigir o título do estádio |
| [Procura nativa de jogadores](spec-pesquisa-jogadores-online.md) | Busca global e catálogo Atuando no por ID integrados; histórico e marcador de promoção são projetados para Times. Aliases de habilidades e ordenação Car. têm diferenças documentadas; o shell de vendedor usa apenas atletas retornados pela busca, não sincroniza retrospecto/títulos/transferências, e o perfil do técnico pode selecionar outra identidade local; o DTO omite suspensões do renderer de Times; botão Transferências ainda lê histórico local; comparação visual/dinâmica, navegação/contexto e aceite completo seguem abertos |
| [Referências na persistência](spec-persistencia-referencias-nativas.md) | Contrato de H→competição/ap→atleta sem renumerar identidades online; diagnóstico estático e aceite aberto |
| [Nacionalidade do técnico](spec-nacionalidade-tecnico-online.md) | Transporte/aplicação/projeção comitados; validação pré-inicialização pode bloquear host novo; teste de arranque frio, retomada e percurso do seletor/JAR pendentes |
| [Convites e troca de clube](spec-empregos-tecnicos-online.md) | Cache de vagas do desempregado é salvo/restaurado no mesmo dia; Agenda ainda alcança contratação nativa local; convites a empregados, troca autoritativa e validação integral continuam abertos |
| [Opções nativas online](spec-opcoes-nativas-online.md) | Implementada; [fontes e JAR validados](../implementacoes/implementacao-opcoes-nativas-online.md), aguardando aceite do usuário |

As investigações usam `task-...`: as abertas ficam na raiz de `docs/`, e as corrigidas e validadas em [resolvidos](../resolvidos/README.md). O [índice principal](../README.md) reúne toda a documentação.
