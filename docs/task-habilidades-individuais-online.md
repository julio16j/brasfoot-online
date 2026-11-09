# Habilidades individuais na projeção online

Status: projeção do modo e atributos integrada em `main`; aceite visual e cobertura completa de consulta ainda abertos. Diagnóstico original em 08/11/2026.

## Evidência e escopo

`VisualModel` cria o estado cliente com `setHabilidadeIndividual(false)` incondicionalmente (`src/brasfoot/VisualModel.java:19`). `NativeClient.openNativeTransferSearch` escolhe o modo 1/2 a partir desse estado, portanto o caminho reconstruído usa a procura por força geral, mesmo quando a carreira autoritativa usa habilidades individuais.

O nativo oferece os dois modos. `analysis/decompiled/b/aH.java:87–153` alterna força geral e sete colunas pelos getters gJ–gP; `a/gx_0.java:131–153` alterna faixa geral e filtros `gol`, `des`, `vel`, `fin`, `arm`, `tec`, `pas`. Os sete valores são campos próprios de F (`best/F.java:3086–3139`). Não são `getCr1/getCr2`, que representam características.

`GameAdapter.visualClub` publicava força geral e características, mas não os sete valores nem o modo da carreira. `VisualModel.updateClubs` reconstruía o mesmo subconjunto. `CareerManagement.playerRow` também não transportava as habilidades individuais.

Correção integrada: snapshot publica o modo efetivo `individualSkills` e sete atributos por jogador; `VisualModel` configura o modo e reidrata os valores nativos, e `CareerManagement.playerRow` os inclui no DTO. `AvailabilityProjectionTest` atribui valores distintos aos sete campos e verifica modo/round-trip. Ainda falta comparar filtros e valores renderizados na janela nativa e testar treino/retomada.

O padrão nativo de `est/Options.java:20` é verdadeiro; isso não demonstra qual modo está ativo no save do usuário. A divergência é condicional ao modo do host, sem reprodução gráfica ou comparação de atleta real nesta rodada. Não há evidência aqui de alteração da simulação autoritativa: a lacuna demonstrada é de consulta, representação e filtros.

## Contrato necessário

### Consequência no acesso ao Treino

`a.eg` esconde o botão Id quando `!SR.isHabilidadeIndividual()` ou quando o painel é de seleção (`analysis/decompiled/a/eg_0.java:320–322`). `VisualModel` força false; `NativePreparation.create` reativa o botão e troca seu listener, mas não chama setVisible(true). O código de preparação, portanto, conserva a ocultação nativa nesse caminho. Não confundir botão habilitado com botão visível.

`TrainingClientWindowTest.java:23` abre `openNativeTraining` por reflexão, após abrir a escalação; não clica no botão Id do dashboard. O sucesso anterior confirma o diálogo/comando/motor exercitados, sem certificar a visibilidade desse acesso. É um limite estático novo da cobertura, não revogação da entrega integrada de treino nem uma captura gráfica atual. Não iniciar o jogo para verificar enquanto vigorar a restrição.

Definir o acesso coerente com o modo autoritativo, preservando a regra nativa de quando o treino se aplica. Não apenas forçar o booleano true sem transportar as sete habilidades; não anunciar a integração completa do acesso com base em chamada reflexiva. Acrescentar ao aceite futuro o clique real no botão visível do painel nas condições suportadas.

Transportar o modo efetivo da carreira e os sete valores autoritativos identificados por jogador, preservando a escolha original e distinguindo campos ausentes de zero. Sincronizar após treino, mudança de clube e retomada; manter o cliente sem simulação própria. Reutilizar colunas e filtros nativos correspondentes ao modo confirmado.

A procura precisa de auditoria adicional de origem dos candidatos: `gx_0.nD` chama `af.D(false)`, que percorre `c.a.SR.O()` (`best/af.java:58–91`). O snapshot visual inclui técnicos, adversários próximos e jogos correntes, enquanto `/management` possui consulta de mercado própria sobre os clubes do host. A existência dessa consulta não prova que a Procura nativa a consuma; ainda é necessário rastrear o preenchimento do índice O e a atualização de resultados antes de afirmar cobertura total ou ausência total de candidatos. O teste `NativeTransferSearchTest` verifica encaminhamento de proposta, preço e listeners, sem pesquisar o universo de atletas.

## Aceite futuro

- Comparar o mesmo atleta no host e em dois clientes nos modos geral e individual, sem confundir características com atributos.
- Filtrar separadamente pelas sete habilidades e por força geral, verificando limites e candidatos fora dos clubes visuais da rodada.
- Confirmar atualização após treino, transferência, reconexão e save/retomada, sem recriar atributos localmente.
- Manter os controles coerentes com o modo confirmado e registrar compatibilidade para snapshots antigos.

Implementação e validação dinâmica permanecem pendentes. Relacionados: [backlog de paridade](task-paridade-nativa.md), [mapa](mapa-implementacao.md) e [treino](especificacao/spec-treino-auxiliar-online.md).
