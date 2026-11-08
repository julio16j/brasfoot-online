package brasfoot;

import best.I;
import best.ah;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Controlled native calendar fixtures, actual client window and real mouse interaction. */
public final class NativeRestDayWindowTest {
    private static NativeClient client;
    private static JFrame frame;
    private static final Map<String,Object> cases=new LinkedHashMap<String,Object>();
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]),output=Paths.get(args[1]);Files.createDirectories(output);
        System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());
        GameAdapter game=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        I initial=game.currentMatches().get(0);ah own=initial.hc(),other=initial.hd(),ai;
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile()))){ai=new ah((e.t)in.readObject());}
        ai.bX(2);ai.k(false);
        c.a.SR.S().clear();best.a next=new best.a();next.a(2022,0,8);c.a.SR.R().add(next);
        new I(null,0,other,ai,0,new f.a(),null);new I(null,0,own,ai,1,new f.a(),null);
        Map<String,Object> rest=state(game,1);
        check(!Boolean.TRUE.equals(Data.map(Data.list(Data.map(rest.get("game")).get("coaches")).get(0)).get("hasMatch")),"Fixture must have a rest day");
        check(Boolean.TRUE.equals(Data.map(Data.list(Data.map(rest.get("game")).get("coaches")).get(1)).get("hasMatch")),"Other coach must play");
        Map<String,Object> noNext=Data.parse(Json.write(rest));Data.map(noNext.get("game")).put("upcoming",Collections.emptyList());Data.map(noNext.get("round")).put("round",2);
        c.a.SR.l(1);Map<String,Object> playing=state(game,3);
        Robot robot=new Robot();robot.setAutoDelay(100);
        try{
            SwingUtilities.invokeAndWait(()->{try{
                client=new NativeClient(root);Data.set(client,"coachId",0);Data.set(client,"latest",rest);invoke("gameScreen",rest);frame=(JFrame)Data.get(client,"frame");frame.setSize(1280,900);frame.setAlwaysOnTop(true);frame.setVisible(true);frame.toFront();
            }catch(Exception e){throw new RuntimeException(e);}});robot.delay(500);robot.waitForIdle();
            assertAccess(false);capture(robot,output.resolve("rest-next.png"));
            SwingUtilities.invokeAndWait(()->{check(text("preparation").contains("Sem partida nesta data"),"Missing date explanation");check(text("status").contains("Sem partida nesta data"),"Missing visible reason");invoke("openLineup");check(Data.get(client,"editor")==null,"Rest day opened editor");});
            cases.put("restWithNextAndOtherCoachPlaying","PASS: three accesses blocked, next labelled consultation, no editor");
            show(noNext);assertAccess(false);capture(robot,output.resolve("rest-no-next.png"));cases.put("restWithoutNext","PASS: explicit no upcoming match");
            show(playing);assertAccess(true);capture(robot,output.resolve("match-editable.png"));
            click(robot,(JButton)Data.get(Data.get(client,"preparationPanel"),"HU"));robot.waitForIdle();
            SwingUtilities.invokeAndWait(()->{check(Data.get(client,"editor")!=null,"Native button did not open editor");invoke("accept",playing);});assertAccess(false);
            SwingUtilities.invokeAndWait(()->((JDialog)Data.get(client,"editor")).dispose());robot.waitForIdle();assertAccess(true);
            click(robot,(JButton)Data.get(client,"lineup"));robot.waitForIdle();
            SwingUtilities.invokeAndWait(()->{check(Data.get(client,"editor")!=null,"Top button did not open editor");((JDialog)Data.get(client,"editor")).dispose();});robot.waitForIdle();
            cases.put("matchAndEditor","PASS: top and native buttons open editor; three accesses blocked while open, restored after close");
            Map<String,Object> ready=Data.parse(Json.write(playing));Data.map(Data.list(Data.map(ready.get("round")).get("coaches")).get(0)).put("ready",true);
            show(ready);assertAccess(false);SwingUtilities.invokeAndWait(()->check(text("status").contains("Retire o Pronto"),"Missing ready explanation"));
            show(playing);assertAccess(true);cases.put("readyAndWithdrawal","PASS: projected ready disables all; withdrawal restores all");
            Map<String,Object> running=Data.parse(Json.write(rest));Data.map(running.get("round")).put("round",4);Data.map(running.get("round")).put("phase","RUNNING");
            show(running);assertAccess(false);SwingUtilities.invokeAndWait(()->check(text("status").contains("todos os jogos desta data"),"Missing match list scope"));capture(robot,output.resolve("rest-running.png"));
            cases.put("restDuringOtherMatch","PASS: list scope explicit; no simulation or calendar skip requested");
            Files.write(output.resolve("result.json"),Json.write(cases).getBytes("UTF-8"));System.out.println("PASS "+Json.write(cases));
        }finally{SwingUtilities.invokeAndWait(()->{if(client!=null)invoke("close");for(Window window:Window.getWindows())window.dispose();});}
    }
    private static Map<String,Object> state(GameAdapter game,int number){
        Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("game",game.snapshot());Map<String,Object> round=new RoundCoordinator(game.coachIds(),game).snapshot();round.put("round",number);result.put("round",round);result.put("playback",new MatchPlayback(game.currentMatches()).snapshot());result.put("sessionOptions",SessionOptions.load(null).snapshot(true,true,null));result.put("host",true);return result;
    }
    private static void show(Map<String,Object> state)throws Exception{SwingUtilities.invokeAndWait(()->invoke("accept",state));}
    private static void assertAccess(boolean enabled)throws Exception{SwingUtilities.invokeAndWait(()->{
        for(String field:new String[]{"lineup","auto"})check(((JButton)Data.get(client,field)).isEnabled()==enabled,"Wrong top access: "+field);
        JPanel panel=(JPanel)Data.get(client,"preparationPanel");if(panel!=null)check(((JButton)Data.get(panel,"HU")).isEnabled()==enabled,"Native access differs");
    });}
    private static String text(String field){return ((JLabel)Data.get(client,field)).getText();}
    private static void invoke(String name,Object... args){try{Method method=NativeClient.class.getDeclaredMethod(name,args.length==0?new Class<?>[0]:new Class<?>[]{Map.class});method.setAccessible(true);method.invoke(client,args);}catch(Exception e){throw new RuntimeException(e);}}
    private static void click(Robot robot,Component control)throws Exception{Point[] p={null};SwingUtilities.invokeAndWait(()->{p[0]=control.getLocationOnScreen();p[0].translate(control.getWidth()/2,control.getHeight()/2);});robot.mouseMove(p[0].x,p[0].y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.delay(250);}
    private static void capture(Robot robot,Path output)throws Exception{robot.waitForIdle();robot.delay(250);Rectangle[] bounds={null};SwingUtilities.invokeAndWait(()->bounds[0]=frame.getBounds());ImageIO.write(robot.createScreenCapture(bounds[0]),"png",output.toFile());}
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
}
