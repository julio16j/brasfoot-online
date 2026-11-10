package brasfoot;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
public final class NativeHiddenAttendanceWindowTest {
 public static void main(String[] args)throws Exception{
  Path root=Paths.get(args[0]);GameAdapter game=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
  best.I match=game.currentMatches().get(0);MatchPlayback playback=new MatchPlayback(game.currentMatches());Map<String,Object> live=playback.snapshot(),row=Data.map(Data.list(live.get("matches")).get(0));row.put("attendance",null);row.put("attendanceBySection",null);row.put("revenue",null);row.put("statisticsAvailable",false);Map<String,Object> state=new LinkedHashMap<String,Object>();state.put("playback",live);
  JFrame[] window={null};SwingUtilities.invokeAndWait(()->{JFrame frame=new JFrame("Playback com dados indisponiveis");window[0]=frame;NativeMatchPanel panel=new NativeMatchPanel(frame,game.currentMatches());panel.muted(true);frame.add(panel);frame.pack();frame.setLocationRelativeTo(null);frame.setVisible(true);panel.project(state);if(!"--".equals(((JLabel)Data.get(panel,"Hm")).getText()))throw new AssertionError("Unavailable attendance was rendered as a known number or null");JTable table=(JTable)Data.get(panel,"Hs");int fixtureRow=0;while(fixtureRow<table.getRowCount()&&((components.v)table.getValueAt(fixtureRow,0)).tR()==null)fixtureRow++;if(fixtureRow==table.getRowCount())throw new AssertionError("No fixture in native table");JLabel stadium=(JLabel)table.prepareRenderer(table.getCellRenderer(fixtureRow,0),fixtureRow,0);if(!stadium.getText().endsWith(" | --"))throw new AssertionError("Match list attendance: ["+stadium.getText()+"] renderer="+table.getCellRenderer(0,0).getClass().getName());row.put("attendance",42);panel.project(state);if(!"42".equals(((JLabel)Data.get(panel,"Hm")).getText())||!((JLabel)table.prepareRenderer(table.getCellRenderer(fixtureRow,0),fixtureRow,0)).getText().endsWith(" | 42"))throw new AssertionError("Released attendance did not reach both native views");row.put("attendance",null);panel.project(state);});
  new Robot().waitForIdle();Thread.sleep(350);try{Path evidence=root.resolve("build/evidence-playback");Files.createDirectories(evidence);ImageIO.write(new Robot().createScreenCapture(window[0].getBounds()),"png",evidence.resolve("hidden-attendance.png").toFile());}finally{SwingUtilities.invokeAndWait(()->window[0].dispose());}
  System.out.println("PASS real native match panel accepts null attendance and displays unavailable placeholder");
 }
}
