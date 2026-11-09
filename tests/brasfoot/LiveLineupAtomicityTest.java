package brasfoot;

import best.F;
import best.I;
import best.ah;
import best.al;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

/** Exercises live lineup validation against real engine objects and round revisions. */
public final class LiveLineupAtomicityTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static int coachFor(ah club){for(Object raw:c.a.SR.M()){al coach=(al)raw;if(coach.fg()==club)return coach.lT();}throw new AssertionError("Coach missing");}
    private static int[] values(Object raw){List<Object> rows=Data.list(raw);int[] result=new int[rows.size()];for(int i=0;i<result.length;i++)result[i]=Data.number(rows.get(i));return result;}
    private static int[] indices(ah club,List<F> players){int[] result=new int[players.size()];for(int i=0;i<result.length;i++){result[i]=club.kc().indexOf(players.get(i));if(result[i]<0)throw new AssertionError("Player is absent from roster");}return result;}
    private static String signature(Object object){StringBuilder result=new StringBuilder(object.getClass().getName());for(Class<?> type=object.getClass();type!=null;type=type.getSuperclass())for(Field field:type.getDeclaredFields()){if(Modifier.isStatic(field.getModifiers()))continue;try{field.setAccessible(true);result.append('|').append(type.getName()).append('.').append(field.getName()).append('=').append(normalize(field.get(object)));}catch(Exception error){throw new AssertionError(error);}}return result.toString();}
    private static String normalize(Object value){if(value==null)return "null";Class<?> type=value.getClass();if(type.isArray()){StringBuilder out=new StringBuilder("[");for(int i=0;i<Array.getLength(value);i++)out.append(normalize(Array.get(value,i))).append(',');return out.append(']').toString();}if(value instanceof Collection){StringBuilder out=new StringBuilder("[");for(Object item:(Collection<?>)value)out.append(normalize(item)).append(',');return out.append(']').toString();}if(value instanceof Number || value instanceof Boolean || value instanceof CharSequence || type.isEnum())return String.valueOf(value);return type.getName()+"@"+System.identityHashCode(value);}
    private static String state(I match,ah club,RoundCoordinator round,MatchPlayback playback){StringBuilder result=new StringBuilder(signature(match)).append(signature(club)).append(Json.write(round.snapshot())).append(playback.sequence()).append('/').append(match.aR(match.hc()==club?0:1));for(Object raw:club.kc())result.append(signature(raw));return result.toString();}
    private static long coachRevision(RoundCoordinator round,int coach){for(Object raw:Data.list(round.snapshot().get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==coach)return Data.longNumber(row.get("revision"));}throw new AssertionError("Coach missing from round");}
    private static void rejected(RoundCoordinator round,int coach,long revision,Runnable operation,Supplier<String> fingerprint,String reason){String before=fingerprint.get();try{round.liveCommand(coach,1,revision,operation);throw new AssertionError("Expected rejection: "+reason);}catch(IllegalArgumentException | IllegalStateException expected){check(fingerprint.get().equals(before),"Rejection changed engine state or round revision: "+reason);}}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());
        GameAdapter game=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        int playerId=100;for(Object raw:c.a.SR.P())for(Object value:((ah)raw).kc())((F)value).az(playerId++);
        I match=game.currentMatches().get(0);ah home=match.hc();int coach=coachFor(home);RoundCoordinator round=new RoundCoordinator(game.coachIds(),game);
        Map<String,Object> snapshot=game.snapshot();for(Object raw:Data.list(snapshot.get("coaches"))){Map<String,Object> row=Data.map(raw),suggestion=Data.map(row.get("suggestion"));
            int[] starters=values(suggestion.get("starters")),bench=values(suggestion.get("bench"));int[] slots={1,2,9,4,7,11,13,14,16,20,23};int[] tactics=values(row.get("tactics"));int id=Data.number(row.get("coachId"));
            round.lineupWithOptions(id,1,0,starters,bench,slots,tactics);
        }
        for(Object raw:Data.list(round.snapshot().get("coaches"))){Map<String,Object> row=Data.map(raw);round.ready(Data.number(row.get("coachId")),1,Data.longNumber(row.get("revision")),true);}
        MatchPlayback playback=new MatchPlayback(game.currentMatches());playback.begin();round.engineStarted();
        List<F> active=new ArrayList<F>(match.hp()),bank=new ArrayList<F>(match.hn());List<F> fieldOut=new ArrayList<F>();for(F p:active)if(p.getPosicao()!=0)fieldOut.add(p);
        List<F> benchField=new ArrayList<F>();for(F p:bank)if(p.getPosicao()!=0)benchField.add(p);
        F out1=fieldOut.get(0),out2=fieldOut.get(1),in1=benchField.get(0),in2=benchField.get(1),outsider=null;
        Set<F> selected=new HashSet<F>(active);selected.addAll(bank);for(Object raw:home.kc()){F player=(F)raw;if(!selected.contains(player)&&player.getPosicao()!=0&&player.a(match,false)){outsider=player;break;}}
        if(outsider==null)throw new AssertionError("Fixture lacks an unselected field player");
        int[] slots=active.stream().mapToInt(F::fT).toArray();int[] tactics=home.kj().clone();
        List<F> desired=new ArrayList<F>(active);desired.set(desired.indexOf(out1),in1);desired.set(desired.indexOf(out2),outsider);
        List<F> nextBank=new ArrayList<F>(bank);nextBank.remove(in1);nextBank.add(out1);nextBank.add(out2);
        int[] starters=indices(home,desired),bench=indices(home,nextBank);long revision=coachRevision(round,coach);
        Supplier<String> fingerprint=()->state(match,home,round,playback);
        final int[] invalidStarters=starters,invalidBench=bench;
        rejected(round,coach,revision,()->game.liveLineup(coach,invalidStarters,invalidBench,slots,tactics,playback),fingerprint,"second incoming player outside the current bench");

        desired=new ArrayList<F>(active);desired.set(desired.indexOf(out1),in1);desired.set(desired.indexOf(out2),in2);
        nextBank=new ArrayList<F>(bank);nextBank.remove(in1);nextBank.remove(in2);nextBank.add(out1);nextBank.add(out2);
        starters=indices(home,desired);bench=indices(home,nextBank);Data.set(in2,"eI",Long.MAX_VALUE);revision=coachRevision(round,coach);
        final int[] injuredStarters=starters,injuredBench=bench;
        rejected(round,coach,revision,()->game.liveLineup(coach,injuredStarters,injuredBench,slots,tactics,playback),fingerprint,"second incoming player became ineligible");
        int outgoingId=out1.gD(),injuredId=in2.gD();
        rejected(round,coach,revision,()->game.substitute(coach,outgoingId,injuredId,playback),fingerprint,"individual substitute route rejects the same ineligible player");
        Data.set(in2,"eI",0L);

        List<F> currentBank=new ArrayList<F>(match.hn());F unused=null;Set<F> current=new HashSet<F>(match.hp());current.addAll(currentBank);for(Object raw:home.kc()){F p=(F)raw;if(!current.contains(p)&&p.getPosicao()!=0){unused=p;break;}}
        List<F> alteredBank=new ArrayList<F>(currentBank);alteredBank.remove(0);alteredBank.add(unused);bench=indices(home,alteredBank);int[] unchangedStarters=indices(home,new ArrayList<F>(match.hp()));revision=coachRevision(round,coach);
        final int[] changedBank=bench,unchanged=unchangedStarters;
        rejected(round,coach,revision,()->game.liveLineup(coach,unchanged,changedBank,slots,tactics,playback),fingerprint,"bank-only replacement");

        List<F> live=new ArrayList<F>(match.hp());List<F> legalBank=new ArrayList<F>(match.hn());F out=live.stream().filter(p->p.getPosicao()!=0).findFirst().get();F in=legalBank.stream().filter(p->p.getPosicao()!=0).findFirst().get();List<F> legal=new ArrayList<F>(live);legal.set(legal.indexOf(out),in);legalBank.remove(in);legalBank.add(out);
        revision=coachRevision(round,coach);long beforeSequence=playback.sequence();int beforeEvents=match.hE().size(),beforeRemaining=match.aR(0);round.liveCommand(coach,1,revision,()->game.liveLineup(coach,indices(home,legal),indices(home,legalBank),slots,tactics,playback));
        check(playback.sequence()==beforeSequence+1&&match.hE().size()==beforeEvents+1&&match.aR(0)==beforeRemaining-1,"Valid live replacement reaches the original engine exactly once");
        check(coachRevision(round,coach)==revision+1,"Valid live replacement advances coach revision once");
        System.out.println("PASS live lineup preflight, eligibility parity, bank authorization, goalkeeper pairing contract, and zero partial mutations");
    }
}
