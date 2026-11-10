# Ordenação da coluna Car. na Procura

## Alteração

Após cada atualização dos resultados de Procura, o cliente instala comparador explícito na coluna `Car.` do `TableRowSorter` nativo: índice 8 no modo geral e 14 no modo de habilidades individuais. A ordenação usa os dois códigos nativos (`Cr1`, `Cr2`) em ordem crescente, correspondendo às categorias exibidas e evitando comparar `F.toString()` ou apenas o nome do jogador.

## Validação

`NativeTransferSearchTest` confirma ordenação crescente dos códigos no índice geral, preservação da identidade do jogador e resolução da seleção visual após ordenar. `NativePlayerSearchWindowTest` agora aciona o cabeçalho `Car.` na Procura real, verifica a ordem dos códigos em todas as linhas, seleciona o resultado remoto depois do sort e confirma que Times recebe esse ID. O teste passou contra o JAR empacotado. `NativeTransferMarketAcademyWindowTest` também passou no mesmo pacote. O ZIP contém 22.735 entradas, `zip.testzip()` retornou `None`, SHA-256 `e72eaf128565bcef6d3fb15f239a39cc676ee0cee11c7f95176ea1226bbac527`.

## Limites

O comparador foi exercitado em tabela Swing reduzida para o índice geral. A coluna equivalente no modo individual compartilha a função, mas ainda não tem fixture gráfica própria. A ordenação das demais colunas permanece sujeita à paridade da spec de Procura.
