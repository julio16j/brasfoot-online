# Paridade nativa da carreira multiplayer

Status: requisitos gerais abertos, revisão de 08/11/2026. [Backlog único](../tasks/sem-prioridade/task-paridade-nativa.md); [inventário e evidências](../estado-agente-requisitos.md). Esta spec não certifica implementação.

## Referência e escopo

A referência é o motor e os artefatos locais de Brasfoot 22–23 em `analysis/brasfoot.jar`, `analysis/decompiled/` e `game/Brasfoot22-23/`. Não presumir funcionalidades de outras edições nem calendários reais atuais. A decompilação ajuda a rastrear consumidores, mas precisa ser confrontada com o artefato executado nos testes de integração.

Cada capacidade nativa identificada deve ter classificação explícita: implementada e validada; presente sem validação; parcial; ausente no adaptador; bug confirmado; investigação pendente. Evidências históricas mantêm data, cenário e limitações. Áreas não investigadas impedem declarar paridade total.

## Contrato comum

- O host é dono do motor, do avanço das datas, dos resultados e dos saves. Clientes enviam intenção; controles e listeners nativos não podem confirmar mutações apenas na projeção local.
- Ações de clube pertencem ao técnico autorizado; opções globais seguem a autoridade do host da [spec de configurações](spec-configuracoes-online.md). Ações usam identidades estáveis e revisões pertinentes, com rejeição sem efeito parcial de pedidos inválidos.
- A confirmação de um comando precisa refletir o efeito no consumidor nativo, o checkpoint e o estado entregue aos clientes. Falha de armazenamento não pode ser apresentada como alteração durável.
- Entrada tardia, reconexão e retomada recebem o estado confirmado. Uma data interrompida segue a recuperação documentada; ofertas e demais metadados devem permanecer coerentes com o checkpoint recuperado.
- Folga de um clube não permite ignorar jogos de outros técnicos. Filtragem da apresentação não remove competições do motor. Avisos e decisões pertencentes a um técnico devem chegar ao destinatário correto sem revelar resultados futuros.
- Funções sem integração permanecem explicitamente indisponíveis, inclusive por teclado e callbacks. Uma aparência nativa preservada não basta como aceite funcional.

## Critérios observáveis

Para cada linha do inventário, registrar: origem nativa; acesso na interface; comando e autorização; efeito no motor; estado sincronizado; persistência; evidência da execução e limitações. Exercitar sucesso, rejeição, repetição/conflito quando aplicável e retomada. Gestão exige testar o resultado econômico/esportivo no ciclo nativo: por exemplo, uma obra deve debitar uma vez e concluir no prazo do motor; treino deve alcançar o processamento semanal.

A certificação do launcher usa o JAR distribuído. Um teste unitário de negociação não certifica uma compra gráfica, e uma carreira com quatro técnicos não certifica automaticamente todas as combinações de dezesseis participantes ou países. Não exigir rebuild em revisões exclusivamente documentais.

## Decisões ainda abertas

Definir suporte a seleções e regionais, empréstimos, leilões, capitão/batedores, juniores na escalação e táticas salvas conforme levantamento local, além da política de paradas de gestão durante automação das datas. A precedência do auxiliar sobre foco manual já foi rastreada e validada no [contrato de treino](spec-treino-auxiliar-online.md); preservar esse comportamento, com o limite de acesso pelo botão ainda registrado na task correspondente. Essas decisões não mudam as specs já existentes de [Opções](spec-opcoes-nativas-online.md), nem encerram as tasks de calendários, folga ou áudio.

As outras frentes abertas e seu estado concreto estão no [backlog rastreável](../tasks/sem-prioridade/task-paridade-nativa.md). Em particular, calendário, Procura, persistência de referências, identidade do técnico, consultas históricas, ajustes ao vivo, finanças, pênaltis, estatísticas e Academia secundária têm tasks/specs específicas; critérios propostos nelas não significam implementação nem validação concluída.
