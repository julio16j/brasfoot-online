package brasfoot;

import java.util.*;

/** Single authority for one preparation phase. Does not simulate matches. */
public final class RoundCoordinator {
    public enum Phase { PREPARING, WAITING_FOR_ENGINE, RUNNING, FINISHED, FAILED }
    public interface Actions {
        void lineup(int coach, int[] starters, int[] bench);
        void ready(int coach, boolean value);
        default void lineupWithOptions(int coach,int[] starters,int[] bench,int[] slots,int[] tactics){lineup(coach,starters,bench);}
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
        validate(coach,expectedRound,expectedRevision);
        if(ready.get(coach))throw new IllegalStateException("Withdraw ready before editing");
        actions.lineupWithOptions(coach,starters.clone(),bench.clone(),slots.clone(),tactics==null?null:tactics.clone());
        changed(coach);
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
