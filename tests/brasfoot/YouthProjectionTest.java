package brasfoot;

import best.F;
import best.ah;
import java.io.File;
import java.util.*;

/** Exercises authoritative academy projection and promotion on a disposable career. */
public final class YouthProjectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]),work=new File(root,"analysis/youth-projection-test-"+System.currentTimeMillis());work.mkdirs();File save=new File(work,"career.s22");
        GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});CareerManagement management=new CareerManagement(game);int coachId=game.coachIds().iterator().next();ah club=null;for(Object raw:c.a.SR.M())if(((best.al)raw).lT()==coachId)club=((best.al)raw).fg();check(club!=null,"Career coach owns a club");
        Map<String,Object> managementState=management.snapshot(coachId);List<Object> youthRows=Data.list(managementState.get("youth"));check(!youthRows.isEmpty(),"Career contains an academy roster");
        Set<Integer> ids=new HashSet<Integer>();for(Object raw:Data.list(managementState.get("players")))check(ids.add(Data.number(Data.map(raw).get("playerId"))),"Professional IDs are unique");
        int promoteId=-1;for(Object raw:youthRows){Map<String,Object> row=Data.map(raw);check(ids.add(Data.number(row.get("playerId"))),"Youth and professional IDs are globally unique");for(String field:new String[]{"country","side","skill1","skill2","development","cpe","junior","contractEndMillis","injuryUntilMillis"})check(row.containsKey(field),"Youth projection includes "+field);if(promoteId<0)promoteId=Data.number(row.get("playerId"));}
        Map<String,Object> initialGame=game.snapshot();Map<String,Object> initialClub=null;for(Object raw:Data.list(initialGame.get("visualClubs")))if(Data.number(Data.map(raw).get("clubId"))==club.lk())initialClub=Data.map(raw);check(initialClub!=null&&Data.list(initialClub.get("youth")).size()==youthRows.size(),"Visual club exports the full host academy");
        Map<String,String> promote=new LinkedHashMap<String,String>();promote.put("action","promote");promote.put("playerId",String.valueOf(promoteId));F originalYouth=null;for(Object raw:club.ky())if(((F)raw).gD()==promoteId)originalYouth=(F)raw;check(originalYouth!=null,"Selected junior object is present before promotion");int originalStrength=originalYouth.fi(),originalDevelopment=originalYouth.fE(),originalCpe=originalYouth.fD();Runnable rollback=management.rollback(coachId,promote);management.execute(coachId,promote);check(!club.ky().contains(originalYouth)&&club.kc().contains(originalYouth),"Promotion mutates native rosters before persistence decision");rollback.run();check(club.ky().contains(originalYouth)&&!club.kc().contains(originalYouth),"Failed-checkpoint rollback restores roster membership");check(originalYouth.fC().booleanValue()&&originalYouth.fi()==originalStrength&&originalYouth.fE()==originalDevelopment&&originalYouth.fD()==originalCpe,"Failed-checkpoint rollback restores junior state");management.execute(coachId,promote);
        F promoted=null;for(Object raw:club.kc())if(((F)raw).gD()==promoteId)promoted=(F)raw;check(promoted!=null,"Host promotion adds the selected stable identity to professionals");for(Object raw:club.ky())check(((F)raw).gD()!=promoteId,"Promoted ID is removed from the academy");
        game.saveLocal(save);GameAdapter restored=GameAdapter.readLocalSave(save);CareerManagement restoredManagement=new CareerManagement(restored);Map<String,Object> restoredData=restoredManagement.snapshot(coachId);check(ids.contains(promoteId),"Original identity was present before promotion");int restoredYouth=Data.list(restoredData.get("youth")).size();check(restoredYouth==youthRows.size()-1,"Academy projection survives checkpoint reload");
        Map<String,Object> state=new LinkedHashMap<String,Object>();state.put("game",restored.snapshot());state.put("playback",new MatchPlayback(restored.currentMatches(),true).snapshot());VisualModel visual=new VisualModel(root,state);ah projected=visual.club(club.lk());check(projected.ky().size()==restoredYouth,"Native academy model matches host youth count after reconnect");boolean found=false;for(Object raw:projected.ky()){F youth=(F)raw;check(ids.contains(youth.gD()),"Projected youth ID belongs to host snapshot");check(Boolean.TRUE.equals(youth.fC()),"Projected row retains native junior state");found|=youth.gD()==promoteId;}check(!found,"Promoted player does not reappear in academy after reload");
        System.out.println("PASS authoritative youth identities, native table fields, host promotion and checkpoint projection");
    }
}
