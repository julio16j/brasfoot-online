package brasfoot;

import best.ah;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

/** Original training-focus screen; the selected focus is committed by the host. */
public final class NativeTraining {
    public interface Submit { void send(Map<String,String> fields,Runnable success); }
    private NativeTraining(){ }
    public static void open(JFrame owner,ah club,Submit submit){
        JDialog dialog=new JDialog(owner,"Treino — "+club.getNome(),true);a.iQ panel=new a.iQ(dialog,club);
        for(int value=0;value<2;value++){final int focus=value;JRadioButton choice=(JRadioButton)Data.get(panel,value==0?"MW":"MX");
            for(java.awt.event.ActionListener listener:choice.getActionListeners())choice.removeActionListener(listener);
            choice.addActionListener(event->{Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","training-focus");fields.put("focus",String.valueOf(focus));choice.setEnabled(false);submit.send(fields,()->choice.setEnabled(true));});
        }
        dialog.add(panel);dialog.setSize(409,252);dialog.setPreferredSize(new Dimension(409,252));dialog.setResizable(false);dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }
}
