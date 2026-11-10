package brasfoot;

import best.F;
import best.ah;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/** Native player search and offer panels with the final transaction sent to the authoritative host. */
public final class NativeTransferSearch {
    public interface Submit { void send(Map<String,String> fields,Runnable success,Runnable failure); }
    public interface Query { void search(Map<String,String> filters,Consumer<Map<String,Object>> success,Consumer<Throwable> failure); }
    private NativeTransferSearch() { }

    public static void open(JFrame owner,ah buyer,int mode,Submit submit){open(owner,buyer,mode,submit,null,null);}

    public static void open(JFrame owner,ah buyer,int mode,Submit submit,NativeYouth.Promotion youthPromotion){
        open(owner,buyer,mode,submit,null,youthPromotion);
    }

    public static void open(JFrame owner,ah buyer,int mode,Submit submit,Query query,NativeYouth.Promotion youthPromotion){
        open(owner,buyer,mode,submit,query,youthPromotion,null);
    }
    public static void open(JFrame owner,ah buyer,int mode,Submit submit,Query query,NativeYouth.Promotion youthPromotion,NativeTransferMarket.AcademyQuery academyQuery){
        if(buyer==null)throw new IllegalStateException("Club is unavailable");
        JDialog dialog=new JDialog(owner,"Procura de jogadores",true);
        a.gx panel=new a.gx(dialog,buyer,false,mode);
        JTable table=(JTable)Data.get(panel,"zj");
        for(java.awt.event.MouseListener listener:table.getMouseListeners())
            if(isNativeOpenListener(listener.getClass().getName()))table.removeMouseListener(listener);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(event.getClickCount()==2)offerSelected(owner,dialog,panel,table,buyer,submit,youthPromotion,academyQuery);}});
        AbstractAction offer=new AbstractAction(){@Override public void actionPerformed(ActionEvent event){offerSelected(owner,dialog,panel,table,buyer,submit,youthPromotion,academyQuery);}};
        JButton searchButton=(JButton)Data.get(panel,"yY");replaceEnterAction(panel,offer);replaceEnterAction(table,offer);replaceEnterAction(searchButton,offer);
        if(query!=null){for(ActionListener listener:searchButton.getActionListeners())searchButton.removeActionListener(listener);searchButton.addActionListener(event->runSearch(dialog,panel,table,searchButton,query,buyer,mode));}
        dialog.add(panel);int width=mode==2?840:820,height=mode==2?670:620;
        dialog.setSize(width,height);dialog.setPreferredSize(new Dimension(width,height));dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }

    private static void offerSelected(JFrame owner,JDialog searchDialog,a.gx search,JTable table,ah buyer,Submit submit,NativeYouth.Promotion youthPromotion,NativeTransferMarket.AcademyQuery academyQuery){
        int selected=table.getSelectedRow();if(selected<0)return;
        Object row=valueAtSelectedRow(table,0);if(!(row instanceof F))return;
        F player=(F)row;if(player.fg()==null)return;
        NativeTransferMarket.openSelected(owner,searchDialog,buyer,player,submit,youthPromotion,academyQuery);
    }

    public static void showOffer(JFrame owner,JDialog parent,F player,ah buyer,Submit submit){
        ah seller=player.fg();if(seller==null || seller==buyer)return;
        JDialog offerDialog=new JDialog(parent,"Proposta de transferência",true);
        a.cz panel=new a.cz(offerDialog,player,buyer,false);
        JButton send=(JButton)Data.get(panel,"uD");JTextField amount=(JTextField)Data.get(panel,"uE");
        for(ActionListener listener:send.getActionListeners())send.removeActionListener(listener);
        send.addActionListener(event->{
            try{
                send.setEnabled(false);submitOffer(submit,player.gD(),seller.lk(),amount.getText(),()->{offerDialog.dispose();parent.dispose();},()->send.setEnabled(true));
            }catch(Exception error){send.setEnabled(true);JOptionPane.showMessageDialog(offerDialog,error.getMessage(),"Proposta",JOptionPane.WARNING_MESSAGE);}
        });
        offerDialog.add(panel);offerDialog.setSize(373,300);offerDialog.setPreferredSize(new Dimension(373,300));offerDialog.setResizable(false);
        offerDialog.setLocationRelativeTo(owner);offerDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);offerDialog.setVisible(true);
    }

    static boolean isNativeOpenListener(String className){if(className==null)return false;String normalized=className.toLowerCase(java.util.Locale.ROOT);return normalized.equals("a.gb")||normalized.startsWith("a.gb$");}

    static void replaceEnterAction(JComponent component,Action action){component.getActionMap().put("st",action);}

    static Map<String,String> captureFilters(a.gx panel,int mode){Object controls=Data.get(panel,"zi");Map<String,String> filters=new LinkedHashMap<String,String>();filters.put("name",((JTextField)Data.get(controls,"uf")).getText());combo(controls,"Nq","position",filters);combo(controls,"Qx","side",filters);combo(controls,"Qy","valueBand",filters);combo(controls,"Qv","characteristic1",filters);combo(controls,"Qw","characteristic2",filters);combo(controls,"BD","nationality",filters,true);combo(controls,"Qu","playingCountry",filters,false);
        if(mode==1)slider(controls,"QB","strength",filters,false);slider(controls,"QC","age",filters,true);
        if(mode==2){String[] names={"goalkeeping","defence","speed","finishing","arm","technique","passing"},components={"QN","QL","QQ","QM","QK","QP","QO"};for(int i=0;i<names.length;i++)slider(controls,components[i],names[i],filters,false);}
        checkbox(controls,"Qs","star",filters);checkbox(controls,"Qt","worldClass",filters);checkbox(controls,"Qr","loanAvailable",filters);checkbox(controls,"Qq","forSale",filters);return filters;}
    private static void combo(Object controls,String component,String key,Map<String,String> fields){combo(controls,component,key,fields,false);}
    private static void combo(Object controls,String controlsField,String key,Map<String,String> fields,boolean countryCatalog){javax.swing.JComboBox<?> combo=(javax.swing.JComboBox<?>)Data.get(controls,controlsField);int selected=combo.getSelectedIndex()-1;if(selected<0)return;if(countryCatalog)fields.put(key,String.valueOf(((best.ad)best.n.cY().get(selected)).getPais()));else if(key.equals("playingCountry")){if(selected>=c.a.SR.N().size())throw new IllegalArgumentException("País de atuação inválido");fields.put(key,String.valueOf(((best.Z)c.a.SR.N().get(selected)).jc()));}else fields.put(key,String.valueOf(selected));}
    private static void slider(Object controls,String component,String key,Map<String,String> fields,boolean age){components.aa slider=(components.aa)Data.get(controls,component);int from=slider.getValue(),to=slider.uv();if(from>slider.getMinimum()||to<slider.getMaximum()){fields.put(key+"From",String.valueOf(from));fields.put(key+"To",String.valueOf(age&&to==slider.getMaximum()?100:to));}}
    private static void checkbox(Object controls,String component,String key,Map<String,String> fields){if(((javax.swing.JCheckBox)Data.get(controls,component)).isSelected())fields.put(key,"true");}

    private static void runSearch(JDialog dialog,a.gx panel,JTable table,JButton button,Query query,ah buyer,int mode){Map<String,String> filters;try{filters=captureFilters(panel,mode);}catch(RuntimeException error){JOptionPane.showMessageDialog(dialog,error.getMessage(),"Procura",JOptionPane.WARNING_MESSAGE);return;}
        JLabel count=(JLabel)Data.get(panel,"zh");button.setEnabled(false);count.setText("Buscando na carreira...");query.search(filters,result->{if(!dialog.isDisplayable())return;try{String expectedMode=mode==2?"individual":"general";if(!expectedMode.equals(result.get("mode")))throw new IllegalStateException("O modo de habilidades mudou; reabra a Procura");List<F> players=projectRows(Data.list(result.get("players")),buyer);Data.set(panel,"uK",new ArrayList<F>(players));Data.set(panel,"vp",new ArrayList<F>(players));Method refresh=panel.getClass().getDeclaredMethod(mode==2?"nE":"nF");refresh.setAccessible(true);refresh.invoke(panel);installCareerColumnSorter(table,mode);if(table.getRowCount()>0)table.setRowSelectionInterval(0,0);count.setText(countText(players.size()));}catch(Exception error){count.setText("Busca não atualizada");JOptionPane.showMessageDialog(dialog,error.getMessage(),"Procura",JOptionPane.WARNING_MESSAGE);}finally{button.setEnabled(true);}},error->{if(!dialog.isDisplayable())return;button.setEnabled(true);count.setText("Busca não atualizada");JOptionPane.showMessageDialog(dialog,error.getMessage(),"Procura",JOptionPane.WARNING_MESSAGE);});}
    static void installCareerColumnSorter(JTable table,int mode){if(!(table.getRowSorter() instanceof TableRowSorter))return;int column=mode==2?14:8;if(column>=table.getModel().getColumnCount())return;((TableRowSorter<?>)table.getRowSorter()).setComparator(column,new Comparator<Object>(){@Override public int compare(Object left,Object right){if(!(left instanceof F)||!(right instanceof F))return String.valueOf(left).compareToIgnoreCase(String.valueOf(right));return compareCareerRatings((F)left,(F)right);}});}
    static int compareCareerRatings(F left,F right){int first=Integer.compare(left.getCr1(),right.getCr1());return first!=0?first:Integer.compare(left.getCr2(),right.getCr2());}
    static boolean isSearchSnapshotCurrent(long currentRevision,Object queryRevision){return queryRevision!=null&&String.valueOf(currentRevision).equals(String.valueOf(queryRevision));}
    private static String countText(int total){if(total==0)return "Nenhum jogador encontrado";if(total==1)return "1 jogador encontrado";return total+" jogadores encontrados";}

    static int parseNativePrice(String entered){
        if(entered==null || !entered.trim().matches("[0-9]+"))throw new IllegalArgumentException("Informe um valor válido.");
        long parsed;try{parsed=Math.multiplyExact(Long.parseLong(entered.trim()),1000L);}catch(NumberFormatException|ArithmeticException error){throw new IllegalArgumentException("O valor informado está fora do limite.",error);}
        if(parsed<=0 || parsed>Integer.MAX_VALUE)throw new IllegalArgumentException("O valor informado está fora do limite.");
        return (int)parsed;
    }

    static Object valueAtSelectedRow(JTable table,int column){int selected=table.getSelectedRow();return selected<0?null:table.getValueAt(selected,column);}

    static List<F> projectRows(List<Object> rows,ah buyer){Map<Integer,ah> clubs=new HashMap<Integer,ah>();List<F> players=new ArrayList<F>();for(Object raw:rows){Map<String,Object> row=Data.map(raw);F player=new F();player.az(Data.number(row.get("playerId")));player.setNome(String.valueOf(row.get("name")));Data.set(player,"en",Data.number(row.get("position")));Data.set(player,"em",Data.number(row.get("age")));player.setPais(Data.number(row.get("country")));player.setLado(Data.number(row.get("side")));player.setCr1(Data.number(row.get("skill1")));player.setCr2(Data.number(row.get("skill2")));player.ad(Data.number(row.get("strength")));player.ae(Data.number(row.get("salary")));player.af(Data.number(row.get("value")));player.ag(Data.number(row.get("askingPrice")));player.a(Boolean.valueOf(bool(row,"star")));player.j(Boolean.valueOf(bool(row,"worldClass")));player.c(Boolean.valueOf(bool(row,"forSale")));player.g(Boolean.valueOf(bool(row,"loanAvailable")));player.i(Boolean.valueOf(bool(row,"loaned")));player.aJ(Data.number(row.get("skillGoalkeeping")));player.aM(Data.number(row.get("skillDefence")));player.aN(Data.number(row.get("skillArm")));player.aO(Data.number(row.get("skillFinishing")));player.aI(Data.number(row.get("skillSpeedNative")));player.aK(Data.number(row.get("skillTechnique")));player.aL(Data.number(row.get("skillPassing")));Data.set(player,"fh",Data.number(row.get("goals")));Data.set(player,"fi",Data.number(row.get("appearances")));Data.set(player,"eJ",Data.longNumber(row.get("contractEndMillis")));player.d(Data.longNumber(row.get("injuryUntilMillis")));player.h(Boolean.valueOf(bool(row,"junior")));
            Object clubId=row.get("clubId");if(clubId!=null){int id=Data.number(clubId);ah club;if(id==buyer.lk())club=buyer;else{club=clubs.get(id);if(club==null){club=new ah();club.bX(id);Data.set(club,"dm",String.valueOf(row.get("club")));club.setPais(Data.number(row.get("clubCountry")));club.setDivisao(Data.number(row.get("clubDivision")));club.k(Boolean.FALSE);String managerName=String.valueOf(row.get("managerName"));if(managerName==null||managerName.equals("null")||managerName.trim().isEmpty())managerName="Interino";best.al manager=new best.al(managerName);manager.n(club);manager.setReputacao(Data.number(row.get("managerReputation")));manager.cl(Data.number(row.get("boardConfidence")));manager.cm(Data.number(row.get("fanConfidence")));club.h(manager);clubs.put(id,club);}}player.n(club);if(club!=buyer)club.kc().add(player);}players.add(player);}return players;}
    private static boolean bool(Map<String,Object> row,String key){return Boolean.TRUE.equals(row.get(key));}

    static Map<String,String> offerRequest(int playerId,int sellerClubId,int amount){
        if(playerId<0 || sellerClubId<0 || amount<=0)throw new IllegalArgumentException("Invalid transfer offer details");
        Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","offer");fields.put("playerId",String.valueOf(playerId));fields.put("sellerClubId",String.valueOf(sellerClubId));fields.put("price",String.valueOf(amount));return fields;
    }

    static void submitOffer(Submit submit,int playerId,int sellerClubId,String entered,Runnable success,Runnable failure){
        submit.send(offerRequest(playerId,sellerClubId,parseNativePrice(entered)),success,failure);
    }
}
