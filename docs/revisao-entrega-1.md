# Revisão da primeira entrega do agente 2

Data: 08/11/2026. Branch `impl/treino-auxiliar-indisponivel`, worktree `F:/Projects/Brasfoot-online-impl-treino`, base `f3200ada55db25b80f80fb0f48e049bc2e6e82ba`. Revisão das alterações locais, ainda sem commit ou integração.

## Resultado

O bloqueio do auxiliar corresponde à alternativa explícita do critério 1 de `task-treino-auxiliar-online.md`. A remoção do callback evita a alteração apenas no cliente, preserva o estado inicial e mantém o encaminhamento do foco manual. Não é implementação do auxiliar online nem conclusão de PN-01.

## Achado P2 — Explicação parcialmente cortada

Em `NativeTraining.java:20`, a janela continua fixa em 409 × 252, incluindo decorações, apesar do tamanho preferido do painel ser 413 × 254. O controle desabilitado que contém a explicação fica próximo ao limite inferior.

Verificação executada com o painel nativo real, configurado pela implementação e disposto em 393 × 213 (área interna representativa dessa janela no Windows): `MY` ocupa x=35, y=198, largura=348, altura=24; termina em y=222, nove pixels além do painel. O teste headless da entrega não dispõe o painel no tamanho da janela e não detecta esse corte. A medida foi obtida sem abrir janela; a área interna exata varia com as decorações do sistema. A dimensão fixa já existia antes, mas afeta diretamente a nova explicação e impede certificar sua apresentação.

Recomendação: dimensionar o diálogo pelo tamanho preferido do painel (`pack`, respeitando o layout), validar a visibilidade integral dos controles e conferir visualmente a explicação no launcher. Não basta testar a presença do texto no componente.

## Validação independente

- `python tools/build_adapter.py --test`: passou, incluindo teste novo, adaptador, elenco grande e mercado.
- `git diff --check`: passou.
- Callback nativo original inspecionado: `MY` chamava diretamente o setter do clube; sua remoção é pertinente.
- Layout medido em teste temporário externo ao repositório, sem alterar fontes.
- Empacotamento e teste do JAR: constam no registro do agente; não repetidos nesta revisão.
- Não executado aceite visual do launcher nem certificação do efeito semanal de treino.

## Integração

A branch está correta como entrega local isolada, mas ainda sem commit; um merge neste estado não transportaria as alterações. Seu pacote usa a base commitada e não inclui as alterações locais de Opções da pasta principal. Revisar e integrar o diff de código antes de reconstruir um pacote combinado; não substituir o pacote atual pelo ZIP da worktree como se contivesse ambos os trabalhos.

Conclusão: recorte funcional coerente, com ajuste de apresentação pendente antes do aceite. A task de integração completa do auxiliar continua aberta.
