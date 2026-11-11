# Usar o botão nativo Opções como acesso às configurações online

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: implementada, aguardando validação do usuário. Revisado em 7 de novembro de 2026. O percurso nativo foi integrado e os testes de opções e carreira passaram. Ver [registro da implementação](../../implementacoes/implementacao-opcoes-nativas-online.md) para componentes, validação e estado dos artefatos.

Plano técnico e critérios de implementação: [spec de Opções nativas online](../../especificacao/spec-opcoes-nativas-online.md).

## Relato e decisão do usuário

O botão adicional **Configurações** funciona e realmente altera a velocidade. Seus valores aparecem na tela nativa **Opções**, mas alterações feitas em **Opções** não percorrem o caminho inverso até o estado autoritativo da sessão.

O usuário não quer o botão adicional. O acesso deve ser o botão nativo do jogo chamado **Opções**, com a aparência e a navegação originais. Não basta renomear o botão criado no adaptador ou manter dois caminhos independentes de escrita.

## Diagnóstico anterior à implementação

- `src/brasfoot/NativeClient.java`, `gameScreen`: cria explicitamente `new JButton("Configurações")` e abre `NativeSessionOptions`, conectado a `sendOptions`.
- `src/brasfoot/NativePreparation.java`: preserva listeners nativos para botões sem roteamento online específico, incluindo o caminho a rastrear de Opções (`HX`). Conferir sua construção e os atalhos no código nativo antes de alterar.
- `SessionOptions.project` projeta o estado recebido sobre as opções locais. Essa projeção explica um caminho de atualização, mas não substitui o envio de comandos autorizados pelos controles nativos.
- Rastreamento concluído: `HX → a.ew → popup Hy → Preferências → fc_0 → eg_0.rv → s_0.a(JTable) → new fB(...)`. O atalho F3 também chega a `s_0.a(JTable)` pelo callback de teclado original. A navegação nativa deve ser preservada, com o item Preferências encaminhado ao controlador online.
- `fB` instala listeners antes de preencher seleções. `fW → fB.c(Object)` chama setters de `c.a.vM()` e depois `c.a.vJ()`, que grava `options.bcf` no processo local. O botão de fechar e a confirmação da paleta também possuem caminhos de gravação. Remover listeners apenas depois da construção completa não impede esses efeitos iniciais.
- O popup `Hy` é um campo separado; a limpeza recursiva do painel de preparação não garante a interceptação de seus itens. O teste atual de `NativeSessionOptions` abre o adaptador diretamente e não comprova o percurso pelo botão original.
- Causa confirmada por inspeção: a janela nativa segue o caminho offline e não chama `sendOptions`; o caminho adicional passa pelo endpoint. A reprodução gráfica e a validação do futuro roteamento permanecem pendentes.

## Implementação requerida

Remover o botão adicional **Configurações** e conectar o botão nativo **Opções**, sua tela e seus acessos equivalentes ao fluxo online existente. Reutilizar os componentes e comportamentos nativos, interceptando o necessário para enviar alterações ao host em vez de gravar preferências locais ou operar apenas sobre a projeção.

Preservar `POST /session-options`, revisão, validação atômica, persistência e confirmação do servidor. No host, alterações pelos controles nativos devem produzir o efeito real e se propagar aos participantes. Os participantes continuam em modo de consulta, inclusive em amostras de cores, tabelas, setas, diálogos e atalhos. Opções ainda sem integração permanecem bloqueadas também no host.

Atualizações recebidas devem refletir-se na janela nativa já aberta sem reenviar comandos. Erros e conflitos restauram o estado confirmado, sem repetir automaticamente uma escolha antiga. Manter o controle de áudio coerente com `soundsEnabled` e preservar limites nativos e sobrescrita CLI.

Esta task trata o acesso e o fluxo dos controles já suportados. Não declara concluída a integração das doze opções ainda bloqueadas, que continua no registro de implementação.

## Critérios de aceite

1. O botão adicional **Configurações** deixa de existir. O usuário acessa a função pelo botão nativo **Opções**, sem tela substituta ou caminho paralelo de gravação local.
2. Alterar velocidade por **Opções** no host muda o relógio do servidor e os valores de todos os clientes; fechar e reabrir a tela mantém o estado confirmado.
3. Os demais controles suportados seguem o mesmo percurso: controle nativo → comando autorizado → aplicação no host → persistência → snapshot → efeito nos clientes.
4. Participantes não alteram opções pela interface, por atalhos ou por requisição direta. Não há gravação de `options.bcf` pelos caminhos online.
5. Janela aberta, reconexão e retomada convergem para o estado da sessão; projeção programática não dispara requisições. Pendências, erros, conflitos e `CLI_OVERRIDE` mantêm o comportamento definido na spec.
6. Testes exercitam o botão e os controles nativos reais, verificando efeito e autoridade. Após a implementação, executar os testes afetados e `python tools/package_client.py`; validar pelo `Brasfoot-Online.cmd` antes do encerramento.

Referências: [spec das configurações](../../especificacao/spec-configuracoes-online.md), [estado da implementação](../../implementacoes/implementacao-configuracoes-online.md), [entrega aceita das configurações](../../resolvidos/task-configuracoes-online.md). O relato atual confirma a alteração de velocidade após o rebuild; a divergência de Opções é acompanhada nesta task, que permanece aberta.
