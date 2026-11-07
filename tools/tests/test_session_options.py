"""Native settings: API authority, atomic validation, recovery and original UI."""
from pathlib import Path
import json, os, socket, subprocess, sys, time, urllib.request, urllib.parse, urllib.error

ROOT = Path(__file__).resolve().parents[2]
JAVA = r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
subprocess.run([sys.executable, str(ROOT/'tools/build_adapter.py')], check=True)
CP = os.pathsep.join(map(str, [ROOT/'build/classes', ROOT/'analysis/brasfoot.jar', *sorted((ROOT/'analysis/lib').glob('*.jar'))]))
for test in ['SessionOptionsTest', 'NonHumanPlaybackTest', 'NativeSessionOptionsTest']:
    subprocess.run([JAVA, '-cp', CP, 'brasfoot.'+test, str(ROOT)], check=True, timeout=60)

def start(extra):
    with socket.socket() as sock:
        sock.bind(('127.0.0.1', 0)); port = sock.getsockname()[1]
    process = subprocess.Popen([JAVA, '-Djava.awt.headless=true', '-cp', CP, 'brasfoot.SessionHost', '--root', str(ROOT), '--bind', '127.0.0.1', '--port', str(port), *extra], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    info = {}
    while 'Host token' not in info:
        line = process.stdout.readline()
        if not line:
            raise AssertionError(process.stderr.read())
        if ': ' in line:
            key, value = line.strip().split(': ', 1); info[key] = value
    return process, f'http://127.0.0.1:{port}', info

def request(base, path, token=None, fields=None):
    headers = {'Authorization': 'Bearer '+token} if token else {}
    data = urllib.parse.urlencode(fields).encode() if fields is not None else None
    try:
        with urllib.request.urlopen(urllib.request.Request(base+path, data, headers), timeout=15) as response:
            return response.status, json.load(response)
    except urllib.error.HTTPError as error:
        return error.code, json.load(error)

process, base, info = start(['--demo'])
try:
    host = info['Host token']; directory = Path(info['Session directory'])
    code, state = request(base, '/state', host); assert code == 200
    options = state['sessionOptions']; assert options['effectiveTickMillis'] == 220 and options['speedSource'] == 'NATIVE'
    assert len(options['values']) == 22 and len(options['nonHumanCompetitions']) == 21
    code, guest = request(base, '/join', fields={'coachId': 1}); assert code == 201
    guest = guest['token']
    def edit(expected, changes, revision=0, token=host):
        code, body = request(base, '/session-options', token, {'optionsRevision': revision, 'changes': changes if isinstance(changes, str) else json.dumps(changes)})
        assert code == expected, (code, body)
        return body
    edit(401, {'soundsEnabled': False}, token=None)
    assert edit(403, {'soundsEnabled': False}, token=guest)['code'] == 'HOST_REQUIRED'
    edit(400, {'soundsEnabled': 0})
    edit(400, {'soundsEnabled': False, 'humanSpeedIndex': 6})
    edit(400, '{"soundsEnabled":false,"soundsEnabled":true}')
    edit(400, {'playerListPalette': ['#000000']})
    edit(400, {'nonHumanCompetitionPlayback': {'21': True}})
    assert edit(409, {'autoSaveMode': 2})['code'] == 'OPTION_NOT_SUPPORTED'
    code, state = request(base, '/state', host); assert state['sessionOptions']['revision'] == 0
    state = edit(200, {'soundsEnabled': False, 'humanSpeedIndex': 3}); assert state['sessionOptions']['revision'] == 1
    assert state['sessionOptions']['effectiveTickMillis'] == 450
    assert edit(409, {'soundsEnabled': True})['code'] == 'OPTIONS_REVISION_CONFLICT'
    state = edit(200, {'soundsEnabled': False}, revision=1); assert state['sessionOptions']['revision'] == 1
    state = edit(200, {'openLineupAtHalfTime': False}, revision=1)
    assert state['sessionOptions']['pendingValues'] == {'openLineupAtHalfTime': False}
    code, observed = request(base, '/state', guest); assert code == 200 and not observed['sessionOptions']['canEdit']
    assert observed['sessionOptions']['values'] == state['sessionOptions']['values']
    manifest = json.loads((directory/'session.json').read_text(encoding='utf-8'))
    assert manifest['sessionOptions']['revision'] == 2 and 'canEdit' not in manifest['sessionOptions']
finally:
    process.terminate(); process.wait(timeout=10)

process, base, info = start(['--resume', str(directory), '--tick-ms', '25'])
try:
    host = info['Host token']; code, state = request(base, '/state', host); assert code == 200
    options = state['sessionOptions']; assert options['revision'] == 2 and options['values']['humanSpeedIndex'] == 3
    assert options['pendingValues'] == {'openLineupAtHalfTime': False}
    assert options['speedSource'] == 'CLI_OVERRIDE' and options['effectiveTickMillis'] == 25
    code, error = request(base, '/session-options', host, {'optionsRevision': 2, 'changes': '{"humanSpeedIndex":2}'})
    assert code == 409 and error['code'] == 'CLI_OVERRIDE'
    code, state = request(base, '/session-options', host, {'optionsRevision': 2, 'changes': '{"soundsEnabled":true}'})
    assert code == 200 and state['sessionOptions']['revision'] == 3
    code, guest = request(base, '/join', fields={'coachId': 1}); assert code == 201
    for index, credential in enumerate([host, guest['token']]):
        code, current = request(base, '/state', host)
        coach = current['game']['coaches'][index]
        suggestion = coach['suggestion']
        round_state = current['round']; revision = round_state['coaches'][index]['revision']
        code, state = request(base, '/lineup', credential, {'round': round_state['round'], 'revision': revision, 'starters': ','.join(map(str, suggestion['starters'])), 'bench': ','.join(map(str, suggestion['bench']))})
        assert code == 200, state
        code, state = request(base, '/ready', credential, {'round': round_state['round'], 'revision': state['round']['coaches'][index]['revision'], 'ready': 'true'})
        assert code == 200, state
    deadline = time.monotonic()+15
    while state['round']['phase'] != 'HALF_TIME':
        assert time.monotonic() < deadline and state['round']['phase'] != 'FAILED', state['playback']
        time.sleep(.05); code, state = request(base, '/state', host)
    assert state['sessionOptions']['revision'] == 4
    assert not state['sessionOptions']['values']['openLineupAtHalfTime'] and not state['sessionOptions']['pendingValues']
    paused = state['playback']['sequence']; time.sleep(.15)
    code, state = request(base, '/state', host); assert state['playback']['sequence'] == paused
    # A failed atomic manifest replacement must not confirm the candidate.
    manifest = Path(info['Session directory'])/'session.json'
    saved = manifest.read_bytes(); manifest.unlink(); manifest.mkdir()
    code, error = request(base, '/session-options', host, {'optionsRevision': 4, 'changes': '{"soundsEnabled":false}'})
    assert code == 503 and error['code'] == 'STORAGE_FAILED'
    code, state = request(base, '/state', host)
    assert state['storageFailed'] and state['sessionOptions']['revision'] == 4 and state['sessionOptions']['values']['soundsEnabled']
    manifest.rmdir(); manifest.write_bytes(saved)
finally:
    process.terminate(); process.wait(timeout=10)
print('PASS options HTTP validation, host authority, guest convergence, idempotency, resume, override and storage failure')
