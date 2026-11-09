"""Exercises host-authoritative stadium construction through HTTP and restart."""
from pathlib import Path
import hashlib, json, os, re, socket, subprocess, time, urllib.parse, urllib.request, urllib.error

ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
processes=[]

def request(base,path,token=None,fields=None,expected=200):
    req=urllib.request.Request(base+path,None if fields is None else urllib.parse.urlencode(fields).encode(),{} if token is None else {'Authorization':'Bearer '+token})
    try:
        with urllib.request.urlopen(req,timeout=30) as response:code=response.status;body=json.load(response)
    except urllib.error.HTTPError as error:code=error.code;body=json.load(error)
    assert code==expected,(path,code,body)
    return body

def start(label,extra):
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
    log_path=ROOT/f'analysis/stadium-{label}-{port}.log';log=log_path.open('w',encoding='utf-8')
    proc=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--bind','127.0.0.1','--port',str(port),'--tick-ms','10000',*extra],stdout=log,stderr=subprocess.STDOUT)
    processes.append((proc,log));base=f'http://127.0.0.1:{port}';deadline=time.monotonic()+45
    while time.monotonic()<deadline:
        output=log_path.read_text(encoding='utf-8',errors='replace');token=re.search(r'^Host token: (\S+)',output,re.M);directory=re.search(r'^Session directory: (.+)$',output,re.M)
        if token:return proc,base,token.group(1),Path(directory.group(1).strip())
        assert proc.poll() is None,output[-4000:];time.sleep(.1)
    raise AssertionError('Career host failed to start')

def round_fields(state):
    coach=state['round']['coaches'][0]
    return {'round':state['round']['round'],'revision':coach['revision']}

def construction(state,club_id):
    return next(row for row in state['game']['stadiumExpansions'] if row['clubId']==club_id)

try:
    proc,base,token,directory=start('transaction',['--career','--clubs','Corinthians,Palmeiras'])
    initial=request(base,'/state',token);coach=initial['game']['coaches'][0];club_id=coach['clubId'];before_balance=next(row['balance'] for row in initial['game']['visualClubs'] if row['clubId']==club_id)
    before_project=initial['game'].get('stadiumExpansions',[]);fields={**round_fields(initial),'action':'stadium-expand','seats0':'500','seats1':'0','seats2':'0','seats3':'0'}
    response=request(base,'/management',token,fields);assert response['command']['status']=='CONSTRUCTION_STARTED',response
    assert response['checkpoint']==initial['checkpoint']+1,'Successful construction must checkpoint exactly once'
    after=request(base,'/state',token);project=construction(after,club_id)
    assert project['seats']==[500,0,0,0],project
    expected_cost=response['command']['cost']
    assert after['checkpoint']==response['checkpoint'] and len(after['game']['stadiumExpansions'])==len(before_project)+1
    duplicate=request(base,'/management',token,{**round_fields(after),'action':'stadium-expand','seats0':'500','seats1':'0','seats2':'0','seats3':'0'},expected=409)
    rejected=request(base,'/state',token);assert rejected['checkpoint']==after['checkpoint'] and rejected['game']['stadiumExpansions']==after['game']['stadiumExpansions']
    assert before_balance-next(row['balance'] for row in rejected['game']['visualClubs'] if row['clubId']==club_id)==expected_cost
    prices={'action':'stadium-settings','suggestedPrices':'false','price0':'125','price1':'250','price2':'450','price3':'900'}
    updated=request(base,'/management',token,{**round_fields(rejected),**prices});assert updated['command']['status']=='STADIUM_SETTINGS_UPDATED'
    assert updated['checkpoint']==rejected['checkpoint']+1
    settings=request(base,'/state',token)['game']['visualClubs'];club=next(row for row in settings if row['clubId']==club_id)
    assert club['stadium']['suggestedPrices'] is False and club['stadium']['prices']==[125,250,450,900]

    # Conflict coverage is verified with both human participants ready; ordinary sequential HTTP calls cannot race revisions.
    ready=request(base,'/lineup',token,{**round_fields(updated),'starters':','.join(map(str,updated['game']['coaches'][0]['suggestion']['starters'])),'bench':','.join(map(str,updated['game']['coaches'][0]['suggestion']['bench']))})
    ready=request(base,'/ready',token,{**round_fields(ready),'ready':'true'})
    stale=request(base,'/management',token,{**round_fields(ready),'action':'stadium-settings','suggestedPrices':'true','price0':'125','price1':'250','price2':'450','price3':'900'},expected=409)
    stale_state=request(base,'/state',token);club_after=next(row for row in stale_state['game']['visualClubs'] if row['clubId']==club_id)
    assert stale_state['checkpoint']==ready['checkpoint'] and club_after['stadium']==club['stadium'],'Ready rejection changed confirmed prices'
    proc.terminate();proc.wait(timeout=10)
    proc,base,token,_=start('resume',['--resume',str(directory)])
    recovered=request(base,'/state',token);assert construction(recovered,club_id)==project,'Construction project must survive a new host process'
    assert next(row for row in recovered['game']['visualClubs'] if row['clubId']==club_id)['stadium']==club['stadium'],'Manual prices must survive restart'
    print(json.dumps({'result':'PASS','checks':['HTTP construction uses native quote and checkpoints once','duplicate construction rejection does not change checkpoint, project or debit','project identity and quote recover after host restart','manual price confirmation checkpoints and survives restart','stale Ready/revision price edit is rejected without mutation'],'cost':expected_cost,'clubId':club_id}))
finally:
    for proc,log in processes:
        if proc.poll() is None:proc.terminate();proc.wait(timeout=10)
        log.close()
