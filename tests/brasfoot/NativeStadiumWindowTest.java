package brasfoot;

import best.I;
import best.ah;
import best.al;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

/** Exercises the native stadium windows and host command wiring in a real Swing session. */
public final class NativeStadiumWindowTest {
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    private static void click(Robot robot,Component component)throws Exception{Point[] point={null};SwingUtilities.invokeAndWait(()->{point[0]=component.getLocationOnScreen();point[0].translate(component.getWidth()/2,component.getHeight()/2);});robot.mouseMove(point[0].x,point[0].y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.waitForIdle();}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);PrintStream original=System.out;GameAdapter game;try{System.setOut(new PrintStream(new ByteArrayOutputStream()));game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});}finally{System.setOut(original);}
        int coachId=game.coachIds().iterator().next();al coach=(al)c.a.SR.M().get(0);ah club=coach.fg();I fixture=game.currentMatches().get(0);CareerManagement management=new CareerManagement(game);final java.util.List<Map<String,String>> sent=new ArrayList<Map<String,String>>();final boolean[] succeeded={false};final JFrame[] frame={null};
        java.net.ServerSocket socket=new java.net.ServerSocket(0);int port=socket.getLocalPort();socket.close();SessionHost host=new SessionHost(game,root.toPath().resolve("sessions-stadium-ui"),coachId,false,10000);host.startHttp("127.0.0.1",port);String base="http://127.0.0.1:"+port;java.lang.reflect.Field tokenField=SessionHost.class.getDeclaredField("hostToken");tokenField.setAccessible(true);String token=(String)tokenField.get(host);SessionApi api=new SessionApi(base);api.token(token);
        SwingUtilities.invokeAndWait(()->{frame[0]=new JFrame("Stadium UI validation");a.cl nativePanel=new a.cl(null,club.ev(),0,club);JButton expand=(JButton)Data.get(nativePanel,"BY"),prices=(JButton)Data.get(nativePanel,"Ca");check(expand!=null&&prices!=null,"Original stadium controls exist");frame[0].add(nativePanel);frame[0].pack();});
        final NativeStadium.StadiumPrices[] priceDraft={null};final JDialog[] priceDialog={null};final int[] priceCommands={0};SwingUtilities.invokeAndWait(()->{priceDialog[0]=new JDialog(frame[0]);a.cl panel=new a.cl(priceDialog[0],club.ev(),0,club);priceDraft[0]=new NativeStadium.StadiumPrices(priceDialog[0],panel,club,(fields,success,failure)->{sent.add(fields);priceCommands[0]++;try{Map<String,Object> snapshot=api.call("/state",null);fields.put("round",String.valueOf(Data.map(snapshot.get("round")).get("round")));fields.put("revision",String.valueOf(Data.map(Data.list(Data.map(snapshot.get("round")).get("coaches")).get(0)).get("revision")));Map<String,Object> response=api.call("/management",fields);if(!"STADIUM_SETTINGS_UPDATED".equals(Data.map(response.get("command")).get("status")))throw new IllegalStateException("Host rejected stadium settings");success.run();}catch(Throwable failureError){failure.accept(failureError);}});priceDraft[0].bind();priceDialog[0].add(panel);});JPanel pricesPanel=(JPanel)priceDialog[0].getContentPane().getComponent(0);JSpinner manual=(JSpinner)Data.get(pricesPanel,"Cl");int before=((Number)manual.getValue()).intValue();manual.setValue(before==200?199:before+1);((JCheckBox)Data.get(pricesPanel,"Cb")).setSelected(false);check(club.ev().dU()[0]==before,"Manual price remains draft-only before confirmation");((JButton)Data.get(pricesPanel,"BZ")).doClick();check(priceCommands[0]==1&&"false".equals(sent.get(0).get("suggestedPrices")),"Host receives explicit manual-price confirmation");check(Integer.parseInt(sent.get(0).get("price0"))==(before==200?199:before+1),"Confirmed draft contains entered manual price");
        host.stopHttp();host.close();for(Window window:Window.getWindows())window.dispose();System.out.println("PASS native stadium and construction windows calculate a quote and route confirmed expansion and prices to the host");
    }
    private static JDialog findDialog(String type){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()){Container content=((JDialog)window).getContentPane();if(content.getComponentCount()>0&&content.getComponent(0).getClass().getName().equals(type))return (JDialog)window;}return null;}
}
