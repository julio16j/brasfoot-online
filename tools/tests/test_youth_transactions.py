"""Checks Ready conflict and atomic rollback when a promotion checkpoint fails."""
from pathlib import Path
import json, os, re, socket, subprocess, time, urllib.request, urllib.parse, urllib.error

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

def start(label):
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
    log_path=ROOT/f'analysis/youth-{label}-{port}.log';log=log_path.open('w',encoding='utf-8')
    proc=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--career','--clubs','Corinthians,Palmeiras','--bind','127.0.0.1','--port',str(port)],stdout=log,stderr=subprocess.STDOUT)
    processes.append((proc,log));base=f'http://127.0.0.1:{port}';deadline=time.monotonic()+45;token=directory=None
    while token is None:
        output=log_path.read_text(encoding='utf-8',errors='replace');token_match=re.search(r'^Host token: (\S+)',output,re.M);dir_match=re.search(r'^Session directory: (.+)$',output,re.M)
        if token_match:token=token_match.group(1);directory=Path(dir_match.group(1).strip());break
        assert proc.poll() is None,output[-4000:]
        assert time.monotonic()<deadline,'Career host failed to start';time.sleep(.1)
    return proc,base,token,directory

def fields(state,index=0):
    row=state['round']['coaches'][index]
    return {'round':state['round']['round'],'revision':row['revision']}

def youth(state,club_id):
    club=next(row for row in state['game']['visualClubs'] if row['clubId']==club_id)
    assert club['youth'],'Host club has no youth players'
    return club['youth'],club['players']

try:
    process,base,token,_=start('ready-conflict');initial=request(base,'/state',token)
    coach=initial['game']['coaches'][0];coach_id=coach['coachId'];club_id=coach['clubId'];before_youth,before_players=youth(initial,club_id)
    suggestion=coach['suggestion'];prepared=request(base,'/lineup',token,{**fields(initial),'starters':','.join(map(str,suggestion['starters'])),'bench':','.join(map(str,suggestion['bench']))})
    ready=request(base,'/ready',token,{**fields(prepared),'ready':'true'});player_id=before_youth[0]['playerId']
    request(base,'/management',token,{**fields(ready),'action':'promote','playerId':player_id},expected=409)
    current=request(base,'/state',token);after_youth,after_players=youth(current,club_id)
    assert player_id in {row['playerId'] for row in after_youth} and player_id not in {row['playerId'] for row in after_players},'Ready conflict changed academy or roster'
    assert current['checkpoint']==ready['checkpoint'],'Ready conflict wrote a checkpoint'
    withdrawn=request(base,'/ready',token,{**fields(current),'ready':'false'})
    editor=subprocess.run([JAVA,'-Dfile.encoding=UTF-8','-cp',CP,'brasfoot.YouthEditorConflictTest',str(ROOT),base,token],capture_output=True,text=True,timeout=60,check=True)
    assert 'PASS lineup editor preserves local draft during remote promotion and reconciles on close' in editor.stdout,editor.stdout+editor.stderr
    process.terminate();process.wait(timeout=10)

    process,base,token,directory=start('storage-rollback');initial=request(base,'/state',token);coach=initial['game']['coaches'][0];club_id=coach['clubId'];before_youth,before_players=youth(initial,club_id);player_id=before_youth[0]['playerId']
    assert ROOT.resolve() in directory.resolve().parents,'Session directory must be inside the test workspace'
    manifest=directory/'session.json';backup=directory/'session.validation-backup';assert manifest.is_file() and not backup.exists()
    manifest.replace(backup);manifest.mkdir()
    try:
        request(base,'/management',token,{**fields(initial),'action':'promote','playerId':player_id},expected=503)
        failed=request(base,'/state',token);after_youth,after_players=youth(failed,club_id)
        assert player_id in {row['playerId'] for row in after_youth} and player_id not in {row['playerId'] for row in after_players},'Failed checkpoint did not restore the player and rosters'
        assert failed['storageFailed'],'Host should report its failed storage state'
    finally:
        manifest.rmdir();backup.replace(manifest)
    process.terminate();process.wait(timeout=10)
    print(json.dumps({'result':'PASS','checks':['Ready blocks promotion without changing roster or checkpoint','open lineup editor preserves its local draft during promotion and reconciles on close','HTTP storage failure returns 503 and rolls back junior membership','the original manifest is restored after the injected failure']}))
finally:
    for process,log in processes:
        if process.poll() is None:process.terminate();process.wait(timeout=10)
        log.close()
