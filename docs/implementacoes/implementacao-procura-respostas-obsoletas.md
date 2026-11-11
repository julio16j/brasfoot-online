# Evitar ações e respostas obsoletas na Procura

Enquanto a consulta remota está pendente, Enter e duplo clique não abrem Times com a seleção da busca anterior. Se o usuário editar filtros ou usar Resetar antes da resposta, o callback compara os filtros capturados com os atuais, descarta a resposta obsoleta e preserva a última tabela válida. Erros também liberam o estado pendente e deixam a lista anterior visível.

`NativeTransferSearchTest` cobre estado pendente e comparação de filtros. `NativePlayerSearchWindowTest` abre a Procura real, simula uma resposta atrasada após edição de filtro e confirma que a tabela anterior é mantida; também verifica a navegação bloqueada enquanto pendente. Testes de mercado passaram. O teste Swing passou contra `dist/brasfoot-online.jar`; o ZIP contém 22.735 entradas e `testzip()` não encontrou erro.
