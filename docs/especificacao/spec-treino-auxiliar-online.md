# Treino semanal e auxiliar online

Limite de acesso identificado estaticamente após a integração: [modo individual e visibilidade do botão Id](../tasks/sem-prioridade/task-habilidades-individuais-online.md). O clique real no dashboard permanece pendente de validação; os testes anteriores abriram o diálogo por reflexão. A entrega funcional e as evidências abaixo continuam válidas no escopo exercitado.

Revisão: 08/11/2026. Status atual: contrato implementado e tecnicamente validado na integração `fd4aa24f`, conforme [registro integrado](../integracao-main-2026-11-08.md) e [implementação completa](../implementacoes/implementacao-treino-auxiliar-completo.md). A [task](../tasks/sem-prioridade/task-treino-auxiliar-online.md) preserva limites e aceite. O diagnóstico e os probes iniciais abaixo são históricos; o bloqueio provisório foi substituído pelo auxiliar online completo.

## Consumidor nativo e precedência comprovada

`best.ah.ll()/M(boolean)` leem/escrevem o campo booleano `nt`; `lm()/bZ(int)` leem/escrevem `ns` (`analysis/decompiled/best/ah.java:2239–2252`). O padrão do construtor é auxiliar ativo e foco 0 (`:113–114`). Os dois campos são independentes: alternar o auxiliar não deve apagar o foco manual.

`best.F.f(double)` (`analysis/decompiled/best/F.java:1238–1261`) consulta o foco no processamento de habilidades individuais. Fora do ciclo especial `bO()==3`, auxiliar ativo faz prevalecer o ramo nativo `h(double)` mesmo se foco manual for 1. Auxiliar inativo e foco 1 escolhem `club.bQ(posição)` e acumulam `d*2` naquela habilidade secundária; foco 0 usa `h(double)`, que distribui conforme posição/subtipo (`:2945–2998`). Não implementar sorteio de foco semanal nem alternância artificial entre os dois rádios: isso não é o consumidor encontrado.

O ciclo especial `bO()==3` força o ramo secundário independentemente desses campos; `best.f.aT()` também atualiza alvos `nr` via `club.lj()` nessa fronteira e reinicia o contador, em vez de incrementá-lo (`analysis/decompiled/best/f_0.java:1981–2006`; `best/ah.java:2212–2218`). Preservar o motor, incluindo idade, reputação e limites; não prometer aumento de habilidade visível a cada semana. `fV()` distingue jogadores menores de 32 e os demais (`best/F.java:1210–1219`); o ganho interno pode não cruzar um ponto inteiro.

## Quando processar

`best.f.aS()` agenda a ação 3 em domingos (`best.a.c(1)`), na fase 0 (`best/f_0.java:1951–1971`). `best.a.b(...,3,0)` adiciona `aj` à fila; `best.a.f(0)` consome `aj` chamando `SR.aT()` e limpa a fila (`best/a.java:532–612`). `aT()` percorre jogadores principais com `fV()` e base com `fY()`.

O fluxo online já chama `SR.q(0)` em `CareerEngine.completeDate()`. `q(0)` varre ações prévias até a data global atual (`best/f_0.java:1867–1883`), inclusive domingos sem partida própria. Não adicionar um segundo relógio semanal no adaptador. Validar o avanço normal através da fronteira e a recuperação para garantir uma execução por fila nativa, sem duplicação introduzida pelo comando.

## Contrato multiplayer requerido

1. Manter `training-focus` para foco 0/1 e definir ação distinta `training-assistant`, com campo booleano estrito `enabled`. O host resolve o clube do técnico autenticado; não aceitar clube arbitrário do formulário. Reutilizar revisão/fase de gestão e invalidação de Pronto de `/management`.
2. A ação do auxiliar chama `club.M(enabled)` no host. Não chamar `aT()` nem alterar `bO()`, habilidades ou foco por conta do comando. O efeito usa o próximo processamento nativo; uma semana já processada não deve ser recalculada.
3. Publicar `trainingAssistant` junto a `trainingFocus` no snapshot do clube e projetar ambos em `VisualModel`. A lacuna inicial de só publicar/projetar foco foi corrigida na integração; `updateTraining` também atualiza esses campos enquanto um editor de escalação impede a projeção completa, preservando o rascunho.
4. A tela nativa deve enviar intenção, aguardar confirmação e refletir os dois valores autoritativos. Pode preservar rádios selecionáveis como no painel original, mas deve explicar que o foco manual só decide quando o auxiliar está desativado, respeitado o ciclo especial do motor. Não simular seleção aleatória do auxiliar na interface.
5. Erro ou conflito restaura valores confirmados e reabilita interação. Projeção e reabertura não reenviam comandos; rejeição não deixa mutação local com aparência de confirmação.
6. Persistir no checkpoint Kryo existente; não criar arquivo paralelo para estes dois campos. A retomada deve preservar a combinação escolhida, incluindo auxiliar ativo com foco 1 armazenado. Oferta de emprego/demissão pode mudar clube e padrões nativos: reconsultar o clube atual após transição.

## Evidência executada e aceite restante

A implementação e a validação integrada posteriores estão nos registros vinculados no cabeçalho. As pendências descritas para os probes iniciais nesta seção delimitam o que eles próprios provaram, não significam que o comando completo esteja ausente na versão integrada. Não houve nova execução do treino nesta revisão somente documental.

Probe temporário independente executado contra `analysis/brasfoot.jar` e adaptador local em 08/11/2026: quatro combinações de auxiliar/foco por invocação do consumidor privado `F.f(double)`, quatro roundtrips por `saveLocal/readLocalSave` em saves descartáveis e chamada de `aT()` com incremento do ciclo passaram. Diretório: `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p` (`AuditTraining.java`, `result.txt`). A primeira compilação falhou por importação wildcard que sombreava pacote `c`; a primeira asserção supunha goleiro no índice 0 e foi corrigida para a posição real. Falhas eram do probe, não do produto.

Essa execução confirma precedência e persistência do modelo, não o futuro comando nem todo o calendário. Para aceite completo executar: dois clientes com estados opostos; envio pela caixa nativa; autoridade e revisão inválida; erro de gravação; reabertura/reconexão; save e retomada em JVM distinta; fronteira semanal pelo avanço normal, comparando acumuladores nativos/ciclo sem exigir ganho inteiro; ciclo especial e clubes trocados. Verificar janela real integralmente visível, sem corte do controle, e pacote do launcher. Não encerrar apenas porque `M(boolean)` foi chamado.

Extensão executada do mesmo probe: calendário nativo `SR.r(2022)` conferido em todos os 365 dias, com `aj` em exatamente 52 domingos; `best.a.b(0,3,0)` seguido de `q(0)` incrementou o ciclo uma vez e segunda chamada a `q(0)` não repetiu o consumo. Log `result-calendar.txt`. Isso certifica agenda e esvaziamento da fila isolados, preservando a pendência do avanço normal de carreira por HTTP.
