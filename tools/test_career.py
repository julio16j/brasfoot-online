"""Exercise several original seasons, keeping the native calendar diagnostics in a log."""
from pathlib import Path
import json, os, subprocess, sys
sys.stdout.reconfigure(errors='replace')
ROOT = Path(__file__).resolve().parents[1]
JAVA = r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
cp = os.pathsep.join(map(str, [ROOT/'build/classes', ROOT/'analysis/brasfoot.jar', *sorted((ROOT/'analysis/lib').glob('*.jar'))]))
log = ROOT/'analysis/career-test.log'
results=[]
with log.open('w', encoding='utf-8') as output:
    for target,mode in [(2025,[]),(2028,['resume'])]:
        result = subprocess.run([JAVA, '-Dfile.encoding=UTF-8','-Djava.awt.headless=true', '-javaagent:'+str(ROOT/'build/host-ui-agent.jar'), '-cp', cp, 'brasfoot.CareerProbe', str(ROOT),str(target),*mode], stdout=output, stderr=subprocess.STDOUT, timeout=180)
        output.flush()
        lines=log.read_text(encoding='utf-8',errors='replace').splitlines()
        if result.returncode:
            print('\n'.join(lines[-35:]));raise SystemExit(result.returncode)
        results.append(json.loads(next(line.removeprefix('CAREER_PASS ') for line in reversed(lines) if line.startswith('CAREER_PASS '))))
report={'result':'PASS','players':4,'years':sum(run['years'] for run in results),'separateJvmContinuation':True,'runs':results}
(ROOT/'analysis/career-test-result.json').write_text(json.dumps(report,indent=2))
print(json.dumps(report,indent=2))
