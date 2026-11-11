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
    /** Complete, read-only calendar and domestic standings for the native client views. */
    public static Map<String,Object> calendarCatalog(){
        List<Object> fixtures=new ArrayList<Object>(),tables=new ArrayList<Object>();
        Set<f.s> leagues=Collections.newSetFromMap(new IdentityHashMap<f.s,Boolean>());
        List<Object> days=c.a.SR.R();
        for(int dateIndex=0;dateIndex<days.size();dateIndex++){
            best.a day=(best.a)days.get(dateIndex);
            for(Object raw:day.h()){
                I match=(I)raw;
                Map<String,Object> row=new LinkedHashMap<String,Object>();
                row.put("date",day.f());row.put("dateMillis",day.a().getTimeInMillis());row.put("dateIndex",dateIndex);row.put("status",match.e()?"played":"scheduled");
                row.put("competition",match.hy()==null?"":match.hy().getNome());row.put("competitionKey",CompetitionIdentity.key(match.hy()));row.put("competitionId",match.hy()==null?-1:match.hy().gD());row.put("competitionType",match.hy()==null?0:match.hy().b());row.put("phase",match.hG());
                row.put("home",match.hc().getNome());row.put("homeId",match.hc().lk());row.put("away",match.hd().getNome());row.put("awayId",match.hd().lk());row.put("result",confirmedResult(match));confirmedMatchFields(row,match);fixtures.add(row);
                if(match.ht() instanceof f.s){f.s league=(f.s)match.ht();if(isSupportedDomesticLeague(league))leagues.add(league);}
            }
            for(Object competition:day.t())if(competition instanceof f.B&&((best.at)competition).b()==1){
                best.L[] phases=((best.at)competition).mB();if(phases!=null)for(best.L phase:phases)if(phase instanceof f.s&&isSupportedDomesticLeague((f.s)phase))leagues.add((f.s)phase);
            }
        }
        List<f.s> ordered=new ArrayList<f.s>(leagues);Collections.sort(ordered,(left,right)->{
            int country=Integer.compare(left.iq()==null?-1:left.iq().jc(),right.iq()==null?-1:right.iq().jc());
            if(country!=0)return country;int division=Integer.compare(left.getDivisao(),right.getDivisao());if(division!=0)return division;return String.valueOf(left.getNome()).compareTo(String.valueOf(right.getNome()));
        });
        for(f.s league:ordered){
            Map<String,Object> table=new LinkedHashMap<String,Object>();best.at competition=league.yT();
            table.put("competition",competition==null?league.getNome():competition.getNome());table.put("competitionKey",CompetitionIdentity.key(competition));table.put("competitionId",competition==null?-1:competition.gD());table.put("competitionType",competition==null?1:competition.b());table.put("country",league.iq()==null?-1:league.iq().jc());table.put("countryName",league.iq()==null?null:league.iq().jf());table.put("division",league.getDivisao());table.put("phase",league.io());table.put("relegationCount",league.getnRebaixados());table.put("directRelegationCount",league.getnRebaixados()-league.Bw());
            List<Object> rows=new ArrayList<Object>();
            for(Object teamRaw:league.yK()){
                best.ah team=(best.ah)teamRaw;int[] stats=team.d(league);Map<String,Object> standing=new LinkedHashMap<String,Object>();standing.put("clubId",team.lk());standing.put("club",team.getNome());standing.put("position",league.Q(team));standing.put("group",0);
                String[] keys={"points","played","wins","draws","losses","goalsFor","goalsAgainst","goalDifference"};for(int i=0;i<keys.length;i++)standing.put(keys[i],stats[i]);rows.add(standing);
            }
            table.put("rows",rows);List<Object> rounds=new ArrayList<Object>();Map<Long,List<Object>> byDate=new TreeMap<Long,List<Object>>();
            for(int dateIndex=0;dateIndex<days.size();dateIndex++){best.a day=(best.a)days.get(dateIndex);for(Object raw:day.h()){I match=(I)raw;if(match.ht()!=league)continue;Map<String,Object> fixture=new LinkedHashMap<String,Object>();fixture.put("date",day.f());fixture.put("dateMillis",day.a().getTimeInMillis());fixture.put("dateIndex",dateIndex);fixture.put("status",match.e()?"played":"scheduled");fixture.put("home",match.hc().getNome());fixture.put("homeId",match.hc().lk());fixture.put("away",match.hd().getNome());fixture.put("awayId",match.hd().lk());fixture.put("result",confirmedResult(match));List<Object> same=byDate.get(day.a().getTimeInMillis());if(same==null){same=new ArrayList<Object>();byDate.put(day.a().getTimeInMillis(),same);}same.add(fixture);}}
            int ordinal=1;for(Map.Entry<Long,List<Object>> entry:byDate.entrySet()){Map<String,Object> round=new LinkedHashMap<String,Object>();round.put("number",ordinal++);round.put("dateMillis",entry.getKey());round.put("fixtures",entry.getValue());rounds.add(round);}table.put("rounds",rounds);tables.add(table);
        }
        List<Object> clubs=new ArrayList<Object>();for(Object raw:c.a.SR.P()){best.ah club=(best.ah)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("clubId",club.lk());row.put("club",club.getNome());row.put("asset",club.jY());row.put("country",club.getPais());row.put("division",club.getDivisao());clubs.add(row);}
        Map<String,Object> response=new LinkedHashMap<String,Object>();response.put("calendar",fixtures);response.put("tables",tables);response.put("clubs",clubs);response.put("date",c.a.SR.T());response.put("dateMillis",c.a.SR.bb().getTimeInMillis());response.put("season",c.a.SR.H());response.put("year",c.a.SR.bb().get(Calendar.YEAR));return response;
    }
    private static boolean isSupportedDomesticLeague(f.s league){if(league.yT()==null||league.yT().b()!=1||league.iq()==null)return false;int country=league.iq().jc();return country==29||country==97||country==65||country==104||country==3||country==72;}
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
