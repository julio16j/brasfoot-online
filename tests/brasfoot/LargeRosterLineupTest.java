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
        for(int i=0;i<club.kc().size();i++)((F)club.kc().get(i)).az(1000+i);
        int[] roles={((F)club.kc().get(starters.get(0))).gD(),((F)club.kc().get(starters.get(1))).gD(),((F)club.kc().get(starters.get(2))).gD(),((F)club.kc().get(starters.get(10))).gD()};
        List<Integer> currentTactics=new ArrayList<Integer>();for(int value:club.kj())currentTactics.add(value);game.lineupWithRoles(coach,indices(starters),indices(bench),new int[]{1,2,9,4,7,11,13,14,16,20,23},indices(currentTactics),roles);
        check(club.kc().equals(identities),"Submitting must preserve the entire roster and its order");
        check(match.hp().size()==11 && match.hn().size()==11,"Native match receives eleven starters and eleven reserves");
        check(match.hp().contains(lateStarter) && lateStarter.fT()<=25,"Roster index 40 must map to a pitch slot");
        check(club.ke().gD()==roles[0] && club.kd().gD()==roles[1] && club.lq().gD()==roles[2] && club.lr().gD()==roles[3],"All four role IDs must project to the chosen native players");
        Map<String,Object> projected=Data.map(Data.list(Data.map(game.snapshot()).get("visualClubs")).stream().filter(raw->Data.number(Data.map(raw).get("clubId"))==club.lk()).findFirst().get());
        Map<String,Object> projectedRoles=Data.map(projected.get("roles"));check(Data.number(projectedRoles.get("falseNine"))==roles[3],"Snapshots must publish stable role player IDs");
        check(match.hn().contains(lateReserve) && lateReserve.fT()==26,"Roster index 41 must map to the first bench slot");
        for(int i=0;i<club.kc().size();i++)if(!starters.contains(i) && !bench.contains(i))check(((F)club.kc().get(i)).fT()==-1,"Unselected players remain outside the match");
        String confirmed=Json.write(game.snapshot());
        int[] duplicates=indices(starters);duplicates[10]=duplicates[9];try{game.lineup(coach,duplicates,indices(bench));throw new AssertionError("Duplicate accepted");}catch(IllegalArgumentException expected){}
        int[] tooMany=new int[12];try{game.lineup(coach,indices(starters),tooMany);throw new AssertionError("Too many reserves accepted");}catch(IllegalArgumentException expected){}
        check(confirmed.equals(Json.write(game.snapshot())),"Rejected selections must not partially alter the match");
        int[] clearedRoles=roles.clone();clearedRoles[3]=-1;game.lineupWithRoles(coach,indices(starters),indices(bench),new int[]{1,2,9,4,7,11,13,14,16,20,23},indices(currentTactics),clearedRoles);check(club.lr()==null,"False nine must support explicit clearing");
        int[] benchRole=roles.clone();benchRole[0]=((F)club.kc().get(bench.get(0))).gD();String beforeBenchRole=Json.write(game.snapshot());try{game.lineupWithRoles(coach,indices(starters),indices(bench),new int[]{1,2,9,4,7,11,13,14,16,20,23},indices(currentTactics),benchRole);throw new AssertionError("A reserve cannot hold a starter role");}catch(IllegalArgumentException expected){}check(beforeBenchRole.equals(Json.write(game.snapshot())),"Rejected reserve role must leave the whole lineup unchanged");
        int[] badSlots={1,2,9,4,7,11,13,14,16,20,14};String withRoleState=Json.write(game.snapshot());try{game.lineupWithRoles(coach,indices(starters),indices(bench),badSlots,indices(currentTactics),roles);throw new AssertionError("False nine in forbidden cell accepted");}catch(IllegalArgumentException expected){}
        check(withRoleState.equals(Json.write(game.snapshot())),"Rejected special roles must not partially alter starters, tactics or roles");
        System.out.println("PASS 42-player roster, high roster indices, 11 starters, 11 reserves and atomic rejection of invalid selections");
    }
}
