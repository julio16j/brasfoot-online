"""Validates the graphical Create Session button and a second original-game client."""
from pathlib import Path
import test_logs
import json, os, re, socket, subprocess, time, urllib.request, urllib.parse
ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
cp=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
address=f'127.0.0.1:{port}'
PACKAGE=Path(os.environ['BRASFOOT_PACKAGE_ROOT']).resolve() if os.environ.get('BRASFOOT_PACKAGE_ROOT') else None
GAME_ROOT=PACKAGE or ROOT
ENTRY=[JAVA,'-jar',str(PACKAGE/'brasfoot-online.jar')] if PACKAGE else [JAVA,'-cp',cp,'brasfoot.NativeClient']
host=subprocess.Popen([*ENTRY,'--root',str(GAME_ROOT),'--port',str(port),'--test-create','true','--test-auto','true','--capture',str(ROOT/'analysis/native-host-created.png')],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
guest=None
try:
    deadline=time.monotonic()+25
    while True:
        try:
            with urllib.request.urlopen(f'http://{address}/session',timeout=1) as response:info=json.load(response)
            break
        except OSError:
            if host.poll() is not None:raise RuntimeError(host.communicate())
            assert time.monotonic()<deadline,'Graphical host did not create its session'
            time.sleep(.2)
    assert info['coaches'][0]['claimed']
    data=urllib.parse.urlencode({'coachId':1}).encode()
    with urllib.request.urlopen(f'http://{address}/join',data,timeout=5) as response:token=json.load(response)['token']
    guest=subprocess.Popen([*ENTRY,'--root',str(GAME_ROOT),'--address',address,'--test-token',token,'--test-auto','true','--capture',str(ROOT/'analysis/native-guest-created.png')],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
    output,error=guest.communicate(timeout=40)
    assert guest.returncode==0,(output,error)
    assert 'GUI_FINISHED' in output,(output,error)
    time.sleep(.6)
    request=urllib.request.Request(f'http://{address}/state',headers={'Authorization':'Bearer '+token})
    with urllib.request.urlopen(request,timeout=5) as response:state=json.load(response)
    assert state['round']['phase']=='FINISHED'
    assert (ROOT/'analysis/native-host-created.png').exists()
    result={'result':'PASS','packagedClient':bool(PACKAGE),'graphicalHostCreatedSession':True,'guestFinishedWithNativeInterface':True,'hostFinishedWithNativeInterface':True,'savedOnHost':state['checkpoint'],'final':state['playback']['matches'][0]}
    (ROOT/'analysis/native-create-result.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2))
finally:
    for proc in [guest,host]:
        if proc and proc.poll() is None:
            proc.terminate()
            try:proc.wait(timeout=5)
            except subprocess.TimeoutExpired:proc.kill();proc.wait()
    # NativeClient shutdown hook owns and stops its child host.
