package brasfoot;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Protocol state-machine checks without a UI or random game results. */
public final class CoordinatorTest {
    private static int checks;
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);checks++;}
    private static void reject(Runnable command,String message){boolean rejected=false;try{command.run();}catch(IllegalStateException|IllegalArgumentException expected){rejected=true;}check(rejected,message);}
    private static final class Actions implements RoundCoordinator.Actions {
        public void lineup(int coach,int[] starters,int[] bench){ }
        public void ready(int coach,boolean value){ }
    }
    private static long revision(RoundCoordinator coordinator,int id){for(Object raw:(List<?>)coordinator.snapshot().get("coaches")){Map<?,?> row=(Map<?,?>)raw;if(((Number)row.get("coachId")).intValue()==id)return ((Number)row.get("revision")).longValue();}throw new AssertionError("Missing coach");}
    public static void main(String[] args)throws Exception{
        List<Integer> ids=Arrays.asList(10,20,30,40);RoundCoordinator coordinator=new RoundCoordinator(ids,new Actions());
        for(int id:ids.subList(0,3))coordinator.ready(id,1,0,true);
        check(coordinator.snapshot().get("phase").equals("PREPARING"),"Three players cannot start a four-player round");
        reject(()->coordinator.ready(99,1,0,true),"Unknown coach rejected");
        reject(()->coordinator.management(40,1,0,Arrays.asList(10,40),()->{}),"Management cannot change another ready user's club");
        AtomicInteger mutations=new AtomicInteger();CountDownLatch gate=new CountDownLatch(1);List<Throwable> failures=Collections.synchronizedList(new ArrayList<Throwable>());
        Runnable ready=()->{try{gate.await();coordinator.ready(40,1,0,true);}catch(Throwable error){failures.add(error);}};
        Thread first=new Thread(ready),duplicate=new Thread(ready);first.start();duplicate.start();gate.countDown();first.join();duplicate.join();
        check(failures.size()==1 && failures.get(0) instanceof IllegalStateException,"Concurrent duplicate ready is rejected once");
        check(coordinator.snapshot().get("roundLocks").equals(1),"Four-player round locks exactly once");
        coordinator.engineStarted();coordinator.engineFinished();
        reject(()->coordinator.nextRound(10,0,1,mutations::incrementAndGet),"Old round cannot advance calendar");
        check(mutations.get()==0 && coordinator.snapshot().get("phase").equals("FINISHED"),"Rejected advance preserves completed round");
        coordinator.nextRound(10,1,1,mutations::incrementAndGet);
        check(mutations.get()==1 && coordinator.snapshot().get("round").equals(2L),"Accepted advance changes calendar once");
        reject(()->coordinator.nextRound(10,1,1,mutations::incrementAndGet),"Replay of advance cannot change calendar again");
        check(mutations.get()==1,"Replayed advance never executes mutation");
        long old=revision(coordinator,10);
        coordinator.management(10,2,old,Arrays.asList(10,20),()->{mutations.incrementAndGet();coordinator.invalidatePreparation(Arrays.asList(10,20),10);});
        check(revision(coordinator,10)==old+1 && revision(coordinator,20)==3,"Roster transaction invalidates both users' old forms");
        reject(()->coordinator.management(10,2,old,Collections.singleton(10),mutations::incrementAndGet),"A repeated management form cannot spend twice");
        check(mutations.get()==2,"Management replay never executes mutation");
        Map<String,Object> saved=coordinator.snapshot();RoundCoordinator recovered=new RoundCoordinator(ids,new Actions());recovered.restoreMetadata(saved);
        check(recovered.snapshot().get("round").equals(2L),"Recovery preserves calendar generation");
        check(recovered.snapshot().get("roundLocks").equals(1),"Recovery preserves completed lock counter");
        check(revision(recovered,10)==revision(coordinator,10)+1,"Recovery invalidates old forms");
        for(Object raw:(List<?>)recovered.snapshot().get("coaches"))check(Boolean.FALSE.equals(((Map<?,?>)raw).get("ready")),"Recovery requires fresh player confirmations");
        System.out.println("PASS "+checks+" coordinator checks");
    }
}
