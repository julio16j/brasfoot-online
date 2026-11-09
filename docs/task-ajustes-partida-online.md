# Ajustes durante a partida: quantidade, banco e atomicidade

Status: parcial; validação atômica da escalação ao vivo integrada e validada em 09/11/2026. Equipe reduzida, expulsões, erro de checkpoint e reconexão permanecem abertos. Diagnóstico original em 08/11/2026.

## Recorte integrado

Antes de qualquer troca, `GameAdapter.liveLineup` agora valida fase, onze ativos, slots/táticas, goleiro, unicidade, elegibilidade, banco atual, composição final do banco, reentrada e pareamento goleiro/linha. Todos os pares são pré-validados pelo `MatchPlayback`; somente depois as substituições são aplicadas. O teste de motor verifica que rejeições deixam jogo, clube, sequência e revisão intactos. O teste HTTP confirma 400 sem mudança em rodada/jogo/playback/checkpoint e aceita uma troca válida. Ambos passaram também com o JAR empacotado.

## Menos de onze após expulsão

O motor nativo remove o expulso da lista ativa hp/hq em eventos 3/4 (`analysis/decompiled/best/I.java:684–694`). `VisualModel.projectSide` projeta o conjunto ativo recebido. Entretanto, `NativeLineup.extract` exige sempre onze jogadores e `GameAdapter.liveLineup` exige starters.length=slots.length=11 (`src/brasfoot/NativeLineup.java:62`; `GameAdapter.java:127`).

Após expulsão, conservar os dez restantes e apenas mudar posições/tática não passa por esse contrato. Acrescentar outro titular também não resolve: outgoing/incoming terão quantidades diferentes e o host rejeita. A substituição individual é outro comando e não comprova o ajuste tático de equipe reduzida. Não foi executado cenário real de cartão nesta rodada.

## Pedido múltiplo pode falhar depois de uma substituição

`liveLineup` calcula outgoing/incoming e verifica a quantidade restante, mas depois aplica cada par sequencialmente por `playback.substitute`; a validação de cada par ocorre dentro dessa execução. `MatchPlayback.substitute` valida campo, banco e compatibilidade goleiro/linha antes de mover aquele par, não o conjunto inteiro.

Contracaso lógico: duas trocas solicitadas, limite suficiente, primeiro par válido e segundo entrante fora do banco atual. A primeira troca altera listas, evento e contador; a segunda lança exceção antes de terminar o comando. `RoundCoordinator.liveCommand` só incrementa revisão após command.run; `SessionHost` só persiste após seu retorno e não restaura o estado em exceção. Portanto, uma rejeição do conjunto pode deixar uma troca em memória sem a confirmação/revisão esperadas. Não foi provocada essa falha no motor nesta etapa.

Há também associação por ordem das listas, sem pareamento explícito. Uma troca de goleiro junto de jogador de linha pode ser recusada pela ordem dos entrantes, ainda que exista um pareamento válido. Deve haver validação completa e pareamento inequívoco antes de qualquer mutação, ou recuperação integral em falha.

## Banco substituído pelo conteúdo do pedido

Os índices de `bench` são checados por existência e unicidade no elenco, mas não por pertencer ao banco original/atual, elegibilidade ou participação anterior. Após processar trocas, o método faz `currentBench.clear();currentBench.addAll(reserves)` e altera club.kZ. Uma requisição sem mudança nos titulares consegue substituir a lista do banco por outros integrantes do elenco. A validação de entrada presente em `MatchPlayback.substitute` não protege essa etapa, pois ela só roda para trocas efetivas.

Isso permite, no contrato rastreado, preparar um banco diferente e usá-lo numa requisição posterior; não afirmar que o usuário realizou esse fluxo ou que a janela atual o oferece em todos os estados. É preciso definir a composição autorizada para cada fase, distinguir preparação de jogo iniciado e respeitar atleta já substituído/expulso/inelegível. Não estender uma regra de montagem pré-jogo ao banco ao vivo sem decisão explícita.

## Elegibilidade difere entre as duas rotas

`GameAdapter.substitute` consulta `incoming.a(fixture,false)` antes de chamar o playback. `GameAdapter.liveLineup` chama diretamente `playback.substitute`, que verifica presença e posição, mas não essa elegibilidade. Logo, uma entrada inelegível ainda presente no banco pode ser rejeitada na rota individual e aceita na rota de escalação, nas condições restantes válidas. Essa divergência é estática; não foi induzida uma contusão ou suspensão no jogo.

Enquanto editor está aberto, `NativeClient.accept` só chama `visual.updateTraining` em lugar da projeção geral; os objetos do rascunho não recebem a evolução médica/disciplinar corrente por esse caminho. A revisão de técnico não muda a cada minuto do motor. Preservar o rascunho evita substituir suas referências, mas não garante que a seleção continue válida: o host deve revalidar a condição atual no instante da aplicação. Atualizar o indicador de conflito sem sobrescrever silenciosamente o desenho é parte do contrato futuro.

## Requisitos e aceite futuro

### Outras validações assimétricas

A preparação `lineupWithOptions` limita banco a onze, exige goleiro primeiro/slot 1, um único goleiro titular e valida os quatro índices táticos contra as tabelas nativas (`GameAdapter.java:71–94`). A rota ao vivo não repete essas condições: aceita banco maior; verifica apenas faixa/unicidade de células; se tactics tem quatro elementos, atribui-os sem checar domínio, e se o tamanho é outro, ignora o vetor. `ah.k(int[])` é atribuição direta, sem validação interna (`best/ah.java:708–714`).

Não inferir uma falha específica do motor sem executar o consumidor, mas o comando pode confirmar valores fora do contrato validado na preparação. Acrescentar banco excessivo, goleiro/célula incoerentes e índices táticos inválidos aos casos de rejeição sem alteração. Preservar diferenças legítimas das regras ao vivo, como equipe reduzida; não copiar indiscriminadamente a exigência de onze da preparação.

- Separar escalação inicial, reposicionamento/tática e substituição durante jogo; aceitar a quantidade ativa legítima sem restaurar o expulso.
- Validar todo o pedido, papéis, elegibilidade, banco e pareamento antes de alterar listas, contador, eventos ou tática. Rejeição deve preservar estado e revisão coerentes.
- Preservar o banco autorizado e histórico de entradas/saídas. Confirmar regras nativas de intervalo e exceções antes de admitir alteração da lista.
- Validar onze/dez/nove ativos; expulsão de goleiro; troca dupla com goleiro; segundo par inválido; falta de trocas; atleta fora do banco/já utilizado; reposicionamento sem troca; erro de checkpoint; repetição/reconexão e persistência após sucesso.

`/substitute` usa IDs, enquanto `/live-lineup` usa índices do elenco. Manter identidade estável durante edição e snapshots também faz parte do contrato; não certificar uma rota pelo resultado da outra. O cliente suspende parte da projeção enquanto o editor está aberto, mas isso não substitui validação do estado atual no host.

Relacionados: [paridade](task-paridade-nativa.md), [disponibilidade](especificacao/spec-disponibilidade-jogadores-online.md), [papéis](especificacao/spec-papeis-escalacao-online.md) e [mapa](mapa-implementacao.md). Implementação e execução de validação pendentes.
