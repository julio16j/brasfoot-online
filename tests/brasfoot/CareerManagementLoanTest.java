package brasfoot;

import best.F;
import best.ah;
import java.io.File;
import java.util.Map;

/** Real-engine checks for AI-origin loans and persisted human-origin requests. */
public final class CareerManagementLoanTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static void reject(Runnable action,String message){try{action.run();}catch(IllegalArgumentException|IllegalStateException expected){return;}throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});
        int coach=game.coachIds().iterator().next();ah destination=((best.al)c.a.SR.M().get(0)).fg();ah source=null;for(Object raw:c.a.SR.P()){ah club=(ah)raw;if(!club.jZ() && club.kc().size()>12){source=club;break;}}check(source!=null,"AI source club exists");CareerManagement management=new CareerManagement(game);
        F target=null;for(Object raw:source.kc()){F player=(F)raw;if(!player.gl() && !player.ff() && !player.gm()){target=player;break;}}
        check(target!=null,"AI source should have an available player");final F selected=target;final ah seller=source;target.g(true);int originalSalary=target.fj();
        Map<String,String> request=new java.util.LinkedHashMap<String,String>();request.put("action","loan");request.put("playerId",String.valueOf(target.gD()));request.put("sellerClubId",String.valueOf(seller.lk()));Map<String,Object> moved=management.execute(coach,request);
        check("LOANED".equals(moved.get("status")),"Loan should complete through native consumer");
        check(target.fg()==destination && Boolean.TRUE.equals(target.gl()),"Native transaction should move player and mark loan");
        check(target.fj()==originalSalary,"Loan transaction must preserve salary through native engine");
        Object contract=null;for(Object raw:c.a.SR.bt()){try{if(raw.getClass().getMethod("x").invoke(raw)==target)contract=raw;}catch(Exception ignored){}}
        check(contract!=null && contract.getClass().getMethod("tP").invoke(contract)==seller,"Native loan contract should preserve source club");
        check(((Number)Data.map(moved.get("loan")).get("dueMillis")).longValue()==((Number)contract.getClass().getMethod("tO").invoke(contract)).longValue(),"Command should return the native due date");
        reject(()->{Map<String,String> again=new java.util.LinkedHashMap<String,String>();again.put("action","loan");again.put("playerId",String.valueOf(selected.gD()));again.put("sellerClubId",String.valueOf(seller.lk()));management.execute(coach,again);},"Already loaned player should be rejected");
        Map<String,Object> view=management.snapshot(coach);check(Data.list(view.get("loans")).size()==1,"Destination manager should see active loan");
        int secondCoach=-1;for(Object raw:c.a.SR.M()){best.al candidate=(best.al)raw;if(candidate.fg()==seller)secondCoach=candidate.lT();}if(secondCoach>=0)check(Data.list(management.snapshot(secondCoach).get("loans")).size()==1,"Source manager should see active loan when source has a human owner");
        best.al sourceCoach=null;for(Object raw:c.a.SR.M()){best.al candidate=(best.al)raw;if(candidate.lT()!=coach){sourceCoach=candidate;break;}}check(sourceCoach!=null,"Second human coach exists");F listed=null;for(Object raw:sourceCoach.fg().kc()){F player=(F)raw;if(!player.gl()){listed=player;break;}}check(listed!=null,"Second human squad has a player");listed.g(true);
        Map<String,String> proposal=new java.util.LinkedHashMap<String,String>();proposal.put("action","loan-request");proposal.put("playerId",String.valueOf(listed.gD()));proposal.put("sellerClubId",String.valueOf(sourceCoach.fg().lk()));
        check(management.affectedCoaches(coach,proposal).contains(sourceCoach.lT()),"Human source must be a preparation participant");Map<String,Object> pending=management.execute(coach,proposal);check("PENDING".equals(pending.get("status")) && !listed.gl(),"Human-origin request must not move player before acceptance");
        CareerManagement recovered=new CareerManagement(game);recovered.restoreState(management.exportState());Map<String,Object> sellerView=recovered.snapshot(sourceCoach.lT());check(Data.list(sellerView.get("offers")).size()==1,"Pending request should survive management-state restoration");
        Map<String,String> response=new java.util.LinkedHashMap<String,String>();response.put("action","respond");response.put("offerId",String.valueOf(pending.get("offerId")));response.put("accept","false");check(recovered.affectedCoaches(sourceCoach.lT(),response).contains(coach),"Response must invalidate requesting coach preparation");check("REJECTED".equals(recovered.execute(sourceCoach.lT(),response).get("status")) && !listed.gl(),"Declined request must leave roster unchanged");
        Map<String,String> returning=new java.util.LinkedHashMap<String,String>();returning.put("action","loan-return");returning.put("playerId",String.valueOf(selected.gD()));check(management.affectedCoaches(coach,returning).contains(coach),"Destination manager must participate in return");check("RETURNED".equals(management.execute(coach,returning).get("status")) && !selected.gl() && selected.fg()==seller,"Native return must restore the original club and remove the contract");
        System.out.println("PASS AI loan transaction/projection; human loan consent and proposal recovery");
    }
}

