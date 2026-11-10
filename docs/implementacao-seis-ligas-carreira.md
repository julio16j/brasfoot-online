# Seis ligas em toda carreira nova

Recorte PN-02 da [task de calendarios](task-calendarios.md) e [spec das seis ligas](especificacao/spec-calendarios-ligas-online.md). Base 77377c6b, branch impl/seis-ligas-carreira. CareerEngine ativa sempre Brasil 29, Inglaterra 97, Espanha 65, Italia 104, Alemanha 3 e Franca 72 antes da geracao nativa. Bootstrap do seletor usa o mesmo conjunto; a tela nativa mostra seis paises e suas divisoes. O host resolve assets e rejeita clubes humanos de outros paises, inclusive entradas por nome e ID. Clubes/jogadores mundiais e catalogo global de nacionalidades permanecem; carregamento de save legado nao foi alterado.

| Cenario | Execucao | Observado |
| --- | --- | --- |
| Um brasileiro, um espanhol e misto | CareerCountrySelectionTest | Flags e competicoes dos seis paises, fixtures domesticos em todos eles |
| Seis humanos de paises diferentes | Mesmo teste | Calendarios para todos e conjunto exato de paises |
| Clube argentino por asset/nome/ID | Mesmo teste | Rejeicao por pais; clube continua no mundo |
| Nome estrangeiro e ID permitido | Mesmo teste | Cria carreira com as seis ligas |
| Nacionalidade estrangeira | Mesmo teste | Codigo independente do pais da liga preservado |
| Seletor nativo real | NativeSixCountrySelectionWindowTest | Seis opcoes e divisoes; captura inspecionada |
| Busca e transporte global HTTP | NativePlayerSearchTest | 63.258 profissionais e paginacao ate ultima pagina, snapshot recebido |

[Captura](evidencias/seis-ligas-2026-11-10/selector.png). Suite de paises e busca aprovadas contra JAR da branch. Dois testes foram corrigidos para os criterios atuais: media de avaliacoes identifica atleta por playerId para evitar homonimos; pagina final de busca calculada pelo total anunciado, sem supor que 51.000 e a ultima pagina. Compilacao e empacotamento executados. Spec local da principal copiada apenas como contexto nao versionado na worktree; nenhuma edicao alheia incluida. Autorrevisao: validacao do pais ocorre depois da resolucao do asset e antes da geracao; bootstrap e host usam a mesma allowlist imutavel; estado de saves existentes nao regenerado. Pendencias da spec ampla: avancos IA sem rodada humana, transicao entre temporadas, migracao/retomada internacional e consultas de agenda/tabelas; nao declarar PN-02 integralmente encerrada.
