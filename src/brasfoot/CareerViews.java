package brasfoot;

import best.I;
import best.ah;
import best.al;
import java.util.*;

/** Read-only views of the authoritative calendar and original league statistics. */
public final class CareerViews {
    private CareerViews(){ }
    public static Map<String,Object> snapshot(int coachId){
        al coach=c.a.SR.y(coachId);if(coach==null || !coach.jZ())throw new IllegalArgumentException("Unknown coach");
        ah club=coach.fg();Map<String,Object> out=new LinkedHashMap<String,Object>();
        List<Object> calendar=new ArrayList<Object>(),tables=new ArrayList<Object>();
        Set<f.s> leagues=Collections.newSetFromMap(new IdentityHashMap<f.s,Boolean>());
        for(Object raw:c.a.SR.R()){
            best.a day=(best.a)raw;
            for(Object value:day.h()){
                I match=(I)value;
                if(club==null || match.hc()==club || match.hd()==club){
                    Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("date",day.f());row.put("dateMillis",day.a().getTime().getTime());row.put("matchId",row.get("dateMillis")+":"+(match.hy()==null?"none":CompetitionIdentity.key(match.hy()))+":"+match.hc().lk()+":"+match.hd().lk());row.put("status",match.e()?"played":"scheduled");
                    row.put("competition",match.hy().getNome());row.put("phase",match.hG());row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());
                    row.put("homeId",match.hc().lk());row.put("awayId",match.hd().lk());row.put("competitionId",match.hy()==null?-1:match.hy().gD());row.put("competitionKey",CompetitionIdentity.key(match.hy()));row.put("competitionType",match.hy()==null?0:match.hy().b());row.put("result",match.e()?match.hu()+" x "+match.hw():"—");row.put("homeGoals",match.e()?match.hu():null);row.put("awayGoals",match.e()?match.hw():null);calendar.add(row);
                    if(match.ht() instanceof f.s)leagues.add((f.s)match.ht());
                }
            }
        }
        for(f.s league:leagues){
            Map<String,Object> table=new LinkedHashMap<String,Object>();table.put("name",league.getNome()+" — "+league.io());
            List<Object> rows=new ArrayList<Object>();
            for(Object raw:league.yK()){
                ah team=(ah)raw;int[] stats=team.d(league);Map<String,Object> row=new LinkedHashMap<String,Object>();
                row.put("position",league.Q(team));row.put("club",team.getNome());
                String[] keys={"points","played","wins","draws","losses","goalsFor","goalsAgainst","goalDifference"};for(int i=0;i<keys.length;i++)row.put(keys[i],stats[i]);rows.add(row);
            }
            Collections.sort(rows,(a,b)->Integer.compare(Data.number(Data.map(a).get("position")),Data.number(Data.map(b).get("position"))));
            table.put("rows",rows);tables.add(table);
        }
        out.put("calendar",calendar);out.put("tables",tables);out.put("date",c.a.SR.T());out.put("year",c.a.SR.bb().get(Calendar.YEAR));
        List<Object> coaches=new ArrayList<Object>();for(Object raw:c.a.SR.M()){al human=(al)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("name",human.dS());row.put("nationality",human.lE());row.put("boardConfidence",human.lL());row.put("fanConfidence",human.lM());row.put("club",human.fg()==null?"Sem clube":human.fg().getNome());coaches.add(row);}out.put("coaches",coaches);
        out.put("balance",club==null?0L:club.kb());out.put("wages",club==null?0L:club.kK());return out;
    }
}
