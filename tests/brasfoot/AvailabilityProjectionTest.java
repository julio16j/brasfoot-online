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
        Map<String,Object> state=new LinkedHashMap<String,Object>();state.put("game",game.snapshot());state.put("playback",new MatchPlayback(game.currentMatches(),true).snapshot());
        Map<String,Object> visualClub=null;for(Object raw:Data.list(Data.map(state.get("game")).get("visualClubs")))if(Data.number(Data.map(raw).get("clubId"))==hostClub.lk())visualClub=Data.map(raw);
        Map<String,Object> visualPlayer=null;for(Object raw:Data.list(visualClub.get("players"))){Map<String,Object> row=Data.map(raw);if(hostPlayer.gD()>=0?Data.number(row.get("playerId"))==hostPlayer.gD():hostPlayer.getNome().equals(row.get("name")))visualPlayer=row;}
        check(Data.longNumber(visualPlayer.get("injuryUntilMillis"))==injuryEnd,"Snapshot carries the real injury deadline: expected="+injuryEnd+" actual="+visualPlayer.get("injuryUntilMillis")+" playerId="+hostPlayer.gD());
        check(Data.longNumber(visualPlayer.get("contractEndMillis"))==contractEnd,"Snapshot carries the exact expired contract deadline");
        check(Boolean.TRUE.equals(visualPlayer.get("forSale"))&&Data.number(visualPlayer.get("askingPrice"))==123000&&Boolean.TRUE.equals(visualPlayer.get("loanAvailable")),"Snapshot carries authoritative sale, price and loan-listing fields");
        check(Data.list(visualPlayer.get("suspensions")).size()==Data.list(Data.get(hostPlayer,"eS")).size(),"Snapshot carries professional suspension records");
        VisualModel model=new VisualModel(root,state);F projected=null;for(Object raw:model.club(hostClub.lk()).kc()){F candidate=(F)raw;if(hostPlayer.gD()>=0?candidate.gD()==hostPlayer.gD():hostPlayer.getNome().equals(candidate.getNome()))projected=candidate;}
        check(projected!=null,"Professional player is projected");check(projected.fo()==injuryEnd,"Projection preserves actual injury deadline");
        check(Data.longNumber(Data.get(projected,"eJ"))==contractEnd,"Projection does not extend an expired contract");
        check(Boolean.TRUE.equals(projected.ft())&&projected.fl()==123000&&Boolean.TRUE.equals(projected.fz()),"Projection uses authoritative listing state instead of asset defaults");
        check(Data.list(Data.get(projected,"eS")).size()==Data.list(Data.get(hostPlayer,"eS")).size(),"Projection rehydrates professional suspension records");
        check(hostPlayer.fo()==injuryEnd&&Data.longNumber(Data.get(hostPlayer,"eJ"))==contractEnd,"Client projection does not mutate host player state");
        System.out.println("PASS distinct injury, exact contract expiry and competition-specific professional suspension projection");
    }
}
