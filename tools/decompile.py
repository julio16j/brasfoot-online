from pathlib import Path
import argparse, hashlib, subprocess, zipfile
root = Path(__file__).resolve().parents[1]
p = argparse.ArgumentParser(description='Descompila a copia local sem executar o jogo.')
p.add_argument('--java', required=True)
args = p.parse_args()
cfr = root / 'tools/cfr-0.152.jar'
assert hashlib.md5(cfr.read_bytes()).hexdigest() == '8a85ada8cec494121246805a5562b82b', 'Checksum CFR incorreto'
a = root / 'analysis'
a.mkdir(exist_ok=True)
libs = a / 'lib'
libs.mkdir(exist_ok=True)
with zipfile.ZipFile(root / 'game/Brasfoot22-23/bf22-23.exe') as src, zipfile.ZipFile(a / 'brasfoot.jar', 'w', zipfile.ZIP_DEFLATED) as dst:
    for name in src.namelist():
        dst.writestr(name, src.read(name))
        if name.endswith('.jar'):
            (libs / Path(name).name).write_bytes(src.read(name))
with (a / 'cfr.stdout.log').open('w') as out, (a / 'cfr.stderr.log').open('w') as err:
    subprocess.run([args.java, '-Xmx2g', '-jar', str(cfr), str(a / 'brasfoot.jar'), '--outputdir', str(a / 'decompiled'), '--extraclasspath', ';'.join(str(x) for x in sorted(libs.glob('*.jar'))), '--silent', 'true'], check=True, stdout=out, stderr=err)
print('Fontes em', a / 'decompiled')
