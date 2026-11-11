package brasfoot;

import best.ah;
import best.al;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Checks the actual native stadium windows in a disposable career. */
public final class NativeStadiumLayoutTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static JDialog visible(String panelType){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()){
        JDialog dialog=(JDialog)window;Container content=dialog.getContentPane();if(content.getComponentCount()>0&&content.getComponent(0).getClass().getName().equals(panelType))return dialog;
    }return null;}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);File output=new File(args[1]);output.mkdirs();GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians"});
        al coach=(al)c.a.SR.M().get(0);ah club=coach.fg();JFrame frame=new JFrame("Stadium layout validation");frame.setSize(900,700);frame.setLocation(80,60);frame.setVisible(true);
        SwingUtilities.invokeLater(()->NativeStadium.openWithPriceSubmit(frame,club,0,(fields,success,failure)->failure.accept(new IllegalStateException("Validation only")),(fields,success,failure)->failure.accept(new IllegalStateException("Validation only"))));
        JDialog stadium=null;for(int i=0;i<100&&stadium==null;i++){Thread.sleep(100);stadium=visible("a.cl");}check(stadium!=null,"Stadium dialog did not open");
        final JDialog activeStadium=stadium;SwingUtilities.invokeAndWait(()->{activeStadium.setAlwaysOnTop(true);activeStadium.toFront();activeStadium.requestFocus();});
        JPanel panel=(JPanel)stadium.getContentPane().getComponent(0);JButton expand=(JButton)Data.get(panel,"BY");
        check(expand.isVisible(),"Expand button is hidden despite no active construction");Rectangle button=SwingUtilities.convertRectangle(expand.getParent(),expand.getBounds(),panel);
        check(button.y>=0&&button.y+button.height<=panel.getHeight(),"Expand button extends beyond the actual content pane");
        check(stadium.getTitle().equals("Estádio — "+club.getNome()),"Stadium title is corrupt");
        Robot robot=new Robot();robot.waitForIdle();BufferedImage capture=robot.createScreenCapture(stadium.getBounds());ImageIO.write(capture,"png",new File(output,"stadium-main.png"));
        Point point=expand.getLocationOnScreen();robot.mouseMove(point.x+expand.getWidth()/2,point.y+expand.getHeight()/2);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        JDialog construction=null;for(int i=0;i<100&&construction==null;i++){Thread.sleep(100);construction=visible("a.cq");}check(construction!=null,"Native construction dialog did not open from the visible button");final JDialog activeConstruction=construction;SwingUtilities.invokeAndWait(()->{activeStadium.setAlwaysOnTop(false);activeConstruction.setAlwaysOnTop(true);activeConstruction.toFront();activeConstruction.requestFocus();});robot.waitForIdle();ImageIO.write(robot.createScreenCapture(construction.getBounds()),"png",new File(output,"stadium-construction.png"));
        construction.dispose();stadium.dispose();frame.dispose();System.out.println("PASS native expansion button visible, clickable, correctly titled and opens original construction panel");
    }
}
