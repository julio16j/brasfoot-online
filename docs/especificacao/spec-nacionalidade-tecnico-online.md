# Nacionalidade do técnico online

Status: requisitos abertos, 09/11/2026. Origem: [task com diagnóstico estático](../tasks/sem-prioridade/task-nacionalidade-tecnico-online.md), anterior a esta spec. Nenhuma criação/retomada de técnico foi executada nesta revisão.

## Objetivo e referência

Preservar a nacionalidade escolhida no seletor nativo até o técnico autoritativo e sua projeção nos clientes. A referência é a.bV.or → best.n.H → ah.b(nome, país) → al.cg; al.lE é o getter. Nacionalidade não é país do clube, país associado ao histórico do técnico nem país de liga habilitado.

A [restrição às seis ligas](spec-calendarios-ligas-online.md) aplica-se às escolhas de clubes/competições. Manter o catálogo nativo de nacionalidades, inclusive países que não oferecem liga na carreira. Não validar nacionalidade apenas por SR.N, pois esse conjunto representa países de competições habilitadas e pode ser menor.

## Seleção, transporte e validação

1. Cada técnico confirmado no seletor deve conservar referência estável do clube, nome e código nativo de nacionalidade. Capturar al.lE ao construir CoachChoice; não usar a última seleção global do combo para todos os técnicos adicionados.
2. Usar o código do catálogo nativo de nacionalidades, com resolução equivalente a best.n.H/best.n.cY. Não transmitir índice visual, posição na lista ordenada nem nome do país como identidade.
3. Transportar esse dado até SessionHost/CareerEngine em associação inequívoca com cada escolha. Um registro por técnico é preferível a listas paralelas; se a implementação mantiver arrays, validar cardinalidade/ordem antes de criar qualquer técnico. O nome concreto do argumento/DTO é decisão de implementação.
4. O host valida código existente e tipo/formato. Nacionalidade explícita inválida não pode virar silenciosamente país do clube ou zero. Rejeitar o pedido inteiro de criação com mensagem identificável, preservando o rascunho do seletor.
5. Criar o técnico usando o país escolhido na chamada nativa apropriada. A geração de competições continua baseada nos clubes/política das ligas; nacionalidade brasileira em clube espanhol não habilita automaticamente a liga brasileira.

Definir compatibilidade para os caminhos por nomes, IDs legados e assets. Se um chamador antigo omitir nacionalidade, pode-se manter o fallback histórico para país do clube, desde que documentado e distinguível de uma escolha explícita. Não sobrescrever uma escolha válida durante reinicialização do mundo ou resolução de assets.

## Estado autoritativo, projeção e persistência

Publicar a nacionalidade como campo próprio no registro do técnico, vinculado ao coachId. Aplicar al.cg no objeto projetado correspondente, preservando nome, ID, clube, flag humana e referências usadas pelos perfis. Campo ausente em snapshot legado não deve ser tratado automaticamente como código zero nem apagar um valor já conhecido.

O host continua responsável pelas consequências esportivas/gestão. best.ay.b consulta al.lE como uma das fontes de convites; não executar esse gerador no cliente para compensar a falta de dados. Corrigir a nacionalidade não certifica o protocolo de convites, ainda tratado na [task de empregos](../tasks/sem-prioridade/task-empregos-tecnicos-online.md).

Save/retomada devem preservar o valor autoritativo lE. Não derivá-lo novamente do clube atual, sobretudo após troca de emprego. Se um save antigo já contém a nacionalidade substituída, não há prova da escolha original do usuário para restaurá-la por inferência; manter o estado conhecido e documentar a limitação. Uma edição posterior de nacionalidade exigiria comando próprio e não faz parte desta correção.

## Interface e erro

Reutilizar o controle nativo de nacionalidade e os perfis pertinentes. Mudar país da liga/clube não deve trocar uma nacionalidade explicitamente escolhida apenas por reutilização de combo. Cancelar criação não inicia host nem altera uma carreira existente. Falha de inicialização deve permitir corrigir a seleção sem perder a associação entre técnicos.

Perfis de outros técnicos devem mostrar a nacionalidade publicada, e não a do clube visitado ou o default de new al(nome). Catálogos locais servem para rótulo/bandeira; o valor vem do host. Reconexão e abertura tardia não dependem de qual combo foi usado na máquina que criou a sessão.

## Aceite observável

| Cenário | Verificação exigida |
| --- | --- |
| Países diferentes | Brasileiro no Real Madrid e espanhol no Corinthians mantêm clube/ligas corretos e nacionalidade escolhida |
| Catálogo independente | Nacionalidade de país fora das seis ligas permanece selecionável e válida no host |
| Vários técnicos | Duas ou mais escolhas distintas preservam associação após adicionar, ordenar, trocar clube e iniciar |
| Identidade | Código nativo difere do índice visual usado no teste; host e clientes recebem o código correto |
| Erro | Código desconhecido/cardinalidade incorreta rejeitados sem criar parcialmente a carreira ou trocar nomes/clubes |
| Legado | Omissão explícita segue fallback documentado; save existente mantém seu lE; não prometer recuperar escolha já perdida |
| Consulta e retomada | Perfil nativo em segundo cliente, reconexão e nova JVM mostram o mesmo valor, inclusive após troca de clube |
| Consequência | Confirmar o valor consumido pelo gerador nativo sem exigir convites idênticos em inicializações aleatórias distintas |
| Artefato | Exercitar o seletor/host/JAR entregue, identificando versão; teste de DTO sozinho não certifica o percurso |

Revisão estática de 10/11: o diff atual transporta/aplica/projeta o código nativo, mas `CareerEngine.validateNationalities` consulta `best.n.cY()` antes da inicialização do catálogo na JVM host recém-lançada. Como `cY()` retorna lista vazia até `cU()`, o caminho explícito do launcher pode rejeitar todos os países. O teste adicionado reutiliza a JVM após cenários que inicializam o catálogo e não prova arranque frio. Corrigir/verificar essa ordem e cobrir seletor, segundo cliente, save/retomada e efeitos antes de fechar a spec. Relacionados: [paridade](spec-paridade-nativa.md), [histórico/perfis](spec-consultas-historicas-online.md) e [avisos](spec-notificacoes-leiloes-online.md).
