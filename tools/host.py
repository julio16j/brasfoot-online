from pathlib import Path
import argparse, os, subprocess, sys
root = Path(__file__).resolve().parents[1]
p = argparse.ArgumentParser(description='Inicia a sessao no computador que guarda o save.')
p.add_argument('--java', default=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe')
p.add_argument('--demo', action='store_true')
p.add_argument('--save', type=Path)
p.add_argument('--host-coach', type=int)
p.add_argument('--port', type=int, default=8212)
p.add_argument('--bind', default='0.0.0.0')
p.add_argument('--tick-ms',type=int,default=500,help='Duracao de um minuto de jogo em milissegundos')
a=p.parse_args()
if a.demo == bool(a.save):
    p.error('Escolha --demo ou --save CAMINHO.s22')
subprocess.run([sys.executable,str(root/'tools/build_adapter.py'),'--java',a.java],check=True,cwd=root)
cp=os.pathsep.join(map(str,[root/'build/classes',root/'analysis/brasfoot.jar',*sorted((root/'analysis/lib').glob('*.jar'))]))
command=[a.java,'-Djava.awt.headless=true','-cp',cp,'brasfoot.SessionHost','--root',str(root),'--bind',a.bind,'--port',str(a.port),'--tick-ms',str(a.tick_ms)]
command += ['--demo'] if a.demo else ['--save',str(a.save.resolve())]
if a.host_coach is not None: command += ['--host-coach',str(a.host_coach)]
try:
    subprocess.run(command,check=True,cwd=root)
except KeyboardInterrupt:
    pass
