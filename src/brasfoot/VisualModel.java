package brasfoot;

import best.F;
import best.I;
import best.ah;
import best.al;
import java.io.*;
import java.util.*;

/** Builds presentation objects from explicit JSON fields; never simulates on a client. */
public final class VisualModel {
    private final Map<Integer,ah> clubs=new LinkedHashMap<Integer,ah>();
    private final Map<Integer,al> coaches=new LinkedHashMap<Integer,al>();
    private final List<I> matches=new ArrayList<I>();
    private final Map<Integer,I> upcoming=new HashMap<Integer,I>();
    private final Map<String,f.a> competitions=new LinkedHashMap<String,f.a>();
    private final Map<String,RemoteCompetition> remoteCompetitions=new LinkedHashMap<String,RemoteCompetition>();
    private final File root;
    public VisualModel(File root,Map<String,Object> state)throws Exception{
        this.root=root;new best.M(true);best.n.cU();c.a.SR=new best.f();c.a.afQ=new best.ay();
        initializeNativeCompetitionSlots();
        c.a.SR.bs=false;rebuild(state);
    }
    public List<I> matches(){return matches;}
    public ah club(int id){return clubs.get(id);}
    public al coach(int id){return coaches.get(id);}
    /** Safe during lineup editing: refresh training without replacing draft roster objects. */
    public void updateTraining(Map<String,Object> state){
        for(Object raw:Data.list(Data.map(state.get("game")).get("visualClubs"))){Map<String,Object> row=Data.map(raw);ah club=clubs.get(Data.number(row.get("clubId")));if(club==null)continue;
            if(row.containsKey("trainingFocus"))club.bZ(Data.number(row.get("trainingFocus")));
            if(row.containsKey("trainingAssistant"))club.M(Boolean.TRUE.equals(row.get("trainingAssistant")));
        }
    }
    static File clubAssetFile(File root,String asset)throws IOException{
        if(asset==null || asset.trim().isEmpty() || asset.equals(".") || asset.equals("..") || !asset.matches("[^\\\\/:*?\"<>|\\p{Cntrl}]+"))throw new IOException("Invalid club asset: "+asset);
        File directory=new File(root,"game/Brasfoot22-23/teams").getCanonicalFile();
        File file=new File(directory,asset+".ban").getCanonicalFile();
        if(!directory.equals(file.getParentFile()))throw new IOException("Invalid club asset: "+asset);
        return file;
    }
    @SuppressWarnings("unchecked")
    public void rebuild(Map<String,Object> state)throws Exception{
        Map<String,Object> game=Data.map(state.get("game"));
        if(game.containsKey("individualSkills"))c.a.SR.setHabilidadeIndividual(Boolean.TRUE.equals(game.get("individualSkills")));
        c.a.SR.R().clear();best.a day=new best.a();day.a().setTimeInMillis(Data.longNumber(game.get("dateMillis")));c.a.SR.R().add(day);c.a.SR.l(0);
        c.a.SR.k(Data.number(game.get("season")));c.a.SR.I(Data.number(game.get("year")));
        matches.clear();competitions.clear();remoteCompetitions.clear();for(Object raw:Data.list(Data.map(state.get("playback")).get("matches")))competition(Data.map(raw));for(Object raw:Data.list(game.get("upcoming")))competition(Data.map(raw));
        updateClubs(game);
        c.a.SR.M().clear();
        for(Object raw:Data.list(game.get("coaches"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("coachId"));
            al coach=coaches.get(id);if(coach==null){coach=new al((String)row.get("name"));Data.set(coach,"nU",id);coaches.put(id,coach);c.a.SR.L().add(coach);}if(row.containsKey("nationality"))coach.cg(Data.number(row.get("nationality")));if(row.containsKey("boardConfidence"))coach.cl(Data.number(row.get("boardConfidence")));if(row.containsKey("fanConfidence"))coach.cm(Data.number(row.get("fanConfidence")));
            ah club=row.get("clubId")==null?null:clubs.get(Data.number(row.get("clubId")));coach.n(club);coach.k(true);
            if(club!=null){Data.set(club,"mZ",coach);Data.set(club,"na",id);club.k(true);}c.a.SR.M().add(coach);
        }
        for(Object raw:Data.list(Data.map(state.get("playback")).get("matches"))){Map<String,Object> row=Data.map(raw);
            f.a competition=competition(row);
            I match=new I(null,0,clubs.get(Data.number(row.get("homeId"))),clubs.get(Data.number(row.get("awayId"))),0,competition,null);matches.add(match);
            if(row.containsKey("phaseLabel"))match.o((String)row.get("phaseLabel"));
        }
        upcoming.clear();
        for(Object raw:Data.list(game.get("upcoming"))){Map<String,Object> row=Data.map(raw);f.a competition=competition(row);
            I next=new I(null,0,clubs.get(Data.number(row.get("homeId"))),clubs.get(Data.number(row.get("awayId"))),0,competition,null);
            if(row.containsKey("phaseLabel"))next.o((String)row.get("phaseLabel"));
            day.h().remove(next);upcoming.put(Data.number(row.get("coachId")),next);
        }
        update(state);
    }
    private f.a competition(Map<String,Object> row){String key=row.containsKey("competitionKey")?String.valueOf(row.get("competitionKey")):row.containsKey("competitionId")?"id:"+Data.number(row.get("competitionId")):"legacy:"+String.valueOf(row.get("competition"))+":"+Data.number(row.get("competitionType"));f.a result=competitions.get(key);if(result==null){result=new f.a();result.setNome((String)row.get("competition"));if(row.containsKey("competitionType"))Data.set(result,"tR",Data.number(row.get("competitionType")));if(row.containsKey("competitionId"))result.az(Data.number(row.get("competitionId")));competitions.put(key,result);}return result;}
    public f.a remoteCompetition(String key){return remoteCompetitions.get(key);}
    /** The original standings screen probes national-board slots before drawing league rows. */
    private static void initializeNativeCompetitionSlots(){
        if(c.a.SR.aF()==null)c.a.SR.a(new f.r());
        if(c.a.SR.aH()==null)c.a.SR.a(new f.H());
        if(c.a.SR.aI()==null)c.a.SR.a(new f.v());
        if(c.a.SR.aK()==null)c.a.SR.a(new f.x());
        if(c.a.SR.aL()==null)c.a.SR.a(new f.u());
        if(c.a.SR.aO()==null)c.a.SR.a(new f.t());
        if(c.a.SR.aP()==null)c.a.SR.a(new f.w());
        if(c.a.SR.aQ()==null)c.a.SR.a(new f.y());
    }
    /** Rehydrates the read-only native calendar from the host without running any local match logic. */
    public void projectCareerCalendar(Map<String,Object> catalog){
        List<Object> source=Data.list(catalog.get("calendar")),tableRows=Data.list(catalog.get("tables"));Map<Integer,Integer> divisionCounts=new HashMap<Integer,Integer>();
        for(Object raw:tableRows){Map<String,Object> row=Data.map(raw);int country=Data.number(row.get("country"));divisionCounts.put(country,Math.max(divisionCounts.containsKey(country)?divisionCounts.get(country):0,Data.number(row.get("division"))));}
        for(Object raw:Data.list(catalog.get("clubs"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("clubId"));if(clubs.containsKey(id))continue;ah club=new ah();club.bX(id);Data.set(club,"dm",String.valueOf(row.get("club")));Data.set(club,"mV",row.get("asset")==null?null:String.valueOf(row.get("asset")));club.setPais(Data.number(row.get("country")));club.setDivisao(Data.number(row.get("division")));clubs.put(id,club);}
        remoteCompetitions.clear();competitions.clear();c.a.SR.R().clear();Map<Long,Integer> dateIndexes=new LinkedHashMap<Long,Integer>();
        Set<Long> dateValues=new TreeSet<Long>();for(Object raw:source)dateValues.add(Data.longNumber(Data.map(raw).get("dateMillis")));dateValues.add(Data.longNumber(Data.map(catalog).get("dateMillis")));
        int currentIndex=0;long current=Data.longNumber(Data.map(catalog).get("dateMillis"));
        for(Long millis:dateValues){best.a day=new best.a();day.a().setTimeInMillis(millis);day.a(1);int index=c.a.SR.R().size();c.a.SR.R().add(day);dateIndexes.put(millis,index);if(millis==current)currentIndex=index;}
        for(Object raw:tableRows){Map<String,Object> row=Data.map(raw);String key=String.valueOf(row.get("competitionKey"));RemoteCompetition competition=new RemoteCompetition(row,divisionCounts.get(Data.number(row.get("country"))));remoteCompetitions.put(key,competition);competitions.put(key,competition);for(Object entry:Data.list(row.get("rows"))){Map<String,Object> standing=Data.map(entry);ah club=clubs.get(Data.number(standing.get("clubId")));if(club==null)continue;competition.table.yK().add(club);best.ak stats=new best.ak(club,competition.table);String[] statFields={"nT","T","bX","d","nK","nL"};String[] statKeys={"points","played","wins","losses","goalsFor","goalsAgainst"};for(int i=0;i<statFields.length;i++)Data.set(stats,statFields[i],Data.number(standing.get(statKeys[i])));List<Object> records=(List<Object>)Data.get(club,"no");records.removeIf(value->((best.ak)value).lC()==competition.table);records.add(stats);}}
        for(Object raw:source){Map<String,Object> row=Data.map(raw);long millis=Data.longNumber(row.get("dateMillis"));int index=dateIndexes.get(millis);best.a day=(best.a)c.a.SR.R().get(index);String key=String.valueOf(row.get("competitionKey"));f.a comp=competitions.get(key);if(comp==null){comp=new f.a();comp.setNome(String.valueOf(row.get("competition")));comp.F(Data.number(row.get("competitionType")),0);if(row.get("competitionId") instanceof Number)comp.az(Data.number(row.get("competitionId")));competitions.put(key,comp);}day.a(comp);
            ah home=clubs.get(Data.number(row.get("homeId"))),away=clubs.get(Data.number(row.get("awayId")));if(home==null||away==null)continue;f.s table=comp instanceof RemoteCompetition?((RemoteCompetition)comp).table:null;
            I match=new I(table,Data.number(row.get("competitionType")),home,away,index,comp,null);if(row.get("phase")!=null)match.o(String.valueOf(row.get("phase")));if("played".equals(row.get("status"))){match.a(true);if(row.get("homeGoals") instanceof Number)Data.set(match,"fB",Data.number(row.get("homeGoals")));if(row.get("awayGoals") instanceof Number)Data.set(match,"fC",Data.number(row.get("awayGoals")));}
            // The native I constructor attaches itself to the date and registers its table/category.
        }
        c.a.SR.l(currentIndex);c.a.SR.k(Data.number(catalog.get("season")));c.a.SR.I(Data.number(catalog.get("year")));
    }
    private final class RemoteCompetition extends f.a{
        final f.s table;
        RemoteCompetition(Map<String,Object> row,int divisions){String key=String.valueOf(row.get("competitionKey"));table=new f.s();table.setNome(String.valueOf(row.get("competition")));table.a(Data.number(row.get("competitionType")));Data.set(table,"divisao",Data.number(row.get("division")));Data.set(table,"nomeLiga",String.valueOf(row.get("competition")));Data.set(table,"nomeDivisao",String.valueOf(row.get("phase")));Data.set(table,"YY",this);Data.set(table,"RT",new RemoteCountry(Data.number(row.get("country")),divisions,Data.number(row.get("division")),Data.number(row.get("relegationCount")),Data.number(row.get("directRelegationCount"))));setNome(String.valueOf(row.get("competition")));F(Data.number(row.get("competitionType")),0);if(row.get("competitionId") instanceof Number)az(Data.number(row.get("competitionId")));remoteCompetitions.put(key,this);}
        @Override public best.L[] mB(){return new best.L[]{table};}
        @Override public java.util.ArrayList mC(){java.util.ArrayList options=new java.util.ArrayList();options.add(new components.y(new best.L[]{table},"Classificação"));return options;}
    }
    private static final class RemoteCountry extends best.Z{
        RemoteCountry(int country,int divisions,int selectedDivision,int relegated,int direct){super(country);try{for(int division=1;division<=divisions;division++){Object config=Class.forName("f.B").getDeclaredConstructor().newInstance();Data.set(config,"KR",this);Data.set(config,"divisao",division);Data.set(config,"nRebaixados",division==selectedDivision?relegated:0);Data.set(config,"rebaixadosDireto",division==selectedDivision?direct:0);eb().add(config);}}catch(Exception error){throw new IllegalStateException("Original league configuration could not be projected",error);}}
        @Override public components.al As(){return new components.al();}
    }
    @SuppressWarnings("unchecked")
    private void updateClubs(Map<String,Object> game)throws Exception{
        for(Object raw:Data.list(game.get("visualClubs"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("clubId"));ah club=clubs.get(id);
            if(club==null){
                String asset=(String)row.get("asset");File file=clubAssetFile(root,asset);
                if(file.isFile())try(ObjectInputStream input=new ObjectInputStream(new FileInputStream(file))){club=new ah((e.t)input.readObject());}
                else {club=new ah();Data.set(club,"dm",row.get("club"));Data.set(club,"mV",asset);}
                club.bX(id);clubs.put(id,club);
            }
            Data.set(club,"nb",Data.longNumber(row.get("balance")));club.setPais(Data.number(row.get("country")));club.setDivisao(Data.number(row.get("division")));
            al manager=club.ka();if(manager!=null){if(row.containsKey("boardConfidence"))manager.cl(Data.number(row.get("boardConfidence")));if(row.containsKey("fanConfidence"))manager.cm(Data.number(row.get("fanConfidence")));}
            List<F> previous=new ArrayList<F>(club.kc());club.kc().clear();
            for(Object value:Data.list(row.get("players"))){Map<String,Object> p=Data.map(value);F player=null;
                for(Iterator<F> it=previous.iterator();it.hasNext();){F candidate=it.next();if(p.containsKey("playerId") && Data.number(p.get("playerId"))>=0?candidate.gD()==Data.number(p.get("playerId")):candidate.getNome().equals(p.get("name"))){player=candidate;it.remove();break;}}
                if(player==null)player=new F();player.n(club);player.setNome((String)p.get("name"));player.setPosicao(Data.number(p.get("position")));
                if(p.containsKey("playerId"))player.az(Data.number(p.get("playerId")));if(p.containsKey("country"))player.setPais(Data.number(p.get("country")));
                if(p.containsKey("seasonStats"))for(Map.Entry<String,Object> entry:Data.map(p.get("seasonStats")).entrySet())if(entry.getKey().equals("fh") || entry.getKey().equals("fi"))Data.set(player,entry.getKey(),Data.number(entry.getValue()));
                if(p.containsKey("careerAverageRating"))player.c(((Number)p.get("careerAverageRating")).doubleValue());
                player.setIdade(Data.number(p.get("age")));player.ad(Data.number(p.get("strength")));player.ai(Data.number(p.get("fitness")));player.setLado(Data.number(p.get("side")));
                player.setCr1(Data.number(p.get("skill1")));player.setCr2(Data.number(p.get("skill2")));player.setStatus(Data.number(p.get("status")));player.a(Boolean.TRUE.equals(p.get("star")));player.j(Boolean.TRUE.equals(p.get("worldClass")));
                player.as(Data.number(p.get("slot")));if(p.containsKey("injuryUntilMillis"))player.d(Data.longNumber(p.get("injuryUntilMillis")));
                Data.set(player,"et",Data.number(p.get("salary")));Data.set(player,"eu",Data.number(p.get("value")));if(p.containsKey("contractEndMillis"))Data.set(player,"eJ",Data.longNumber(p.get("contractEndMillis")));
                if(p.containsKey("forSale"))player.c((Boolean)p.get("forSale"));if(p.containsKey("askingPrice"))player.ag(Data.number(p.get("askingPrice")));if(p.containsKey("loanAvailable"))player.g((Boolean)p.get("loanAvailable"));
                if(p.containsKey("skillGoalkeeping")){player.aJ(Data.number(p.get("skillGoalkeeping")));player.aM(Data.number(p.get("skillDefence")));player.aN(Data.number(p.get("skillSpeed")));player.aO(Data.number(p.get("skillFinishing")));player.aI(Data.number(p.get("skillHeading")));player.aK(Data.number(p.get("skillTechnique")));player.aL(Data.number(p.get("skillPassing")));}
                if(p.containsKey("suspensions"))projectSuspensions(player,Data.list(p.get("suspensions")));
                club.kc().add(player);
            }
            if(row.containsKey("youth")){List<F> previousYouth=new ArrayList<F>(club.ky());club.ky().clear();for(Object value:Data.list(row.get("youth"))){Map<String,Object> p=Data.map(value);int playerId=Data.number(p.get("playerId"));F player=null;for(Iterator<F> it=previousYouth.iterator();it.hasNext();){F candidate=it.next();if(playerId>=0&&candidate.gD()==playerId){player=candidate;it.remove();break;}}if(player==null)player=new F();player.n(club);player.az(playerId);player.setNome((String)p.get("name"));player.setPosicao(Data.number(p.get("position")));player.setPais(Data.number(p.get("country")));player.setIdade(Data.number(p.get("age")));player.setLado(Data.number(p.get("side")));player.setCr1(Data.number(p.get("skill1")));player.setCr2(Data.number(p.get("skill2")));player.ad(Data.number(p.get("strength")));player.ao(Data.number(p.get("cpe")));player.ap(Data.number(p.get("development")));player.ae(Data.number(p.get("salary")));Data.set(player,"eu",Data.number(p.get("value")));player.h(Boolean.TRUE.equals(p.get("junior")));if(p.containsKey("injuryUntilMillis"))player.d(Data.longNumber(p.get("injuryUntilMillis")));if(p.containsKey("contractEndMillis"))Data.set(player,"eJ",Data.longNumber(p.get("contractEndMillis")));if(p.containsKey("suspensions"))projectSuspensions(player,Data.list(p.get("suspensions")));club.ky().add(player);}}
            List<Object> t=Data.list(row.get("tactics"));int[] settings=new int[4];for(int i=0;i<4;i++)settings[i]=Data.number(t.get(i));club.k(settings);
            if(row.containsKey("roles")){Map<String,Object> roles=Data.map(row.get("roles"));club.k(rolePlayer(club,roles.get("freeKicks")));club.j(rolePlayer(club,roles.get("captain")));club.n(rolePlayer(club,roles.get("corners")));club.o(rolePlayer(club,roles.get("falseNine")));}
            if(row.containsKey("trainingFocus"))club.bZ(Data.number(row.get("trainingFocus")));
            if(row.containsKey("trainingAssistant"))club.M(Boolean.TRUE.equals(row.get("trainingAssistant")));
            if(row.get("stadium") instanceof Map){Map<String,Object> stadiumRow=Data.map(row.get("stadium"));List<Object> capacities=Data.list(stadiumRow.get("seats")),prices=Data.list(stadiumRow.get("prices"));int[] seats=new int[4],costs=new int[4];for(int i=0;i<4;i++){seats[i]=Data.number(capacities.get(i));costs[i]=Data.number(prices.get(i));}
                best.v stadium=club.ev();if(stadium==null){int total=0;for(int value:seats)total+=value;stadium=new best.v(String.valueOf(stadiumRow.get("name")),total,club);}
                stadium.i(String.valueOf(stadiumRow.get("name")));stadium.a(seats);stadium.b(costs);stadium.o(Boolean.TRUE.equals(stadiumRow.get("suggestedPrices")));club.a(stadium);
            }
        }
        reconcileNativeIndexes(game);
    }
    private void reconcileNativeIndexes(Map<String,Object> game){List<ah> nativeClubs=c.a.SR.P();nativeClubs.clear();List<F> nativePlayers=c.a.SR.O();nativePlayers.clear();Set<F> seen=Collections.newSetFromMap(new IdentityHashMap<F,Boolean>());
        for(Object raw:Data.list(game.get("visualClubs"))){int id=Data.number(Data.map(raw).get("clubId"));ah club=clubs.get(id);if(club==null)continue;nativeClubs.add(club);for(Object player:club.kc()){F value=(F)player;if(seen.add(value))nativePlayers.add(value);}}
    }
    private void projectSuspensions(F player,List<Object> source){List<Object> records=new ArrayList<Object>();for(Object rawRecord:source){Map<String,Object> record=Data.map(rawRecord);String key=String.valueOf(record.get("competitionKey"));f.a competition=competitions.get(key);if(competition==null){Map<String,Object> synthetic=new LinkedHashMap<String,Object>();synthetic.put("competitionKey",key);synthetic.put("competitionId",record.get("competitionId"));synthetic.put("competition",record.get("competition"));synthetic.put("competitionType",record.get("competitionType"));competition=competition(synthetic);}best.H suspension=new best.H();suspension.m(competition);for(int i=0;i<Data.number(record.get("yellowCards"));i++)suspension.cz();for(int i=0;i<Data.number(record.get("redCards"));i++)suspension.cA();suspension.gY();records.add(suspension);}Data.set(player,"eS",records);}
    private static F rolePlayer(ah club,Object id){if(id==null)return null;int playerId=Data.number(id);for(Object raw:club.kc()){F player=(F)raw;if(player.gD()==playerId)return player;}throw new IllegalStateException("Special role references an unknown player");}
    @SuppressWarnings("unchecked")
    public void update(Map<String,Object> state)throws Exception{
        Map<String,Object> game=Data.map(state.get("game"));updateClubs(game);syncStadiumExpansions(game);
        for(Object raw:Data.list(game.get("matches"))){Map<String,Object> row=Data.map(raw);I match=findMatch(Data.number(row.get("homeId")),Data.number(row.get("awayId")));if(match==null)continue;
            projectSide(match,true,Data.list(row.get("homeStarters")),Data.list(row.get("homeBench")));
            projectSide(match,false,Data.list(row.get("awayStarters")),Data.list(row.get("awayBench")));
        }
    }
    private void syncStadiumExpansions(Map<String,Object> game){
        c.a.SR.bw().clear();for(Object raw:Data.list(game.get("stadiumExpansions"))){Map<String,Object> row=Data.map(raw);ah club=clubs.get(Data.number(row.get("clubId")));if(club==null || club.ev()==null)continue;
            best.B expansion=new best.B();expansion.a(club.ev());Calendar end=Calendar.getInstance();end.setTimeInMillis(Data.longNumber(row.get("endMillis")));expansion.c(end);List<Object> values=Data.list(row.get("seats"));int[] seats=new int[4];for(int i=0;i<4;i++)seats[i]=Data.number(values.get(i));expansion.c(seats);c.a.SR.bw().add(expansion);
        }
    }
    private I findMatch(int home,int away){for(I match:matches)if(match.hc().lk()==home && match.hd().lk()==away)return match;return null;}
    private void projectSide(I match,boolean home,List<Object> starters,List<Object> bench){
        ah club=home?match.hc():match.hd();List<F> all=home?match.hl():match.hm(),active=home?match.hp():match.hq(),reserves=home?match.hn():match.ho();
        all.clear();active.clear();reserves.clear();club.kY().clear();club.kZ().clear();Map<Integer,F> roster=new HashMap<Integer,F>();for(Object raw:club.kc()){F player=(F)raw;roster.put(player.gD(),player);}
        for(Object raw:starters){int id=Data.number(Data.map(raw).get("playerId"));F player=roster.get(id);if(player==null)throw new IllegalStateException("Live lineup contains an unknown starter");all.add(player);active.add(player);club.kY().add(player);}
        for(Object raw:bench){int id=Data.number(Data.map(raw).get("playerId"));F player=roster.get(id);if(player==null)throw new IllegalStateException("Live lineup contains an unknown substitute");reserves.add(player);club.kZ().add(player);}
    }
    public I ownMatch(int coach){al owner=coaches.get(coach);if(owner==null)return null;for(I match:matches)if(match.hc()==owner.fg() || match.hd()==owner.fg())return match;return null;}
    public I preparationMatch(int coach){I current=ownMatch(coach);return current==null?upcoming.get(coach):current;}
}
