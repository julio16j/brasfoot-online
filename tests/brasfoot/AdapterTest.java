package brasfoot;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import best.F;
import best.ah;
public final class AdapterTest {
    private static int assertions;
    private static void check(boolean value, String label) {
        if (!value) throw new AssertionError(label);
        assertions++;
    }
    private static void reject(Runnable task, String label) {
        boolean rejected = false;
        try { task.run(); } catch (IllegalArgumentException | IllegalStateException expected) { rejected = true; }
        check(rejected, label);
    }
    private static int[] eligible(ah club) {
        List<Integer> result = new ArrayList<Integer>();
        best.I match = (best.I)c.a.SR.S().get(0);
        for(int role:new int[]{0,1,1,2,2,3,3,3,3,4,4}) {
            int found=-1;
            for(int i=0;i<club.kc().size();i++) if(!result.contains(i) && ((F)club.kc().get(i)).getPosicao()==role && ((F)club.kc().get(i)).a(match,false)) { found=i;break; }
            if(found<0)for(int i=0;i<club.kc().size();i++)if(!result.contains(i) && ((F)club.kc().get(i)).getPosicao()!=0 && ((F)club.kc().get(i)).a(match,false)){found=i;break;}
            if(found>=0)result.add(found);
        }
        check(result.size()==11, "Real roster must have 11 eligible players");
        int[] eleven=new int[11];for(int i=0;i<11;i++)eleven[i]=result.get(i);
        return eleven;
    }
    public static void main(String[] args) throws Exception {
        File root = new File(args[0]);
        GameAdapter game = Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"), new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        final RoundCoordinator round = new RoundCoordinator(game.coachIds(), game);
        Integer[] ids = game.coachIds().toArray(new Integer[0]);
        ah home = ((best.al)c.a.SR.M().get(0)).fg();
        ah away = ((best.al)c.a.SR.M().get(1)).fg();
        final int a=ids[0], b=ids[1];
        int[] starters=eligible(home), other=eligible(away);
        List<Integer> benchList = new ArrayList<Integer>(); for (int i=0;i<away.kc().size();i++) { boolean used=false; for(int j:other) if(j==i)used=true; if(!used && benchList.size()<11 && ((F)away.kc().get(i)).a((best.I)c.a.SR.S().get(0),false)) benchList.add(i); }
        int[] otherBench = new int[benchList.size()]; for(int i=0;i<otherBench.length;i++)otherBench[i]=benchList.get(i);
        reject(() -> round.ready(a,1,0,true), "No ready without lineup");
        check(!home.kf(), "Failed command does not change readiness");
        int[] duplicate=starters.clone(); duplicate[10]=duplicate[0];
        reject(() -> round.lineup(a,1,0,duplicate,new int[0]), "Reject duplicates");
        check(home.kY().isEmpty(), "Invalid lineup does not mutate engine");
        round.lineup(a,1,0,starters,new int[0]);
        check(home.kY().size()==11, "Original club updated");
        check(((F)home.kY().get(0)).fT()==1,"Goalkeeper has original slot 1");
        check(((F)home.kY().get(10)).fT()>=19,"Forward occupies attacking slot");
        check(((best.I)c.a.SR.S().get(0)).hl().size()==11, "Original match updated");
        check(!home.kf(), "Lineup does not imply ready");
        round.lineup(a,1,1,starters,new int[0]);
        round.lineup(b,1,0,other,otherBench);
        check(away.kY().size()==11, "Independent revisions permit parallel preparation");
        check(away.kZ().size()==otherBench.length,"Bench is separate from starting eleven");
        check(((best.I)c.a.SR.S().get(0)).hq().size()==11,"Bench cannot enter active player list");
        round.ready(a,1,2,true);
        reject(() -> round.lineup(a,1,3,starters,new int[0]), "Ready blocks edits");
        round.ready(a,1,3,false);
        check(!home.kf(), "Withdraw ready reaches original engine");
        round.lineup(a,1,4,starters,new int[0]);
        reject(() -> round.ready(a,1,4,true), "Stale revision rejected");
        reject(() -> round.ready(a,0,5,true), "Stale round rejected");
        reject(() -> round.ready(-1,1,0,true), "Unknown coach rejected");
        final CountDownLatch start = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        Thread t1 = new Thread(() -> { try { start.await(); round.ready(a,1,5,true); } catch(Throwable e) { failure.set(e); } });
        Thread t2 = new Thread(() -> { try { start.await(); round.ready(b,1,1,true); } catch(Throwable e) { failure.set(e); } });
        t1.start();t2.start();start.countDown();t1.join();t2.join();
        check(failure.get()==null, "Concurrent ready succeeds");
        check(round.snapshot().get("phase").equals("WAITING_FOR_ENGINE"), "Both ready lock phase");
        check(round.snapshot().get("roundLocks").equals(1), "Exactly one round lock");
        reject(() -> round.ready(b,1,2,true), "Duplicate ready cannot trigger again");
        reject(() -> round.ready(a,1,6,false), "Cannot withdraw after lock");
        check(home.kf() && away.kf(), "Both engine clubs ready");
        MatchPlayback playback=new MatchPlayback(game.currentMatches());
        playback.begin();
        check(playback.snapshot().get("status").equals("FIRST_HALF"),"Kickoff uses original engine");
        int ticks=0;boolean interval=false;
        while(!playback.tick()) {
            ticks++;if(playback.snapshot().get("status").equals("HALF_TIME")){interval=true;playback.resumeSecondHalf();}
            if(ticks>=110)throw new AssertionError("Match exceeded duration bound");
        }
        check(ticks>=90 && ticks<110,"Bounded match duration");
        check(interval,"Half time reached");
        check(playback.snapshot().get("status").equals("FINISHED"),"Full time reached");
        check(game.currentMatches().get(0).e(),"Original match marked played");
        reject(() -> playback.begin(),"Cannot simulate a match twice");
        best.I original=game.currentMatches().get(0);
        best.A converted=new best.A(0);converted.k(home);converted.f((F)home.kY().get(10));converted.a(1);converted.R(3);
        int before=original.hu();
        MatchPlayback.resolvePenalty(original,converted,new Random(){private static final long serialVersionUID=1L;@Override public int nextInt(int bound){return 0;}});
        check(original.hu()==before+1 && converted.b()==1,"Converted penalty updates original score");
        best.A missed=new best.A(0);missed.k(home);missed.f((F)home.kY().get(10));missed.a(1);missed.R(3);
        MatchPlayback.resolvePenalty(original,missed,new Random(){private static final long serialVersionUID=1L;@Override public int nextInt(int bound){return bound-1;}});
        check(original.hu()==before+1 && missed.b()==7,"Missed penalty is not counted as goal");
        System.out.println("PASS " + assertions + " assertions using original game bytecode and real rosters");
        System.out.println(Json.write(round.snapshot()));
    }
}

