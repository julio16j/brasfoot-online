package brasfoot;

import java.nio.file.*;
import java.util.*;

public final class NonHumanPlaybackTest {
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);GameAdapter game=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        for(Object raw:Data.list(game.snapshot().get("coaches"))){Map<String,Object> coach=Data.map(raw),suggestion=Data.map(coach.get("suggestion"));List<Object> chosen=Data.list(suggestion.get("starters"));int[] starters=new int[chosen.size()];for(int i=0;i<starters.length;i++)starters[i]=Data.number(chosen.get(i));game.lineup(Data.number(coach.get("coachId")),starters,new int[0]);}
        best.I match=game.currentMatches().get(0);match.hc().k(false);match.hd().k(false);
        MatchPlayback playback=new MatchPlayback(game.currentMatches(),true);playback.begin();
        int finalHome=match.hu(),finalAway=match.hw();if(!match.e())throw new AssertionError("AI result was not precomputed");
        Map<String,Object> initial=Data.map(Data.list(playback.snapshot().get("matches")).get(0));
        if(Data.number(initial.get("homeGoals"))!=0 || Data.number(initial.get("awayGoals"))!=0 || Boolean.TRUE.equals(initial.get("played")) || !Data.list(initial.get("events")).isEmpty())throw new AssertionError("Initial snapshot leaks future outcome");
        for(String key:Arrays.asList("attendance","attendanceBySection","revenue","homePenalties","awayPenalties","winnerClubId"))if(initial.get(key)!=null)throw new AssertionError("Precomputed value exposed: "+key);
        for(int tick=0;tick<85;tick++){
            if(playback.status().equals("HALF_TIME"))playback.resumeSecondHalf();else playback.tick();
            Map<String,Object> row=Data.map(Data.list(playback.snapshot().get("matches")).get(0));
            for(String key:Arrays.asList("attendance","attendanceBySection","revenue","homePenalties","awayPenalties","winnerClubId"))if(row.get(key)!=null)throw new AssertionError("Precomputed value exposed during playback: "+key);
            int home=0,away=0;for(Object raw:Data.list(row.get("events"))){Map<String,Object> event=Data.map(raw);int half=Data.number(event.get("half")),minute=Data.number(event.get("minute"))-(half==2?45:0);if(half>playback.half() || (half==playback.half() && minute>=playback.halfMinute()))throw new AssertionError("Future event exposed");if(Data.number(event.get("type"))==1){if(match.hc().getNome().equals(event.get("club")))home++;else away++;}}
            if(Data.number(row.get("homeGoals"))!=home || Data.number(row.get("awayGoals"))!=away || Boolean.TRUE.equals(row.get("played")))throw new AssertionError("Score is not temporal");
            if(!Data.list(row.get("shots")).equals(Arrays.asList(null,null)))throw new AssertionError("Final AI statistics exposed");
            if(match.hu()!=finalHome || match.hw()!=finalAway)throw new AssertionError("Presentation mutated the native result");
        }
        while(!playback.status().equals("FINISHED")){if(playback.status().equals("HALF_TIME"))playback.resumeSecondHalf();else playback.tick();}
        Map<String,Object> finished=Data.map(Data.list(playback.snapshot().get("matches")).get(0));if(!Boolean.TRUE.equals(finished.get("financialDataAvailable"))||Data.number(finished.get("attendance"))!=match.hU()||Data.number(finished.get("revenue"))!=match.hN()||(match.hQ()[0]>=0?Data.number(finished.get("homePenalties"))!=match.hQ()[0]:finished.get("homePenalties")!=null))throw new AssertionError("Final snapshot did not release native values");
        System.out.println("PASS native AI precomputation remains unchanged; minute projection conceals future goals, events, completion and statistics");
    }
}
