package brasfoot;

import best.F;
import best.ah;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Keeps an actual lineup draft open while the host promotes an academy player. */
public final class YouthEditorConflictTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static Object field(Object target,String name){return Data.get(target,name);}
    private static void invoke(Object target,String name)throws Exception{Method method=target.getClass().getDeclaredMethod(name);method.setAccessible(true);method.invoke(target);}
    public static void main(String[] args)throws Exception{
        NativeClient client=new NativeClient(Paths.get(args[0]));Method connect=NativeClient.class.getDeclaredMethod("connect",String.class,String.class);connect.setAccessible(true);connect.invoke(client,args[1],args[2]);
        long deadline=System.currentTimeMillis()+15000;while(field(client,"nativePanel")==null&&System.currentTimeMillis()<deadline)Thread.sleep(50);check(field(client,"nativePanel")!=null,"Client connected");
        JFrame frame=(JFrame)field(client,"frame");SwingUtilities.invokeAndWait(()->{frame.setVisible(true);try{invoke(client,"openLineup");}catch(Exception error){throw new RuntimeException(error);}});
        JDialog editor=(JDialog)field(client,"editor");check(editor!=null&&editor.isDisplayable(),"Native lineup editor opened");
        VisualModel visual=(VisualModel)field(client,"visual");int coachId=(Integer)field(client,"coachId");ah club=visual.coach(coachId).fg();check(club!=null&&!club.kc().isEmpty(),"Client club is projected");F draft=(F)club.kc().get(0);draft.as(24);
        SessionApi api=(SessionApi)field(client,"api");Map<String,Object> management=api.call("/management",null);Map<String,Object> youth=Data.map(Data.list(management.get("youth")).get(0));int promotedId=Data.number(youth.get("playerId"));
        Map<String,Object> latest=Data.map(field(client,"latest"));Map<String,Object> coachRow=null;for(Object raw:Data.list(Data.map(latest.get("round")).get("coaches")))if(Data.number(Data.map(raw).get("coachId"))==coachId)coachRow=Data.map(raw);check(coachRow!=null,"Own preparation revision exists");
        Map<String,String> command=new LinkedHashMap<String,String>();command.put("action","promote");command.put("playerId",String.valueOf(promotedId));command.put("round",String.valueOf(Data.map(latest.get("round")).get("round")));command.put("revision",String.valueOf(coachRow.get("revision")));
        Map<String,Object> response=api.call("/management",command);long targetCheckpoint=Data.longNumber(response.get("checkpoint"));deadline=System.currentTimeMillis()+10000;
        while(Data.longNumber(Data.map(field(client,"latest")).get("checkpoint"))<targetCheckpoint&&System.currentTimeMillis()<deadline)Thread.sleep(50);
        check(editor.isDisplayable(),"Promotion does not close the editor");check(club.kc().get(0)==draft&&draft.fT()==24,"Host refresh overwrote the local lineup draft");
        Map<String,Object> latestGame=Data.map(Data.map(field(client,"latest")).get("game"));Map<String,Object> latestClub=null;for(Object raw:Data.list(latestGame.get("visualClubs")))if(Data.number(Data.map(raw).get("clubId"))==club.lk())latestClub=Data.map(raw);check(latestClub!=null,"Client received the host's club snapshot");boolean wireProfessional=false,wireYouth=false;for(Object raw:Data.list(latestClub.get("players")))if(Data.number(Data.map(raw).get("playerId"))==promotedId)wireProfessional=true;for(Object raw:Data.list(latestClub.get("youth")))if(Data.number(Data.map(raw).get("playerId"))==promotedId)wireYouth=true;check(wireProfessional&&!wireYouth,"Host snapshot contains the promoted identity in the professional roster");
        SwingUtilities.invokeAndWait(editor::dispose);deadline=System.currentTimeMillis()+5000;boolean projected=false;
        while(!projected&&System.currentTimeMillis()<deadline){for(Object raw:club.kc())if(((F)raw).gD()==promotedId)projected=true;if(!projected)Thread.sleep(50);}
        check(projected,"After closing the draft, the client projects the host's promoted identity");
        System.out.println("PASS lineup editor preserves local draft during remote promotion and reconciles on close");
        SwingUtilities.invokeAndWait(()->{try{invoke(client,"close");}catch(Exception error){throw new RuntimeException(error);}});
    }
}
