package brasfoot;

import best.ah;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** Opens the original Brasfoot career calendar and standings controls over the host projection. */
public final class NativeCareerScreens {
    private NativeCareerScreens(){}
    public static JDialog openCalendar(JFrame owner,ah club){
        JDialog dialog=new JDialog(owner,"Calendario - "+club.getNome(),true);dialog.setUndecorated(true);a.L panel=new a.L(dialog,club);
        // The online fixture DTO does not contain native event history for the detail popup.
        javax.swing.JTable table=(javax.swing.JTable)Data.get(panel,"vN");for(MouseListener listener:table.getMouseListeners())if(listener.getClass().getName().equals("a.P"))table.removeMouseListener(listener);
        dialog.add(panel);dialog.setSize(new Dimension(820,600));dialog.setPreferredSize(new Dimension(820,600));dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);return dialog;
    }
    public static JDialog openTables(JFrame owner,best.at competition){
        if(competition==null)throw new IllegalArgumentException("A tabela solicitada nao esta disponivel no catalogo do host");
        JDialog dialog=new JDialog(owner,"Tabelas - "+competition.getNome(),true);dialog.setUndecorated(true);a.ay panel=new a.ay(dialog,competition);
        dialog.add(panel);dialog.setSize(new Dimension(1000,740));dialog.setPreferredSize(new Dimension(1000,740));dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);return dialog;
    }
}
