# Autonomia neste repositorio

Execute os comandos necessarios para concluir as tarefas solicitadas sem pedir
confirmacao rotineira de comandos. Isso inclui inspecao, edicao, instalacao de
dependencias, builds, testes e inicializacao de servidores locais.

Esta orientacao vale para o agente principal e para os agentes delegados.
Quando uma tarefa ja estiver autorizada, prossiga ate concluir e validar o
resultado, sem solicitar novamente a mesma autorizacao.

Preserve alteracoes existentes do usuario. A autonomia para executar comandos
nao autoriza apagar trabalho alheio, publicar, fazer deploy ou enviar mensagens
sem que essas acoes facam parte da solicitacao do usuario.

As permissoes efetivas sao definidas pelo Codex e pelo ambiente de execucao.
Este arquivo nao substitui politicas superiores nem remove restricoes de uma
sessao ja iniciada. A configuracao local esta em `.codex/config.toml`.
