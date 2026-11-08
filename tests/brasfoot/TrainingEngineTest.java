package brasfoot;
import best.F;
import best.I;
import best.ah;
import best.al;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;

/** Tests native precedence and weekly queues through normal match completion/calendar advance. */
public final class TrainingEngineTest {
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);Path output=Paths.get(args[1]);Files.createDirectories(output);
        if(args.length>2){
            GameAdapter restored=GameAdapter.readLocalSave(output.resolve("weekly.s22").toFile());int expected=Integer.parseInt(args[2]);
            check(c.a.SR.bO()==expected,"Cycle changed during separate-JVM load");
            MatchPlayback finished=new MatchPlayback(restored.currentMatches(),true);check(finished.tick(),"Completed date did not restore finished");check(c.a.SR.bO()==expected,"Reload replayed weekly processing");
            check(((al)c.a.SR.M().get(0)).fg().ll() && ((al)c.a.SR.M().get(0)).fg().lm()==1,"Stored assistant/focus combination lost");
            System.out.println("PASS separate-JVM weekly resume without duplicated processing");return;
        }
        GameAdapter fixture=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));
        ah club=((al)c.a.SR.M().get(0)).fg();F player=(F)club.kc().get(0);Method consume=F.class.getDeclaredMethod("f",double.class);consume.setAccessible(true);
        for(boolean assistant:new boolean[]{false,true})for(int focus:new int[]{0,1})for(int cycle:new int[]{0,3}){
            club.M(assistant);club.bZ(focus);c.a.SR.A(cycle);double[] before=gains(player);consume.invoke(player,.001);double[] after=gains(player);int target=club.bQ(player.getPosicao());
            if(cycle==3 || (!assistant && focus==1))check(after[target]>before[target],"Native secondary precedence differs");
            else{double primary=0;for(int i:new int[]{0,3,4,5,6})primary+=after[i]-before[i];check(primary>0,"Native primary precedence differs");}
        }
        GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});c.a.SR.setHabilidadeIndividual(true);c.a.SR.A(0);
        CareerManagement commands=new CareerManagement(game);int first=game.coachIds().iterator().next();club=((al)c.a.SR.M().get(0)).fg();
        Map<String,String> command=new LinkedHashMap<String,String>();command.put("action","training-assistant");command.put("enabled","true");int cycle=c.a.SR.bO();commands.execute(first,command);command.clear();command.put("action","training-focus");command.put("focus","1");commands.execute(first,command);check(c.a.SR.bO()==cycle,"Command processed a week immediately");
        int completed=0,weeklyDates=0;boolean gainChanged=false;
        for(int day=0;day<15 && weeklyDates<2;day++){
            int queued=0;for(int index=0;index<=c.a.SR.J();index++)if(((best.a)c.a.SR.R().get(index)).r().contains("aj"))queued++;
            int beforeCycle=c.a.SR.bO();F observed=(F)club.kc().get(0);double[] before=gains(observed);
            for(I match:game.currentMatches()){
                if(match.hc().jZ())best.ah.a(match.hc(),match,1,-1,true);
                if(match.hd().jZ())best.ah.a(match.hd(),match,2,-1,true);
            }
            MatchPlayback playback=new MatchPlayback(game.currentMatches(),true);playback.begin();
            for(int tick=0;!playback.tick();tick++){check(tick<130,"Playback stalled");if(playback.status().equals("HALF_TIME"))playback.resumeSecondHalf();}
            completed++;check(c.a.SR.bO()==(beforeCycle+queued)%4,"Weekly queue count differs from completed date");
            if(queued>0){weeklyDates++;gainChanged|=!Arrays.equals(before,gains(observed));}
            int afterCycle=c.a.SR.bO();double[] after=gains(observed);playback.tick();check(c.a.SR.bO()==afterCycle && Arrays.equals(after,gains(observed)),"Finished playback processed twice");
            if(weeklyDates<2)CareerEngine.nextDate(game);
        }
        check(weeklyDates==2 && gainChanged,"Did not observe two weekly boundaries and native gain accumulation");
        game.saveLocal(output.resolve("weekly.s22").toFile());Files.write(output.resolve("weekly-cycle.txt"),String.valueOf(c.a.SR.bO()).getBytes("UTF-8"));
        System.out.println("PASS native precedence (8 combinations), "+completed+" normal dates, two weekly queues, gains changed, no duplicate completion, Kryo checkpoint");
    }
    private static double[] gains(F player){double[] values=new double[7];for(int i=0;i<7;i++)values[i]=player.aE(i);return values;}
    private static void check(boolean c,String label){if(!c)throw new AssertionError(label);}
}
