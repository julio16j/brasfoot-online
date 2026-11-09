from pathlib import Path
import argparse, hashlib, os, subprocess, urllib.request, zipfile
ROOT = Path(__file__).resolve().parents[1]
p = argparse.ArgumentParser()
p.add_argument('--java', default=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe')
p.add_argument('--test', action='store_true')
p.add_argument('--test-transfer', action='store_true')
a = p.parse_args()
compiler = ROOT / 'tools/ecj-3.26.0.jar'
expected = 'ac0ba5876eaf7ebb47749a0d1be179c51f194b9dd0b875d1c09e1b530f5a2db5'
if not compiler.exists():
    data = urllib.request.urlopen('https://repo.maven.apache.org/maven2/org/eclipse/jdt/ecj/3.26.0/ecj-3.26.0.jar', timeout=30).read()
    if hashlib.sha256(data).hexdigest() != expected:
        raise SystemExit('ECJ checksum mismatch')
    compiler.write_bytes(data)
if hashlib.sha256(compiler.read_bytes()).hexdigest() != expected:
    raise SystemExit('ECJ checksum mismatch')
libs = [ROOT / 'analysis/brasfoot.jar', *sorted((ROOT / 'analysis/lib').glob('*.jar'))]
if not libs[0].exists():
    raise SystemExit('Run tools/decompile.py first')
out = ROOT / 'build/classes'
out.mkdir(parents=True, exist_ok=True)
sources = sorted((ROOT / 'src').rglob('*.java')) + sorted((ROOT / 'tests').rglob('*.java'))
subprocess.run([a.java, '-jar', str(compiler), '-1.8', '-encoding', 'UTF-8', '-proc:none', '-warn:none', '-classpath', os.pathsep.join(map(str, libs)), '-d', str(out), *map(str, sources)], check=True, cwd=ROOT)
with zipfile.ZipFile(ROOT/'build/host-ui-agent.jar','w') as agent:
    agent.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nPremain-Class: brasfoot.HostUiAgent\n\n')
    for file in out.glob('brasfoot/HostUiAgent*.class'):
        agent.write(file,file.relative_to(out).as_posix())
if a.test:
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.NativeAudioProjectionTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.YouthProjectionTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.AvailabilityProjectionTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.NativeCareerDialogsTest'], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.AdapterTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.LargeRosterLineupTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.LiveLineupAtomicityTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.CareerCountrySelectionTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.CareerManagementStadiumTest', str(ROOT)], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.NativeStadiumPricesTest', str(ROOT)], check=True, cwd=ROOT)
if a.test or a.test_transfer:
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.NativeTransferSearchTest'], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.NativeTransferMarketTest'], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.CareerManagementOfferTest'], check=True, cwd=ROOT)
    subprocess.run([a.java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, [out, *libs])), 'brasfoot.CareerManagementLoanTest', str(ROOT)], check=True, cwd=ROOT)
