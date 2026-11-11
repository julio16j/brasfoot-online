package brasfoot;
import best.F;
import best.I;
import best.ah;
import java.util.*;
import java.lang.reflect.Method;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import javax.swing.*;
import java.util.function.Consumer;
/** Uses the original draggable pitch; submission is rerouted to the host. */
public final class NativeLineup {
    private NativeLineup() { }
    public interface Submit { void send(Map<String,String> form,Runnable success); }
    public static JDialog open(JFrame owner,ah club,I match,Submit submit){
        return open(owner,club,match,submit,true);
    }
    public static JDialog open(JFrame owner,ah club,I match,Submit submit,boolean editable){
        return open(owner,club,match,submit,editable,editable);
    }
    public static JDialog open(JFrame owner,ah club,I match,Submit submit,boolean editable,boolean rolesEditable){
        return open(owner,club,match,submit,editable,rolesEditable,editable?club:null,editable,null);
    }
    static JDialog open(JFrame owner,ah club,I match,Submit submit,boolean editable,boolean rolesEditable,ah editableClub,boolean editableSides,Consumer<JDialog> onOpen){
        return open(owner,club,match,submit,editable,rolesEditable,true,editableClub,editableSides,onOpen);
    }
    private static JDialog open(JFrame owner,ah club,I match,Submit submit,boolean editable,boolean rolesEditable,boolean allowSideSwitch,ah editableClub,boolean editableSides,Consumer<JDialog> onOpen){
        java.util.List<F> roster=new ArrayList<F>((java.util.List<F>)(java.util.List<?>)club.kc());
        JDialog dialog=new JDialog(owner,"Escalação online — "+club.getNome(),false);dialog.setUndecorated(true);
        a.bz original=new a.bz(dialog,club,match,false);
        seed(original,club,match);
        sortAvailablePlayers(original);
        int[] roles={roleId(club.ke()),roleId(club.kd()),roleId(club.lq()),roleId(club.lr())};
        Object[] baseline={club.ke(),club.kd(),club.lq(),club.lr()};boolean[] confirmed={false};
        Runnable save=()->{try{Map<String,String> form=extract(original,club,roster);if(rolesEditable)form.put("roles",Data.csv(Arrays.asList(roles[0],roles[1],roles[2],roles[3])));submit.send(form,()->{if(rolesEditable)applyRoles(club,roster,roles);confirmed[0]=true;dialog.dispose();});}catch(Exception error){JOptionPane.showMessageDialog(dialog,error.getMessage(),"Lineup",JOptionPane.WARNING_MESSAGE);}};
        JButton play=(JButton)Data.get(original,"Bn");for(java.awt.event.ActionListener listener:play.getActionListeners())play.removeActionListener(listener);play.setText(editable?"Aplicar alterações":"Somente leitura");if(editable)play.addActionListener(e->save.run());else play.setEnabled(false);
        JButton back=(JButton)Data.get(original,"Bo");for(java.awt.event.ActionListener listener:back.getActionListeners())back.removeActionListener(listener);back.setText("Fechar");back.addActionListener(e->dialog.dispose());
        int actionIndex=0;for(Object key:new Object[]{"AW"})for(Component item:((JPopupMenu)Data.get(original,(String)key)).getComponents())if(item instanceof JMenuItem){final int role=actionIndex++;item.setEnabled(rolesEditable && role<3);if(rolesEditable && role<3){JMenuItem menuItem=(JMenuItem)item;for(java.awt.event.ActionListener listener:menuItem.getActionListeners())menuItem.removeActionListener(listener);menuItem.addActionListener(event->{int selectedSlot=Data.number(Data.get(original,"AU"));F player=((components.ao)Data.list(Data.get(original,"AE")).get(selectedSlot)).x();if(player!=null){roles[role]=player.gD();setRole(club,role,player);refreshRolesPanel(original);}});}}
        if(editable)routeKeyboard(original,save);else{makeReadOnly(original,back);back.setEnabled(true);}
        back.setEnabled(true);
        JScrollPane scroll=new JScrollPane(original);dialog.setLayout(null);dialog.add(scroll);scroll.setBounds(0,allowSideSwitch?36:0,1080,allowSideSwitch?764:800);
        if(allowSideSwitch && match!=null){
            ah other=match.hc()==club?match.hd():match.hc();
            JButton switchSide=new JButton(match.hc()==club?"Ver visitante":"Ver mandante");switchSide.setToolTipText("Abrir a outra escala desta partida");switchSide.setBounds(8,4,150,28);dialog.add(switchSide);
            switchSide.addActionListener(event->{
                Object existing=dialog.getRootPane().getClientProperty("otherSideDialog");
                if(existing instanceof JDialog && ((JDialog)existing).isDisplayable()){
                    JDialog next=(JDialog)existing;if(onOpen!=null)onOpen.accept(next);dialog.setVisible(false);next.setVisible(true);return;
                }
                boolean editOther=editableSides && other==editableClub;JDialog next=open(owner,other,match,submit,editOther,false,true,editableClub,editableSides,onOpen);
                dialog.getRootPane().putClientProperty("otherSideDialog",next);next.getRootPane().putClientProperty("otherSideDialog",dialog);
                dialog.setVisible(false);next.setVisible(true);
            });
        }
        dialog.getRootPane().putClientProperty("clubId",club.lk());
        dialog.setSize(1080,800);dialog.setMinimumSize(new Dimension(850,600));dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent event){if(rolesEditable && !confirmed[0])restoreRoles(club,baseline);Object peer=dialog.getRootPane().getClientProperty("otherSideDialog");if(peer instanceof JDialog && ((JDialog)peer).isDisplayable()){((JDialog)peer).getRootPane().putClientProperty("otherSideDialog",null);dialog.getRootPane().putClientProperty("otherSideDialog",null);((JDialog)peer).dispose();}}});if(onOpen!=null)onOpen.accept(dialog);return dialog;
    }
    @SuppressWarnings("unchecked")
    private static void sortAvailablePlayers(a.bz original){
        java.util.List<F> players=(java.util.List<F>)(java.util.List<?>)Data.get(original,"AF");
        Collections.sort(players,(left,right)->{
            int position=Integer.compare(left.getPosicao(),right.getPosicao());if(position!=0)return position;
            int strength=Integer.compare(right.fi(),left.fi());if(strength!=0)return strength;
            int name=left.getNome().compareToIgnoreCase(right.getNome());return name!=0?name:Integer.compare(left.gD(),right.gD());
        });
        original.oe();
    }
    @SuppressWarnings("unchecked")
    public static void seed(a.bz original,ah club){seed(original,club,null);}
    @SuppressWarnings("unchecked")
    public static void seed(a.bz original,ah club,I match){
        components.ag saved=new components.ag();Set<Integer> occupied=new HashSet<Integer>();
        java.util.List<F> selected=new ArrayList<F>();if(match!=null){selected.addAll(match.hc()==club?match.hp():match.hq());selected.addAll(match.hc()==club?match.hn():match.ho());}else selected.addAll((java.util.List<F>)(java.util.List<?>)club.kc());
        for(F player:selected)if(player.fT()>=1 && player.fT()<=36){saved.uH().add(player);saved.uI().add(player.fT());occupied.add(player.fT());}
        if(saved.uH().isEmpty())return;
        for(int slot=26;slot<=36;slot++)if(!occupied.contains(slot)){saved.uH().add(null);saved.uI().add(slot);}
        original.a(saved,false);
    }
    private static void makeReadOnly(Component component,JButton closeButton){
        if(component==closeButton){component.setEnabled(true);return;}
        if(component instanceof JButton || component instanceof JComboBox || component instanceof JCheckBox || component instanceof JTextField || component instanceof JTable)component.setEnabled(false);
        for(java.awt.event.MouseListener listener:component.getMouseListeners())component.removeMouseListener(listener);
        if(component instanceof Container)for(Component child:((Container)component).getComponents())makeReadOnly(child,closeButton);
    }
    private static void routeKeyboard(Component component,Runnable save){
        if(component instanceof JComponent){JComponent c=(JComponent)component;c.getActionMap().put("st",new AbstractAction(){private static final long serialVersionUID=1L;public void actionPerformed(java.awt.event.ActionEvent e){save.run();}});}
        if(component instanceof JCheckBox){component.setEnabled(false);((JCheckBox)component).setToolTipText("Neste teste online é usado apenas o elenco principal");}
        if(component instanceof Container)for(Component child:((Container)component).getComponents())routeKeyboard(child,save);
    }
    public static Map<String,String> extract(a.bz original,ah club){
        return extract(original,club,new ArrayList<F>((java.util.List<F>)(java.util.List<?>)club.kc()));
    }
    private static Map<String,String> extract(a.bz original,ah club,java.util.List<F> roster){
        java.util.List<Object> cells=Data.list(Data.get(original,"AE"));
        java.util.List<Integer> starters=new ArrayList<Integer>(),bench=new ArrayList<Integer>(),slots=new ArrayList<Integer>();
        for(int slot=1;slot<=36 && slot<cells.size();slot++){
            F player=((components.ao)cells.get(slot)).x();if(player==null)continue;
            int index=roster.indexOf(player);if(index<0)throw new IllegalArgumentException("Use apenas jogadores do elenco principal");
            if(slot<=25){starters.add(index);slots.add(slot);}else bench.add(index);
        }
        if(starters.size()!=11)throw new IllegalArgumentException("Selecione 11 titulares no campo");
        int goalkeeper=-1;for(int i=0;i<starters.size();i++)if(roster.get(starters.get(i)).getPosicao()==0){if(goalkeeper>=0)throw new IllegalArgumentException("Selecione apenas um goleiro titular");goalkeeper=i;}
        if(goalkeeper<0)throw new IllegalArgumentException("O goleiro precisa estar entre os titulares");
        if(goalkeeper!=0){Integer player=starters.remove(goalkeeper),slot=slots.remove(goalkeeper);starters.add(0,player);slots.add(0,slot);}
        if(slots.get(0)!=1){int goalkeeperSlot=slots.get(0);for(int i=0;i<slots.size();i++)if(slots.get(i)==1){slots.set(i,goalkeeperSlot);break;}slots.set(0,1);}
        java.util.List<Integer> tactics=new ArrayList<Integer>();for(int value:club.kj())tactics.add(value);
        Map<String,String> result=new LinkedHashMap<String,String>();result.put("starters",Data.csv(starters));result.put("bench",Data.csv(bench));result.put("slots",Data.csv(slots));result.put("tactics",Data.csv(tactics));return result;
    }
    private static int roleId(F player){return player==null?-1:player.gD();}
    private static void setRole(ah club,int role,F player){if(role==0)club.k(player);else if(role==1)club.j(player);else if(role==2)club.n(player);else club.o(player);}
    private static void refreshRolesPanel(a.bz panel){try{Method refresh=a.bz.class.getDeclaredMethod("cS",int.class);refresh.setAccessible(true);refresh.invoke(panel,-1);}catch(Exception error){throw new IllegalStateException("Could not refresh native role symbols",error);}}
    private static void applyRoles(ah club,java.util.List<F> roster,int[] roles){club.k(rolePlayer(roster,roles[0]));club.j(rolePlayer(roster,roles[1]));club.n(rolePlayer(roster,roles[2]));club.o(rolePlayer(roster,roles[3]));}
    private static F rolePlayer(java.util.List<F> roster,int id){if(id<0)return null;for(F player:roster)if(player.gD()==id)return player;return null;}
    private static void restoreRoles(ah club,Object[] roles){club.k((F)roles[0]);club.j((F)roles[1]);club.n((F)roles[2]);club.o((F)roles[3]);}
}
