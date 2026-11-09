# Treino automático do auxiliar sem comando online

Atualização de 08/11/2026: `impl/treino-auxiliar-online` implementa o comando do auxiliar, estado confirmado, projeção, tratamento de erro e persistência; o conjunto da principal incorpora essa entrega e a correção para editor aberto. Ver [registro completo](implementacao-treino-auxiliar-completo.md), [revisão independente](revisao-independente-auxiliar-online.md) e [validação integrada](integracao-main-2026-11-08.md). Não confundir com o bloqueio provisório da primeira entrega. Mantida aberta para aceite de uso e limites documentados; o diagnóstico abaixo é histórico.

Status: aberta. P1 proposta. Diagnóstico estático em 08/11/2026; sem reprodução gráfica ou correção. Origem: primeira auditoria de [paridade nativa](task-paridade-nativa.md), PN-01.

Complemento atual de acesso, somente por código: a [task de habilidades individuais](task-habilidades-individuais-online.md) rastreia `VisualModel` fixando modo false, construtor `a.eg` ocultando Id e `NativePreparation` não restaurando sua visibilidade. O teste gráfico chama openNativeTraining por reflexão; não certifica clique no botão do dashboard. Preservar a validação integrada de diálogo/comando/persistência/motor e acrescentar esse acesso aos critérios ainda pendentes, sem restaurar o diagnóstico histórico de auxiliar sem comando.

Checkpoint posterior em 08/11/2026: [spec completa de treino](especificacao/spec-treino-auxiliar-online.md) disponível, com consumidor semanal e precedência verificados por execução isolada do motor e quatro combinações persistidas em Kryo. O diagnóstico inicial abaixo permanece histórico; a revisão do bloqueio provisório do Agente 2 não equivale ao aceite do auxiliar completo nem à integração na principal.

## Fato observado no código

O acesso `NativeClient.java:236` abre `NativeTraining` e fornece o envio a `/management`. `NativeTraining.java:17–20` instancia `a.iQ`, remove e substitui os listeners apenas dos rádios `MW` e `MX`, enviando `training-focus`. Não intercepta nem bloqueia a caixa `MY`.

Na fonte nativa local decompilada, `analysis/decompiled/a/iQ.java:65–69` instala `iS` em `MY`; `:106` apresenta “Deixar auxiliar técnico decidir semanalmente”. `analysis/decompiled/a/iS.java:19–20` chama `club.M(isSelected())`; `analysis/decompiled/best/ah.java:2239–2245` contém o getter e o setter desse estado. O callback atua sobre o clube fornecido ao painel cliente, sem passar pelo host.

O contrato de `CareerManagement.java:121–122` aceita somente `focus` 0 ou 1 para treino e chama `bZ(focus)`. A lista de ações termina em rejeição de ação desconhecida (`:128`); não há comando correspondente à escolha do auxiliar. Assim, a lacuna de roteamento é confirmada por inspeção. Ainda não foram comprovados visualmente a duração da divergência, a recomposição pelo próximo snapshot ou o efeito após fechar/reabrir a janela. Não há evidência de alteração do save do host por essa caixa.

## Reprodução dirigida pendente

Em sessão descartável iniciada pelo launcher, abrir Treino, registrar o valor do auxiliar no host e nos clientes, alternar a caixa e inspecionar requisições, snapshots e reabertura do painel. Repetir com outro técnico e após retomada em nova JVM. Não usar nem modificar saves existentes. Não registrar tokens.

## Requisitos e aceite

1. A caixa deve enviar comando autorizado do próprio técnico ao host, com revisão e persistência, e projetar o valor confirmado; enquanto sem suporte, deve ficar desabilitada com explicação.
2. O envio deve transportar a opção do auxiliar separadamente do foco manual. Definir precedência entre ambos a partir do consumidor semanal nativo antes de habilitar a função.
3. Atualizar a janela e receber snapshots não pode enviar novas alterações. Erros restauram o estado confirmado e permitem nova interação.
4. Conferir propriedade do clube, revisão obsoleta e fase; mudanças não autorizadas não alteram o motor. Validar efeito semanal, reconexão e retomada, além da simples alteração de campo.
5. Após implementação futura, validar fontes e pacote do launcher. Esta auditoria não autoriza nem realiza implementação.

Referência: [contrato geral](especificacao/spec-paridade-nativa.md). Uma tentativa de conferir bytecode com `javap` falhou porque o executável não estava no caminho JDK tentado nem foi localizado no PATH; esta conclusão usa fontes do adaptador e decompilação local, sem alegar verificação do bytecode nesta rodada.
