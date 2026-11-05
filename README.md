# Brasfoot Online

## Estado atual do projeto

O fluxo multiplayer de carreira roda em uma instância autoritativa no host. A seleção de clubes usa os seletores nativos do Brasfoot e a configuração atual aceita de 1 a 16 técnicos. O host executa as partidas, mantém o calendário e compartilha os dados com os clientes.

Durante uma partida, qualquer participante pode clicar em um clube para abrir sua escalação nativa. A tela de outro clube é somente leitura; o técnico responsável pode editar a própria escalação. O servidor valida as alterações e o limite de substituições. Abrir a tela não pausa o relógio.

No fim do primeiro tempo, o relógio pausa e a tela editável do próprio clube abre automaticamente para cada técnico. O segundo tempo começa somente quando todos confirmam **Pronto para o 2º tempo**. O host mantém checkpoints locais; uma partida interrompida é retomada desde a preparação da data salva.

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
python tools/test_session.py
python tools/test_native_session.py
python tools/test_native_create.py
python tools/test_career.py
python tools/test_career_session.py
python tools/package_client.py
```

`src/brasfoot/` contém host, protocolo, motor de carreira e interfaces online. `tests/brasfoot/` contém verificações do adaptador e da interface. `tools/` compila, executa os fluxos automatizados e empacota o cliente. `docs/multiplayer.md` descreve o funcionamento e os limites atuais.

O jogo original não é recompilado nem alterado. O pacote distribuível é produzido em `dist/` pelo script de empacotamento.
