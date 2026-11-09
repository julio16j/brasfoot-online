package brasfoot;

import best.F;
import best.I;
import best.ah;
import java.io.File;
import java.util.*;

/** Keeps medical, contract and competition-ban state distinct in the visual projection. */
public final class AvailabilityProjectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});
        check(!game.currentMatches().isEmpty(),"Fixture is available for a competition-specific suspension");
        I fixture=game.currentMatches().get(0);ah hostClub=fixture.hc();F hostPlayer=(F)hostClub.kc().get(0);
        long gameDate=Data.longNumber(game.snapshot().get("dateMillis"));
        long injuryEnd=gameDate+7L*24L*60L*60L*1000L;
        long contractEnd=gameDate-24L*60L*60L*1000L;
        hostPlayer.d(injuryEnd);Data.set(hostPlayer,"eJ",contractEnd);
        hostPlayer.c(Boolean.TRUE);hostPlayer.ag(123000);hostPlayer.g(Boolean.TRUE);
        hostPlayer.aJ(31);hostPlayer.aM(32);hostPlayer.aN(33);hostPlayer.aO(34);hostPlayer.aI(35);hostPlayer.aK(36);hostPlayer.aL(37);c.a.SR.setHabilidadeIndividual(true);
        Map<String,Object> state=new LinkedHashMap<String,Object>();state.put("game",game.snapshot());state.put("playback",new MatchPlayback(game.currentMatches(),true).snapshot());
        check(Boolean.TRUE.equals(Data.map(state.get("game")).get("individualSkills")),"Snapshot carries the host's individual-skills mode");
        Map<String,Object> visualClub=null;for(Object raw:Data.list(Data.map(state.get("game")).get("visualClubs")))if(Data.number(Data.map(raw).get("clubId"))==hostClub.lk())visualClub=Data.map(raw);
        Map<String,Object> visualPlayer=null;for(Object raw:Data.list(visualClub.get("players"))){Map<String,Object> row=Data.map(raw);if(hostPlayer.gD()>=0?Data.number(row.get("playerId"))==hostPlayer.gD():hostPlayer.getNome().equals(row.get("name")))visualPlayer=row;}
        check(Data.longNumber(visualPlayer.get("injuryUntilMillis"))==injuryEnd,"Snapshot carries the real injury deadline: expected="+injuryEnd+" actual="+visualPlayer.get("injuryUntilMillis")+" playerId="+hostPlayer.gD());
        check(Data.longNumber(visualPlayer.get("contractEndMillis"))==contractEnd,"Snapshot carries the exact expired contract deadline");
        check(Boolean.TRUE.equals(visualPlayer.get("forSale"))&&Data.number(visualPlayer.get("askingPrice"))==123000&&Boolean.TRUE.equals(visualPlayer.get("loanAvailable")),"Snapshot carries authoritative sale, price and loan-listing fields");
        check(Data.list(visualPlayer.get("suspensions")).size()==Data.list(Data.get(hostPlayer,"eS")).size(),"Snapshot carries professional suspension records");
        VisualModel model=new VisualModel(root,state);F projected=null;for(Object raw:model.club(hostClub.lk()).kc()){F candidate=(F)raw;if(hostPlayer.gD()>=0?candidate.gD()==hostPlayer.gD():hostPlayer.getNome().equals(candidate.getNome()))projected=candidate;}
        check(c.a.SR.P().size()==Data.list(Data.map(state.get("game")).get("visualClubs")).size(),"Native club index contains exactly clubs from the current projection");
        int expectedIndexedPlayers=0;for(Object raw:Data.list(Data.map(state.get("game")).get("visualClubs")))expectedIndexedPlayers+=model.club(Data.number(Data.map(raw).get("clubId"))).kc().size();
        check(c.a.SR.O().size()==expectedIndexedPlayers&&c.a.SR.O().contains(projected),"Native search index contains reconstructed current players and no extra asset entries");
        check(projected!=null,"Professional player is projected");check(projected.fo()==injuryEnd,"Projection preserves actual injury deadline");
        check(Data.longNumber(Data.get(projected,"eJ"))==contractEnd,"Projection does not extend an expired contract");
        check(Boolean.TRUE.equals(projected.ft())&&projected.fl()==123000&&Boolean.TRUE.equals(projected.fz()),"Projection uses authoritative listing state instead of asset defaults");
        check(c.a.SR.isHabilidadeIndividual()&&projected.gK()==31&&projected.gN()==32&&projected.gO()==33&&projected.gP()==34&&projected.gJ()==35&&projected.gL()==36&&projected.gM()==37,"Projection preserves all seven individual skills and host mode");
        check(Data.list(Data.get(projected,"eS")).size()==Data.list(Data.get(hostPlayer,"eS")).size(),"Projection rehydrates professional suspension records");
        check(hostPlayer.fo()==injuryEnd&&Data.longNumber(Data.get(hostPlayer,"eJ"))==contractEnd,"Client projection does not mutate host player state");
        System.out.println("PASS distinct injury, exact contract expiry and competition-specific professional suspension projection");
    }
}
