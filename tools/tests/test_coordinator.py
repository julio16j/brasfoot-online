"""Validate the shared-round protocol independently of native career or UI changes."""
from pathlib import Path
import subprocess

ROOT=Path(__file__).resolve().parents[2]
JAVA=r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe'
out=ROOT/'build/protocol-test'
out.mkdir(parents=True,exist_ok=True)
subprocess.run([JAVA,'-jar',str(ROOT/'tools/ecj-3.26.0.jar'),'-1.8','-encoding','UTF-8','-proc:none','-warn:none','-d',str(out),str(ROOT/'src/brasfoot/RoundCoordinator.java'),str(ROOT/'tests/brasfoot/CoordinatorTest.java')],check=True,cwd=ROOT)
subprocess.run([JAVA,'-cp',str(out),'brasfoot.CoordinatorTest'],check=True,cwd=ROOT)
