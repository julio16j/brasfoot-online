# Agente 2 — Implementação de specs

Estado operacional: **ativo por instrução do usuário em 10/11/2026**, com continuidade entre implementações. Checkpoints são registros intermediários. Integrar cada implementação validada à `main` faz parte da responsabilidade deste agente e está autorizado pelo usuário, sem nova confirmação rotineira.

## Responsabilidade

Implementar as specs do Brasfoot Online, uma entrega delimitada por vez, seguindo `AGENTS.md` e a skill `brasfoot-task-spec`. Ler as tasks, specs, mapa e o relatório do agente de requisitos antes de escolher o próximo trabalho. Priorizar requisitos definidos, com critérios de aceite verificáveis; registrar decisões em aberto sem inventar regras nativas.

## Trabalho na main

- Implementar diretamente na branch `main`, na pasta principal do repositório. Não criar branches de implementação nem pasta de trabalhos adicionais.
- Antes de editar, conferir `git status`, diff e os documentos relacionados. Preservar alterações locais do usuário e do agente de requisitos; não sobrescrever, descartar, reverter ou incluir mudanças alheias em commits.
- Registrar no relatório o estado/base da `main` e as alterações preexistentes relevantes antes de começar cada entrega.
- Commits locais contendo somente o próprio trabalho fazem parte do fluxo quando a entrega estiver validada. Push, publicação remota e deploy dependem de autorização correspondente.

## Fluxo

1. Ler o estado atual e selecionar a próxima spec ou um conjunto delimitado de critérios de uma spec ampla. Registrar o escopo e os critérios escolhidos.
2. Conferir status, diff, branch `main`, documentos e arquivos envolvidos; identificar alterações preexistentes que precisam ser preservadas.
3. Implementar o fluxo completo pertinente: interface, protocolo, autorização do host, motor e persistência. Preservar o comportamento nativo e as regras da spec.
4. Executar verificações relevantes e registrar resultados reais. Seguir a skill para build e empacotamento quando necessários.
5. Atualizar task e registro de implementação. Documentar suporte parcial, limitações e critérios ainda não atendidos. Não encerrar task que depende de aceite do usuário.
6. Revisar o diff para garantir que contém apenas o trabalho autorizado, validar o conjunto pertinente e reconstruir os artefatos quando aplicável. Registrar commit, validação e pendências; então iniciar o próximo item executável.

## Arquivo de progresso separado

Registrar os avanços em `docs/estado-agente-implementacao.md`, separado deste arquivo de instruções. Manter o relatório central na pasta principal para consulta pelo usuário e pelo agente de requisitos; ele é o único arquivo documental compartilhado sob responsabilidade exclusiva deste agente.

Para cada entrega, informar data, spec/task de origem, escopo, status, branch `main`, commit-base, alterações locais preexistentes relevantes, avanços, verificações executadas e resultados, artefatos gerados, limitações, próximo passo e ponto de retomada. Diferenciar implementado, validado, commitado e aguardando aceite.

## Continuidade

### Regra de transição entre tasks

1. Ao concluir um recorte, registrar resultado, branch, evidências e pendências no relatório e enviar atualização de progresso. Não enviar resposta final apenas para anunciar a entrega.
2. Na mesma execução, reler o backlog e as specs atuais, escolher o próximo item executável e iniciar sua investigação/implementação na main. Não aguardar que o usuário diga “continue”.
3. Se um item depender de aceite, revisão ou definição externa, registrar essa dependência e trabalhar no próximo item independente. Uma pendência de um item não bloqueia todo o backlog.
4. Antes de encerrar por falta de trabalho, conferir todas as tasks e specs abertas e registrar os candidatos examinados e o impedimento concreto de cada um. Não usar conclusão de pacote, commit local, teste aprovado ou checkpoint como condição de parada.
5. Só parar por pedido explícito do usuário, esgotamento real do trabalho autorizado ou impedimento externo que afete todos os candidatos executáveis. Registrar o ponto exato de retomada quando limites da sessão ou do ambiente interromperem a execução; não anunciar que a continuidade está garantida fora desses limites.

Esta regra substitui as orientações históricas de parada e de criação de branches/pasta de trabalhos. O trabalho ocorre na `main`; não criar múltiplas pastas de trabalho. Push e deploy continuam dependendo de autorização correspondente.

Trabalhar continuamente pelo backlog autorizado: concluir uma entrega é um checkpoint, não motivo para encerrar a execução. Após validar e documentar, escolher o próximo item executável na `main`. Corrigir primeiro falhas da própria entrega. Se faltar definição indispensável, registrar a pergunta concreta ao agente de requisitos e avançar em outro item independente. Só encerrar quando não houver trabalho executável, houver bloqueio externo real ou o usuário pedir pausa. Registrar o impedimento preciso; não usar a ausência de aceite visual do usuário para adiar verificações técnicas que o agente consegue executar. Não criar loops ociosos. Este perfil não instala agendamento nem garante execução após fechamento da sessão ou limites do ambiente.

## Validação obrigatória da própria entrega

- Converter os critérios de aceite selecionados em uma matriz de cenário, execução, resultado esperado, resultado observado e evidência. Não declarar validado com base apenas na leitura do código ou no teste que verifica o próprio setter.
- Para interface Swing, executar o painel em janela real ou o launcher empacotado, em sessão descartável, interagir com os controles e capturar a janela. Inspecionar a captura, o layout, cortes, estado desabilitado, mensagens e erros. Um teste headless não comprova apresentação visual.
- Usar os scripts gráficos existentes, `java.awt.Robot` ou ferramentas disponíveis para conduzir a interação; não pressupor que é impossível validar apenas porque não há ferramenta de navegador. Harnesses temporários e capturas devem ficar em diretórios isolados, sem tokens ou saves do usuário.
- Para comportamento online, verificar efeito no host e em outro cliente quando pertinente; incluir rejeições, persistência/retomada e efeito real no motor conforme o critério. Testar uma mudança em memória não comprova esses efeitos.
- Para entrega pelo launcher, empacotar na pasta de trabalho e verificar o artefato gerado. Registrar comandos, saídas, caminhos de evidências e a versão/base testada. Não repetir suítes sem necessidade.
- Fazer autorrevisão do diff contra a base, confrontar cada critério com as evidências e corrigir falhas antes de seguir. Revisão independente do agente de requisitos complementa, mas não substitui esta validação.
- Se uma verificação realmente não puder ser executada, registrar tentativa, erro e limitação específica; manter o critério pendente. Aceite de preferência do usuário é distinto de comprovação técnica pelo agente.
