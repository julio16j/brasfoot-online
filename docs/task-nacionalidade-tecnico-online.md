# Nacionalidade do técnico na criação e projeção

Status: aberta, P2 proposta, complemento PN-11. Desvio identificado por inspeção em 08/11/2026. Nenhuma sessão, jogo ou teste executado nesta etapa.

O seletor original `a.bV` contém uma seleção de nacionalidade BD, distinta do país/competição/clube escolhidos. `or()` passa o país selecionado, convertido por `best.n.H`, a `club.b(nome,pais)`; esse método grava o valor em `al.cg` (`analysis/decompiled/a/bv_0.java:106–109,234–239`; `best/ah.java:2019–2035`). `al.lE()` é o getter dessa nacionalidade.

`NativeClubSelection` chama o fluxo nativo local, mas seu `CoachChoice` conserva apenas ID/nome do clube e nome do técnico. O comando que cria o host envia apenas club-ids/coach-names. `CareerEngine.start` então cria o técnico usando `selected.get(index).getPais()`, o país do clube, sem receber a nacionalidade escolhida. Selecionar técnico de um país em clube de outro país resulta, pelo caminho do código, na substituição dessa escolha pelo país do clube. Não foi criado um técnico real para reproduzir agora.

O snapshot de técnicos também não inclui lE: publica ID/nome/clube. `VisualModel` cria `new al(nome)` e ajusta ID/clube/flag humana, sem aplicar nacionalidade. Assim, preservar somente a criação no host não completa a projeção dos perfis nativos. Não confundir o país do técnico com country do clube ou dos jogadores.

A nacionalidade participa de regra esportiva/gestão: `best.ay.b(al,false)`, chamada por `CareerManagement.jobs`, consulta `SR.o(al.lE())` como uma das origens dos convites, além do país associado por bz (`analysis/decompiled/best/ay.java:200–235`). Há guardas adicionais por reputação e histórico e busca em outros países; portanto, não concluir que toda lista de emprego muda, mas a escolha descartada pode alterar a seleção de convites. Isso é consequência condicional do código, sem ofertas reais comparadas.

Requisito: transportar a nacionalidade selecionada como dado próprio, validar o país usando o catálogo nativo e preservar a ordem/identidade dos técnicos na criação. O ID do país deve ser o código nativo, não o índice visual ordenado do combo. Publicar e projetar a nacionalidade autoritativa; definir fallback explícito para criações/manifestos antigos, sem sobrescrever uma escolha válida.

Aceite futuro: técnico brasileiro em clube estrangeiro, estrangeiro em clube brasileiro, dois técnicos com nacionalidades distintas, índice visual versus código do país, reinício/reconexão/save e perfil nativo exibido em outro cliente. IDs e nomes dos clubes devem continuar corretos; a correção não substitui a investigação de [calendários](task-calendarios.md).

Relacionado: [backlog](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
