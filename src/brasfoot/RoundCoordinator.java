package brasfoot;

import java.util.*;

/** Single authority for one preparation phase. Does not simulate matches. */
public final class RoundCoordinator {
    public enum Phase { PREPARING, WAITING_FOR_ENGINE, RUNNING, HALF_TIME, FINISHED, FAILED }
    public interface Actions {
        void lineup(int coach, int[] starters, int[] bench);
        void ready(int coach, boolean value);
        default void lineupWithOptions(int coach,int[] starters,int[] bench,int[] slots,int[] tactics){lineup(coach,starters,bench);}
        default void lineupWithRoles(int coach,int[] starters,int[] bench,int[] slots,int[] tactics,int[] roles){lineupWithOptions(coach,starters,bench,slots,tactics);}
    }
    private final Actions actions;
    private final Map<Integer, Boolean> ready = new LinkedHashMap<Integer, Boolean>();
    private final Map<Integer, Long> revisions = new LinkedHashMap<Integer, Long>();
    private Phase phase = Phase.PREPARING;
    private long round = 1;
    private long revision;
    private int starts;

    public RoundCoordinator(Collection<Integer> coaches, Actions actions) {
        if (coaches.isEmpty()) throw new IllegalArgumentException("No human coaches");
        for (Integer id : coaches) {
            if (ready.containsKey(id)) throw new IllegalArgumentException("Duplicate coach");
            ready.put(id, false); revisions.put(id, 0L);
        }
        this.actions = actions;
    }
    private void validate(int coach, long expectedRound, long expectedRevision) {
        if (!ready.containsKey(coach)) throw new IllegalArgumentException("Unknown coach");
        if (expectedRound != round) throw new IllegalStateException("Stale round");
        if (phase != Phase.PREPARING) throw new IllegalStateException("Round locked for engine");
        if (expectedRevision != revisions.get(coach)) throw new IllegalStateException("Stale coach revision");
    }
    private void changed(int coach) {
        revisions.put(coach, revisions.get(coach) + 1); revision++;
    }
    public synchronized void lineup(int coach,long expectedRound,long expectedRevision,int[] starters,int[] bench){
        lineupWithOptions(coach,expectedRound,expectedRevision,starters,bench,new int[]{1,2,9,4,7,11,13,14,16,20,23},null);
    }
    public synchronized void lineupWithOptions(int coach,long expectedRound,long expectedRevision,int[] starters,int[] bench,int[] slots,int[] tactics){
        lineupWithRoles(coach,expectedRound,expectedRevision,starters,bench,slots,tactics,null);
    }
    public synchronized void lineupWithRoles(int coach,long expectedRound,long expectedRevision,int[] starters,int[] bench,int[] slots,int[] tactics,int[] roles){
        validate(coach,expectedRound,expectedRevision);
        if(ready.get(coach))throw new IllegalStateException("Withdraw ready before editing");
        actions.lineupWithRoles(coach,starters.clone(),bench.clone(),slots.clone(),tactics==null?null:tactics.clone(),roles==null?null:roles.clone());
        changed(coach);
    }
    public synchronized void management(int coach,long expectedRound,long expectedRevision,Collection<Integer> affected,Runnable command){
        validate(coach,expectedRound,expectedRevision);
        if(ready.get(coach))throw new IllegalStateException("Withdraw ready before changing the career");
        for(Integer id:affected){
            if(!ready.containsKey(id))throw new IllegalArgumentException("Unknown affected coach");
            if(ready.get(id))throw new IllegalStateException("Affected coach must withdraw ready before this transaction");
        }
        command.run();ready.put(coach,false);changed(coach);
    }
    /** Live match commands are serialized with the engine tick and versioned per coach. */
    public synchronized void liveCommand(int coach,long expectedRound,long expectedRevision,Runnable command){
        if(!ready.containsKey(coach))throw new IllegalArgumentException("Unknown coach");
        if(expectedRound!=round || expectedRevision!=revisions.get(coach))throw new IllegalStateException("Stale round or coach revision");
        if(phase!=Phase.RUNNING && phase!=Phase.HALF_TIME)throw new IllegalStateException("Live match adjustments are only available during play");
        command.run();ready.put(coach,false);changed(coach);
    }
    public synchronized void engineHalfTime(){
        if(phase!=Phase.RUNNING)throw new IllegalStateException("Match is not in the first half");
        phase=Phase.HALF_TIME;revision++;
        for(Integer id:ready.keySet()){ready.put(id,false);revisions.put(id,revisions.get(id)+1);}
    }
    public synchronized void halftimeReady(int coach,long expectedRound,long expectedRevision,boolean value,Runnable resume){
        if(!ready.containsKey(coach))throw new IllegalArgumentException("Unknown coach");
        if(expectedRound!=round || expectedRevision!=revisions.get(coach))throw new IllegalStateException("Stale round or coach revision");
        if(phase!=Phase.HALF_TIME)throw new IllegalStateException("The halftime barrier is not active");
        ready.put(coach,value);changed(coach);
        if(!ready.containsValue(false)){resume.run();phase=Phase.RUNNING;revision++;}
    }
    /** Changes to a roster invalidate selections and old forms from every affected manager. */
    public synchronized void invalidatePreparation(Collection<Integer> coaches,int actingCoach){
        if(phase!=Phase.PREPARING)throw new IllegalStateException("Cannot invalidate a locked round");
        for(Integer coach:coaches){
            if(!ready.containsKey(coach))throw new IllegalArgumentException("Unknown coach");
            actions.ready(coach,false);ready.put(coach,false);
            if(coach!=actingCoach)changed(coach);
        }
    }
    public synchronized void ready(int coach, long expectedRound, long expectedRevision, boolean value) {
        validate(coach, expectedRound, expectedRevision);
        actions.ready(coach, value);
        ready.put(coach, value); changed(coach);
        if (!ready.containsValue(false)) {
            phase = Phase.WAITING_FOR_ENGINE;
            starts++;
        }
    }
    public synchronized void engineStarted() {
        if(phase!=Phase.WAITING_FOR_ENGINE)throw new IllegalStateException("Round not ready for kickoff");
        phase=Phase.RUNNING;revision++;
    }
    public synchronized void engineFinished() {
        if(phase!=Phase.RUNNING)throw new IllegalStateException("Round is not running");
        phase=Phase.FINISHED;revision++;
    }
    public synchronized void engineFailed() { phase=Phase.FAILED;revision++; }
    public synchronized void restoreFinished() {
        if(phase!=Phase.PREPARING)throw new IllegalStateException("Cannot restore active round");
        phase=Phase.FINISHED;
    }
    /** Recovery preserves calendar generations; preparation commands must be confirmed again. */
    public synchronized void restoreMetadata(Map<String,Object> saved) {
        if(phase!=Phase.PREPARING && phase!=Phase.FINISHED)throw new IllegalStateException("Cannot restore an active coordinator");
        long restoredRound=number(saved.get("round"));
        long restoredRevision=number(saved.get("revision"));
        long restoredStarts=number(saved.get("roundLocks"));
        if(restoredRound<1 || restoredRevision<0 || restoredStarts<0 || restoredStarts>Integer.MAX_VALUE)throw new IllegalArgumentException("Invalid saved coordinator counters");
        Map<Integer,Long> restored=new LinkedHashMap<Integer,Long>();
        Object list=saved.get("coaches");
        if(!(list instanceof List))throw new IllegalArgumentException("Missing saved coach revisions");
        for(Object raw:(List<?>)list){
            if(!(raw instanceof Map))throw new IllegalArgumentException("Invalid saved coach revision");
            Map<?,?> row=(Map<?,?>)raw;long id=number(row.get("coachId")),value=number(row.get("revision"));
            if(id<Integer.MIN_VALUE || id>Integer.MAX_VALUE || value<0 || restored.put((int)id,value)!=null)throw new IllegalArgumentException("Invalid saved coach revision");
        }
        if(!restored.keySet().equals(ready.keySet()))throw new IllegalArgumentException("Saved coaches differ from career coaches");
        round=restoredRound;revision=restoredRevision+1;starts=(int)restoredStarts;
        for(Integer id:ready.keySet()){
            ready.put(id,false);
            // The adapter intentionally forgets submitted lineups and engine-ready flags on reload.
            revisions.put(id,restored.get(id)+1);
        }
    }
    private static long number(Object value){
        if(!(value instanceof Number))throw new IllegalArgumentException("Invalid saved coordinator number");
        Number numeric=(Number)value;long result=numeric.longValue();
        if(numeric.doubleValue()!=result || result==Long.MAX_VALUE)throw new IllegalArgumentException("Invalid saved coordinator number");
        return result;
    }
    public synchronized void nextRound(int coach,long expectedRound,long expectedRevision,Runnable advance) {
        if(!ready.containsKey(coach))throw new IllegalArgumentException("Unknown coach");
        if(expectedRound!=round || expectedRevision!=revisions.get(coach))throw new IllegalStateException("Stale round or coach revision");
        if(phase!=Phase.FINISHED)throw new IllegalStateException("Current round is unfinished");
        advance.run();round++;revision++;phase=Phase.PREPARING;
        for(Integer id:ready.keySet()){ready.put(id,false);revisions.put(id,revisions.get(id)+1);}
    }
    public synchronized Map<String, Object> snapshot() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("round", round); result.put("revision", revision);
        result.put("phase", phase.name()); result.put("roundLocks", starts);
        List<Object> coaches = new ArrayList<Object>();
        for (Integer id : ready.keySet()) {
            Map<String, Object> entry = new LinkedHashMap<String, Object>();
            entry.put("coachId", id); entry.put("ready", ready.get(id));
            entry.put("revision", revisions.get(id)); coaches.add(entry);
        }
        result.put("coaches", coaches);
        return result;
    }
}
