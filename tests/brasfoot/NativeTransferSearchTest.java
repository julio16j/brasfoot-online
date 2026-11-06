package brasfoot;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Unit coverage for native offer confirmation and removal of the local-only double-click action. */
public final class NativeTransferSearchTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static void rejects(Runnable action,String message){try{action.run();throw new AssertionError(message);}catch(IllegalArgumentException expected){}}

    public static void main(String[] args){
        check(NativeTransferSearch.isNativeOpenListener("a.gb"),"Native double-click listener must be recognized");
        check(NativeTransferSearch.isNativeOpenListener("a.gb$1"),"Nested native listener must be recognized");
        check(!NativeTransferSearch.isNativeOpenListener("a.gy"),"Search button listener must remain untouched");
        check(!NativeTransferSearch.isNativeOpenListener("brasfoot.TransferListener"),"Online listeners must remain untouched");

        check(NativeTransferSearch.parseNativePrice("125")==125000,"Native prices are entered in thousands");
        check(NativeTransferSearch.parseNativePrice(" 2 ")==2000,"Whitespace around native price should be accepted");
        rejects(()->NativeTransferSearch.parseNativePrice(""),"Empty price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("-1"),"Negative price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("0"),"Zero price must be rejected");
        rejects(()->NativeTransferSearch.parseNativePrice("2147484"),"Overflowing price must be rejected");

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
