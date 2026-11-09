# PN-08 — Academia nativa

Implementação parcial integrada na `main`. Branch de origem: `impl/juniores-nativos-v2`. O escopo integrado cobre a projeção autoritativa dos juniores no painel nativo e a promoção pelo comando do host.

## Implementação

- `CareerManagement` publica IDs estáveis e atributos nativos de jogadores da base; `GameAdapter` projeta a academia completa do host.
- `VisualModel` atualiza a lista por identidade estável e identidade canônica de competição, sem reconstruir a base a partir de recursos locais.
- A academia nativa preserva sua tabela e os campos originais. Promoção usa `/management promote`, com persistência e rollback quando o checkpoint falha.
- Dispensar, comprar base externa e definir apelido ficam indisponíveis na janela nativa. Uso direto de juniores na escalação segue fora deste recorte.

## Validações

- `python tools/build_adapter.py --test`: aprovado com `YouthProjectionTest`, PN-01, PN-02, PN-07 e teste de elenco grande.
- `python tools/tests/test_youth_session.py`: aprovado contra o JAR empacotado. Promoção pelo painel real, checkpoint, projeção no segundo cliente e retomada em processo novo preservaram o ID no elenco profissional e o removeram da base.
- `python tools/package_client.py`: pacote combinado PN-02/07/08 reconstruído.
- `NativeYouthWindowTest` capturou a janela e confirmou os controles desativados, mas falhou ao localizar a caixa de confirmação após clique Robot. O roteiro de cliente real acima concluiu a promoção pela interface. A falha isolada do harness não foi atribuída ao produto.

JAR SHA-256: `3ac92c4f75de2ebb2fadfdc88e8eb48651dc42650d99d0e8aa4f9822f9e1aa51`. ZIP: 80.157.343 bytes, SHA-256 `79697fe3315e09b50cbf812dd773403d2dc49eb86d485589feee72e51853b4e4`.

## Limites

Não há teste de falha de gravação injetada pela rota HTTP, atualização dinâmica de uma academia já aberta ou conflito de promoção com editor/Pronto. A janela usa o snapshot recebido ao abrir. A tarefa permanece parcial; não declarar paridade completa da academia nem suporte a uso direto de juniores na escalação.
