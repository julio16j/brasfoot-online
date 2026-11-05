@echo off
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
start "" "%BRASFOOT_JAVA%" -jar "%~dp0dist\brasfoot-online.jar" --root "%~dp0."
