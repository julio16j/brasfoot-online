package brasfoot;

import best.F;
import best.I;
import best.ah;
import java.io.File;
import java.util.*;

/** Roster indices are independent of the original pitch/bench slot numbers. */
public final class LargeRosterLineupTest {
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    private static int[] indices(List<Integer> values){int[] result=new int[values.size()];for(int i=0;i<result.length;i++)result[i]=values.get(i);return result;}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);GameAdapter game=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        int coach=game.coachIds().iterator().next();ah club=((best.al)c.a.SR.M().get(0)).fg();I match=game.currentMatches().get(0);
        Map<String,Object> before=Data.map(Data.list(game.snapshot().get("coaches")).get(0));List<Object> suggested=Data.list(Data.map(before.get("suggestion")).get("starters"));
        F lateStarter=(F)club.kc().get(Data.number(suggested.get(10)));F lateReserve=null;
        for(Object raw:club.kc()){F player=(F)raw;if(player!=lateStarter && player.getPosicao()!=0 && player.a(match,false)){lateReserve=player;break;}}
        club.kc().remove(lateStarter);club.kc().remove(lateReserve);
        while(club.kc().size()<40){F player=new F();player.setNome("Reserva extra "+club.kc().size());player.n(club);player.setPosicao(4);player.setIdade(24);player.fI();player.a(365L,true);club.kc().add(player);}
        club.kc().add(lateStarter);club.kc().add(lateReserve);
        List<F> identities=new ArrayList<F>(club.kc());
        Map<String,Object> current=Data.map(Data.list(game.snapshot().get("coaches")).get(0));List<Integer> starters=new ArrayList<Integer>();
        for(Object raw:Data.list(Data.map(current.get("suggestion")).get("starters")))starters.add(Data.number(raw));starters.set(10,40);
        List<Integer> bench=new ArrayList<Integer>();bench.add(41);for(int i=0;i<club.kc().size() && bench.size()<11;i++)if(!starters.contains(i) && !bench.contains(i) && ((F)club.kc().get(i)).a(match,false))bench.add(i);
        check(bench.size()==11,"Test needs eleven eligible reserves");
        game.lineup(coach,indices(starters),indices(bench));
        check(club.kc().equals(identities),"Submitting must preserve the entire roster and its order");
        check(match.hp().size()==11 && match.hn().size()==11,"Native match receives eleven starters and eleven reserves");
        check(match.hp().contains(lateStarter) && lateStarter.fT()<=25,"Roster index 40 must map to a pitch slot");
        check(match.hn().contains(lateReserve) && lateReserve.fT()==26,"Roster index 41 must map to the first bench slot");
        for(int i=0;i<club.kc().size();i++)if(!starters.contains(i) && !bench.contains(i))check(((F)club.kc().get(i)).fT()==-1,"Unselected players remain outside the match");
        String confirmed=Json.write(game.snapshot());
        int[] duplicates=indices(starters);duplicates[10]=duplicates[9];try{game.lineup(coach,duplicates,indices(bench));throw new AssertionError("Duplicate accepted");}catch(IllegalArgumentException expected){}
        int[] tooMany=new int[12];try{game.lineup(coach,indices(starters),tooMany);throw new AssertionError("Too many reserves accepted");}catch(IllegalArgumentException expected){}
        check(confirmed.equals(Json.write(game.snapshot())),"Rejected selections must not partially alter the match");
        System.out.println("PASS 42-player roster, high roster indices, 11 starters, 11 reserves and atomic rejection of invalid selections");
    }
}
