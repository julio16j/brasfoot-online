# Implementação: Calendário e Tabelas nativos

Data: 11/11/2026  
Task: [calendários de ligas](../resolvidos/task-calendarios.md)  
Spec: [calendários e catálogo das seis ligas](../especificacao/spec-calendarios-ligas-online.md)

## Entrega

- O host publica `GET /calendar-catalog` para carreiras autenticadas. A resposta contém os jogos globais efetivamente materializados, as classificações das ligas domésticas dos seis países habilitados, participantes, rodadas agrupadas por data e a revisão do snapshot.
- As entradas HR (Agenda) e HS (Tabelas) da tela original de preparação encaminham para os painéis originais do Brasfoot: `a.L` e `a.ay`.
- O cliente consulta o catálogo do host ao abrir cada tela. Cria os objetos nativos de consulta com IDs de clube, competição, tabela e índices de data preservados. As linhas do Calendário exibem datas cronológicas, adversários, competição e resultado disponível.
- Tabelas reutiliza `f.s` com participantes e estatísticas autoritativas; Rodadas utiliza os mesmos objetos de partida registrados na tabela nativa. A inicialização cria os slots de competição nacional que a tela original consulta e configura as divisões/rebaixamentos para evitar estruturas nulas.
- O diálogo de Tabelas agora tem 740 px de altura para deixar visível o controle nativo Rodadas.
- A projeção da Agenda é somente de consulta. O listener nativo de detalhe por duplo clique é removido porque o catálogo não contém eventos/escalações/histórico necessários ao retrospecto; partidas disputadas ainda não abrem um detalhe sintético vazio.

## Validação executada

`python tools/build_adapter.py` compilou a implementação; `python tools/package_client.py` reconstruiu `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip`. O ZIP contém 22.735 entradas e passou na verificação CRC. O JAR cliente tem 426.137 bytes (SHA-256 `2ac77902160261ffea8012b8ca6f07267685ce0f1853541619e7c48bc4eb8afa`); o ZIP tem 80.321.653 bytes (SHA-256 `cc127f789de1734fd4071c38338df938bb4bf49992fcf6dcb4f848d6629bc75a`).

`NativeCareerCalendarViewsTest` passou com uma carreira real nova de um técnico no Corinthians. O teste consultou o endpoint autenticado e conferiu sua revisão; verificou o roteamento dos botões HR/HS, as seis ligas no catálogo, 75 datas com fixtures, 53 jogos do clube em 53 datas distintas, 18 participantes na Bundesliga, abertura das duas janelas nativas, datas distintas no Calendário e partidas na aba Rodadas. Capturas de Calendário, classificação e Rodadas foram inspecionadas e preservadas em [evidências](../evidencias/calendario-tabelas-nativas-2026-11-11/). O mesmo teste passou com `dist/brasfoot-online.jar` antes de `build/classes` no classpath.

O teste começa antes da primeira rodada; por isso pontos, jogos e gols da classificação aparecem em zero, conforme o estado do motor. A construção dos campos usa os contadores nativos de cada clube/tabela.

## Posicionamento da data inicial

Em carreira nova, `SessionHost.main` chama `CareerEngine.advanceToFirstHumanFixture` antes de criar a sessao HTTP. O host consulta `GameAdapter.hasHumanMatch()` para qualquer clube dos tecnicos selecionados. Datas apenas com jogos de IA sao processadas por `MatchPlayback`, com retomada automatica do intervalo, e o calendario avanca pelo caminho nativo `CareerEngine.nextDate`. Datas sem fixtures tambem executam os hooks de fim de data. O limite e de 400 datas e a rotina nao e usada ao retomar uma sessao.

Validacao: `CareerCountrySelectionTest` passou; o caso direcionado com Real Madrid confirmou que a data inicial nao tinha partida de tecnico e que o host avancou ate uma data com jogo humano. `python tools/package_client.py` reconstruiu `dist/brasfoot-online.jar` e `dist/Brasfoot-Online-Teste.zip`.

## Estado e limites

O recorte das duas telas está implementado no fonte. O teste visual não exerceu avanço/reconexão/retomada após partidas nem comparou zonas de acesso/rebaixamento entre host e renderer. A consulta é renovada a cada abertura, mas detalhes históricos, fases eliminatórias/pendentes, datas automáticas só de IA, saves legados e validação multitemporada permanecem abertos na spec. Portanto a spec completa de calendários não está concluída por este registro.
