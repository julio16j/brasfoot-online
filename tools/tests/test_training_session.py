"""Native training: real client mouse commands, HTTP rejection and separate-JVM recovery."""
from pathlib import Path
import argparse, json, os, socket, subprocess, time, urllib.request, urllib.parse, urllib.error

ROOT = Path(__file__).resolve().parents[2]
parser = argparse.ArgumentParser()
parser.add_argument('--packaged', action='store_true')
args = parser.parse_args()
OUT = ROOT / ('analysis/validation-training-session-jar' if args.packaged else 'analysis/validation-training-session')
OUT.mkdir(parents=True, exist_ok=True)
JAVA = r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
CP = os.pathsep.join(map(str, [ROOT/'dist/brasfoot-online.jar', *sorted((ROOT/'dist/lib').glob('*.jar'))] if args.packaged else [ROOT/'build/classes', ROOT/'analysis/brasfoot.jar', *sorted((ROOT/'analysis/lib').glob('*.jar'))]))
AGENT = ROOT / ('dist/host-ui-agent.jar' if args.packaged else 'build/host-ui-agent.jar')
processes = []
checks = []

def request(base, path, token=None, fields=None, expected=200):
    req = urllib.request.Request(base+path, None if fields is None else urllib.parse.urlencode(fields).encode(), {} if token is None else {'Authorization': 'Bearer '+token})
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            code, body = response.status, json.load(response)
    except urllib.error.HTTPError as error:
        code, body = error.code, json.load(error)
    assert code == expected, (path, code, body)
    return body

def start(extra):
    with socket.socket() as sock:
        sock.bind(('127.0.0.1', 0)); port = sock.getsockname()[1]
    log = (OUT/f'host-errors-{len(processes)}.log').open('w', encoding='utf-8')
    proc = subprocess.Popen([JAVA, '-Dfile.encoding=UTF-8', '-Djava.awt.headless=true', '-javaagent:'+str(AGENT), '-cp', CP, 'brasfoot.SessionHost', '--root', str(ROOT), '--bind', '127.0.0.1', '--port', str(port), '--tick-ms', '1000', *extra], stdout=subprocess.PIPE, stderr=log, text=True, encoding='utf-8')
    processes.append(proc)
    info = {}
    while 'Host token' not in info:
        line = proc.stdout.readline()
        assert line, 'Host failed; inspect isolated host error log'
        if ': ' in line:
            key, value = line.strip().split(': ', 1); info[key] = value
    return proc, f'http://127.0.0.1:{port}', info['Host token'], Path(info['Session directory'])

def coach(state, index):
    return state['game']['coaches'][index]

def club(state, index):
    return next(row for row in state['game']['visualClubs'] if row['clubId'] == coach(state, index)['clubId'])

def fields(state, index):
    row = state['round']['coaches'][index]
    return {'round': state['round']['round'], 'revision': row['revision']}

def command(base, token, index, change, expected=200, state=None):
    current = state or request(base, '/state', token)
    return request(base, '/management', token, {**fields(current, index), **change}, expected)

def gui(base, token, assistant, focus, label):
    result = subprocess.run([JAVA, '-cp', CP, 'brasfoot.TrainingClientWindowTest', str(ROOT), base, token, str(assistant).lower(), str(focus), str(OUT/label)], capture_output=True, text=True, timeout=50)
    (OUT/f'{label}.log').write_text(result.stdout+result.stderr, encoding='utf-8')
    assert result.returncode == 0, result.stdout+result.stderr
    print(result.stdout.strip(), flush=True)

try:
    process, base, host, directory = start(['--career', '--clubs', 'Corinthians,Palmeiras'])
    initial = request(base, '/state', host)
    guest = request(base, '/join', fields={'coachId': coach(initial, 1)['coachId']}, expected=201)['token']
    gui(base, host, False, 1, 'host-window')
    gui(base, guest, True, 0, 'guest-window')
    state = request(base, '/state', guest)
    assert club(state, 0)['trainingAssistant'] is False and club(state, 0)['trainingFocus'] == 1
    assert club(state, 1)['trainingAssistant'] is True and club(state, 1)['trainingFocus'] == 0
    checks.append('two actual NativeClient dialogs send distinct combinations; other client observes both')
    checkpoint = state['checkpoint']
    request(base, '/management', 'invalid', {**fields(state, 0), 'action': 'training-assistant', 'enabled': 'true'}, expected=401)
    for invalid in ['TRUE', '1', '', 'yes']:
        command(base, host, 0, {'action': 'training-assistant', 'enabled': invalid}, expected=400)
    command(base, host, 0, {'action': 'training-assistant'}, expected=400)
    command(base, host, 0, {'action': 'training-assistant', 'enabled': 'true'}, expected=409, state=initial)
    assert request(base, '/state', host)['checkpoint'] == checkpoint
    # An arbitrary form clubId can never redirect a command to another coach's club.
    command(base, guest, 1, {'action': 'training-assistant', 'enabled': 'false', 'clubId': coach(state, 0)['clubId']})
    seen = request(base, '/state', host)
    assert club(seen, 0)['trainingAssistant'] is False and club(seen, 1)['trainingAssistant'] is False
    command(base, guest, 1, {'action': 'training-assistant', 'enabled': 'true'})
    checks.append('strict boolean, authentication, obsolete revision and authenticated-club authority')
    process.terminate(); process.wait(timeout=10)
    process, base, host, resumed = start(['--resume', str(directory)])
    state = request(base, '/state', host)
    guest = request(base, '/join', fields={'coachId': coach(state, 1)['coachId']}, expected=201)['token']
    assert club(state, 0)['trainingAssistant'] is False and club(state, 0)['trainingFocus'] == 1
    assert club(state, 1)['trainingAssistant'] is True and club(state, 1)['trainingFocus'] == 0
    gui(base, host, False, 1, 'resumed-window')
    checks.append('separate host JVM and client reconnection retain confirmed values')
    state = request(base, '/state', host)
    suggestion = coach(state, 0)['suggestion']
    state = request(base, '/lineup', host, {**fields(state, 0), 'starters': ','.join(map(str, suggestion['starters'])), 'bench': ','.join(map(str, suggestion['bench']))})
    state = request(base, '/ready', host, {**fields(state, 0), 'ready': 'true'})
    command(base, host, 0, {'action': 'training-assistant', 'enabled': 'true'}, expected=409)
    state = request(base, '/ready', host, {**fields(state, 0), 'ready': 'false'})
    checks.append('Ready rejects training until withdrawal')
    # Own disposable manifest only: provoke the real atomic replacement failure.
    manifest = resumed/'session.json'; backup = resumed/'session.validation-backup'
    assert ROOT.resolve() in manifest.resolve().parents
    manifest.replace(backup); manifest.mkdir()
    try:
        command(base, host, 0, {'action': 'training-assistant', 'enabled': 'true'}, expected=503)
        failed = request(base, '/state', host)
        assert failed['storageFailed'] and club(failed, 0)['trainingAssistant'] is False
        command(base, host, 0, {'action': 'training-assistant', 'enabled': 'true'}, expected=409)
    finally:
        manifest.rmdir(); backup.replace(manifest)
    process.terminate(); process.wait(timeout=10)
    process, base, host, _ = start(['--resume', str(resumed)])
    state = request(base, '/state', host)
    assert club(state, 0)['trainingAssistant'] is False and club(state, 0)['trainingFocus'] == 1
    checks.append('storage failure returns error, rolls back training, pauses host; new JVM uses last manifest')
    guest = request(base, '/join', fields={'coachId': coach(state, 1)['coachId']}, expected=201)['token']
    for index, token in enumerate([host, guest]):
        state = request(base, '/state', token)
        suggestion = coach(state, index)['suggestion']
        state = request(base, '/lineup', token, {**fields(state, index), 'starters': ','.join(map(str, suggestion['starters'])), 'bench': ','.join(map(str, suggestion['bench']))})
        request(base, '/ready', token, {**fields(state, index), 'ready': 'true'})
    command(base, host, 0, {'action': 'training-assistant', 'enabled': 'true'}, expected=409)
    checks.append('running phase rejects management without changing training')
    (OUT/'result.json').write_text(json.dumps({'result': 'PASS', 'checks': checks}, indent=2), encoding='utf-8')
    print(json.dumps({'result': 'PASS', 'checks': checks}, indent=2))
finally:
    for proc in processes:
        if proc.poll() is None:
            proc.terminate(); proc.wait(timeout=10)
