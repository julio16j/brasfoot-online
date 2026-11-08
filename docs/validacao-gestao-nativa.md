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
| Treino | Consumidor, agenda semanal, precedência e Kryo testados na [spec](especificacao/spec-treino-auxiliar-online.md) | Comando do auxiliar, dois clientes, fronteira semanal por avanço normal e erro de gravação |
| Base | Divergência de projeção executada na [task](task-juniores-nativos-online.md) | Tela nativa roteada ao host; promoção/dispensa/apelido e atualização consistente |
| Renovação, lista, venda e emprego | Fontes presentes, sem execução individual nova até este checkpoint | Regras/efeito, revisão e rejeição sem mutação; save/retomada e tela do pacote |
| Empréstimos | Consumidor e Kryo testados na [spec](especificacao/spec-emprestimos-online.md) | Protocolo, consentimento humano, salários, vencimento normal, interface e avisos |

Manter PN-09 aberta. O teste reduziu a incerteza do modelo de obra; não autoriza declarar a gestão inteira validada nem encerrar relatos gráficos.
