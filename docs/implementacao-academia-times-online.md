# Integração do acesso Times → Academia

Estado: integrada na `main` em 10/11/2026. Relacionada à [task Academia pelo acesso Times](task-academia-via-times-online.md) e à [spec de juniores](especificacao/spec-juniores-nativos-online.md).

Ao abrir Times a partir de um jogador pesquisado, `openSelected` agora passa o vendedor/clube exibido em `a.iA.uk` e o comprador autenticado em `a.iA.ul`, conforme o construtor nativo e o consumo do cabeçalho/elenco. O botão `HW` resolve a academia a partir de `uk` e abre `NativeYouth` com o comprador como viewer. Assim, a base consultada corresponde ao clube da janela; promover permanece autorizado somente para a própria base, enquanto dispensar, compra e apelido continuam bloqueados pelo wrapper de juniores.

`NativeTransferMarketTest` prepara uma instância isolada de `a.iA` e confirma que o encaminhamento seleciona `uk` mesmo quando `ul` é um clube diferente. `NativeTransferMarketAcademyWindowTest` também abriu Times e HW com interação Swing e confirmou o elenco remoto, promoção bloqueada e as outras três mutações indisponíveis. A verificação foi executada com `dist/brasfoot-online.jar` à frente das classes de teste no classpath. `python tools/build_adapter.py --test` passou. `python tools/package_client.py` atualizou o JAR e o ZIP; `zip.testzip()` retornou `None` para 22.735 entradas. SHA-256 do ZIP: `f1e990497194d3c8640be231d279ed1f75a0194cb6de7b380e6eace22ab924ae`.

Limites: a validação gráfica cobriu consulta da base remota em Times→Academia; ainda faltam promoção da própria base nesse acesso, fluxo dashboard, entrada Procura→Times→Academia, atualização multi-cliente e retomada/save para fechar a task inteira.
