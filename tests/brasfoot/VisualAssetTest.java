package brasfoot;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Exercises the actual clubs and opponents loaded for an Arsenal career. */
public final class VisualAssetTest {
    public static void main(String[] args)throws Exception {
        File root=new File(args[0]);
        for(File file:new File(root,"game/Brasfoot22-23/teams").listFiles())if(file.getName().endsWith(".ban")){
            String asset=file.getName().substring(0,file.getName().length()-4);
            if(!VisualModel.clubAssetFile(root,asset).equals(file.getCanonicalFile()))throw new AssertionError(asset);
        }
        for(String invalid:new String[]{null,"","../outside","..\\outside","C:outside","/outside",".","..","bad\nname"}){
            try{VisualModel.clubAssetFile(root,invalid);throw new AssertionError("Accepted invalid asset: "+invalid);}catch(IOException expected){}
        }
        CareerEngine.initializeClubSelection(root);int arsenal=-1;
        for(Object raw:c.a.SR.P()){best.ah club=(best.ah)raw;if("arsenal".equals(club.jY()))arsenal=club.lk();}
        if(arsenal<0)throw new AssertionError("Arsenal asset not found");
        GameAdapter game=CareerEngine.create(root,new int[]{arsenal},null);
        Map<String,Object> snapshot=game.snapshot();
        for(Object raw:Data.list(snapshot.get("visualClubs"))){
            Map<String,Object> row=Data.map(raw);String asset=(String)row.get("asset");
            if(asset==null || !asset.matches("[A-Za-z0-9_\\-]+"))System.out.println("NATIVE_ASSET "+row.get("club")+" = ["+asset+"]");
        }
        Map<String,Object> state=new LinkedHashMap<String,Object>();
        state.put("game",snapshot);state.put("playback",new MatchPlayback(game.currentMatches(),true).snapshot());
        VisualModel visual=new VisualModel(root,state);
        for(Object raw:Data.list(snapshot.get("visualClubs"))){
            Map<String,Object> row=Data.map(raw);
            if(visual.club(Data.number(row.get("clubId")))==null)throw new AssertionError("Missing visual club: "+row.get("club"));
        }
        System.out.println("ARSENAL_VISUAL_PASS");
    }
}
