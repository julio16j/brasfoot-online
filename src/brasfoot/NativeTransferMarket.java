package brasfoot;

import best.F;
import best.ah;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/** Original Brasfoot "Times" market panel with purchases committed by the host. */
public final class NativeTransferMarket {
    private NativeTransferMarket() { }

    public static void open(JFrame owner,ah buyer,NativeTransferSearch.Submit submit,NativeYouth.Promotion youthPromotion){
        if(buyer==null)throw new IllegalStateException("Club is unavailable");
        JDialog dialog=new JDialog(owner,"Times",true);a.iA panel=new a.iA(dialog,buyer,buyer,false);
        show(owner,dialog,panel,buyer,submit,youthPromotion);
    }

    /** Opens the visited seller's native Times window with the searched player selected. */
    public static void openSelected(JFrame owner,JDialog searchDialog,ah buyer,F player,NativeTransferSearch.Submit submit,NativeYouth.Promotion youthPromotion){
        if(buyer==null || player==null || player.fg()==null || player.fg()==buyer)return;
        ah seller=player.fg();
        JDialog dialog=new JDialog(searchDialog,"Times",true);a.iA panel=new a.iA(dialog,seller,buyer,false);panel.s(player);
        show(owner,dialog,panel,buyer,submit,youthPromotion);
    }

    private static void show(JFrame owner,JDialog dialog,a.iA panel,ah buyer,NativeTransferSearch.Submit submit,NativeYouth.Promotion youthPromotion){
        JButton buy=(JButton)Data.get(panel,"FN"),offer=(JButton)Data.get(panel,"MK"),loan=(JButton)Data.get(panel,"MJ");
        replaceLocalHandler(buy,(event)->purchase(owner,dialog,panel,buyer,buy,submit));
        replaceLocalHandler(offer,(event)->makeOffer(owner,dialog,panel,buyer,submit));
        replaceLocalHandler(loan,(event)->requestLoan(owner,dialog,panel,buyer,loan,submit));loan.setEnabled(true);loan.setToolTipText("Solicitar empréstimo do jogador selecionado");
        replaceLocalHandler((JButton)Data.get(panel,"HW"),(event)->{ah visited=academyClub(panel);if(visited!=null)NativeYouth.open(dialog,buyer,visited,visited==buyer,youthPromotion);});
        Dimension size=marketSize();dialog.add(panel);dialog.setSize(size);dialog.setPreferredSize(size);dialog.setResizable(false);
        dialog.setLocationRelativeTo(owner);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);dialog.setVisible(true);
    }

    private static Dimension marketSize(){
        java.awt.Rectangle bounds=java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        return new Dimension(bounds.width>=1366?1200:1024,bounds.height>=768?740:718);
    }

    private static void purchase(JFrame owner,JDialog dialog,a.iA panel,ah buyer,JButton button,NativeTransferSearch.Submit submit){
        F player=panel.sE();if(player==null)return;ah seller=player.fg();
        if(seller==null || seller==buyer){JOptionPane.showMessageDialog(dialog,"Selecione um jogador de outro clube.","Compra",JOptionPane.INFORMATION_MESSAGE);return;}
        int amount=Math.max(1,player.fl()>0?player.fl():player.fk());
        Map<String,String> request=NativeTransferSearch.offerRequest(player.gD(),seller.lk(),amount);
        button.setEnabled(false);submit.send(request,dialog::dispose,()->button.setEnabled(true));
    }

    private static void makeOffer(JFrame owner,JDialog dialog,a.iA panel,ah buyer,NativeTransferSearch.Submit submit){
        F player=panel.sE();if(player==null)return;
        if(player.fg()==null || player.fg()==buyer){JOptionPane.showMessageDialog(dialog,"Selecione um jogador de outro clube.","Proposta",JOptionPane.INFORMATION_MESSAGE);return;}
        NativeTransferSearch.showOffer(owner,dialog,player,buyer,submit);
    }

    private static void requestLoan(JFrame owner,JDialog dialog,a.iA panel,ah buyer,JButton button,NativeTransferSearch.Submit submit){
        F player=panel.sE();if(player==null)return;ah seller=player.fg();
        if(seller==null || seller==buyer){JOptionPane.showMessageDialog(dialog,"Selecione um jogador de outro clube.","Empréstimo",JOptionPane.INFORMATION_MESSAGE);return;}
        if(player.gl() || !player.fz()){JOptionPane.showMessageDialog(dialog,"Este jogador não está disponível para empréstimo.","Empréstimo",JOptionPane.INFORMATION_MESSAGE);return;}
        int answer=JOptionPane.showConfirmDialog(dialog,"Solicitar " + player.getNome() + " por empréstimo?","Empréstimo",JOptionPane.YES_NO_OPTION);
        if(answer!=JOptionPane.YES_OPTION)return;
        Map<String,String> request=new java.util.LinkedHashMap<String,String>();request.put("action","loan-request");request.put("playerId",String.valueOf(player.gD()));request.put("sellerClubId",String.valueOf(seller.lk()));
        button.setEnabled(false);submit.send(request,()->{dialog.dispose();JOptionPane.showMessageDialog(owner,"Solicitação de empréstimo enviada.","Empréstimo",JOptionPane.INFORMATION_MESSAGE);},()->button.setEnabled(true));
    }

    static void replaceLocalHandler(AbstractButton button,ActionListener hostAction){
        for(ActionListener listener:button.getActionListeners())button.removeActionListener(listener);
        if(hostAction!=null)button.addActionListener(hostAction);
    }

    static Map<String,String> purchaseRequest(int playerId,int sellerClubId,int askingPrice){
        return NativeTransferSearch.offerRequest(playerId,sellerClubId,askingPrice);
    }

    static ah academyClub(a.iA panel){return (ah)Data.get(panel,"uk");}
}
