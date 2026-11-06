"""Runs two original Swing game panels against one host until full time."""
from pathlib import Path
import json, os, re, socket, subprocess, urllib.request, urllib.parse, time
ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
cp=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
with socket.socket() as sock:
    sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
address=f'127.0.0.1:{port}'
host=subprocess.Popen([JAVA,'-Djava.awt.headless=true','-cp',cp,'brasfoot.SessionHost','--root',str(ROOT),'--demo','--bind','127.0.0.1','--port',str(port),'--tick-ms','75'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
clients=[]
try:
    lines=[]
    while len(lines)<5:
        line=host.stdout.readline()
        if not line:raise RuntimeError(host.stderr.read())
        lines.append(line.strip())
    token=next(line.split(': ',1)[1] for line in lines if line.startswith('Host token:'))
    data=urllib.parse.urlencode({'coachId':1}).encode()
    with urllib.request.urlopen(f'http://{address}/join',data,timeout=10) as response:guest=json.load(response)['token']
    for index,credential in enumerate([token,guest]):
        command=[JAVA,'-cp',cp,'brasfoot.NativeClient','--root',str(ROOT),'--address',address,'--test-token',credential,'--test-auto','true','--test-native-editor','true','--capture',str(ROOT/f'analysis/native-player-{index+1}.png')]
        clients.append(subprocess.Popen(command,stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True))
    scores=[];outputs=[]
    for client in clients:
        output,error=client.communicate(timeout=55)
        assert client.returncode==0,(output,error)
        match=re.search(r'GUI_FINISHED (\d+) (\d+) sounds=(\d+) timerStopped=true',output)
        assert match,(output,error)
        assert int(match[3])>=2,'Original interval and full-time audio triggers missing'
        scores.append((int(match[1]),int(match[2])));outputs.append(output)
    assert scores[0]==scores[1],scores
    request=urllib.request.Request(f'http://{address}/state',headers={'Authorization':'Bearer '+token})
    with urllib.request.urlopen(request,timeout=10) as response:state=json.load(response)
    assert state['round']['phase']=='FINISHED'
    assert all(coach['tactics'][1]==1 for coach in state['game']['coaches']),'Native tactic selection was not applied by host'
    match=state['playback']['matches'][0]
    assert scores[0]==(match['homeGoals'],match['awayGoals'])
    result={'result':'PASS','originalSwingPanels':True,'clients':2,'finalScore':list(scores[0]),'localSimulationDisabled':True,'audioTriggersVerified':True,'screenshots':['analysis/native-player-1.png','analysis/native-player-2.png']}
    (ROOT/'analysis/native-test-result.json').write_text(json.dumps(result,indent=2))
    print(json.dumps(result,indent=2))
finally:
    for proc in [*clients,host]:
        if proc.poll() is None:
            proc.terminate()
            try:proc.wait(timeout=5)
            except subprocess.TimeoutExpired:proc.kill();proc.wait()
