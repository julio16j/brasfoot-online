# Revisão independente da escalação em folga

Data: 08/11/2026. Branch `impl/escalacao-folga`, worktree `F:/Projects/Brasfoot-online-impl-folga`. Nenhuma alteração nessa worktree. Artefato copiado para `C:/Users/Julio/AppData/Local/Temp/brasfoot-review-rest-day-o7akoyd5/delivery.jar`; SHA256 `0afdda1f2f26f8b2f09f05981e8b2ad80c1c39acaf4a58a029a716ab7d10dd09`.

## Execuções e resultado

Diff de `NativeClient` e `NativePreparation` revisado: motivo de bloqueio comum, acesso nativo sincronizado, folga e próximo confronto diferenciados, escopo de jogos explicado. O host e suas regras de avanço não foram modificados nesse recorte.

`NativeRestDayWindowTest` foi executado novamente contra a cópia do JAR. Primeira execução falhou em “Native button did not open editor” após clique Robot. A captura de data com jogo mostra o controle habilitado; não havia outra JVM quando os processos foram inspecionados depois da falha. Não foi demonstrada causa de foco, concorrência ou atraso; não classificar como bug de produto nem ocultar essa instabilidade.

Segunda execução idêntica, sem alterações ou aumento de timeout, passou: folga com próximo/partida de outro técnico; ausência de próximo; abertura dos editores por acesso nativo e topo; bloqueio durante editor e restauração ao fechar; Pronto/retirada projetados; explicação dos jogos alheios. Capturas e log ficam em `repeat/` e `repeat.txt`. `rest-next.png` e `rest-running.png` foram visualizadas pelo revisor, confirmando mensagens e controles.

O recorte está coerente com o critério 1 da task original nos cenários aprovados, com ressalva da primeira falha do teste gráfico. Teste dirigido por snapshots controlados não certifica o envio Pronto/retirada por rede, nem o save original do Barcelona. Não foram repetidas aqui a regressão HTTP de dois clientes ou a carreira completa; o resultado do Agente 2 permanece evidência dele.

## Achado independente

A captura de folga revela lesões fictícias em todos os atletas e prazo absurdo de contusão. O probe da principal confirmou a causa em `VisualModel`: [task de projeção de lesões](task-projecao-lesoes-online.md). Isso já existia antes do recorte de mensagem, que não corrige o estado médico. Manter problema separado e não anunciar correção completa da experiência de folga.

Não há integração à principal nem pacote combinado com Opções/treino por esta revisão. A task de escalação continua com automação de folgas e planejamento antecipado pendentes.
