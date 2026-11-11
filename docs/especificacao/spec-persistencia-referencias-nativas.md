# Referências nativas na gravação e retomada

Status: requisitos abertos; implementação parcial integrada em `a8d399b5` (10/11/2026). Origem: [task e diagnóstico](../tasks/sem-prioridade/task-persistencia-referencias-nativas.md). O implementador registra round-trip em JVMs novas; esta revisão não repetiu o teste. A spec complementa disponibilidade, consultas históricas e identidade dos dados usados por calendário/Procura.

## Objetivo

Após salvar e retomar em outra JVM, preservar os vínculos de registros disciplinares com a competição correta e de transferências históricas com o atleta correto, sem alterar identidades usadas por comandos, ofertas, calendário ou checkpoints. O host restaura esses vínculos antes de aceitar participantes/comandos ou publicar consultas.

## Referências e identidades

| Relação | Contrato nativo | Requisito online |
| --- | --- | --- |
| best.H → best.at | Y transitório; ft persistente; H.gY prepara o ID e H.m reconecta | Registrar identidade de competição e reconstruir a referência canônica usada pelas partidas e por F.h(at) |
| best.ap → best.F | U transitório; ei/ej persistentes; mb prepara identidade/tipo e me reconecta | Manter referência ao mesmo atleta, distinguindo profissional/junior sem depender da ordem atual das listas |

O fluxo nativo usa Ag/Af e Ai/Aj. São referência de comportamento, não receita para copiá-los integralmente: Ai renumera jogadores e Ag/Af numeram competições pela ordem de enumeração. IDs já expostos no protocolo não podem mudar por salvar, pesquisar, ordenar, promover ou retirar alguém de uma lista.

Se a implementação separar ID público e índice interno de serialização, persistir uma relação explícita e sem ambiguidades. Mesmo nome, país, posição, clube ou divisão não identificam um jogador/competição. Competições homônimas e temporadas distintas não podem compartilhar punições por coincidência de texto.

## Gravação

1. Preparar os registros a partir do estado autoritativo consistente, sob a mesma exclusão usada pelo checkpoint; não permitir que uma transferência/avanço intercale a preparação e a escrita.
2. Persistir identidades suficientes de cada referência abrangida. Não habilitar serialização global de campos transient como solução genérica; caches, componentes e motor de execução têm outro ciclo de vida.
3. Preservar IDs públicos, vínculos existentes, contagens de cartões, punição, data/valor e clubes de origem/destino do histórico. Preparar um save não pode mudar a elegibilidade nem a identidade de uma oferta pendente na sessão em execução.
   Preservar também os contadores e avaliações de H usados por rankings por competição; reconectar somente o bloqueio disciplinar não cobre os demais consumidores do mesmo registro.
4. Manter o mecanismo existente de arquivo temporário e publicação atômica do manifesto. Só anunciar checkpoint confirmado depois de publicar save e metadados coerentes; falhas mantêm o tratamento de storageFailed, sem apontar o manifesto para um arquivo incompleto.

## Carregamento e reconexão

Ler e validar o conjunto salvo antes de disponibilizá-lo para uso. Reconectar H à mesma instância canônica de competição usada pelas partidas correspondentes; F.h(at) compara referências. Não criar uma competição vazia apenas com o nome para satisfazer o vínculo.

Reconectar ap ao atleta correspondente no mundo restaurado. Profissionais/juniores e mudanças de categoria exigem identidade que não aponte para outro objeto com o mesmo índice. Uma transferência posterior não deve alterar retrospectivamente o atleta de registros anteriores. Definir representação histórica explícita para aposentado/removido quando o mundo já não mantém seu objeto; não fabricar um atleta ativo para preencher a consulta.

Comparar aposentadoria/remoção ao comportamento nativo específico antes de diagnosticar perda na persistência. O fluxo F.a(String,int,boolean) limpa registros gr/gH e contadores por decisão do motor; essa limpeza não deve ser atribuída ao serializer nem revertida silenciosamente pela reconexão. Caso a navegação histórica nativa já deixe de resolver um atleta removido, documentar esse limite e separar qualquer ampliação de retenção de uma correção de paridade.

Restabelecer os globais exigidos pelos getters nativos na ordem necessária, mas não servir HTTP nem executar rodada em estado parcialmente restaurado. Validar ausência/duplicidade de identidades e referências incompatíveis. A reconexão só é íntegra se cada H (disciplina e avaliações por competição) resolver para a competição canônica esperada; H com ID ausente ou sem correspondência na agenda não pode ser contado como restaurado só porque o carregamento terminou. Definir se rejeita o save, expõe aviso ou preserva um histórico destacado, com contexto; não eliminar registros silenciosamente nem suspender atleta arbitrário.

Não reconstruir todos os campos transitórios indiscriminadamente: F.fg/al.fg têm resolução por ID em seus próprios consumidores; caches de avaliação e estado de minuto precisam de análise separada. Esta spec não certifica todas as relações do grafo Kryo.

## Compatibilidade e recuperação

- Saves nativos com índices preparados devem ser reconectados respeitando seu formato; preservar o arquivo original durante migração.
- Saves online anteriores podem conter H.ft/ap.ei sem identidade utilizável. Não é possível prometer recuperação exata apenas pelo nome ou pela posição na lista. Identificar registros irrecuperáveis e definir aviso/limitação de recuperação antes de declarar retomada íntegra.
- Manifesto e save devem descrever o mesmo checkpoint. Uma retomada de partida interrompida deve usar também o conjunto de referências e ofertas da preparação escolhida, conforme o mecanismo de recuperação existente.
- Reconectar deve ser idempotente: repetir carregamento e salvar novamente não duplica H/ap nem muda jogador/competição.
- Esta etapa não modifica automaticamente saves do usuário nem regenera temporadas para preencher vínculos perdidos.

## Aceite observável

| Cenário | Verificação exigida |
| --- | --- |
| Disciplina em duas competições | Mesmo atleta suspenso em uma e permitido na outra; H, cartões e bloqueio iguais antes/depois; referências das partidas corretas |
| Repetição e nomes iguais | Duas retomadas em novas JVMs e competições homônimas; nenhum registro novo criado por lookup nem punição transferida de contexto |
| Avaliações por competição | Mesmo conjunto de candidatos/avaliações no ranking nativo antes/depois, reconstruindo o cache sem perder os registros H de origem |
| Fronteira do ranking por competição | 0–3 candidatos não falham; exatamente 4 são listados sem acesso fora do intervalo; 5+ mantêm o limite visual de cinco |
| Histórico | Transferência registrada, atleta vendido outra vez, save/retomada; registro original mantém atleta, data, valor e clubes corretos na consulta nativa |
| Identidade online | IDs de todos os atletas/competições envolvidos iguais antes/depois de salvar; oferta pendente e comando posterior ainda apontam para os objetos corretos |
| Profissional/junior | Promoção e mudança de categoria não reconectam histórico a outro atleta; tratar aposentado/removido conforme a política definida |
| Recuperação | Manifesto/save coerentes em retomada normal e interrupção; falha de escrita não publica sucesso ou checkpoint incompleto |
| Compatibilidade | Save nativo e legado online testados separadamente; erro/limitação explícita para referência sem identidade recuperável |
| H órfão | H sem ID e H apontando para competição ausente na agenda geram o resultado de compatibilidade definido, sem falsa confirmação de retomada íntegra nem criação de contadores vazios na primeira consulta |
| Interface e pacote | Conferir suspensão e histórico pelos consumidores reais e pelo JAR entregue, além do round-trip de objetos |

Comparar relações e comportamento, não somente tamanho do arquivo ou campos de snapshot. `CareerProbe.stableSnapshot` remove eligible e não publica os registros de histórico; seu resultado anterior não cobre estes critérios. Testes de projeção de H no cliente também não certificam o host retomado.

Relacionados: [disponibilidade](spec-disponibilidade-jogadores-online.md), [histórico](spec-consultas-historicas-online.md), [calendários](spec-calendarios-ligas-online.md) e [Procura](spec-pesquisa-jogadores-online.md).

## Estado e lacunas da implementação — 10/11/2026

`NativeReferencePersistence.prepare/reconnect` cobre as duas relações principais H→competição e ap→atleta profissional/júnior, e o teste adicionado declara duas retomadas entre JVMs. Permanecem sem evidência: legado com referências sem ID/correspondência, associação de H a competições homônimas em temporadas distintas, ranking/galeria usando as avaliações H, transferências históricas através da janela nativa e coerência de ofertas/checkpoint em interrupção. A reconexão atual não contém uma política explícita por registro não resolvido; definir se rejeita o save, expõe aviso ou preserva um histórico destacado antes de aceitar esses dados como íntegros.
