# Convites e troca de clube online

Status: requisitos abertos, 09/11/2026. Origem: [task de empregos](../tasks/sem-prioridade/task-empregos-tecnicos-online.md), anterior a esta spec. Diagnóstico estático; não houve convite aceito, demissão ou troca de clube executados nesta investigação.

## Referência e fluxos distintos

| Fluxo | Referência local | Estado atual e requisito |
| --- | --- | --- |
| Técnico sem clube | CareerManagement.jobs → ay.b(coach,false), cache por data; job valida candidato e chama SR.a | Presente no host/formulário; manter acesso sem painel de clube, autoridade e atualização |
| Convite a empregado | CareerEngine.nextDate → ay.zw → ay.b(coach,true) → best.S → a.gO modo 0 | Apresentação desativada no host; integrar o convite sem obrigar o técnico a ficar desempregado primeiro |
| Assumir destino | a.gO.rT libera vínculo anterior quando existe e chama SR.a | Transação autoritativa com ambos os clubes e consumidores reconciliados |
| Demissão esportiva | Ciclo nativo por competição pode chamar club.kE antes da notificação | Preservar efeito do motor e notificar; navegador inerte não significa ausência da demissão |

Reutilizar as regras nativas de candidatos, reputação, nacionalidade e histórico; não oferecer todos os clubes do catálogo nem escolher por reputação isolada. A [nacionalidade](spec-nacionalidade-tecnico-online.md) e o país do vínculo anterior são dados distintos consumidos por ay.b. A [restrição de ligas](spec-calendarios-ligas-online.md) não autoriza substituir nacionalidade pelo país do clube.

## Oferta e consulta

Separar busca de vagas de convite dirigido. Uma resposta de consulta deve identificar técnico, clube destino, origem do fluxo, data/temporada e versão da leitura, com estado elegível/indisponível. Uma lista de IDs em cache não comprova que exista um convite durável.

Definir explicitamente o ciclo antes de implementar:

- Para consulta recalculável de desempregado, documentar quando o conjunto pode ser gerado novamente e como a versão muda; revalidar o clube escolhido na confirmação.
- Para convite dirigido, definir identidade, destinatário, criação, prazo/expiração e tratamento de recusa/fechamento conforme o fluxo nativo. Persistir o conjunto se a interface o apresenta como pendente após reconexão/retomada.
- Não sortear novas opções a cada snapshot de 250 ms nem consumir oferta ao listar. Reabrir não deve aparentar a mesma oferta quando o conjunto já é outro.
- Ofertas de transferência e de emprego usam identidades/ciclos separados. Não reutilizar offerId de negociação de jogador para contratar técnico.

Proposta de implementação: registrar lotes/versionamento de candidatos e convites no host na fronteira nativa apropriada. Ainda é decisão aberta o prazo de resposta, se a decisão pausa automação da data e se o desempregado recebe busca recalculável ou ofertas persistentes. Não tratar essas escolhas como comportamento já confirmado pelo usuário.

## Confirmação e autoridade

O técnico autenticado só decide por si. Resolver o destino por ID; validar que a oportunidade ainda corresponde ao contexto/versionamento apresentado, que o clube existe e não foi ocupado por outro humano. Rejeitar convite obsoleto com motivo e nova consulta, sem escolher outro destino automaticamente. Troca entre clubes humanos ou troca de participantes/tokens exige contrato próprio e não é habilitada por aceitar um convite IA.

O fluxo de empregado deve liberar o vínculo anterior e atribuir o novo no mesmo comando/checkpoint, usando as funções nativas e preservando invariantes. Não permitir que só o ponteiro coach.fg mude. Reconciliar também o técnico IA sucessor, referências do clube anterior/destino, flags humanas, confiança e consumidores por clube. Reconhecer comandos repetidos para não contratar duas vezes.

Cobrir cada ponto de entrada nativo preservado. No HEAD revisado, Agenda (`HR`) mantém seu listener; “Central de Empregos” no popup `Hx` abre `a.gE`, que consulta `a.afQ.Ap/Aq` localmente e confirma via `SR.a`/`kE`, sem encaminhar para `/management`. O nativo pode desabilitar esse item quando `HK` ou `afj` está ativo; cobrir o caso em que o item permanece habilitado. A ação precisa ser bloqueada no painel conectado ou roteada ao mesmo comando autoritativo. Não permitir que a tela local mude a projeção enquanto host e demais clientes mantêm o vínculo anterior. Incluir também técnico empregado e desempregado no contrato; não certificar só o botão Carreira do diálogo online.

Definir fronteira de fase/revisão para a decisão. Invalidações de Pronto/escalação e demais rascunhos devem atingir o contexto anterior e novo; um editor aberto não pode confirmar intenção do clube anterior depois da troca. Folga ou ausência de confronto não elimina a necessidade de preparar o novo clube corretamente. Não permitir decisão que altere os participantes de uma partida já em execução sem regra explícita. No HEAD, a rota de gestão online bloqueia GET de `/management` durante apresentação ativa e protege POST com `RoundCoordinator.management` (fase PREPARING, round/revision e Pronto de todos os afetados retirado). A Central nativa `a.gE.Az()` aplica a contratação local sem esse gate. Enquanto essa rota nativa permanecer no cliente conectado, bloqueá-la em fase não autorizada ou roteá-la pelo mesmo comando; não depender da validação do endpoint se o handler local ainda puder mutar o modelo. A tentativa pelo botão superior online durante apresentação recebe erro do GET; apresentar estado desabilitado/contextual no lugar de erro genérico.

Confirmar sucesso somente após persistência coerente. Falha de armazenamento/validação não pode deixar o técnico entre clubes, destino sem treinador ou oferta consumida. Rollback deve abranger os vínculos e metadados da decisão, além do texto exibido. Não presumir que o rollback de treino ou promoção cubra contratação.

## Projeção, consulta e interface

Publicar vínculos bidirecionais e propriedade humana por IDs, com estado sem clube explícito. Atualizar os clubes antigo e novo no mesmo ciclo; não conservar o anterior como humano nem ka apontando ao técnico que já saiu. A janela de carreira deve permanecer acessível sem painel de clube. Após sucesso, dashboard, elenco, agenda, treino, estádio e permissões correspondem ao novo contexto.

Indicadores de diretoria/torcida usam `al.lL/of` e `al.lM/og`. No HEAD do commit `4f008afe`, `game.coaches` transporta `boardConfidence`/`fanConfidence` e `VisualModel` aplica os valores com `cl()`/`cm()`. `CareerViews.snapshot` ainda não os inclui, e não há nesta revisão evidência de leitura visual, propagação ao segundo cliente ou retomada. Não recalcular derrotas ou público no cliente. Nacionalidade, reputação e confiança são campos distintos; mostrar barras corretas não certifica a regra de demissão.

Reutilizar a apresentação nativa de convite/dados do destino com ações online. Não abrir callback offline a.gO.rT na projeção para depois tentar sincronizar o resultado. Durante pedido, preservar a seleção e impedir confirmação duplicada; erro permite corrigir/reconsultar. Fechar janela segue o ciclo de convite definido, sem aceitar por omissão. Não expor convites privados a outros técnicos nem estado pós-data IA ainda oculto.

## Retomada e compatibilidade

Persistir a decisão e, quando o ciclo definido exigir, as oportunidades pendentes com o checkpoint correspondente. O commit `246a94a1` grava `jobDate`/`jobOffers` em `exportState`/`restoreState`; falta demonstrar uma retomada de processo/sessão com o estado recuperado e definir se o cache de desempregado equivale a convite durável. Não reexecutar silenciosamente um sorteio para apresentar outro destino com a identidade de uma oferta anterior.

Save do técnico empregado/desempregado conserva nacionalidade, vínculos e contexto de histórico. Recuperação de data interrompida deve usar o conjunto de ofertas/decisões coerente com o checkpoint escolhido. O cache de vagas do desempregado passou a ser exportado no commit `246a94a1`; legado sem ofertas persistidas exige política explícita de reconstrução/indisponibilidade; não inventar uma aceitação concluída.

## Aceite observável

| Cenário | Verificação exigida |
| --- | --- |
| Desempregado | Com/sem candidatos; acesso por Carreira sem painel; consulta e confirmação corretas |
| Rota Agenda | Agenda → Central de Empregos não altera estado local sem host; ação online confirma pelo mesmo comando e atualiza todos os clientes |
| Empregado | Convite na fronteira anual nativa, com opção de decidir sem desligamento prévio |
| Concorrência | Destino ocupado depois da consulta, oferta expirada e repetição de pedido; nenhuma troca parcial |
| Vínculos | A→B→C e desligamento sem novo clube; ambos os sentidos coerentes, flags/IA sucessor atualizados |
| Preparação | Editor/Pronto/folga e clube com jogo corrente/próximo reconciliados; sem autoridade no clube anterior |
| Indicadores | Diretoria/torcida/nacionalidade iguais ao host; não usar oe como confiança da torcida |
| Erro e recuperação | Falha de armazenamento preserva checkpoint/vínculos; retomada antes/depois da decisão e oferta pendente conforme política |
| Clientes e pacote | Dois clientes recebem novo contexto; convite do destinatário correto; UI/JAR identificados |

Comparar o mesmo estado nativo; não exigir candidatos idênticos em inicializações aleatórias independentes. Testes de troca artificial de clube para verificar o diálogo de Treino não substituem contratação real. Implementação e validação dos fluxos acima permanecem abertas.

Relacionados: [avisos](spec-notificacoes-leiloes-online.md), [disponibilidade](spec-disponibilidade-jogadores-online.md), [histórico](spec-consultas-historicas-online.md) e [paridade](spec-paridade-nativa.md).

## Separação entre convite anual e Central de Empregos

Não colapsar duas origens nativas numa lista genérica de “empregos”. `afQ.b(coach,false)` é a origem de ofertas dirigidas por elegibilidade do técnico e aparece no fluxo anual do motor; é a fonte atual de `management.jobs`/`CareerManagement.job`. A Central nativa `a.gE` em modo Times usa `afQ.Ap()`, baseada no pool de IDs `Jl`; quando o pool não tem mais clubes sem treinador, a própria janela chama `afQ.zj()` para sortear candidatos entre clubes da metade inferior de cada divisão. Essa opção representa procurar/assumir uma vaga disponível, não aceitar uma oferta anual dirigida.

Manter comandos e estados distintos: (1) aceitar/rejeitar/adiar convite com técnico destinatário, clube oferecedor e ciclo/validade; (2) listar e assumir clube atualmente sem técnico, com disponibilidade revalidada pelo host e sem garantir que a vaga permaneça livre após a consulta. A Central não pode chamar o comando de aceitar convite só por enviar um `clubId`; o endpoint não pode substituir sua lista por `b(coach,false)` e chamar isso de paridade. Não executar `b(coach,false)` no getter da consulta para criar convite: o método embaralha candidatos, e o motor o chama novamente na transição anual. Persistir o conjunto produzido no evento junto da revisão/data/destinatário e aceitar somente esse registro; consulta posterior apenas lê o estado já criado. Persistir/cachear o pool nativo somente segundo a semântica de `Jl` e o ciclo de carreira; não sortear em cada snapshot nem aceitar ID de pool antigo depois de mudança de temporada/estado. A interface deve nomear os dois fluxos de acordo com a ação real.

Aceite adicional: comparar as listas por fonte no mesmo save; confirmar que Central apresenta apenas clubes atualmente sem treinador do pool nativo; convite anual fica associado ao destinatário correto; duas ações não podem ser confirmadas uma pela outra; clube ocupado/convite expirado são recusados sem troca parcial. A descoberta é estática e não certifica o comportamento em runtime.
## Cobertura estática existente após aceitar emprego

No comando host-backed `job` para técnico desempregado, o HEAD compara os elencos dos técnicos antes/depois da mutação; quando a contratação acrescenta o elenco do destino ao técnico, invalida a escalação e Pronto, avança revisão e publica o clube ligado ao técnico. O snapshot inclui esse clube entre os visíveis, e o cliente reconstrói o painel quando o `rosterKey` próprio muda se não há editor de escalação aberto; com editor aberto o código atual não refaz a rodada nesse callback. Essa trilha cobre estruturalmente invalidação/projeção do comando `/management`, sem provar comportamento dinâmico nem a apresentação correta após contratar. Não se aplica à ação `a.gE.Az()` preservada em Agenda, que ainda muta localmente, nem a uma troca direta de técnico empregado; não considerar esses caminhos cobertos por esta integração parcial.
