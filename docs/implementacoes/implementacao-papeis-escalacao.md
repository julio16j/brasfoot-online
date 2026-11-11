# Implementação PN-06 — papéis especiais na escalação

Revisão integrada na `main` em 09/11/2026. Requisitos: [spec PN-06](../especificacao/spec-papeis-escalacao-online.md). A tarefa continua aberta para efeitos esportivos dinâmicos, fase ao vivo e fluxos complexos de mudança de elenco.

## O que a main entrega

O editor original permite escolher batedor de faltas, capitão e batedor de escanteios durante a preparação. O falso 9 permanece desabilitado online. O formulário transmite IDs de jogador; o host valida elenco, elegibilidade, titularidade para novos papéis, revisão e fase antes de mutar escalação, tática ou papéis. Papéis preexistentes podem permanecer com o jogador no banco. A projeção visual reata as referências por ID.

## Evidências executadas

- `python tools/build_adapter.py --test`: passou; `SpecialRolesProjectionTest` cobre IDs, rejeição atômica, falsa alteração de falso 9, papel preservado no banco e reconstrução visual.
- `SpecialRolesWindowTest` com classes locais e com `dist/brasfoot-online.jar` à frente do classpath: passou; editor original, três ações habilitadas, falso 9 bloqueado e IDs submetidos.
- `python tools/tests/test_special_roles.py`: passou no HTTP; rejeição inválida 400, revisão antiga 409, estado inalterado e IDs confirmados.
- `python tools/tests/test_special_roles.py --jar dist/brasfoot-online.jar`: passou incluindo reinício do host com retomada e papéis preservados.
- ZIP: `testzip=None`, 22.735 entradas, sem harnesses de teste.

JAR: SHA-256 `4410E7F84BFAF1486C9D3C1BAD7708D3CEC9F350BA9F1A01F48B26A72424795B`. ZIP: SHA-256 `F9F50FC8C32BDD2E0F4F9C03AC190B9AE1CF12399833068015E8A5C962CB7ED1`.

## Limites

Os eventos individuais que consomem cada papel não foram forçados durante a partida; o vínculo do capitão e falso 9 com efeitos esportivos dinâmicos requer investigação/aceite adicional. Alterações de papel durante a partida permanecem somente leitura. Esta integração registra apenas o recorte de preparação validado; nenhum push foi feito.
