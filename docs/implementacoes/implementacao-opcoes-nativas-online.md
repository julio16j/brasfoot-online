# Implementação de Opções nativas online

Revisão: 7 de novembro de 2026. Status: implementada; aguardando validação do usuário pelo launcher. Referências: [task](../tasks/sem-prioridade/task-opcoes-nativas-online.md) e [spec](../especificacao/spec-opcoes-nativas-online.md).

## Entregue

O botão adicional **Configurações** foi removido. O botão nativo **Opções** continua abrindo o popup original; seu item **Preferências** agora encaminha ao controlador online, sem chamar a janela offline. F3 abre a mesma função, inclusive durante partidas ou sem o painel de preparação visível. Uma janela já aberta é trazida à frente.

`NativePreparation` intercepta o item Preferências por seu rótulo nativo, exige uma única ocorrência e retira o acelerador local para não duplicar o binding de F3 na janela principal. Itens locais de salvamento, demissão e saída foram desabilitados nesse popup para impedir execução sobre a projeção do cliente. Sobre o Brasfoot conserva a ação original; fechar a sessão continua disponível pela janela online.

`NativeSessionOptions` conserva o painel `a.fB` e sua construção sem efeitos iniciais de gravação. A janela usa o formato nativo modal e sem decoração, com o botão original de fechar e Escape. Abrir Preferências não altera o estado do relógio do host. A modalidade usa o ciclo de eventos Swing, permitindo a aplicação das respostas e snapshots.

Os dez controles já suportados usam o transporte existente de `sendOptions`: revisão, validação, persistência e confirmação autoritativa. Participantes não enviam alterações, inclusive por mudanças programáticas em controles desabilitados. As doze opções ainda indisponíveis não foram habilitadas por esta entrega.

O editor `NativeOptionsPalette` usa o painel nativo `a.aX` (identificado como `ax_0` pelo descompilador), com cinco amostras e restauração do padrão original. A confirmação offline foi substituída por um único comando `playerListPalette`; o rascunho não altera o estado nativo nem grava `options.bcf`. Fechar ou cancelar descarta a edição. Mudança da revisão invalida o rascunho, exigindo reabertura em vez de reenviar escolhas antigas com uma revisão nova.

Pedidos em andamento bloqueiam edição e usam o estado confirmado para restaurar a tela em falhas. Fechar o diálogo não cancela um comando já enviado. Respostas de outra conexão são descartadas. Atualizações programáticas não disparam novos comandos.

## Componentes

| Arquivo | Responsabilidade |
| --- | --- |
| `src/brasfoot/NativePreparation.java` | Botão e popup originais, item Preferências encaminhado ao cliente e bloqueio de ações offline |
| `src/brasfoot/NativeClient.java` | Controlador único, F3, remoção do botão adicional, transporte e proteção contra respostas de outra conexão |
| `src/brasfoot/NativeSessionOptions.java` | Painel nativo, permissões, atualização sem callbacks e abertura do editor de paleta |
| `src/brasfoot/NativeOptionsPalette.java` | Editor nativo com rascunho, cancelamento, padrão e conflito de revisão |
| `tests/brasfoot/NativeSessionOptionsTest.java` | Percurso real do botão, dez bindings, permissões, paleta e ausência de gravação local |
| `tools/tests/test_native_create.py` | Teste de F3 e janela única nos clientes gráficos; opção `--jar` para executar o artefato distribuído |

## Validação

- `python tools/build_adapter.py --test`: aprovado, incluindo coordenação, elencos grandes e transferências.
- `python tools/tests/test_session_options.py`: aprovado; percorre o botão nativo e Preferências, exercita os dez bindings, bloqueio do participante, sobrescrita CLI, edição pendente, paleta, cancelamento, padrão, rascunho desatualizado e comparação de `options.bcf` antes/depois. Inclui testes HTTP de autorização, revisão, persistência, intervalo e recuperação.
- `python tools/tests/test_career_session.py`: aprovado com quatro clientes, duas rodadas, barreiras, retomada e recuperação de interrupção.
- `python tools/package_client.py`: concluído; `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip` atualizados.
- `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar`: aprovado no JAR distribuído, com criação do host gráfico, abertura por F3, janela única, ausência do botão adicional e conclusão da partida com dois clientes.

O teste do artefato exercitou a mesma entrada `java -jar dist/brasfoot-online.jar` usada pelo launcher. A confirmação final do usuário permanece pendente; por isso a task continua fora de `resolvidos/`.

## Limites

O defeito de áudio repetido permanece na [task de áudio](../tasks/sem-prioridade/task-audio-eventos-repetidos.md). A implementação dos consumidores das opções ainda bloqueadas permanece no [registro das configurações](implementacao-configuracoes-online.md). Nenhuma dessas pendências é encerrada pela troca de acesso à tela.
