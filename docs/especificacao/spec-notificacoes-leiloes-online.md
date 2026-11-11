# Avisos de gestão e leilões no online

Status: requisitos abertos de PN-05, baseados em inspeção estática de 08/11/2026. Nenhuma funcionalidade implementada nem jogo/teste executado nesta etapa. Complementa a [spec de configurações](spec-configuracoes-online.md); as opções continuam bloqueadas até integração efetiva.

## Avisos de contrato e mudanças de técnicos

O painel nativo `a.eg.qr` executa regras ao construir a preparação do clube (`analysis/decompiled/a/eg_0.java:305,443–481`): atualiza o cache de dias restantes, renova automaticamente vencidos quando a opção está ativa e cria mensagens de contrato. Modo 1 avisa vencidos apenas quando a renovação automática está desligada; modo 2 avisa com menos de 31 dias e usa `F.gQ/r` para não repetir o aviso de um mês. A regra não é simplesmente um modal mostrado por setter.

As mensagens são objetos `components.as`, com tipo/subtipo, textos, data e estado de leitura; o construtor as adiciona à lista `al.lQ()` do destinatário (`components/as.java:31–65`). O servidor hoje não projeta essa caixa postal nem possui ação de leitura. Criar um painel cliente a partir de objetos incompletos não deve gerar mensagens ou renovar contratos por conta própria. `CareerEngine.prepare` cobre a renovação automática no host, mas não a geração/distribuição dos avisos.

Mudanças de técnicos: `best.f.n(int)` obtém eventos das competições e, sob as guardas `bs`, `!bD()` e `getVerMudancaTecnicos()==1`, encaminha a lista ao navegador `best.S.K` (`best/f_0.java:1734–1761`). `HostUiAgent` torna inertes os métodos de best/S; capturar os dados antes do navegador é necessário para apresentar os eventos no online. A opção não deve impedir as mudanças esportivas realizadas anteriormente pelo motor.

Contrato: avaliar no host no limite nativo correto, identificar mensagem/evento e técnico destinatário, persistir o estado necessário e publicar somente a caixa autorizada. Definir se leitura é por técnico ou por conexão, sem repetir notificações ao reconstruir uma tela. O estado nativo de leitura não prova que exista confirmação online; criar um protocolo explícito com revisão/identidade. Preservar os filtros e o comportamento combinado de renovação e aviso.

### Caixa postal também contém decisões

O acesso ze → `a.ek` → `eg.rm` abre `a.bn` com a lista do técnico. `NativePreparation.disconnect` remove o mouse listener nativo de ze e não reinstala esse acesso; não há substituto de caixa postal na gestão online atual (`a/eg_0.java:1009,1646–1658`; `src/brasfoot/NativePreparation.java`).

No painel bn, selecionar mensagem marca `as.aF(true)`; mensagens com vr=true exibem decisões. Em cQ(1), a mensagem perde a pendência e, para vt=1/valor positivo, o clube recebe lançamento `v(valor,9)` (“Multado!”), podendo gerar outra mensagem por nQ; cQ(2) encerra a pendência sem essa multa (`a/bn_0.java:86–112,140–166`). Logo, transportar texto e estado de leitura não integra todas as funções dessa caixa. O comportamento de crédito foi apenas rastreado na fonte, sem execução econômica nova.

Definir leitura e resposta como ações distintas, com identidade estável da mensagem, autorização pelo técnico e confirmação autoritativa. A decisão deve se consumir uma vez; duas conexões ou repetição de requisição não podem aplicar a multa nem sortear consequência novamente. Preservar lançamento, estado resolvido e mensagens resultantes no mesmo checkpoint. Não executar os handlers mutáveis de bn apenas na projeção cliente.

## Leilão tem efeito econômico e participação

O fluxo nativo `a.hk.nx` só gera a lista de leilões quando `isVerLeiloes()` está ativo, existe KS e o tipo é 1, 3 ou 10 (`a/hk_0.java:277–304`). Sorteia até três candidatos `best.N`, armazena a lista em `a.dz.X` e pode encaminhar ao navegador antes de avançar. Essa origem é de interface: o avanço online pelo motor não demonstra sua execução.

`best.N` seleciona jogador de clube IA com pelo menos 21 atletas e busca comprador IA. A janela `a.dz` reúne clubes humanos com saldo positivo e menos de 35 jogadores, define mínimo em 50% do valor do atleta e processa lances em sequência; a resolução pode escolher comprador humano ou IA e chama `F.a` para transferir (`best/N.java:21–44`; `a/dz_0.java:110–159,201–289`). Portanto, “ver leilão” nesse caminho não é mera consulta passiva de uma transação previamente concluída. Não afirmar que a economia do leilão roda independentemente do controle: isso não se sustenta no caminho rastreado.

No online atual não há comandos de lance/desistência, estado de disputa, destinatários/turnos ou persistência da lista gerada. Habilitar o setter ou abrir o diálogo cliente executaria decisões locais incompatíveis com a autoridade do host. Uma lista obtida por consulta também não substitui uma etapa de disputa.

Contrato proposto: geração única no host no limite correspondente; IDs da disputa, atleta, clubes e turno; lance e desistência autenticados; validação de saldo/vaga e oferta atual; resolução e transação únicas; propagação da decisão. Reutilizar a apresentação nativa com callbacks de rede. Preservar regras de mínimo/resposta IA com confirmação dos consumidores; não corrigir incidentalmente peculiaridades da lógica nativa sem decisão de requisito.

Decisões abertas de multiplayer: como avançar entre participantes, o que fazer com desconexão e ausência de lance, como tratar um clube que ficou sem vaga/saldo e quando liberar a próxima data. Não introduzir prazo automático como se fosse regra nativa; qualquer prazo precisa ser adaptação explícita. A recuperação deve distinguir leilão em andamento de venda persistida, sem sorteio ou débito repetidos.

## Aceite futuro

- Avisos: modos 0/1/2; exatamente 31/30/0 dias; combinação com auto-renovação; uma mensagem no destinatário correto, leitura e reconexão; nenhum efeito duplicado por reconstrução visual.
- Técnicos: mudança real capturada sem abrir navegador local no host, visibilidade e identificação corretas, desativação da apresentação sem impedir mudança do motor.
- Leilões: geração, candidatos elegíveis, mínimo, lances/desistência, concorrência, comprador IA/humano/ausente, débito/crédito/elenco/contrato/histórico; falha/repetição/reconexão e save/retomada durante cada etapa.
- Manter os três controles indisponíveis enquanto os consumidores não estiverem integrados; evidência do setter não encerra PN-05.

Nenhum critério foi executado nesta etapa. Relacionados: [gestão](../validacao-gestao-nativa.md), [finanças](../tasks/sem-prioridade/task-financas-nativas-online.md), [renovação manual](../tasks/sem-prioridade/task-renovacao-contratos-online.md) e [backlog](../tasks/sem-prioridade/task-paridade-nativa.md).
