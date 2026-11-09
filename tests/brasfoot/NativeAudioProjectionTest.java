package brasfoot;

import best.A;
import best.F;
import best.I;
import best.ah;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/** Regression checks for sound baselines, match scope, perspective and stable event references. */
public final class NativeAudioProjectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static GameAdapter game(File root)throws Exception{
        GameAdapter game=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        int id=1000;for(Object raw:c.a.SR.P())for(Object value:((ah)raw).kc())((F)value).az(id++);
        return game;
    }
    private static ah aiClub(File root,String asset,int id)throws Exception{
        ah club;try(ObjectInputStream input=new ObjectInputStream(new FileInputStream(VisualModel.clubAssetFile(root,asset)))){club=new ah((e.t)input.readObject());}
        club.bX(id);club.k(Boolean.FALSE);int playerId=id*1000;for(Object raw:club.kc()){F player=(F)raw;player.n(club);player.az(playerId++);}return club;
    }
    private static Map<String,Object> event(I match,int id,int type,int detail,ah club,F player){
        Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",id);row.put("type",type);row.put("detail",detail);row.put("half",1);row.put("minute",12);
        row.put("clubId",club.lk());row.put("club",club.getNome());row.put("playerId",player==null?null:player.gD());row.put("player",player==null?null:player.getNome());return row;
    }
    private static Map<String,Object> state(I match,String status,int half,List<Object> events){
        Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",0);row.put("home",match.hc().getNome());row.put("away",match.hd().getNome());row.put("homeId",match.hc().lk());row.put("awayId",match.hd().lk());
        row.put("homeGoals",0);row.put("awayGoals",0);row.put("played",status.equals("FINISHED"));row.put("shots",Arrays.asList(0,0));row.put("possession",Arrays.asList(50,50));row.put("events",events);
        Map<String,Object> playback=new LinkedHashMap<String,Object>();playback.put("status",status);playback.put("half",half);playback.put("minute",half==2?45:0);playback.put("halfMinute",0);playback.put("matches",Collections.singletonList(row));
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("playback",playback);return result;
    }
    private static NativeMatchPanel panel(I match){match.hE().clear();NativeMatchPanel panel=new NativeMatchPanel(null,Collections.singletonList(match));panel.muted(true);return panel;}
    private static void audio(I match,File root)throws Exception{
        ah home=match.hc(),away=match.hd();F homePlayer=(F)home.kc().get(0),awayPlayer=(F)away.kc().get(0);
        home.k(Boolean.TRUE);away.k(Boolean.FALSE);c.a.SR.setVerJanelaSubs(1);

        NativeMatchPanel baseline=panel(match);List<Object> historical=Collections.<Object>singletonList(event(match,3,1,1,home,homePlayer));baseline.project(state(match,"SECOND_HALF",2,historical));baseline.project(state(match,"FINISHED",2,historical));
        check(baseline.emittedSounds().equals(Arrays.asList(1)),"A second-half baseline must not discover halftime at full time: "+baseline.emittedSounds());
        NativeMatchPanel alreadyFinished=panel(match);alreadyFinished.project(state(match,"FINISHED",2,Collections.emptyList()));check(alreadyFinished.emittedSounds().isEmpty(),"A finished first snapshot is silent");
        NativeMatchPanel failed=panel(match);failed.project(state(match,"FIRST_HALF",1,Collections.emptyList()));failed.project(state(match,"FAILED",2,Collections.emptyList()));check(failed.emittedSounds().isEmpty(),"A failed transition does not invent halftime or fulltime audio");

        NativeMatchPanel events=panel(match);List<Object> first=new ArrayList<Object>();first.add(event(match,4,1,1,home,homePlayer));events.project(state(match,"FIRST_HALF",1,first));
        List<Object> next=new ArrayList<Object>(first);Map<String,Object> homonym=event(match,5,1,1,away,awayPlayer);homonym.put("player",homePlayer.getNome());next.add(homonym);events.project(state(match,"FIRST_HALF",1,next));events.project(state(match,"FIRST_HALF",1,next));
        check(events.emittedSounds().equals(Arrays.asList(3)),"First-snapshot history is silent; later goals use native human/AI perspective and stable IDs: "+events.emittedSounds());
        check(match.hE().size()==2&&((A)match.hE().get(1)).eo()==awayPlayer,"Homonymous event names resolve by stable player ID");

        NativeMatchPanel twoHuman=panel(match);away.k(Boolean.TRUE);twoHuman.project(state(match,"FIRST_HALF",1,Collections.emptyList()));
        List<Object> bothGoals=Arrays.<Object>asList(event(match,6,1,1,home,homePlayer),event(match,7,1,1,away,awayPlayer));twoHuman.project(state(match,"FIRST_HALF",1,bothGoals));
        check(twoHuman.emittedSounds().equals(Arrays.asList(2,2)),"Goals by either human club use the native human-goal effect");

        NativeMatchPanel humanScope=panel(match);away.k(Boolean.TRUE);humanScope.project(state(match,"FIRST_HALF",1,Collections.emptyList()));
        humanScope.project(state(match,"HALF_TIME",1,Collections.emptyList()));humanScope.project(state(match,"HALF_TIME",1,Collections.emptyList()));
        humanScope.project(state(match,"SECOND_HALF",2,Collections.emptyList()));humanScope.project(state(match,"FINISHED",2,Collections.emptyList()));
        check(humanScope.emittedSounds().equals(Arrays.asList(0,1)),"Halftime and fulltime transitions emit once");

        away.k(Boolean.FALSE);NativeMatchPanel noBreakSound=panel(match);c.a.SR.setVerJanelaSubs(0);
        noBreakSound.project(state(match,"FIRST_HALF",1,Collections.emptyList()));noBreakSound.project(state(match,"HALF_TIME",1,Collections.emptyList()));noBreakSound.project(state(match,"SECOND_HALF",2,Collections.emptyList()));noBreakSound.project(state(match,"FINISHED",2,Collections.emptyList()));
        check(noBreakSound.emittedSounds().equals(Arrays.asList(1)),"Halftime sound follows the native substitution-window setting");

        NativeMatchPanel penalty=panel(match);List<Object> scored=Collections.<Object>singletonList(event(match,8,1,3,home,homePlayer));
        penalty.project(state(match,"FIRST_HALF",1,Collections.emptyList()));penalty.project(state(match,"FIRST_HALF",1,scored));penalty.project(state(match,"FIRST_HALF",1,scored));
        check(penalty.emittedSounds().equals(Arrays.asList(4,2)),"A scored penalty keeps its composed start and result effects");
        NativeMatchPanel missed=panel(match);List<Object> miss=Collections.<Object>singletonList(event(match,9,7,3,home,homePlayer));missed.project(state(match,"FIRST_HALF",1,Collections.emptyList()));missed.project(state(match,"FIRST_HALF",1,miss));
        check(missed.emittedSounds().equals(Arrays.asList(4,3)),"A missed human penalty keeps the native penalty and result effects");

        NativeMatchPanel disabled=panel(match);disabled.project(state(match,"FIRST_HALF",1,Collections.emptyList()));List<Object> hidden=Collections.<Object>singletonList(event(match,10,1,1,home,homePlayer));disabled.project(state(match,"FIRST_HALF",1,hidden));disabled.muted(false);disabled.project(state(match,"FIRST_HALF",1,hidden));
        check(disabled.emittedSounds().equals(Arrays.asList(2))&&disabled.playedSounds().isEmpty(),"Muted event is consumed without playback");disabled.muted(false);disabled.project(state(match,"FIRST_HALF",1,hidden));
        check(disabled.emittedSounds().equals(Arrays.asList(2))&&disabled.playedSounds().isEmpty(),"Enabling audio does not replay events consumed while muted");disabled.muted(true);

        home.k(Boolean.FALSE);away.k(Boolean.FALSE);NativeMatchPanel aiOnly=panel(match);aiOnly.project(state(match,"FIRST_HALF",1,Collections.emptyList()));aiOnly.project(state(match,"HALF_TIME",1,Collections.emptyList()));aiOnly.project(state(match,"FINISHED",2,Collections.emptyList()));
        check(aiOnly.emittedSounds().isEmpty(),"AI-only matches visible in a rest-day screen stay silent");

        ah secondHome=aiClub(root,"flamengopb_bra",20),secondAway=aiClub(root,"santos",21);I aiFixture=new I(null,0,secondHome,secondAway,0,new f.a(),null);
        home.k(Boolean.TRUE);List<I> mixedMatches=Arrays.asList(match,aiFixture);NativeMatchPanel mixed=new NativeMatchPanel(null,mixedMatches);mixed.muted(true);
        mixed.project(combinedState(match,aiFixture,"FIRST_HALF",1,Collections.emptyList(),Collections.emptyList()));
        mixed.project(combinedState(match,aiFixture,"FIRST_HALF",1,Collections.emptyList(),Collections.<Object>singletonList(event(aiFixture,11,1,1,secondHome,(F)secondHome.kc().get(0)))));
        check(mixed.emittedSounds().isEmpty(),"An AI-only fixture stays silent when displayed beside a human fixture");
        mixed.project(combinedState(match,aiFixture,"FIRST_HALF",1,Collections.<Object>singletonList(event(match,11,1,1,home,homePlayer)),Collections.<Object>singletonList(event(aiFixture,11,1,1,secondHome,(F)secondHome.kc().get(0)))));
        check(mixed.emittedSounds().equals(Arrays.asList(2)),"Human match events remain audible in a mixed multi-match panel");

        home.k(Boolean.FALSE);
        Map<String,Object> snapshot=new MatchPlayback(Collections.singletonList(match)).snapshot();Map<String,Object> matchRow=Data.map(Data.list(snapshot.get("matches")).get(0));
        check(Boolean.FALSE.equals(matchRow.get("homeHuman"))&&Boolean.FALSE.equals(matchRow.get("awayHuman")),"Playback carries explicit human-club identity");
        A goal=new A(0);goal.k(home);goal.a(1);goal.f(homePlayer);goal.T(1);goal.S(12);match.hE().add(goal);
        snapshot=new MatchPlayback(Collections.singletonList(match)).snapshot();matchRow=Data.map(Data.list(snapshot.get("matches")).get(0));Map<String,Object> payload=Data.map(Data.list(matchRow.get("events")).get(0));
        check(Data.number(payload.get("clubId"))==home.lk()&&Data.number(payload.get("playerId"))==homePlayer.gD(),"Playback transports stable event participant IDs");
        check(Data.number(matchRow.get("id"))==0,"Playback retains original match-list identity");
        for(NativeMatchPanel panel:Arrays.asList(baseline,alreadyFinished,failed,events,twoHuman,humanScope,noBreakSound,penalty,missed,disabled,aiOnly,mixed))panel.oR();
        a.dV.qb();System.out.println("PASS audio baseline, identity, human scope, option policy, composed penalties, mute transition");
    }
    private static Map<String,Object> combinedState(I first,I second,String status,int half,List<Object> firstEvents,List<Object> secondEvents){
        Map<String,Object> left=Data.map(Data.list(Data.map(state(first,status,half,firstEvents).get("playback")).get("matches")).get(0));
        Map<String,Object> right=Data.map(Data.list(Data.map(state(second,status,half,secondEvents).get("playback")).get("matches")).get(0));
        right.put("id",1);
        Map<String,Object> playback=Data.map(state(first,status,half,Collections.emptyList()).get("playback"));playback.put("matches",Arrays.<Object>asList(left,right));
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("playback",playback);return result;
    }
    public static void main(String[] args)throws Exception{
        System.setProperty("user.dir",new File(args[0],"game/Brasfoot22-23").getAbsolutePath());
        GameAdapter game=game(new File(args[0]));c.a.vH();audio(game.currentMatches().get(0),new File(args[0]));
    }
}
