# Papéis especiais na escalação online

Status: integracao da preparacao PN-06 concluida; certificacao de efeitos e retomada parcial. Revisao de 09/11/2026. [Backlog](../task-paridade-nativa.md).

## Origem e identidade nativa

O menu AW da escalação (`analysis/decompiled/a/bz.java:193–208`) contém quatro ações, todas bloqueadas hoje em `NativeLineup.java:23`: faltas, capitão, escanteios e **falso 9**. Falso 9 também é uma lacuna, embora o tooltip atual cite apenas batedores e capitão.

| Papel | Callback → método privado do painel | Estado no clube |
| --- | --- | --- |
| Faltas | `bL` → sintético `a(bz,F)` → `u(F)` | `club.k(F)` / `ke()`, campo ni |
| Capitão | `bO` → sintético `b(bz,F)` → `x(F)` | `club.j(F)` / `kd()`, campo nh |
| Escanteios | `bP` → sintético `c(bz,F)` → `v(F)` | `club.n(F)` / `lq()`, campo nj |
| Falso 9 | `bQ` → sintético `d(bz,F)` → `w(F)` | `club.o(F)` / `lr()`, campo nk; alterna escolhido/nulo |

Fontes: `a/bz.java:724–746,1001–1014`; `best/ah.java:310–337,2280–2307`. Não associar capitão a `lq()` apenas pela ordem alfabética dos métodos. O menu só abre em célula titular 1–25 ocupada. Falso 9 só habilita nas células >14, exceto 17 (`a/bz.java:702–720`); preservar a regra de célula, sem presumir elegibilidade só pelo nome da posição.

## Consumidores encontrados e limites

`c/b.java:858–885` consulta `lq()` para evento tipo 5 e `ke()` para tipos 3/4, usando a escolha apenas se o jogador estiver nos ativos do lado correspondente. O motor mantém alternativas quando o escolhido não está em campo. `best.ah.O/Q` preservam ou recalculam capitão/faltas conforme lista ativa; `F.a` recalcula papéis ao transferir jogador (`best/ah.java:1390–1428`; `best/F.java:2591–2596`).

Capitão e falso 9 também são desenhados por `components/ao.java:187–202`. A busca desta rodada não comprovou efeito esportivo adicional do falso 9 nem do capitão em todos os ramos do motor; aparência e setter não bastam. Essas duas partes exigem confronto com o JAR e testes de consumidor antes de declarar paridade. Não inventar bônus nem alterar o motor para criar um efeito presumido.

Confronto estático posterior com o artefato distribuído: leitura dos constant pools de todas as classes de `dist/lib/brasfoot.jar`, sem iniciar JVM ou executar o jogo. As referências diretas a `best.ah.lr()` aparecem somente em `a/bz`, `a/cD` e `components/ao`; ao campo privado nk, somente em `best/ah`. Referências a `kd()` aparecem em `best/F`, `best/ah`, `a/bz` e `components/ao`, coerentes com transferência, fallback e apresentação encontrados na decompilação. Resultado `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-integrated-1881dwvb/static-role-references.json`.

Essa inspeção reduz a lacuna de verificação do bytecode registrada anteriormente: não foi encontrado consumidor esportivo direto de falso 9 no JAR local. Não prova ausência de todo caminho reflexivo ou indireto e não constitui teste de efeito de jogo. A implementação de paridade deve conservar escolha, disponibilidade e indicação efetivamente nativas, sem exigir/criar bônus fictício para satisfazer um pressuposto sobre o futebol real.

## Contrato multiplayer requerido

Transportar IDs estáveis dos quatro papéis na escalação, separadamente dos índices de titulares e do vetor tático. O host valida propriedade, fase/revisão, identidade e elegibilidade nas posições finais do pedido antes de aplicar qualquer campo. Alteração inválida de papel não pode deixar titulares ou táticas parcialmente aplicados. Definir tratamento explícito de manter/limpar seleção; falso 9 admite nulo conforme nativo.

Publicar escolhas confirmadas em snapshot e projetar referências dos jogadores corretos em `VisualModel`, após reconstituir elenco. Abrir ou atualizar editor não deve confirmar defaults apenas na projeção. Transferência, suspensão, substituição e remoção de titular precisam respeitar o consumidor/fallback nativo; confirmar regra ao longo de preparação, intervalo e jogo ao vivo antes de habilitar cada fase.

Save e retomada devem preservar identidades e referências coerentes. Cliente de outro clube só consulta; menus e atalhos não podem deixar callbacks offline modificarem papéis. Enquanto um papel/fase não estiver integrado, apresentar bloqueio explícito correspondente.

Aceite exige teste com jogador escolhido no índice alto do elenco, movimento entre titulares/reservas, retirada/transferência, falha atômica, revisão obsoleta, dois técnicos, save/retomada, janela real com símbolos corretos e consumidor efetivo para faltas/escanteios. Para capitão/falso 9, registrar quais efeitos o JAR realmente contém e quais são apenas indicação; essa decisão continua em aberto até validação.

## Implementacao PN-06

O editor nativo mantem um rascunho dos quatro IDs e os envia com titulares, reservas, celulas e taticas. O host aceita o contrato somente durante PREPARING e valida identidade, disponibilidade e posicao final antes de alterar listas ou campos. Faltas, capitao e escanteios exigem titular; `-1` limpa explicitamente um papel, com alternancia nativa disponivel para falso 9, que segue as celulas acima de 14 exceto 17. Snapshots publicam IDs e `VisualModel` resolve as referencias apos reconstruir o elenco. O menu de papeis fica bloqueado durante edicao ao vivo e em modo somente leitura.

Validacao: `LargeRosterLineupTest` escolheu jogador no indice 40, conferiu os quatro setters e IDs publicados, testou a limpeza do falso 9 e rejeitou atomicamente uma celula proibida. `tools/tests/test_session.py` confirmou envio HTTP, revisao inalterada e snapshot preservado apos rejeicao, alem dos IDs no save do jogo. `python tools/build_adapter.py --test` passou.

Limites: o JAR local tem consumidores diretos para batedores de faltas e escanteios, mas nenhum evento foi forcado para medir os efeitos. Nao foi comprovado efeito esportivo adicional para capitao ou falso 9; falso 9 aparece como selecao e indicacao visual. Retomada explicita do host com papeis e reabertura da janela nativa ainda precisam de testes. Papeis especiais continuam indisponiveis durante a partida.
