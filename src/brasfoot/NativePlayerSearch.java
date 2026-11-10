package brasfoot;

import best.F;
import best.Z;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Runs the original best.af predicate over the host's complete professional index. */
final class NativePlayerSearch {
    private static final Set<String> FIELDS=new HashSet<String>(Arrays.asList(
        "name","position","side","strengthFrom","strengthTo","ageFrom","ageTo","valueBand",
        "characteristic1","characteristic2","nationality","playingCountry","star","worldClass","loanAvailable","forSale",
        "goalkeepingFrom","goalkeepingTo","defenceFrom","defenceTo","speedFrom","speedTo","finishingFrom","finishingTo",
        "armFrom","armTo","techniqueFrom","techniqueTo","passingFrom","passingTo"));
    private NativePlayerSearch(){ }

    static List<F> find(best.f state,Map<String,String> fields){
        if(fields.size()>FIELDS.size() || !FIELDS.containsAll(fields.keySet()))throw new IllegalArgumentException("Unknown player search filter");
        best.af filter=new best.af();String name=fields.get("name");if(name!=null)filter.setNome(name);
        optionalIndex(fields,"position",0,best.aq.rH.length-1,filter::bv);optionalIndex(fields,"side",0,best.aq.rK.length-1,filter::bw);
        range(fields,"strengthFrom","strengthTo",0,100,filter::bx,(from,to)->{filter.bM(from);filter.bN(to);});
        range(fields,"ageFrom","ageTo",0,100,filter::by,(from,to)->{filter.bO(from);filter.bP(to);});
        optionalIndex(fields,"valueBand",0,6,filter::bz);
        optionalIndex(fields,"characteristic1",0,best.aq.qM.length-1,filter::bA);optionalIndex(fields,"characteristic2",0,best.aq.qM.length-1,filter::bB);
        optionalIndex(fields,"nationality",0,999,filter::bC);
        if(fields.containsKey("playingCountry"))filter.bD(countryIndex(state,integer(fields,"playingCountry",0,999)));
        skill(fields,"goalkeeping",filter,"gol");skill(fields,"defence",filter,"des");skill(fields,"speed",filter,"vel");
        skill(fields,"finishing",filter,"fin");skill(fields,"arm",filter,"arm");skill(fields,"technique",filter,"tec");skill(fields,"passing",filter,"pas");
        flag(fields,"star",filter::E);flag(fields,"worldClass",filter::F);flag(fields,"loanAvailable",filter::G);flag(fields,"forSale",filter::H);
        List<F> result=new ArrayList<F>();for(Object raw:filter.D(false))result.add((F)raw);return result;
    }
    private interface IntConsumer{void accept(int value);}
    private interface RangeConsumer{void accept(int low,int high);}
    private interface FlagConsumer{void accept(boolean value);}
    private static void optionalIndex(Map<String,String> fields,String key,int min,int max,IntConsumer apply){if(fields.containsKey(key))apply.accept(integer(fields,key,min,max));}
    private static int integer(Map<String,String> fields,String key,int min,int max){String raw=fields.get(key);if(raw==null || !raw.matches("-?[0-9]{1,10}"))throw new IllegalArgumentException("Invalid "+key);int value;try{value=Integer.parseInt(raw);}catch(NumberFormatException error){throw new IllegalArgumentException("Invalid "+key);}if(value<min || value>max)throw new IllegalArgumentException("Invalid "+key);return value;}
    private static void range(Map<String,String> fields,String lowKey,String highKey,int min,int max,IntConsumer enable,RangeConsumer setter){
        boolean low=fields.containsKey(lowKey),high=fields.containsKey(highKey);if(low!=high)throw new IllegalArgumentException("Both "+lowKey+" and "+highKey+" are required");if(!low)return;
        int from=integer(fields,lowKey,min,max),to=integer(fields,highKey,min,max);if(from>to)throw new IllegalArgumentException("Invalid "+lowKey+"/"+highKey+" range");
        enable.accept(0);setter.accept(from,to);
    }
    private static void skill(Map<String,String> fields,String key,best.af filter,String nativeKey){String lowKey=key+"From",highKey=key+"To";boolean low=fields.containsKey(lowKey),high=fields.containsKey(highKey);if(low!=high)throw new IllegalArgumentException("Both "+lowKey+" and "+highKey+" are required");if(low){int from=integer(fields,lowKey,0,100),to=integer(fields,highKey,0,100);if(from>to)throw new IllegalArgumentException("Invalid "+lowKey+"/"+highKey+" range");filter.b(nativeKey,from,to);}}
    private static void flag(Map<String,String> fields,String key,FlagConsumer apply){if(fields.containsKey(key)){String raw=fields.get(key);if(!"true".equals(raw)&&!"false".equals(raw))throw new IllegalArgumentException("Invalid "+key);apply.accept(Boolean.parseBoolean(raw));}}
    private static int countryIndex(best.f state,int country){for(int i=0;i<state.N().size();i++)if(((Z)state.N().get(i)).jc()==country)return i;throw new IllegalArgumentException("Unknown playingCountry");}
}
