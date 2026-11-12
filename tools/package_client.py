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
        if cls.name.startswith(('AdapterTest','AudioSmoke','NativeUiSmoke','NativeAudioProjectionTest','SpecialRolesProjectionTest','SpecialRolesWindowTest','LiveLineupAtomicityTest')):continue
        archive.write(cls,cls.relative_to(ROOT/'build/classes').as_posix())
compiler=Path(os.environ.get('WINDIR',r'C:\Windows'))/'Microsoft.NET'/'Framework64'/'v4.0.30319'/'csc.exe'
if not compiler.exists():
    compiler=Path(os.environ.get('WINDIR',r'C:\Windows'))/'Microsoft.NET'/'Framework'/'v4.0.30319'/'csc.exe'
if not compiler.exists():raise SystemExit('Nao foi encontrado o compilador C# do .NET Framework (csc.exe).')
launcher=DIST/'Brasfoot-Online.exe'
subprocess.run([str(compiler),'/nologo','/target:winexe','/out:'+str(launcher),'/reference:System.Windows.Forms.dll','/reference:System.Drawing.dll',str(ROOT/'tools/launcher/BrasfootLauncher.cs')],check=True,cwd=ROOT)
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
readme='''BRASFOOT ONLINE - CARREIRA MULTIPLAYER\n\n1. Extraia a pasta inteira e tenha Java 8 instalado.\n2. Abra Iniciar.cmd; Carreira ja vem selecionada.\n3. O host escolhe de 4 a 16 clubes distintos e cria a carreira; quatro tecnicos sao usados por padrao.\n4. Compartilhe o IP e a porta. Os demais jogadores buscam tecnicos e entram em vagas livres.\n5. Cada tecnico prepara a escalacao e confirma Pronto. A rodada comeca quando todos confirmam.\n6. Ao fim das partidas, o host avanca para a proxima data do calendario original.\n\nResultados, calendario, eventos e classificacoes sao compartilhados. A gestao online inclui contratos, base, transferencias e vagas de emprego. Os checkpoints ficam em sessions/ no computador do host. Use Retomar carreira para continuar; os jogadores entram novamente depois que o host reinicia.\n\nPara testar localmente, abra Iniciar.cmd duas vezes e conecte a segunda janela a 127.0.0.1:8212. Conexoes entre redes precisam estar liberadas na rede do host. O jogo e os saves permanecem no computador do host; o EXE original nao e alterado.\n'''
readme='''BRASFOOT ONLINE - CARREIRA MULTIPLAYER\n\n1. Extraia a pasta inteira e tenha Java 8 instalado.\n2. Abra Iniciar.cmd; Carreira ja vem selecionada.\n3. Na tela original do Brasfoot, escolha pais, divisao, clube e nome de cada tecnico. Adicione de 1 a 16 tecnicos.\n4. Compartilhe o IP e a porta. Os demais jogadores buscam tecnicos e entram em vagas livres.\n5. Cada tecnico prepara a escalacao e confirma Pronto. A rodada comeca quando todos confirmam.\n6. Ao fim das partidas, o host avanca para a proxima data do calendario original.\n\nResultados, calendario, eventos e classificacoes sao compartilhados. A gestao online inclui contratos, base, transferencias e vagas de emprego. Os checkpoints ficam em sessions/ no computador do host. Use Retomar carreira para continuar; os jogadores entram novamente depois que o host reinicia.\n\nPara conexoes entre redes, a porta TCP precisa estar acessivel pela rede do host. O jogo e os saves permanecem no computador do host; o EXE original nao e alterado.\n'''
readme='''BRASFOOT ONLINE - CARREIRA MULTIPLAYER

1. Extraia a pasta inteira e tenha Java 8 instalado.
2. Abra Brasfoot-Online.exe para iniciar.
3. O modo carreira usa os seletores originais para escolher país, divisão, clube e nome de cada técnico. É possível incluir de 1 a 16 técnicos.
4. O host compartilha partidas, calendário, resultados e saves; os demais jogadores informam o IP e a porta para entrar.
5. Cada técnico escala seu próprio clube e confirma Pronto. O relógio pausa no intervalo e retoma quando todos confirmam o segundo tempo.

Os checkpoints ficam em sessions/ no computador do host. Use Retomar carreira para continuar. Conexões entre redes dependem da configuração TCP do host.

Para registrar a cópia nativa do Brasfoot, abra game/Brasfoot22-23/bf22-23.exe e use o registro oficial informado em https://www.brasfoot.com/registro-brasfoot-2022-2023.html. O registro é feito por cada usuário no próprio computador.
'''
zip_path=DIST/'Brasfoot-Online-Teste.zip'
prefix='Brasfoot-Online/'
with zipfile.ZipFile(zip_path,'w',zipfile.ZIP_DEFLATED) as archive:
    archive.write(jar,prefix+jar.name)
    archive.write(ROOT/'build/host-ui-agent.jar',prefix+'host-ui-agent.jar')
    for library in libraries:archive.write(library,prefix+'lib/'+library.name)
    archive.write(launcher,prefix+launcher.name)
    archive.writestr(prefix+'LEIA-ME.txt',readme)
    game=ROOT/'game/Brasfoot22-23'
    for path in sorted(game.rglob('*')):
        if not path.is_file() or (path.suffix.lower() in ('.exe','.pdf') and path.name.lower()!='bf22-23.exe') or 'sav' in path.relative_to(game).parts:continue
        archive.write(path,prefix+'game/Brasfoot22-23/'+path.relative_to(game).as_posix())
print('JAR:',jar)
print('Launcher:',launcher)
print('Pacote:',zip_path,'bytes:',zip_path.stat().st_size)
