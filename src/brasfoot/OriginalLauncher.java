package brasfoot;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
/** Boots the original game unchanged and adds the Online menu to its frames. */
public final class OriginalLauncher {
    private static Path root;
    private static void install(JFrame frame){
        if(Boolean.TRUE.equals(frame.getRootPane().getClientProperty("brasfoot.online.menu")))return;
        frame.getRootPane().putClientProperty("brasfoot.online.menu",true);
        JMenuBar bar=frame.getJMenuBar();if(bar==null){bar=new JMenuBar();frame.setJMenuBar(bar);}
        JMenu online=new JMenu("Online");
        JMenuItem create=new JMenuItem("Criar sessão online"),join=new JMenuItem("Entrar em uma sessão");
        create.addActionListener(e->open(true));join.addActionListener(e->open(false));online.add(create);online.add(join);bar.add(online);frame.getRootPane().revalidate();frame.repaint();
    }
    private static void open(boolean create){
        try{
            Path java=Paths.get(System.getProperty("java.home"),"bin","javaw.exe");
            ProcessBuilder process=new ProcessBuilder(java.toString(),"-cp",System.getProperty("java.class.path"),"brasfoot.NativeClient","--root",root.toString(),"--mode",create?"create":"join");
            process.directory(root.resolve("game/Brasfoot22-23").toFile());process.redirectErrorStream(true);process.redirectOutput(ProcessBuilder.Redirect.appendTo(root.resolve("analysis/native-client.log").toFile()));process.start();
        }catch(Exception error){JOptionPane.showMessageDialog(null,error.getMessage(),"Não foi possível abrir o multiplayer",JOptionPane.ERROR_MESSAGE);}
    }
    public static void main(String[] args)throws Exception{
        root=Paths.get(args.length>0?args[0]:".").toAbsolutePath().normalize();System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());
        Toolkit.getDefaultToolkit().addAWTEventListener(event->{if(event instanceof WindowEvent && event.getID()==WindowEvent.WINDOW_OPENED){Window window=((WindowEvent)event).getWindow();if(window instanceof JFrame)SwingUtilities.invokeLater(()->install((JFrame)window));}},AWTEvent.WINDOW_EVENT_MASK);
        if(args.length>1 && args[1].equals("--smoke")){Thread watchdog=new Thread(()->{try{Thread.sleep(9000);}catch(InterruptedException ignored){return;}for(Map.Entry<Thread,StackTraceElement[]> entry:Thread.getAllStackTraces().entrySet()){System.err.println(entry.getKey().getName());for(StackTraceElement element:entry.getValue())System.err.println("  "+element);}System.exit(2);},"smoke-watchdog");watchdog.setDaemon(true);watchdog.start();}
        try{best.h2.main(new String[0]);}catch(Throwable error){error.printStackTrace();System.exit(1);return;}
        if(args.length>1 && args[1].equals("--smoke"))SwingUtilities.invokeLater(()->{
            javax.swing.Timer check=new javax.swing.Timer(2000,event->{try{
                JFrame found=null;for(Window window:Window.getWindows())if(window instanceof JFrame && window.isShowing()){found=(JFrame)window;break;}
                if(found==null)throw new AssertionError("Original game window not visible");
                install(found);boolean online=false;for(int i=0;i<found.getJMenuBar().getMenuCount();i++)online |= found.getJMenuBar().getMenu(i).getText().equals("Online");
                if(!online)throw new AssertionError("Online menu missing");
                java.awt.image.BufferedImage image=new java.awt.image.BufferedImage(found.getWidth(),found.getHeight(),java.awt.image.BufferedImage.TYPE_INT_RGB);Graphics2D graphics=image.createGraphics();found.paintAll(graphics);graphics.dispose();javax.imageio.ImageIO.write(image,"png",root.resolve("analysis/original-launcher-preview.png").toFile());
                System.out.println("PASS original game launched with Online menu");System.exit(0);
            }catch(Throwable error){error.printStackTrace();System.exit(1);}});check.setRepeats(false);check.start();
        });
        SwingUtilities.invokeLater(()->{for(Window window:Window.getWindows())if(window instanceof JFrame)install((JFrame)window);});
    }
}
