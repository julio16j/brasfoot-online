# Integração das entregas de 08/11/2026

Status: conjunto combinado revisado e aprovado nas verificações abaixo, preparado para publicação na `main`. Base anterior: `f3200ada55db25b80f80fb0f48e049bc2e6e82ba`. Os dois agentes concluíram o checkpoint e estão parados por instrução do usuário.

## Escopo

- Preservar e incluir o trabalho local de Opções/Preferências/F3 e paleta nativa.
- Integrar a coerência dos três acessos de escalação em folgas, com indicação de próximo confronto e escopo dos jogos exibidos (`impl/escalacao-folga`). Automação de folgas e planejamento antecipado continuam pendentes.
- Integrar auxiliar online, foco manual independente, confirmação pelo host, persistência e tratamento de erro (`impl/treino-auxiliar-online`). Essa implementação substitui o bloqueio provisório de `impl/treino-auxiliar-indisponivel` e conserva seu ajuste de dimensão da janela.
- Preservar os diagnósticos e contratos produzidos pelo agente de requisitos, sem declarar implementadas as demais specs.

Os diffs de código foram combinados por merge de três versões, preservando o estado local. Não foram copiados os ZIPs isolados das branches: o pacote principal foi reconstruído a partir do conjunto. Snapshot de segurança do estado anterior em `analysis/integration-backup-20261108-135345/`, com patch binário e arquivos não rastreados.

## Revisão

A revisão inicial encontrou corte na janela do treino, corrigido por dimensionamento nativo. A revisão independente do auxiliar encontrou confirmação visual desatualizada com editor de escalação aberto. O ajuste atualiza somente os campos de treino quando a projeção completa fica suspensa pelo editor, preservando o rascunho de elenco. Evidências das branches nos registros e revisões vinculados abaixo; a validação integrada é registrada separadamente.

## Validação integrada

- `python tools/build_adapter.py --test`: passou no primeiro conjunto combinado.
- `TrainingEngineTest`: oito combinações nativas, quatro datas, duas fronteiras semanais, ganhos internos e retomada em outra JVM sem repetição passaram.
- `python tools/tests/test_session.py`: passou; autenticação, revisões, barreira, rejeição após início, placar compartilhado e save final.
- `python tools/tests/test_session_options.py`: passou antes da combinação e novamente no conjunto final; autoridade, persistência, erro de gravação, convergência, opções e painel nativo.
- `TrainingStateTest` sobre o JAR combinado: passou; pendência, restauração, projeção, fase e troca de clube.
- `NativeRestDayWindowTest` sobre o JAR combinado: passou; janela real, folga com/sem próximo, três acessos, editor e Pronto/retirada projetados.
- `python tools/tests/test_training_session.py --packaged`: passou; clientes reais, cliques Robot, editor aberto com rascunho preservado, confirmação/erro, API, autoridade, fases, falha de gravação e retomada em outra JVM.
- `python tools/tests/test_native_create.py --jar dist/brasfoot-online.jar`: passou; criação pelo JAR, dois clientes, F3, ausência do botão adicional e resultado salvo.
- `python tools/tests/test_career_session.py`: passou; quatro clientes, duas rodadas, barreira do quarto técnico, resultados compartilhados, save/retomada e recuperação de interrupção.
- Capturas `analysis/validation-training-session-jar/host-window/training-confirmed.png` e `analysis/integration-validation/rest-day/rest-next.png` inspecionadas pelo agente principal: controles do treino integralmente visíveis, explicação de folga e três acessos bloqueados. A captura também preserva o bug preexistente PN-12, fora deste lote.
- `git diff --check`, links relativos dos documentos, CRC do ZIP e igualdade do JAR/agente internos com os arquivos distribuídos: passaram.

Logs da execução integrada em `analysis/integration-validation/`. Os testes usam sessões descartáveis; não foram encerrados processos do usuário nem usados seus saves. As transições Pronto/retirada do teste de folgas são fixtures projetadas; não alegam reprodução do save original reportado.

## Artefatos combinados

`python tools/package_client.py` recompilou e atualizou `dist/brasfoot-online.jar`, `dist/host-ui-agent.jar` e `dist/Brasfoot-Online-Teste.zip` na pasta principal. O ZIP tem 80.127.030 bytes. Os JARs locais são ignorados pelo Git; o ZIP rastreado contém os artefatos combinados.

- JAR SHA256: `02f328fb24ba0f59f43da46b5b3f32373facfb931ec9b21d9ff95dc71c5cdf87`.
- ZIP SHA256: `cf1230c647e7fd0a466fdef26c2b0140deeb7cd5c1718c1179047f44761548d6`.

Uma instância do jogo já aberta deve ser fechada e reaberta pelo launcher para carregar o JAR atualizado.

## Documentos de origem

- [Entrega de folgas](implementacoes/implementacao-escalacao-folga.md).
- [Entrega do auxiliar](implementacoes/implementacao-treino-auxiliar-completo.md).
- [Opções nativas](implementacoes/implementacao-opcoes-nativas-online.md).
- [Revisão da folga](revisao-independente-folga.md).
- [Revisão do auxiliar](revisao-independente-auxiliar-online.md).
- [Backlog de paridade](tasks/sem-prioridade/task-paridade-nativa.md).

## Limites preservados

PN-12 (lesões fictícias na projeção), juniores, áudio e demais lacunas do backlog não foram implementados neste lote. A worktree reservada para lesões não faz parte da entrega. Não há declaração de paridade completa. Os agentes devem permanecer parados após o checkpoint solicitado pelo usuário, até nova instrução.
