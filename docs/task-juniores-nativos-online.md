# Juniores nativos sem projeção autoritativa

Status: aberta, P1 proposta; 08/11/2026. Origem PN-08 e auditoria dos callbacks restaurados PN-11. Não há implementação nesta investigação.

## Diagnóstico e execução

`NativePreparation.java:27–40` preserva o listener do botão `HW`, que no nativo tem rótulo Juniores (`analysis/decompiled/a/eg_0.java:1993`). O listener original chama `qY()`, que abre `a.ds` com o clube da projeção (`a/eg_0.java:1264–1284`; nome da classe decompilada `ds_0`). Não confundir HW com o acesso financeiro Iw, cujo listener de mouse é removido por `disconnect`.

`VisualModel.updateClubs` carrega o clube do arquivo de recursos e reconstitui apenas `kc()` com os jogadores profissionais dos snapshots (`VisualModel.java:59–83`). Não substitui `ky()` pela base do host. Já `CareerManagement.snapshot` publica os juniores autoritativos no campo `youth` (`CareerManagement.java:163–168`). Ter essa consulta no protocolo não integra o botão nativo.

Probe independente temporário `AuditYouth.java`, em `C:/Users/Julio/AppData/Local/Temp/brasfoot-audit-training-6e_ixo3p`, criou carreira descartável do Corinthians e introduziu um junior identificável somente em memória. Resultado: base original do host com 7, depois 8 com marcador; snapshot de gestão com 8; clube de `VisualModel` com 1; marcador ausente. Log `result-youth.txt`, processo Java encerrado com código 0. A tentativa inicial com Fixture foi rejeitada corretamente porque gestão exige carreira; foi substituída por criação real de carreira, sem alterar código ou saves existentes.

Isso confirma por execução a divergência da projeção de juniores. O processamento foi headless; não houve clique gráfico nessa tela. Na decompilação, `a.ds.pN` promove via `best.K.a`, `pM` dispensa removendo do clube/estado local e `pP` abre edição de apelido (`a/ds_0.java:185–245`), sem envio a `/management`. O roteamento local dessas ações é confirmado por inspeção; o resultado do clique na sessão do usuário ainda não foi reproduzido. A existência de comando `promote` no host não corrige esse acesso independente.

## Impacto e correção requerida

A academia nativa pode listar jogadores do recurso inicial em vez da carreira e mantém caminhos de edição locais. Integrar a tela nativa à base e aos comandos do técnico no host, ou bloquear claramente os controles sem suporte enquanto essa integração não existir. Não substituir a conclusão por apenas interceptar o botão de promoção: dispensa, apelido, seleção e eventual compra da base de outros clubes também precisam de classificação.

Critérios: identidade estável dos juniores; lista nativa igual à consulta autoritativa após criação, promoção e retomada; seleção por ID após atualização; promoção pelo host com limites/elegibilidade e persistência; dispensa/apelido integrados ou explicitamente indisponíveis; rejeições restauram estado confirmado; nenhuma mutação de confirmação somente local; teste com dois clientes e janela real; testar pacote combinado após integração. Reutilizar PN-08 para o uso direto de juniores na escalação, que é capacidade diferente.

Dependências: contrato de gestão, revisão por técnico, projeção completa da base e acesso nativo. Task aberta até implementação e validação pertinente; mudanças em worktree separada não significam integração à principal.
