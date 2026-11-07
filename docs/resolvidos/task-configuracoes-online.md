# Configurações online e velocidade — entrega resolvida

**Status atual: resolvida por aceite explícito do usuário em 6 de novembro de 2026.** O usuário confirmou que as configurações alteram a velocidade após o rebuild e solicitou marcar a configuração online como resolvida. O JAR e o ZIP foram reconstruídos com `python tools/package_client.py`. O encerramento se refere à entrega atual das opções suportadas; não declara implementadas as doze opções ainda bloqueadas do catálogo completo.

Permanecem abertas e independentes a [task de integração do botão nativo Opções](../task-opcoes-nativas-online.md) e a [task do áudio repetido ou fora de contexto](../task-audio-eventos-repetidos.md). A confirmação da velocidade não constitui medição exata da duração dos minutos.

Na reabertura anterior, os testes haviam recompilado os fontes em `build/classes`, mas o JAR de `dist/` usado por `Brasfoot-Online.cmd` ainda não havia sido reconstruído. Esse histórico permanece registrado para distinguir teste dos fontes de entrega do executável atualizado.

As alterações da integração da velocidade passaram nos testes dos fontes. Filtros e outras configurações continuam parcialmente pendentes; consulte a [implementação das configurações](../implementacao-configuracoes-online.md) e a [especificação completa](../especificacao/spec-configuracoes-online.md). O custo de snapshots durante a carreira foi tratado também na [investigação do teste de carreira](task-teste-carreira.md). A investigação original abaixo foi preservada como histórico do código anterior às correções.

Data: 6 de novembro de 2026. Relato: o tempo durante a simulação está muito lento. Escopo: inspeção do código local; sem medição da sessão em execução, alteração de velocidade ou novo teste de desempenho.

## Resultado

Há uma espera deliberada no fluxo iniciado pela interface: `NativeClient.java:114` cria o host com **`--tick-ms 1000`**, enquanto os testes usam 75 ms. Cada chamada de `MatchPlayback.tick()` avança um minuto de jogo (`MatchPlayback.java:65–94`). Assim, uma partida com humanos exige nominalmente **92 a 98 segundos**, incluindo os acréscimos sorteados, além do processamento e da espera por Pronto no intervalo. Isso explica um ritmo lento mesmo sem defeito de desempenho.

O host iniciado diretamente usa outro padrão: **500 ms** (`SessionHost.java:321` e `tools/host.py:14`), aproximadamente 46 a 49 segundos para os mesmos minutos. A interface força o dobro desse intervalo. Esses valores são cálculos do agendamento e dos acréscimos do código, não tempos medidos na máquina do usuário.

## O que pode acrescentar demora

- `SessionHost.java:111` usa `scheduleWithFixedDelay`: a espera configurada começa depois de terminar o tick anterior. O custo de processamento se soma ao intervalo, sem compensação automática.
- `tick()` e `handle(HttpExchange)` são sincronizados no mesmo host (`SessionHost.java:118` e `:184`). Requisições atendidas pelo host podem atrasar o relógio enquanto mantêm esse monitor ocupado. É um possível custo adicional; não foi medido.
- Cada cliente consulta `/state` com intervalo de 250 ms após a consulta anterior (`NativeClient.java:65`). Isso afeta a frequência de apresentação; não determina o avanço do motor. Reduzir apenas esse intervalo não acelera a partida.
- O intervalo pausa intencionalmente até a confirmação dos técnicos. Esse tempo de espera precisa ser separado da duração efetiva da simulação.

## Relação com o caso do Barcelona

Não ter jogo do Barcelona não significa que a data inteira seja exclusiva da IA: a captura anterior informa **2/2 prontos**, e o outro técnico pode ter partida nessa data. Nesse caso, o relógio segue o ritmo normal das partidas humanas, mesmo que a tela do Barcelona esteja mostrando outros campeonatos.

Se não houver jogo de **nenhum** humano, `SessionHost.kickoffIfReady()` (`:94–108`) já executa um laço sem a espera de `tick-ms`, retoma o segundo tempo automaticamente e conclui a data. Portanto, o intervalo de 1000 ms não explica lentidão numa data efetivamente exclusiva da IA nesse código. Nesse cenário, medir o início das partidas, a simulação dos jogos da IA em `MatchPlayback.begin()`, a conclusão da data e a persistência antes de escolher uma correção.

## Proposta de correção

### Comportamento esperado

O relógio online deve obedecer à velocidade nativa selecionada pelo host. Ao escolher **Super rápido**, uma data com partida humana deve usar **220 ms por minuto de jogo**, em vez dos atuais 1000 ms. Para 92 a 98 minutos, a espera programada passa de 92–98 segundos para aproximadamente **20–22 segundos**, sem contar processamento, gravação e pausa do intervalo. Essa estimativa é uma referência para validação, não uma garantia de duração total.

Somente o host pode alterar as opções da sessão. Os demais participantes consultam os valores confirmados pelo servidor em controles sem edição. A mudança deve alcançar o relógio autoritativo, inclusive quando feita durante uma partida.

### 1. Carregar as opções antes de iniciar o relógio

Criar uma representação das opções da sessão no host, contendo os índices nativos de velocidade com e sem jogos humanos. Resolver os intervalos pelas tabelas originais `best.aq.rz` e `best.aq.rA`, verificando a disponibilidade desses membros no bytecode usado na compilação. Validar os índices antes de consultar as tabelas.

Na criação, importar as opções persistidas da instalação do host para o processo que executa a sessão. Na retomada, dar precedência às opções salvas com a sessão; para saves anteriores sem esse registro, importar as opções do host uma única vez e persistir o resultado. Confirmar esse carregamento com o processo separado: o valor mostrado na janela do host não demonstra, por si só, o valor disponível no servidor.

Remover o envio obrigatório de `--tick-ms 1000` em `NativeClient` e o padrão independente de 500 ms no caminho normal de `SessionHost`/`tools/host.py`. Manter `--tick-ms` como sobrescrita explícita para testes e diagnóstico, identificada no estado publicado para evitar que a tela anuncie uma velocidade nativa diferente da efetiva.

### 2. Aplicar mudanças somente pelo servidor

Adicionar uma operação autenticada de atualização de opções da sessão. O servidor deve conferir a identidade do host, validar os índices e a revisão das opções, aplicar a mudança e devolver os valores efetivos. A revisão das opções deve ser independente da revisão de escalação de cada técnico.

A tela nativa do host deve encaminhar suas alterações a essa operação, substituindo os listeners que hoje gravam apenas no estado local. Em caso de falha, manter ou restaurar os valores confirmados pelo servidor e informar o erro. Nos participantes, desabilitar a edição e os caminhos de gravação por teclado ou listeners, mantendo a leitura e o fechamento da janela.

Publicar índices, intervalo efetivo, eventual sobrescrita de diagnóstico e revisão no snapshot. Atualizar janelas abertas sem disparar comandos de gravação. Entrada tardia e reconexão devem receber a configuração corrente do servidor.

### 3. Tornar o agendamento ajustável

Substituir o intervalo imutável de `SessionHost` por um agendamento que consulte a configuração efetiva. Uma opção de implementação é manter apenas uma tarefa futura de tick: depois de processar um minuto, agendar o próximo usando o intervalo vigente. Ao alterar a velocidade, cancelar a tarefa pendente e reagendar sob a mesma sincronização que protege o estado da partida. Usar uma geração do agendamento para que uma tarefa antiga já disparada não execute um tick adicional.

Preservar a semântica de espera após processamento usada pelo timer nativo. Não compensar atraso executando vários minutos consecutivos para alcançar o relógio de parede. A mudança de velocidade não deve reiniciar a partida, alterar resultados já processados ou avançar minutos por conta própria.

No intervalo, atualizar a opção sem avançar o jogo; o segundo tempo deve começar no novo ritmo depois da confirmação dos técnicos. Em estados finalizado, com falha ou com falha de armazenamento, não manter tarefas que possam avançar o motor. O timer original dos clientes continua parado: todos apresentam o estado calculado pelo host.

### 4. Definir o ritmo das datas sem humanos

Para respeitar as duas opções nativas, a proposta adota `velocidadeNH` quando **nenhum** clube humano joga na data. Em “Super rápido”, o intervalo de apresentação será **70 ms**. A classificação deve considerar todos os técnicos da sessão, e não apenas o clube do host ou do cliente que está assistindo.

Isso exige substituir o laço imediato de `kickoffIfReady` por avanço agendado nessas datas. Preservar o segundo tempo automático sem barreira humana, o processamento de fim da data e os checkpoints. Como `MatchPlayback.begin()` atualmente resolve jogos da IA antecipadamente, a implementação deve impedir que a apresentação publique resultados e eventos futuros antes do minuto correspondente; testar esse comportamento antes de anunciar compatibilidade com a velocidade nativa sem humanos.

O processamento imediato de datas exclusivas da IA pode voltar como modo explícito separado. Não deve substituir silenciosamente a opção nativa selecionada. A futura automação de confirmações e avanços entre datas permanece no planejamento da [investigação dos calendários](../task-calendarios.md).

### 5. Persistência e entrega

Salvar as opções confirmadas no estado persistido da sessão e restaurá-las antes de construir o relógio. Tratar falha de gravação conforme o mecanismo de proteção já usado pelo host, sem confirmar uma alteração como durável quando ela não foi salva.

Implementar em duas entregas verificáveis: primeiro, carregamento, autoridade do host, sincronização e velocidade das partidas humanas; depois, apresentação temporizada das datas sem humanos. A primeira entrega deve declarar a limitação de `velocidadeNH` até a segunda estar concluída. A correção completa exige ambas.

Medir duração dos ticks, espera programada, atendimento de `/state`, início/fim da data e gravação para distinguir o ritmo escolhido de atrasos adicionais. Otimizações de rede ou renderização devem ser orientadas por essas medidas.

Não acelerar o relógio apenas no cliente: ele deve continuar apresentando o estado do host. Também não pular minutos do motor para encurtar a partida.

## Validação necessária na implementação

Comparar uma partida humana no comportamento atual de 1000 ms e na opção nativa “Super rápido” de 220 ms, registrando duração dos tempos sem somar a pausa do intervalo. Conferir placar e eventos consistentes nos clientes, funcionamento das substituições e barreira do segundo tempo. Verificar uma data exclusiva da IA conforme a política de velocidade adotada e uma sessão mista em que apenas o outro técnico joga. Executar com um e dois clientes para identificar eventual custo de consultas ao host.

Critérios adicionais para aceitar a proposta implementada:

- Todos os índices nativos resolvem os intervalos corretos; índices inválidos são rejeitados sem mudar a configuração vigente.
- Uma alteração no host durante o primeiro tempo muda os intervalos seguintes sem duplicar ticks; uma alteração no intervalo só afeta o ritmo após a retomada.
- Em “Super rápido”, a configuração efetiva publicada é 220 ms com humanos e 70 ms sem humanos, sem sobrescrita implícita da interface ou CLI.
- Participantes não conseguem gravar opções pela tela nem por requisição direta; reconexão e retomada preservam os valores do host.
- A data sem humanos não revela placar final ou eventos futuros no início da apresentação e conclui seus efeitos uma única vez.
- A validação automatizada do agendamento usa um relógio controlável para verificar ordem e ausência de ticks extras; a medição de duração real é um teste de integração separado, com tolerância ao custo de processamento.

## Política proposta pelo usuário: opções controladas pelo host

No online, as opções da sessão devem refletir exclusivamente as alterações do host. Nas telas dos demais participantes, os valores ficam disponíveis para consulta, com a edição desabilitada. Essa é a direção adotada para o planejamento; ainda não foi implementada.

- **Host:** pode editar as opções suportadas; a alteração é enviada ao processo autoritativo, validada e aplicada ao motor e ao relógio compartilhado. A tela deve exibir o valor confirmado pelo servidor.
- **Demais participantes:** recebem os valores efetivos do host e os exibem em modo somente leitura, com indicação “Configurações controladas pelo host”. Bloquear também atalhos e listeners nativos que possam gravar opções localmente por outro caminho.
- **Servidor:** verifica a identidade do host em toda operação de alteração de opções. Desabilitar controles na interface é insuficiente para impedir uma mudança enviada diretamente por outro cliente.
- **Sincronização:** publicar as opções efetivas e sua revisão no estado da sessão; atualizar telas abertas, entrada tardia e reconexão. A aplicação do estado recebido não deve disparar os listeners de gravação da tela nativa.
- **Persistência:** salvar as opções da sessão no host e restaurá-las ao retomar a carreira. A configuração local de quem entra não pode sobrescrever a configuração da sessão.
- **Opções sem integração:** manter a escrita desabilitada também no host, com explicação, até que o efeito nativo correspondente esteja conectado ao fluxo online. Evitar controles que aceitem uma seleção sem produzir o comportamento anunciado.

O escopo inicial é a tela de opções nativas discutida nesta investigação, incluindo velocidades e filtros de partidas. Eventuais preferências estritamente locais, como volume ou aparência, precisam de uma decisão explícita antes de receber uma exceção à regra de somente leitura nos participantes.

Critérios de aceite: alterar a velocidade no host muda o ritmo efetivo e o valor exibido em todos os clientes; um participante não consegue alterar opções pela tela nem por requisição direta; janelas abertas e clientes reconectados convergem para o estado do host; retomar a sessão preserva as opções; uma alteração de velocidade durante o jogo não duplica timers, não pula minutos e mantém a pausa do intervalo. Validar também que os filtros suportados respeitam os valores confirmados pelo host.

A causa do ritmo configurado está confirmada no código; a parcela de lentidão percebida atribuível a processamento, rede ou renderização permanece sem medição. Esta entrega é documental e não modifica a sessão em andamento.

## Confirmação: a velocidade online não segue a configuração nativa

Após a observação do usuário de que as configurações nativas parecem ignoradas, o rastreamento do motor original confirmou isso **para a velocidade da simulação**:

| Origem local | Comportamento |
| --- | --- |
| `analysis/decompiled/a/fB.java:533–537` | A tela original oferece “Velocidade do Jogo” e “Velocidade sem jogos humanos” separadamente |
| `analysis/decompiled/est/Options.java:214–229` | Guarda os índices de velocidade; os setters também atualizam `a.SR` |
| `analysis/decompiled/a/dV.java:269–275` | Escolhe `getVelocidade()` com humanos ou `getVelocidadeNH()` sem humanos e consulta a tabela correspondente; também contém uma condição especial `bD()` que força intervalo 1 |
| `analysis/decompiled/best/aq.java:542–543` | Intervalos com humanos: `[2000, 1200, 700, 450, 220, 100]` ms; sem humanos: `[1200, 700, 300, 100, 70, 30]` ms, por índice de 0 a 5 |
| `analysis/decompiled/a/dV.java:388–419` e `a/ee.java` | O timer original usa o intervalo selecionado e chama `qa()`, que incrementa o contador do relógio |
| `src/brasfoot/NativeMatchPanel.java:30–36` | A apresentação online para o timer local original, inclusive na sobrescrita de `oR()` |
| `src/brasfoot/SessionHost.java:111` e `src/brasfoot/NativeClient.java:114` | O relógio que efetivamente avança a partida online usa `tickMillis`, fixado em 1000 ms pela interface normal, sem consultar os getters nativos de velocidade |

O índice inicial de `Options` é 4 para ambas as velocidades (`est/Options.java:71–72`), que corresponde a 220 ms com humanos e 70 ms sem humanos. Isso descreve o padrão dessa classe, não comprova a opção salva na instalação do usuário. O ritmo online de 1000 ms é aproximadamente 4,5 vezes o intervalo nativo de 220 ms, sem considerar processamento, pausas e diferenças de apresentação.

Reutilizar a tela original não preservou a política original de velocidade: o adaptador substituiu o mecanismo de relógio. Desativar a simulação local continua necessário para manter a autoridade do host; a correção deve transportar a configuração nativa para esse relógio compartilhado. O avanço imediato de datas exclusivas da IA também substitui o ritmo nativo sem humanos e precisa ser uma política explícita.

Essa constatação corrige a recomendação inicial de simplesmente reduzir o padrão para 500 ms: isso aceleraria a partida, mas continuaria sem seguir a configuração original. Antes da implementação, verificar como o host carrega as opções persistidas e como uma mudança durante a sessão deve reagendar seu relógio. Não foi auditada nesta etapa a compatibilidade de todas as demais opções nativas.

### Evidência da configuração exibida ao usuário

A captura adicional mostra **“Super rápido” tanto em “Velocidade do Jogo” quanto em “Velocidade sem jogos humanos”**. O rótulo corresponde ao índice 4 na lista `II` de `analysis/decompiled/a/fB.java:62`; as seleções são preenchidas pelos valores de `a.SR` em `:351–361`. Portanto, já não é apenas uma suposição baseada no padrão de `Options`: a interface fotografada efetivamente apresenta essa seleção.

Para esse índice, os intervalos nativos são **220 ms com jogos humanos** e **70 ms sem jogos humanos**. O host iniciado pela interface online continua recebendo **1000 ms**, sem ler essa seleção. A opção visível não controla o relógio online: isso é uma falha de integração entre as configurações nativas e o adaptador. A captura não comprova que o processo separado do host tenha carregado os mesmos valores; esse carregamento também precisa ser validado na correção.

Também aparecem marcadas as opções de não mostrar rodadas de ligas nacionais não humanas, não mostrar estaduais/regionais fora do Brasil e não mostrar regionais sem humanos. Essas seleções justificam uma auditoria própria dos filtros da apresentação online, em conjunto com a lista extensa de estaduais relatada anteriormente. Não demonstram, isoladamente, quais jogos deveriam ser ocultados naquela sessão de dois técnicos, pois ainda falta conhecer o clube do outro participante. Não confundir o filtro de visualização com a seleção de competições que o motor deve gerar ou simular.
