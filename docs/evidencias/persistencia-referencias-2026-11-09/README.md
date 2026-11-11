# Leitura estática do serializer — 09/11/2026

Evidência da [task de referências nativas](../../tasks/sem-prioridade/task-persistencia-referencias-nativas.md) e da [spec](../../especificacao/spec-persistencia-referencias-nativas.md). O parser lê ZIP/class em Python, sem JVM, jogo ou round-trip de save.

- [Parser auxiliar](classread.py.txt): arquivo de investigação, sem alteração de produção/testes.
- [FieldSerializerConfig](serializer-config.json): construtor grava serializeTransient=false; copyTransient é configuração distinta.
- [Kryo](Kryo.json): construção da configuração do serializer.
- [FieldSerializer](FieldSerializer.json): cópia da configuração e guardas de escrita/leitura dos campos transitórios.
- [Hashes dos JARs lidos](artefatos-lidos.json) e [hashes destas evidências](sha256.json).

As extrações preservam a saída original e os offsets de bytecode. São evidência do formato/configuração da biblioteca distribuída, não da execução de save/retomada. A consequência para H/ap também depende dos consumidores e hooks rastreados na task; a omissão de transient não torna todo campo desse tipo um defeito.

Nativo: SHA-256 `096c9b3d2f6b4cb1feb220ec1b6d3c6f150df1eac92a17546cc804e35d4a5d0d`. Kryo 4.0.2: SHA-256 `f32aceb5b2eb39be6f339da722c3bb043df163c38d51522acbb77ee4d6c4d362`. Não foram incluídos JARs, saves ou logs de sessões.
