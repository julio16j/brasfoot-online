# Finanças nativas e crédito bancário

Prioridade: Sem prioridade. Definição do produto mínimo pendente; classificações antigas neste documento são históricas e não definem a fila atual.

Status: aberta complemento de PN-09. Inventário por inspeção de código em 08/11/2026, sem execução do jogo. Crédito bancário é distinto do empréstimo de jogadores de PN-07.

## Capacidade nativa local

O painel `a.cW` mostra lançamentos e dívida e oferece crédito/amortização (`analysis/decompiled/a/cW.java:65–77,151–165`). As ações chamam `clube.kL().m(clube)` para tomar e `.l(clube)` para quitar. No modelo `best.C`:

- Crédito soma 500.000 à dívida e ao saldo, se a dívida anterior for inferior ao limite da divisão. Limites no código: divisão 1 → 5.000.000; 2 → 3.000.000; 3 → 2.000.000; 4 → 1.500.000; demais → 1.000.000 (`best/C.java:222–231`).
- Amortização retira 500.000 do saldo e da dívida quando há dívida positiva e saldo de ao menos 500.000 (`:212–219`). Não presumir suporte a valor livre pela interface.
- Encargo recalculado em `eP()` corresponde a 3% da dívida positiva e é exposto por `eQ()` (`:234–240`). `best.a.o()` debita esse valor como categoria 4; o agendamento mensal usa ação `dJ` (`best/a.java:712–720`; `best/f_0.java:1951–1967`).

Essas são regras da versão local do jogo. Casos de saves com valores fora dos incrementos normais ainda exigem investigação antes de definir tratamento adicional. Não foi executado probe bancário nesta etapa.

## Estado online atual

O acesso financeiro original `a.eg.qT` abre `a.cW` ao clicar Iw/atalho nativo. `NativePreparation.disconnect` remove listeners de mouse do pacote `a.` e limpa mapas de teclado; esse acesso não é restaurado como ação online. O botão HW é Juniores, não Finanças.

`CareerViews.snapshot` publica saldo e folha; não transporta dívida/encargo nem o detalhamento contábil nativo. `CareerManagement` não contém ações bancárias; sua lista de operações termina em rejeição de ação desconhecida. Assim, saldo/folha presentes não comprovam paridade do painel financeiro ou crédito. As simulações de expansão do estádio não certificam esse fluxo.

## Requisitos a definir

Reutilizar o painel nativo e publicar os lançamentos/dívida do host. Crédito e amortização devem resolver o clube do técnico autenticado, respeitar fase/revisão e regras nativas, persistir e confirmar valores efetivos. Não permitir que listeners do painel movam dinheiro apenas na projeção.

Verificar atomicidade em falha de gravação: o rollback específico de treino não protege automaticamente uma operação financeira. Não confirmar crédito durável sem checkpoint coerente. Repetição de pedido não pode duplicar movimento; snapshot, reconexão e retomada devem manter saldo, dívida e encargo compatíveis. O processamento de juros deve continuar no calendário do host, sem segundo agendamento de cliente.

Critérios futuros: limites por divisão, saldo insuficiente, tomada/amortização, rejeição sem efeito parcial, encargos na fronteira mensal, dois clientes, revisão obsoleta, falha de armazenamento, save/retomada e comparação de cada lançamento entre painel e host. Enquanto sem integração, crédito e demais mutações devem permanecer explicitamente indisponíveis. Nenhum desses cenários foi executado nesta etapa estática; não há pedido para reativar implementação.
