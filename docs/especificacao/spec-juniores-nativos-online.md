# Academia de juniores online

Status: requisitos e aceite geral ainda abertos para PN-08, revisão de 09/11/2026. Diagnóstico e evidências: [task de juniores](../task-juniores-nativos-online.md); [implementação parcial validada em branch isolada](../implementacao-juniores-nativos-online.md). Esta implementação é do agente executor, não integra a main e não encerra os critérios pendentes.

## Percursos atuais distintos

O botão nativo HW → `a.eg.qY()` → `a.ds` abre a academia com o clube de `VisualModel`. O modelo da tabela `b.N` usa diretamente `club.ky()`, ordena essa lista e escolhe o jogador pela referência da linha (`analysis/decompiled/b/N.java:26–37,49–55,68–70`). Hoje essa base vem do recurso local do clube e não é substituída pelo `youth` do host.

O menu adicional Carreira usa outro caminho: `/management` → `NativeCareerDialogs`, lista `youth`, ação `promote` com `playerId` (`src/brasfoot/NativeCareerDialogs.java:12–13,42–45,62–67`). Essa ação no servidor não integra os botões da academia nativa. A correção deve reutilizar a academia e a validação do host, sem deixar dois caminhos de mutação divergentes.

## Operações nativas e fronteiras

| Acesso | Comportamento nativo local | Estado online exigido |
| --- | --- | --- |
| Listagem e seleção | `b.N` lê e ordena ky; painel guarda FL selecionado | Lista do host, IDs estáveis e seleção preservada por ID após ordenar/atualizar |
| Promover (FQ) | `a.ds.pN()` limita profissionais a 35 e chama `best.K.a(false,F,clube)` e `F.aB(300)` | Encaminhar à ação `promote` existente, sem chamar K no cliente |
| Dispensar (FP) | `pM()` confirma, desvincula F e remove de ky e SR.Q | Novo contrato autoritativo específico ou indisponibilidade explícita; não usar venda/profissional para remover base |
| Definir apelido (FO) | `pP()` abre `a.l`; valida comprimento 2–32 e altera `F.setNome` | Comando por ID, validação equivalente, persistência e atualização dos consumidores; ou bloqueio explícito |
| Propor compra (FN) | Visível ao consultar base de outro clube, abre `a.cz`; limite 20 juniores | Rastrear negociação específica antes de habilitar; compra profissional existente não é equivalente |

Fontes: `analysis/decompiled/a/ds_0.java:151–245`, `a/l_0.java:49–67`. Na promoção, `best.K.a` recalcula força/habilidades usando regras e aleatoriedade nativas, muda condição de junior, define contrato de 180 dias e move o objeto entre listas (`best/k_0.java:20–139`). `aB(300)` escreve `fq` (`best/F.java:2929–2931`); não interpretar esse número como duração de contrato. O comando online já repete as mesmas chamadas em `CareerManagement.java:96–99`.

## Identidade, snapshots e autoridade

O host atribui IDs globalmente únicos a profissionais e juniores em `CareerManagement.ensureIds`, com reatribuição de negativos/duplicados (`:21–30`). O recurso local não pode criar um segundo junior com identidade própria que pareça ser o mesmo atleta. Publicar a base suficiente para a tabela nativa, incluindo posição, idade, lado, desenvolvimento e dados de apresentação exigidos pelos renderizadores, sem inventar defaults de força ou salário. Confirmar cada coluna no código antes de definir o formato final.

Cotejo estático das colunas realizado: `b.N` entrega o mesmo F para cada célula, e `b.ai_0` decide pela coluna (`analysis/decompiled/b/N.java:49–55`; `b/ai_0.java:30–90`). Dados mínimos de apresentação:

| Coluna nativa | Consumidor | Observação para o contrato |
| --- | --- | --- |
| Estado | `F.fS()` | Lesão/cartões dependem dos campos médicos e competição; não fabricar contusão |
| Posição | `getPosicao()` | Rótulo `aq.rI` |
| País | `getPais()` | Sigla e bandeira locais |
| Nome, idade e lado | `getNome()/getIdade()/getLado()` | Identidade por ID, não por nome |
| Desenvolvimento | `go()` usa `fd`, getter `fE()` | Barra de 1–100; não é `fitness`/energia `fp()` |
| CPE | `fD()` lê `fc` | Ícone estimado de 1–10; não é o potencial interno `fH()`/`es` |
| Características | `getCr1()/getCr2()` | Rótulos `aq.qN` |
| Valor estimado e salário | `fk()/fj()` | Valores confirmados pelo host, formatados nativamente |

O `playerRow` de gestão atual não publica país, lado, progresso, CPE nem características da base, embora publique força e dados profissionais. Transportar só essa lista para a academia não basta para fidelidade visual. Não expor automaticamente o potencial interno es para preencher a coluna CPE; o renderizador usa a estimativa fc. A condição nativa de junior também precisa ser preservada para o comportamento/formatos corretos.

Se `visualClubs` transportar a base, `VisualModel` precisa substituir/atualizar ky por IDs e vincular cada F ao clube correto. Evitar recriar recursos locais durante uma atualização e ressuscitar dispensados. Considerar campos médicos/contratuais da [spec de disponibilidade](spec-disponibilidade-jogadores-online.md) e ocultação temporal de IA; não propagar estado final de uma data ainda apresentada.

Todas as mutações usam o técnico autenticado, fase e revisão atuais. O host resolve clube/jogador e verifica a lista ky antes de aplicar. Promoção/dispensa afetam a elegibilidade do elenco e os rascunhos existentes: invalidar/revalidar os consumidores conforme `RoundCoordinator`, sem deixar editor antigo confirmar IDs removidos. Botão, atalho, seleção e diálogo secundário não podem conservar callback offline de escrita.

## Confirmação, erro e persistência

Enquanto pendente, mostrar valores confirmados e bloquear ações concorrentes sobre a mesma seleção. Atualização de tabela não envia comandos. Após erro/conflito, buscar estado corrente, restaurar controles e informar o motivo; não reaplicar intenção obsoleta automaticamente. Mudança de clube fecha ou reconstrói o painel.

Usar checkpoint nativo e metadados existentes. Para promoção, preservar identidade entre o junior anterior e o profissional resultante, contrato e efeitos nativos. Falha de gravação não deve aparecer como promoção/dispensa durável; verificar tratamento específico porque o rollback novo de treino não é um rollback geral de gestão.

## Aceite a executar futuramente

Lista nativa igual à base do host em criação e retomada; ordenação sem trocar alvo; promoção uma vez e remoção da base; limite de 35 e rejeições sem efeito; dispensa/apelido integrados ou bloqueados; dados atualizados em dois clientes e janela já aberta; IDs não reutilizados; editor/Pronto e troca de clube; falha de armazenamento; JAR distribuído. Testar compra de juniores como fluxo próprio quando houver contrato. O uso de juniores diretamente na escalação continua separado deste escopo.

Nota histórica: a restrição à análise estática acima precede a retomada de implementação autorizada pelo usuário em 09/11/2026. Os cenários não devem ser tratados como executados além das evidências explicitadas no [registro PN-08](../implementacao-juniores-nativos-online.md); os itens remanescentes continuam como aceite futuro.

## Formação por aposentadoria — capacidade relacionada ainda fora do recorte

O menu profissional original contém “Aposentar Jogador” (HE) e abre `a.ji` por `qW()`; o popup online da tabela o substitui por renovar/listar/retirar da lista. `a.ji.qW` permite escolher nome/posição de reposição e chama `F.a(nome,posição,false)` (`analysis/decompiled/a/ji_0.java:72–84`). O consumidor nativo retira o profissional e pode gerar junior ou promovê-lo conforme vagas/regras (`best/F.java:1657–1751`).

Esse caminho não é equivalente a dispensar um junior ou promover um já existente, e não há ação correspondente em `CareerManagement`. Documentar como capacidade ausente no adaptador ao inventariar a formação da base; não habilitar aposentadoria reutilizando `promote` ou removendo diretamente o F no cliente. A aposentadoria automática/aviso por idade tem outro percurso (`F.ge`) e não foi certificado funcionalmente nesta análise estática.
