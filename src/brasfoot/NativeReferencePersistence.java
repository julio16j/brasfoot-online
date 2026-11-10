package brasfoot;

import best.F;
import best.H;
import best.ap;
import best.at;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Prepares and reconnects only the transient native references backed by persisted IDs. */
final class NativeReferencePersistence {
    private NativeReferencePersistence(){ }

    static void prepare(best.f state){
        Map<Integer,at> ids=new HashMap<Integer,at>();List<at> competitions=new ArrayList<at>();int nextId=0;
        for(Object raw:state.R())for(Object item:((best.a)raw).t()){
            at competition=(at)item;if(!competitions.contains(competition))competitions.add(competition);int id=competition.gD();
            if(id>=0){at previous=ids.put(id,competition);if(previous!=null&&previous!=competition)throw new IllegalStateException("Duplicate competition identity before save: "+id);nextId=Math.max(nextId,id+1);}
        }
        for(at competition:competitions)if(competition.gD()<0)competition.az(nextId++);
        for(Object raw:state.O())((F)raw).aC(1);
        for(Object raw:state.Q())((F)raw).aC(2);
        for(F player:players(state))for(Object raw:player.gH()){
            H suspension=(H)raw;at competition=suspension.gS();
            if(competition!=null&&competition.gD()>=0)suspension.gY();
            else if(competition!=null)Data.set(suspension,"ft",-1);
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
            H suspension=(H)raw;at competition=competitions.get(suspension.gX());if(competition!=null)suspension.m(competition);
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
