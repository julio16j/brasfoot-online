package brasfoot;

import best.F;
import best.I;
import best.ah;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Opens the original Swing editor and drives its native role menu callbacks. */
public final class SpecialRolesWindowTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static int[] values(Object raw){java.util.List<Object> rows=Data.list(raw);int[] result=new int[rows.size()];for(int i=0;i<result.length;i++)result[i]=Data.number(rows.get(i));return result;}
    private static int[] array(java.util.List<Integer> values){int[] result=new int[values.size()];for(int i=0;i<result.length;i++)result[i]=values.get(i);return result;}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());final Throwable[] failure=new Throwable[1];
        SwingUtilities.invokeAndWait(()->{JFrame frame=null;JDialog dialog=null;try{
            GameAdapter game=Fixture.create(new File(root,"game/Brasfoot22-23/teams/abcrn_bra.ban"),new File(root,"game/Brasfoot22-23/teams/corinthians_bra.ban"));c.a.vH();
            Map<String,Object> coach=Data.map(Data.list(game.snapshot().get("coaches")).get(0)),suggestion=Data.map(coach.get("suggestion"));java.util.List<Integer> starters=new ArrayList<Integer>(),bench=new ArrayList<Integer>();for(int value:values(suggestion.get("starters")))starters.add(value);for(int value:values(suggestion.get("bench")))bench.add(value);
            int high=-1;for(Object raw:Data.list(coach.get("players"))){Map<String,Object> player=Data.map(raw);int index=Data.number(player.get("index"));if(index>=20&&Data.number(player.get("position"))!=0&&Boolean.TRUE.equals(player.get("eligible")))high=index;}
            if(high<0)throw new AssertionError("Fixture lacks an eligible field player at roster index 20 or above");int prior=starters.get(1),existing=starters.indexOf(high);if(existing>=0){starters.set(existing,prior);starters.set(1,high);}else{starters.set(1,high);if(bench.remove(Integer.valueOf(high)))bench.add(prior);}
            game.lineup(0,array(starters),array(bench));
            I match=game.currentMatches().get(0);ah club=match.hc();frame=new JFrame("Special roles online validation");frame.setSize(1180,850);frame.setLocationRelativeTo(null);frame.setVisible(true);
            @SuppressWarnings("unchecked") Map<String,String>[] submitted=new Map[1];dialog=NativeLineup.open(frame,club,match,(form,success)->{submitted[0]=form;success.run();});dialog.setVisible(true);a.bz editor=(a.bz)((JScrollPane)dialog.getContentPane().getComponent(0)).getViewport().getView();
            JPopupMenu menu=(JPopupMenu)Data.get(editor,"AW");java.util.List<JMenuItem> actions=new ArrayList<JMenuItem>();for(Component item:menu.getComponents())if(item instanceof JMenuItem)actions.add((JMenuItem)item);
            check(actions.size()==4&&actions.get(0).isEnabled()&&actions.get(1).isEnabled()&&actions.get(2).isEnabled()&&!actions.get(3).isEnabled(),"Only the three supported roles are enabled");
            int action=0;java.util.List<Object> cells=Data.list(Data.get(editor,"AE"));for(int slot=1;slot<=25&&action<3;slot++){F player=((components.ao)cells.get(slot)).x();if(player==null||player.getPosicao()==0)continue;Data.set(editor,"AU",slot);actions.get(action++).doClick();}check(action==3,"Fixture exposes three field starters");
            F freeKick=club.ke(),captain=club.kd(),corner=club.lq();check(freeKick!=null&&captain!=null&&corner!=null,"Native menu callbacks update the intended role fields");check(club.kc().indexOf(freeKick)>=20,"High-index player was selected for a role by stable identity");
            ((JButton)Data.get(editor,"Bn")).doClick();String[] ids=submitted[0].get("roles").split(",");check(ids.length==4&&Integer.parseInt(ids[0])==freeKick.gD()&&Integer.parseInt(ids[1])==captain.gD()&&Integer.parseInt(ids[2])==corner.gD(),"Editor submits stable player IDs");
            BufferedImage image=new BufferedImage(editor.getWidth(),editor.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D graphics=image.createGraphics();editor.printAll(graphics);graphics.dispose();ImageIO.write(image,"png",new File(root,"analysis/special-roles-lineup.png"));
            dialog.dispose();dialog=NativeLineup.open(frame,club,match,(ignored,success)->success.run(),true,false);JPopupMenu liveMenu=(JPopupMenu)Data.get((a.bz)((JScrollPane)dialog.getContentPane().getComponent(0)).getViewport().getView(),"AW");for(Component item:liveMenu.getComponents())if(item instanceof JMenuItem)check(!item.isEnabled(),"Live detail stays read-only for special roles");
            System.out.println("PASS original Swing role menu, stable-ID form, supported actions, and online phase gating");
        }catch(Throwable error){failure[0]=error;}finally{if(dialog!=null)dialog.dispose();if(frame!=null)frame.dispose();}});
        if(failure[0]!=null){failure[0].printStackTrace();System.exit(1);}
    }
}
