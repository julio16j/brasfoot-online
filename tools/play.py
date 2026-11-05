"""Abre a sessão online com as telas originais do Brasfoot."""
from pathlib import Path
import argparse, ctypes, os, subprocess, sys
ROOT=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser(description=__doc__)
p.add_argument('--online',action='store_true',help='Abrir a janela online (padrao)')
p.add_argument('--original-menu',action='store_true',help='Abrir a inicializacao original com um menu Online adicional')
p.add_argument('--java',default=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe')
a=p.parse_args()
try:
    (ROOT/'analysis').mkdir(exist_ok=True)
    log=ROOT/'analysis/graphical-launch.log'
    with log.open('w',encoding='utf-8') as output:
        subprocess.run([sys.executable.replace('pythonw.exe','python.exe'),str(ROOT/'tools/build_adapter.py'),'--java',a.java],cwd=ROOT,stdout=output,stderr=subprocess.STDOUT,check=True)
    cp=os.pathsep.join(map(str,[ROOT/'build/classes',ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]))
    java=Path(a.java).with_name('javaw.exe')
    if not java.exists():java=Path(a.java)
    entry='brasfoot.OriginalLauncher' if a.original_menu else 'brasfoot.NativeClient'
    args=[str(ROOT)] if a.original_menu else ['--root',str(ROOT)]
    with log.open('a',encoding='utf-8') as output:
        subprocess.Popen([str(java),'-cp',cp,entry,*args],cwd=ROOT/'game/Brasfoot22-23',stdout=output,stderr=subprocess.STDOUT)
except Exception as error:
    message=f'Falha ao abrir o Brasfoot Online: {error}\nConsulte {ROOT / "analysis/graphical-launch.log"}'
    if os.name=='nt':ctypes.windll.user32.MessageBoxW(None,message,'Brasfoot Online',0x10)
    raise
