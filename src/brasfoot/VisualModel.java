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
    private final File root;
    public VisualModel(File root,Map<String,Object> state)throws Exception{
        this.root=root;new best.M(true);best.n.cU();c.a.SR=new best.f();c.a.afQ=new best.ay();
        c.a.SR.setHabilidadeIndividual(false);c.a.SR.bs=false;rebuild(state);
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
        c.a.SR.R().clear();best.a day=new best.a();day.a().setTimeInMillis(Data.longNumber(game.get("dateMillis")));c.a.SR.R().add(day);c.a.SR.l(0);
        c.a.SR.k(Data.number(game.get("season")));c.a.SR.I(Data.number(game.get("year")));
        matches.clear();competitions.clear();for(Object raw:Data.list(Data.map(state.get("playback")).get("matches")))competition(Data.map(raw));for(Object raw:Data.list(game.get("upcoming")))competition(Data.map(raw));
        updateClubs(game);
        c.a.SR.M().clear();
        for(Object raw:Data.list(game.get("coaches"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("coachId"));
            al coach=coaches.get(id);if(coach==null){coach=new al((String)row.get("name"));Data.set(coach,"nU",id);coaches.put(id,coach);c.a.SR.L().add(coach);}
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
            List<F> previous=new ArrayList<F>(club.kc());club.kc().clear();
            for(Object value:Data.list(row.get("players"))){Map<String,Object> p=Data.map(value);F player=null;
                for(Iterator<F> it=previous.iterator();it.hasNext();){F candidate=it.next();if(p.containsKey("playerId") && Data.number(p.get("playerId"))>=0?candidate.gD()==Data.number(p.get("playerId")):candidate.getNome().equals(p.get("name"))){player=candidate;it.remove();break;}}
                if(player==null)player=new F();player.n(club);player.setNome((String)p.get("name"));player.setPosicao(Data.number(p.get("position")));
                if(p.containsKey("playerId"))player.az(Data.number(p.get("playerId")));if(p.containsKey("country"))player.setPais(Data.number(p.get("country")));
                if(p.containsKey("seasonStats"))for(Map.Entry<String,Object> entry:Data.map(p.get("seasonStats")).entrySet())if(entry.getKey().equals("fh") || entry.getKey().equals("fi"))Data.set(player,entry.getKey(),Data.number(entry.getValue()));
                player.setIdade(Data.number(p.get("age")));player.ad(Data.number(p.get("strength")));player.ai(Data.number(p.get("fitness")));player.setLado(Data.number(p.get("side")));
                player.setCr1(Data.number(p.get("skill1")));player.setCr2(Data.number(p.get("skill2")));player.setStatus(Data.number(p.get("status")));player.a(Boolean.TRUE.equals(p.get("star")));player.j(Boolean.TRUE.equals(p.get("worldClass")));
                player.as(Data.number(p.get("slot")));player.d(Boolean.TRUE.equals(p.get("eligible"))?0L:Long.MAX_VALUE);
                Data.set(player,"et",Data.number(p.get("salary")));Data.set(player,"eu",Data.number(p.get("value")));player.a(Math.max(1,Data.number(p.get("contractDays"))),true);
                club.kc().add(player);
            }
            if(row.containsKey("youth")){List<F> previousYouth=new ArrayList<F>(club.ky());club.ky().clear();for(Object value:Data.list(row.get("youth"))){Map<String,Object> p=Data.map(value);int playerId=Data.number(p.get("playerId"));F player=null;for(Iterator<F> it=previousYouth.iterator();it.hasNext();){F candidate=it.next();if(playerId>=0&&candidate.gD()==playerId){player=candidate;it.remove();break;}}if(player==null)player=new F();player.n(club);player.az(playerId);player.setNome((String)p.get("name"));player.setPosicao(Data.number(p.get("position")));player.setPais(Data.number(p.get("country")));player.setIdade(Data.number(p.get("age")));player.setLado(Data.number(p.get("side")));player.setCr1(Data.number(p.get("skill1")));player.setCr2(Data.number(p.get("skill2")));player.ad(Data.number(p.get("strength")));player.ao(Data.number(p.get("cpe")));player.ap(Data.number(p.get("development")));player.ae(Data.number(p.get("salary")));Data.set(player,"eu",Data.number(p.get("value")));player.h(Boolean.TRUE.equals(p.get("junior")));if(p.containsKey("injuryUntilMillis"))player.d(Data.longNumber(p.get("injuryUntilMillis")));if(p.containsKey("contractEndMillis"))Data.set(player,"eJ",Data.longNumber(p.get("contractEndMillis")));if(p.containsKey("suspensions")){List<Object> records=new ArrayList<Object>();for(Object rawRecord:Data.list(p.get("suspensions"))){Map<String,Object> record=Data.map(rawRecord);String key=String.valueOf(record.get("competitionKey"));f.a competition=competitions.get(key);if(competition==null){Map<String,Object> synthetic=new LinkedHashMap<String,Object>();synthetic.put("competitionKey",key);synthetic.put("competitionId",record.get("competitionId"));synthetic.put("competition",record.get("competition"));synthetic.put("competitionType",record.get("competitionType"));competition=competition(synthetic);}best.H suspension=new best.H();suspension.m(competition);Data.set(suspension,"ca",Data.number(record.get("yellowCards")));Data.set(suspension,"cb",Data.number(record.get("redCards")));suspension.gY();records.add(suspension);}Data.set(player,"eS",records);}club.ky().add(player);}}
            List<Object> t=Data.list(row.get("tactics"));int[] settings=new int[4];for(int i=0;i<4;i++)settings[i]=Data.number(t.get(i));club.k(settings);
            if(row.containsKey("roles")){Map<String,Object> roles=Data.map(row.get("roles"));club.k(rolePlayer(club,roles.get("freeKicks")));club.j(rolePlayer(club,roles.get("captain")));club.n(rolePlayer(club,roles.get("corners")));club.o(rolePlayer(club,roles.get("falseNine")));}
            if(row.containsKey("trainingFocus"))club.bZ(Data.number(row.get("trainingFocus")));
            if(row.containsKey("trainingAssistant"))club.M(Boolean.TRUE.equals(row.get("trainingAssistant")));
            if(row.get("stadium") instanceof Map){Map<String,Object> stadiumRow=Data.map(row.get("stadium"));List<Object> capacities=Data.list(stadiumRow.get("seats")),prices=Data.list(stadiumRow.get("prices"));int[] seats=new int[4],costs=new int[4];for(int i=0;i<4;i++){seats[i]=Data.number(capacities.get(i));costs[i]=Data.number(prices.get(i));}
                best.v stadium=club.ev();if(stadium==null){int total=0;for(int value:seats)total+=value;stadium=new best.v(String.valueOf(stadiumRow.get("name")),total,club);}
                stadium.i(String.valueOf(stadiumRow.get("name")));stadium.a(seats);stadium.b(costs);stadium.o(Boolean.TRUE.equals(stadiumRow.get("suggestedPrices")));club.a(stadium);
            }
        }
    }
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
