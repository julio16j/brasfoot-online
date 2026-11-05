---
name: brasfoot-native-first
description: Apply when implementing or changing Brasfoot Online features, especially screens, buttons, dialogs, game actions, or packaging.
---

# Brasfoot Online: desenvolvimento com prioridade nativa

Ao implementar uma funcionalidade neste projeto, preserve e reutilize primeiro os menus, telas, validações, regras e listeners nativos do Brasfoot. Antes de criar uma interface substituta, localize como a ação original é construída e acionada. Intercepte apenas o necessário para encaminhar a operação ao host e manter o estado multiplayer sincronizado. Evite trocar uma tela nativa por um menu genérico quando a tela original puder ser usada.

Para cada funcionalidade nova, escreva testes unitários que cubram seu comportamento e os casos inválidos relevantes. Execute esses testes depois de cada implementação e corrija falhas antes de concluir.

Depois de concluir uma implementação, atualize os artefatos distribuíveis executando `python tools/package_client.py`. Esse script recompila o cliente e atualiza `dist/brasfoot-online.jar` e `dist/Brasfoot-Online-Teste.zip`. Se a compilação ou o empacotamento falhar, corrija a causa e repita; não deixe os artefatos distribuídos desatualizados em relação ao código-fonte.

Na revisão final, confira se o fluxo nativo continua acessível, se a alteração é aplicada no host quando necessário e se o JAR e o ZIP foram gerados com sucesso.
