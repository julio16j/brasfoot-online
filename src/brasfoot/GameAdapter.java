package brasfoot;

import best.F;
import best.I;
import best.ah;
import best.al;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.*;
import java.util.*;

/** Adapter to original bytecode. One game per JVM; no calls to Swing controllers. */
public final class GameAdapter implements RoundCoordinator.Actions {
    private final best.f state;
    private final Map<Integer, al> coaches = new LinkedHashMap<Integer, al>();
    private final Set<Integer> submitted = new HashSet<Integer>();

    public GameAdapter(best.f state) {
        this.state = state;
        if (c.a.SR != state) throw new IllegalStateException("Game globals must belong to adapter");
        for (Object value : state.M()) {
            al coach = (al) value;
            if (Boolean.TRUE.equals(coach.jZ())) {
                if (coaches.put(coach.lT(), coach) != null) throw new IllegalArgumentException("Duplicate coach ID");
            }
        }
    }
    public Set<Integer> coachIds() { return new LinkedHashSet<Integer>(coaches.keySet()); }
    private void checkGlobal() {
        if (c.a.SR != state) throw new IllegalStateException("A different game replaced the global state");
    }
    private ah club(int coach) {
        checkGlobal();
        al value = coaches.get(coach);
        if (value == null) throw new IllegalArgumentException("Unknown coach");
        return value.fg();
    }
    private I match(ah club) {
        if (club == null) return null;
        I found = null;
        for (Object object : state.S()) {
            I match = (I) object;
            if (match.hc() == club || match.hd() == club) {
                if (found != null) throw new IllegalStateException("Multiple matches for club in this phase");
                found = match;
            }
        }
        return found;
    }
    @SuppressWarnings("unchecked")
    public void lineup(int coach, int[] starters, int[] bench) {
        lineupWithOptions(coach,starters,bench,new int[]{1,2,9,4,7,11,13,14,16,20,23},null);
    }
    @SuppressWarnings("unchecked")
    public void lineupWithOptions(int coach,int[] starters,int[] bench,int[] slots,int[] tactics) {
        ah club = club(coach);
        I match = match(club);
        if (match == null) throw new IllegalStateException("No current match for coach");
        if (club.kf()) throw new IllegalStateException("Club already ready");
        if (starters.length != 11 || bench.length > 11) throw new IllegalArgumentException("Need 11 starters and at most 11 substitutes");
        if(slots.length!=11 || slots[0]!=1)throw new IllegalArgumentException("Need 11 field slots with goalkeeper first");
        Set<Integer> usedSlots=new HashSet<Integer>();
        for(int slot:slots)if(slot<1 || slot>25 || !usedSlots.add(slot))throw new IllegalArgumentException("Invalid or duplicate pitch slot");
        if(tactics!=null){
            int[] limits={best.aq.rB.length,best.aq.rC.length,best.aq.rE.length,best.aq.rD.length};
            if(tactics.length!=4)throw new IllegalArgumentException("Need four tactic settings");
            for(int i=0;i<4;i++)if(tactics[i]<0 || tactics[i]>=limits[i])throw new IllegalArgumentException("Invalid tactic");
        }
        List<F> roster = new ArrayList<F>(club.kc());
        if (roster.size() > 36) throw new IllegalStateException("Roster above supported 36 slots");
        Set<Integer> selected = new HashSet<Integer>();
        List<F> chosen = new ArrayList<F>();
        for (int[] group : new int[][] {starters, bench}) {
            for (int index : group) {
                if (index < 0 || index >= roster.size() || !selected.add(index)) throw new IllegalArgumentException("Invalid or duplicate roster index");
                F player = roster.get(index);
                if (player.fg() != club || !player.a(match, false)) throw new IllegalArgumentException("Player unavailable for this match");
                chosen.add(player);
            }
        }
        if (chosen.get(0).getPosicao() != 0) throw new IllegalArgumentException("First starter must be the goalkeeper");
        for (int i=1;i<11;i++) if (chosen.get(i).getPosicao()==0) throw new IllegalArgumentException("Only one starting goalkeeper");
        club.kY().clear(); club.kZ().clear();
        List<F> all = match.hc() == club ? match.hl() : match.hm();
        List<F> active = match.hc() == club ? match.hp() : match.hq();
        List<F> reserves = match.hc() == club ? match.hn() : match.ho();
        all.clear(); active.clear(); reserves.clear();
        for (F player : roster) { player.as(-1); player.b(Boolean.FALSE); }
        // Fixed 4-4-2: goalkeeper, two fullbacks, two centre backs, four midfielders, two forwards.
        if(tactics!=null)club.k(tactics.clone());
        for (int i=0;i<11;i++) {
            F player=chosen.get(i); player.as(slots[i]); player.b(Boolean.TRUE);
            club.kY().add(player); all.add(player); active.add(player);
        }
        for (int i=11;i<chosen.size();i++) {
            F player=chosen.get(i); player.as(26+i-11); player.b(Boolean.TRUE);
            club.kZ().add(player); reserves.add(player);
        }
        submitted.add(coach);
    }
    public void ready(int coach, boolean value) {
        ah club = club(coach);
        if (value && match(club) != null && !submitted.contains(coach)) throw new IllegalStateException("Submit a lineup before ready");
        if (club != null) club.I(value);
    }
    public List<I> currentMatches() {
        checkGlobal(); List<I> result=new ArrayList<I>();
        for(Object value: state.S()) result.add((I)value);
        return result;
    }
    private Map<String,Object> suggestion(ah club, I match) {
        List<Integer> starters=new ArrayList<Integer>(), bench=new ArrayList<Integer>();
        if (club!=null && match!=null) {
            int[] roles={0,1,1,2,2,3,3,3,3,4,4};
            for(int role:roles) {
                int found=-1;
                for(int i=0;i<club.kc().size();i++) {
                    F p=(F)club.kc().get(i);
                    if(!starters.contains(i) && p.getPosicao()==role && p.a(match,false)) { found=i; break; }
                }
                if(found<0 && role!=0) for(int i=0;i<club.kc().size();i++) {
                    F p=(F)club.kc().get(i);
                    if(!starters.contains(i) && p.getPosicao()!=0 && p.a(match,false)) { found=i;break; }
                }
                if(found>=0)starters.add(found);
            }
            for(int i=0;i<club.kc().size() && bench.size()<11;i++)
                if(!starters.contains(i) && ((F)club.kc().get(i)).a(match,false))bench.add(i);
        }
        Map<String,Object> result=new LinkedHashMap<String,Object>();
        result.put("starters",starters);result.put("bench",bench);return result;
    }
    public Map<String, Object> snapshot() {
        checkGlobal();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("calendarIndex", state.J());
        result.put("dateMillis", state.bb().getTimeInMillis());
        List<Object> list = new ArrayList<Object>();
        for (Map.Entry<Integer, al> entry : coaches.entrySet()) {
            ah club = club(entry.getKey());
            I match = match(club);
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("coachId", entry.getKey()); row.put("name", entry.getValue().dS());
            row.put("clubId", club == null ? null : club.lk());
            row.put("club", club == null ? null : club.getNome());
            if(club!=null){List<Integer> t=new ArrayList<Integer>();for(int value:club.kj())t.add(value);row.put("tactics",t);}
            row.put("hasMatch", match != null); row.put("engineReady", club != null && club.kf());
            List<Object> players = new ArrayList<Object>();
            if (club != null) {
                int index = 0;
                for (Object object : club.kc()) {
                    F player = (F) object;
                    Map<String, Object> p = new LinkedHashMap<String, Object>();
                    p.put("index", index++); p.put("name", player.getNome());
                    p.put("position", player.getPosicao()); p.put("slot", player.fT());
                    p.put("age",player.getIdade());p.put("strength",player.fi());p.put("fitness",player.fp());p.put("side",player.getLado());
                    p.put("skill1",player.getCr1());p.put("skill2",player.getCr2());p.put("status",player.getStatus());p.put("star",player.ff());p.put("worldClass",player.gm());
                    p.put("eligible", match != null && player.a(match, false)); players.add(p);
                }
            }
            row.put("players", players); row.put("suggestion",suggestion(club,match)); list.add(row);
        }
        result.put("coaches", list);
        List<Object> matches=new ArrayList<Object>();
        for(I match:currentMatches()) {
            Map<String,Object> m=new LinkedHashMap<String,Object>();
            m.put("home",match.hc().getNome());m.put("away",match.hd().getNome());
            m.put("homeGoals",match.hu());m.put("awayGoals",match.hw());m.put("played",match.e());matches.add(m);
        }
        result.put("matches",matches);return result;
    }
    public void saveLocal(File destination) throws IOException {
        checkGlobal();
        Kryo kryo = new Kryo(); kryo.setRegistrationRequired(false);
        try (Output output = new Output(new FileOutputStream(destination))) {
            kryo.writeClassAndObject(output, state); kryo.writeClassAndObject(output, c.a.afQ);
        }
    }
    public static GameAdapter readLocalSave(File save) throws IOException {
        new best.M(true); best.n.cU();
        Kryo kryo = new Kryo(); kryo.setRegistrationRequired(false);
        try (Input input = new Input(new FileInputStream(save))) {
            best.f loaded = (best.f) kryo.readClassAndObject(input);
            best.ay secondary = (best.ay) kryo.readClassAndObject(input);
            loaded.G(); c.a.SR = loaded; c.a.afQ = secondary;
            for (Object object : loaded.M()) { al coach = (al)object; if (coach.fg() != null) coach.fg().I(false); }
            return new GameAdapter(loaded);
        }
    }
}

