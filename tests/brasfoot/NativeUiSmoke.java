package brasfoot;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
public final class NativeUiSmoke {
    private static Map<String,Object> live(String status,int minute,int goals){
        Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",0);row.put("home","ABC");row.put("away","Corinthians");row.put("homeGoals",goals);row.put("awayGoals",0);row.put("played",status.equals("FINISHED"));row.put("shots",Arrays.asList(8,5));row.put("possession",Arrays.asList(55,45));
        java.util.List<Object> events=new ArrayList<Object>();
        if(goals>0){Map<String,Object> e=new LinkedHashMap<String,Object>();e.put("id",0);e.put("type",1);e.put("detail",1);e.put("half",1);e.put("minute",12);e.put("club","ABC");e.put("player",((best.F)((best.I)c.a.SR.S().get(0)).hc().kc().get(0)).getNome());events.add(e);}row.put("events",events);
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
        SwingUtilities.invokeAndWait(()->{try{
            GameAdapter local=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());c.a.vH();
            Map<String,Object> coach=Data.map(Data.list(local.snapshot().get("coaches")).get(0));Map<String,Object> suggestion=Data.map(coach.get("suggestion"));
            int[] starters=Data.list(suggestion.get("starters")).stream().mapToInt(Data::number).toArray();int[] bench=Data.list(suggestion.get("bench")).stream().mapToInt(Data::number).toArray();local.lineup(0,starters,bench);
            JFrame frame=new JFrame("Brasfoot Online - validation");NativeMatchPanel panel=new NativeMatchPanel(frame,local.currentMatches());panel.setPreferredSize(new Dimension(1030,650));frame.add(panel);frame.pack();
            panel.muted(true);panel.project(live("NOT_STARTED",0,0));panel.project(live("FIRST_HALF",12,1));panel.project(live("HALF_TIME",45,1));panel.project(live("FINISHED",90,1));
            if(panel.GO!=null && panel.GO.isRunning())throw new AssertionError("Original timer must never run online");
            if(!panel.emittedSounds().equals(Arrays.asList(2,0,1)))throw new AssertionError(panel.emittedSounds());
            if(!((JLabel)Data.get(panel,"Hn")).getText().equals("1 x 0"))throw new AssertionError("Score projection");
            capture(panel,root.resolve("analysis/native-match-preview.png"));
            best.I match=local.currentMatches().get(0);JDialog dialog=NativeLineup.open(frame,match.hc(),match,(form,success)->success.run());a.bz editor=(a.bz)((JScrollPane)dialog.getContentPane().getComponent(0)).getViewport().getView();
            Map<String,String> extracted=NativeLineup.extract(editor,match.hc());if(extracted.get("starters").split(",").length!=11)throw new AssertionError("Native lineup submission must contain 11 players");if(extracted.get("slots").split(",").length!=11)throw new AssertionError("Native slots lost");
            editor.setPreferredSize(new Dimension(1040,720));dialog.pack();capture(editor,root.resolve("analysis/native-lineup-preview.png"));dialog.dispose();
            a.dV.qb();frame.dispose();System.out.println("PASS original match panel, original lineup panel, projected score, stopped local clock, and 3 audio event triggers");
        }catch(Throwable error){failure[0]=error;}});
        if(failure[0]!=null){failure[0].printStackTrace();System.exit(1);}System.exit(0);
    }
}
