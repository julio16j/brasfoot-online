package brasfoot;

import best.ah;
import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

/** Native training panel; every change is confirmed by the authoritative host. */
public final class NativeTraining extends JDialog {
    public interface Submit {void send(Map<String,String> fields,Runnable success,Runnable failure);}
    private final a.iQ panel;
    private final Submit submit;
    private final int clubId;
    private final JLabel explanation=new JLabel();
    private int focus;
    private boolean assistant,pending,available=true;

    public NativeTraining(JFrame owner,ah club,Submit submit){
        super(owner,"Treino — "+club.getNome(),true);this.submit=submit;clubId=club.lk();panel=new a.iQ(this,club);
        JCheckBox automatic=(JCheckBox)Data.get(panel,"MY");
        for(java.awt.event.ActionListener listener:automatic.getActionListeners())automatic.removeActionListener(listener);
        automatic.addActionListener(event->{if(!pending && available)send("training-assistant","enabled",String.valueOf(automatic.isSelected()));});
        for(int value=0;value<2;value++){
            final int selected=value;JRadioButton choice=(JRadioButton)Data.get(panel,value==0?"MW":"MX");
            for(java.awt.event.ActionListener listener:choice.getActionListeners())choice.removeActionListener(listener);
            choice.addActionListener(event->{if(!pending && available)send("training-focus","focus",String.valueOf(selected));});
        }
        explanation.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        add(panel,BorderLayout.CENTER);add(explanation,BorderLayout.SOUTH);update(club,true);setResizable(false);pack();setLocationRelativeTo(owner);setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    public void update(ah club,boolean canEdit){
        if(club==null || club.lk()!=clubId){dispose();return;}
        focus=club.lm();assistant=club.ll();available=canEdit;render();
    }
    private void send(String action,String key,String value){
        Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action",action);fields.put(key,value);
        pending=true;render();
        submit.send(fields,()->{pending=false;render();},()->{pending=false;render();});
    }
    private void render(){
        JCheckBox automatic=(JCheckBox)Data.get(panel,"MY");automatic.setSelected(assistant);automatic.setEnabled(available && !pending);
        for(int value=0;value<2;value++){JRadioButton choice=(JRadioButton)Data.get(panel,value==0?"MW":"MX");choice.setSelected(focus==value);choice.setEnabled(available && !pending);}
        explanation.setText(pending?"Aguardando confirmação do host…":!available?"Treino indisponível nesta fase ou após Pronto.":"<html>O foco manual vale com o auxiliar desativado.<br>O ciclo especial de treino segue as regras do jogo.</html>");
    }
}
