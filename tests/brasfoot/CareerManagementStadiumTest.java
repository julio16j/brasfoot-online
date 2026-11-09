package brasfoot;

import best.F;
import best.ah;
import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import best.al;

/** Real-career coverage for host-authoritative stadium construction. */
public final class CareerManagementStadiumTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static Map<String,String> request(String action,int seats){Map<String,String> data=new LinkedHashMap<String,String>();data.put("action",action);data.put("seats0",String.valueOf(seats));data.put("seats1","0");data.put("seats2","0");data.put("seats3","0");return data;}
    private static Object field(Object target,String name)throws Exception{Field field=target.getClass().getDeclaredField(name);field.setAccessible(true);return field.get(target);}
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);System.setProperty("java.awt.headless","true");GameAdapter game=CareerEngine.create(root,new String[]{"Corinthians","Palmeiras"});CareerManagement management=new CareerManagement(game);al coach=(al)c.a.SR.M().get(0);ah club=coach.fg();best.v stadium=club.ev();check(stadium!=null,"Career club must have a stadium");long balance=club.kb();java.lang.reflect.Field world=CareerManagement.class.getDeclaredField("state");world.setAccessible(true);best.f state=(best.f)world.get(management);
        Map<String,Object> result=management.execute(coach.lT(),request("stadium-expand",500));check("CONSTRUCTION_STARTED".equals(result.get("status")),"Host should start construction");int cost=((Number)result.get("cost")).intValue();check(club.kb()==balance-cost,"Host debits the quoted cost exactly once");check(state.bw().size()==1,"Host records one construction project");best.B project=(best.B)state.bw().get(0);check(project.ev()==stadium,"Construction references the owned stadium");
        try{management.execute(coach.lT(),request("stadium-expand",500));throw new AssertionError("Concurrent construction should be rejected");}catch(IllegalStateException expected){check(club.kb()==balance-cost&&state.bw().size()==1,"Rejected duplicate must not charge or add a project");}
        try{management.execute(coach.lT(),request("stadium-expand",Integer.MAX_VALUE));throw new AssertionError("Native seat limit should be enforced");}catch(IllegalArgumentException|IllegalStateException expected){check(club.kb()==balance-cost&&state.bw().size()==1,"Rejected limit must not mutate finance or projects");}
        System.out.println("PASS real career stadium quote, exact debit, project identity, duplicate rejection and native limit; cost="+cost);
    }
}
