# Marcação de amistosos dentro da carreira

Status: requisitos abertos, complemento de PN-02/PN-11. Revisão somente por código em 08/11/2026; [investigação de calendários](../task-calendarios.md). Nenhuma execução do jogo ou implementação nesta etapa.

## Capacidade nativa e distinção do modo atual

Agenda → Jogos Amistosos segue `a.eg.re()` → `best.s.w(clube)` → painel `a.h`. A tela escolhe data futura, adversário e mando, consulta aceitação/custo e cria a partida no calendário nativo (`analysis/decompiled/a/eg_0.java:1526–1532`; `best/s_0.java:441–454`; `a/h.java:77–121`).

O modo Amistoso na criação da sessão online usa `--demo`; o host rejeita avançar calendário de carreira nesse modo (`NativeClient.createSession`; `SessionHost` ramo `/advance`). Uma partida avulsa multiplayer não certifica a função de marcar amistosos numa carreira existente.

## Regras locais rastreadas

`f.a.L(clube)` lista datas nativas de tipo 100, estritamente posteriores à data corrente e sem partida daquele clube. `M(clube)` faz operação análoga para tipo 200. `d(clube,índice)` verifica ocupação na lista de partidas da data (`analysis/decompiled/f/a.java:24–60`). Não inventar datas livres só por calendário de parede nem tratar folga atual como data automaticamente agendável.

A avaliação `f.a.a(solicitante,adversário,mando,data)` rejeita adversário já ocupado; retorna 1 diretamente quando adversário é humano; para IA usa reputação, países e mando para aceitar/recusar ou exigir quantia. `xY()` expõe a quantia em campo estático ci. No painel, se há cobrança e o usuário confirma, o solicitante é debitado, depois `f.a.b` insere `best.I` na data escolhida com estádio do mandante (`f/a.java:63–147`; `a/h.java:90–119`).

O contrato online não deve guardar apenas “o último ci global”: outras avaliações podem alterar essa quantia. Persistir os termos de proposta/aceite ou revalidá-los antes da confirmação, detectando mudanças sem cobrança silenciosa. Não recalcular partida no cliente.

## Lacuna atual da projeção

`VisualModel.rebuild` limpa `SR.R()` e cria apenas a data atual; próximos confrontos são objetos adicionais na mesma data de projeção e são retirados de sua lista de jogos (`VisualModel.java:40–63`). Assim, a consulta nativa de datas futuras estritamente posteriores não tem o calendário do host para trabalhar. O acesso à tela local não constitui consulta autoritativa de disponibilidade e pode apresentar nenhuma data no estado de projeção padrão.

Não existe comando de gestão de amistoso no protocolo atual. A solução precisa de consulta do host para datas/adversários elegíveis e comando de agendamento, além de preservar a aparência nativa. Apenas popular o combo local ou habilitar o listener original não resolve a falta de mutação/persistência no host.

## Adaptação multiplayer proposta

O técnico autenticado propõe para seu clube. O host valida fase/revisão, calendário corrente, mando, identidades e disponibilidade dos dois clubes e aplica o consumidor nativo. Para adversário humano, definir consentimento/revisões dos envolvidos; aceite direto no offline não resolve sozinho disputa entre usuários de rede. Essa é decisão multiplayer em aberto, não regra nativa já certificada.

Valores/custos devem ser os confirmados pelo servidor, com efeito financeiro único. Definir como a regra nativa trata saldo insuficiente antes de acrescentar política nova: a tela rastreada confirma e debita sem validação explícita de saldo nesse trecho. Não afirmar que o motor nativo já rejeita esse caso sem verificar todo o consumidor.

Partida confirmada atualiza consultas, próximos confrontos e checkpoint; entrada tardia e retomada mantêm o mesmo jogo. Pedidos concorrentes não podem ocupar o mesmo clube duas vezes na data, e repetição não cria duas partidas nem cobra novamente. Reservar data/proposta sem criar o jogo é mecanismo diferente e precisa de estado persistido claro.

## Aceite futuro

Consulta de datas real do host; nenhum clube ocupado; mando/estádio corretos; proposta IA sem custo, com custo e recusada; humanos conforme política decidida; conflito de data/revisão; falha de gravação; atualização de duas interfaces; save/retomada e avanço até a partida pelo ciclo normal. Até esse contrato estar integrado, manter a mutação local explicitamente indisponível. Cenários não foram executados nesta investigação estática.
