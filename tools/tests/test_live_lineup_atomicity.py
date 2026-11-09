"""Checks the live-lineup HTTP transaction against a real demo match."""
from pathlib import Path
import json, os, socket, subprocess, time, urllib.error, urllib.parse, urllib.request
import argparse

ROOT=Path(__file__).resolve().parents[2]
parser=argparse.ArgumentParser();parser.add_argument('--jar',type=Path);args=parser.parse_args()
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
ENTRY=[JAVA,'-cp',CP,'brasfoot.SessionHost']
if args.jar:ENTRY=[JAVA,'-cp',os.pathsep.join(map(str,[args.jar.resolve(),*sorted((args.jar.resolve().parent/'lib').glob('*.jar'))])),'brasfoot.SessionHost']
with socket.socket() as sock:
    sock.bind(('127.0.0.1',0));port=sock.getsockname()[1]
address=f'127.0.0.1:{port}'
host=subprocess.Popen([*ENTRY,'--root',str(ROOT),'--demo','--bind','127.0.0.1','--port',str(port),'--tick-ms','10000'],stdout=subprocess.PIPE,stderr=subprocess.PIPE,text=True)

def call(path,token=None,form=None,allow_error=False):
    data=urllib.parse.urlencode(form).encode() if form is not None else None
    headers={'Authorization':'Bearer '+token} if token else {}
    request=urllib.request.Request(f'http://{address}{path}',data=data,headers=headers)
    try:
        with urllib.request.urlopen(request,timeout=10) as response:return response.status,json.load(response)
    except urllib.error.HTTPError as error:
        body=json.loads(error.read().decode())
        if allow_error:return error.code,body
        raise AssertionError((path,error.code,body))

def wait_state(token,phase):
    deadline=time.monotonic()+18
    while time.monotonic()<deadline:
        state=call('/state',token)[1]
        if state['round']['phase']==phase:return state
        time.sleep(.1)
    raise AssertionError('Host did not reach '+phase)

try:
    lines=[]
    while len(lines)<5:
        line=host.stdout.readline()
        if not line:raise RuntimeError(host.stderr.read())
        lines.append(line.strip())
    token=next(line.split(': ',1)[1] for line in lines if line.startswith('Host token:'))
    guest=call('/join',form={'coachId':1})[1]['token']
    tokens=[token,guest]
    for credential in tokens:
        state=call('/state',credential)[1]
        coach=next(row for row in state['game']['coaches'] if row['coachId']==state['viewerCoachId'])
        round_state=state['round'];suggestion=coach['suggestion']
        form={'round':round_state['round'],'revision':next(row['revision'] for row in round_state['coaches'] if row['coachId']==coach['coachId']),
              'starters':','.join(map(str,suggestion['starters'])),'bench':','.join(map(str,suggestion['bench']))}
        call('/lineup',credential,form)
        state=call('/state',credential)[1];row=next(item for item in state['round']['coaches'] if item['coachId']==state['viewerCoachId'])
        call('/ready',credential,{'round':state['round']['round'],'revision':row['revision'],'ready':'true'})

    state=wait_state(token,'RUNNING')
    own=next(row for row in state['game']['coaches'] if row['coachId']==state['viewerCoachId'])
    players={p['index']:p for p in own['players']};lineup=own['liveLineup']
    starters=[next(index for index,p in players.items() if p['playerId']==row['playerId']) for row in lineup['starters']]
    bench=[next(index for index,p in players.items() if p['playerId']==row['playerId']) for row in lineup['bench']]
    slot_values=[row['slot'] for row in lineup['starters']]
    tactics=own['tactics'];active_rows=lineup['starters'];bank_rows=lineup['bench']
    field_out=[row for row in active_rows if row['position']!=0]
    field_in=[row for row in bank_rows if row['position']!=0]
    active_ids={row['playerId'] for row in active_rows};bank_ids={row['playerId'] for row in bank_rows}
    outsider=next(p for p in own['players'] if p['position']!=0 and p['eligible'] and p['playerId'] not in active_ids|bank_ids)
    first,second=field_out[:2];replacement=field_in[0]
    desired=starters[:]
    for row,repl in ((first,replacement),(second,outsider)):
        old_index=next(i for i,p in enumerate(players.values()) if p['playerId']==row['playerId'])
        new_index=next(i for i,p in players.items() if p['playerId']==repl['playerId'])
        desired[starters.index(old_index)]=new_index
    invalid_bank=[index for index in bench if players[index]['playerId']!=replacement['playerId']]
    invalid_bank += [next(i for i,p in players.items() if p['playerId']==first['playerId']),next(i for i,p in players.items() if p['playerId']==second['playerId'])]
    coach_id=own['coachId'];round_row=next(row for row in state['round']['coaches'] if row['coachId']==coach_id)
    bad={'round':state['round']['round'],'revision':round_row['revision'],'starters':','.join(map(str,desired)),
         'bench':','.join(map(str,invalid_bank)),'slots':','.join(map(str,slot_values)),'tactics':','.join(map(str,tactics))}
    status,error=call('/live-lineup',token,bad,allow_error=True)
    assert status in (400,409), (status,error)
    after=call('/state',token)[1]
    for key in ('round','game','playback','checkpoint'):
        assert after[key]==state[key],f'Rejected request changed {key}'

    legal_desired=starters[:];out=field_out[0];incoming=field_in[0]
    out_index=next(i for i,p in players.items() if p['playerId']==out['playerId']);in_index=next(i for i,p in players.items() if p['playerId']==incoming['playerId'])
    legal_desired[starters.index(out_index)]=in_index
    legal_bank=[i for i in bench if i!=in_index]+[out_index]
    current_revision=next(row['revision'] for row in after['round']['coaches'] if row['coachId']==coach_id)
    good={'round':after['round']['round'],'revision':current_revision,'starters':','.join(map(str,legal_desired)),
          'bench':','.join(map(str,legal_bank)),'slots':','.join(map(str,slot_values)),'tactics':','.join(map(str,tactics))}
    call('/live-lineup',token,good)
    final=call('/state',token)[1]
    assert final['playback']['sequence']==state['playback']['sequence']+1
    final_revision=next(row['revision'] for row in final['round']['coaches'] if row['coachId']==coach_id)
    assert final_revision==current_revision+1
    result={'result':'PASS','invalidRequestStatus':status,'rejectionStateUnchanged':True,'validSubstitutionApplied':True}
    (ROOT/'analysis/live-lineup-http-result.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2))
finally:
    if host.poll() is None:
        host.terminate()
        try:host.wait(timeout=5)
        except subprocess.TimeoutExpired:host.kill();host.wait()
