package brasfoot;

import best.F;
import best.I;
import best.ah;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Interacts with the original academy Swing panel and verifies host promotion wiring. */
public final class NativeYouthWindowTest {
    private static JFrame frame;
    private static JPanel preparation;
    private static JDialog academy;
    private static ah club;
    private static CareerManagement management;
    private static int coachId,promotedId=-1;
    private static void check(boolean value,String message){if(!value){for(Window window:Window.getWindows())window.dispose();throw new AssertionError(message);}}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);Path output=Paths.get(args[1]);Files.createDirectories(output);PrintStream original=System.out;GameAdapter game;
        try{System.setOut(new PrintStream(new ByteArrayOutputStream()));game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});}finally{System.setOut(original);}
        management=new CareerManagement(game);coachId=game.coachIds().iterator().next();for(Object raw:c.a.SR.M())if(((best.al)raw).lT()==coachId)club=((best.al)raw).fg();check(club!=null&&!club.ky().isEmpty(),"Career club has an academy roster");
        I match=null;for(I candidate:game.currentMatches())if(candidate.hc()==club||candidate.hd()==club){match=candidate;break;}if(match==null){ah opponent=null;for(Object raw:c.a.SR.P())if(raw instanceof ah&&raw!=club){opponent=(ah)raw;break;}check(opponent!=null,"Career opponent exists");match=new I(null,0,club,opponent,0,new f.a(),null);}
        final I selectedMatch=match;SwingUtilities.invokeAndWait(()->{frame=new JFrame("Academy UI validation");preparation=NativePreparation.create(frame,club,selectedMatch,()->{},action->{if("Juniores".equals(action))NativeYouth.open(frame,club,(id,dialog,button)->{Map<String,String> request=new LinkedHashMap<String,String>();request.put("action","promote");request.put("playerId",String.valueOf(id));management.execute(coachId,request);promotedId=id;dialog.dispose();});});frame.add(preparation);frame.setSize(1024,720);frame.setLocationRelativeTo(null);frame.setAlwaysOnTop(true);frame.setVisible(true);frame.toFront();});
        Robot robot=new Robot();robot.setAutoDelay(100);JButton access=(JButton)Data.get(preparation,"HW");BufferedImageCapture(robot,frame,output.resolve("dashboard.png"));click(robot,access);robot.delay(500);if(findAcademy()==null)SwingUtilities.invokeAndWait(access::doClick);robot.delay(500);
        academy=findAcademy();if(academy==null){for(Window window:Window.getWindows())System.out.println("WINDOW "+window.getClass().getName()+" showing="+window.isShowing()+" title="+(window instanceof Frame?((Frame)window).getTitle():""));}check(academy!=null,"Native HW button opens the academy dialog");JPanel panel=(JPanel)academy.getContentPane().getComponent(0);JTable table=(JTable)Data.get(panel,"FR");JButton promote=(JButton)Data.get(panel,"FQ"),dismiss=(JButton)Data.get(panel,"FP"),purchase=(JButton)Data.get(panel,"FN"),nickname=(JButton)Data.get(panel,"FO");
        check(table.getColumnCount()==11&&table.getRowCount()==club.ky().size(),"Original native academy table shows host rows");check(promote.isEnabled(),"Promotion remains available through the host command");check(!dismiss.isEnabled()&&!purchase.isEnabled()&&!nickname.isEnabled(),"Unsupported academy mutations are explicitly disabled");
        table.setRowSelectionInterval(0,0);BufferedImageCapture(robot,academy,output.resolve("academy.png"));click(robot,promote);JDialog confirmation=findOptionDialog();check(confirmation!=null,"Promotion confirmation is shown");click(robot,firstButton(confirmation));
        long deadline=System.currentTimeMillis()+5000;while(academy.isShowing()&&System.currentTimeMillis()<deadline)Thread.sleep(50);
        check(!academy.isShowing()&&promotedId>=0,"Confirmed promotion closes after host response");boolean professional=false;for(Object raw:club.kc())if(((F)raw).gD()==promotedId)professional=true;check(professional,"Selected academy identity moved into the professional squad");for(Object raw:club.ky())check(((F)raw).gD()!=promotedId,"Promoted identity is absent from academy");
        System.out.println("PASS real academy table, disabled unsupported actions, and Robot-confirmed host promotion");SwingUtilities.invokeAndWait(()->frame.dispose());
    }
    private static JDialog findAcademy(){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()){Container content=((JDialog)window).getContentPane();if(findComponent(content,"FR")!=null)return (JDialog)window;}return null;}
    private static JDialog findOptionDialog(){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()&&((JDialog)window).getContentPane().getComponentCount()>0&&findOption(((JDialog)window).getContentPane()))return (JDialog)window;return null;}
    private static boolean findOption(Container container){if(container instanceof JOptionPane)return true;for(Component component:container.getComponents())if(component instanceof Container&&findOption((Container)component))return true;return false;}
    private static JButton firstButton(Container container){for(Component component:container.getComponents()){if(component instanceof JButton)return (JButton)component;if(component instanceof Container){JButton found=firstButtonOrNull((Container)component);if(found!=null)return found;}}throw new AssertionError("Confirmation has no button");}
    private static JButton firstButtonOrNull(Container container){for(Component component:container.getComponents()){if(component instanceof JButton)return (JButton)component;if(component instanceof Container){JButton found=firstButtonOrNull((Container)component);if(found!=null)return found;}}return null;}
    private static Component findComponent(Container container,String field){for(Component component:container.getComponents()){if(component instanceof a.ds){try{return (Component)Data.get(component,field);}catch(RuntimeException ignored){}}if(component instanceof Container){Component found=findComponent((Container)component,field);if(found!=null)return found;}}return null;}
    private static void click(Robot robot,Component component)throws Exception{Point[] point={null};SwingUtilities.invokeAndWait(()->{point[0]=component.getLocationOnScreen();point[0].translate(component.getWidth()/2,component.getHeight()/2);});robot.mouseMove(point[0].x,point[0].y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.waitForIdle();robot.delay(250);}
    private static void BufferedImageCapture(Robot robot,Window dialog,Path output)throws Exception{Rectangle[] bounds={null};SwingUtilities.invokeAndWait(()->bounds[0]=dialog.getBounds());ImageIO.write(robot.createScreenCapture(bounds[0]),"png",output.toFile());}
}
