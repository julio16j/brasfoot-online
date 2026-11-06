"""End-to-end test: real bytecode, HTTP clients, host-only checkpoints."""
from pathlib import Path
import test_logs
import concurrent.futures, json, os, subprocess, sys, time, urllib.request, urllib.parse, urllib.error, socket
ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
cp=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
with socket.socket() as s:
    s.bind(('127.0.0.1',0)); port=s.getsockname()[1]
base=f'http://127.0.0.1:{port}'
viewers=[]
proc=subprocess.Popen([JAVA,'-Djava.awt.headless=true','-cp',cp,'brasfoot.SessionHost','--root',str(ROOT),'--demo','--bind','127.0.0.1','--port',str(port),'--tick-ms','25'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
def request(path,data=None,token=None):
    headers={} if token is None else {'Authorization':'Bearer '+token}
    body=None if data is None else urllib.parse.urlencode(data).encode()
    try:
        with urllib.request.urlopen(urllib.request.Request(base+path,body,headers),timeout=10) as r:
            return r.status,json.load(r)
    except urllib.error.HTTPError as e:
        return e.code,json.load(e)
def status(expected,path,data=None,token=None):
    code,body=request(path,data,token)
    assert code==expected,(path,code,body)
    return body
try:
    lines=[]
    # Each line is emitted after bind/start; EOF means startup failed.
    while len(lines)<5:
        line=proc.stdout.readline()
        if not line: raise RuntimeError(proc.stderr.read())
        lines.append(line.strip())
    host_token=next(x.split(': ',1)[1] for x in lines if x.startswith('Host token:'))
    directory=Path(next(x.split(': ',1)[1] for x in lines if x.startswith('Session directory:')))
    info=status(200,'/session')
    assert len(info['coaches'])==2
    status(401,'/state')
    status(409,'/join',{'coachId':0})
    guest=status(201,'/join',{'coachId':1})['token']
    status(409,'/join',{'coachId':1})
    state=status(200,'/state',token=host_token)
    status(409,'/ready',{'round':1,'revision':0,'ready':'true'},host_token)
    status(400,'/lineup',{'round':1,'revision':0,'starters':','.join(['0']*11)},host_token)
    for coach,token in zip(state['game']['coaches'],[host_token,guest]):
        eligible=coach['suggestion']['starters']; bench=coach['suggestion']['bench']
        assert len(eligible)>=11
        state=status(200,'/lineup',{'round':1,'revision':0,'starters':','.join(map(str,eligible[:11])),'bench':','.join(map(str,bench))},token)
    # Multiple edits before Ready, independent of the other coach's revision.
    coach=state['game']['coaches'][0]
    eligible=coach['suggestion']['starters']; bench=coach['suggestion']['bench']
    state=status(200,'/lineup',{'round':1,'revision':1,'starters':','.join(map(str,eligible[:11])),'bench':','.join(map(str,bench))},host_token)
    with concurrent.futures.ThreadPoolExecutor() as executor:
        results=list(executor.map(lambda item:request('/ready',{'round':1,'revision':item[1],'ready':'true'},item[0]),[(host_token,2),(guest,1)]))
    assert all(code==200 for code,_ in results),results
    state=status(200,'/state',token=host_token)
    assert state['round']['phase']=='RUNNING'
    assert state['round']['roundLocks']==1
    status(409,'/ready',{'round':1,'revision':2,'ready':'true'},guest)
    status(409,'/lineup',{'round':1,'revision':3,'starters':','.join(map(str,eligible[:11]))},host_token)
    viewers=[]
    for token in [host_token,guest]:
        viewers.append(subprocess.Popen([sys.executable,str(ROOT/'tools/client.py'),base,'--token='+token,'--watch'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True,encoding='utf-8',env={**os.environ,'PYTHONIOENCODING':'utf-8'}))
    seen_running=False; seen_half=False; halftime_confirmed=False; last_sequence=-1; deadline=time.monotonic()+20
    while True:
        state=status(200,'/state',token=host_token)
        live=state['playback']
        assert live['sequence']>=last_sequence
        last_sequence=live['sequence']
        seen_running |= state['round']['phase']=='RUNNING'
        seen_half |= live['status'] in ('HALF_TIME','SECOND_HALF','FINISHED')
        if state['round']['phase']=='HALF_TIME' and not halftime_confirmed:
            round_state=state['round']
            coaches=round_state['coaches']
            for token,coach_state in zip([host_token,guest],coaches):
                status(200,'/halftime-ready',{'round':round_state['round'],'revision':coach_state['revision'],'ready':'true'},token)
            halftime_confirmed=True
        if state['round']['phase']=='FAILED':raise AssertionError(live)
        if state['round']['phase']=='FINISHED':break
        assert time.monotonic()<deadline,'Match did not finish'
        time.sleep(0.025)
    assert seen_running and seen_half
    assert state['round']['roundLocks']==1
    assert live['minute']>=90 and live['status']=='FINISHED'
    final_guest=status(200,'/state',token=guest)
    assert final_guest['playback']==live,'Players received different final results'
    for match in live['matches']:
        assert match['played']
        for side in ['home','away']:
            goals=sum(e['type']==1 and e['club']==match[side] for e in match['events'])
            assert goals==match[side+'Goals'],(side,goals,match)
    for viewer in viewers:
        output,error=viewer.communicate(timeout=5)
        assert viewer.returncode==0,(output,error)
        assert 'FIM DE JOGO' in output and ('1o tempo' in output or '2o tempo' in output),output
        for match in live['matches']:
            assert f"{match['home']} {match['homeGoals']} x {match['awayGoals']} {match['away']}" in output
    manifest=json.loads((directory/'session.json').read_text(encoding='utf-8'))
    assert manifest['round']['phase']=='FINISHED'
    assert manifest['playback']['status']==live['status']
    assert manifest['playback']['minute']==live['minute']
    assert len(manifest['playback']['matches'])==len(live['matches'])
    for saved_match,live_match in zip(manifest['playback']['matches'],live['matches']):
        for field in ['homeGoals','awayGoals','played']:
            assert saved_match[field]==live_match[field],(field,saved_match,live_match)
    assert (directory/manifest['save']).stat().st_size>0
    restored=subprocess.run([JAVA,'-Djava.awt.headless=true','-cp',cp,'brasfoot.Inspect',str(directory/manifest['save'])],capture_output=True,text=True,check=True)
    saved=json.loads(restored.stdout)
    assert saved['matches'][0]['played']
    assert saved['matches'][0]['homeGoals']==live['matches'][0]['homeGoals']
    assert saved['matches'][0]['awayGoals']==live['matches'][0]['awayGoals']
    result={'result':'PASS','checks':['join and exclusive coach slots','authenticated commands','lineup validation','unlimited edits before ready','independent coach revisions','concurrent ready locks once','commands rejected after lock','shared live clock and events','both terminal clients render full time','matching final results','final score reloaded from host save'],'session':str(directory),'simulationImplemented':True,'final':live}
    (ROOT/'analysis/http-test-result.json').write_text(json.dumps(result,indent=2))
    print(json.dumps(result,indent=2))
finally:
    for viewer in viewers:
        if viewer.poll() is None:
            viewer.terminate()
            try:viewer.wait(timeout=5)
            except subprocess.TimeoutExpired:viewer.kill();viewer.wait()
    proc.terminate()
    try: proc.wait(timeout=5)
    except subprocess.TimeoutExpired: proc.kill(); proc.wait()

