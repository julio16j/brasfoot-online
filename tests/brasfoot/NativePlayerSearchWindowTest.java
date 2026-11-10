package brasfoot;

import best.F;
import best.ah;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Exercises the real Procura and Times Swing windows against the authenticated host API. */
public final class NativePlayerSearchWindowTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private interface Check{boolean ok()throws Exception;}
    private static void await(Check condition,String label)throws Exception{long deadline=System.currentTimeMillis()+45000;while(System.currentTimeMillis()<deadline){if(condition.ok())return;Thread.sleep(50);}throw new AssertionError("Timed out waiting for "+label);}
    private static Object nativePanel(Container root,String className){for(Component child:root.getComponents()){if(child.getClass().getName().equals(className))return child;if(child instanceof Container){Object found=nativePanel((Container)child,className);if(found!=null)return found;}}return null;}
    private static JDialog searchDialog(){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()&&"Procura de jogadores".equals(((JDialog)window).getTitle()))return (JDialog)window;return null;}
    private static int queryFilter(components.O controls,JButton button,JTable table,JLabel count,String name,Consumer<components.O> configure)throws Exception{AtomicInteger expected=new AtomicInteger();SwingUtilities.invokeAndWait(()->{controls.rR();configure.accept(controls);Map<String,String> filters=NativeTransferSearch.captureFilters((a.gx)searchDialog().getContentPane().getComponent(0),c.a.SR.isHabilidadeIndividual()?2:1);if(name.equals("playing country filter")){Object selectedCountry=((javax.swing.JComboBox<?>)Data.get(controls,"Qu")).getSelectedItem();check(selectedCountry instanceof NativeTransferSearch.PlayingCountryOption&&filters.get("playingCountry").equals(String.valueOf(((NativeTransferSearch.PlayingCountryOption)selectedCountry).id)),"Playing-country request preserves the selected host country ID");}expected.set(NativePlayerSearch.find(c.a.SR,filters).size());button.doClick();});await(()->button.isEnabled()&&!count.getText().contains("Buscando"),name);check(table.getRowCount()==expected.get(),name+" count differs from native best.af: "+table.getRowCount()+" vs "+expected.get());return expected.get();}
    private static void closeTimesWithSelection(long playerId,int buyerClubId,AtomicReference<Throwable> failure)throws Exception{SwingUtilities.invokeAndWait(()->new Timer(100,event->{for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()&&"Times".equals(((JDialog)window).getTitle())){try{Object panel=nativePanel(((JDialog)window).getContentPane(),"a.iA");check(panel!=null,"Original Times panel must be shown");F selected=(F)Data.get(panel,"yK");check(selected!=null&&selected.gD()==playerId,"Times must retain the selected search player");JTable roster=(JTable)Data.get(panel,"zj");int selectedRow=roster.getSelectedRow();check(selectedRow>=0&&((F)roster.getValueAt(selectedRow,0)).gD()==playerId,"Times roster row must match the player detail selection");JButton loan=(JButton)Data.get(panel,"MJ");boolean expectedLoan=selected.fg()!=null&&selected.fg().lk()!=buyerClubId&&!selected.gl()&&selected.fz();check(loan.isEnabled()==expectedLoan,"Times loan button must reflect the initially selected player");}catch(Throwable error){failure.set(error);}window.dispose();((Timer)event.getSource()).stop();return;}}).start());}
    private static void openTimesByDoubleClick(JTable table,int row)throws Exception{java.awt.Rectangle cell=table.getCellRect(row,0,true);int x=cell.x+cell.width/2,y=cell.y+cell.height/2;table.setRowSelectionInterval(row,row);table.dispatchEvent(new MouseEvent(table,MouseEvent.MOUSE_CLICKED,System.currentTimeMillis(),0,x,y,2,false,MouseEvent.BUTTON1));}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);PrintStream original=System.out;GameAdapter game;try{System.setOut(new PrintStream(new ByteArrayOutputStream()));game=CareerEngine.createByAssets(root,new String[]{"corinthians_bra"},null);}finally{System.setOut(original);}
        int coach=((best.al)c.a.SR.M().get(0)).lT();ah buyer=((best.al)c.a.SR.M().get(0)).fg();SessionHost host=new SessionHost(game,root.toPath().resolve("sessions-native-search-ui"),coach,false,10000);NativeClient client=null;JFrame owner=null;AtomicReference<Throwable> navigationFailure=new AtomicReference<Throwable>();AtomicInteger submits=new AtomicInteger();
        try{host.startHttp("127.0.0.1",0);java.lang.reflect.Field token=SessionHost.class.getDeclaredField("hostToken");token.setAccessible(true);SessionApi api=new SessionApi("127.0.0.1:"+host.httpPort());api.token((String)token.get(host));final NativeClient[] holder=new NativeClient[1];final JFrame[] ownerHolder=new JFrame[1];SwingUtilities.invokeAndWait(()->{holder[0]=new NativeClient(root.toPath());ownerHolder[0]=new JFrame("Search UI validation");ownerHolder[0].setSize(320,100);ownerHolder[0].setLocationRelativeTo(null);ownerHolder[0].setVisible(true);});client=holder[0];owner=ownerHolder[0];Data.set(client,"api",api);Map<String,Object> minimalState=new LinkedHashMap<String,Object>();minimalState.put("round",new RoundCoordinator(game.coachIds(),game).snapshot());java.util.List<Object> countryCatalog=new java.util.ArrayList<Object>();for(Object raw:c.a.SR.N()){best.Z country=(best.Z)raw;Map<String,Object> countryRow=new LinkedHashMap<String,Object>();countryRow.put("id",country.jc());countryRow.put("label",country.jf());countryCatalog.add(countryRow);}Map<String,Object> sessionOptions=new LinkedHashMap<String,Object>();sessionOptions.put("leagueCountries",countryCatalog);minimalState.put("sessionOptions",sessionOptions);Data.set(client,"latest",minimalState);
            final NativeClient onlineClient=client;Method searchMethod=NativeClient.class.getDeclaredMethod("searchNativePlayers",Map.class,java.util.function.Consumer.class,java.util.function.Consumer.class);searchMethod.setAccessible(true);
            NativeTransferSearch.Query query=(filters,success,failure)->{try{searchMethod.invoke(onlineClient,filters,success,failure);}catch(Exception error){failure.accept(error);}};
            final JFrame searchOwner=owner;SwingUtilities.invokeLater(()->NativeTransferSearch.open(searchOwner,buyer,c.a.SR.isHabilidadeIndividual()?2:1,(fields,success,failure)->submits.incrementAndGet(),query,null,null,null,countryCatalog));await(()->searchDialog()!=null,"native Procura window");
            JDialog dialog=searchDialog();a.gx panel=(a.gx)dialog.getContentPane().getComponent(0);JTable table=(JTable)Data.get(panel,"zj");JButton search=(JButton)Data.get(panel,"yY");JLabel count=(JLabel)Data.get(panel,"zh");int worldPlayers=c.a.SR.O().size(),clubCount=c.a.SR.P().size();
            SwingUtilities.invokeAndWait(search::doClick);await(()->search.isEnabled()&&!count.getText().contains("Buscando"),"unfiltered host query");check(table.getRowCount()==worldPlayers,"Native table must contain the complete host world; rows="+table.getRowCount()+" world="+worldPlayers);check(worldPlayers>10000,"Career fixture must include remote player clubs");check(table.getValueAt(0,0) instanceof F,"Original renderer must receive F objects");check(c.a.SR.O().size()==worldPlayers&&c.a.SR.P().size()==clubCount,"Remote F projection must leave local global indexes untouched");
            Object controls=Data.get(panel,"zi");components.O nativeControls=(components.O)controls;
            queryFilter(nativeControls,search,table,count,"position filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Nq")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"side filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Qx")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"value band filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Qy")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"first characteristic filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Qv")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"second characteristic filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Qw")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"nationality filter",p->((javax.swing.JComboBox<?>)Data.get(p,"BD")).setSelectedIndex(1));
            if(c.a.SR.N().size()>0)queryFilter(nativeControls,search,table,count,"playing country filter",p->((javax.swing.JComboBox<?>)Data.get(p,"Qu")).setSelectedIndex(1));
            queryFilter(nativeControls,search,table,count,"strength range filter",p->{components.aa s=(components.aa)Data.get(p,"QB");s.setValue(40);s.dP(60);});
            queryFilter(nativeControls,search,table,count,"age range filter",p->{components.aa s=(components.aa)Data.get(p,"QC");s.setValue(18);s.dP(24);});
            queryFilter(nativeControls,search,table,count,"star flag filter",p->((javax.swing.JCheckBox)Data.get(p,"Qs")).setSelected(true));
            queryFilter(nativeControls,search,table,count,"world class flag filter",p->((javax.swing.JCheckBox)Data.get(p,"Qt")).setSelected(true));
            queryFilter(nativeControls,search,table,count,"loan flag filter",p->((javax.swing.JCheckBox)Data.get(p,"Qr")).setSelected(true));
            queryFilter(nativeControls,search,table,count,"for sale flag filter",p->((javax.swing.JCheckBox)Data.get(p,"Qq")).setSelected(true));
            int selectedRow=-1;for(int row=0;row<table.getRowCount();row++){F candidate=(F)table.getValueAt(row,0);if(candidate.fg()!=null&&candidate.fg().lk()!=buyer.lk()){selectedRow=row;break;}}check(selectedRow>=0,"Search should contain a player on a remote club");
            F sample=(F)table.getValueAt(selectedRow,0);String prefix=sample.getNome().substring(0,Math.min(3,sample.getNome().length()));final String namePrefix=prefix;queryFilter(nativeControls,search,table,count,"name filter",p->((JTextField)Data.get(p,"uf")).setText(namePrefix));int expected=table.getRowCount();
            int careerColumn=c.a.SR.isHabilidadeIndividual()?14:8;check(table.getRowSorter()!=null,"Native player results retain the table sorter");table.getRowSorter().toggleSortOrder(careerColumn);F previousCareer=null;for(int row=0;row<table.getRowCount();row++){F current=(F)table.getValueAt(row,0);if(previousCareer!=null)check(NativeTransferSearch.compareCareerRatings(previousCareer,current)<=0,"Career header sorting follows the native visible rating codes");previousCareer=current;}selectedRow=-1;for(int row=0;row<table.getRowCount();row++){F candidate=(F)table.getValueAt(row,0);if(candidate.fg()!=null&&candidate.fg().lk()!=buyer.lk()){selectedRow=row;break;}}check(selectedRow>=0,"Filtered results must retain a remote club identity after sorting");F selected=(F)table.getValueAt(selectedRow,0);table.setRowSelectionInterval(selectedRow,selectedRow);check(NativeTransferSearch.valueAtSelectedRow(table,0)==selected,"Selected view row resolves to the sorted model player");long selectedId=selected.gD();int chosenRow=selectedRow;closeTimesWithSelection(selectedId,buyer.lk(),navigationFailure);
            SwingUtilities.invokeAndWait(()->{try{openTimesByDoubleClick(table,chosenRow);}catch(Exception error){throw new RuntimeException(error);}});if(navigationFailure.get()!=null)throw new AssertionError(navigationFailure.get());
            closeTimesWithSelection(selectedId,buyer.lk(),navigationFailure);SwingUtilities.invokeAndWait(()->table.getActionMap().get("st").actionPerformed(new ActionEvent(table,ActionEvent.ACTION_PERFORMED,"enter")));if(navigationFailure.get()!=null)throw new AssertionError(navigationFailure.get());
            check(submits.get()==0,"Search navigation must not submit a transfer");dialog.dispose();System.out.println("PASS real Procura UI: total="+worldPlayers+", native name filter="+expected+", double-click/Enter opened selected Times, commands="+submits.get());
        }finally{if(client!=null){try{((java.util.concurrent.ExecutorService)Data.get(client,"network")).shutdownNow();}catch(Exception ignored){}}if(host!=null){host.stopHttp();host.close();}for(Window window:Window.getWindows())SwingUtilities.invokeLater(window::dispose);}
    }
}
