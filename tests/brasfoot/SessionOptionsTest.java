package brasfoot;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public final class SessionOptionsTest {
    private static void check(boolean condition){if(!condition)throw new AssertionError();}
    private static void invalid(Runnable action){try{action.run();throw new AssertionError("Accepted invalid input");}catch(IllegalArgumentException expected){}}
    private static Map<String,Object> change(String key,Object value){Map<String,Object> result=new LinkedHashMap<String,Object>();result.put(key,value);return result;}
    static final class FakeFuture implements Future<Object>{boolean cancelled;public boolean cancel(boolean interrupt){cancelled=true;return true;}public boolean isCancelled(){return cancelled;}public boolean isDone(){return cancelled;}public Object get(){return null;}public Object get(long timeout,TimeUnit unit){return null;}}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        c.a.ey(1920);Data.set(c.a.vM(),"velocidade",4);Data.set(c.a.vM(),"velocidadeNH",4);
        SessionOptions original=SessionOptions.load(null);check(original.interval(true,null)==220 && original.interval(false,null)==70);
        int[] human={2000,1200,700,450,220,100},ai={1200,700,300,100,70,30};for(int i=0;i<6;i++){check(SessionOptions.interval(i,true)==human[i]);check(SessionOptions.interval(i,false)==ai[i]);}
        invalid(()->SessionOptions.interval(-1,true));invalid(()->original.change(change("humanSpeedIndex",6),false));invalid(()->original.change(change("soundsEnabled",1),false));invalid(()->original.change(change("humanSpeedIndex",1.2),false));
        invalid(()->original.change(change("playerListPalette",Arrays.asList("#000000")),false));invalid(()->original.change(change("nonHumanCompetitionPlayback",change("21",true)),false));
        invalid(()->OptionsJson.parse("{\"soundsEnabled\":true,\"soundsEnabled\":false}"));invalid(()->OptionsJson.parse("{\"a\":{\"x\":1,\"x\":2}}"));invalid(()->OptionsJson.parse("{\"a\":01}"));invalid(()->OptionsJson.parse("[]"));
        check(Boolean.TRUE.equals(OptionsJson.parse("{\"soundsEnabled\":true}").get("soundsEnabled")));
        SessionOptions changed=original.change(change("soundsEnabled",false),false);check(changed.revision()==1);check(changed.change(change("soundsEnabled",false),false)==changed);check(Boolean.TRUE.equals(original.get("soundsEnabled")));
        SessionOptions waiting=changed.change(change("openLineupAtHalfTime",false),false);check(Boolean.TRUE.equals(waiting.get("openLineupAtHalfTime")));check(Data.map(waiting.persisted().get("pendingValues")).size()==1);
        SessionOptions restored=SessionOptions.load(OptionsJson.parse(Json.write(waiting.persisted())));check(restored.persisted().equals(waiting.persisted()));
        check(restored.activate("NEXT_SAFE_CHECKPOINT")==restored);SessionOptions active=restored.activate("NEXT_HALF_TIME");check(active.revision()==3 && Boolean.FALSE.equals(active.get("openLineupAtHalfTime")));check(active.activate("NEXT_HALF_TIME")==active);
        SessionOptions cancelled=waiting.change(change("openLineupAtHalfTime",true),false);check(Data.map(cancelled.persisted().get("pendingValues")).isEmpty());check(cancelled.revision()==3);
        try{original.change(change("humanSpeedIndex",1),true);throw new AssertionError();}catch(SessionOptions.Conflict expected){check(expected.code.equals("CLI_OVERRIDE"));}
        try{original.change(change("autoSaveMode",2),false);throw new AssertionError();}catch(SessionOptions.Conflict expected){check(expected.code.equals("OPTION_NOT_SUPPORTED"));}
        active.applyNative();check(!c.a.SR.isUsaSons() && c.a.SR.getVerJanelaSubs()==0);
        SessionOptions renewOffPending=active.change(change("autoRenewExpiredContracts",false),false);check(Boolean.TRUE.equals(renewOffPending.get("autoRenewExpiredContracts")) && Boolean.FALSE.equals(Data.map(renewOffPending.persisted().get("pendingValues")).get("autoRenewExpiredContracts")));
        SessionOptions renewOff=renewOffPending.activate("NEXT_CONTRACT_EVALUATION");check(Boolean.FALSE.equals(renewOff.get("autoRenewExpiredContracts")) && Data.map(renewOff.persisted().get("pendingValues")).isEmpty());renewOff.applyNative();
        best.ah managed=((best.al)c.a.SR.M().get(0)).fg();best.F expired=(best.F)managed.kc().get(0);expired.a(0L,true);check(expired.fR()==0);CareerEngine.prepare();check(expired.fR()==0);
        SessionOptions renewOnPending=renewOff.change(change("autoRenewExpiredContracts",true),false);SessionOptions renewOn=renewOnPending.activate("NEXT_CONTRACT_EVALUATION");renewOn.applyNative();CareerEngine.prepare();check(expired.fR()==180);
        Object monitor=new Object();List<Runnable> tasks=new ArrayList<Runnable>();List<Long> delays=new ArrayList<Long>();List<FakeFuture> futures=new ArrayList<FakeFuture>();int[] ticks={0};
        PlaybackClock clock=new PlaybackClock(monitor,(task,delay)->{tasks.add(task);delays.add(delay);FakeFuture future=new FakeFuture();futures.add(future);return future;},()->ticks[0]++);
        clock.schedule(220);clock.schedule(70);check(futures.get(0).cancelled);tasks.get(0).run();check(ticks[0]==0);tasks.get(1).run();check(ticks[0]==1);check(delays.equals(Arrays.asList(220L,70L)));
        clock.schedule(30);clock.cancel();tasks.get(2).run();check(ticks[0]==1);clock.schedule(100);clock.close();tasks.get(3).run();check(ticks[0]==1);clock.schedule(1);check(tasks.size()==4);
        System.out.println("PASS options catalog, strict JSON, native tables, revision, pending activation, persistence and stale clock tasks");
    }
}
