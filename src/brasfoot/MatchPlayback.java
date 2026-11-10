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
    private final boolean career;
    private final List<I> liveMatches=new ArrayList<I>();
    public MatchPlayback(List<I> matches) {
        this(matches,false);
    }
    public MatchPlayback(List<I> matches,boolean career) {
        this.career=career;
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
            if(!career && (match.hy()==null || match.hy().b()!=0))throw new IllegalStateException("Prototype currently supports friendly matches only");
        }
        if(career)best.ah.kh();
        for(I match:matches)if((!career && (match.hl().size()!=11 || match.hm().size()!=11)) || match.hl().isEmpty() || match.hm().isEmpty())throw new IllegalStateException("Invalid lineup: "+match.hc().getNome()+"="+match.hl().size()+", "+match.hd().getNome()+"="+match.hm().size());
        firstExtra=random.nextInt(3); secondExtra=random.nextInt(5)+1;
        for(I match:matches) {
            match.o(match.hG());match.ib()[0]=firstExtra;match.ib()[1]=secondExtra;match.V();
            if(match.hc().jZ() || match.hd().jZ())liveMatches.add(match);
            else match.a(true);
        }
        status="FIRST_HALF";sequence++;
    }
    public boolean isLive(){return status.equals("FIRST_HALF") || status.equals("SECOND_HALF");}
    public boolean acceptsAdjustments(){return isLive() || status.equals("HALF_TIME");}
    public String status(){return status;}
    public long sequence(){return sequence;}
    public int half(){return half;}
    public int halfMinute(){return minute;}
    public synchronized void validateSubstitution(I match,int side,F outgoing,F incoming){
        if(!acceptsAdjustments() || !matches.contains(match))throw new IllegalStateException("This match is not accepting adjustments");
        if(side<0 || side>1)throw new IllegalArgumentException("Invalid team side");
        int remaining=match.aR(side);if(remaining<=0)throw new IllegalStateException("No substitutions remain");
        List<F> active=side==0?match.hp():match.hq(),bench=side==0?match.hn():match.ho();
        if(!active.contains(outgoing))throw new IllegalArgumentException("The player leaving is not on the field");
        if(!bench.contains(incoming))throw new IllegalArgumentException("The player entering is not on the bench");
        if(outgoing==incoming)throw new IllegalArgumentException("Choose two different players");
        if((outgoing.getPosicao()==0)!=(incoming.getPosicao()==0))throw new IllegalArgumentException("A goalkeeper can only be replaced by another goalkeeper");
    }
    public synchronized void substitute(I match,int side,F outgoing,F incoming){
        validateSubstitution(match,side,outgoing,incoming);
        int remaining=match.aR(side);
        match.a(side,outgoing,incoming,half,minute,outgoing.fT());
        if(match.aR(side)!=remaining-1)throw new IllegalStateException("The original engine did not apply the substitution");
        sequence++;
    }
    @SuppressWarnings("unchecked")
    public boolean tick() {
        if(status.equals("FINISHED"))return true;
        if(status.equals("NOT_STARTED") || status.equals("FAILED"))throw new IllegalStateException("Match is not running");
        if(status.equals("HALF_TIME")) {
            throw new IllegalStateException("Waiting for all coaches to confirm the second half");
        }
        for(I match:liveMatches) {
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
                for(I match:liveMatches) {
                    if(career && match.hS() && match.hP() && match.hk()) {
                        int home=random.nextInt(7)+2,comparison=random.nextInt(7)+2;
                        int away=home>=comparison?home-1:home+1;
                        match.i(new int[]{home,away});match.u(home>away?match.hc():match.hd());
                    }
                    match.a(true); match.hc().I(false);match.hd().I(false);
                }
                if(career)CareerEngine.completeDate();
                status="FINISHED";
            }
        }
        return status.equals("FINISHED");
    }
    public void resumeSecondHalf(){
        if(!status.equals("HALF_TIME"))throw new IllegalStateException("The match is not at halftime");
        half=2;minute=0;for(I match:liveMatches)match.m(2,0);status="SECOND_HALF";sequence++;
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
            boolean concealed=career && !liveMatches.contains(match) && !status.equals("FINISHED") && !status.equals("NOT_STARTED");
            Map<String,Object> row=new LinkedHashMap<String,Object>();
            row.put("id",index);row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());
            row.put("homeId",match.hc().lk());row.put("awayId",match.hd().lk());
            row.put("homeHuman",Boolean.TRUE.equals(match.hc().jZ()));row.put("awayHuman",Boolean.TRUE.equals(match.hd().jZ()));
            row.put("competition",match.hy()==null?"Partida":match.hy().getNome());
            row.put("competitionType",match.hy()==null?0:match.hy().b());
            row.put("competitionId",match.hy()==null?-1:match.hy().gD());
            row.put("competitionKey",CompetitionIdentity.key(match.hy()));
            row.put("phaseLabel",match.hG());row.put("stadium",match.ev()!=null?match.ev().dS():match.ik());
            row.put("attendance",concealed?null:match.hU());row.put("attendanceBySection",concealed?null:integers(match.hT()));row.put("revenue",concealed?null:match.hN());row.put("financialDataAvailable",!concealed);
            boolean penaltiesAvailable=!concealed&&match.e()&&match.hQ()[0]>=0&&match.hQ()[1]>=0;row.put("penaltiesAvailable",penaltiesAvailable);row.put("homePenalties",penaltiesAvailable?match.hQ()[0]:null);row.put("awayPenalties",penaltiesAvailable?match.hQ()[1]:null);row.put("winnerClubId",!concealed&&match.e()&&match.hR()!=null?match.hR().lk():null);
            row.put("homeGoals",match.hu());row.put("awayGoals",match.hw());row.put("played",!concealed && match.e());
            row.put("shots",Arrays.asList(match.hA()[0],match.hA()[1]));
            row.put("possession",Arrays.asList(match.hz()[0],match.hz()[1]));
            row.put("shotsOnTarget",integers(match.hZ()));row.put("shotsOffTarget",integers(match.ia()));
            row.put("tackles",integers(match.hB()));row.put("passErrors",integers(match.hC()));row.put("fouls",integers(match.hD()));
            List<Object> events=new ArrayList<Object>();
            for(int i=0;i<match.hE().size();i++) {
                A event=(A)match.hE().get(i);
                if(event.b()==8)continue;
                if(concealed && (event.en()>half || (event.en()==half && event.em()>=minute)))continue;
                Map<String,Object> e=new LinkedHashMap<String,Object>();
                e.put("id",i);e.put("type",event.b());e.put("detail",event.el());e.put("half",event.en());
                e.put("minute",(event.en()==2?45:0)+event.em());
                e.put("club",event.cu()==null?null:event.cu().getNome());
                if(event.cu()!=null && event.cu().lk()>=0)e.put("clubId",event.cu().lk());
                e.put("player",event.eo()==null?null:event.eo().getNome());
                if(event.eo()!=null && event.eo().gD()>=0)e.put("playerId",event.eo().gD());
                e.put("player2",event.ep()==null?null:event.ep().getNome());if(event.ep()!=null && event.ep().gD()>=0)e.put("player2Id",event.ep().gD());events.add(e);
            }
            if(concealed){int home=0,away=0;for(Object raw:events){Map<String,Object> event=Data.map(raw);if(Data.number(event.get("type"))==1 && event.get("clubId")!=null){if(match.hc().lk()==Data.number(event.get("clubId")))home++;else if(match.hd().lk()==Data.number(event.get("clubId")))away++;}}row.put("homeGoals",home);row.put("awayGoals",away);
                for(String key:Arrays.asList("shots","possession","shotsOnTarget","shotsOffTarget","tackles","passErrors","fouls"))row.put(key,Arrays.asList(null,null));
                row.put("statisticsAvailable",false);
            }else row.put("statisticsAvailable",true);
            row.put("events",events);rows.add(row);
        }
        result.put("matches",rows);return result;
    }
    private static List<Integer> integers(int[] values){List<Integer> result=new ArrayList<Integer>();for(int value:values)result.add(value);return result;}
}
