# Brasfoot Online

Atualizações de 08/11/2026: auxiliar de treino com confirmação pelo host, persistência e processamento nativo; explicação de folgas e bloqueio coerente dos três acessos de escalação; acesso às Preferências pelo menu nativo e F3. Consulte o [registro de integração, validação e limites](docs/integracao-main-2026-11-08.md).

## Estado verificado das telas de preparação

**Revisado em 5 de novembro de 2026.** O fluxo multiplayer de carreira roda em uma instância autoritativa no host. A criação da sessão, a preparação, as partidas, o calendário e as ações de gestão usam o estado do host para manter os clientes sincronizados.

- A seleção de clubes e as telas de preparação reutilizam os menus nativos do Brasfoot.
- A tela nativa de escalação permite ajustar titulares e opções táticas; o host valida e aplica as alterações.
- Durante as partidas, os clientes veem o mesmo placar e os mesmos eventos. O relógio pausa no intervalo e só continua quando todos os técnicos confirmam o segundo tempo.
- A tela nativa **Times** encaminha compras e propostas ao host. Negociações com clubes da IA usam as regras nativas, incluindo contrapropostas. Propostas a clubes controlados por outros jogadores ficam pendentes para resposta do técnico vendedor.
- Empréstimos ainda não estão integrados à sessão online e ficam desabilitados.
- A expansão do estádio ainda precisa de validação completa; não conte com a obra nem com o débito do custo até essa correção ser confirmada.

## Estado atual (revisado)

O fluxo multiplayer de carreira roda em uma instância autoritativa no host. A seleção de clubes usa os seletores nativos do Brasfoot e a configuração atual aceita de 1 a 16 técnicos. O host executa as partidas, mantém o calendário e compartilha os dados com os clientes.

Durante uma partida, qualquer participante pode clicar em um clube para abrir sua escalação nativa. A tela de outro clube é somente leitura; o técnico responsável pode editar a própria escalação. O servidor valida as alterações e o limite de substituições. Abrir a tela não pausa o relógio.

No fim do primeiro tempo com partidas humanas, o relógio pausa. A tela editável do próprio clube abre automaticamente conforme a configuração do host; a abertura manual continua disponível. O segundo tempo começa somente quando todos confirmam **Pronto para o 2º tempo**. O host mantém checkpoints locais; uma partida interrompida é retomada desde a preparação da data salva.

## Jogar

1. No Windows com Java 8, execute `Brasfoot-Online.cmd`.
2. O host escolhe **Carreira**, seleciona de 1 a 16 clubes na interface nativa e cria a sessão.
3. Os demais jogadores informam o endereço e a porta do host, buscam os técnicos e entram em vagas livres.
4. Cada técnico ajusta sua escalação na tela original e confirma **Pronto**. A data só começa quando todos confirmam.
5. Depois das partidas, o host avança para a próxima data. O motor original mantém competições, calendário e temporadas.

O host compartilha o IP e a porta TCP. Conexões entre redes diferentes dependem da configuração de rede do host. O jogo e os saves ficam no computador do host.

## Carreira online

- Escalação original, titulares, reservas e opções táticas enviados ao host.
- Partidas da data, placares, lances e eventos compartilhados; clientes não simulam resultados localmente.
- Substituições durante os tempos e no intervalo, autorizadas para o técnico responsável pelo clube. O relógio só pausa quando o primeiro tempo termina e retoma quando todos confirmam o segundo.
- Visão de calendário e classificações, além de saldo, folha salarial e elenco.
- Gestão por técnico: renovações, promoção da base, lista de transferências, propostas entre clubes humanos, venda para a IA e busca de emprego.
- Checkpoints Kryo locais no host. **Retomar carreira** carrega o último checkpoint seguro; uma data interrompida volta à preparação.
- Os tokens não são restaurados depois que o host reinicia. Os jogadores entram novamente na sessão retomada.

## Desenvolvimento

Requer Java 8. Os comandos abaixo compilam o adaptador contra o bytecode original e executam as verificações automatizadas disponíveis:

```powershell
python tools/build_adapter.py --test
python tools/build_adapter.py --test-transfer
python tools/tests/test_coordinator.py
python tools/tests/test_session.py
python tools/tests/test_native_session.py
python tools/tests/test_native_create.py
python tools/tests/test_career.py
python tools/tests/test_career_session.py
python tools/package_client.py
```

Os testes Python ficam em `tools/tests/`. Os testes Java ficam em `tests/brasfoot/`. `src/brasfoot/` contém o host, o protocolo, o motor de carreira e as interfaces online. `docs/multiplayer.md` descreve o funcionamento e os limites atuais.

O [índice da documentação](docs/README.md) reúne o [mapa da implementação](docs/mapa-implementacao.md), as pendências e o histórico de [problemas resolvidos](docs/resolvidos/README.md).

As [configurações online](docs/especificacao/spec-configuracoes-online.md) têm edição exclusiva do host. Use o botão nativo **Opções → Preferências** ou **F3** para ajustar velocidades, som, cores, tema e abertura da escalação no intervalo. O botão adicional Configurações foi removido; veja a [implementação do acesso nativo](docs/implementacao-opcoes-nativas-online.md) e as [opções ainda bloqueadas](docs/implementacao-configuracoes-online.md). Validação específica: `python tools/tests/test_session_options.py`.

A [investigação dos calendários de outras ligas](docs/task-calendarios.md) registra os problemas encontrados, os cenários exercitados e a ordem proposta de implementação. Para reproduzir o diagnóstico local, execute `python tools/investigate_calendars.py` após compilar o adaptador.

O jogo original não é recompilado nem alterado. O pacote distribuível é produzido em `dist/` pelo script de empacotamento.
