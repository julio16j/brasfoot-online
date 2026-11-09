package brasfoot;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
public final class NativeUiSmoke {
    private static Map<String,Object> live(String status,int minute,int goals){
        best.I current=(best.I)c.a.SR.S().get(0);Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",0);row.put("home","ABC");row.put("away","Corinthians");row.put("homeId",current.hc().lk());row.put("awayId",current.hd().lk());row.put("homeGoals",goals);row.put("awayGoals",0);row.put("played",status.equals("FINISHED"));row.put("shots",Arrays.asList(8,5));row.put("possession",Arrays.asList(55,45));
        java.util.List<Object> events=new ArrayList<Object>();
        if(goals>0){best.I match=(best.I)c.a.SR.S().get(0);best.F player=(best.F)match.hc().kc().get(0);Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("id",0);e.put("type",1);e.put("detail",1);e.put("half",1);e.put("minute",12);e.put("clubId",match.hc().lk());e.put("club","ABC");e.put("playerId",player.gD());e.put("player",player.getNome());events.add(e);}row.put("events",events);
        Map<String,Object> playback=new LinkedHashMap<String,Object>();playback.put("status",status);playback.put("half",1);playback.put("minute",minute);playback.put("halfMinute",minute);playback.put("matches",Arrays.asList(row));
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("playback",playback);return result;
    }
    private static void capture(Component panel,Path path)throws Exception{
        panel.setSize(panel.getPreferredSize());if(panel instanceof Container)((Container)panel).doLayout();
        BufferedImage image=new BufferedImage(panel.getWidth(),panel.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=image.createGraphics();panel.printAll(g);g.dispose();ImageIO.write(image,"png",path.toFile());
    }
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]).toAbsolutePath();System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());
        final Throwable[] failure=new Throwable[1];
        final NativeMatchPanel[] penaltyPanel=new NativeMatchPanel[1];
        SwingUtilities.invokeAndWait(()->{try{
            GameAdapter local=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());c.a.vH();
            c.a.SR.setVerJanelaSubs(1);Map<String,Object> coach=Data.map(Data.list(local.snapshot().get("coaches")).get(0));Map<String,Object> suggestion=Data.map(coach.get("suggestion"));
            int[] starters=Data.list(suggestion.get("starters")).stream().mapToInt(Data::number).toArray();int[] bench=Data.list(suggestion.get("bench")).stream().mapToInt(Data::number).toArray();local.lineup(0,starters,bench);
            JFrame frame=new JFrame("Brasfoot Online - validation");NativeMatchPanel panel=new NativeMatchPanel(frame,local.currentMatches());panel.setPreferredSize(new Dimension(1030,650));frame.add(panel);frame.pack();
            panel.muted(false);panel.project(live("NOT_STARTED",0,0));panel.project(live("FIRST_HALF",12,1));javax.sound.sampled.Clip[] clips=(javax.sound.sampled.Clip[])Data.get(panel,"He");if(clips[2]==null || !clips[2].isOpen())throw new AssertionError("Native goal clip did not open on the output line");panel.project(live("HALF_TIME",45,1));panel.project(live("FINISHED",90,1));
            if(panel.GO!=null && panel.GO.isRunning())throw new AssertionError("Original timer must never run online");
            if(!panel.emittedSounds().equals(Arrays.asList(2,0,1)))throw new AssertionError(panel.emittedSounds());
            if(!((JLabel)Data.get(panel,"Hn")).getText().equals("1 x 0"))throw new AssertionError("Score projection");
            capture(panel,root.resolve("analysis/native-match-preview.png"));
            best.I match=local.currentMatches().get(0);JDialog dialog=NativeLineup.open(frame,match.hc(),match,(form,success)->success.run());a.bz editor=(a.bz)((JScrollPane)dialog.getContentPane().getComponent(0)).getViewport().getView();
            Map<String,String> extracted=NativeLineup.extract(editor,match.hc());if(extracted.get("starters").split(",").length!=11)throw new AssertionError("Native lineup submission must contain 11 players");if(extracted.get("slots").split(",").length!=11)throw new AssertionError("Native slots lost");
            editor.setPreferredSize(new Dimension(1040,720));dialog.pack();capture(editor,root.resolve("analysis/native-lineup-preview.png"));dialog.dispose();
            panel.stopAudio();frame.dispose();a.dV.qb();best.I matchForPenalty=local.currentMatches().get(0);NativeMatchPanel penalty=new NativeMatchPanel(null,local.currentMatches());penalty.muted(false);
            penalty.project(live("FIRST_HALF",0,0));Map<String,Object> pending=live("FIRST_HALF",12,0);Map<String,Object> playback=Data.map(pending.get("playback"));Map<String,Object> row=Data.map(Data.list(playback.get("matches")).get(0));best.F taker=(best.F)matchForPenalty.hc().kc().get(0);
            Map<String,Object> event=new LinkedHashMap<String,Object>();event.put("id",1);event.put("type",1);event.put("detail",3);event.put("half",1);event.put("minute",12);event.put("clubId",matchForPenalty.hc().lk());event.put("playerId",taker.gD());row.put("events",Collections.<Object>singletonList(event));
            penalty.project(pending);playback.put("status","FINISHED");playback.put("half",2);playback.put("minute",90);row.put("played",true);row.put("homeGoals",1);penalty.project(pending);
            if(!penalty.emittedSounds().equals(Arrays.asList(4,2,1))||!penalty.playedSounds().equals(Arrays.asList(4)))throw new AssertionError("Penalty and fulltime effects must queue after the first effect: "+penalty.playedSounds());
            javax.sound.sampled.Clip[] penaltyClips=(javax.sound.sampled.Clip[])Data.get(penalty,"He");if(penaltyClips[4]==null || !penaltyClips[4].isOpen())throw new AssertionError("Native penalty clip did not open on the output line");penaltyPanel[0]=penalty;
        }catch(Throwable error){failure[0]=error;}});
        if(failure[0]!=null){failure[0].printStackTrace();System.exit(1);}Thread.sleep(2200L);
        SwingUtilities.invokeAndWait(()->{try{NativeMatchPanel penalty=penaltyPanel[0];if(!penalty.playedSounds().equals(Arrays.asList(4,2)))throw new AssertionError("Penalty result did not play after native delay: "+penalty.playedSounds());javax.sound.sampled.Clip[] clips=(javax.sound.sampled.Clip[])Data.get(penalty,"He");if(clips[2]==null || !clips[2].isOpen())throw new AssertionError("Native penalty result clip did not open");}catch(Throwable error){error.printStackTrace();System.exit(1);}});
        Thread.sleep(2200L);SwingUtilities.invokeAndWait(()->{try{NativeMatchPanel penalty=penaltyPanel[0];if(!penalty.playedSounds().equals(Arrays.asList(4,2,1)))throw new AssertionError("Fulltime sound did not follow the penalty result: "+penalty.playedSounds());javax.sound.sampled.Clip[] clips=(javax.sound.sampled.Clip[])Data.get(penalty,"He");if(clips[1]==null || !clips[1].isOpen())throw new AssertionError("Native fulltime clip did not open after the penalty sequence");penalty.muted(true);a.dV.qb();System.out.println("PASS original Swing panels and goal/interval/fulltime output; penalty, result and fulltime played sequentially through open Clips");}catch(Throwable error){error.printStackTrace();System.exit(1);}});System.exit(0);
    }
}
