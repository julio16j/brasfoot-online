package brasfoot;

import best.F;
import best.H;
import best.I;
import best.ah;
import java.io.File;
import java.util.*;

/** Checks that a client projection preserves exact medical, contract and competition state. */
public final class AvailabilityProjectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    @SuppressWarnings("unchecked")
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);GameAdapter adapter=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        ah home=((best.al)c.a.SR.M().get(0)).fg();F player=(F)home.kc().get(0);long now=c.a.SR.bb().getTimeInMillis();

        Map<String,Object> restGame=adapter.snapshot();Data.list(restGame.get("matches")).clear();Map<String,Object> rest=new LinkedHashMap<String,Object>();rest.put("game",restGame);Map<String,Object> noPlayback=new LinkedHashMap<String,Object>();noPlayback.put("matches",new ArrayList<Object>());rest.put("playback",noPlayback);
        Map<String,Object> coach=Data.map(Data.list(restGame.get("coaches")).get(0));Map<String,Object> coachPlayer=Data.map(Data.list(coach.get("players")).get(0));coachPlayer.put("eligible",false);coachPlayer.put("eligibilityContext",null);
        player.d(now+1234567L);Data.set(player,"eJ",now-987654L);
        player.c(Boolean.TRUE);player.ag(123000);player.g(Boolean.TRUE);player.aJ(31);player.aM(32);player.aN(33);player.aO(34);player.aI(35);player.aK(36);player.aL(37);c.a.SR.setHabilidadeIndividual(true);
        f.a firstCompetition=new f.a();firstCompetition.setNome("Copa Homonima");firstCompetition.F(1,0);firstCompetition.az(101);
        f.a secondCompetition=new f.a();secondCompetition.setNome("Copa Homonima");secondCompetition.F(3,0);secondCompetition.az(202);
        I current=adapter.currentMatches().get(0);current.hy().setNome("Copa Homonima");Data.set(current.hy(),"tR",3);current.hy().az(202);
        H suspension=new H();suspension.m(current.hy());Data.set(suspension,"ca",3);Data.set(suspension,"cb",0);suspension.gY();((List<Object>)Data.get(player,"eS")).add(suspension);
        Map<String,Object> game=adapter.snapshot();
        Map<String,Object> beforePlayback=adapter.snapshot();player.d(now+999L);Data.set(player,"eJ",now+888L);Map<String,Object> duringPlayback=adapter.snapshot();SessionHost.preserveAvailability(duringPlayback,beforePlayback);player.d(now+1234567L);Data.set(player,"eJ",now-987654L);
        Map<String,Object> beforeVisual=null,duringVisual=null;for(Object raw:Data.list(beforePlayback.get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==home.lk())beforeVisual=Data.map(Data.list(row.get("players")).get(0));}for(Object raw:Data.list(duringPlayback.get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==home.lk())duringVisual=Data.map(Data.list(row.get("players")).get(0));}
        check(Data.longNumber(duringVisual.get("injuryUntilMillis"))==Data.longNumber(beforeVisual.get("injuryUntilMillis"))&&Data.longNumber(duringVisual.get("contractEndMillis"))==Data.longNumber(beforeVisual.get("contractEndMillis"))&&Objects.equals(duringVisual.get("suspensions"),beforeVisual.get("suspensions"))&&Objects.equals(duringVisual.get("eligible"),beforeVisual.get("eligible")),"Playback visual roster must not reveal final medical or disciplinary state");
        Map<String,Object> beforeCoach=Data.map(Data.list(beforePlayback.get("coaches")).get(0)),duringCoach=Data.map(Data.list(duringPlayback.get("coaches")).get(0));Map<String,Object> beforeCoachPlayer=Data.map(Data.list(beforeCoach.get("players")).get(0)),duringCoachPlayer=Data.map(Data.list(duringCoach.get("players")).get(0));
        check(Data.longNumber(duringCoachPlayer.get("injuryUntilMillis"))==Data.longNumber(beforeCoachPlayer.get("injuryUntilMillis"))&&Data.longNumber(duringCoachPlayer.get("contractEndMillis"))==Data.longNumber(beforeCoachPlayer.get("contractEndMillis"))&&Objects.equals(duringCoachPlayer.get("eligible"),beforeCoachPlayer.get("eligible"))&&Objects.equals(duringCoachPlayer.get("eligibilityContext"),beforeCoachPlayer.get("eligibilityContext")),"Playback coach roster must retain pre-match availability context");
        I first=new I(null,0,home,(ah)((best.al)c.a.SR.M().get(1)).fg(),0,firstCompetition,null);I second=new I(null,0,home,(ah)((best.al)c.a.SR.M().get(1)).fg(),0,secondCompetition,null);
        Map<String,Object> state=new LinkedHashMap<String,Object>();state.put("game",game);
        Map<String,Object> playback=new MatchPlayback(Arrays.asList(first,second),false).snapshot();List<Object> rows=Data.list(playback.get("matches"));Map<String,Object> firstRow=Data.map(rows.get(0)),secondRow=Data.map(rows.get(1));firstRow.put("competitionKey","id:101");secondRow.put("competitionKey","id:202");playback.put("matches",rows);state.put("playback",playback);
        VisualModel restVisual=new VisualModel(root,rest);F restPlayer=(F)restVisual.club(home.lk()).kc().get(0);
        check(restPlayer.fo()==0 && !restPlayer.fP(),"No fixture context must not invent an injury");
        check(coachPlayer.get("eligibilityContext")==null,"Rest-day eligibility context must be explicit null");
        VisualModel visual=new VisualModel(root,state);F projected=(F)visual.club(home.lk()).kc().get(0);List<I> matches=visual.matches();
        check(projected.fo()==now+1234567L,"Exact injury deadline must survive projection");
        check(Data.longNumber(Data.get(projected,"eJ"))==now-987654L,"Expired contract deadline must survive projection");
        Map<String,Object> visualPlayer=null;for(Object raw:Data.list(game.get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==home.lk()){visualPlayer=Data.map(Data.list(row.get("players")).get(0));break;}}
        check(Boolean.TRUE.equals(visualPlayer.get("forSale"))&&Data.number(visualPlayer.get("askingPrice"))==123000&&Boolean.TRUE.equals(visualPlayer.get("loanAvailable")),"Snapshot preserves authoritative transfer listing fields");
        check(c.a.SR.isHabilidadeIndividual()&&projected.gK()==31&&projected.gN()==32&&projected.gO()==33&&projected.gP()==34&&projected.gJ()==35&&projected.gL()==36&&projected.gM()==37,"Projection preserves all individual skills and mode");
        check(c.a.SR.P().size()==Data.list(game.get("visualClubs")).size(),"Native club index contains exactly projected clubs");
        Map<String,Object> coachRow=Data.map(Data.list(game.get("coaches")).get(0));Map<String,Object> hostPlayer=Data.map(Data.list(coachRow.get("players")).get(0));Map<String,Object> context=Data.map(hostPlayer.get("eligibilityContext"));
        check(Boolean.TRUE.equals(context.get("injured"))&&Boolean.TRUE.equals(context.get("suspended"))&&Boolean.TRUE.equals(context.get("contractExpired")),"Snapshot must preserve separate simultaneous availability reasons");
        check(matches.size()==2 && matches.get(0).hy()!=matches.get(1).hy(),"Homonymous competitions with distinct IDs must remain distinct");
        check(matches.get(0).hy().b()==1 && matches.get(1).hy().b()==3,"Competition types must not overwrite each other");
        check(!projected.c(matches.get(0).hy()) && projected.c(matches.get(1).hy()),"Suspension must remain bound to its exact competition object");
        projected.d(0L);Data.set(projected,"eJ",now+86400000L);check(projected.a(matches.get(0),false)&&!projected.a(matches.get(1),false),"Native lineup eligibility must honor competition-scoped suspension");
        Data.set(projected,"eS",new ArrayList<Object>());Data.set(projected,"eJ",now-1L);check(!projected.a(matches.get(1),false),"Exact expired contract must continue to block native selection");
        VisualModel reconnected=new VisualModel(root,Data.parse(Json.write(state)));F reconnectedPlayer=(F)reconnected.club(home.lk()).kc().get(0);
        check(reconnectedPlayer.fo()==now+1234567L&&Data.longNumber(Data.get(reconnectedPlayer,"eJ"))==now-987654L,"Serialized reconnect snapshot must retain exact deadlines");
        check(!reconnectedPlayer.c(reconnected.matches().get(0).hy())&&reconnectedPlayer.c(reconnected.matches().get(1).hy()),"Reconnect must bind suspension to canonical competition identity again");
        check(player.fo()==now+1234567L&&Data.longNumber(Data.get(player,"eJ"))==now-987654L,"Client projection must not mutate host medical state");
        player.d(now);check(!player.fP(),"Injury ending exactly at the projected game date is recovered");player.d(now+1L);check(player.fP(),"Injury ending one millisecond after the projected game date remains active");
        player.d(0L);Data.set(player,"eS",new ArrayList<Object>());Data.set(player,"eJ",now);check(player.a(current,false),"Contract ending exactly at the projected game date remains eligible under native comparison");Data.set(player,"eJ",now-1L);check(!player.a(current,false),"Contract ending one millisecond before the game date is expired");
        System.out.println("PASS rest-day health, exact injury and contract deadlines, and competition-scoped suspension projection");
    }
}
