package brasfoot;

import best.I;
import best.ah;
import best.al;
import java.util.*;

/** Read-only views of the authoritative calendar and original league statistics. */
public final class CareerViews {
    private CareerViews(){ }
    public static Map<String,Object> clubAgenda(int clubId){
        ah club=c.a.SR.x(clubId);if(club==null)throw new IllegalArgumentException("Unknown club");
        List<Object> fixtures=new ArrayList<Object>();
        for(Object raw:c.a.SR.R()){
            best.a day=(best.a)raw;
            for(Object value:day.h()){
                I match=(I)value;if(match.hc()!=club&&match.hd()!=club)continue;
                Map<String,Object> row=new LinkedHashMap<String,Object>();
                row.put("date",day.f());row.put("dateMillis",day.a().getTime().getTime());row.put("status",match.e()?"played":"scheduled");
                row.put("competition",match.hy()==null?"":match.hy().getNome());row.put("phase",match.hG());row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());
                row.put("result",confirmedResult(match));competitionFields(row,match);confirmedMatchFields(row,match);fixtures.add(row);
            }
        }
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("clubId",clubId);result.put("club",club.getNome());result.put("fixtures",fixtures);return result;
    }
    private static void competitionFields(Map<String,Object> row,I match){best.at competition=match.hy();row.put("homeId",match.hc().lk());row.put("awayId",match.hd().lk());row.put("competitionId",competition==null?-1:competition.gD());row.put("competitionKey",CompetitionIdentity.key(competition));row.put("competitionType",competition==null?0:competition.b());row.put("competitionCountryName",competition==null||competition.iq()==null?null:competition.iq().jf());row.put("competitionDivision",competition==null?-1:competition.ip());}
    private static String confirmedResult(I match){if(!match.e())return "x";int[] penalties=match.hQ();return penalties[0]>=0&&penalties[1]>=0?match.hu()+" ("+penalties[0]+") x "+match.hw()+" ("+penalties[1]+")":match.hu()+" x "+match.hw();}
    private static void confirmedMatchFields(Map<String,Object> row,I match){row.put("attendance",match.e()?match.hU():null);row.put("revenue",match.e()?match.hN():null);boolean penalties=match.e()&&match.hQ()[0]>=0&&match.hQ()[1]>=0;row.put("penaltiesAvailable",penalties);row.put("homePenalties",penalties?match.hQ()[0]:null);row.put("awayPenalties",penalties?match.hQ()[1]:null);row.put("winnerClubId",match.e()&&match.hR()!=null?match.hR().lk():null);}
    public static Map<String,Object> snapshot(int coachId){
        al coach=c.a.SR.y(coachId);if(coach==null || !coach.jZ())throw new IllegalArgumentException("Unknown coach");
        ah club=coach.fg();Map<String,Object> out=new LinkedHashMap<String,Object>();
        List<Object> calendar=new ArrayList<Object>(),tables=new ArrayList<Object>();
        Set<f.s> leagues=Collections.newSetFromMap(new IdentityHashMap<f.s,Boolean>());
        for(Object raw:c.a.SR.R()){
            best.a day=(best.a)raw;
            for(Object value:day.h()){
                I match=(I)value;
                if(club!=null && (match.hc()==club || match.hd()==club)){
                    Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("date",day.f());row.put("dateMillis",day.a().getTime().getTime());row.put("matchId",row.get("dateMillis")+":"+(match.hy()==null?"none":CompetitionIdentity.key(match.hy()))+":"+match.hc().lk()+":"+match.hd().lk());row.put("status",match.e()?"played":"scheduled");
                    row.put("competition",match.hy().getNome());row.put("phase",match.hG());row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());
                    row.put("homeId",match.hc().lk());row.put("awayId",match.hd().lk());row.put("competitionId",match.hy()==null?-1:match.hy().gD());row.put("competitionKey",CompetitionIdentity.key(match.hy()));row.put("competitionType",match.hy()==null?0:match.hy().b());row.put("result",confirmedResult(match));row.put("homeGoals",match.e()?match.hu():null);row.put("awayGoals",match.e()?match.hw():null);competitionFields(row,match);confirmedMatchFields(row,match);calendar.add(row);
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
        out.put("calendar",calendar);out.put("tables",tables);out.put("scorers",scorers());out.put("assists",assists());out.put("date",c.a.SR.T());out.put("year",c.a.SR.bb().get(Calendar.YEAR));
        List<Object> coaches=new ArrayList<Object>();for(Object raw:c.a.SR.M()){al human=(al)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("name",human.dS());row.put("nationality",human.lE());row.put("boardConfidence",human.lL());row.put("fanConfidence",human.lM());row.put("club",human.fg()==null?"Sem clube":human.fg().getNome());coaches.add(row);}out.put("coaches",coaches);
        out.put("balance",club==null?0L:club.kb());out.put("wages",club==null?0L:club.kK());return out;
    }
    private static List<Object> scorers(){List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();for(Object raw:c.a.SR.O()){best.F player=(best.F)raw;if(player.gy()<=0)continue;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("playerId",player.gD());row.put("name",player.getNome());row.put("club",player.fg()==null?null:player.fg().getNome());row.put("goals",player.gy());row.put("appearances",player.gz());row.put("retired",false);rows.add(row);}for(Object raw:c.a.SR.bJ()){best.b retired=(best.b)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("playerId",null);row.put("name",retired.getN());row.put("club",null);row.put("goals",retired.v());row.put("appearances",retired.w());row.put("retired",true);rows.add(row);}Collections.sort(rows,(left,right)->{int goals=Integer.compare(Data.number(right.get("goals")),Data.number(left.get("goals")));return goals!=0?goals:Integer.compare(Data.number(left.get("appearances")),Data.number(right.get("appearances")));});List<Object> result=new ArrayList<Object>();for(int i=0;i<Math.min(401,rows.size());i++)result.add(rows.get(i));return result;}
    private static List<Object> assists(){List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();for(Object raw:c.a.SR.O()){best.F player=(best.F)raw;int assists=player.gs();if(assists<=0)continue;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("playerId",player.gD());row.put("name",player.getNome());row.put("club",player.fg()==null?null:player.fg().getNome());row.put("assists",assists);row.put("appearances",player.gu());rows.add(row);}Collections.sort(rows,(left,right)->{int assists=Integer.compare(Data.number(right.get("assists")),Data.number(left.get("assists")));return assists!=0?assists:Integer.compare(Data.number(left.get("appearances")),Data.number(right.get("appearances")));});List<Object> result=new ArrayList<Object>();for(int i=0;i<Math.min(401,rows.size());i++)result.add(rows.get(i));return result;}
}
