# PN-07 — Empréstimos online

Data: 09/11/2026. Implementação integrada localmente na `main` após validação dos comandos do host e da retomada. Branch de origem: `impl/emprestimos-online-v2`.

## Entrega

- O painel original **Times** encaminha solicitações ao host. Empréstimo de clube IA usa `best.l.a(player,buyer)`; empréstimo de clube humano cria proposta pendente, sem mover o jogador antes do aceite do técnico de origem.
- A gestão da carreira permite disponibilizar e retirar atletas para empréstimo. O limite nativo de dez vagas/listagens é verificado pelo host.
- O técnico de origem aceita ou recusa propostas pela gestão. Jogador, clube, vínculo e disponibilidade são revalidados no aceite; propostas duplicadas para o mesmo jogador são rejeitadas e vencem na mudança de data.
- Técnico de origem ou destino pode pedir devolução. A operação usa o contrato nativo e preserva o registro se a origem não tiver vaga. O ciclo original do motor continua responsável por tentar a devolução ao vencer o prazo.
- Snapshots publicam disponibilidade, contratos ativos, clubes, prazo, atraso e indicação de devolução pendente. As propostas humanas são salvas no estado de gestão do host.
- Comandos usam autenticação, fase/revisão e participação dos técnicos afetados; alterações de elenco invalidam escalações e Pronto.

## Validação executada

- `python tools/build_adapter.py --test-transfer`: aprovado, incluindo consumidor nativo, projeção e proposta/retomada de estado de gestão.
- `python tools/tests/test_career_loans_http.py`: aprovado. Comando HTTP autenticado de listagem e restauração da disponibilidade em novo processo do host.
- `python tools/build_adapter.py --test`: aprovado antes da integração.

Os testes de motor usam carreira real e fixtures controladas para marcar disponibilidade e escolher resultados determinísticos. O teste HTTP verifica a listagem e save/restart; não automatiza a janela Swing do painel Times nem uma aceitação humana ponta a ponta entre dois clientes.

## Limites explícitos

Compra definitiva de atleta emprestado permanece indisponível: a especificação exige integrar o fluxo de saldo/origem antes de expor o consumidor nativo. Avisos de prazo para os clientes e validação executada de folha salarial, origem sem vaga, retorno por vencimento após restart e operação visual entre dois clientes seguem como cobertura adicional. A PN-07 está integrada como fluxo de empréstimo com proposta, aceite, devolução e retomada; esses limites não devem ser descritos como paridade total.
