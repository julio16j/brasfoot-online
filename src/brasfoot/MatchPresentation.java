package brasfoot;

import best.I;
import best.at;
import best.ah;
import best.Z;
import java.util.*;

/** Selects competitions for the client presentation without changing host fixtures. */
final class MatchPresentation {
    private MatchPresentation() { }
    static List<I> competitions(List<I> fixtures,I ownMatch,Map<String,Object> values){
        IdentityHashMap<at,Boolean> hasHuman=new IdentityHashMap<at,Boolean>();
        for(I match:fixtures)if(match.hy()!=null && (human(match.hc())||human(match.hd())))hasHuman.put(match.hy(),Boolean.TRUE);
        LinkedHashSet<at> selected=new LinkedHashSet<at>();
        if(ownMatch!=null && ownMatch.hy()!=null)selected.add(ownMatch.hy());
        for(I match:fixtures){at competition=match.hy();if(competition==null)continue;
            if(Boolean.TRUE.equals(hasHuman.get(competition))){selected.add(competition);continue;}
            if(visibleWithoutHuman(competition,match,values))selected.add(competition);
        }
        if(selected.isEmpty() && ownMatch!=null && ownMatch.hy()!=null)selected.add(ownMatch.hy());
        List<I> result=new ArrayList<I>();for(I match:fixtures)if(match.hy()!=null && selected.contains(match.hy()))result.add(match);
        if(result.isEmpty() && ownMatch!=null)result.add(ownMatch);else if(result.isEmpty() && !fixtures.isEmpty())result.add(fixtures.get(0));
        return result;
    }
    private static boolean visibleWithoutHuman(at competition,I match,Map<String,Object> values){
        int type=competition.b();int country=countryId(competition,match);
        if(type==1){
            Object rawWatched=values.get("watchedLeagueCountryIds");boolean watched=rawWatched instanceof List && ((List<?>)rawWatched).contains(country);
            return !Boolean.TRUE.equals(values.get("hideNonHumanNationalLeagues")) || watched;
        }
        if((type==3 || type==10) && Boolean.TRUE.equals(values.get("hideStateRegionalOutsideBrazil")) && country!=29)return false;
        if(type==10 && Boolean.TRUE.equals(values.get("hideRegionalsWithoutHumanHistory")) && !regionalHasHumanContext(competition,values))return false;
        return true;
    }
    private static boolean regionalHasHumanContext(at competition,Map<String,Object> values){
        if(!(competition.ir() instanceof best.w))return false;best.w region=(best.w)competition.ir();
        for(Object raw:c.a.SR.M()){best.al coach=(best.al)raw;ah current=coach.fg();if(current!=null && region.j(current))return true;
            if(current==null && !Boolean.TRUE.equals(values.get("hideStateRegionalOutsideBrazil")) && coach.lF()!=null && region.j(coach.lF()))return true;}
        return false;
    }
    private static int countryId(at competition,I match){
        Z country=competition.iq();if(country!=null)return SessionOptions.countryId(country);
        ah home=match.hc();return home==null?-1:home.getPais();
    }
    private static boolean human(ah club){return club!=null && Boolean.TRUE.equals(club.jZ());}
    static Map<String,Object> playbackOnly(Map<String,Object> values){return Data.map(values.get("nonHumanCompetitionPlayback"));}
    static String key(Map<String,Object> values){Map<String,Object> filters=new LinkedHashMap<String,Object>();for(String key:new String[]{"groupStateCompetitions","hideNonHumanNationalLeagues","hideStateRegionalOutsideBrazil","hideRegionalsWithoutHumanHistory","watchedLeagueCountryIds","nonHumanCompetitionPlayback"})filters.put(key,values.get(key));return Json.write(filters);}
}
