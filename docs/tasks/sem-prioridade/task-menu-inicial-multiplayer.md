# Menu inicial da sessao multiplayer

**Status:** implementado e empacotado; aceite visual do usuario pendente. Revisado em 11/11/2026.

## Comportamento

A primeira tela apresenta tres opcoes centralizadas horizontalmente com icones e posicionadas acima do centro vertical. Os cartoes mantem seu tamanho, tem cantos arredondados e realce ao passar o mouse. Os campos de criacao aparecem somente depois da primeira opcao; retomar mantem o seletor de pasta direto; entrar apresenta IP e porta separados, busca de tecnicos e reconexao por token.

Em carreiras novas, a geracao do calendario comeca em 2026. Em sessoes novas, a velocidade humana inicia em Super rapido e a velocidade sem jogos humanos em Ultra rapido quando a instalacao permite; caso contrario, respeita o limite nativo disponivel. Uma sessao retomada conserva as velocidades salvas.

## Validacao

`python tools/package_client.py` compilou e atualizou o JAR e o ZIP. Testes automatizados e aceite visual nao foram executados.
