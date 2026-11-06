package brasfoot;

/** Unit coverage for native AI negotiations, including unlisted players and counteroffers. */
public final class CareerManagementOfferTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args){
        check(!CareerManagement.nativeListedPurchase(false,1,100),"An unlisted AI player must go through native negotiation, not be rejected");
        check(!CareerManagement.nativeListedPurchase(true,99,100),"An offer below the listed asking price must use native negotiation");
        check(CareerManagement.nativeListedPurchase(true,100,100),"Listed players at the asking price may be bought directly");
        check(CareerManagement.nativeListedPurchase(true,150,100),"Listed players above asking price remain purchasable");
        check(CareerManagement.nativeOfferAccepted(1),"Native acceptance must complete the transfer");
        check(CareerManagement.nativeOfferCountered(6),"Native salary counteroffers must be surfaced for confirmation");
        check(CareerManagement.nativeOfferCountered(7),"Native price counteroffers must be surfaced for confirmation");
        check(!CareerManagement.nativeOfferAccepted(0),"Native refusal must not complete the transfer");
        check(CareerManagement.nativeOfferRefusal(0).toLowerCase().contains("refused"),"Native refusal must return a useful error");
        check(CareerManagement.nativeOfferRefusal(4).toLowerCase().contains("does not want"),"Player refusal must be preserved");
        String withoutRaise=NativeClient.counterofferText(1500000,0);
        check(withoutRaise.contains(best.C.c(1500000)),"Counteroffer transfer price must use native money formatting");
        check(withoutRaise.contains("manter o salário atual"),"Missing salary counteroffer must explicitly keep the current salary");
        String withRaise=NativeClient.counterofferText(1500000,42000);
        check(withRaise.contains(best.C.c(1500000)),"Counteroffer price formatting must be consistent");
        check(withRaise.contains(best.C.c(42000)),"Counteroffer salary must use native money formatting");
        System.out.println("PASS native AI negotiation for unlisted players and counteroffers");
    }
}
