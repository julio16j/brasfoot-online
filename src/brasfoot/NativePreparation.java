package brasfoot;

import best.F;
import best.I;
import best.ah;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.util.function.Consumer;

/** The original club dashboard, with network actions in place of local career actions. */
public final class NativePreparation {
    private NativePreparation() { }
    public static void lineupAvailability(JPanel panel,boolean enabled,String reason){
        JButton scale=(JButton)Data.get(panel,"HU");scale.setEnabled(enabled);scale.setToolTipText(reason);
    }
    @SuppressWarnings("unchecked")
    public static JPanel create(JFrame frame,ah club,I match,Runnable lineup){
        return create(frame,club,match,lineup,action->{ });
    }
    @SuppressWarnings("unchecked")
    public static JPanel create(JFrame frame,ah club,I match,Runnable lineup,Consumer<String> menu){
        best.f.a(club,match);
        java.util.List<F> order=new ArrayList<F>(club.kc());
        a.eg original=new a.eg(frame);
        JTable table=(JTable)Data.get(original,"Iy");
        Data.set(table.getModel(),"vp",new ArrayList<F>(club.kc()));
        club.kc().clear();club.kc().addAll(order);
        String[] fields={"HR","HS","HT","HV","HW","HX","yY","HY","HZ","Ia","Ib","Ic","Id","Ie"};
        Map<String,ActionListener[]> nativeActions=new LinkedHashMap<String,ActionListener[]>();
        for(String field:fields)nativeActions.put(field,((JButton)Data.get(original,field)).getActionListeners());
        disconnect(original);
        routePreferences(original,menu);
        JButton scale=(JButton)Data.get(original,"HU");scale.setEnabled(true);scale.addActionListener(e->lineup.run());
        for(String field:fields){JButton button=(JButton)Data.get(original,field);button.setEnabled(true);button.setToolTipText(null);
            if(field.equals("HV"))button.addActionListener(e->menu.accept("Estádio"));
            else if(field.equals("yY"))button.addActionListener(e->menu.accept("Procura"));
            else if(field.equals("Ib"))button.addActionListener(e->menu.accept("Times"));
            else if(field.equals("HW"))button.addActionListener(e->menu.accept("Juniores"));
            else if(field.equals("Id"))button.addActionListener(e->menu.accept("Treino"));
            else for(ActionListener action:nativeActions.get(field))button.addActionListener(action);
        }
        JPopupMenu actions=new JPopupMenu();
        for(String action:new String[]{"Renovar contrato","Colocar à venda","Retirar da lista de transferências"}){JMenuItem item=new JMenuItem(action);item.addActionListener(e->{int selected=table.getSelectedRow();if(selected>=0){F player=(F)table.getValueAt(table.convertRowIndexToModel(selected),0);menu.accept(action+":"+player.gD());}});actions.add(item);}
        table.setComponentPopupMenu(actions);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting() && table.getSelectedRow()>=0){Object player=table.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()),0);if(player instanceof F)original.s((F)player);}});
        return original;
    }
    private static void routePreferences(a.eg original,Consumer<String> menu){
        JPopupMenu popup=(JPopupMenu)Data.get(original,"Hy");int preferences=0;
        for(Component child:popup.getComponents())if(child instanceof JMenuItem){
            JMenuItem item=(JMenuItem)child;String label=item.getText();
            if("Preferências".equals(label)){
                preferences++;for(ActionListener action:item.getActionListeners())item.removeActionListener(action);
                item.setAccelerator(null);item.setEnabled(true);item.addActionListener(event->menu.accept("Opções"));
            }else if(!"Sobre o Brasfoot".equals(label)){
                for(ActionListener action:item.getActionListeners())item.removeActionListener(action);
                item.setAccelerator(null);item.setEnabled(false);item.setToolTipText("Esta ação local não está integrada à sessão online");
            }
        }
        if(preferences!=1)throw new IllegalStateException("Item nativo Preferências indisponível nesta versão");
    }
    private static void disconnect(Component item){
        if(item instanceof JComponent){JComponent c=(JComponent)item;c.getActionMap().clear();for(int condition:new int[]{0,1,2})c.getInputMap(condition).clear();c.setComponentPopupMenu(null);}
        // Preserve Swing's own selection and scrolling listeners.
        for(MouseListener listener:item.getMouseListeners())if(listener.getClass().getName().startsWith("a."))item.removeMouseListener(listener);
        if(item instanceof AbstractButton){AbstractButton button=(AbstractButton)item;for(ActionListener listener:button.getActionListeners())button.removeActionListener(listener);button.setEnabled(false);button.setToolTipText("Disponível quando esta ação estiver integrada ao host");}
        if(item instanceof Container)for(Component child:((Container)item).getComponents())disconnect(child);
    }
}
