"""Runs the native academy promotion through the real host management endpoint."""
from pathlib import Path
import json, os, re, socket, subprocess, time, urllib.request, urllib.parse

ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
PACKAGE=ROOT/'dist/brasfoot-online.jar'
CLIENT_ENTRY=[JAVA,'-jar',str(PACKAGE)] if PACKAGE.is_file() else [JAVA,'-cp',CP,'brasfoot.NativeClient']
stamp=str(time.time_ns())
log_path=ROOT/f'analysis/youth-session-host-{stamp}.log'
capture=ROOT/f'analysis/youth-session-academy-{stamp}.png'
with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
address=f'127.0.0.1:{port}'
log=log_path.open('w',encoding='utf-8')
host=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--career','--clubs','Corinthians,Palmeiras','--bind','127.0.0.1','--port',str(port)],stdout=log,stderr=subprocess.STDOUT)
client=None;observer=None;resumed_host=None;resumed_log=None
def state(token):
    request=urllib.request.Request(f'http://{address}/state',headers={'Authorization':'Bearer '+token})
    with urllib.request.urlopen(request,timeout=10) as response:return json.load(response)
def state_at(server,token):
    request=urllib.request.Request(f'http://{server}/state',headers={'Authorization':'Bearer '+token})
    with urllib.request.urlopen(request,timeout=10) as response:return json.load(response)
try:
    deadline=time.monotonic()+45;token=None
    while token is None:
        output=log_path.read_text(encoding='utf-8',errors='replace')
        match=re.search(r'^Host token: (\S+)',output,re.M)
        if match:token=match.group(1);break
        if host.poll() is not None:raise RuntimeError(output[-4000:])
        assert time.monotonic()<deadline,'Career host did not start'
        time.sleep(.1)
    before=state(token);coach_id=before['viewerCoachId'];own=next(row for row in before['game']['coaches'] if row['coachId']==coach_id);club_id=own['clubId']
    other_coach=next(row for row in before['game']['coaches'] if row['coachId']!=coach_id);join_data=urllib.parse.urlencode({'coachId':other_coach['coachId']}).encode()
    with urllib.request.urlopen(f'http://{address}/join',data=join_data,timeout=10) as response:guest=json.load(response)['token']
    club=next(row for row in before['game']['visualClubs'] if row['clubId']==club_id);youth_ids={row['playerId'] for row in club['youth']};assert youth_ids,'Career host has no youth players'
    client=subprocess.Popen([*CLIENT_ENTRY,'--root',str(ROOT),'--address',address,'--test-token',token,'--test-youth','true','--test-youth-capture',str(capture)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    output,_=client.communicate(timeout=75)
    assert client.returncode==0,output
    promoted=re.search(r'YOUTH_PROMOTION_PASS playerId=(\d+)',output);assert promoted,output
    player_id=int(promoted.group(1));assert player_id in youth_ids,'UI selected a player outside the initial host academy'
    after=state(token);after_club=next(row for row in after['game']['visualClubs'] if row['clubId']==club_id)
    assert player_id not in {row['playerId'] for row in after_club['youth']},'Promoted identity remains in academy'
    assert player_id in {row['playerId'] for row in after_club['players']},'Promoted identity is absent from professional roster'
    assert after['checkpoint']>before['checkpoint'],'Host did not persist the promotion checkpoint'
    observer=subprocess.Popen([*CLIENT_ENTRY,'--root',str(ROOT),'--address',address,'--test-token',guest,'--test-youth-observe',str(player_id),'--test-youth-observe-club',str(club_id)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    observer_output,_=observer.communicate(timeout=45);assert observer.returncode==0,observer_output;assert f'YOUTH_OBSERVER_PASS playerId={player_id}' in observer_output,observer_output
    assert capture.is_file(),'Native academy screenshot was not captured'
    session_match=re.search(r'^Session directory: (.+)$',log_path.read_text(encoding='utf-8',errors='replace'),re.M);assert session_match,'Host did not publish its session directory'
    host.terminate();host.wait(timeout=10)
    with socket.socket() as sock:sock.bind(('127.0.0.1',0));resume_port=sock.getsockname()[1]
    resume_address=f'127.0.0.1:{resume_port}';resume_log_path=ROOT/f'analysis/youth-resumed-host-{stamp}.log';resumed_log=resume_log_path.open('w',encoding='utf-8')
    resumed_host=subprocess.Popen([JAVA,'-Dfile.encoding=UTF-8','-Djava.awt.headless=true','-javaagent:'+str(ROOT/'build/host-ui-agent.jar'),'-cp',CP,'brasfoot.SessionHost','--root',str(ROOT),'--resume',session_match.group(1).strip(),'--bind','127.0.0.1','--port',str(resume_port)],stdout=resumed_log,stderr=subprocess.STDOUT)
    deadline=time.monotonic()+45;resume_token=None
    while resume_token is None:
        resumed_text=resume_log_path.read_text(encoding='utf-8',errors='replace');resume_match=re.search(r'^Host token: (\S+)',resumed_text,re.M)
        if resume_match:resume_token=resume_match.group(1);break
        if resumed_host.poll() is not None:raise RuntimeError(resumed_text[-4000:])
        assert time.monotonic()<deadline,'Host recovery timed out';time.sleep(.1)
    recovered=state_at(resume_address,resume_token);recovered_club=next(row for row in recovered['game']['visualClubs'] if row['clubId']==club_id)
    assert player_id in {row['playerId'] for row in recovered_club['players']} and player_id not in {row['playerId'] for row in recovered_club['youth']},'Restart lost the promoted identity or duplicated it in the academy'
    result={'result':'PASS','packagedClient':PACKAGE.is_file(),'viewerCoachId':coach_id,'observerCoachId':other_coach['coachId'],'promotedPlayerId':player_id,'initialYouth':len(youth_ids),'remainingYouth':len(after_club['youth']),'professionalCount':len(after_club['players']),'checkpointBefore':before['checkpoint'],'checkpointAfter':after['checkpoint'],'checkpointAfterRestart':recovered['checkpoint'],'secondClientProjection':'PASS','separateHostRestart':'PASS','capture':str(capture.relative_to(ROOT))}
    (ROOT/f'analysis/youth-session-result-{stamp}.json').write_text(json.dumps(result,indent=2),encoding='utf-8')
    print(json.dumps(result,indent=2))
finally:
    if client and client.poll() is None:client.terminate()
    if observer and observer.poll() is None:observer.terminate()
    if host.poll() is None:host.terminate()
    if resumed_host and resumed_host.poll() is None:resumed_host.terminate()
    for process in (client,observer,host,resumed_host):
        if process:
            try:process.wait(timeout=8)
            except subprocess.TimeoutExpired:process.kill();process.wait()
    log.close()
    if resumed_log:resumed_log.close()
