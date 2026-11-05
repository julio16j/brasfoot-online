"""Cliente de terminal com transmissao compartilhada da partida."""
import argparse, getpass, json, threading, urllib.request, urllib.parse, urllib.error
p=argparse.ArgumentParser(description=__doc__)
p.add_argument('address',help='Exemplo: http://192.168.0.6:8212')
p.add_argument('--host',action='store_true',help='Pede o token mostrado pelo host')
p.add_argument('--token',help='Token para reconectar')
p.add_argument('--watch',action='store_true',help='Acompanha a partida ate o fim sem abrir o menu')
a=p.parse_args()
base=a.address.rstrip('/')
if not base.startswith(('http://','https://')):base='http://'+base

def request(path,data=None,token=None):
    body=None if data is None else urllib.parse.urlencode(data).encode()
    headers={} if token is None else {'Authorization':'Bearer '+token}
    try:
        with urllib.request.urlopen(urllib.request.Request(base+path,body,headers),timeout=15) as r:return json.load(r)
    except urllib.error.HTTPError as e:
        error=json.load(e)
        raise RuntimeError(error.get('error',str(e))) from e

LABELS={1:'GOL',2:'Cartao amarelo',3:'Segundo amarelo / expulso',4:'Cartao vermelho',5:'Lesao',6:'Substituicao',7:'Penalti perdido'}
PHASES={'NOT_STARTED':'Aguardando os tecnicos','FIRST_HALF':'1o tempo','HALF_TIME':'Intervalo','SECOND_HALF':'2o tempo','FINISHED':'FIM DE JOGO','FAILED':'FALHA NA PARTIDA'}

def follow(token,stop,finished):
    previous=None;seen=set();last_ready=None;failed=False
    while not stop.is_set():
        try:
            state=request('/state',token=token)
            live=state['playback']
            ready=tuple((c['coachId'],c['ready']) for c in state['round']['coaches'])
            if ready!=last_ready:
                print('\nPreparacao: '+', '.join(f"Tecnico {i}: {'PRONTO' if value else 'preparando'}" for i,value in ready),flush=True)
                last_ready=ready
            if live['sequence']!=previous:
                phase=PHASES.get(live['status'],live['status'])
                for match in live['matches']:
                    score=f"{match['home']} {match['homeGoals']} x {match['awayGoals']} {match['away']}"
                    print(f"\n[{phase} | {live['minute']}'] {score}",flush=True)
                    for event in match['events']:
                        key=(match['id'],event['id'])
                        if key not in seen:
                            print(f"  {event['minute']}' {LABELS.get(event['type'],'Lance')} - {event['player'] or ''} ({event['club'] or ''})",flush=True)
                            seen.add(key)
                previous=live['sequence']
            if live['status'] in ('FINISHED','FAILED'):
                if live.get('failure'):print('Erro do host:',live['failure'],flush=True)
                finished.set();return
            if state['storageFailed']:
                print('O host pausou os comandos por falha ao gravar o save.',flush=True)
                finished.set();return
            failed=False
        except (OSError,RuntimeError,ValueError) as error:
            if not failed:print('\nConexao interrompida; tentando reconectar:',error,flush=True)
            failed=True
        stop.wait(0.25)

def run():
    info=request('/session')
    if info['demo']:print('AMISTOSO DE DEMONSTRACAO: ABC x Corinthians. Todos acompanham a mesma partida.')
    token=a.token
    if token is None and a.host:token=getpass.getpass('Token exibido no host: ')
    if token is None:
        for c in info['coaches']:print(f"{c['coachId']}: {c['name']} / {c['club']} - {'ocupado' if c['claimed'] else 'disponivel'}")
        joined=request('/join',{'coachId':int(input('Seu tecnico: '))})
        token=joined['token']
        print('Token para reconectar enquanto o host estiver aberto:',token)
    state=request('/state',token=token)
    coach_id=state['viewerCoachId']
    stop=threading.Event();finished=threading.Event()
    watcher=threading.Thread(target=follow,args=(token,stop,finished),daemon=True);watcher.start()
    try:
        if a.watch:
            while not finished.wait(0.5):pass
            return
        print('Escalacao manual em 4-4-2: goleiro, dois laterais, dois zagueiros, quatro meias e dois atacantes.')
        print('Use [u] para receber uma escalacao automatica, que pode ser alterada antes do Pronto.')
        while True:
            choice=input('\n[u] Autoescalar [e] Escalacao [p] Pronto [v] Retirar Pronto [a] Atualizar [s] Sair: ').strip().lower()
            if choice=='s':return
            try:
                state=request('/state',token=token)
                r=state['round']
                coach=next(c for c in state['game']['coaches'] if c['coachId']==coach_id)
                local=next(c for c in r['coaches'] if c['coachId']==coach_id)
                data={'round':r['round'],'revision':local['revision']}
                if choice in ('e','u'):
                    if choice=='e':
                        for player in coach['players']:
                            role=['G','L','Z','M','A'][player['position']] if player['position'] in range(5) else '?'
                            print(f"{player['index']:2} {player['name']} | {role} | {'disponivel' if player['eligible'] else 'indisponivel'}")
                        data['starters']=input('11 titulares na ordem indicada (indices separados por virgula): ').replace(' ','')
                        data['bench']=input('Ate 11 reservas (opcional): ').replace(' ','')
                    else:
                        data['starters']=','.join(map(str,coach['suggestion']['starters']))
                        data['bench']=','.join(map(str,coach['suggestion']['bench']))
                    request('/lineup',data,token)
                    print('Escalacao enviada. Voce ainda pode alterar antes de confirmar Pronto.')
                elif choice in ('p','v'):
                    data['ready']='true' if choice=='p' else 'false'
                    request('/ready',data,token)
                elif choice=='a':print(PHASES.get(state['playback']['status']),state['round']['phase'])
            except (OSError,RuntimeError,ValueError) as error:print('Comando recusado:',error)
    finally:stop.set()
try:run()
except (KeyboardInterrupt,EOFError):pass
except (OSError,RuntimeError,ValueError,StopIteration) as error:p.exit(1,f'Erro: {error}\n')
