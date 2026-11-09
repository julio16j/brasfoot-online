# Geração de ligas pelos países escolhidos

Data: 09/11/2026. Status: recorte implementado e validado na branch; não integrado à principal.

Branch `impl/calendario-paises-v2`, worktree `F:/Projects/Brasfoot-online-impl-calendario-paises-v2`, base `68874d081879f519a39b6496ac91711dcc82bde0`. Escopo PN-02: resolver clubes e países antes da geração de competições, gerar ligas dos países escolhidos e manter os clubes estrangeiros necessários no mundo. Não abrange avanço automático de datas, consultas/navegação do calendário, transição de temporada ou migração de saves.

## Alterações

`CareerEngine.createByAssets` valida referências estáveis, inicializa o mundo sem competições, liga os países dos clubes selecionados e chama a geração uma única vez. Clubes estrangeiros continuam carregados para copas e demais relações nativas. A interface envia `club.jY()` como referência estável em `--club-assets`; o host mantém compatibilidade com `--club-ids` e nomes. A resolução por nome agora aceita países diferentes, identifica nomes desconhecidos ou ambíguos e evita restringir a busca ao Brasil. O caminho de IDs legado traduz os IDs em referências de asset e então cria a carreira no recorte selecionado.

## Validação

| Cenário | Resultado |
| --- | --- |
| Brasil, Espanha e Brasil + Espanha com motor real | Somente países selecionados habilitados e suas ligas geradas; calendários e técnicos presentes |
| Bayern de Munique, Liverpool, Juventus, Paris Saint Germain e Boca Juniors | Um país habilitado por cenário; simulações avançaram entre 120 e 129 datas sem falhas |
| `Real Madrid` pelo CLI de nomes e Corinthians + Real Madrid por assets | Host encontrou os clubes, reconheceu países e inicializou a carreira |
| IDs numéricos legados e nome desconhecido | IDs resolvidos para assets antes da geração final; nome inválido retorna erro |
| Retenção de clubes estrangeiros | Teste direto confirma que o mundo continua contendo clubes estrangeiros |
| `python tools/build_adapter.py --test` | Aprovado, incluindo `CareerCountrySelectionTest` |
| `python tools/tests/test_career_session.py` | Aprovado: quatro técnicos, duas rodadas, barreira, resultados compartilhados e retomada |
| `python tools/tests/test_career_country_selection.py --jar dist/brasfoot-online.jar` | Aprovado usando JAR e agente empacotados |
| `python tools/package_client.py` | Concluído; ZIP contém 22.735 arquivos, CRC íntegro e sem harnesses de teste |

Artefatos nesta worktree: `dist/brasfoot-online.jar`, SHA-256 `086ebb87df01b2fa535f1c4ee6918b17510810f6e1d1693d25a677280ac905a1`; `dist/Brasfoot-Online-Teste.zip`, 80.131.643 bytes, SHA-256 `0f028fde4f2c04fb5185c57e190ceab8f47e8e2f9a93dfd477c5fa6b5efc40d2`.

## Limites

As carreiras e simulações são descartáveis e usam a base local Brasfoot 22–23. A matriz não certifica regras atuais, várias temporadas estrangeiras nem save/reload de uma carreira internacional. O teste de CLI valida o transporte de assets e o host; não é uma interação gráfica automatizada com o seletor nativo. O caminho por IDs legado pode inicializar o mundo uma vez para resolver os IDs e novamente para a carreira final; a interface nova usa referências de asset e evita essa etapa. Folgas ainda exigem a barreira/avanço já existentes; filtros, ordenação e atualização das janelas do calendário seguem pendentes. Saves existentes não são migrados.
