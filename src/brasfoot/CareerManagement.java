package brasfoot;

import best.F;
import best.ah;
import best.al;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/** Explicit online commands that apply the original career transactions on the host. */
public final class CareerManagement {
    private final GameAdapter game;
    private final best.f state;
    private final Map<Long,Map<String,Object>> offers=new LinkedHashMap<Long,Map<String,Object>>();
    private final Map<Integer,List<Integer>> jobOffers=new HashMap<Integer,List<Integer>>();
    private long jobDate=-1,nextOffer=1;
    private int nextPlayer;

    public CareerManagement(GameAdapter game){this.game=game;this.state=c.a.SR;ensureIds();}
    private void check(){if(c.a.SR!=state)throw new IllegalStateException("Career globals changed");if(!game.isCareer())throw new IllegalStateException("Management requires a career session");}
    private al coach(int id){check();if(!game.coachIds().contains(id))throw new IllegalArgumentException("Unknown human coach");for(Object raw:state.M()){al coach=(al)raw;if(coach.lT()==id)return coach;}throw new IllegalArgumentException("Unknown human coach");}
    private ah own(int id){ah club=coach(id).fg();if(club==null)throw new IllegalStateException("Choose a club before managing players");return club;}
    private List<F> allPlayers(){Set<F> seen=Collections.newSetFromMap(new IdentityHashMap<F,Boolean>());List<F> result=new ArrayList<F>();
        for(Object value:state.O())if(seen.add((F)value))result.add((F)value);
        for(Object raw:state.P()){ah club=(ah)raw;for(Object value:club.kc())if(seen.add((F)value))result.add((F)value);for(Object value:club.ky())if(seen.add((F)value))result.add((F)value);}
        return result;
    }
    /** The native ID field is saved with the player, so roster edits never change a command target. */
    synchronized void ensureIds(){Set<Integer> used=new HashSet<Integer>();List<F> players=allPlayers();for(F player:players)nextPlayer=Math.max(nextPlayer,player.gD()+1);
        for(F player:players){if(player.gD()<0 || !used.add(player.gD())){player.az(nextPlayer++);used.add(player.gD());}}
    }
    private F player(int id){ensureIds();for(F player:allPlayers())if(player.gD()==id)return player;throw new IllegalArgumentException("Player no longer exists");}
    private F ownedPlayer(int coach,int id,boolean youth){ah club=own(coach);F player=player(id);if(player.fg()!=club || !(youth?club.ky():club.kc()).contains(player))throw new IllegalArgumentException("Player does not belong to your squad");return player;}
    private int humanOwner(ah club){if(club==null || !Boolean.TRUE.equals(club.jZ()))return -1;for(Object raw:state.M()){al coach=(al)raw;if(coach.fg()==club)return coach.lT();}return -1;}
    private long date(){return state.bb().getTimeInMillis();}
    private static String required(Map<String,String> data,String key){String value=data.get(key);if(value==null || value.trim().isEmpty())throw new IllegalArgumentException("Missing "+key);return value;}
    private static int number(Map<String,String> data,String key){try{return Integer.parseInt(required(data,key));}catch(NumberFormatException error){throw new IllegalArgumentException("Invalid "+key);}}
    private static boolean bool(Map<String,String> data,String key){String value=required(data,key);if(!value.equals("true") && !value.equals("false"))throw new IllegalArgumentException("Invalid "+key);return Boolean.parseBoolean(value);}
    private static int price(Map<String,String> data){int price=number(data,"price");if(price<=0)throw new IllegalArgumentException("Price must be positive");return price;}
    private Map<String,Object> offer(long id){Map<String,Object> value=offers.get(id);if(value==null)throw new IllegalArgumentException("Unknown offer");return value;}
    private boolean pending(Map<String,Object> offer){return "PENDING".equals(offer.get("status")) && Data.longNumber(offer.get("dateMillis"))==date();}

    /** The caller checks every participant's Ready flag before entering execute(). */
    public Set<Integer> affectedCoaches(int coachId,Map<String,String> data){coach(coachId);Set<Integer> affected=new LinkedHashSet<Integer>();affected.add(coachId);String action=required(data,"action");
        if(action.equals("stadium-settings")){for(Object raw:state.M()){al other=(al)raw;if(other.lT()!=coachId && other.fg()==own(coachId))affected.add(other.lT());}}
        if(action.equals("offer") || action.equals("loan-request")){int owner=humanOwner(player(number(data,"playerId")).fg());if(owner>=0)affected.add(owner);}
        if(action.equals("respond")){Map<String,Object> offer=offer(Long.parseLong(required(data,"offerId")));affected.add(Data.number(offer.get("buyerCoachId")));}
        if(action.equals("loan-return")){F player=player(number(data,"playerId"));for(Object raw:state.bt())if(isLoanFor(raw,player)){ah source=(ah)invokeLoan(raw,"tP");int other=humanOwner(source==own(coachId)?player.fg():source);if(other>=0)for(Object value:state.M())if(((al)value).fg()==(source==own(coachId)?player.fg():source))affected.add(((al)value).lT());break;}}
        return affected;
    }
    public Map<String,Object> execute(int coachId,Map<String,String> data){coach(coachId);ensureIds();String action=required(data,"action");Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("action",action);
        if(action.equals("loan-request")){
            F player=player(number(data,"playerId"));ah seller=player.fg(),buyer=own(coachId);int sellerId=number(data,"sellerClubId");
            if(seller==null || seller==buyer || seller.lk()!=sellerId || !seller.kc().contains(player) || player.gl() || !player.fz())throw new IllegalStateException("Player is not available for this loan request");
            int owner=humanOwner(seller);
            if(owner<0){int decision=best.l.a(player,buyer);if(decision!=1)throw new IllegalStateException(nativeLoanRefusal(decision));Object contract=null;for(Object raw:state.bt())if(isLoanFor(raw,player)){contract=raw;break;}if(contract==null || !player.gl() || player.fg()!=buyer)throw new IllegalStateException("Native loan transaction did not create its contract");result.put("status","LOANED");result.put("loan",loanRow(contract));}
            else {for(Map<String,Object> current:offers.values())if(pending(current) && "LOAN".equals(current.get("kind")) && Data.number(current.get("playerId"))==player.gD())throw new IllegalStateException("A loan request for this player is already pending");Map<String,Object> request=new LinkedHashMap<String,Object>();long id=nextOffer++;request.put("offerId",id);request.put("kind","LOAN");request.put("playerId",player.gD());request.put("player",player.getNome());request.put("sellerClubId",seller.lk());request.put("sellerCoachId",owner);request.put("buyerClubId",buyer.lk());request.put("buyerCoachId",coachId);request.put("buyerClub",buyer.getNome());request.put("price",0);request.put("dateMillis",date());request.put("status","PENDING");offers.put(id,request);result.put("offerId",id);result.put("status","PENDING");}
        } else if(action.equals("offer")){
            ah buyer=own(coachId);F player=player(number(data,"playerId"));ah seller=player.fg();int amount=price(data);
            if(seller==null || seller==buyer || seller.lk()!=number(data,"sellerClubId") || !seller.kc().contains(player))throw new IllegalArgumentException("Invalid seller or player");
            validateTransfer(player,seller,buyer,amount);
            int owner=humanOwner(seller);
            if(owner<0){
                boolean acceptCounter=data.containsKey("acceptCounter") && bool(data,"acceptCounter");
                if(!acceptCounter && nativeListedPurchase(player.ft(),amount,asking(player))){
                    transfer(player,seller,buyer,asking(player),false);result.put("status","COMPLETED");
                } else {
                    int originalOffer=acceptCounter?number(data,"offerAmount"):amount;
                    int decision=best.l.a(player,buyer,originalOffer);
                    if(nativeOfferAccepted(decision)){
                        if(acceptCounter)throw new IllegalStateException("The native counteroffer changed; submit a new offer");
                        transfer(player,seller,buyer,originalOffer,false);result.put("status","COMPLETED");
                    } else if(nativeOfferCountered(decision)){
                        int counterPrice=decision==7?best.l.cN():originalOffer,counterSalary=best.l.cM();
                        if(acceptCounter){
                            if(amount!=counterPrice || !data.containsKey("counterSalary") || number(data,"counterSalary")!=counterSalary)throw new IllegalStateException("The native counteroffer changed; submit a new offer");
                            validateTransfer(player,seller,buyer,counterPrice);if(counterSalary>0)player.ae(counterSalary);
                            transfer(player,seller,buyer,counterPrice,false);result.put("status","COMPLETED");
                        } else {
                            result.put("status","COUNTEROFFER");result.put("counterPrice",counterPrice);result.put("counterSalary",counterSalary);result.put("offerAmount",originalOffer);
                        }
                    } else throw new IllegalStateException(nativeOfferRefusal(decision));
                }
            }
            else {
                for(Map<String,Object> current:offers.values())if(pending(current) && Data.number(current.get("buyerCoachId"))==coachId && Data.number(current.get("playerId"))==player.gD())throw new IllegalStateException("An offer for this player is already pending");
                if(offers.size()>=400){Iterator<Map.Entry<Long,Map<String,Object>>> iterator=offers.entrySet().iterator();while(iterator.hasNext()){Map.Entry<Long,Map<String,Object>> entry=iterator.next();if(!pending(entry.getValue()))iterator.remove();}if(offers.size()>=400)throw new IllegalStateException("Too many pending offers");}
                Map<String,Object> offer=new LinkedHashMap<String,Object>();long id=nextOffer++;
                offer.put("offerId",id);offer.put("playerId",player.gD());offer.put("player",player.getNome());offer.put("sellerClubId",seller.lk());offer.put("sellerCoachId",owner);offer.put("buyerClubId",buyer.lk());offer.put("buyerCoachId",coachId);offer.put("buyerClub",buyer.getNome());offer.put("price",amount);offer.put("dateMillis",date());offer.put("status","PENDING");offers.put(id,offer);
                result.put("offerId",id);result.put("status","PENDING");
            }
        } else if(action.equals("respond")){
            Map<String,Object> offer=offer(Long.parseLong(required(data,"offerId")));
            if(Data.number(offer.get("sellerCoachId"))!=coachId)throw new IllegalArgumentException("Only the seller may respond");
            if(!pending(offer))throw new IllegalStateException("Offer already resolved or expired");boolean accept=bool(data,"accept");
            if(accept){ah seller=own(coachId),buyer=own(Data.number(offer.get("buyerCoachId")));F player=player(Data.number(offer.get("playerId")));int amount=Data.number(offer.get("price"));
                if(seller.lk()!=Data.number(offer.get("sellerClubId")) || buyer.lk()!=Data.number(offer.get("buyerClubId")))throw new IllegalStateException("Offer clubs changed");
                if("LOAN".equals(offer.get("kind"))){if(!seller.kc().contains(player) || player.gl() || !player.fz())throw new IllegalStateException("Loan request is no longer valid");int decision=best.l.a(player,buyer);if(decision!=1)throw new IllegalStateException(nativeLoanRefusal(decision));}
                else {validateTransfer(player,seller,buyer,amount);transfer(player,seller,buyer,amount,false);}
            }
            offer.put("status",accept?"ACCEPTED":"REJECTED");result.put("status",offer.get("status"));
        } else if(action.equals("renew")){
            F player=ownedPlayer(coachId,number(data,"playerId"),false);int days=number(data,"days"),index=-1;int[] durations={180,365,730,1095};for(int i=0;i<durations.length;i++)if(days==durations[i])index=i;
            if(index<0)throw new IllegalArgumentException("Choose 180, 365, 730 or 1095 days");
            int salary=data.containsKey("salary")?number(data,"salary"):minimumSalary(player,index);
            if(salary<=0 || salary<minimumSalary(player,index) || salary>Math.max(1,Math.round(player.fk()*0.25f)))throw new IllegalArgumentException("Salary is outside this player's accepted range");
            player.c(false);player.fm();player.a((long)days,player.fR()<0);player.ae(salary);result.put("status","RENEWED");
        } else if(action.equals("promote")){
            ah club=own(coachId);F player=ownedPlayer(coachId,number(data,"playerId"),true);
            if(club.kc().size()>=35)throw new IllegalStateException("Professional squad is full");
            best.K.a(false,player,club);player.aB(300);result.put("status","PROMOTED");
        } else if(action.equals("list")){
            F player=ownedPlayer(coachId,number(data,"playerId"),false);boolean forSale=bool(data,"forSale");int amount=forSale?(data.containsKey("price")?price(data):Math.max(1,player.fk())):player.fk();
            if(player.gl())throw new IllegalStateException("Loaned players cannot be listed");player.ag(amount);player.c(forSale);result.put("status",forSale?"LISTED":"WITHDRAWN");
        } else if(action.equals("loan-list")){
            F player=ownedPlayer(coachId,number(data,"playerId"),false);boolean available=bool(data,"available");if(player.gl())throw new IllegalStateException("Loaned players cannot be listed or withdrawn");
            if(available){int listed=own(coachId).kr()+state.f(own(coachId)).size();if(!player.fz() && listed>=10)throw new IllegalStateException("Native loan listing limit reached");}
            player.g(available);result.put("status",available?"LOAN_LISTED":"LOAN_WITHDRAWN");result.put("available",available);
        } else if(action.equals("loan")){
            ah buyer=own(coachId);F player=player(number(data,"playerId"));ah seller=player.fg();
            if(seller==null || seller==buyer || seller.jZ() || seller.lk()!=number(data,"sellerClubId") || !seller.kc().contains(player))throw new IllegalArgumentException("Loans are available only from an AI club");
            if(player.gl())throw new IllegalStateException("Player is already loaned");if(!player.fz())throw new IllegalStateException("Player is not listed as available for loan");
            int decision=best.l.a(player,buyer);if(decision!=1)throw new IllegalStateException(nativeLoanRefusal(decision));
            Object contract=null;for(Object raw:state.bt())if(isLoanFor(raw,player)){contract=raw;break;}if(contract==null || !Boolean.TRUE.equals(player.gl()) || player.fg()!=buyer)throw new IllegalStateException("Native loan transaction did not create its contract");
            result.put("status","LOANED");result.put("loan",loanRow(contract));
        } else if(action.equals("loan-return")){
            F player=player(number(data,"playerId"));ah club=own(coachId);Object contract=null;for(Object raw:state.bt())if(isLoanFor(raw,player)){contract=raw;break;}
            if(contract==null)throw new IllegalStateException("Active loan contract was not found");ah source=(ah)invokeLoan(contract,"tP");if(club!=source && club!=player.fg())throw new IllegalArgumentException("Only the source or destination club may return this loan");
            boolean returned=Boolean.TRUE.equals(invokeLoan(contract,"tN"));if(returned){state.bt().remove(contract);result.put("status","RETURNED");}else result.put("status","RETURN_PENDING");
        } else if(action.equals("sell")){
            F player=ownedPlayer(coachId,number(data,"playerId"),false);ah seller=own(coachId);int amount=price(data);validateSeller(player,seller);
            if(amount>Math.max(1,player.fk()*1.15))throw new IllegalArgumentException("Price exceeds native AI buyer range");
            best.l search=new best.l(player,amount,true,false,0);search.a(false,false);ah buyer=search.cK();int accepted=search.cL();
            if(buyer==null || buyer.jZ() || accepted<amount)throw new IllegalStateException("No AI club accepted this price; lower the price or list the player");
            validateTransfer(player,seller,buyer,accepted);transfer(player,seller,buyer,accepted,false);result.put("status","SOLD");result.put("buyerClub",buyer.getNome());result.put("price",accepted);
        } else if(action.equals("stadium-expand")){
            ah club=own(coachId);best.v stadium=club.ev();if(stadium==null)throw new IllegalStateException("Club has no stadium model");
            for(Object raw:state.bw())if(((best.B)raw).ev()==stadium)throw new IllegalStateException("Stadium construction is already in progress");
            int[] additions=new int[4];int total=0;for(int i=0;i<4;i++){additions[i]=number(data,"seats"+i);if(additions[i]<0)throw new IllegalArgumentException("Seat additions cannot be negative");total+=additions[i];}
            if(total==0)throw new IllegalArgumentException("Select seats to build");
            NativeStadium.Quote quote=NativeStadium.quote(club,additions);if(club.kb()<quote.cost)throw new IllegalStateException("Insufficient funds for this stadium expansion");
            best.B project=new best.B();project.a(stadium);project.c(quote.completion);project.c(additions);state.bw().add(project);club.w(quote.cost,7);
            result.put("status","CONSTRUCTION_STARTED");result.put("cost",quote.cost);result.put("endMillis",quote.completion.getTimeInMillis());result.put("seats",integers(additions));
        } else if(action.equals("stadium-settings")){
            best.v stadium=own(coachId).ev();if(stadium==null)throw new IllegalStateException("Club has no stadium model");boolean suggested=bool(data,"suggestedPrices");int[] prices=new int[4];String[] keys={"price0","price1","price2","price3"};int[] limits={200,300,500,1000};
            for(int i=0;i<4;i++){prices[i]=number(data,keys[i]);if(prices[i]<1 || prices[i]>limits[i])throw new IllegalArgumentException("Price is outside native range");}
            stadium.o(suggested);if(!suggested)stadium.b(prices);result.put("status","STADIUM_SETTINGS_UPDATED");
        } else if(action.equals("training-focus")){
            int focus=number(data,"focus");if(focus<0 || focus>1)throw new IllegalArgumentException("Invalid native training focus");own(coachId).bZ(focus);result.put("status","TRAINING_UPDATED");result.put("focus",focus);
        } else if(action.equals("training-assistant")){
            boolean enabled=bool(data,"enabled");own(coachId).M(enabled);result.put("status","TRAINING_UPDATED");result.put("enabled",enabled);
        } else if(action.equals("job")){
            al coach=coach(coachId);if(coach.fg()!=null)throw new IllegalStateException("You already manage a club");int clubId=number(data,"clubId");
            if(!jobs(coach).contains(clubId))throw new IllegalStateException("Club has not offered this position");ah club=state.x(clubId);
            if(club==null || club.jZ())throw new IllegalStateException("Club is no longer available");
            state.a(club,club.ka(),coach);jobOffers.remove(coachId);result.put("status","EMPLOYED");
        } else throw new IllegalArgumentException("Unknown management action");
        return result;
    }
    private static int asking(F player){return Math.max(1,player.fl()>0?player.fl():player.fk());}
    static String nativeLoanRefusal(int result){switch(result){case 0:return "The AI club refused this loan";case 2:return "This player already belongs to your club";case 3:return "The native loan limit was reached";case 4:return "The player does not want to join this club";case 5:return "The player is unavailable for a loan";case 6:return "The buyer's squad is full";default:return "The native Brasfoot loan failed ("+result+")";}}
    private boolean isLoanFor(Object raw,F player){try{return raw.getClass().getMethod("x").invoke(raw)==player;}catch(Exception ignored){return false;}}
    private Object invokeLoan(Object raw,String method){try{return raw.getClass().getMethod(method).invoke(raw);}catch(Exception failure){throw new IllegalStateException("Native loan contract is unavailable",failure);}}
    private Map<String,Object> loanRow(Object loan){F player=(F)invokeLoan(loan,"x");ah source=(ah)invokeLoan(loan,"tP"),destination=player==null?null:player.fg();Map<String,Object> row=playerRow(player);row.put("sourceClubId",source==null?null:source.lk());row.put("sourceClub",source==null?null:source.getNome());row.put("destinationClubId",destination==null?null:destination.lk());row.put("destinationClub",destination==null?null:destination.getNome());long due=((Number)invokeLoan(loan,"tO")).longValue();boolean overdue=state.bb().getTimeInMillis()>due;row.put("dueMillis",due);row.put("overdue",overdue);row.put("returnPending",overdue && source!=null && source.jZ() && source.kw()>=35);row.put("returnNoticeSent",Boolean.TRUE.equals(invokeLoan(loan,"tQ")));return row;}
    public Runnable trainingRollback(int coachId){ah club=own(coachId);boolean assistant=club.ll();int focus=club.lm();return ()->{club.M(assistant);club.bZ(focus);};}
    public Runnable stadiumSettingsRollback(int coachId){best.v stadium=own(coachId).ev();boolean suggested=stadium.dV();int[] prices=stadium.dU();return ()->{stadium.o(suggested);stadium.b(prices);};}
    public Runnable rollback(int coachId,Map<String,String> data){String action=required(data,"action");if(action.equals("training-focus")||action.equals("training-assistant"))return trainingRollback(coachId);if(action.equals("stadium-settings"))return stadiumSettingsRollback(coachId);if(!action.equals("promote"))return ()->{};
        ah club=own(coachId);F player=ownedPlayer(coachId,number(data,"playerId"),true);PlayerState savedPlayer=new PlayerState(player);List<Object> youth=new ArrayList<Object>(club.ky()),roster=new ArrayList<Object>(club.kc()),allPlayers=new ArrayList<Object>(state.O()),allYouth=new ArrayList<Object>(state.Q());
        return ()->{savedPlayer.restore(player);restore(club.ky(),youth);restore(club.kc(),roster);restore(state.O(),allPlayers);restore(state.Q(),allYouth);};
    }
    @SuppressWarnings({"rawtypes","unchecked"}) private static void restore(List target,List saved){target.clear();target.addAll(saved);}
    private static final class PlayerState {
        private final Map<Field,Object> values=new LinkedHashMap<Field,Object>();
        PlayerState(F player){for(Class<?> type=player.getClass();type!=null;type=type.getSuperclass())for(Field field:type.getDeclaredFields()){if(Modifier.isStatic(field.getModifiers()))continue;try{field.setAccessible(true);Object value=field.get(player);values.put(field,copy(value));}catch(Exception error){throw new IllegalStateException("Could not capture player for transaction rollback",error);}}}
        void restore(F player){for(Map.Entry<Field,Object> entry:values.entrySet()){Field field=entry.getKey();Object saved=entry.getValue();try{field.setAccessible(true);Object current=field.get(player);if(current instanceof List && saved instanceof List)CareerManagement.restore((List)current,(List)saved);else if(current instanceof Map && saved instanceof Map){((Map<?,?>)current).clear();((Map)current).putAll((Map)saved);}else field.set(player,saved);}catch(Exception error){throw new IllegalStateException("Could not restore player after failed checkpoint",error);}}}
        private static Object copy(Object value){if(value instanceof List)return new ArrayList<Object>((List<?>)value);if(value instanceof Map)return new LinkedHashMap<Object,Object>((Map<?,?>)value);if(value!=null&&value.getClass().isArray()){int length=Array.getLength(value);Object clone=Array.newInstance(value.getClass().getComponentType(),length);System.arraycopy(value,0,clone,0,length);return clone;}return value;}
    }
    static boolean nativeListedPurchase(boolean listed,int amount,int asking){return listed && amount>=asking;}
    static boolean nativeOfferAccepted(int nativeResult){return nativeResult==1;}
    static boolean nativeOfferCountered(int nativeResult){return nativeResult==6 || nativeResult==7;}
    static String nativeOfferRefusal(int nativeResult){switch(nativeResult){case 0:return "The AI club refused this offer";case 2:return "This player already belongs to your club";case 3:return "The native loan limit was reached";case 4:return "The player does not want to join this club";case 5:return "The AI club refused this offer";default:return "The native Brasfoot negotiation failed ("+nativeResult+")";}}
    private static List<Integer> integers(int[] values){List<Integer> result=new ArrayList<Integer>();for(int value:values)result.add(value);return result;}
    private static int minimumSalary(F player,int duration){int[] reduction={1,3,5,12},increase={10,12,15,5};return player.fR()<60?Math.max(1,player.fj()-Math.round(player.fj()*reduction[duration]/100f)):Math.max(1,player.fj()+Math.round(player.fj()*increase[duration]/100f));}
    private void validateSeller(F player,ah seller){
        if(player.fg()!=seller || !seller.kc().contains(player))throw new IllegalStateException("Player no longer belongs to the seller");
        if(player.gl())throw new IllegalStateException("Loaned players cannot be transferred");
        if(seller.kc().size()<=11)throw new IllegalStateException("Seller needs at least eleven players after the sale");
        if(player.getPosicao()==0){int keepers=0;for(Object raw:seller.kc())if(((F)raw).getPosicao()==0)keepers++;if(keepers<2)throw new IllegalStateException("Cannot sell the only goalkeeper");}
    }
    private void validateTransfer(F player,ah seller,ah buyer,int amount){validateSeller(player,seller);if(buyer.kc().size()>=35)throw new IllegalStateException("Buyer's squad is full");if(amount<=0 || buyer.kb()<amount)throw new IllegalStateException("Buyer has insufficient funds");}
    private void transfer(F player,ah seller,ah buyer,int amount,boolean listedPurchase){
        // Native transaction updates the roster, contracts, finances and transfer history.
        // CPU clubs do not account for balances in F.a(), so update their ledger explicitly.
        long sellerBefore=seller.kb(),buyerBefore=buyer.kb();player.a(buyer,amount,listedPurchase,false,false);
        long sellerChange=seller.kb()-sellerBefore,buyerChange=buyerBefore-buyer.kb();
        if(sellerChange<amount)seller.v((int)(amount-sellerChange),1);
        if(buyerChange<amount)buyer.w((int)(amount-buyerChange),1);
        if(seller.kc().contains(player) || !buyer.kc().contains(player) || player.fg()!=buyer)throw new IllegalStateException("Native transfer did not update both club rosters");
        player.as(-1);player.b(false);
    }
    private List<Integer> jobs(al coach){
        if(jobDate!=date()){jobDate=date();jobOffers.clear();}
        List<Integer> available=jobOffers.get(coach.lT());if(available!=null)return available;
        available=new ArrayList<Integer>();if(coach.fg()==null)for(Object raw:c.a.afQ.b(coach,false)){ah club=(ah)raw;if(!club.jZ() && !available.contains(club.lk()))available.add(club.lk());}
        jobOffers.put(coach.lT(),available);return available;
    }
    private Map<String,Object> playerRow(F player){ah club=player.fg();Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("playerId",player.gD());row.put("name",player.getNome());row.put("position",player.getPosicao());row.put("age",player.getIdade());row.put("country",player.getPais());row.put("side",player.getLado());row.put("skill1",player.getCr1());row.put("skill2",player.getCr2());row.put("skillGoalkeeping",player.gK());row.put("skillDefence",player.gN());row.put("skillSpeed",player.gO());row.put("skillArm",player.gO());row.put("skillFinishing",player.gP());row.put("skillHeading",player.gJ());row.put("skillSpeedNative",player.gJ());row.put("skillTechnique",player.gL());row.put("skillPassing",player.gM());row.put("development",player.fE());row.put("cpe",player.fD());row.put("junior",Boolean.TRUE.equals(player.fC()));row.put("strength",player.fi());row.put("salary",player.fj());row.put("value",player.fk());row.put("askingPrice",asking(player));row.put("contractDays",player.fR());row.put("contractEndMillis",Data.longNumber(Data.get(player,"eJ")));row.put("injuryUntilMillis",player.fo());row.put("forSale",player.ft());row.put("loanAvailable",player.fz());row.put("star",player.ff());row.put("worldClass",player.gm());row.put("goals",player.gy());row.put("appearances",player.gz());row.put("clubId",club==null?null:club.lk());row.put("club",club==null?null:club.getNome());row.put("clubCountry",club==null?-1:club.getPais());row.put("clubDivision",club==null?-1:club.getDivisao());row.put("humanOwner",humanOwner(club));row.put("loaned",player.gl());return row;}
    synchronized Map<String,Object> searchPlayers(int coachId,Map<String,String> request){coach(coachId);ensureIds();Map<String,String> filters=new LinkedHashMap<String,String>(request);int offset=pageNumber(filters,"offset",0,50000),limit=pageNumber(filters,"limit",1000,1000);filters.remove("offset");filters.remove("limit");List<F> matches=NativePlayerSearch.find(state,filters);if(offset>matches.size())throw new IllegalArgumentException("Search offset is past the result set");int end=Math.min(matches.size(),offset+limit);List<Object> rows=new ArrayList<Object>(end-offset);for(int i=offset;i<end;i++)rows.add(playerRow(matches.get(i)));Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("players",rows);result.put("total",matches.size());result.put("offset",offset);result.put("nextOffset",end<matches.size()?end:null);result.put("mode",state.isHabilidadeIndividual()?"individual":"general");result.put("dateMillis",date());return result;}
    private static int pageNumber(Map<String,String> fields,String key,int defaultValue,int max){if(!fields.containsKey(key))return defaultValue;String value=fields.get(key);if(value==null||!value.matches("[0-9]{1,6}"))throw new IllegalArgumentException("Invalid "+key);int number=Integer.parseInt(value);if(number<0||number>max||(key.equals("limit")&&number==0))throw new IllegalArgumentException("Invalid "+key);return number;}
    public Map<String,Object> snapshot(int coachId){al coach=coach(coachId);ensureIds();ah own=coach.fg();Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("clubId",own==null?null:own.lk());result.put("balance",own==null?0L:own.kb());
        List<Object> squad=new ArrayList<Object>(),youth=new ArrayList<Object>(),market=new ArrayList<Object>(),pending=new ArrayList<Object>(),jobs=new ArrayList<Object>(),loans=new ArrayList<Object>();
        if(own!=null){for(Object raw:own.kc())squad.add(playerRow((F)raw));for(Object raw:own.ky())youth.add(playerRow((F)raw));}
        for(Object raw:state.P()){ah club=(ah)raw;if(club==own)continue;for(Object value:club.kc()){F player=(F)value;if(!player.gl() && (player.ft() || player.fz() || club.jZ()))market.add(playerRow(player));}}
        for(Map<String,Object> value:offers.values())if(Data.number(value.get("buyerCoachId"))==coachId || Data.number(value.get("sellerCoachId"))==coachId){Map<String,Object> row=new LinkedHashMap<String,Object>(value);if("PENDING".equals(row.get("status")) && !pending(row))row.put("status","EXPIRED");pending.add(row);}
        for(Object raw:state.bt()){Object source=invokeLoan(raw,"tP");F player=(F)invokeLoan(raw,"x");ah destination=player==null?null:player.fg();if(own==source || own==destination)loans.add(loanRow(raw));}
        for(Integer clubId:jobs(coach)){ah club=state.x(clubId);if(club!=null && !club.jZ()){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("clubId",clubId);row.put("club",club.getNome());row.put("division",club.getDivisao());jobs.add(row);}}
        result.put("players",squad);result.put("youth",youth);result.put("market",market);result.put("offers",pending);result.put("loans",loans);result.put("jobs",jobs);return result;
    }
    public Map<String,Object> exportState(){Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("nextOffer",nextOffer);result.put("nextPlayer",nextPlayer);List<Object> rows=new ArrayList<Object>();for(Map<String,Object> offer:offers.values())rows.add(new LinkedHashMap<String,Object>(offer));result.put("offers",rows);result.put("jobDate",jobDate);Map<String,Object> jobs=new LinkedHashMap<String,Object>();for(Map.Entry<Integer,List<Integer>> entry:jobOffers.entrySet()){List<Object> candidates=new ArrayList<Object>();candidates.addAll(entry.getValue());jobs.put(String.valueOf(entry.getKey()),candidates);}result.put("jobOffers",jobs);return result;}
    public void restoreState(Map<String,Object> saved){if(saved==null)return;offers.clear();jobOffers.clear();nextOffer=Data.longNumber(saved.get("nextOffer"));nextPlayer=Math.max(nextPlayer,Data.number(saved.get("nextPlayer")));for(Object raw:Data.list(saved.get("offers"))){Map<String,Object> offer=new LinkedHashMap<String,Object>(Data.map(raw));offers.put(Data.longNumber(offer.get("offerId")),offer);}if(saved.containsKey("jobDate")&&Data.longNumber(saved.get("jobDate"))==date()){jobDate=date();Map<String,Object> stored=Data.map(saved.get("jobOffers"));for(Map.Entry<String,Object> entry:stored.entrySet()){int coachId=Integer.parseInt(entry.getKey());List<Integer> candidates=new ArrayList<Integer>();for(Object raw:Data.list(entry.getValue()))candidates.add(Data.number(raw));jobOffers.put(coachId,candidates);}}ensureIds();}
}
