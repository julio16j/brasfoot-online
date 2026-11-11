package brasfoot;

import best.ah;
import best.v;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Original Brasfoot stadium screens with construction and price changes sent to the host. */
public final class NativeStadium {
    public interface Submit { void send(Map<String,String> fields,Runnable success); }
    public interface PriceSubmit { void send(Map<String,String> fields,Runnable success,java.util.function.Consumer<Throwable> failure); }
    public static final class Quote {public final int cost;public final Calendar completion;private Quote(int cost,Calendar completion){this.cost=cost;this.completion=completion;}}
    private NativeStadium(){ }

    public static void open(JFrame owner,ah club,int competitionType,Submit submit){openWithPriceSubmit(owner,club,competitionType,(fields,success,failure)->submit.send(fields,success),(fields,success,failure)->submit.send(fields,success));}
    public static JDialog openWithPriceSubmit(JFrame owner,ah club,int competitionType,PriceSubmit construction,PriceSubmit prices){
        if(club==null || club.ev()==null)throw new IllegalStateException("Stadium information is unavailable");JDialog dialog=new JDialog(owner,"Estádio — "+club.getNome(),true);dialog.setUndecorated(true);a.cl panel=new a.cl(dialog,club.ev(),competitionType,club);
        JButton expand=(JButton)Data.get(panel,"BY");for(ActionListener listener:expand.getActionListeners())expand.removeActionListener(listener);expand.addActionListener(event->openConstruction(dialog,panel,club.ev(),club,construction));StadiumPrices draft=new StadiumPrices(dialog,panel,club,prices);draft.bind();dialog.addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent event){WindowState.unregister(dialog);}});panel.setPreferredSize(new Dimension(600,330));dialog.add(panel);dialog.pack();dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);return dialog;
    }

    private static void openConstruction(JDialog parent,a.cl stadiumPanel,v stadium,ah club,PriceSubmit submit){
        JDialog dialog=new JDialog(parent,"Expansão do estádio",true);dialog.setUndecorated(true);a.cq panel=new a.cq(dialog,stadium,club,stadiumPanel);
        JButton start=(JButton)Data.get(panel,"Cv");for(java.awt.event.ActionListener listener:start.getActionListeners())start.removeActionListener(listener);
        start.addActionListener(event->{Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","stadium-expand");String[] keys={"CA","CB","CC","CD"};
            for(int i=0;i<keys.length;i++)fields.put("seats"+i,String.valueOf(((JSpinner)Data.get(panel,keys[i])).getValue()));
            start.setEnabled(false);submit.send(fields,()->{dialog.dispose();stadiumPanel.oB();stadiumPanel.repaint();},failure->{start.setEnabled(true);if(dialog.isShowing())JOptionPane.showMessageDialog(dialog,failure.getMessage(),"Expansão do estádio",JOptionPane.ERROR_MESSAGE);});
        });
        panel.setPreferredSize(new Dimension(438,360));dialog.add(panel);dialog.pack();dialog.setResizable(false);dialog.setLocationRelativeTo(parent);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }
    static void openConstructionPanel(JDialog parent,a.cl stadiumPanel,ah club,Submit submit){openConstruction(parent,stadiumPanel,club.ev(),club,(fields,success,failure)->submit.send(fields,success));}

    /** Ask the original Brasfoot expansion panel to validate and price a host-side request. */
    public static Quote quote(ah club,int[] additions){
        if(additions==null || additions.length!=4)throw new IllegalArgumentException("Four stadium sections are required");
        a.cq nativeQuote=new a.cq(null,club.ev(),club,null);String[] fields={"CA","CB","CC","CD"};int total=0;
        for(int i=0;i<4;i++){JSpinner spinner=(JSpinner)Data.get(nativeQuote,fields[i]);Object maximum=((javax.swing.SpinnerNumberModel)spinner.getModel()).getMaximum();
            if(additions[i]<0 || additions[i]>((Number)maximum).intValue())throw new IllegalArgumentException("Expansion exceeds the native stadium limit");spinner.setValue(additions[i]);total+=additions[i];}
        if(total==0)throw new IllegalArgumentException("Select seats to build");((JButton)Data.get(nativeQuote,"Cu")).doClick();
        int cost=((Number)Data.get(nativeQuote,"Ct")).intValue();Calendar completion=(Calendar)Data.get(nativeQuote,"dI");if(cost<=0 || completion==null)throw new IllegalStateException("Brasfoot could not calculate this construction");
        return new Quote(cost,(Calendar)completion.clone());
    }

    /** Holds manual prices and mode outside the shared projected stadium until confirmed. */
    static final class StadiumPrices {
        final JDialog dialog;final a.cl panel;final ah club;final PriceSubmit submit;final int[] draft=new int[4];final ChangeListener[] changes=new ChangeListener[4];final javax.swing.event.ChangeListener modeChange;boolean suggested,inFlight;
        StadiumPrices(JDialog dialog,a.cl panel,ah club,PriceSubmit submit){this.dialog=dialog;this.panel=panel;this.club=club;this.submit=submit;suggested=club.ev().dV();int[] prices=club.ev().dU();String[] keys={"Cl","Ci","Cj","Ck"};for(int i=0;i<4;i++)draft[i]=prices[i];modeChange=event->suggested=((JCheckBox)event.getSource()).isSelected();}
        void bind(){String[] keys={"Cl","Ci","Cj","Ck"};for(int i=0;i<4;i++){final int index=i;JSpinner spinner=(JSpinner)Data.get(panel,keys[i]);spinner.setValue(draft[i]);spinner.setToolTipText("Desmarque 'Usar sempre os preços sugeridos' para editar este setor.");changes[i]=event->draft[index]=((Number)((JSpinner)event.getSource()).getValue()).intValue();spinner.addChangeListener(changes[i]);}
            JCheckBox mode=(JCheckBox)Data.get(panel,"Cb");mode.setSelected(suggested);mode.setToolTipText("Desmarque para liberar a edição manual dos quatro preços de ingressos.");mode.addChangeListener(modeChange);
            ((JButton)Data.get(panel,"Ca")).setToolTipText("Preencher os valores sugeridos pelo Brasfoot.");
            JButton close=(JButton)Data.get(panel,"BZ");close.setToolTipText("Salvar os preços no servidor e fechar.");for(ActionListener listener:close.getActionListeners())close.removeActionListener(listener);close.addActionListener(event->confirm());WindowState.register(dialog,this);
        }
        void snapshot(ah projected){if(projected==club || projected.ev()==null)return;int[] prices=projected.ev().dU();String[] keys={"Cl","Ci","Cj","Ck"};for(int i=0;i<4;i++){draft[i]=prices[i];((JSpinner)Data.get(panel,keys[i])).setValue(prices[i]);}suggested=projected.ev().dV();((JCheckBox)Data.get(panel,"Cb")).setSelected(suggested);}
        void confirm(){if(inFlight)return;for(int i=0;i<4;i++){Object value=((JSpinner)Data.get(panel,new String[]{"Cl","Ci","Cj","Ck"}[i])).getValue();draft[i]=((Number)value).intValue();}suggested=((JCheckBox)Data.get(panel,"Cb")).isSelected();Map<String,String> fields=SettingsRequest.fields(suggested,draft);
            JButton close=(JButton)Data.get(panel,"BZ");close.setEnabled(false);inFlight=true;
            try{submit.send(fields,()->{club.ev().o(suggested);if(!suggested)club.ev().b(draft.clone());dialog.dispose();},this::failed);}
            catch(RuntimeException failure){failed(failure);}
        }
        void failed(Throwable failure){inFlight=false;((JButton)Data.get(panel,"BZ")).setEnabled(true);if(dialog.isShowing())JOptionPane.showMessageDialog(dialog,failure.getMessage(),"Precos do estadio",JOptionPane.ERROR_MESSAGE);}
    }
    static final class SettingsRequest {
        static Map<String,String> fields(boolean suggested,int[] prices){if(prices==null || prices.length!=4)throw new IllegalArgumentException("Four stadium prices are required");Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","stadium-settings");fields.put("suggestedPrices",String.valueOf(suggested));for(int i=0;i<4;i++)fields.put("price"+i,String.valueOf(prices[i]));return fields;}
    }
    static final class PriceDraft {
        final int[] prices;boolean suggested;
        PriceDraft(int[] confirmed,boolean suggested){if(confirmed==null || confirmed.length!=4)throw new IllegalArgumentException("Four stadium prices are required");prices=confirmed.clone();this.suggested=suggested;}
        void edit(int index,int value){if(index<0 || index>=prices.length)throw new IllegalArgumentException("Invalid stadium sector");prices[index]=value;}
        Map<String,String> confirm(){return SettingsRequest.fields(suggested,prices);}
    }
    static final class WindowState {private static final Map<JDialog,StadiumPrices> OPEN=new java.util.IdentityHashMap<JDialog,StadiumPrices>();static void register(JDialog dialog,StadiumPrices state){OPEN.put(dialog,state);}static void unregister(JDialog dialog){OPEN.remove(dialog);}static void refresh(JDialog dialog,ah projected){StadiumPrices state=OPEN.get(dialog);if(state!=null)state.snapshot(projected);}}
}
