# Transporte de snapshots de carreira

O cliente deve receber snapshots legitimos completos, inclusive quando os clubes projetados nas partidas da data excederem 8 MiB. Nao truncar clubes, jogadores ou partidas para contornar o limite de transporte. Manter limite explicito de 32 MiB de bytes UTF-8 por resposta, tanto com Content-Length quanto em transferencia chunked; rejeitar antes do parse JSON quando ultrapassado. Aumentos futuros exigem medicao e revisao de memoria/protocolo.

Aceite deste recorte: JSON acima de 8 MiB preservado integralmente; resposta declarada/chunked acima de 32 MiB rejeitada; GET /state real de carreira global recebido; autentica??o e demais limites preservados. Otimizacao/deltas e matriz de 16 tecnicos ficam fora deste recorte.
