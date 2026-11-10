package brasfoot;

import best.F;
import best.H;
import best.ah;
import best.ap;
import best.at;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.List;

/** Round-trips discipline and transfer-history references without renumbering player IDs. */
public final class NativeReferencePersistenceTest {
    @SuppressWarnings("unchecked")
    public static void main(String[] args)throws Exception{
        if(args.length>0&&args[0].equals("--verify")){verify(new File(args[1]),new File(args[2]),new File(args[3]),new File(args[4]));return;}
        File root=new File(args[0]);GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"},null);
        best.f state=c.a.SR;new CareerManagement(game);ah club=((best.al)state.M().get(0)).fg();F player=(F)club.kc().get(0);int playerId=player.gD();NativeReferencePersistence.prepare(state);at competition=null;for(Object raw:state.R())for(Object item:((best.a)raw).t())if(competition==null)competition=(at)item;check(competition!=null,"Career has no competition");int competitionId=competition.gD();
        H record=new H();record.m(competition);Data.set(record,"ca",2);Data.set(record,"cb",1);player.gH().add(record);
        ap transfer=new ap();transfer.a(player);((List<Object>)state.bo()).add(transfer);
        File save=File.createTempFile("native-references-",".s22"),second=File.createTempFile("native-references-repeat-",".s22"),metadata=File.createTempFile("native-references-ids-",".txt");try{
            game.saveLocal(save);java.nio.file.Files.write(metadata.toPath(),(playerId+"\n"+competitionId+"\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
            verifyInNewJvm(root,save,metadata,second);verifyInNewJvm(root,second,metadata,save);
        }finally{save.delete();second.delete();metadata.delete();}
        System.out.println("PASS native discipline and transfer-history references reconnect across repeated saves in separate JVMs");
    }
    private static void verifyInNewJvm(File root,File save,File metadata,File next)throws Exception{String java=System.getProperty("java.home")+File.separator+"bin"+File.separator+(System.getProperty("os.name").toLowerCase().contains("win")?"java.exe":"java");Process process=new ProcessBuilder(java,"-Djava.awt.headless=true","-cp",System.getProperty("java.class.path"),NativeReferencePersistenceTest.class.getName(),"--verify",root.getAbsolutePath(),save.getAbsolutePath(),metadata.getAbsolutePath(),next.getAbsolutePath()).inheritIO().start();int exit=process.waitFor();check(exit==0,"Reference round-trip child JVM failed with exit "+exit);}
    private static void verify(File root,File save,File metadata,File next)throws Exception{List<String> ids=java.nio.file.Files.readAllLines(metadata.toPath(),java.nio.charset.StandardCharsets.UTF_8);int playerId=Integer.parseInt(ids.get(0)),competitionId=Integer.parseInt(ids.get(1));GameAdapter loaded=GameAdapter.readLocalSave(save);best.f state=c.a.SR;F player=null;for(Object raw:state.O())if(((F)raw).gD()==playerId)player=(F)raw;check(player!=null,"Player identity changed across process restart");at competition=null;for(Object raw:state.R())for(Object item:((best.a)raw).t())if(((at)item).gD()==competitionId)competition=(at)item;check(competition!=null,"Competition identity changed across process restart");H suspension=null;for(Object raw:player.gH())if(((H)raw).gX()==competitionId)suspension=(H)raw;check(suspension!=null&&suspension.gS()==competition,"Discipline reference did not reconnect to its canonical competition");check(suspension.gW()[0]==2&&suspension.gW()[1]==1&&player.c(competition),"Discipline counters or suspension eligibility changed across process restart");ap history=null;for(Object raw:state.bo())if(((ap)raw).gD()==playerId)history=(ap)raw;check(history!=null&&history.x()==player,"Transfer history did not reconnect to the matching professional");check(history.gD()==playerId,"Transfer history changed the public player ID");loaded.saveLocal(next);}
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
}
