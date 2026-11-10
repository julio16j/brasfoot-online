package brasfoot;

import best.F;
import best.H;
import best.ap;
import best.at;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Prepares and reconnects only the transient native references backed by persisted IDs. */
final class NativeReferencePersistence {
    private NativeReferencePersistence(){ }

    static void prepare(best.f state){
        Map<Integer,at> ids=new HashMap<Integer,at>();List<at> competitions=new ArrayList<at>();Map<at,Boolean> scheduled=new IdentityHashMap<at,Boolean>();int nextId=0;
        for(Object raw:state.R())for(Object item:((best.a)raw).t()){
            at competition=(at)item;scheduled.put(competition,Boolean.TRUE);if(!competitions.contains(competition))competitions.add(competition);int id=competition.gD();
            if(id>=0){at previous=ids.put(id,competition);if(previous!=null&&previous!=competition)throw new IllegalStateException("Duplicate competition identity before save: "+id);nextId=Math.max(nextId,id+1);}
        }
        for(F player:players(state))for(Object raw:player.gH()){
            H suspension=(H)raw;at competition=suspension.gS();if(competition==null||!scheduled.containsKey(competition)){int id=suspension.gX();at canonical=ids.get(id);if(id<0||canonical==null)throw new IllegalStateException("Orphan discipline reference before save: competition id "+id);suspension.m(canonical);}
        }
        for(at competition:competitions)if(competition.gD()<0)competition.az(nextId++);
        for(Object raw:state.O())((F)raw).aC(1);
        for(Object raw:state.Q())((F)raw).aC(2);
        for(F player:players(state))for(Object raw:player.gH()){
            H suspension=(H)raw;at competition=suspension.gS();
            if(competition==null||!scheduled.containsKey(competition)||competition.gD()<0)throw new IllegalStateException("Orphan discipline reference before save");suspension.gY();if(suspension.gX()!=competition.gD())throw new IllegalStateException("Discipline identity was not prepared for save");
        }
        for(Object raw:state.bo())((ap)raw).mb();
    }

    static void reconnect(best.f state){
        Map<Integer,at> competitions=new HashMap<Integer,at>();
        for(Object raw:state.R())for(Object item:((best.a)raw).t()){
            at competition=(at)item;int id=competition.gD();if(id<0)continue;
            at previous=competitions.put(id,competition);if(previous!=null&&previous!=competition)throw new IllegalStateException("Duplicate competition identity in save: "+id);
        }
        for(F player:players(state))for(Object raw:player.gH()){
            H suspension=(H)raw;int id=suspension.gX();at competition=competitions.get(id);if(id<0||competition==null)throw new IllegalStateException("Orphan discipline reference in save: competition id "+id);suspension.m(competition);if(suspension.gS()!=competition)throw new IllegalStateException("Discipline reference did not resolve canonically: competition id "+id);
        }
        Map<String,F> players=new HashMap<String,F>();
        index(players,state.O(),1);index(players,state.Q(),2);
        for(Object raw:state.bo()){
            ap history=(ap)raw;int id=history.gD(),type=Data.number(Data.get(history,"ej"));
            if(id<0)continue;F player=players.get(type+":"+id);if(player!=null)history.a(player);
        }
    }

    private static List<F> players(best.f state){List<F> result=new ArrayList<F>(state.O().size()+state.Q().size());result.addAll((List<F>)(List<?>)state.O());result.addAll((List<F>)(List<?>)state.Q());return result;}
    private static void index(Map<String,F> result,List<?> source,int expectedType){for(Object raw:source){F player=(F)raw;if(player.gD()<0)continue;String key=expectedType+":"+player.gD();F previous=result.put(key,player);if(previous!=null&&previous!=player)throw new IllegalStateException("Duplicate player identity in save: "+key);}}
}
