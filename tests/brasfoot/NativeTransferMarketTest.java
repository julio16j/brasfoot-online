package brasfoot;

import java.util.Map;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;

/** Unit checks for host-only purchase actions in the native Times window. */
public final class NativeTransferMarketTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        AtomicInteger localCalls=new AtomicInteger(),hostCalls=new AtomicInteger();JButton buy=new JButton("Comprar");
        buy.addActionListener(event->localCalls.incrementAndGet());
        NativeTransferMarket.replaceLocalHandler(buy,event->hostCalls.incrementAndGet());buy.doClick();
        check(localCalls.get()==0,"The native client-side purchase handler must be removed");
        check(hostCalls.get()==1,"Purchase confirmation must invoke the host handler once");

        Map<String,String> request=NativeTransferMarket.purchaseRequest(19,4,825000);
        check("offer".equals(request.get("action")),"A purchase must use the authoritative offer endpoint");
        check("19".equals(request.get("playerId")),"Purchase request must identify the selected player");
        check("4".equals(request.get("sellerClubId")),"Purchase request must identify the selling club");
        check("825000".equals(request.get("price")),"Purchase request must debit the native asking price");
        sun.misc.Unsafe unsafe;Field singleton=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");singleton.setAccessible(true);unsafe=(sun.misc.Unsafe)singleton.get(null);a.iA panel=(a.iA)unsafe.allocateInstance(a.iA.class);best.ah buyer=new best.ah(),seller=new best.ah();Data.set(panel,"uk",seller);Data.set(panel,"ul",buyer);check(NativeTransferMarket.academyClub(panel)==seller,"Academy access must use the displayed/visited club, not the buyer finance field");
        System.out.println("PASS native Times purchase uses only the host transaction");
    }
}
