# Investigação dos calendários de ligas

Data: 6 de novembro de 2026. Escopo: código local, motor original incluído no projeto e carreiras descartáveis. Nenhum save existente foi carregado ou alterado. Esta etapa acrescenta diagnóstico e planejamento; não muda regras do jogo.

Complemento estático de 08/11/2026: [marcação de amistosos na carreira](especificacao/spec-amistosos-carreira-online.md). O acesso nativo depende de datas futuras do host, ausentes da projeção de uma única data; o modo avulso `--demo` não equivale a agendamento na carreira. Contrato e critérios próprios registrados, sem executar o jogo nem alterar o planejamento de geração de ligas abaixo.

## Resultado principal

A hipótese de que somente a liga brasileira funciona não se confirmou: os cenários abaixo geraram jogos de liga e avançaram até janeiro de 2023 sem exceção. Há, entretanto, um defeito confirmado na ordem de inicialização das ligas e limitações de navegação e apresentação que prejudicam a experiência fora do Brasil.

Os calendários são os do Brasfoot 22–23 local. Esta investigação não compara datas com calendários reais atuais.

## Evidência das simulações

Contagem capturada imediatamente após a criação. A coluna de jogos contabiliza a liga nacional, sem somar copas, estaduais ou continentais. Todos os cenários concluídos processaram 152 datas globais até a primeira troca de ano.

| País / cenário | Clube | Jogos de liga inicialmente visíveis | Datas simuladas sem jogo humano | Resultado |
| --- | --- | ---: | ---: | --- |
| Brasil | Corinthians | 38 | 75 | Avançou a 2023 |
| Espanha | Real Madrid | 38 | 98 | Avançou a 2023 |
| Espanha | Barcelona | 38 | 96 | Avançou a 2023 |
| Inglaterra | Liverpool | 38 | 90 | Avançou a 2023 |
| Alemanha | Bayern de Munique | 34 | 100 | Avançou a 2023 |
| Itália | Juventus | 38 | 98 | Avançou a 2023 |
| França | Paris Saint Germain | 34 | 94 | Avançou a 2023 |
| Argentina | Boca Juniors | 29 | 105 | Avançou a 2023 |
| Brasil + Espanha | Corinthians + Real Madrid | 38 por clube | 67 | Avançou a 2023 |

Em todos esses cenários, havia 43 países com competições geradas; os indicadores de países habilitados continham somente o país escolhido ou os dois da sessão mista. As classificações iniciais retornaram uma tabela por clube estrangeiro e duas para o Corinthians, que também disputa o Paulista. Essas contagens são observações do motor, não uma certificação dos regulamentos: em particular, os 34 jogos da França devem ser confrontados com a configuração local de participantes antes de decidir se há necessidade de atualização da base.

A primeira rodada do diagnóstico usou os nomes `Bayern München` e `Paris Saint-Germain`, ausentes no catálogo local. Essas duas tentativas falharam na resolução do nome, antes de testar suas carreiras. A seleção foi corrigida para os nomes locais `Bayern de Munique` e `Paris Saint Germain`. Não interpretar essas falhas do diagnóstico como erro do calendário ou do seletor por IDs. O script já usa os nomes corrigidos.

## Como reproduzir

Com Java 8 e os artefatos locais de compilação disponíveis:

```powershell
python tools/build_adapter.py
python tools/investigate_calendars.py
```

O diagnóstico compila sua própria classe em `analysis/calendar-investigation/`, cria uma JVM por cenário e usa o mesmo caminho por IDs empregado pela interface gráfica. Registra países habilitados, países com competições já geradas, jogos inicialmente visíveis por competição, tabelas e avanço até a primeira troca de ano. Simula as partidas com `MatchPlayback` e `CareerEngine.nextDate`, sem abrir o servidor HTTP ou clientes gráficos.

Resultados brutos: `analysis/calendar-investigation/results.json` e `case-*.log`; os cenários repetidos com nomes corrigidos estão em `results-additional.json` e `case-additional-*.log`. Também é possível passar nomes locais como argumentos para executar somente alguns cenários. A pasta `analysis/` é ignorada pelo Git; a tabela acima preserva a evidência principal no repositório. Os números de datas sem jogos humanos podem variar com os resultados e a permanência do técnico no clube. Jogos de fases futuras de copas podem ser criados durante a temporada; sua ausência na captura inicial não demonstra perda de partidas.

## Problemas confirmados e limitações

### 1. Países escolhidos são aplicados depois da geração de competições — prioridade alta

Em `src/brasfoot/CareerEngine.java`:

1. `create(File, int[], String[])` chama `initialize(root, true)`.
2. `initialize` marca todos os países de `bF` como habilitados e chama `best.p.dg()`.
3. `dg()` instancia clubes e gera competições; no motor descompilado, `best/p_0.java`, chama `w(bF)`, que cria as ligas de cada país habilitado com `Z.Ar()` e as adiciona a `SR.N()`.
4. Só depois, `start` altera os indicadores de `bF` para os países dos clubes humanos. Não desfaz as competições já criadas.

Evidência de execução: tanto com somente Corinthians quanto com somente Real Madrid, `SR.N()` contém **43 países**, embora `bF` indique apenas um país habilitado. Os indicadores de configuração e o mundo já gerado discordam. Não se deve remover indiscriminadamente todos os clubes estrangeiros: eles também podem ser necessários às competições continentais.

Consequência observada: calendário global inclui muitos jogos sem humanos. No cenário Corinthians foram 75 de 152 datas simuladas; no Real Madrid, 98 de 152. A simulação confirma o excesso de datas, mas não mede seu custo total na interface ou na rede.

Implementação proposta: separar descoberta dos clubes de criação da carreira. Resolver IDs e países antes de `dg()`, aplicar a seleção e gerar o mundo uma única vez. Manter os participantes estrangeiros necessários ao motor. Definir explicitamente se ligas adicionais sem humanos serão uma configuração do host.

### 2. Criação por nome aceita somente clubes brasileiros — prioridade média

`CareerEngine.create(File, String[], String[])` usa `initialize(root, false)` e exige `candidate.getPais() == 29` na busca. O parâmetro `--clubs` de `SessionHost` utiliza essa sobrecarga. Assim, tentar criar uma carreira estrangeira por esse caminho resulta em `Unknown club`, mesmo que o clube exista no catálogo.

A interface gráfica passa `--club-ids` e usa outra sobrecarga. Portanto, essa restrição explica falhas em CLI e automações, mas não prova que a seleção gráfica estrangeira esteja quebrada.

Implementação proposta: adotar IDs como identidade principal; se mantida a busca por nome, aceitar país e detectar ambiguidades. Cobrir os dois pontos de entrada.

### 3. Datas sem jogo humano ainda dependem da barreira de Pronto — prioridade alta

`RoundCoordinator` inclui todos os técnicos na barreira. `SessionHost.kickoffIfReady()` acelera uma data sem humanos somente depois de chegar a `WAITING_FOR_ENGINE`. Ao terminar, deixa ao host o avanço para a próxima data. `GameAdapter.ready` permite confirmar Pronto sem jogo, mas isso ainda exige a confirmação de todos.

Logo, acelerar a simulação não elimina a sequência de confirmações e avanços em datas que só interessam à IA. Em sessões de países distintos, também é necessário distinguir quem tem jogo e quem está de folga.

Implementação proposta: identificar participantes necessários por data; processar datas exclusivas da IA no host até um ponto relevante de parada. Definir esses pontos antes de automatizar: partidas humanas, decisões de gestão que precisem de intervenção e fim de temporada. Preservar efeitos pós-jogo e checkpoints. Não pular apenas o índice do calendário.

### 4. Calendário e classificação dependem dos jogos já materializados do clube — prioridade média

`CareerViews.snapshot` percorre `SR.R()` e inclui apenas jogos do clube do técnico. Descobre classificações pelas instâncias de `f.s` presentes nesses jogos. Não oferece catálogo de ligas, filtro por país ou visão de outro campeonato.

Efeitos: uma liga fora dos jogos desse clube não pode ser consultada; uma competição sem confronto ainda materializado pode não aparecer. Técnicos sem clube recebem todos os jogos no calendário por causa de `club == null`, mudando silenciosamente o escopo da tela. As classificações também seguem esse percurso global nesse caso.

Implementação proposta: separar consultas de calendário e classificações, listar competições diretamente de seus registros e oferecer filtros por clube, país, competição e temporada. Explicar na tela quando próximos confrontos dependem da definição de uma fase.

### 5. Ordenação de datas e atualização da janela — prioridade média

O calendário envia apenas `day.f()`, uma data formatada, e `NativeCareerDialogs.table` cria um `DefaultTableModel` sem tipo de coluna com ordenação automática. Ao clicar na coluna Data, a ordenação usa os textos e pode colocar `01/12` antes de `02/01`. A ordem inicial vem do calendário nativo; o problema surge na reordenação da tabela.

Complemento estático de 08/11: o mesmo helper constrói as classificações e não sobrescreve getColumnClass. Assim, o sorter não recebe o tipo numérico de posição/pontos/jogos/saldo e pode ordenar pelas representações textuais (por exemplo 1, 10, 2). A ordenação inicial por posição em `CareerViews` é explícita e não comprova a ordenação interativa por coluna. Preservar tipos/comparadores numéricos, sinal do saldo e valores ausentes; acrescentar números de um/dois/três dígitos ao aceite. Sem execução de janela nesta etapa.

Além disso, a janela de carreira é montada com uma consulta pontual a `/overview`; não há atualização periódica dessa tabela enquanto o calendário avança. O usuário precisa reabri-la para obter novo estado.

Implementação proposta: enviar data ISO ou timestamp e identificadores estáveis de partida/competição, mantendo a formatação na apresentação. Usar comparador temporal e atualizar a janela após mudança de estado.

### 6. Cobertura anterior não validava ligas estrangeiras — prioridade alta

`tests/brasfoot/CareerProbe.java` inicia com Corinthians, Palmeiras, Santos e São Paulo pela sobrecarga de nomes. `tools/tests/test_career.py` executa esse cenário por vários anos e retomada. Essa cobertura é útil para o Brasil, mas não comprova o fluxo por IDs nem uma carreira com países distintos.

O diagnóstico novo é exploratório: gera relatórios, registra exceções e limita a duração. Não substitui uma suíte de regressão com expectativas por liga e falha automática quando as invariantes forem violadas.

## Ordem proposta de implementação

| Etapa | Entrega | Critério de aceite |
| --- | --- | --- |
| 1 | Corrigir seleção e geração por IDs; definir ligas adicionais | Países com ligas simuladas correspondem à configuração; participantes continentais necessários continuam disponíveis; nenhuma liga escolhida fica sem calendário |
| 2 | Automatizar datas exclusivas da IA e tratar folgas | Sessão não exige Pronto de todos em datas exclusivas da IA; para nos pontos definidos; nenhum jogo ou efeito pós-jogo é duplicado ou perdido |
| 3 | Separar calendário, catálogo de competições e classificações | Técnico consegue consultar sua liga e outras ligas habilitadas; estado sem clube tem escopo explícito; copas indicam fases pendentes |
| 4 | Corrigir datas e atualizar a janela | Ordenação cronológica correta entre meses e anos; resultados e próximas fases aparecem sem reabrir a tela |
| 5 | Ampliar criação e regressão internacional | CLI e interface usam a mesma política de seleção; duas temporadas e retomada passam para ligas representativas e sessão mista |

Adicionar testes de regressão junto de cada correção. Na etapa 5, consolidar a matriz: Brasil, Espanha, Inglaterra, Alemanha, Itália, França, Argentina, uma divisão inferior e uma carreira com Brasil + Europa. Verificar jogos esperados conforme a configuração local, confrontos duplicados, conflitos de horário por clube, pontuação, progressão de copas, promoção/rebaixamento e consistência após save/reload.

## Limites e decisões para o planejamento

- Manter o calendário nativo do Brasfoot é o escopo inicial. Migrar para temporadas europeias agosto–maio seria uma mudança de regras e exige investigação própria; o avanço em janeiro, isoladamente, não prova um defeito do adaptador.
- Não há evidência suficiente para declarar todos os países disponíveis funcionais ou quebrados. Os cenários exercitam clubes específicos e a primeira troca de ano, sem a cobertura completa de rede, interface, várias temporadas ou retomada estrangeira.
- Corrigir a criação afeta carreiras novas. Saves existentes já contêm o mundo gerado; decidir separadamente entre manter compatibilidade e oferecer migração validada. Não remover competições de saves existentes como efeito incidental da correção.
- Prioridade recomendada: corrigir a inicialização e o fluxo de datas antes de ampliar a interface ou reescrever regras de campeonatos.
