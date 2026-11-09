package brasfoot;

import best.F;
import best.I;
import best.ah;
import best.al;
import java.io.File;
import java.util.*;

/** Verifies native special roles travel by player ID and survive projection. */
public final class SpecialRolesProjectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static int coachFor(ah club){for(Object raw:c.a.SR.M()){al coach=(al)raw;if(coach.fg()==club)return coach.lT();}throw new AssertionError("Coach missing");}
    private static int[] values(Object raw){List<Object> rows=Data.list(raw);int[] result=new int[rows.size()];for(int i=0;i<result.length;i++)result[i]=Data.number(rows.get(i));return result;}
    private static int[] indices(ah club,List<F> players){int[] result=new int[players.size()];for(int i=0;i<result.length;i++)result[i]=club.kc().indexOf(players.get(i));return result;}
    private static int[] roleIds(F a,F b,F c,F d){return new int[]{a==null?-1:a.gD(),b==null?-1:b.gD(),c==null?-1:c.gD(),d==null?-1:d.gD()};}
    private static String fingerprint(GameAdapter game,RoundCoordinator round){return Json.write(game.snapshot())+Json.write(round.snapshot());}
    private static void rejected(GameAdapter game,RoundCoordinator round,int coach,long revision,int[] starters,int[] bench,int[] slots,int[] tactics,int[] roles){String before=fingerprint(game,round);try{round.lineupWithRoles(coach,1,revision,starters,bench,slots,tactics,roles);throw new AssertionError("Expected invalid special role");}catch(IllegalArgumentException|IllegalStateException expected){check(fingerprint(game,round).equals(before),"Invalid role changed roster, tactics, role selection, or round revision");}}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());
        GameAdapter game=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        int playerId=3000;for(Object raw:c.a.SR.P())for(Object value:((ah)raw).kc())((F)value).az(playerId++);
        I match=game.currentMatches().get(0);ah club=match.hc();int coach=coachFor(club);Map<String,Object> state=game.snapshot();
        Map<String,Object> clubRow=null,coachRow=null;for(Object raw:Data.list(state.get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==club.lk())clubRow=row;}for(Object raw:Data.list(state.get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==coach)coachRow=row;}
        Map<String,Object> suggestion=Data.map(coachRow.get("suggestion"));int[] starters=values(suggestion.get("starters"));
        List<F> available=new ArrayList<F>();for(int index:starters)available.add((F)club.kc().get(index));
        F freeKick=available.get(1),captain=available.get(2),corner=available.get(3);int[] roles=roleIds(freeKick,captain,corner,club.lr());
        int[] bench=values(suggestion.get("bench"));
        int[] slots={1,2,9,4,7,11,13,14,16,20,23};int[] tactics=new int[4];for(int i=0;i<4;i++)tactics[i]=Data.number(Data.list(coachRow.get("tactics")).get(i));
        RoundCoordinator round=new RoundCoordinator(game.coachIds(),game);long revision=0;
        int[] invalid=roles.clone();invalid[1]=Integer.MAX_VALUE;rejected(game,round,coach,revision,starters,bench,slots,tactics,invalid);
        int[] blockedFalseNine=roles.clone();blockedFalseNine[3]=available.get(4).gD();rejected(game,round,coach,revision,starters,bench,slots,tactics,blockedFalseNine);
        round.lineupWithRoles(coach,1,revision,starters,bench,slots,tactics,roles);revision++;
        List<F> desired=new ArrayList<F>(available),reserves=new ArrayList<F>();for(int index:bench)reserves.add((F)club.kc().get(index));
        F substitute=null;for(F player:reserves)if(player.getPosicao()!=0){substitute=player;break;}if(substitute==null)throw new AssertionError("Fixture lacks field substitute");
        F displaced=desired.get(1);desired.set(1,substitute);reserves.remove(substitute);reserves.add(displaced);
        round.lineupWithRoles(coach,1,revision,indices(club,desired),indices(club,reserves),slots,tactics,roles);
        check(club.ke()==freeKick&&club.kd()==captain&&club.lq()==corner,"Host applied all three supported role IDs");
        check(freeKick==reserves.get(reserves.size()-1),"Existing role identity survives moving its player to the bench");
        F outsider=null;Set<F> selected=new HashSet<F>(desired);selected.addAll(reserves);for(Object raw:club.kc()){F player=(F)raw;if(!selected.contains(player)&&player.getPosicao()!=0&&player.a(match,false)){outsider=player;break;}}
        if(outsider==null)throw new AssertionError("Fixture lacks eligible unselected player");int[] invalidChange=roleIds(outsider,captain,corner,club.lr());revision++;
        rejected(game,round,coach,revision,indices(club,desired),indices(club,reserves),slots,tactics,invalidChange);
        Map<String,Object> projectedState=game.snapshot();for(Object raw:Data.list(projectedState.get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==club.lk())clubRow=row;}
        Map<String,Object> publishedRoles=Data.map(clubRow.get("roles"));check(Data.number(publishedRoles.get("freeKicks"))==freeKick.gD(),"Host snapshot publishes stable special-role IDs");
        Map<String,Object> session=new LinkedHashMap<String,Object>();session.put("game",projectedState);session.put("playback",new MatchPlayback(game.currentMatches()).snapshot());session.put("round",new LinkedHashMap<String,Object>());VisualModel model=new VisualModel(root,session);ah projected=model.club(club.lk());
        check(projected.ke()!=null&&projected.ke().gD()==freeKick.gD(),"Visual projection restores free-kick taker by ID");
        check(projected.kd()!=null&&projected.kd().gD()==captain.gD(),"Visual projection restores captain by ID");
        check(projected.lq()!=null&&projected.lq().gD()==corner.gD(),"Visual projection restores corner taker by ID");
        System.out.println("PASS stable IDs, three native role consumers, atomic role validation, bench fallback identity, and VisualModel projection");
    }
}
