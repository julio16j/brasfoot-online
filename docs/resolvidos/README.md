# Problemas resolvidos

Esta pasta guarda o diagnóstico, a causa, a correção e a validação de problemas já resolvidos. Os relatos históricos descrevem o código da época da investigação; o [mapa de implementação](../mapa-implementacao.md) apresenta o estado atual.

| Problema | Correção | Evidência | Registro |
| --- | --- | --- | --- |
| Consultas nativas de Calendario/Agenda e Tabelas/Rodadas | Janelas nativas abastecidas pelas consultas do host | Carreira real, telas nativas e JAR distribuido validados em 11/11/2026 | [Calend?rio e Tabelas](task-calendarios.md) |
| Expansao nativa do estadio estava inacessivel | Botao nativo visivel, titulo corrigido e fluxo de expansao integrado; encerrada apos confirmacao do usuario | Correcao visual e rollback validados; usuario confirmou que a expansao funciona | [Expansao do estadio](task-expansao-estadio-online.md) |
| Precos manuais do estadio nao podiam ser editados | Modo sugerido explicado; edicao manual dos quatro setores e confirmacao pelo host | Clique real no controle, envio pelo JAR distribuido e aceite do usuario | [Precos do estadio](task-precos-estadio-online.md) |
| Snapshot global excedia o limite do cliente | Teto de resposta ampliado de 8 para 32 MiB com guardas no cabeçalho e no fluxo | Testes HTTP e carreira real aprovados no JAR da branch e principal; integração registrada | [Snapshot de carreira grande](task-snapshot-carreira-grande.md) |
| Configurações online e velocidade | Opções suportadas sincronizadas com o host e velocidade aplicada pelo relógio nativo; JAR e ZIP atualizados | Usuário confirmou o efeito e solicitou o encerramento | [Configurações online](task-configuracoes-online.md) |
| Elencos acima de 36 jogadores não conseguiam escalar | Removido o limite sobre o elenco, preservando os limites de titulares, reservas e posições | Regressão com 42 jogadores e carreira com Santos (37) e São Paulo (41) | [Teste de carreira](task-teste-carreira.md) |
| Carreira com quatro clientes excedia o timeout após Pronto | Projeção de IA sem buscas repetidas, reutilização de snapshots, envio fora do monitor e uma atualização Swing periódica pendente por cliente | Duas rodadas com quatro clientes, retomada e recuperação de interrupção; timeout mantido em 60 segundos | [Teste de carreira](task-teste-carreira.md) |
| Proje??o da academia nativa divergente da carreira online | Academia ligada ? proje??o autoritativa e promo??o do host; muta??es sem contrato bloqueadas | Janela aberta, editor concorrente, Pronto, rollback HTTP, segundo cliente, retomada e pacote reconstru?do | [Juniores nativos](task-juniores-nativos-online.md) |

Configuracoes ainda estao [parcialmente implementadas](../implementacoes/implementacao-configuracoes-online.md). A task de [escalacao em folgas](../tasks/sem-prioridade/task-escalacao.md) permanece aberta; Calendario/Tabelas foi concluido neste recorte e requisitos adicionais seguem na spec.

A entrega atual das configurações foi aceita após rebuild. Continuam abertas a [substituição do botão Configurações por Opções](../tasks/sem-prioridade/task-opcoes-nativas-online.md) e a [correção do áudio](../tasks/sem-prioridade/task-audio-eventos-repetidos.md).
