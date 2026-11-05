package brasfoot;

import best.ah;
import best.v;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;

/** Original Brasfoot stadium screens with construction and price changes sent to the host. */
public final class NativeStadium {
    public interface Submit { void send(Map<String,String> fields,Runnable success); }
    public static final class Quote {public final int cost;public final Calendar completion;private Quote(int cost,Calendar completion){this.cost=cost;this.completion=completion;}}
    private NativeStadium(){ }

    public static void open(JFrame owner,ah club,int competitionType,Submit submit){
        if(club==null || club.ev()==null)throw new IllegalStateException("Stadium information is unavailable");
        JDialog dialog=new JDialog(owner,"Estádio — "+club.getNome(),true);
        a.cl panel=new a.cl(dialog,club.ev(),competitionType,club);
        JButton expand=(JButton)Data.get(panel,"BY");for(java.awt.event.ActionListener listener:expand.getActionListeners())expand.removeActionListener(listener);
        expand.addActionListener(event->openConstruction(dialog,panel,club.ev(),club,submit));
        JButton prices=(JButton)Data.get(panel,"Ca");for(java.awt.event.ActionListener listener:prices.getActionListeners())prices.removeActionListener(listener);
        prices.addActionListener(event->{Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","stadium-settings");fields.put("suggestedPrices",String.valueOf(club.ev().dV()));
            String[] keys={"Cl","Ci","Cj","Ck"};for(int i=0;i<keys.length;i++)fields.put("price"+i,String.valueOf(((JSpinner)Data.get(panel,keys[i])).getValue()));
            prices.setEnabled(false);submit.send(fields,()->{prices.setEnabled(true);});
        });
        dialog.add(panel);dialog.setSize(598,328);dialog.setPreferredSize(new Dimension(598,328));dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }

    private static void openConstruction(JDialog parent,a.cl stadiumPanel,v stadium,ah club,Submit submit){
        JDialog dialog=new JDialog(parent,"Expansão do estádio",true);a.cq panel=new a.cq(dialog,stadium,club,stadiumPanel);
        JButton start=(JButton)Data.get(panel,"Cv");for(java.awt.event.ActionListener listener:start.getActionListeners())start.removeActionListener(listener);
        start.addActionListener(event->{Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","stadium-expand");String[] keys={"CA","CB","CC","CD"};
            for(int i=0;i<keys.length;i++)fields.put("seats"+i,String.valueOf(((JSpinner)Data.get(panel,keys[i])).getValue()));
            start.setEnabled(false);submit.send(fields,()->{dialog.dispose();stadiumPanel.oB();stadiumPanel.repaint();});
        });
        dialog.add(panel);dialog.setSize(438,360);dialog.setPreferredSize(new Dimension(438,360));dialog.setResizable(false);dialog.setLocationRelativeTo(parent);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }

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
}
