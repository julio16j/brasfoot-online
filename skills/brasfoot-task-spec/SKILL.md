---
name: brasfoot-task-spec
description: Organize tasks, specifications, implementation records and resolution evidence in Brasfoot Online. Use when investigating project issues, defining feature requirements, implementing a documented task, updating progress, or reopening a reported regression.
---

# Brasfoot: task, spec e implementação

Mantenha rastreável o percurso entre relato, diagnóstico, requisitos, código, testes e artefato entregue. Use as convenções deste repositório; não transforme uma solicitação apenas documental em autorização para implementar funcionalidades.

## Onde registrar

Resolva os caminhos a partir da raiz do projeto, não do diretório onde esta skill foi instalada.

| Documento | Local | Responsabilidade |
| --- | --- | --- |
| Task aberta | `docs/task-<assunto>.md` | Problema ou trabalho solicitado, investigação, evidências, ações e pendências |
| Spec | `docs/especificacao/spec-<assunto>.md` | Comportamento desejado, regras e critérios de aceite |
| Registro de implementação | `docs/implementacao-<assunto>.md` | O que o código efetivamente entrega, cobertura, limitações e trabalho restante |
| Task resolvida | `docs/resolvidos/task-<assunto>.md` | Histórico do problema, correção e validação que justificam seu encerramento |
| Índices | `docs/README.md`, `docs/especificacao/README.md`, `docs/resolvidos/README.md` | Navegação e estado dos documentos |
| Mapa do produto | `docs/mapa-implementacao.md` | Funcionalidades presentes, código responsável, validação e pendências |

Use nomes curtos em minúsculas e com hífens. Reutilize um documento existente sobre o mesmo problema; preserve informações válidas. Uma correção pequena pode precisar apenas da task: crie uma spec ou um registro separado quando regras, contratos ou volume de implementação justificarem essa separação.

## Investigar e abrir uma task

Leia o índice, o mapa e os documentos relacionados antes de criar outro arquivo. Confira o código e as evidências disponíveis para não repetir um diagnóstico já feito.

Na task, registre de forma proporcional ao problema:

- Status e data da revisão, relato e comportamento esperado.
- Caminho usado pelo usuário: launcher, JAR, script, pacote extraído ou outro ponto de entrada relevante.
- Reprodução e escopo observado; diferencie fatos, hipóteses e conclusões de testes anteriores.
- Causa confirmada, componentes afetados e links para spec e implementação, quando existirem.
- Correções realizadas, validação executada e pendências para aceite.

Não use o sucesso de outro cenário como prova do relato atual. Diferencie teste de motor, API, interface e pacote distribuído. Para velocidade, separe intervalo configurado, processamento do motor, espera por sincronização, rede e renderização. Um teste com `--tick-ms` explícito valida a sobrescrita de diagnóstico, não o comportamento normal das velocidades nativas.

## Especificar

Escreva a spec em `docs/especificacao/spec-<assunto>.md` quando for necessário definir regras ou contratos antes ou durante a implementação. Inclua apenas o que ajuda a decidir e verificar a entrega: objetivo, escopo, regras nativas preservadas, autoridade do host, estado e protocolo afetados, persistência, erros, interface e critérios de aceite observáveis.

Uma spec descreve requisitos, não comprova que eles foram implementados. Identifique propostas e decisões ainda abertas. Atualize requisitos quando o escopo autorizado mudar, preservando pendências explícitas. Não imponha aprovação adicional da spec se a implementação já estiver autorizada pelo usuário.

## Implementar e mapear o estado real

Conecte cada entrega aos critérios de aceite e aos consumidores nativos correspondentes. Um setter chamado, um botão visível ou um campo sincronizado não comprovam o efeito funcional completo.

No registro de implementação, relacione comportamento entregue, componentes responsáveis e evidência de validação. Separe implementação presente no código de execução aprovada em testes. Registre suporte parcial e opções indisponíveis; não declare toda a spec concluída porque apenas parte dos controles está integrada.

Atualize `docs/mapa-implementacao.md` quando a capacidade do produto mudar. Mantenha a spec como referência de requisitos e o registro como retrato do código; use links em vez de copiar catálogos completos entre documentos.

## Validar, recompilar e entregar

Execute as verificações relevantes para a mudança e registre quais passaram, quais falharam e quais não foram executadas. Investigue a causa de uma falha; não aumente um timeout apenas para esconder uma exceção, barreira não satisfeita ou atraso sem diagnóstico.

No fluxo atual do projeto:

- `python tools/build_adapter.py --test` recompila em `build/classes` e executa os testes selecionados pelo script.
- `python tools/tests/test_session_options.py`, `test_session.py`, `test_native_session.py` e `test_career_session.py` cobrem áreas diferentes. Escolha os testes afetados; não trate a existência de um teste como evidência de sua execução.
- `Brasfoot-Online.cmd` abre `dist/brasfoot-online.jar`, não `build/classes`.
- Depois de uma alteração de código destinada ao teste ou uso pelo launcher, execute `python tools/package_client.py`. Ele recompila e atualiza `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip`.

Confirme o sucesso do empacotamento e informe qual artefato foi atualizado. Se falhar, registre a falha e não anuncie a versão como entregue. Não encerre processos ou sessões do usuário sem autorização; explique que uma instância já aberta precisa ser fechada e reaberta para carregar o novo JAR.

Para mudanças apenas na documentação ou na skill, não recompile o jogo sem necessidade. Verifique nomes, caminhos, links e coerência dos estados.

## Resolver ou reabrir

Mova a task para `docs/resolvidos/` somente quando o problema específico tiver correção implementada, validação pertinente e artefato atualizado quando aplicável. Se o aceite depende de novo teste do usuário e ele ainda não ocorreu, mantenha a task aberta com esse status. Testes internos aprovados podem ser registrados sem declarar o relato resolvido.

Se a entrega for parcial, preserve a task aberta ou delimite claramente um problema independente resolvido, mantendo as pendências em documentos abertos. A spec continua em `docs/especificacao/`, com seu status atualizado; não arquive uma spec inteira apenas por resolver uma de suas tasks.

Quando o usuário disser que o problema continua, retorne a task de `docs/resolvidos/` para `docs/`, registre a reabertura e a nova evidência e continue a investigação dentro do escopo autorizado. Preserve os resultados anteriores com seu contexto, corrigindo conclusões que deixaram de se sustentar. Não trate o relato como refutado por testes anteriores nem peça que ele confirme novamente a autorização já dada.

Ao mover ou renomear arquivos, atualize links relativos, índices, mapa e referências no README principal. Retire a task reaberta do catálogo de resolvidos. Busque referências ao caminho antigo e confira os destinos, inclusive os links dentro do arquivo movido.

Na entrega ao usuário, diga o que foi alterado, o que foi validado, se houve rebuild e o que permanece pendente. Evite usar “resolvido” como sinônimo de “compilou” ou “passou no teste unitário”.
