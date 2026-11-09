# Validação independente da gestão nativa

Revisão em 08/11/2026 pelo agente de requisitos. Referência: PN-09 do [backlog](task-paridade-nativa.md). Testes isolados de modelo/comando direto, sem modificar implementação nem pacotes. Não certificam interface ou contrato HTTP.

## Expansão do estádio

`AuditStadium.java` e `result-stadium.txt`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`, criaram carreira descartável do Corinthians, com saldo controlado de teste em memória. A ação `stadium-expand` foi executada em `CareerManagement` para 500 lugares na primeira seção.

Passaram: orçamento nativo positivo e débito do valor exato; um projeto na fila; segunda obra rejeitada sem novo débito; save e leitura Kryo com referência do projeto ao estádio restaurado; consumidor nativo `best.f.aw()` executado por reflexão após o prazo removeu o projeto e adicionou exatamente 500 lugares; repetir consumidor não adicionou lugares novamente. A primeira tentativa do probe tentou ler um campo inexistente (`dh`); foi corrigida para o getter real `dT()`, sem alteração de produção.

Origem da conclusão: `CareerManagement.java:109–116`, `NativeStadium.quote`, `best/B.java:33–41` e `best/f_0.java:1613–1632`. O consumidor compara prazo estritamente anterior à data corrente. Assim, não exigir conclusão antes ou exatamente no instante do prazo; o avanço nativo precisa ultrapassá-lo. A execução direta do consumidor não demonstra sozinha que toda carreira/país atinge o callback corretamente pelo fluxo HTTP.

## Matriz restante

| Capacidade | Evidência nova | Ainda necessário |
| --- | --- | --- |
| Estádio: obra | Débito, duplicação, identidade Kryo e conclusão pelo consumidor aprovados | Janela real → host; saldo insuficiente; autorização/revisão; falha de armazenamento; avanço normal até conclusão; snapshot/reconexão |
| Estádio: preços | Caminho estático para `stadium-settings` | Valores sugeridos/manuais, limites atômicos e receita real de jogo; persistência e interface |
| Treino | Entrega integrada; comando, projeção, precedência, ciclo semanal e recuperação constam do [registro atual](implementacao-treino-auxiliar-completo.md) | Respeitar os limites de cobertura e aceite explícitos desse registro; a ausência de comando descrita no checkpoint inicial foi superada |
| Base | Divergência de projeção executada na [task](task-juniores-nativos-online.md) | Tela nativa roteada ao host; promoção/dispensa/apelido e atualização consistente |
| Renovação, lista, venda e emprego | Fontes presentes, sem execução individual nova até este checkpoint | Regras/efeito, revisão e rejeição sem mutação; save/retomada e tela do pacote |
| Empréstimos | Consumidor e Kryo testados na [spec](especificacao/spec-emprestimos-online.md) | Protocolo, consentimento humano, salários, vencimento normal, interface e avisos |

Manter PN-09 aberta. O teste reduziu a incerteza do modelo de obra; não autoriza declarar a gestão inteira validada nem encerrar relatos gráficos.

Continuação somente por código: [seleção na gestão](task-selecao-gestao-online.md) rastreia conversão duplicada de linha, ID contextual não aplicado ao formulário e retirada iniciada com forSale=true. [Preços do estádio](task-precos-estadio-online.md) distingue o botão nativo de sugestões da confirmação ao fechar e documenta o rascunho sujeito a snapshots. [Índices de mercado](task-indices-mercado-online.md) cobre candidatos, lista/preço pedido e seleção ordenada; [habilidades individuais](task-habilidades-individuais-online.md) cobre modo e atributos omitidos. São diagnósticos estáticos; nenhum cenário de UI novo executado.

Complementos estáticos posteriores, sem execução do jogo: [finanças/crédito bancário](task-financas-nativas-online.md) ainda não têm painel/protocolo integrado; [compra direta de listado](task-compra-listados-online.md) pula a recusa nativa do atleta no ramo IA/preço. Nenhum teste anterior de regras auxiliares de negociação certifica essas duas capacidades. Renovação manual também requer atenção ao contrato: `F.fR()` nunca retorna negativo, logo o `fR()<0` do online é sempre falso; a tela nativa `a.fy` igualmente usa `F.a(dias,false)` e estende o prazo anterior. Isso não foi classificado como bug novo sem decisão/reprodução do caso vencido: a regra nativa de extensão precisa ser distinguida da renovação automática a partir da data atual.
