package brasfoot;

import best.ah;
import best.al;
import java.io.File;
import java.util.Map;

/** Confirms price edits stay draft-only until explicit host confirmation. */
public final class NativeStadiumPricesTest {
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        GameAdapter game=CareerEngine.create(new File(args[0]),new String[]{"Corinthians","Palmeiras"});al coach=(al)c.a.SR.M().get(0);ah club=coach.fg();int[] confirmed=club.ev().dU();NativeStadium.PriceDraft draft=new NativeStadium.PriceDraft(confirmed,club.ev().dV());draft.edit(0,confirmed[0]==200?199:confirmed[0]+1);draft.suggested=false;
        check(club.ev().dU()[0]==confirmed[0],"Draft edit must not mutate shared projected stadium");Map<String,String> request=draft.confirm();check("stadium-settings".equals(request.get("action"))&&"false".equals(request.get("suggestedPrices")),"Explicit confirmation serializes selected mode");check(Integer.parseInt(request.get("price0"))==draft.prices[0],"Confirmation serializes edited value");
        NativeStadium.PriceDraft snapshot=new NativeStadium.PriceDraft(confirmed,true);snapshot.edit(1,confirmed[1]+1);snapshot.suggested=false;check(snapshot.prices[1]==confirmed[1]+1&&!snapshot.suggested,"Draft survives a remote projection updating the shared model");
        System.out.println("PASS stadium price draft remains isolated from projected values until confirmation");
    }
}
