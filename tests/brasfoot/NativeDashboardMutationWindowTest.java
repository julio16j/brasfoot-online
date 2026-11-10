package brasfoot;
import java.nio.file.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
/** Real dashboard regression for local actions without an online authority contract. */
public final class NativeDashboardMutationWindowTest {
 public static void main(String[] args)throws Exception{
  Path root=Paths.get(args[0]);System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
  final JFrame[] visible={null};SwingUtilities.invokeAndWait(()->{JFrame owner=new JFrame("Dashboard online");visible[0]=owner;try{
   best.I match=(best.I)c.a.SR.S().get(0);JPanel panel=NativePreparation.create(owner,match.hc(),match,()->{});owner.add(panel);owner.pack();owner.setLocationRelativeTo(null);owner.setVisible(true);
   JButton search=(JButton)Data.get(panel,"yY");NativePreparation.playerSearchAvailability(panel,false,"Procura disponivel apos a apresentacao da data");if(search.isEnabled()||!search.getToolTipText().contains("apresentacao"))throw new AssertionError("Search availability reason not applied");NativePreparation.playerSearchAvailability(panel,true,null);if(!search.isEnabled()||search.getToolTipText()!=null)throw new AssertionError("Search did not recover after barrier");NativePreparation.playerSearchAvailability(panel,false,"Procura disponivel apos a apresentacao da data");
   int before=c.a.SR.M().size();JButton colors=(JButton)Data.get(panel,"HT");if(colors.isEnabled()||colors.getActionListeners().length!=0)throw new AssertionError("Local color editor remains active");colors.doClick();
   JButton coaches=(JButton)Data.get(panel,"Ia");coaches.doClick();JPopupMenu popup=(JPopupMenu)Data.get(panel,"Hz");if(!popup.isVisible())throw new AssertionError("Coach queries menu did not open");int blocked=0,queries=0;
   for(Component child:popup.getComponents())if(child instanceof JMenuItem){JMenuItem item=(JMenuItem)child;if("Adicionar/remover t\u00e9cnicos".equals(item.getText())){blocked++;if(item.isEnabled()||item.getActionListeners().length!=0||item.getAccelerator()!=null)throw new AssertionError("Participant mutation remains active");item.doClick();}else if(item.isEnabled()&&item.getActionListeners().length>0)queries++;}
   if(blocked!=1||queries==0||before!=c.a.SR.M().size())throw new AssertionError("Mutation blocked or query preservation failed");
  }finally{}});new Robot().waitForIdle();Thread.sleep(350);try{Path dir=root.resolve("build/evidence-dashboard");Files.createDirectories(dir);ImageIO.write(new Robot().createScreenCapture(visible[0].getBounds()),"png",dir.resolve("dashboard.png").toFile());}finally{SwingUtilities.invokeAndWait(()->visible[0].dispose());}System.out.println("PASS real dashboard blocks local colors and participant mutations, preserves coach queries");
 }
}
