package brasfoot;

import best.F;
import best.ah;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/** Original junior academy table with host-only promotion and locked local mutations. */
public final class NativeYouth {
    private static final Map<JDialog,OpenWindow> OPEN=new IdentityHashMap<JDialog,OpenWindow>();
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
        OPEN.put(dialog,new OpenWindow(club,panel,table,promoteButton));dialog.addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent event){OPEN.remove(dialog);}});
        dialog.add(panel);dialog.setSize(763,568);dialog.setPreferredSize(new java.awt.Dimension(763,568));dialog.setModal(true);dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setUndecorated(true);dialog.setVisible(true);
    }
    /** Refresh an open native academy after its host projection changes. Must run on the EDT. */
    public static void refreshOpenWindows(ah club){
        if(!SwingUtilities.isEventDispatchThread()){SwingUtilities.invokeLater(()->refreshOpenWindows(club));return;}
        for(Map.Entry<JDialog,OpenWindow> entry:new java.util.ArrayList<Map.Entry<JDialog,OpenWindow>>(OPEN.entrySet())){
            JDialog dialog=entry.getKey();OpenWindow open=entry.getValue();if(!dialog.isDisplayable() || open.club!=club)continue;
            int selectedId=-1,oldRow=open.table.getSelectedRow();if(oldRow>=0){Object value=open.table.getValueAt(oldRow,0);if(value instanceof F)selectedId=((F)value).gD();}
            if(open.table.getModel() instanceof AbstractTableModel)((AbstractTableModel)open.table.getModel()).fireTableDataChanged();
            invoke(open.panel,"pK");int next=-1,fallback=-1;
            for(int row=0;row<open.table.getRowCount();row++){Object value=open.table.getValueAt(row,0);if(!(value instanceof F))continue;if(fallback<0)fallback=row;if(((F)value).gD()==selectedId)next=row;}
            if(next<0)next=fallback;if(next>=0)open.table.setRowSelectionInterval(next,next);else{open.table.clearSelection();invoke(open.panel,"z",new Class<?>[]{F.class},new Object[]{null});}
            open.promote.setEnabled(open.table.getRowCount()>0 && club.kc().size()<35);
        }
    }
    private static void invoke(Object target,String method){invoke(target,method,new Class<?>[0],new Object[0]);}
    private static void invoke(Object target,String method,Class<?>[] types,Object[] values){try{Method call=target.getClass().getMethod(method,types);call.invoke(target,values);}catch(Exception failure){throw new IllegalStateException("Could not refresh native academy",failure);}}
    private static final class OpenWindow {final ah club;final a.ds panel;final JTable table;final JButton promote;OpenWindow(ah club,a.ds panel,JTable table,JButton promote){this.club=club;this.panel=panel;this.table=table;this.promote=promote;}}
    private static void lock(a.ds panel,String field,String label){JButton button=(JButton)Data.get(panel,field);for(ActionListener listener:button.getActionListeners())button.removeActionListener(listener);button.setEnabled(false);button.setToolTipText(label+" ainda não está integrada à sessão online");}
}
