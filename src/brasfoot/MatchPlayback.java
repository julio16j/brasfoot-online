package brasfoot;
import best.I;
import best.A;
import best.F;
import java.util.*;

/** Drives the original human-match minute loop without opening Swing windows. */
public final class MatchPlayback {
    private final List<I> matches;
    private final Random random = new Random();
    private String status="NOT_STARTED";
    private int half=1, minute;
    private int firstExtra, secondExtra;
    private long sequence;
    private String failure;
    public MatchPlayback(List<I> matches) {
        this.matches=matches;
        if(!matches.isEmpty()) { boolean all=true;for(I match:matches)all &= match.e();
            if(all) {status="FINISHED";half=2;minute=45+matches.get(0).ib()[1];}
        }
    }
    public void begin() {
        if(!status.equals("NOT_STARTED"))throw new IllegalStateException("Match already started");
        if(matches.isEmpty())throw new IllegalStateException("No matches on current date");
        for(I match:matches) {
            if(match.e())throw new IllegalStateException("Match already played");
            if(match.hu()!=0 || match.hw()!=0 || !match.hE().isEmpty())throw new IllegalStateException("Resuming an in-progress match is not supported yet");
            if(match.hy()==null || match.hy().b()!=0)throw new IllegalStateException("Prototype currently supports friendly matches only");
            if(match.hl().size()!=11 || match.hm().size()!=11)throw new IllegalStateException("Each side needs exactly 11 starters");
        }
        firstExtra=random.nextInt(3); secondExtra=random.nextInt(5)+1;
        for(I match:matches) { match.ib()[0]=firstExtra;match.ib()[1]=secondExtra;match.V(); }
        status="FIRST_HALF";sequence++;
    }
    @SuppressWarnings("unchecked")
    public boolean tick() {
        if(status.equals("FINISHED"))return true;
        if(status.equals("NOT_STARTED") || status.equals("FAILED"))throw new IllegalStateException("Match is not running");
        if(status.equals("HALF_TIME")) {
            half=2;minute=0;
            for(I match:matches) match.m(2,0);
            status="SECOND_HALF";sequence++;return false;
        }
        for(I match:matches) {
            int previous=match.hE().size();
            match.q(minute,half);
            for(int i=previous;i<match.hE().size();i++) {
                A event=(A)match.hE().get(i);
                if(event.b()==1 && event.el()==3)resolvePenalty(match,event,random);
            }
        }
        minute++;sequence++;
        int duration=45+(half==1?firstExtra:secondExtra);
        if(minute>=duration) {
            if(half==1)status="HALF_TIME";
            else {
                for(I match:matches) { match.a(true); match.hc().I(false);match.hd().I(false); }
                status="FINISHED";
            }
        }
        return status.equals("FINISHED");
    }
    // Same conversion chance used by the original a.gb penalty dialog, resolved on the host.
    static void resolvePenalty(I match,A event,Random random) {
        F taker=event.eo();
        F keeper=I.G(event.cu()==match.hc()?match.hq():match.hp());
        int chance=70;
        if(taker!=null) {
            if(taker.getCr1()==9 || taker.getCr2()==9 || taker.gm())chance+=10;
            if(taker.ff())chance+=5;
        }
        if(keeper!=null) {
            if(keeper.getCr1()==1 || keeper.getCr2()==1)chance-=10;
            if(keeper.gm())chance-=10;
            if(keeper.ff())chance-=5;
        }
        int side=event.cu()==match.hc()?0:1;
        match.hA()[side]++;
        if(random.nextInt(100)+1<=chance) {
            match.hW().eG(side);if(side==0)match.hv();else match.hx();
            match.hZ()[side]++;if(taker!=null)taker.gB().z();
        } else {
            event.a(7);if(taker!=null)taker.gB().tF();match.ia()[side]++;
        }
        event.R(3);
    }
    public void fail(Throwable error) { status="FAILED";failure=error.getClass().getSimpleName()+": "+error.getMessage();sequence++; }
    public Map<String,Object> snapshot() {
        Map<String,Object> result=new LinkedHashMap<String,Object>();
        result.put("status",status);result.put("half",half);result.put("minute",half==1?minute:45+minute);
        result.put("halfMinute",minute);result.put("sequence",sequence);result.put("failure",failure);
        List<Object> rows=new ArrayList<Object>();
        for(int index=0;index<matches.size();index++) {
            I match=matches.get(index);
            Map<String,Object> row=new LinkedHashMap<String,Object>();
            row.put("id",index);row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());
            row.put("homeGoals",match.hu());row.put("awayGoals",match.hw());row.put("played",match.e());
            row.put("shots",Arrays.asList(match.hA()[0],match.hA()[1]));
            row.put("possession",Arrays.asList(match.hz()[0],match.hz()[1]));
            List<Object> events=new ArrayList<Object>();
            for(int i=0;i<match.hE().size();i++) {
                A event=(A)match.hE().get(i);
                if(event.b()==8)continue;
                Map<String,Object> e=new LinkedHashMap<String,Object>();
                e.put("id",i);e.put("type",event.b());e.put("detail",event.el());e.put("half",event.en());
                e.put("minute",(event.en()==2?45:0)+event.em());
                e.put("club",event.cu()==null?null:event.cu().getNome());
                e.put("player",event.eo()==null?null:event.eo().getNome());events.add(e);
            }
            row.put("events",events);rows.add(row);
        }
        result.put("matches",rows);return result;
    }
}
