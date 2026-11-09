"""Read-only career diagnostic; generates isolated careers, never opens existing saves.

Run: python tools/investigate_calendars.py
Requires the local Java 8 runtime and the dependencies used by build_adapter.py.
Raw logs and JSON are written under ignored analysis/calendar-investigation/.
"""
from pathlib import Path
import json
import os
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
JAVA = Path(r'C:\Program Files\Java\jre1.8.0_511\bin\java.exe')
OUT = ROOT / 'analysis/calendar-investigation'
SOURCE = r'''
package brasfoot;
import java.io.*;
import java.util.*;
import best.ah;
import best.al;
import best.I;
import best.O;
import best.Z;
public class CalendarInvestigation {
 public static void main(String[] args)throws Exception {
  File root=new File(args[0]);
  CareerEngine.initializeClubSelection(root);
  List<ah> selected=new ArrayList<ah>();
  for(String name:args[1].split("\\|")){
   ah found=null;
   for(Object raw:c.a.SR.P()){ah team=(ah)raw;if(team.getNome().equalsIgnoreCase(name)){found=team;break;}}
   if(found==null)throw new IllegalArgumentException("Unknown club "+name);
   selected.add(found);
  }
  String[] assets=new String[selected.size()],names=new String[assets.length];
  for(int i=0;i<assets.length;i++){assets[i]=selected.get(i).jY();names[i]="Diagnostic "+i;}
  GameAdapter game=CareerEngine.createByAssets(root,assets,names);
  Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("selection",args[1]);
  List<Integer> active=new ArrayList<Integer>();
  for(Object raw:c.a.SR.N())active.add(((Z)raw).jc());
  result.put("generatedCountries",active);
  List<Integer> enabled=new ArrayList<Integer>();
  for(Object raw:c.a.SR.bF){O country=(O)raw;if(country.iC())enabled.add(country.getPais());}
  result.put("enabledCountries",enabled);result.put("date",c.a.SR.T());
  result.put("calendarDays",c.a.SR.R().size());
  List<Object> views=new ArrayList<Object>();
  for(int coach:game.coachIds()){
   al human=c.a.SR.y(coach);ah club=human.fg();Map<String,Object> view=CareerViews.snapshot(coach);
   Map<String,Integer> competitions=new TreeMap<String,Integer>();
   for(Object raw:Data.list(view.get("calendar"))){String name=String.valueOf(Data.map(raw).get("competition"));competitions.put(name,competitions.containsKey(name)?competitions.get(name)+1:1);}
   Map<String,Object> entry=new LinkedHashMap<String,Object>();entry.put("club",club.getNome());entry.put("country",club.getPais());entry.put("fixtures",competitions);entry.put("tables",Data.list(view.get("tables")).size());views.add(entry);
  }
  result.put("views",views);
  int without=0,dates=0;String failure=null;int startYear=c.a.SR.bb().get(Calendar.YEAR);
  try {
   while(c.a.SR.bb().get(Calendar.YEAR)==startYear && dates<180){
    if(!game.hasHumanMatch())without++;
    for(I match:game.currentMatches()){
     if(match.hc().jZ())ah.a(match.hc(),match,1,-1,true);
     if(match.hd().jZ())ah.a(match.hd(),match,2,-1,true);
    }
    MatchPlayback playback=new MatchPlayback(game.currentMatches(),true);playback.begin();
    for(int ticks=0;!playback.tick();ticks++){if(ticks>150)throw new IllegalStateException("Tick limit");if(playback.status().equals("HALF_TIME"))playback.resumeSecondHalf();}
    dates++;CareerEngine.nextDate(game);
   }
  }catch(Throwable error){failure=error.toString();error.printStackTrace();}
  result.put("simulatedDates",dates);result.put("datesWithoutHumanMatch",without);result.put("endDate",c.a.SR.T());result.put("failure",failure);
  System.out.println("CALENDAR_REPORT "+Json.write(result));
 }
}
'''


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    source = OUT / 'CalendarInvestigation.java'
    source.write_text(SOURCE, encoding='utf-8')
    cp = os.pathsep.join(map(str, [ROOT/'build/classes', ROOT/'analysis/brasfoot.jar', *sorted((ROOT/'analysis/lib').glob('*.jar'))]))
    subprocess.run([str(JAVA), '-jar', str(ROOT/'tools/ecj-3.26.0.jar'), '-1.8', '-encoding', 'UTF-8', '-proc:none', '-warn:none', '-classpath', cp, '-d', str(OUT), str(source)], check=True)
    reports = []
    selections = sys.argv[1:] or ['Corinthians', 'Real Madrid', 'Barcelona', 'Liverpool', 'Bayern de Munique', 'Juventus', 'Paris Saint Germain', 'Boca Juniors', 'Corinthians|Real Madrid']
    suffix = '-additional' if sys.argv[1:] else ''
    for index, selection in enumerate(selections):
        log = OUT / f'case{suffix}-{index}.log'
        with log.open('w', encoding='utf-8') as output:
            try:
                run = subprocess.run([str(JAVA), '-Xmx512m', '-Dfile.encoding=UTF-8', '-Djava.awt.headless=true', '-javaagent:'+str(ROOT/'build/host-ui-agent.jar'), '-cp', str(OUT)+os.pathsep+cp, 'brasfoot.CalendarInvestigation', str(ROOT), selection], stdout=output, stderr=subprocess.STDOUT, timeout=180, cwd=ROOT)
                returncode = run.returncode
            except subprocess.TimeoutExpired:
                returncode = 'timeout'
        lines = log.read_text(encoding='utf-8', errors='replace').splitlines()
        report = next((json.loads(line[len('CALENDAR_REPORT '):]) for line in reversed(lines) if line.startswith('CALENDAR_REPORT ')), {'selection': selection, 'error': lines[-12:]})
        report['returncode'] = returncode
        reports.append(report)
        print(json.dumps(report, ensure_ascii=True), flush=True)
        (OUT/f'results{suffix}.json').write_text(json.dumps(reports, ensure_ascii=False, indent=2), encoding='utf-8')
    if any(report['returncode'] != 0 or report.get('failure') or report.get('simulatedDates', 180) >= 180 for report in reports):
        raise SystemExit('Diagnostic incomplete or failed; inspect the JSON and case logs.')


if __name__ == '__main__':
    main()
