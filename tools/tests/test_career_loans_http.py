"""Loan listing over HTTP and recovery in a fresh host JVM."""
from pathlib import Path
import json, os, socket, subprocess, time, urllib.request, urllib.parse, urllib.error

ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
processes=[]

def request(base,path,token=None,data=None,expected=200):
    req=urllib.request.Request(base+path,None if data is None else urllib.parse.urlencode(data).encode(),{} if token is None else {'Authorization':'Bearer '+token})
    try:
        with urllib.request.urlopen(req,timeout=30) as response:code=response.status;body=json.load(response)
    except urllib.error.HTTPError as error:code=error.code;body=json.load(error)
    assert code==expected,(path,code,body)
    return body

def start(extra):
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
    output=ROOT/f'analysis/loan-host-{port}.log'
    log=output.open('w',encoding='utf-8')
    proc=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--bind','127.0.0.1','--port',str(port),'--tick-ms','1000',*extra],stdout=log,stderr=subprocess.STDOUT)
    processes.append(proc);deadline=time.monotonic()+30
    while time.monotonic()<deadline:
        text=output.read_text(encoding='utf-8',errors='replace')
        values={line.split(': ',1)[0]:line.split(': ',1)[1] for line in text.splitlines() if ': ' in line}
        if 'Host token' in values:return f'http://127.0.0.1:{port}',values['Host token'],Path(values['Session directory'])
        assert proc.poll() is None,text[-4000:]
        time.sleep(.1)
    raise AssertionError('Host failed to start')

try:
    base,token,directory=start(['--career','--clubs','Corinthians,Palmeiras'])
    state=request(base,'/state',token)
    revision=state['round']['coaches'][0]['revision'];round_no=state['round']['round']
    snapshot=request(base,'/management',token)
    player=next(row for row in snapshot['players'] if not row['loaned'])
    request(base,'/management',token,{'round':round_no,'revision':revision,'action':'loan-list','playerId':player['playerId'],'available':'true'})
    snapshot=request(base,'/management',token)
    assert next(row for row in snapshot['players'] if row['playerId']==player['playerId'])['loanAvailable']
    process=processes[-1];process.terminate();process.wait(timeout=10)
    base,token,resumed=start(['--resume',str(directory)])
    snapshot=request(base,'/management',token)
    recovered=next(row for row in snapshot['players'] if row['playerId']==player['playerId'])
    assert recovered['loanAvailable'],recovered
    print(json.dumps({'result':'PASS','checks':['authenticated loan-list command mutates the owned roster','loan availability is saved and restored in a separate host JVM']}))
finally:
    for process in processes:
        if process.poll() is None:process.terminate();process.wait(timeout=10)
