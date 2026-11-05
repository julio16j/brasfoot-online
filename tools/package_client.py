"""Cria o JAR online e um pacote local que nao exige Python para jogar."""
from pathlib import Path
import os, subprocess, sys, zipfile, io
ROOT=Path(__file__).resolve().parents[1]
subprocess.run([sys.executable,str(ROOT/'tools/build_adapter.py')],check=True,cwd=ROOT)
DIST=ROOT/'dist';DIST.mkdir(exist_ok=True)
LIB=DIST/'lib';LIB.mkdir(exist_ok=True)
libraries=[ROOT/'analysis/brasfoot.jar',*sorted((ROOT/'analysis/lib').glob('*.jar'))]
for library in libraries:(LIB/library.name).write_bytes(library.read_bytes())
(DIST/'host-ui-agent.jar').write_bytes((ROOT/'build/host-ui-agent.jar').read_bytes())
manifest='Manifest-Version: 1.0\r\nMain-Class: brasfoot.NativeClient\r\nClass-Path: '+ ' '.join('lib/'+p.name for p in libraries)+'\r\n\r\n'
# JAR manifest physical lines are limited to 72 bytes; continuation starts with a space.
physical=[]
for line in manifest.split('\r\n'):
    while len(line.encode('ascii'))>70:
        physical.append(line[:70]);line=' '+line[70:]
    physical.append(line)
jar=DIST/'brasfoot-online.jar'
with zipfile.ZipFile(jar,'w',zipfile.ZIP_DEFLATED) as archive:
    archive.writestr('META-INF/MANIFEST.MF','\r\n'.join(physical))
    for cls in sorted((ROOT/'build/classes').rglob('*.class')):
        if cls.name.startswith(('AdapterTest','AudioSmoke','NativeUiSmoke')):continue
        archive.write(cls,cls.relative_to(ROOT/'build/classes').as_posix())
CMD=r'''@echo off
setlocal
cd /d "%~dp0"
set "BRASFOOT_JAVA="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javaw.exe" set "BRASFOOT_JAVA=%JAVA_HOME%\bin\javaw.exe"
for /d %%J in ("%ProgramFiles%\Java\jre1.8*" "%ProgramFiles%\Java\jdk1.8*" "%ProgramFiles%\Eclipse Adoptium\jdk-8*" "%ProgramFiles(x86)%\Java\jre1.8*") do if exist "%%~fJ\bin\javaw.exe" set "BRASFOOT_JAVA=%%~fJ\bin\javaw.exe"
if not defined BRASFOOT_JAVA (
 echo Instale o Java 8 para abrir o Brasfoot Online.
 pause
 exit /b 1
)
start "" "%BRASFOOT_JAVA%" -jar "%~dp0brasfoot-online.jar" --root "%~dp0."
'''
(ROOT/'Brasfoot-Online.cmd').write_bytes(CMD.replace('%~dp0brasfoot-online.jar','%~dp0dist\\brasfoot-online.jar').replace('\n','\r\n').encode('ascii'))
(ROOT/'Sala-Online.cmd').write_bytes(b'@echo off\r\ncall "%~dp0Brasfoot-Online.cmd"\r\n')
readme='''BRASFOOT ONLINE - TESTE DO AMISTOSO\n\n1. Extraia esta pasta inteira. E necessario ter Java 8 instalado.\n2. Abra Iniciar.cmd.\n3. O host clica Criar sessao e compartilha o IP e a porta mostrados.\n4. No outro computador, informe esse IP:porta, clique Buscar tecnicos, escolha o disponivel e clique Entrar.\n5. Use Autoescalar ou Escalacao para arrastar os jogadores na tela original; clique Enviar escalacao.\n6. Cada tecnico confirma Pronto. Todos acompanham a partida e os sons originais ate o final.\n\nO save fica em sessions/ no computador do host. Para repetir o amistoso, feche o host e crie outra sessao.\nPara testar sozinho, abra Iniciar.cmd duas vezes e entre com 127.0.0.1:8212 na segunda janela.\nA porta TCP 8212 precisa estar acessivel; acesso entre redes externas ainda depende da configuracao de rede.\n\nEste pacote usa as classes e os recursos da instalacao local do jogo em um lancador online separado. Nao altera o EXE original. Ainda nao inclui campeonato completo, transferencias ou substituicoes durante a partida.\n'''
zip_path=DIST/'Brasfoot-Online-Teste.zip'
prefix='Brasfoot-Online/'
with zipfile.ZipFile(zip_path,'w',zipfile.ZIP_DEFLATED) as archive:
    archive.write(jar,prefix+jar.name)
    archive.write(ROOT/'build/host-ui-agent.jar',prefix+'host-ui-agent.jar')
    for library in libraries:archive.write(library,prefix+'lib/'+library.name)
    archive.writestr(prefix+'Iniciar.cmd',CMD.replace('\n','\r\n').encode('ascii'))
    archive.writestr(prefix+'LEIA-ME.txt',readme)
    game=ROOT/'game/Brasfoot22-23'
    for path in sorted(game.rglob('*')):
        if not path.is_file() or path.suffix.lower() in ('.exe','.pdf') or 'sav' in path.relative_to(game).parts:continue
        archive.write(path,prefix+'game/Brasfoot22-23/'+path.relative_to(game).as_posix())
print('JAR:',jar)
print('Pacote:',zip_path,'bytes:',zip_path.stat().st_size)
