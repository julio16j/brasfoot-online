"""Exercise one full original season with four coaches, keeping the native calendar diagnostics in a log."""
from pathlib import Path
import test_logs
import json, os, subprocess, sys
sys.stdout.reconfigure(errors='replace')
ROOT = Path(__file__).resolve().parents[2]
JAVA = r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
cp = os.pathsep.join(map(str, [ROOT/'build/classes', ROOT/'analysis/brasfoot.jar', *sorted((ROOT/'analysis/lib').glob('*.jar'))]))
log = ROOT/'analysis/career-test.log'
results=[]
with test_logs.open_log(log) as output:
    for target,mode in [(2023,[])]:
        result = subprocess.run([JAVA, '-Xms64m', '-Xmx512m', '-XX:ActiveProcessorCount=2', '-Dfile.encoding=UTF-8','-Djava.awt.headless=true', '-javaagent:'+str(ROOT/'build/host-ui-agent.jar'), '-cp', cp, 'brasfoot.CareerProbe', str(ROOT),str(target),*mode], stdout=output, stderr=subprocess.STDOUT, timeout=180)
        output.flush()
        lines=log.read_text(encoding='utf-8',errors='replace').splitlines()
        if result.returncode:
            print('\n'.join(lines[-35:]));raise SystemExit(result.returncode)
        results.append(json.loads(next(line.removeprefix('CAREER_PASS ') for line in reversed(lines) if line.startswith('CAREER_PASS '))))
report={'result':'PASS','players':4,'years':sum(run['years'] for run in results),'separateJvmContinuation':False,'runs':results}
(ROOT/'analysis/career-test-result.json').write_text(json.dumps(report,indent=2))
print(json.dumps(report,indent=2))
