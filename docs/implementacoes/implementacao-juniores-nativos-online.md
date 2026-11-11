# PN-08 — Academia nativa

Implementacao concluida para o problema especifico de projecao/promo??o na `main`. O painel usa a base autoritativa do host, promove pelo comando `/management promote`, atualiza janelas abertas e preserva o rascunho do editor durante mudancas concorrentes. Dispensar, apelidar e comprar base externa continuam desativados; uso de juniores na escalacao e outro escopo.

## Validação executada

- `python tools/build_adapter.py --test`: aprovado com `YouthProjectionTest` e as suítes PN-01/02/07 e elenco grande.
- `python tools/tests/test_youth_transactions.py`: aprovado. Pronto impede a promoção sem alterar elenco/checkpoint; uma falha HTTP injetada na gravação retorna 503, restaura o júnior às listas e preserva o manifesto anterior.
- `NativeYouthWindowTest`: aprovado. A janela aberta atualiza contagem e jogadores após a promoção, mantém uma seleção válida e desativa Promover quando o elenco profissional chega ao limite de 35.
- `python tools/tests/test_youth_session.py`: aprovado com cliente empacotado, projeção no segundo cliente e retomada em outro processo.
- Pacote reconstruído e validado: `python tools/tests/test_youth_session.py`, `test_youth_transactions.py`, `test_career_loans_http.py --packaged` e `test_career_country_selection.py --jar dist/brasfoot-online.jar` aprovados.

JAR SHA-256: `bcf94b7939651ba8854d98ad961fe26f36ff67c66d33ad6728c38d62eacf17cd`. ZIP: 80.159.743 bytes, SHA-256 `5794efc0e8871ffe3dedd51cb048d155b585c8d35ed0f41183248cba7215e5d6`.

## Limites

Limite: a implementacao cobre a academia e o fluxo de promocao descritos nesta task. O uso direto de juniores na escalacao permanece em requisito separado.
