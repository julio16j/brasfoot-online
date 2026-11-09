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
    public boolean isCareer(){return !state.N().isEmpty();}
    public void resetPreparation(){submitted.clear();for(al coach:coaches.values())if(coach.fg()!=null)coach.fg().I(false);}
    public void invalidateLineups(Collection<Integer> ids){for(Integer id:ids){submitted.remove(id);al coach=coaches.get(id);if(coach!=null && coach.fg()!=null)coach.fg().I(false);}}
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
    private I upcoming(ah club){
        if(club==null)return null;
        for(int day=state.J();day<state.R().size();day++)for(Object raw:state.m(day)){
            I fixture=(I)raw;if(!fixture.e() && (fixture.hc()==club || fixture.hd()==club))return fixture;
        }
        return match(club);
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
        // Native slots 1..25 describe pitch positions and 26..36 the bench.
        // Roster indices may exceed 35; unselected players keep slot -1.
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
    public Map<String,Object> substitute(int coach,int outgoingId,int incomingId,MatchPlayback playback){
        ah club=club(coach);if(club==null)throw new IllegalStateException("Choose a club before making substitutions");
        I fixture=match(club);if(fixture==null)throw new IllegalStateException("No current match for this club");
        boolean home=fixture.hc()==club;int side=home?0:1;
        F outgoing=null,incoming=null;
        for(Object raw:club.kc()){F player=(F)raw;if(player.gD()==outgoingId)outgoing=player;if(player.gD()==incomingId)incoming=player;}
        if(outgoing==null || incoming==null)throw new IllegalArgumentException("Player no longer belongs to your club");
        if(!outgoing.a(fixture,false) && !((home?fixture.hp():fixture.hq()).contains(outgoing)))throw new IllegalStateException("Player leaving is unavailable");
        if(!incoming.a(fixture,false))throw new IllegalStateException("Player entering is unavailable");
        playback.substitute(fixture,side,outgoing,incoming);
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("status","SUBSTITUTED");result.put("outgoing",outgoing.getNome());result.put("incoming",incoming.getNome());result.put("substitutionsRemaining",fixture.aR(side));return result;
    }
    public Map<String,Object> liveLineup(int coach,int[] starters,int[] bench,int[] slots,int[] tactics,MatchPlayback playback){
        ah club=club(coach);if(club==null)throw new IllegalStateException("Choose a club before editing its live lineup");
        I fixture=match(club);if(fixture==null)throw new IllegalStateException("No current match for this club");
        if(starters.length!=11 || slots.length!=11)throw new IllegalArgumentException("A live lineup needs eleven starters and positions");
        List<F> desired=new ArrayList<F>(),reserves=new ArrayList<F>();Set<F> unique=new HashSet<F>();
        for(int index:starters){if(index<0 || index>=club.kc().size())throw new IllegalArgumentException("Invalid starter");F player=(F)club.kc().get(index);if(!unique.add(player))throw new IllegalArgumentException("Duplicate player in lineup");desired.add(player);}
        for(int index:bench){if(index<0 || index>=club.kc().size())throw new IllegalArgumentException("Invalid substitute");F player=(F)club.kc().get(index);if(!unique.add(player))throw new IllegalArgumentException("Duplicate player in lineup");reserves.add(player);}
        Set<Integer> positions=new HashSet<Integer>();for(int slot:slots)if(slot<1 || slot>25 || !positions.add(slot))throw new IllegalArgumentException("Invalid player position");
        boolean home=fixture.hc()==club;int side=home?0:1;List<F> active=home?fixture.hp():fixture.hq(),currentBench=home?fixture.hn():fixture.ho();
        List<F> outgoing=new ArrayList<F>(),incoming=new ArrayList<F>();for(F player:active)if(!desired.contains(player))outgoing.add(player);for(F player:desired)if(!active.contains(player))incoming.add(player);
        if(outgoing.size()!=incoming.size())throw new IllegalArgumentException("The requested lineup cannot be applied");
        if(outgoing.size()>fixture.aR(side))throw new IllegalStateException("Not enough substitutions remain");
        for(int i=0;i<outgoing.size();i++)playback.substitute(fixture,side,outgoing.get(i),incoming.get(i));
        active.clear();active.addAll(desired);currentBench.clear();currentBench.addAll(reserves);
        List<F> all=home?fixture.hl():fixture.hm();all.clear();all.addAll(desired);
        for(int i=0;i<desired.size();i++){desired.get(i).as(slots[i]);desired.get(i).b(true);}
        for(int i=0;i<reserves.size();i++){reserves.get(i).as(26+i);reserves.get(i).b(true);}
        club.kY().clear();club.kY().addAll(desired);club.kZ().clear();club.kZ().addAll(reserves);
        if(tactics!=null && tactics.length==4)club.k(tactics);
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("status","LINEUP_UPDATED");result.put("substitutionsApplied",outgoing.size());result.put("substitutionsRemaining",fixture.aR(side));return result;
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
    /** Whether this date contains a fixture managed by any human coach. */
    public boolean hasHumanMatch(){for(I fixture:currentMatches())if(fixture.hc().jZ() || fixture.hd().jZ())return true;return false;}
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
        return snapshot(Collections.<Integer,Object>emptyMap(),null);
    }
    /** Reuse pre-match AI presentation data instead of exporting already final player state. */
    Map<String,Object> snapshot(Map<Integer,Object> frozenClubs,List<Object> frozenMatches) {
        checkGlobal();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("calendarIndex", state.J());
        result.put("career",isCareer());result.put("season",state.H());
        result.put("year",state.bb().get(Calendar.YEAR));result.put("date",state.T());
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
                    p.put("index", index++);p.put("playerId",player.gD());p.put("name", player.getNome());
                    p.put("position", player.getPosicao()); p.put("slot", player.fT());
                    p.put("age",player.getIdade());p.put("strength",player.fi());p.put("fitness",player.fp());p.put("side",player.getLado());
                    p.put("skill1",player.getCr1());p.put("skill2",player.getCr2());p.put("status",player.getStatus());p.put("star",player.ff());p.put("worldClass",player.gm());
                    p.put("eligible", match != null && player.a(match, false)); players.add(p);
                }
            }
            row.put("players", players);row.put("suggestion",suggestion(club,match));
            if(match!=null){int side=match.hc()==club?0:1;row.put("liveLineup",liveLineup(match,side));row.put("substitutionsRemaining",match.aR(side));}
            else {row.put("liveLineup",Collections.<String,Object>emptyMap());row.put("substitutionsRemaining",0);}
            list.add(row);
        }
        result.put("coaches", list);
        List<Object> matches=new ArrayList<Object>();
        for(I match:currentMatches()) {
            Map<String,Object> frozen=frozenMatches!=null && !match.hc().jZ() && !match.hd().jZ()?Data.map(frozenMatches.get(matches.size())):null;
            Map<String,Object> m=new LinkedHashMap<String,Object>();
            m.put("home",match.hc().getNome());m.put("away",match.hd().getNome());m.put("homeId",match.hc().lk());m.put("awayId",match.hd().lk());m.put("competition",match.hy()==null?"Partida":match.hy().getNome());m.put("competitionId",match.hy()==null?-1:match.hy().gD());m.put("competitionKey",CompetitionIdentity.key(match.hy()));m.put("competitionType",match.hy()==null?0:match.hy().b());
            m.put("homeStarters",frozen==null?livePlayers(match.hp()):frozen.get("homeStarters"));m.put("homeBench",frozen==null?livePlayers(match.hn()):frozen.get("homeBench"));m.put("awayStarters",frozen==null?livePlayers(match.hq()):frozen.get("awayStarters"));m.put("awayBench",frozen==null?livePlayers(match.ho()):frozen.get("awayBench"));
            m.put("homeGoals",match.hu());m.put("awayGoals",match.hw());m.put("played",match.e());matches.add(m);
        }
        result.put("matches",matches);
        Set<ah> visible=new LinkedHashSet<ah>();
        for(al coach:coaches.values())if(coach.fg()!=null)visible.add(coach.fg());
        List<Object> upcoming=new ArrayList<Object>();
        for(Map.Entry<Integer,al> entry:coaches.entrySet()){
            I fixture=upcoming(entry.getValue().fg());if(fixture==null)continue;
            visible.add(fixture.hc());visible.add(fixture.hd());Map<String,Object> next=new LinkedHashMap<String,Object>();
            next.put("coachId",entry.getKey());next.put("homeId",fixture.hc().lk());next.put("awayId",fixture.hd().lk());next.put("competition",fixture.hy().getNome());next.put("competitionId",fixture.hy().gD());next.put("competitionKey",CompetitionIdentity.key(fixture.hy()));next.put("competitionType",fixture.hy().b());upcoming.add(next);
            next.put("phaseLabel",fixture.hG());
        }
        result.put("upcoming",upcoming);
        List<Object> stadiumExpansions=new ArrayList<Object>();
        for(Object raw:state.bw()){best.B project=(best.B)raw;int owner=-1;for(Object value:state.P()){ah candidate=(ah)value;if(candidate.ev()==project.ev()){owner=candidate.lk();break;}}
            if(owner<0)continue;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("clubId",owner);row.put("endMillis",project.ew().getTimeInMillis());row.put("seats",integers(projectSeats(project)));stadiumExpansions.add(row);}
        result.put("stadiumExpansions",stadiumExpansions);
        for(I fixture:currentMatches()){visible.add(fixture.hc());visible.add(fixture.hd());}
        List<Object> visual=new ArrayList<Object>();
        for(ah club:visible)visual.add(frozenClubs.containsKey(club.lk())?frozenClubs.get(club.lk()):visualClub(club));
        result.put("visualClubs",visual);return result;
    }
    private Map<String,Object> visualClub(ah club){
        Map<String,Object> row=new LinkedHashMap<String,Object>();
        row.put("clubId",club.lk());row.put("club",club.getNome());row.put("asset",club.jY());row.put("country",club.getPais());
        row.put("balance",club.kb());row.put("division",club.getDivisao());row.put("trainingFocus",club.lm());row.put("trainingAssistant",club.ll());
        if(club.ev()!=null){Map<String,Object> stadium=new LinkedHashMap<String,Object>();stadium.put("name",club.ev().dS());stadium.put("seats",integers(club.ev().dT()));stadium.put("prices",integers(club.ev().dU()));stadium.put("suggestedPrices",club.ev().dV());row.put("stadium",stadium);}
        row.put("confidence",club.ka()==null?null:scalarFields(club.ka(),new String[]{"oe","of"}));
        List<Integer> tactics=new ArrayList<Integer>();for(int value:club.kj())tactics.add(value);row.put("tactics",tactics);
        List<Object> players=new ArrayList<Object>();I fixture=match(club);int index=0;
        for(Object raw:club.kc()){
            F p=(F)raw;Map<String,Object> player=new LinkedHashMap<String,Object>();
            player.put("index",index++);player.put("name",p.getNome());player.put("position",p.getPosicao());player.put("slot",p.fT());
            player.put("age",p.getIdade());player.put("strength",p.fi());player.put("fitness",p.fp());player.put("side",p.getLado());
            player.put("skill1",p.getCr1());player.put("skill2",p.getCr2());player.put("status",p.getStatus());player.put("star",p.ff());player.put("worldClass",p.gm());
            player.put("salary",p.fj());player.put("value",p.fk());player.put("contractDays",p.fR());
            player.put("playerId",p.gD());player.put("country",p.getPais());
            player.put("seasonStats",scalarFields(p,new String[]{"fh","fi"}));
            player.put("eligible",fixture!=null && p.a(fixture,false));players.add(player);
        }
        row.put("players",players);List<Object> youth=new ArrayList<Object>();int youthIndex=0;for(Object raw:club.ky())youth.add(youthRow((F)raw,youthIndex++));row.put("youth",youth);return row;
    }
    private static Map<String,Object> youthRow(F player,int index){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("index",index);row.put("playerId",player.gD());row.put("name",player.getNome());row.put("position",player.getPosicao());row.put("age",player.getIdade());row.put("country",player.getPais());row.put("side",player.getLado());row.put("skill1",player.getCr1());row.put("skill2",player.getCr2());row.put("development",player.fE());row.put("cpe",player.fD());row.put("junior",Boolean.TRUE.equals(player.fC()));row.put("strength",player.fi());row.put("salary",player.fj());row.put("value",player.fk());row.put("injuryUntilMillis",player.fo());row.put("contractEndMillis",Data.longNumber(Data.get(player,"eJ")));List<Object> cards=new ArrayList<Object>();for(Object raw:Data.list(Data.get(player,"eS"))){best.H suspension=(best.H)raw;best.at competition=suspension.gS();if(competition==null && suspension.gX()<0)continue;Map<String,Object> record=new LinkedHashMap<String,Object>();record.put("competitionId",suspension.gX());record.put("competitionKey",competition==null?"id:"+suspension.gX():CompetitionIdentity.key(competition));record.put("competition",competition==null?"":competition.getNome());record.put("competitionType",competition==null?0:competition.b());int[] count=suspension.gW();record.put("yellowCards",count[0]);record.put("redCards",count[1]);cards.add(record);}row.put("suspensions",cards);return row;}
    private Map<String,Object> liveLineup(I match,int side){Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("starters",livePlayers(side==0?match.hp():match.hq()));result.put("bench",livePlayers(side==0?match.hn():match.ho()));result.put("substitutionsRemaining",match.aR(side));return result;}
    private static List<Object> livePlayers(List<?> players){List<Object> result=new ArrayList<Object>();for(Object raw:players){F player=(F)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("playerId",player.gD());row.put("name",player.getNome());row.put("position",player.getPosicao());row.put("slot",player.fT());result.add(row);}return result;}
    private static int[] projectSeats(best.B project){return (int[])Data.get(project,"dJ");}
    private static List<Integer> integers(int[] values){List<Integer> result=new ArrayList<Integer>();for(int value:values)result.add(value);return result;}
    private Map<String,Object> scalarFields(Object target,String[] fields){
        Map<String,Object> result=new LinkedHashMap<String,Object>();
        for(String field:fields)try{result.put(field,Data.get(target,field));}catch(IllegalStateException ignored){ }
        return result;
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

