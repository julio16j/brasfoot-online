# Estado médico e disponibilidade dos jogadores

Status: requisitos para [PN-12](../task-projecao-lesoes-online.md), implementação parcial em `main`, aceite final aberto. Revisão 08/11/2026. A fonte autoritativa permanece o host; não reconstituir estado médico a partir de `eligible`.

## Campos e regras nativas comprovadas no código local

| Informação | Origem nativa | Regra |
| --- | --- | --- |
| Fim de contusão | `F.fo()/d(long)`, campo eI | `F.fP()` é verdadeiro quando eI > 0 e eI > data global em milissegundos (`best/F.java:319–325,1092–1094`) |
| Fim de contrato | Campo eJ; `fQ()` formata e `fR()` retorna dias restantes | Elegibilidade humana compara eJ < data global; a contagem `fR()` devolve 0 para prazo vencido, portanto não permite recuperar o instante nem distinguir todas as situações de limite (`:1067,1097–1119`) |
| Suspensão por competição | `F.c(best.at)`, registro `h_0.gT()`; `F.e(at)` retorna cartões/punição | Não equivale a contusão; pode bloquear uma competição e permitir outra (`:1170–1188`) |
| Elegibilidade para jogo | `F.a(I,false)` | Lesão, suspensão quando competição aplicável e contrato de técnico humano; amistoso/tipo 0 segue exceção nativa de suspensão (`:1055–1072`) |

O booleano atual mistura ausência de contexto e resultado negativo. Definir contexto explícito para a partida atual, sem atribuir a elegibilidade de hoje ao próximo confronto mostrado numa folga. `VisualModel` cria competições de apresentação por nome: não usar só esse nome como identidade global de suspensão ao reconstruir contexto.

## Contrato de snapshot

Adicionar aos jogadores de `game.coaches` e `visualClubs` o prazo médico real `injuryEndMillis` e o prazo exato `contractEndMillis` (ou representação igualmente sem perda), preservando `contractDays` como derivado de apresentação. Expor `eligibilityContext` nulo quando não há jogo atual; quando existe, incluir identidade de partida/rodada e resultado calculado no host, com motivos separados para lesão, suspensão e contrato. Motivos podem coexistir; não reduzir a um campo que esconda outro bloqueio.

Os nomes acima são proposta de contrato de implementação; manter consistência em todo produtor/consumidor e documentar o formato escolhido. Campo `eligible` legado pode permanecer como resultado derivado no contexto, mas nunca altera fim da contusão. Não criar prazo fictício nem renovar contrato do cliente com `Math.max(1,contractDays)` para fazer caber o layout.

Revisão técnica com o Agente 2: nomes `injuryUntilMillis`/`contractUntilMillis` são equivalentes aceitáveis aos acima. A proposta de reconstituir registros `best.H` na mesma referência canônica `best.at` é coerente: `F.h(at)` compara identidade `==` (`best/F.java:2087–2098`); `H.gS()/m(at)` acessam referência transitória e `gY()` guarda `at.gD()` em `ft` (`best/h_0.java:16,39–45,107–114`). Usar ID válido da competição como chave quando disponível; IDs ausentes/negativos precisam de identificador distinto por instância no snapshot, sem fundir por nome/tipo. Atuais e próximos confrontos da mesma competição devem compartilhar a referência na projeção. Testar competições homônimas e remontagem após retomada antes de habilitar a reconstrução de suspensão. Implementação desta proposta foi adiada pela instrução de parada do usuário, sem aceite do código ainda.

`VisualModel` deve projetar prazo médico real por `d(long)`, prazo contratual exato e contexto de suspensão/disponibilidade por mecanismo específico. Se o editor nativo depende de `F.a`, sua integração precisa manter o filtro correto sem trocar indisponibilidade por lesão artificial. A escolha concreta entre projeção do registro nativo de suspensão e um adaptador contextual deve preservar o consumidor nativo e a distinção visual. Não basta remover a linha `Long.MAX_VALUE` deixando todos selecionáveis.

Implementado: snapshot de profissionais publica `injuryUntilMillis`, `contractEndMillis` e registros de suspensão; projeção do cliente aplica esses dados e ignora `eligible` para estado médico/contratual. O teste de projeção cobre campos, suspensão reidratada e isolamento do host. Permanecem pendentes os cenários de UI nativa, lookup de suspensão por fixture em competições sem ID, troca de data, reconexão e playback temporal.

## Tempo, playback e persistência

O contexto recebido pertence à data/partida publicada. A projeção usa a mesma referência temporal do snapshot; mudar data em outro cliente não pode recalcular por relógio de parede. Lesão com prazo exatamente igual à data deixa de satisfazer `fP`; contrato usa comparação própria do motor, sem impor a mesma desigualdade por conveniência.

A renderização nativa de cartões `F.fS()` consulta a competição de `best.f.bl()` (`best/F.java:1130–1153`). `NativePreparation.create` chama `best.f.a(clube,partida)`; em folga o cliente pode passar o próximo confronto por `preparationMatch`. Logo, o contexto de consulta dessa tela pode ser próximo jogo, enquanto o contexto de edição/elegibilidade do host é a partida atual. A implementação futura deve explicitar essa distinção e associar registros à referência correta; não alimentar ambos com um booleano genérico ou manter referência global de competição anterior. Essa observação é estática e não constitui nova reprodução gráfica.

Durante apresentação, snapshots congelados e projeções temporais da IA devem ocultar estados médicos finais até o evento/minuto correspondente. Reutilizar o mecanismo de ocultação atual, testando que novos campos não vazam o fim do jogo. Não persistir objetos cliente no save do host: o estado nativo verdadeiro já pertence ao checkpoint, enquanto as novas informações são projeções.

## Aceite observável

1. Jogador saudável em folga aparece sem cruz médica e sem data de contusão fictícia, mantendo explícita a falta de contexto de partida.
2. Lesão real preserva o prazo exato e sua indicação; recuperação obedece à data global, inclusive igualdade na fronteira.
3. Atleta suspenso só é bloqueado no contexto correspondente e não recebe cruz de lesão; contrato vencido continua vencido e não recebe contusão.
4. Sugestão, tabela, detalhe, editor nativo e envio de escalação respeitam os motivos. Pedido direto de atleta indisponível continua rejeitado pelo host, sem mutação parcial.
5. Dois clientes, folga→dia de jogo, partida de competição diferente, reconexão e retomada preservam os campos; nenhuma informação médica futura é publicada no playback da IA.
6. Janela real comprova os rótulos/ícones e o desaparecimento da data absurda. Testar fontes e JAR após integração do recorte; não aceitar apenas asserção de campo.

Evidência inicial: `AuditEligibility.java`/`result-eligibility.txt` reproduziram o bug na principal; a captura da [revisão da folga](../revisao-independente-folga.md) mostra o efeito gráfico. Novas execuções da implementação devem identificar branch/commit ou hash do JAR e registrar seus limites.

Validação de diagnóstico posterior à integração: a [matriz PN-12](../task-projecao-lesoes-online.md) confirma separadamente lesão, contrato vencido e suspensão no JAR combinado, além de controle saudável e janela real. Probe de identidade confirma que o mapa por nome funde competições distintas e altera o tipo da primeira. O contrato precisa preservar a identidade de cada competição na lista de partidas, no próximo confronto e nos registros disciplinares; nome e tipo são atributos, não identificadores exclusivos. O efeito do contrato vencido deve ser corrigido com o campo exato, sem usar contagem truncada em dias como fonte autoritativa.
