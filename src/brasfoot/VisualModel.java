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
    private final File root;
    public VisualModel(File root,Map<String,Object> state)throws Exception{
        this.root=root;new best.M(true);best.n.cU();c.a.SR=new best.f();c.a.afQ=new best.ay();
        c.a.SR.setHabilidadeIndividual(false);c.a.SR.bs=false;rebuild(state);
    }
    public List<I> matches(){return matches;}
    public ah club(int id){return clubs.get(id);}
    public al coach(int id){return coaches.get(id);}
    @SuppressWarnings("unchecked")
    public void rebuild(Map<String,Object> state)throws Exception{
        Map<String,Object> game=Data.map(state.get("game"));
        c.a.SR.R().clear();best.a day=new best.a();day.a().setTimeInMillis(Data.longNumber(game.get("dateMillis")));c.a.SR.R().add(day);c.a.SR.l(0);
        c.a.SR.k(Data.number(game.get("season")));c.a.SR.I(Data.number(game.get("year")));
        updateClubs(game);
        c.a.SR.M().clear();
        for(Object raw:Data.list(game.get("coaches"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("coachId"));
            al coach=coaches.get(id);if(coach==null){coach=new al((String)row.get("name"));Data.set(coach,"nU",id);coaches.put(id,coach);c.a.SR.L().add(coach);}
            ah club=row.get("clubId")==null?null:clubs.get(Data.number(row.get("clubId")));coach.n(club);coach.k(true);
            if(club!=null){Data.set(club,"mZ",coach);Data.set(club,"na",id);club.k(true);}c.a.SR.M().add(coach);
        }
        matches.clear();Map<String,f.a> competitions=new LinkedHashMap<String,f.a>();
        for(Object raw:Data.list(Data.map(state.get("playback")).get("matches"))){Map<String,Object> row=Data.map(raw);
            String name=(String)row.get("competition");f.a competition=competitions.get(name);
            if(competition==null){competition=new f.a();competition.setNome(name);competitions.put(name,competition);}
            I match=new I(null,0,clubs.get(Data.number(row.get("homeId"))),clubs.get(Data.number(row.get("awayId"))),0,competition,null);matches.add(match);
            if(row.containsKey("phaseLabel"))match.o((String)row.get("phaseLabel"));
        }
        upcoming.clear();
        for(Object raw:Data.list(game.get("upcoming"))){Map<String,Object> row=Data.map(raw);f.a competition=new f.a();competition.setNome((String)row.get("competition"));
            I next=new I(null,0,clubs.get(Data.number(row.get("homeId"))),clubs.get(Data.number(row.get("awayId"))),0,competition,null);
            if(row.containsKey("phaseLabel"))next.o((String)row.get("phaseLabel"));
            day.h().remove(next);upcoming.put(Data.number(row.get("coachId")),next);
        }
        update(state);
    }
    @SuppressWarnings("unchecked")
    private void updateClubs(Map<String,Object> game)throws Exception{
        for(Object raw:Data.list(game.get("visualClubs"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("clubId"));ah club=clubs.get(id);
            if(club==null){
                String asset=(String)row.get("asset");if(asset==null || asset.isEmpty() || asset.contains("/") || asset.contains("\\") || asset.contains(".."))throw new IOException("Invalid club asset");
                File file=new File(root,"game/Brasfoot22-23/teams/"+asset+".ban");
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
            List<Object> t=Data.list(row.get("tactics"));int[] settings=new int[4];for(int i=0;i<4;i++)settings[i]=Data.number(t.get(i));club.k(settings);
        }
    }
    @SuppressWarnings("unchecked")
    public void update(Map<String,Object> state)throws Exception{
        updateClubs(Data.map(state.get("game")));
        for(I match:matches) {
            for(ah club:new ah[]{match.hc(),match.hd()}){
                List<F> all=club==match.hc()?match.hl():match.hm(),active=club==match.hc()?match.hp():match.hq(),bench=club==match.hc()?match.hn():match.ho();
                club.kY().clear();club.kZ().clear();all.clear();active.clear();bench.clear();
                for(Object raw:club.kc()){F p=(F)raw;if(p.fT()>=1 && p.fT()<=25){all.add(p);active.add(p);club.kY().add(p);}else if(p.fT()>=26 && p.fT()<=36){bench.add(p);club.kZ().add(p);}}
            }
        }
    }
    public I ownMatch(int coach){al owner=coaches.get(coach);if(owner==null)return null;for(I match:matches)if(match.hc()==owner.fg() || match.hd()==owner.fg())return match;return null;}
    public I preparationMatch(int coach){I current=ownMatch(coach);return current==null?upcoming.get(coach):current;}
}
