"""Checks host startup with foreign names and stable club-asset selection."""
from pathlib import Path
import argparse, base64, json, os, socket, subprocess, time, urllib.request

ROOT=Path(__file__).resolve().parents[2]
parser=argparse.ArgumentParser();parser.add_argument('--jar',type=Path);args=parser.parse_args()
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
ENTRY=[JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost']
if args.jar:ENTRY=[JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(args.jar.resolve().parent/'host-ui-agent.jar'),'-cp',os.pathsep.join(map(str,[args.jar.resolve(),*sorted((args.jar.resolve().parent/'lib').glob('*.jar'))])),'brasfoot.SessionHost']

def start(extra,expected):
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
    log=ROOT/f'analysis/career-country-{port}.log'
    output=log.open('w',encoding='utf-8')
    proc=subprocess.Popen([*ENTRY,'--root',str(ROOT),'--bind','127.0.0.1','--port',str(port),'--tick-ms','10000','--career',*extra],stdout=output,stderr=subprocess.STDOUT)
    try:
        deadline=time.monotonic()+35;token=None
        while time.monotonic()<deadline:
            text=log.read_text(encoding='utf-8',errors='replace')
            for line in text.splitlines():
                if line.startswith('Host token: '):token=line.split(': ',1)[1];break
            if token:break
            assert proc.poll() is None,text[-3000:]
            time.sleep(.1)
        assert token,'Career host startup timed out'
        request=urllib.request.Request(f'http://127.0.0.1:{port}/state',headers={'Authorization':'Bearer '+token})
        with urllib.request.urlopen(request,timeout=10) as response:state=json.load(response)
        coaches=state['game']['coaches'];assets={row['asset']:row for row in state['game']['visualClubs']}
        for asset,club_name,country in expected:
            coach=next(row for row in coaches if row['club']==club_name)
            upcoming={row['coachId'] for row in state['game']['upcoming']}
            assert coach['hasMatch'] or coach['coachId'] in upcoming,(club_name,'no current or upcoming fixture')
            assert assets[asset]['country']==country,(asset,assets.get(asset))
        return {'coaches':[row['club'] for row in coaches],'assets':[asset for asset,_,_ in expected]}
    finally:
        proc.terminate()
        try:proc.wait(timeout=8)
        except subprocess.TimeoutExpired:proc.kill();proc.wait()
        output.close()

name_result=start(['--clubs','Real Madrid'],[('realmadrid_esp','Real Madrid',65)])
names=base64.urlsafe_b64encode(('Host'+chr(10)+'Guest').encode()).decode().rstrip('=')
asset_result=start(['--club-assets','corinthians_bra,realmadrid_esp','--coach-names',names],[('corinthians_bra','Corinthians',29),('realmadrid_esp','Real Madrid',65)])
result={'result':'PASS','foreignNameCLI':name_result,'stableAssetCLI':asset_result}
(ROOT/'analysis/career-country-selection-result.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2))
