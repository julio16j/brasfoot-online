package brasfoot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Actual NativeClient network route, native modal window and Robot interaction. */
public final class TrainingClientWindowTest {
    private static NativeClient client;
    private static NativeTraining dialog;
    public static void main(String[] args)throws Exception{
        boolean desired=Boolean.parseBoolean(args[3]);int focus=Integer.parseInt(args[4]);File output=new File(args[5]);output.mkdirs();
        Robot robot=new Robot();robot.setAutoDelay(100);
        SwingUtilities.invokeAndWait(()->client=new NativeClient(Paths.get(args[0])));
        try{
            Method connect=NativeClient.class.getDeclaredMethod("connect",String.class,String.class);connect.setAccessible(true);connect.invoke(client,args[1],args[2]);
            await(()->Data.get(client,"nativePanel")!=null,"client connection");
            SwingUtilities.invokeLater(()->{JFrame frame=(JFrame)Data.get(client,"frame");frame.setVisible(true);invoke("openLineup");invoke("openNativeTraining");});
            await(()->Data.get(client,"trainingDialog")!=null && ((NativeTraining)Data.get(client,"trainingDialog")).isShowing(),"training modal");
            SwingUtilities.invokeAndWait(()->{dialog=(NativeTraining)Data.get(client,"trainingDialog");dialog.setAlwaysOnTop(true);dialog.toFront();});robot.delay(300);
            robot.mouseMove(dialog.getX()+100,dialog.getY()+15);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.delay(250);
            await(()->dialog.isActive(),"native dialog activation");
            a.iQ panel=(a.iQ)Data.get(dialog,"panel");JCheckBox assistant=(JCheckBox)Data.get(panel,"MY");
            best.F[] draft={null};
            SwingUtilities.invokeAndWait(()->{VisualModel visual=(VisualModel)Data.get(client,"visual");draft[0]=(best.F)visual.coach(Data.number(Data.get(client,"coachId"))).fg().kc().get(0);draft[0].as(24);});
            boolean[] initial={false};SwingUtilities.invokeAndWait(()->initial[0]=assistant.isSelected());
            if(initial[0]!=desired){click(robot,assistant);await(()->assistant.isEnabled() && assistant.isSelected()==desired,"assistant host confirmation");}
            JRadioButton selected=(JRadioButton)Data.get(panel,focus==0?"MW":"MX");
            click(robot,selected);await(()->selected.isEnabled() && selected.isSelected(),"focus host confirmation");
            SwingUtilities.invokeAndWait(()->{
                check(assistant.isSelected()==desired,"Focus reset assistant");
                check(Data.get(client,"editor")!=null && ((JDialog)Data.get(client,"editor")).isDisplayable(),"Concurrent lineup editor was not exercised");
                VisualModel visual=(VisualModel)Data.get(client,"visual");int viewer=Data.number(Data.get(client,"coachId"));
                check(visual.coach(viewer).fg().ll()==desired && visual.coach(viewer).fg().lm()==focus,"Lineup editor held stale training projection");
                check(visual.coach(viewer).fg().kc().get(0)==draft[0] && draft[0].fT()==24,"Training confirmation replaced lineup draft");
                for(String field:new String[]{"MY","MW","MX","vm"}){JComponent control=(JComponent)Data.get(panel,field);check(control.getVisibleRect().equals(new Rectangle(control.getSize())),"Clipped native control "+field);}
            });
            robot.waitForIdle();robot.delay(250);ImageIO.write(robot.createScreenCapture(dialog.getBounds()),"png",new File(output,"training-confirmed.png"));
            // Force one real revision conflict at the instant the user's intention is read.
            SwingUtilities.invokeAndWait(()->{
                Map<String,Object> latest=Data.map(Data.get(client,"latest"));int viewer=Data.number(latest.get("viewerCoachId"));
                for(Object raw:Data.list(Data.map(latest.get("round")).get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==viewer)row.put("revision",-1);}
                assistant.doClick();
            });
            await(()->{for(Window w:Window.getWindows())if(w instanceof JDialog && ((JDialog)w).isModal() && w!=dialog && w.isShowing())return true;return false;},"real stale-revision warning");
            SwingUtilities.invokeAndWait(()->{
                check(assistant.isEnabled() && assistant.isSelected()==desired,"Network conflict did not restore confirmed controls");
                for(Window w:Window.getWindows())if(w instanceof JDialog && ((JDialog)w).isModal() && w!=dialog && w.isShowing())w.dispose();
            });
            SwingUtilities.invokeAndWait(()->dialog.dispose());
            SwingUtilities.invokeLater(()->invoke("openNativeTraining"));
            await(()->((NativeTraining)Data.get(client,"trainingDialog")).isShowing(),"reopened training modal");
            SwingUtilities.invokeAndWait(()->{
                NativeTraining reopened=(NativeTraining)Data.get(client,"trainingDialog");a.iQ p=(a.iQ)Data.get(reopened,"panel");
                check(((JCheckBox)Data.get(p,"MY")).isSelected()==desired,"Reopen lost assistant");
                check(((JRadioButton)Data.get(p,focus==0?"MW":"MX")).isSelected(),"Reopen lost focus");reopened.dispose();
            });
            Map<String,Object> state=((SessionApi)Data.get(client,"api")).call("/state",null);int coach=Data.number(state.get("viewerCoachId"));int club=-1;
            for(Object raw:Data.list(Data.map(state.get("game")).get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==coach)club=Data.number(row.get("clubId"));}
            for(Object raw:Data.list(Data.map(state.get("game")).get("visualClubs"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("clubId"))==club)check(Boolean.valueOf(desired).equals(row.get("trainingAssistant")) && Data.number(row.get("trainingFocus"))==focus,"Host state differs from panel");}
            System.out.println("PASS real NativeClient: assistant="+desired+" focus="+focus+", HTTP host confirmation, conflict restoration, native panel, reopen, full layout");
        }catch(Throwable failure){
            if(dialog!=null && dialog.isShowing())ImageIO.write(robot.createScreenCapture(dialog.getBounds()),"png",new File(output,"training-failure.png"));
            SwingUtilities.invokeAndWait(()->{if(dialog!=null)System.err.println("UI diagnostic: active="+dialog.isActive()+" pending="+Data.get(dialog,"pending")+" assistant="+Data.get(dialog,"assistant")+" focus="+Data.get(dialog,"focus")+" available="+Data.get(dialog,"available"));});throw failure;
        }finally{SwingUtilities.invokeAndWait(()->{invoke("close");for(Window window:Window.getWindows())window.dispose();});}
    }
    private static void invoke(String name){try{Method m=NativeClient.class.getDeclaredMethod(name);m.setAccessible(true);m.invoke(client);}catch(Exception e){throw new RuntimeException(e);}}
    private interface Condition {boolean get();}
    private static void click(Robot robot,Component c)throws Exception{Point[] target={null};SwingUtilities.invokeAndWait(()->{Point p=c.getLocationOnScreen();p.translate(c.getWidth()/2,c.getHeight()/2);target[0]=p;});robot.mouseMove(target[0].x,target[0].y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.delay(250);}
    private static void check(boolean condition,String label){if(!condition)throw new AssertionError(label);}
    private static void await(Condition condition,String label)throws Exception{long deadline=System.currentTimeMillis()+15000;for(;;){boolean[] ok={false};SwingUtilities.invokeAndWait(()->ok[0]=condition.get());if(ok[0])return;if(System.currentTimeMillis()>deadline)throw new AssertionError("Timed out: "+label);Thread.sleep(50);}}
}
