package brasfoot;

import best.F;
import best.Z;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;

/** Compares the authenticated host query predicate against native best.af results. */
public final class NativePlayerSearchTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static Map<String,String> query(String... pairs){Map<String,String> result=new LinkedHashMap<String,String>();for(int i=0;i<pairs.length;i+=2)result.put(pairs[i],pairs[i+1]);return result;}
    private static Set<Integer> ids(List<F> players){Set<Integer> result=new TreeSet<Integer>();for(F player:players)result.add(player.gD());return result;}
    private static Set<Integer> nativeIds(Map<String,String> fields){best.af filter=new best.af();if(fields.containsKey("name"))filter.setNome(fields.get("name"));if(fields.containsKey("position"))filter.bv(Integer.parseInt(fields.get("position")));if(fields.containsKey("side"))filter.bw(Integer.parseInt(fields.get("side")));
        if(fields.containsKey("strengthFrom")){filter.bx(0);filter.bM(Integer.parseInt(fields.get("strengthFrom")));filter.bN(Integer.parseInt(fields.get("strengthTo")));}
        if(fields.containsKey("ageFrom")){filter.by(0);filter.bO(Integer.parseInt(fields.get("ageFrom")));filter.bP(Integer.parseInt(fields.get("ageTo")));}
        if(fields.containsKey("valueBand"))filter.bz(Integer.parseInt(fields.get("valueBand")));
        if(fields.containsKey("characteristic1"))filter.bA(Integer.parseInt(fields.get("characteristic1")));if(fields.containsKey("characteristic2"))filter.bB(Integer.parseInt(fields.get("characteristic2")));
        if(fields.containsKey("nationality"))filter.bC(Integer.parseInt(fields.get("nationality")));
        if(fields.containsKey("playingCountry")){int country=Integer.parseInt(fields.get("playingCountry"));for(int i=0;i<c.a.SR.N().size();i++)if(((Z)c.a.SR.N().get(i)).jc()==country)filter.bD(i);}
        String[][] skills={{"goalkeeping","gol"},{"defence","des"},{"speed","vel"},{"finishing","fin"},{"arm","arm"},{"technique","tec"},{"passing","pas"}};
        for(String[] skill:skills)if(fields.containsKey(skill[0]+"From"))filter.b(skill[1],Integer.parseInt(fields.get(skill[0]+"From")),Integer.parseInt(fields.get(skill[0]+"To")));
        filter.E(Boolean.parseBoolean(fields.get("star")));filter.F(Boolean.parseBoolean(fields.get("worldClass")));filter.G(Boolean.parseBoolean(fields.get("loanAvailable")));filter.H(Boolean.parseBoolean(fields.get("forSale")));
        List<F> players=new ArrayList<F>();for(Object raw:filter.D(false))players.add((F)raw);return ids(players);}
    public static void main(String[] args)throws Exception{
        PrintStream original=System.out;System.setOut(new PrintStream(new OutputStream(){public void write(int value){}}));GameAdapter game;try{game=CareerEngine.createByAssets(new File(args[0]),new String[]{"corinthians_bra"},null);}finally{System.setOut(original);}
        CareerManagement management=new CareerManagement(game);management.ensureIds();best.f state=c.a.SR;
        check(state.O().size()>10000,"Fixture should contain the complete generated world index");
        List<Map<String,String>> matrix=new ArrayList<Map<String,String>>();matrix.add(query());F sample=(F)state.O().get(0);matrix.add(query("name",sample.getNome().substring(0,Math.min(4,sample.getNome().length()))));
        matrix.add(query("position",String.valueOf(sample.getPosicao()),"side",String.valueOf(sample.getLado())));
        matrix.add(query("strengthFrom",String.valueOf(sample.fi()),"strengthTo",String.valueOf(sample.fi()),"ageFrom",String.valueOf(sample.getIdade()),"ageTo",String.valueOf(sample.getIdade())));
        matrix.add(query("valueBand","0"));matrix.add(query("characteristic1",String.valueOf(sample.getCr1()),"characteristic2",String.valueOf(sample.getCr2())));
        matrix.add(query("nationality",String.valueOf(sample.getPais())));if(!state.N().isEmpty())matrix.add(query("playingCountry",String.valueOf(((Z)state.N().get(0)).jc())));
        matrix.add(query("goalkeepingFrom",String.valueOf(sample.gK()),"goalkeepingTo",String.valueOf(sample.gK()),"armFrom",String.valueOf(sample.gO()),"armTo",String.valueOf(sample.gO())));
        matrix.add(query("speedFrom",String.valueOf(sample.gJ()),"speedTo",String.valueOf(sample.gJ()),"passingFrom",String.valueOf(sample.gM()),"passingTo",String.valueOf(sample.gM())));
        matrix.add(query("star","true"));matrix.add(query("worldClass","true"));matrix.add(query("loanAvailable","true"));matrix.add(query("forSale","true"));
        for(Map<String,String> filters:matrix){Set<Integer> expected=nativeIds(filters),actual=ids(NativePlayerSearch.find(state,filters));check(expected.equals(actual),"Host query differs from native af.D(false) for filters "+filters);}
        int coach=((best.al)state.M().get(0)).lT();Map<String,Object> response=management.searchPlayers(coach,query("name",sample.getNome().substring(0,Math.min(4,sample.getNome().length()))));
        check(Data.number(response.get("total"))>0,"Host query response should return matching world players");Map<String,Object> row=Data.map(Data.list(response.get("players")).get(0));
        StringBuilder longPrefix=new StringBuilder();for(int i=0;i<121;i++)longPrefix.append('Z');check(Data.number(management.searchPlayers(coach,query("name",longPrefix.toString())).get("total"))==0,"Long name prefixes should follow the native predicate without an online length ceiling");
        check(row.containsKey("skillArm")&&row.containsKey("skillSpeedNative")&&row.containsKey("clubCountry"),"Search DTO must preserve native skill semantics and club identity");
        Map<String,Object> first=management.searchPlayers(coach,query("offset","0","limit","1000")),second=management.searchPlayers(coach,query("offset","1000","limit","1000"));
        check(Data.number(first.get("total"))==state.O().size()&&Data.number(second.get("offset"))==1000,"Unfiltered result pages must report the complete native universe and offset");
        Set<Integer> pageIds=new HashSet<Integer>();for(Object raw:Data.list(first.get("players")))pageIds.add(Data.number(Data.map(raw).get("playerId")));for(Object raw:Data.list(second.get("players")))check(pageIds.add(Data.number(Data.map(raw).get("playerId"))),"Adjacent pages must not repeat player IDs");
        check(Data.number(first.get("nextOffset"))==1000&&Data.number(second.get("nextOffset"))==2000,"Paged response must expose the next offset");
        int clubsBefore=state.P().size(),playersBefore=state.O().size();List<F> detached=NativeTransferSearch.projectRows(Data.list(first.get("players")),((best.al)state.M().get(0)).fg());
        check(state.P().size()==clubsBefore&&state.O().size()==playersBefore,"Detached search results must not mutate native club or player indexes");
        for(F player:detached)if(player.fg()!=null&&player.fg()!=((best.al)state.M().get(0)).fg()){check(player.fg().kc().contains(player),"Detached seller club must retain the selected player for native Times details");check(player.fg().ka()!=null,"Detached seller projection must include a manager for the native Times header");}
        List<F> paginationExtras=new ArrayList<F>();for(int i=0;i<1300;i++){F extra=new F();extra.setNome("Pagination fixture "+i);extra.setPosicao(0);extra.setIdade(20);state.O().add(extra);paginationExtras.add(extra);}Map<String,Object> boundaryPage=management.searchPlayers(coach,query("offset","50000","limit","1000"));Map<String,Object> overflowPage=management.searchPlayers(coach,query("offset","51000","limit","1000"));check(Data.number(boundaryPage.get("nextOffset"))==51000&&Data.number(overflowPage.get("offset"))==51000&&Data.list(overflowPage.get("players")).size()>0,"Search pagination must continue beyond the former 50,000 offset ceiling");check(Data.number(overflowPage.get("total"))>51000&&overflowPage.get("nextOffset")==null,"The final page beyond 51,000 must report the complete result set");try{management.searchPlayers(coach,query("offset",String.valueOf(Data.number(overflowPage.get("total"))+1),"limit","1000"));throw new AssertionError("Offset beyond the result set must be rejected");}catch(IllegalArgumentException expected){}
        SessionHost host=new SessionHost(game,new File(args[0],"sessions-native-player-search").toPath(),coach,false,10000);try{
            host.startHttp("127.0.0.1",0);Field tokenField=SessionHost.class.getDeclaredField("hostToken");tokenField.setAccessible(true);SessionApi api=new SessionApi("http://127.0.0.1:"+host.httpPort());api.token((String)tokenField.get(host));
            Map<String,Object> remote=api.call("/players/search",query("name",sample.getNome().substring(0,Math.min(4,sample.getNome().length())),"limit","25"));
            check(Data.number(remote.get("total"))>0&&Data.list(remote.get("players")).size()>0,"Authenticated search endpoint must return host results");
            Map<String,Object> longNameSearch=api.call("/players/search",query("name",longPrefix.toString(),"limit","25"));check(Data.number(longNameSearch.get("total"))==0,"HTTP search must accept a name prefix longer than 120 characters");
            if(!state.N().isEmpty()){String countryId=String.valueOf(((Z)state.N().get(0)).jc());api.call("/players/search",query("playingCountry",countryId,"limit","25"));try{api.call("/players/search",query("playingCountry","999","limit","25"));throw new AssertionError("Unknown playing-country ID must be rejected by the host");}catch(java.io.IOException expected){}}
            Map<String,Object> academy=api.call("/club-academy",query("clubId",String.valueOf(sample.fg().lk()),"queryRevision",String.valueOf(remote.get("queryRevision"))));check(Data.number(academy.get("clubId"))==sample.fg().lk()&&Data.list(academy.get("players")).size()==sample.fg().ky().size(),"Academy endpoint must return the complete host roster for the requested club");check(String.valueOf(academy.get("queryRevision")).equals(String.valueOf(remote.get("queryRevision"))),"Academy response must preserve the career query revision");if(!Data.list(academy.get("players")).isEmpty())check(Data.number(Data.map(Data.list(academy.get("players")).get(0)).get("clubId"))==sample.fg().lk(),"Academy rows must preserve their authoritative club identity");
            Map<String,Object> agenda=api.call("/club-agenda",query("clubId",String.valueOf(sample.fg().lk()),"queryRevision",String.valueOf(remote.get("queryRevision"))));Map<String,Object> expectedAgenda=CareerViews.clubAgenda(sample.fg().lk());check(Data.number(agenda.get("clubId"))==sample.fg().lk()&&Data.list(agenda.get("fixtures")).size()==Data.list(expectedAgenda.get("fixtures")).size(),"Agenda endpoint must return every host fixture for the requested club");check(String.valueOf(agenda.get("queryRevision")).equals(String.valueOf(remote.get("queryRevision"))),"Agenda response must preserve the career query revision");
            try{api.call("/club-agenda",query("clubId",String.valueOf(sample.fg().lk()),"queryRevision","-1"));throw new AssertionError("Stale agenda revision must be rejected");}catch(java.io.IOException expected){}
            try{api.call("/club-agenda",query("clubId","-1","queryRevision",String.valueOf(remote.get("queryRevision"))));throw new AssertionError("Unknown agenda club must be rejected");}catch(java.io.IOException expected){}
            try{api.call("/club-academy",query("clubId",String.valueOf(sample.fg().lk()),"queryRevision","-1"));throw new AssertionError("Stale academy revision must be rejected");}catch(java.io.IOException expected){}
            try{api.call("/club-academy",query("clubId","-1"));throw new AssertionError("Unknown academy club must be rejected");}catch(java.io.IOException expected){}
            try{new SessionApi("http://127.0.0.1:"+host.httpPort()).call("/club-academy",query("clubId",String.valueOf(sample.fg().lk())));throw new AssertionError("Academy endpoint must reject unauthenticated callers");}catch(java.io.IOException expected){}
            Map<String,Object> stable=api.call("/players/search",query("limit","25","offset","25","queryRevision",String.valueOf(remote.get("queryRevision"))));
            check(Data.number(stable.get("offset"))==25,"Paged HTTP query must accept the revision returned by the first page");
            Map<String,Object> largeFirst=api.call("/players/search",query("limit","25","offset","0"));Map<String,Object> largeLast=api.call("/players/search",query("limit","25","offset","51000","queryRevision",String.valueOf(largeFirst.get("queryRevision"))));check(Data.number(largeLast.get("total"))>51000&&Data.number(largeLast.get("offset"))==51000&&!Data.list(largeLast.get("players")).isEmpty(),"Authenticated HTTP pagination must serve pages beyond offset 51,000");
            try{api.call("/players/search",query("limit","25","offset","25"));throw new AssertionError("Continuation pages must require the first page revision");}catch(java.io.IOException expected){}
            try{api.call("/players/search",query("limit","1","queryRevision","-1"));throw new AssertionError("Stale search revision must be rejected");}catch(java.io.IOException expected){}
            try{new SessionApi("http://127.0.0.1:"+host.httpPort()).call("/players/search",query("limit","1"));throw new AssertionError("Search endpoint must reject unauthenticated callers");}catch(java.io.IOException expected){}
            Field storageFailed=SessionHost.class.getDeclaredField("storageFailed");storageFailed.setAccessible(true);storageFailed.setBoolean(host,true);try{api.call("/players/search",query("limit","1"));throw new AssertionError("Search must be unavailable when the host cannot checkpoint IDs");}catch(java.io.IOException expected){}finally{storageFailed.setBoolean(host,false);}
            best.al searchingCoach=(best.al)state.y(coach);best.ah managedTeam=searchingCoach.fg();searchingCoach.n(null);try{api.call("/players/search",query("limit","1"));throw new AssertionError("A coach without a club must not access player search");}catch(java.io.IOException expected){check(expected.getMessage().contains("Choose a club"),"No-club search rejection must identify the missing club context");}finally{searchingCoach.n(managedTeam);}
        }finally{host.stopHttp();host.close();state.O().removeAll(paginationExtras);}
        rejects(query("ageFrom","18"),"Half ranges must be rejected");rejects(query("unexpected","1"),"Unknown filters must be rejected");rejects(query("star","yes"),"Invalid flags must be rejected");rejects(query("position","999"),"Out of range positions must be rejected");
        System.out.println("PASS host player search matches native af.D(false) across default, identity, range, nationality, skill and flag filters; world="+state.O().size());
    }
    private static void rejects(Map<String,String> fields,String message){try{NativePlayerSearch.find(c.a.SR,fields);throw new AssertionError(message);}catch(IllegalArgumentException expected){}}
}
