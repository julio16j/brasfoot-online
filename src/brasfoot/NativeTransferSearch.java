package brasfoot;

import best.F;
import best.ah;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/** Native player search and offer panels with the final transaction sent to the authoritative host. */
public final class NativeTransferSearch {
    public interface Submit { void send(Map<String,String> fields,Runnable success,Runnable failure); }
    private NativeTransferSearch() { }

    public static void open(JFrame owner,ah buyer,int mode,Submit submit){
        if(buyer==null)throw new IllegalStateException("Club is unavailable");
        JDialog dialog=new JDialog(owner,"Procura de jogadores",true);
        a.gx panel=new a.gx(dialog,buyer,false,mode);
        JTable table=(JTable)Data.get(panel,"zj");
        for(java.awt.event.MouseListener listener:table.getMouseListeners())
            if(isNativeOpenListener(listener.getClass().getName()))table.removeMouseListener(listener);
        table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(event.getClickCount()==2)offerSelected(owner,dialog,panel,table,buyer,submit);}});
        AbstractAction offer=new AbstractAction(){@Override public void actionPerformed(ActionEvent event){offerSelected(owner,dialog,panel,table,buyer,submit);}};
        panel.getActionMap().put("st",offer);table.getActionMap().put("st",offer);
        dialog.add(panel);int width=mode==2?840:820,height=mode==2?670:620;
        dialog.setSize(width,height);dialog.setPreferredSize(new Dimension(width,height));dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }

    private static void offerSelected(JFrame owner,JDialog searchDialog,a.gx search,JTable table,ah buyer,Submit submit){
        int selected=table.getSelectedRow();if(selected<0)return;
        Object row=valueAtSelectedRow(table,0);if(!(row instanceof F))return;
        F player=(F)row;ah seller=player.fg();if(seller==null || seller==buyer){JOptionPane.showMessageDialog(searchDialog,"Selecione um jogador de outro clube.","Transferências",JOptionPane.INFORMATION_MESSAGE);return;}
        showOffer(owner,searchDialog,player,buyer,submit);
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

    static int parseNativePrice(String entered){
        if(entered==null || !entered.trim().matches("[0-9]+"))throw new IllegalArgumentException("Informe um valor válido.");
        long parsed;try{parsed=Math.multiplyExact(Long.parseLong(entered.trim()),1000L);}catch(NumberFormatException|ArithmeticException error){throw new IllegalArgumentException("O valor informado está fora do limite.",error);}
        if(parsed<=0 || parsed>Integer.MAX_VALUE)throw new IllegalArgumentException("O valor informado está fora do limite.");
        return (int)parsed;
    }

    static Object valueAtSelectedRow(JTable table,int column){int selected=table.getSelectedRow();return selected<0?null:table.getValueAt(selected,column);}

    static Map<String,String> offerRequest(int playerId,int sellerClubId,int amount){
        if(playerId<0 || sellerClubId<0 || amount<=0)throw new IllegalArgumentException("Invalid transfer offer details");
        Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","offer");fields.put("playerId",String.valueOf(playerId));fields.put("sellerClubId",String.valueOf(sellerClubId));fields.put("price",String.valueOf(amount));return fields;
    }

    static void submitOffer(Submit submit,int playerId,int sellerClubId,String entered,Runnable success,Runnable failure){
        submit.send(offerRequest(playerId,sellerClubId,parseNativePrice(entered)),success,failure);
    }
}
