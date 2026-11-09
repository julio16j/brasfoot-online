# Problemas resolvidos

Esta pasta guarda o diagnóstico, a causa, a correção e a validação de problemas já resolvidos. Os relatos históricos descrevem o código da época da investigação; o [mapa de implementação](../mapa-implementacao.md) apresenta o estado atual.

| Problema | Correção | Evidência | Registro |
| --- | --- | --- | --- |
| Configurações online e velocidade | Opções suportadas sincronizadas com o host e velocidade aplicada pelo relógio nativo; JAR e ZIP atualizados | Usuário confirmou o efeito e solicitou o encerramento | [Configurações online](task-configuracoes-online.md) |
| Elencos acima de 36 jogadores não conseguiam escalar | Removido o limite sobre o elenco, preservando os limites de titulares, reservas e posições | Regressão com 42 jogadores e carreira com Santos (37) e São Paulo (41) | [Teste de carreira](task-teste-carreira.md) |
| Carreira com quatro clientes excedia o timeout após Pronto | Projeção de IA sem buscas repetidas, reutilização de snapshots, envio fora do monitor e uma atualização Swing periódica pendente por cliente | Duas rodadas com quatro clientes, retomada e recuperação de interrupção; timeout mantido em 60 segundos | [Teste de carreira](task-teste-carreira.md) |
| Proje??o da academia nativa divergente da carreira online | Academia ligada ? proje??o autoritativa e promo??o do host; muta??es sem contrato bloqueadas | Janela aberta, editor concorrente, Pronto, rollback HTTP, segundo cliente, retomada e pacote reconstru?do | [Juniores nativos](task-juniores-nativos-online.md) |
| Pre?os manuais do est?dio divergiam da confirma??o do host | Rascunho isolado e confirma??o expl?cita pelo host; bot?o de sugest?es preservado | Janela nativa, obra HTTP, conflito Ready, checkpoint e retomada aprovados | [Pre?os do est?dio](task-precos-estadio-online.md) |

As configurações como um todo ainda estão [parcialmente implementadas](../implementacao-configuracoes-online.md). Os problemas de [calendários](../task-calendarios.md) e de [escalação em folgas](../task-escalacao.md) permanecem abertos e não foram arquivados aqui.

A entrega atual das configurações foi aceita após rebuild. Continuam abertas a [substituição do botão Configurações por Opções](../task-opcoes-nativas-online.md) e a [correção do áudio](../task-audio-eventos-repetidos.md).
