# Táticas salvas e escalação padrão

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: aberta complemento de PN-11; investigação somente por código em 08/11/2026. Nenhuma janela, motor ou teste executado nesta etapa.

## Comportamento nativo local

No editor `a.bz`, o botão vb “táticas salvas” mantém listener `bf_0` que chama `oj()`, abrindo `a.il` (`analysis/decompiled/a/bz.java:607,691–702`; `a/bf_0.java:20–22`). O diálogo salva, carrega e apaga objetos `components.ag` na lista `club.kV()` (`a/il_0.java:85–173`).

`components.ag` contém nome, lista de referências de jogadores e lista de posições. O salvar percorre células 0–36 e mantém posições vazias por placeholders quando presentes. Não grava o vetor de estilo/marcação/ataque de `club.kj()` nessa estrutura. Logo, “táticas salvas” nesse fluxo não equivale automaticamente ao vetor `tactics` enviado pela API.

Carregar chama `bz.a(ag,true)`: o editor só reaplica referências presentes na coleção AF de disponíveis; para escolhido ausente/inapto deixa posição vaga/placeholder (`a/bz.java:420–456`). A escalação padrão usa outro campo, `club.kX()`, e `W(true)` a atualiza no fluxo original; checkbox Be alterna a configuração nativa (`a/bz.java:398–419,625`; `a/be_0.java:20–21`; `best/ah.java:2039–2057`).

## Integração presente e lacunas

`NativeLineup` substitui Aplicar/Fechar e alguns atalhos; `routeKeyboard` desabilita todas as caixas, incluindo escalação padrão e juniores. O botão de táticas salvas permanece com seu listener original no editor editável. Seus comandos de salvar/apagar mexem em `kV()` do clube cliente; não são redirecionados ao host.

`NativeLineup.extract` só envia titulares, reservas, células e vetor tático atual. `GameAdapter.visualClub`/`VisualModel.updateClubs` não publicam/projetam a biblioteca kV nem o padrão kX. Não há ação correspondente em `/management`/escalação. Portanto, carregar um desenho e depois aplicar pode usar o envio de escalação normal, mas salvar uma biblioteca local não comprova persistência online dela. O checkpoint do host não recebe essas mutações locais.

Essas conclusões são de inspeção; não foi certificado graficamente o tempo de vida da biblioteca na sessão atual nem seu resultado após reconexão. O checkbox padrão está explicitamente bloqueado e não deve ser anunciado como integrado.

## Contrato necessário

Definir biblioteca por clube/técnico e sua persistência no host, com nome e IDs estáveis dos atletas, células e placeholders. Não serializar referências de objetos do cliente como se fossem os mesmos F do motor. Separar guardar/carregar biblioteca de confirmar a escalação da partida. Caso se opte por biblioteca estritamente local, a interface e a persistência local precisam de contrato explícito; o estado atual não comprova nenhuma das duas entregas.

Reutilizar o diálogo nativo; remover callbacks de escrita apenas local com aparência de save da carreira. Revalidar atleta vendido/dispensado/promovido, suspensão/lesão e competição corrente ao carregar, preservando lacunas como no nativo em vez de forçar escolhido inválido. A correção da [disponibilidade](../../especificacao/spec-disponibilidade-jogadores-online.md) é dependência para não confundir bloqueios ao carregar.

Escalação padrão exige decisão própria: quais dados são armazenados, quando se aplicam e quando a elegibilidade é reavaliada. Não reativar apenas a caixa Be sem transportar a opção e o padrão ao host. Papéis especiais/capitão são outro [contrato](../../especificacao/spec-papeis-escalacao-online.md).

Aceite futuro: salvar/listar/carregar/apagar na janela nativa; posições vazias e jogadores ausentes; índice alto de elenco; revisão/propriedade; erro/falha de gravação sem confirmação enganosa; dois clientes; troca de clube; save/retomada/reconexão; distinção entre biblioteca, padrão e envio da escalação atual. A execução fica pendente até nova autorização para abrir o jogo.
