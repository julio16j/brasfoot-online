"""Exercises special-role IDs and atomic rejection through SessionHost."""
from pathlib import Path
import argparse, json, os, socket, subprocess, time, urllib.error, urllib.parse, urllib.request

ROOT=Path(__file__).resolve().parents[2]
parser=argparse.ArgumentParser();parser.add_argument('--jar',type=Path);args=parser.parse_args()
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
ENTRY=[JAVA,'-Djava.awt.headless=true','-cp',CP,'brasfoot.SessionHost']
if args.jar:ENTRY=[JAVA,'-cp',os.pathsep.join(map(str,[args.jar.resolve(),*sorted((args.jar.resolve().parent/'lib').glob('*.jar'))])),'brasfoot.SessionHost']
with socket.socket() as sock:sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
address=f'127.0.0.1:{port}'
host=subprocess.Popen([*ENTRY,'--root',str(ROOT),'--demo','--bind','127.0.0.1','--port',str(port),'--tick-ms','10000'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)

def call(path,token=None,form=None,allow_error=False):
    data=urllib.parse.urlencode(form).encode() if form is not None else None;headers={'Authorization':'Bearer '+token} if token else {}
    try:
        with urllib.request.urlopen(urllib.request.Request(f'http://{address}{path}',data=data,headers=headers),timeout=10) as response:return response.status,json.load(response)
    except urllib.error.HTTPError as error:
        body=json.loads(error.read().decode())
        if allow_error:return error.code,body
        raise AssertionError((path,error.code,body))

try:
    def startup(process):
        lines=[]
        while len(lines)<5:
            line=process.stdout.readline()
            if not line:raise RuntimeError(process.stderr.read())
            lines.append(line.strip())
        return lines
    lines=startup(host)
    token=next(line.split(': ',1)[1] for line in lines if line.startswith('Host token:'))
    session_dir=next(line.split(': ',1)[1] for line in lines if line.startswith('Session directory:'))
    before=call('/state',token)[1];round_state=before['round'];coach=next(row for row in before['game']['coaches'] if row['coachId']==before['viewerCoachId'])
    suggestion=coach['suggestion'];players={row['index']:row for row in coach['players']};starters=suggestion['starters'];bench=suggestion['bench']
    current_club=next(row for row in before['game']['visualClubs'] if row['clubId']==coach['clubId'])
    existing_roles=current_club['roles'];role_ids=[players[index]['playerId'] for index in starters[1:4]]+[existing_roles['falseNine'] if existing_roles['falseNine'] is not None else -1]
    base={'round':round_state['round'],'revision':next(row['revision'] for row in round_state['coaches'] if row['coachId']==coach['coachId']),
          'starters':','.join(map(str,starters)),'bench':','.join(map(str,bench)),'slots':'1,2,9,4,7,11,13,14,16,20,23',
          'tactics':','.join(map(str,coach['tactics']))}
    bad=dict(base);bad['roles']=','.join(map(str,[role_ids[0],2147483647,role_ids[2],role_ids[3]]))
    status,error=call('/lineup',token,bad,allow_error=True);assert status==400,(status,error)
    after=call('/state',token)[1]
    for key in ('round','game','playback','checkpoint'):assert after[key]==before[key],f'Invalid role request changed {key}'
    changed_false_nine=players[starters[0]]['playerId']
    if changed_false_nine==role_ids[3]:changed_false_nine=players[bench[0]]['playerId']
    blocked=dict(base);blocked['roles']=','.join(map(str,role_ids[:3]+[changed_false_nine]))
    blocked_status,blocked_error=call('/lineup',token,blocked,allow_error=True);assert blocked_status==400,(blocked_status,blocked_error)
    after=call('/state',token)[1]
    for key in ('round','game','playback','checkpoint'):assert after[key]==before[key],f'Unsupported false-nine request changed {key}'
    good=dict(base);good['roles']=','.join(map(str,role_ids));call('/lineup',token,good)
    confirmed=call('/state',token)[1];club_id=coach['clubId'];visual=next(row for row in confirmed['game']['visualClubs'] if row['clubId']==club_id)
    for key,value in zip(('freeKicks','captain','corners','falseNine'),role_ids):assert visual['roles'][key]==(None if value<0 else value),(key,visual['roles'][key],value)
    stale=dict(good);stale['revision']=base['revision'];stale_status,stale_error=call('/lineup',token,stale,allow_error=True);assert stale_status==409,(stale_status,stale_error)
    stale_after=call('/state',token)[1]
    for key in ('round','game','playback','checkpoint'):assert stale_after[key]==confirmed[key],f'Stale role request changed {key}'
    host.terminate();host.wait(timeout=8)
    host=subprocess.Popen([*ENTRY,'--root',str(ROOT),'--resume',session_dir,'--bind','127.0.0.1','--port',str(port),'--tick-ms','10000'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)
    resumed_lines=startup(host);resumed_token=next(line.split(': ',1)[1] for line in resumed_lines if line.startswith('Host token:'))
    resumed=call('/state',resumed_token)[1];resumed_club=next(row for row in resumed['game']['visualClubs'] if row['clubId']==club_id)
    for key,value in zip(('freeKicks','captain','corners','falseNine'),role_ids):assert resumed_club['roles'][key]==(None if value<0 else value),(key,resumed_club['roles'][key],value)
    result={'result':'PASS','invalidRoleStatus':status,'staleRevisionStatus':stale_status,'rejectionStateUnchanged':True,'roleIDsConfirmed':role_ids,'saveResumePreservedRoles':True,'packaged':bool(args.jar)}
    (ROOT/'analysis/special-roles-http-result.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2))
finally:
    if host.poll() is None:
        host.terminate()
        try:host.wait(timeout=5)
        except subprocess.TimeoutExpired:host.kill();host.wait()
