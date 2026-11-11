# Marcação de amistosos dentro da carreira

Status: requisitos abertos, complemento de PN-02/PN-11. Revisão somente por código em 08/11/2026; [investigação de calendários](../resolvidos/task-calendarios.md). Nenhuma execução do jogo ou implementação nesta etapa.

Revisão estática complementar de 10/11/2026: o painel conectado conserva a navegação Agenda → Jogos Amistosos, mas `VisualModel.rebuild` deixa em `SR.R` apenas um `best.a` sintético de tipo 0. Como `f.a.L(clube)` procura datas futuras de tipo 100, a lista de datas fica vazia nessa projeção; o botão “Propor amistoso” em `a.h.mL()` mostra “Não há datas disponíveis” e retorna antes de cobrar ou inserir partida. Portanto, a rota de interface permanece, mas essa projeção padrão não permite alcançar a mutação local. Não generalizar para outro estado que contenha datas 100 nem tratar a guarda local como implementação da consulta/gestão online.

## Capacidade nativa e distinção do modo atual

Agenda → Jogos Amistosos segue `a.eg.re()` → `best.s.w(clube)` → painel `a.h`. A tela escolhe data futura, adversário e mando, consulta aceitação/custo e cria a partida no calendário nativo (`analysis/decompiled/a/eg_0.java:1526–1532`; `best/s_0.java:441–454`; `a/h.java:77–121`).

O modo Amistoso na criação da sessão online usa `--demo`; o host rejeita avançar calendário de carreira nesse modo (`NativeClient.createSession`; `SessionHost` ramo `/advance`). Uma partida avulsa multiplayer não certifica a função de marcar amistosos numa carreira existente.

## Regras locais rastreadas

`f.a.L(clube)` lista datas nativas de tipo 100, estritamente posteriores à data corrente e sem partida daquele clube. `M(clube)` faz operação análoga para tipo 200. `d(clube,índice)` verifica ocupação na lista de partidas da data (`analysis/decompiled/f/a.java:24–60`). Não inventar datas livres só por calendário de parede nem tratar folga atual como data automaticamente agendável.

A avaliação `f.a.a(solicitante,adversário,mando,data)` rejeita adversário já ocupado; retorna 1 diretamente quando adversário é humano; para IA usa reputação, países e mando para aceitar/recusar ou exigir quantia. `xY()` expõe a quantia em campo estático ci. No painel, se há cobrança e o usuário confirma, o solicitante é debitado, depois `f.a.b` insere `best.I` na data escolhida com estádio do mandante (`f/a.java:63–147`; `a/h.java:90–119`). Na projeção online de tipo 0 descrita acima, esse trecho não é alcançado porque não existe índice selecionável; a descrição é da capacidade nativa para uma data elegível.

O contrato online não deve guardar apenas “o último ci global”: outras avaliações podem alterar essa quantia. Persistir os termos de proposta/aceite ou revalidá-los antes da confirmação, detectando mudanças sem cobrança silenciosa. Não recalcular partida no cliente.

## Lacuna atual da projeção

`VisualModel.rebuild` limpa `SR.R()` e cria apenas a data atual com tipo padrão 0; próximos confrontos são objetos adicionais na mesma data de projeção e são retirados de sua lista de jogos (`VisualModel.java:40–63`). `f.a.L` procura datas futuras do tipo 100 (`best.a.b(100)`) e `f.a.M`, tipo 200; nenhum desses dias é transportado pelo snapshot. Assim, a consulta nativa de datas futuras não tem o calendário do host para trabalhar. No estado padrão, a tela local mostra nenhuma data e bloqueia a confirmação pela própria guarda. O acesso à tela não constitui consulta autoritativa de disponibilidade.

Não existe comando de gestão de amistoso no protocolo atual. Confirmação estática adicional em 10/11/2026: o handler de gestão do `SessionHost` chama `CareerManagement.execute`; sua lista de ações cobre operações de mercado, elenco, estádio, treino e emprego, mas não consulta/agendamento de amistoso, e ação desconhecida termina em `IllegalArgumentException`. Portanto nem a consulta de datas tipo 100/200 nem a confirmação nativa local `f.a.b` têm rota autoritativa no host. A solução precisa de consulta do host para datas/adversários elegíveis e comando de agendamento, além de preservar a aparência nativa. Apenas popular o combo local ou habilitar o listener original não resolve a falta de mutação/persistência no host.

## Perguntas de amistosos na virada anual

Há um segundo caminho nativo além de Agenda → Jogos Amistosos. Em `best.f_0.aa()`, quando existe técnico humano com clube, `bs=true` e `bD=false`, o motor chama `cR.pl()`, que pergunta se deseja marcar amistosos; aceitando, percorre os clubes humanos e encaminha a abertura da tela `best.S.w(club)`. Mais adiante, o mesmo ramo chama `cR.AC()` para perguntar sobre o Torneio Amistoso de início de temporada; a confirmação encaminha para a navegação nativa do torneio. No host, `CareerEngine.initializeWorld()` força `SR.bs=false`, então os dois prompts e a rota anual são pulados. Se `SR.yL()` estiver ativo, `best.f_0.aa()` ainda chama `iY()` por seu próprio ramo e pode criar o torneio automaticamente; não presumir que o flag seja equivalente à resposta manual. Além disso, `HostUiAgent` torna inertes os métodos de `best.S`, e as confirmações `JOptionPane` não podem ser chamadas em headless. Portanto o estado online não pode oferecer escolha anual de amistosos pela navegação nativa atual, mesmo depois de integrar o comando manual descrito acima. Definir comandos/estado de decisão do host para a oferta da virada, sem bloquear o avanço com diálogo local; cobrir aceitar/recusar, vários clubes humanos, flag `yL`, checkpoint/reconexão e criação de fixtures com datas tipo 100 corretas. Evidência estática em `best.f_0.aa/iY/yL`, `a.cR.pl/AC`, `CareerEngine.initializeWorld` e `HostUiAgent`; sem execução.

## Adaptação multiplayer proposta

O técnico autenticado propõe para seu clube. O host valida fase/revisão, calendário corrente, mando, identidades e disponibilidade dos dois clubes e aplica o consumidor nativo. Para adversário humano, definir consentimento/revisões dos envolvidos; aceite direto no offline não resolve sozinho disputa entre usuários de rede. Essa é decisão multiplayer em aberto, não regra nativa já certificada.

Valores/custos devem ser os confirmados pelo servidor, com efeito financeiro único. Definir como a regra nativa trata saldo insuficiente antes de acrescentar política nova: a tela rastreada confirma e debita sem validação explícita de saldo nesse trecho. Não afirmar que o motor nativo já rejeita esse caso sem verificar todo o consumidor.

Partida confirmada atualiza consultas, próximos confrontos e checkpoint; entrada tardia e retomada mantêm o mesmo jogo. Pedidos concorrentes não podem ocupar o mesmo clube duas vezes na data, e repetição não cria duas partidas nem cobra novamente. Reservar data/proposta sem criar o jogo é mecanismo diferente e precisa de estado persistido claro.

## Aceite futuro

Consulta de datas real do host; nenhum clube ocupado; mando/estádio corretos; proposta IA sem custo, com custo e recusada; humanos conforme política decidida; conflito de data/revisão; falha de gravação; atualização de duas interfaces; save/retomada e avanço até a partida pelo ciclo normal. Até esse contrato estar integrado, manter a mutação local explicitamente indisponível. Cenários não foram executados nesta investigação estática.
