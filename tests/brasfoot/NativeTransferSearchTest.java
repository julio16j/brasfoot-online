package brasfoot;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import best.F;

/** Unit coverage for native offer confirmation and removal of the local-only double-click action. */
public final class NativeTransferSearchTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static void rejects(Runnable action,String message){try{action.run();throw new AssertionError(message);}catch(IllegalArgumentException expected){}}

    public static void main(String[] args){
        check(NativeTransferSearch.isNativeOpenListener("a.gb"),"Native double-click listener must be recognized");
        check(NativeTransferSearch.isNativeOpenListener("a.gb$1"),"Nested native listener must be recognized");
        check(NativeTransferSearch.isNativeOpenListener("a.gB"),"Native listener's actual mixed-case class name must be recognized");
        check(NativeTransferSearch.isNativeOpenListener("a.gB$1"),"Nested listener's actual mixed-case class name must be recognized");
        check(!NativeTransferSearch.isNativeOpenListener("a.gy"),"Search button listener must remain untouched");
        check(!NativeTransferSearch.isNativeOpenListener("brasfoot.TransferListener"),"Online listeners must remain untouched");
        check(NativeTransferSearch.isSearchPending(Boolean.TRUE)&&!NativeTransferSearch.isSearchPending(Boolean.FALSE),"Pending searches block actions against the previous result");Map<String,String> submittedFilters=new java.util.LinkedHashMap<String,String>();submittedFilters.put("name","Ana");check(NativeTransferSearch.sameSearchFilters(submittedFilters,new java.util.LinkedHashMap<String,String>(submittedFilters)),"Unchanged filters may accept the pending response");Map<String,String> editedFilters=new java.util.LinkedHashMap<String,String>(submittedFilters);editedFilters.put("name","Bia");check(!NativeTransferSearch.sameSearchFilters(submittedFilters,editedFilters),"Edited or reset filters invalidate the pending response");
        check("nE".equals(NativeTransferSearch.refreshMethodName(1)),"General search refresh must use the general native table builder");check("nF".equals(NativeTransferSearch.refreshMethodName(2)),"Individual-skill search refresh must use the individual native table builder");rejects(()->NativeTransferSearch.refreshMethodName(0),"Invalid mode cannot select a native table builder");
        check(NativeTransferSearch.sameSearchPageSnapshot(-1,null,null,0,25,"general","7",1234L),"First search page establishes its snapshot metadata");check(NativeTransferSearch.sameSearchPageSnapshot(25,"general","7",1234L,25,"general","7",1234L),"Continuation page with matching metadata is accepted");check(!NativeTransferSearch.sameSearchPageSnapshot(25,"general","7",1234L,25,"general","7",1235L),"Continuation page with a changed career date is rejected");check(!NativeTransferSearch.sameSearchPageSnapshot(25,"general","7",1234L,26,"general","7",1234L),"Continuation page with a changed total is rejected");
        NativeTransferSearch.PlayingCountryOption england=new NativeTransferSearch.PlayingCountryOption(1,"Inglaterra");check("1".equals(NativeTransferSearch.playingCountryId(england,1)),"Playing-country filters send the selected host country ID");check(NativeTransferSearch.playingCountryId("Qualquer",0)==null,"The unrestricted playing-country option adds no filter");rejects(()->NativeTransferSearch.playingCountryId("Inglaterra",1),"A displayed label without stable country identity must not be submitted");

        AtomicInteger nativeEnter=new AtomicInteger(),onlineEnter=new AtomicInteger();JButton searchButton=new JButton();
        searchButton.getActionMap().put("st",new AbstractAction(){@Override public void actionPerformed(java.awt.event.ActionEvent event){nativeEnter.incrementAndGet();}});
        Action onlineSelection=new AbstractAction(){@Override public void actionPerformed(java.awt.event.ActionEvent event){onlineEnter.incrementAndGet();}};
        NativeTransferSearch.replaceEnterAction(searchButton,onlineSelection);
        searchButton.getActionMap().get("st").actionPerformed(new java.awt.event.ActionEvent(searchButton,java.awt.event.ActionEvent.ACTION_PERFORMED,"enter"));
        check(nativeEnter.get()==0 && onlineEnter.get()==1,"Enter on Procurar must use the online selected-player route instead of local Times");

        check(NativeTransferSearch.parseNativePrice("125")==125000,"Native prices are entered in thousands");
        check(NativeTransferSearch.parseNativePrice(" 2 ")==2000,"Whitespace around native price should be accepted");
        rejects(()->NativeTransferSearch.parseNativePrice(""),"Empty price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("-1"),"Negative price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("0"),"Zero price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("2147484"),"Overflowing price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("18446744073709552"),"Long multiplication wraparound must be rejected");

        JTable sorted=new JTable(new DefaultTableModel(new Object[][]{{"A"},{"B"},{"C"}},new Object[]{"Player"}));sorted.setAutoCreateRowSorter(true);sorted.getRowSorter().toggleSortOrder(0);sorted.getRowSorter().toggleSortOrder(0);sorted.setRowSelectionInterval(0,0);
        check("C".equals(NativeTransferSearch.valueAtSelectedRow(sorted,0)),"Selected visual row must be converted by JTable exactly once after sorting");

        F careerA=new F(),careerB=new F();careerA.setCr1(1);careerA.setCr2(2);careerB.setCr1(3);careerB.setCr2(4);check(NativeTransferSearch.compareCareerRatings(careerA,careerB)<0,"Career characteristic ordering follows the native rating-code order");DefaultTableModel careerModel=new DefaultTableModel(2,9);careerModel.setColumnIdentifiers(new Object[]{"P","","Nome","Time","L","F","Salário","Valor","Car."});careerModel.setValueAt(careerB,0,8);careerModel.setValueAt(careerA,1,8);JTable careerTable=new JTable(careerModel);careerTable.setAutoCreateRowSorter(true);NativeTransferSearch.installCareerColumnSorter(careerTable,1);careerTable.getRowSorter().toggleSortOrder(8);F firstCareer=(F)careerTable.getValueAt(0,8);check(firstCareer==careerA,"Sorted career column must retain and order the lower native rating identity first");check(NativeTransferSearch.compareCareerRatings(firstCareer,(F)careerTable.getValueAt(1,8))<=0,"Sorted career rating codes must be ascending");careerTable.setRowSelectionInterval(0,0);check(NativeTransferSearch.valueAtSelectedRow(careerTable,8)==firstCareer,"Selection after career-column sorting must resolve to the displayed player identity");
        check(NativeTransferSearch.isSearchSnapshotCurrent(9,9L),"Search results may install when the career revision is current");check(!NativeTransferSearch.isSearchSnapshotCurrent(10,9L),"Queued search results from an older career revision must be discarded");check(!NativeTransferSearch.isSearchSnapshotCurrent(9,null),"Search results without a revision must be discarded");

        Map<String,String> request=NativeTransferSearch.offerRequest(41,7,125000);
        check("offer".equals(request.get("action")),"Transfer must use the host offer operation");
        check("41".equals(request.get("playerId")),"Offer must contain the selected player id");
        check("7".equals(request.get("sellerClubId")),"Offer must contain the seller club id");
        check("125000".equals(request.get("price")),"Offer must contain the converted native price");
        rejects(()->NativeTransferSearch.offerRequest(-1,7,1),"Invalid player id must be rejected");
        rejects(()->NativeTransferSearch.offerRequest(41,-1,1),"Invalid seller id must be rejected");
        rejects(()->NativeTransferSearch.offerRequest(41,7,0),"Invalid offer amount must be rejected");

        AtomicInteger calls=new AtomicInteger();final Map<?,?>[] submitted=new Map<?,?>[1];final Runnable[] callbacks=new Runnable[2];
        NativeTransferSearch.submitOffer((fields,success,failure)->{calls.incrementAndGet();submitted[0]=fields;callbacks[0]=success;callbacks[1]=failure;},41,7,"125",()->{},()->{});
        check(calls.get()==1,"Offer confirmation must submit exactly one host command");
        check("125000".equals(submitted[0].get("price")),"Submitted host command must use normalized price");
        check(callbacks[0]!=null && callbacks[1]!=null,"Offer submission must preserve success and failure callbacks");
        System.out.println("PASS native transfer request, price validation, and local listener routing");
    }
}
