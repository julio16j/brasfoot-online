package brasfoot;

import best.F;
import best.ah;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/** Original junior academy table with host-only promotion and locked local mutations. */
public final class NativeYouth {
    private NativeYouth(){ }
    public interface Promotion {void submit(int playerId,JDialog dialog,JButton button);}
    public static void open(JFrame owner,ah club,Promotion promote){
        JDialog dialog=new JDialog(owner);a.ds panel=new a.ds(dialog,club,club);
        JTable table=(JTable)Data.get(panel,"FR");JButton promoteButton=(JButton)Data.get(panel,"FQ");
        lock(panel,"FP","Dispensar");lock(panel,"FN","Propor compra");lock(panel,"FO","Definir apelido");
        for(ActionListener listener:promoteButton.getActionListeners())promoteButton.removeActionListener(listener);
        promoteButton.addActionListener(event->{int selected=table.getSelectedRow();if(selected<0)return;int modelRow=table.convertRowIndexToModel(selected);Object value=table.getModel().getValueAt(modelRow,0);if(!(value instanceof F))return;F player=(F)value;
            if(JOptionPane.showConfirmDialog(dialog,"Deseja promover o junior "+player.getNome()+"?","Confirmar",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
            promoteButton.setEnabled(false);promote.submit(player.gD(),dialog,promoteButton);
        });
        dialog.add(panel);dialog.setSize(763,568);dialog.setPreferredSize(new java.awt.Dimension(763,568));dialog.setModal(true);dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setUndecorated(true);dialog.setVisible(true);
    }
    private static void lock(a.ds panel,String field,String label){JButton button=(JButton)Data.get(panel,field);for(ActionListener listener:button.getActionListeners())button.removeActionListener(listener);button.setEnabled(false);button.setToolTipText(label+" ainda não está integrada à sessão online");}
}
