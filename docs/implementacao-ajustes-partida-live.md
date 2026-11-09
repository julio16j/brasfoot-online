# PN-09 — validação atômica da escalação ao vivo

Status: recorte integrado à `main` em 09/11/2026. A task [ajustes durante a partida](task-ajustes-partida-online.md) permanece aberta para equipe reduzida, expulsões e recuperação.

Origem: recorte anteriormente desenvolvido no worktree `impl/ajustes-partida-live-v2`, refeito sobre a `main` atual para preservar papéis especiais, identidade de participantes e demais mudanças já integradas.

## Mudanças

`GameAdapter.liveLineup` valida o estado completo antes da primeira chamada ao motor: onze ativos, posições únicas, goleiro titular, limite e domínios táticos, identidade/uniqueness, banco atual autorizado, elegibilidade dos entrantes, reentrada, quantidade de trocas e pares goleiro/linha. Reservas finais devem corresponder ao banco atual menos quem entra mais quem sai. Todos os pares passam por `MatchPlayback.validateSubstitution`; a lista pré-validada é a mesma aplicada. A rota individual usa as mesmas pré-condições de playback e mantém a verificação de elegibilidade já existente.

O escopo mantém a interface em equipes com onze jogadores. Não implementa formação reduzida após expulsão, nem reposicionamento sem substituição. Regras de slots/tática e identidade podem precisar de ajuste quando a interface de equipe reduzida for definida.

## Validação

- `python tools/build_adapter.py --test`: aprovado. `LiveLineupAtomicityTest` compara estado nativo e revisão após rejeições, verifica elegibilidade/banco/rota individual e uma troca válida.
- `python tools/tests/test_live_lineup_atomicity.py`: aprovado contra `SessionHost` HTTP. Pedido de duas trocas com segundo entrante fora do banco retorna 400 sem alterar `round`, `game`, `playback` ou `checkpoint`; troca válida posterior é aplicada.
- `python tools/package_client.py`: concluído. `python tools/tests/test_live_lineup_atomicity.py --jar dist/brasfoot-online.jar` também passou usando o cliente empacotado. ZIP com CRC íntegro e sem harness Java de teste.

Hashes SHA-256:

- `dist/brasfoot-online.jar`: `9a8ffdb7f3c7dce738312fea9333fc08e7a7bf2e91bd3ab32b5b8e20bec89d6d`.
- `dist/host-ui-agent.jar`: `a42d4e4f2b62408895cff2b4bfd52d6366241daee849cd76c94e441bb00d66c0`.
- `dist/Brasfoot-Online-Teste.zip`: `742586b412136170ac020ae8e8abf57148e4adba03c94016cb1e8f5314f434b6`.

## Limites restantes

Não foram validados: partida com expulsão real e escalação de dez/nove; expulsão e substituição do goleiro; alteração clínica/disciplinar enquanto o editor está aberto; falha de persistência imediatamente após uma substituição; retomada/reconexão com o editor; experiência gráfica completa no JAR distribuído; aceitação humana. O teste HTTP cobre rejeição antes da mutação e um caminho de sucesso, não rollback se o checkpoint falhar depois da mutação.
