package brasfoot;
import best.F;
import best.I;
import best.ah;
import java.util.*;
import java.awt.*;
import javax.swing.*;
/** Uses the original draggable pitch; submission is rerouted to the host. */
public final class NativeLineup {
    private NativeLineup() { }
    public interface Submit { void send(Map<String,String> form,Runnable success); }
    public static JDialog open(JFrame owner,ah club,I match,Submit submit){
        JDialog dialog=new JDialog(owner,"Escalação online — "+club.getNome(),false);
        a.bz original=new a.bz(dialog,club,match,false);
        seed(original,club);
        Runnable save=()->{try{submit.send(extract(original,club),dialog::dispose);}catch(Exception error){JOptionPane.showMessageDialog(dialog,error.getMessage(),"Escalação",JOptionPane.WARNING_MESSAGE);}};
        JButton play=(JButton)Data.get(original,"Bn");for(java.awt.event.ActionListener listener:play.getActionListeners())play.removeActionListener(listener);play.setText("Enviar escalação");play.addActionListener(e->save.run());
        JButton back=(JButton)Data.get(original,"Bo");for(java.awt.event.ActionListener listener:back.getActionListeners())back.removeActionListener(listener);back.setText("Cancelar");back.addActionListener(e->dialog.dispose());
        for(Object key:new Object[]{"AW"})for(Component item:((JPopupMenu)Data.get(original,(String)key)).getComponents())if(item instanceof JMenuItem){item.setEnabled(false);((JMenuItem)item).setToolTipText("Batedores e capitão ainda não configuráveis no online");}
        routeKeyboard(original,save);
        dialog.add(new JScrollPane(original));dialog.setSize(1080,800);dialog.setMinimumSize(new Dimension(850,600));dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);return dialog;
    }
    @SuppressWarnings("unchecked")
    public static void seed(a.bz original,ah club){
        components.ag saved=new components.ag();Set<Integer> occupied=new HashSet<Integer>();
        for(Object value:club.kc()){F player=(F)value;if(player.fT()>=1 && player.fT()<=36){saved.uH().add(player);saved.uI().add(player.fT());occupied.add(player.fT());}}
        if(saved.uH().isEmpty())return;
        for(int slot=26;slot<=36;slot++)if(!occupied.contains(slot)){saved.uH().add(null);saved.uI().add(slot);}
        original.a(saved,false);
    }
    private static void routeKeyboard(Component component,Runnable save){
        if(component instanceof JComponent){JComponent c=(JComponent)component;c.getActionMap().put("st",new AbstractAction(){private static final long serialVersionUID=1L;public void actionPerformed(java.awt.event.ActionEvent e){save.run();}});}
        if(component instanceof JCheckBox){component.setEnabled(false);((JCheckBox)component).setToolTipText("Neste teste online é usado apenas o elenco principal");}
        if(component instanceof Container)for(Component child:((Container)component).getComponents())routeKeyboard(child,save);
    }
    public static Map<String,String> extract(a.bz original,ah club){
        java.util.List<Object> cells=Data.list(Data.get(original,"AE"));
        java.util.List<Integer> starters=new ArrayList<Integer>(),bench=new ArrayList<Integer>(),slots=new ArrayList<Integer>();
        for(int slot=1;slot<=36 && slot<cells.size();slot++){
            F player=((components.ao)cells.get(slot)).x();if(player==null)continue;
            int index=club.kc().indexOf(player);if(index<0)throw new IllegalArgumentException("Use apenas jogadores do elenco principal");
            if(slot<=25){starters.add(index);slots.add(slot);}else bench.add(index);
        }
        if(starters.size()!=11)throw new IllegalArgumentException("Selecione 11 titulares no campo");
        java.util.List<Integer> tactics=new ArrayList<Integer>();for(int value:club.kj())tactics.add(value);
        Map<String,String> result=new LinkedHashMap<String,String>();result.put("starters",Data.csv(starters));result.put("bench",Data.csv(bench));result.put("slots",Data.csv(slots));result.put("tactics",Data.csv(tactics));return result;
    }
}
