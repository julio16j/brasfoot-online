# Convites e troca de clube do técnico

Status: aberta, P2 proposta, complemento PN-09/PN-11. Investigação estática em 08/11/2026, sem jogo, janela ou testes executados.

Atualização de implementação: a consulta de vagas do técnico desempregado mantém seus candidatos no estado de gestão do host, com data do ciclo; `exportState`/`restoreState` preservam o cache no mesmo dia e descartam uma consulta de data anterior. Isso evita novo sorteio ao retomar o mesmo checkpoint. Não representa convite dirigido persistente para técnico empregado, que segue pendente na spec.

Continuidade estática de 09/11: jobs/job continuam restritos ao técnico sem clube; jobOffers é cache em memória por data e não aparece em exportState. Fluxo anual ay.zw para empregado continua separado. Após esta task, foi criada a [spec de convites e troca de clube](especificacao/spec-empregos-tecnicos-online.md), sem executar criação/aceitação de convite nesta revisão.

## Suporte presente e limite

`CareerManagement.jobs` consulta `c.a.afQ.b(coach,false)` somente quando coach.fg()==null e mantém os IDs em cache até mudar a data. A ação job também exige técnico sem clube, clube oferecido e não humano; chama a função nativa de contratação `state.a(club,club.ka(),coach)`. A janela genérica de carreira oferece essa operação e o botão superior Carreira permanece disponível mesmo sem painel de clube. Não declarar que o desempregado não consegue abrir gestão apenas porque a preparação está vazia.

O nativo possui outro fluxo: `best.ay.zw()` percorre técnicos humanos que ainda têm clube, consulta `b(coach,true)` e apresenta convites (`analysis/decompiled/best/ay.java:239–246`). `CareerEngine.nextDate` chama zw na virada anual, mas a apresentação vai a `best.S`, tornado inerte por HostUiAgent. A consulta/ação online só para desempregados não integra esses convites de empregado.

O diálogo nativo `a.gO.rT`, modo 0, admite técnico com clube: libera o vínculo anterior por `fg().kE()` e aplica `SR.a(novo,tecnicoAtual,tecnicoHumano)` (`a/gO.java:142–149`). Portanto, assumir oferta de outro clube sem ficar previamente desempregado é capacidade distinta, não comprovada pela ação job atual. Não confundir com edição de nome/nacionalidade ou convocação de seleção.

## Origem e persistência dos convites

### Indicadores do técnico no painel

O snapshot visual publica `confidence` com campos oe/of, mas `VisualModel` não consome esse objeto. Os indicadores nativos de diretoria/torcida leem lL/lM, respectivamente of/og (`analysis/decompiled/best/al.java:673–690`; `a/eg_0.java:612–613,646–647`). oe é outro contador, incrementado no ramo de derrota em `al.e(I)`; não substitui og. Portanto, nem aplicar os dois campos publicados sem revisão reproduziria o par correto.

`VisualModel` cria o técnico cliente com valores padrão of=95/og=85 e não os sincroniza. O código confirma a ausência de projeção, sem comparar barras em janela executada nesta etapa. Publicar e aplicar os dois indicadores corretos por técnico/clube, inclusive após troca de emprego, sem recalcular resultados no cliente. A regra de demissão ou manutenção no cargo exige seu próprio rastreamento: barras visuais não comprovam que um técnico será ou deixará de ser demitido no motor.

O rastreamento seguinte encontrou um caminho autoritativo preservado: `CareerEngine.nextDate` chama `f.ax`, que em certas datas chama `n(tipo)`; para tipo 1, Z.A encaminha às ligas e `f.s.A` verifica confiança da diretoria <10 e chama `club.kE()` antes de notificar via best.S (`best/f_0.java:1635–1639,1734–1761`; `best/Z.java:1125–1130`; `f/s.java:1722–1748`). Assim, tornar o navegador inerte não demonstra que a troca esportiva foi suprimida; a atualização pode ocorrer antes dele. Isso é confirmação de encadeamento estático, sem execução de uma demissão real nem certificação de todos os tipos de competição.

Confiança da torcida também participa de público: `best.v.b(I)` usa `coach.lM()` como fator de capacidade/demanda (`best/v_0.java:171–180`). O erro de projeção das barras não altera automaticamente esse cálculo no host; revalidar interface e efeito esportivo/econômico como camadas distintas.

O gerador b considera nacionalidade e país/histórico do técnico, reputação e outras condições; a [nacionalidade descartada na criação](task-nacionalidade-tecnico-online.md) pode alterar candidatos. A lista jobOffers é cache de IDs em memória; `exportState` não a grava, publicando nextOffer/nextPlayer e ofertas de transferência. Reconstruir a lista após retomada pode executar novamente um gerador com embaralhamento. Definir se esses IDs representam convites duráveis ou só consulta recalculável; não anunciar persistência da oferta de emprego sem esse contrato.

Propostas de emprego e de transferência precisam de identidades e ciclos separados. A consulta em todos os snapshots não deve consumir uma oferta ou sortear repetidamente novas opções. Confirmar elegibilidade atual ao assumir; mudança de técnico ou clube já ocupado deve invalidar a oferta com mensagem coerente.

## Requisitos e aceite pendentes

### Limpeza do vínculo anterior na projeção

`VisualModel.rebuild` atualiza `coach.n(novoClube)` e marca o novo clube como humano, atribuindo mZ/na. Não limpa explicitamente o clube anterior que permanece no mapa acumulado: sua marca jZ e referência mZ/na podem conservar o antigo técnico. `visualClub` não publica treinador/propriedade humana por clube que corrija isso. Sequência lógica: técnico sai de A para B; a projeção passa fg=B, mas A pode continuar com jZ=true e ka apontando para o técnico cujo fg agora é B. Não foi executada troca de emprego para observar o efeito visual.

Reconciliar os dois sentidos do vínculo e o técnico IA sucessor no mesmo ciclo de projeção. Classificação de partida humana, identificação do vendedor e perfis nativos dependem dessa informação; IDs e validação autoritativa continuam no host, portanto não alegar que a inconsistência cliente transfere posse real do clube anterior. Acrescentar desligamento sem novo emprego e troca A→B→C ao aceite.

Integrar convites para técnico sem clube e empregado nas fronteiras nativas corretas, com consulta, recusa/adiamento quando aplicável, aceitação e efeito único no host. Validar autorização, vínculo anterior, clube destino, calendário/escalação/Pronto dos afetados e recuperação da operação. Reutilizar as regras de desligamento/contratação; não apenas mudar o ponteiro fg.

Aceite futuro: desempregado com/sem convites; empregado na virada anual; aceitar destino IA; destino que se tornou humano/ocupado; técnico/clube de países distintos; oferta obsoleta; painel e dados de treino/estádio/elenco atualizados após troca; ausência de acesso ao clube anterior; reconexão/save/retomada antes/depois da decisão. Os testes de troca de clube do ciclo do diálogo de Treino não certificam a contratação real.

Relacionados: [gestão](validacao-gestao-nativa.md), [avisos](especificacao/spec-notificacoes-leiloes-online.md), [backlog](task-paridade-nativa.md) e [mapa](mapa-implementacao.md). Implementação e validação dinâmica pendentes.
