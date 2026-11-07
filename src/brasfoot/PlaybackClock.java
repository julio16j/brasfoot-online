package brasfoot;

import java.util.concurrent.*;

/** One pending minute, with generation checks under the host's monitor. */
public final class PlaybackClock implements AutoCloseable {
    public interface Scheduler { Future<?> schedule(Runnable task,long delay); }
    private final Object monitor;
    private final Scheduler scheduler;
    private final Runnable tick;
    private Future<?> pending;
    private long generation;
    private boolean closed;
    public PlaybackClock(Object monitor,Scheduler scheduler,Runnable tick){this.monitor=monitor;this.scheduler=scheduler;this.tick=tick;}
    public void schedule(long delay){synchronized(monitor){
        cancel();if(closed)return;final long expected=generation;
        pending=scheduler.schedule(()->{synchronized(monitor){if(closed || generation!=expected)return;pending=null;tick.run();}},delay);
    }}
    public void cancel(){synchronized(monitor){generation++;if(pending!=null)pending.cancel(false);pending=null;}}
    public void close(){synchronized(monitor){cancel();closed=true;}}
}
