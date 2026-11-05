"""Four authenticated users, original Swing panels, repeated rounds and restart recovery."""
from pathlib import Path
import concurrent.futures, json, os, re, socket, subprocess, time, urllib.request, urllib.parse, urllib.error
ROOT=Path(__file__).resolve().parents[1]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
processes=[]
def request(base,path,data=None,token=None,expected=200):
    req=urllib.request.Request(base+path,None if data is None else urllib.parse.urlencode(data).encode(),{} if token is None else {'Authorization':'Bearer '+token})
    try:
        with urllib.request.urlopen(req,timeout=30) as response:code=response.status;body=json.load(response)
    except urllib.error.HTTPError as error:code=error.code;body=json.load(error)
    assert code==expected,(path,code,body)
    return body
def host(mode):
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
    log_path=ROOT/f'analysis/career-host-{port}.log'
    log=log_path.open('w',encoding='utf-8')
    proc=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--bind','127.0.0.1','--port',str(port),'--tick-ms','25',*mode],stdout=log,stderr=subprocess.STDOUT)
    processes.append(proc);deadline=time.monotonic()+30
    while True:
        output=log_path.read_text(encoding='utf-8',errors='replace')
        token=re.search(r'^Host token: (\S+)',output,re.M)
        directory=re.search(r'^Session directory: (.+)',output,re.M)
        if token and directory:return proc,f'http://127.0.0.1:{port}',token[1],Path(directory[1].strip())
        assert proc.poll() is None,output[-4000:]
        assert time.monotonic()<deadline,'Host did not start'
        time.sleep(.1)
def fields(state,index):
    return {'round':state['round']['round'],'revision':state['round']['coaches'][index]['revision']}
def join_all(base,host_token):
    info=request(base,'/session');assert len(info['coaches'])==4
    return [host_token,*[request(base,'/join',{'coachId':row['coachId']},expected=201)['token'] for row in info['coaches'][1:]]]
def prepare(base,tokens):
    for index,token in enumerate(tokens):
        state=request(base,'/state',token=token);coach=state['game']['coaches'][index]
        if coach['hasMatch']:
            s=coach['suggestion'];assert len(s['starters'])==11,(coach['club'],s)
            request(base,'/lineup',{**fields(state,index),'starters':','.join(map(str,s['starters'])),'bench':','.join(map(str,s['bench']))},token)
    state=request(base,'/state',token=tokens[0])
    for index in range(3):request(base,'/ready',{**fields(state,index),'ready':'true'},tokens[index])
    state=request(base,'/state',token=tokens[0]);assert state['round']['phase']=='PREPARING','Started without the fourth user'
    request(base,'/ready',{**fields(state,3),'ready':'true'},tokens[3])
def finish(base,tokens):
    deadline=time.monotonic()+30
    while True:
        state=request(base,'/state',token=tokens[0]);assert state['round']['phase']!='FAILED',state['playback']['failure']
        if state['round']['phase']=='FINISHED':break
        assert time.monotonic()<deadline,'Round stalled'
        time.sleep(.1)
    for token in tokens[1:]:assert request(base,'/state',token=token)['playback']==state['playback']
    return state
try:
    subprocess.run([JAVA,'-cp',CP,'brasfoot.CoordinatorTest'],check=True,timeout=15)
    proc,base,token,directory=host(['--career']);tokens=join_all(base,token)
    # Four separate client JVMs submit through the original lineup screen.
    gui=[]
    for index,credential in enumerate(tokens):
        out=(ROOT/f'analysis/career-native-{index+1}.log').open('w',encoding='utf-8')
        client=subprocess.Popen([JAVA,'-cp',CP,'brasfoot.NativeClient','--root',str(ROOT),'--address',base,'--test-token',credential,'--test-auto','true','--test-native-editor','true','--capture',str(ROOT/f'analysis/career-game-{index+1}.png'),'--capture-preparation',str(ROOT/f'analysis/career-preparation-{index+1}.png')],stdout=out,stderr=subprocess.STDOUT)
        processes.append(client);gui.append(client)
    for index,client in enumerate(gui):
        client.wait(timeout=60)
        output=(ROOT/f'analysis/career-native-{index+1}.log').read_text(encoding='utf-8',errors='replace')
        assert client.returncode==0 and 'GUI_FINISHED' in output,output[-4000:]
    first=finish(base,tokens)
    first_round=first['round']['round']
    request(base,'/advance',fields(first,1),tokens[1],expected=401)
    request(base,'/advance',{'round':0,'revision':0},tokens[0],expected=409)
    assert request(base,'/state',token=token)['round']['phase']=='FINISHED'
    second=request(base,'/advance',fields(first,0),tokens[0]);assert second['round']['round']==2
    request(base,'/advance',fields(first,0),tokens[0],expected=409)
    assert request(base,'/state',token=token)['round']['round']==2
    assert second['game']['calendarIndex']>first['game']['calendarIndex']
    assert not any(row['ready'] for row in second['round']['coaches'])
    request(base,'/ready',{'round':1,'revision':0,'ready':'true'},tokens[0],expected=409)
    prepare(base,tokens);second=finish(base,tokens)
    assert second['round']['roundLocks']==2
    # Restore a completed date in another host process: no duplicated match or post-match hooks.
    proc.terminate();proc.wait(timeout=5)
    restored,base,token,new_directory=host(['--resume',str(directory)])
    state=request(base,'/state',token=token)
    assert state['round']['phase']=='FINISHED'
    assert state['round']['round']==second['round']['round']
    assert state['round']['roundLocks']==second['round']['roundLocks']
    assert all(not row['ready'] for row in state['round']['coaches'])
    assert state['game']['dateMillis']==second['game']['dateMillis']
    assert state['game']['matches']==second['game']['matches']
    state=request(base,'/advance',fields(state,0),token);assert state['round']['phase']=='PREPARING'
    assert state['round']['round']==3
    tokens=join_all(base,token);prepare(base,tokens)
    # Interrupt during play; recovery explicitly returns to preparation from the prior checkpoint.
    restored.terminate();restored.wait(timeout=5)
    _,base,token,_=host(['--resume',str(new_directory)])
    recovery=request(base,'/state',token=token)
    assert recovery['round']['phase']=='PREPARING'
    assert recovery['round']['round']==3
    assert all(not m['played'] for m in recovery['game']['matches'])
    result={'result':'PASS','players':4,'originalPreparationPanel':True,'originalLineupPanel':True,'originalMatchPanel':True,'consecutiveRounds':2,'fourthPlayerBarrier':True,'sharedResults':True,'saveResume':True,'interruptedRoundRecovery':'restart from preparation','screenshots':['analysis/career-preparation-1.png','analysis/career-game-1.png']}
    (ROOT/'analysis/career-session-result.json').write_text(json.dumps(result,indent=2))
    print(json.dumps(result,indent=2))
finally:
    for proc in processes:
        if proc.poll() is None:
            proc.terminate()
            try:proc.wait(timeout=5)
            except subprocess.TimeoutExpired:proc.kill();proc.wait()
