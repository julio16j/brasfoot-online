# Feedback visual na criação da sessão

**Status:** implementação entregue; aceite visual do usuário pendente. Revisado em 11/11/2026.

## Relato e comportamento esperado

A criação ou retomada da sessão pode demorar sem indicar progresso. Durante essa espera, a própria janela deve escurecer e mostrar um indicador animado centralizado, sem abrir outra janela. Ao criar uma carreira, o feedback começa após escolher os times e técnicos e clicar em Iniciar; cobre a montagem da carreira e do checkpoint.

## Implementação e validação

`NativeClient` exibe um overlay semitransparente com barra de progresso indeterminada após fechar o seletor nativo em Iniciar e durante a inicialização do host. Cancelar a seleção não exibe overlay; conexão concluída, erro ou encerramento fecham o indicador.

`python tools/package_client.py` compilou e atualizou o JAR e o ZIP. Testes automatizados e aceite visual não foram executados.
