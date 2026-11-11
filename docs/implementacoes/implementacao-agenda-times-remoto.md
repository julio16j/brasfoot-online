# Agenda host-backed em Times

A aba nativa `a.L` procura partidas em `SR.R()` e compara o clube por identidade de objeto; a Procura constrói um shell remoto separado, sem fixtures futuros locais. O botão Agenda de Times agora consulta `POST /club-agenda` com `clubId` e `queryRevision`. O host devolve todos os confrontos do calendário autoritativo daquele clube. O cliente rejeita resposta de clube diferente ou revisão obsoleta e exibe as partidas numa tabela de leitura com data, competição, fase, mandante, visitante e resultado.

`NativePlayerSearchTest` passou contra o JAR distribuído, comparando o total de partidas do endpoint ao calendário do host e cobrindo revisão antiga e clube desconhecido. `NativeTransferMarketAcademyWindowTest` abriu a Agenda pela janela Times e comparou as linhas exibidas com os fixtures autoritativos; a consulta da Academia e a promoção própria também continuaram passando. O ZIP contém 22.735 entradas e `testzip()` não encontrou erro.

Limite: a tabela usa os dados autoritativos do host numa janela Swing de leitura; não instancia objetos de partida `best.I` nem oferece a navegação/ações da agenda nativa.
