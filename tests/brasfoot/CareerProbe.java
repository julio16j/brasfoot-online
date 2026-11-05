package brasfoot;

import best.I;
import best.ah;
import best.al;
import java.io.*;
import java.util.*;

/** Multi-season original-bytecode test, including a separate-JVM continuation. */
public final class CareerProbe {
    private static void check(boolean condition,String label){if(!condition)throw new AssertionError(label);}
    private static String stableSnapshot(String json)throws Exception{
        Map<String,Object> snapshot=Data.parse(json);
        for(Object club:Data.list(snapshot.get("visualClubs")))for(Object player:Data.list(Data.map(club).get("players")))Data.map(player).remove("eligible");
        return Json.write(snapshot);
    }
    public static void main(String[] args) throws Exception {
        File root=new File(args[0]);int target=args.length>1?Integer.parseInt(args[1]):2025;
        File save=new File(root,"analysis/career-probe.s22");
        GameAdapter game=args.length>2 && args[2].equals("resume")?GameAdapter.readLocalSave(save):CareerEngine.create(root,new String[]{"Corinthians","Palmeiras","Santos","São Paulo"});
        Set<Integer> ids=game.coachIds();check(ids.size()==4,"Four distinct human coach identities");
        Map<Integer,Long> initialBalances=new LinkedHashMap<Integer,Long>();
        for(Object raw:c.a.SR.M()){al coach=(al)raw;if(coach.fg()!=null)initialBalances.put(coach.lT(),coach.fg().kb());}
        int dates=0,matches=0,humanMatches=0,shootouts=0,shortLineups=0,unemployed=0;
        int year=c.a.SR.bb().get(Calendar.YEAR),initialYear=year;
        long lastDate=0;Set<String> processed=new HashSet<String>();
        while(c.a.SR.bb().get(Calendar.YEAR)<target){
            long nowDate=c.a.SR.bb().getTimeInMillis();check(nowDate>lastDate,"Calendar strictly advances between preparation phases");lastDate=nowDate;
            check(processed.add(c.a.SR.T()),"A calendar date is not simulated twice");
            for(I match:game.currentMatches()) {
                check(!match.e(),"Preparing match is unplayed");
                if(match.hc().jZ())best.ah.a(match.hc(),match,1,-1,true);
                if(match.hd().jZ())best.ah.a(match.hd(),match,2,-1,true);
                if(match.hc().jZ() || match.hd().jZ()){
                    humanMatches++;if(match.hl().size()<11 || match.hm().size()<11)shortLineups++;
                }
            }
            List<I> fixtures=game.currentMatches();
            MatchPlayback playback=new MatchPlayback(fixtures,true);playback.begin();
            for(int ticks=0;!playback.tick();ticks++){check(ticks<120,"Match reaches full time");if(playback.status().equals("HALF_TIME"))playback.resumeSecondHalf();}
            for(I match:fixtures){
                check(match.e(),"Every fixture finalizes");check(match.hu()>=0 && match.hw()>=0,"Nonnegative score");matches++;
                if(match.hR()!=null){shootouts++;check(match.hQ()[0]!=match.hQ()[1],"Shootout has a winner");check(match.hR()==(match.hQ()[0]>match.hQ()[1]?match.hc():match.hd()),"Shootout winner agrees with score");}
                if(match.hc().jZ() || match.hd().jZ())for(ah club:new ah[]{match.hc(),match.hd()}){
                    int goals=0;for(Object raw:match.hE()){best.A event=(best.A)raw;if(event.b()==1 && event.cu()==club)goals++;}
                    check(goals==(club==match.hc()?match.hu():match.hw()),"Goal events agree with authoritative score");
                }
            }
            dates++;
            if(dates==25){
                String before=Json.write(game.snapshot());game.saveLocal(save);game=GameAdapter.readLocalSave(save);
                String after=Json.write(game.snapshot());
                if(!before.equals(after)){
                    java.nio.file.Files.write(new File(root,"analysis/career-before-reload.json").toPath(),before.getBytes("UTF-8"));
                    java.nio.file.Files.write(new File(root,"analysis/career-after-reload.json").toPath(),after.getBytes("UTF-8"));
                }
                check(stableSnapshot(before).equals(stableSnapshot(after)),"Completed-date snapshot survives Kryo reload without replay");
            }
            CareerEngine.nextDate(game);check(ids.equals(game.coachIds()),"Human identities survive native season changes");
            int now=c.a.SR.bb().get(Calendar.YEAR);
            if(now!=year){
                year=now;int active=0;for(Object raw:c.a.SR.M())if(((al)raw).fg()!=null)active++;
                unemployed=Math.max(unemployed,4-active);
                System.out.println("YEAR "+year+" dates="+dates+" activeHumans="+active);
            }
            check(dates<1800,"Career does not stall");
        }
        boolean financesChanged=false;for(Object raw:c.a.SR.M()){al coach=(al)raw;if(coach.fg()!=null && initialBalances.containsKey(coach.lT()))financesChanged|=coach.fg().kb()!=initialBalances.get(coach.lT());}
        check(financesChanged,"Native career finances change over multiple years");
        check(matches>1000 && humanMatches>100,"Substantial native competition coverage");
        game.saveLocal(save);
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("result","PASS");result.put("startYear",initialYear);result.put("endYear",year);result.put("years",year-initialYear);result.put("dates",dates);result.put("matches",matches);result.put("humanMatches",humanMatches);result.put("shootouts",shootouts);result.put("shortLineups",shortLineups);result.put("maxUnemployed",unemployed);result.put("players",ids.size());result.put("saveReload",true);
        System.out.println("CAREER_PASS "+Json.write(result));
    }
}
