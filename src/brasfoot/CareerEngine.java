package brasfoot;

import best.I;
import best.ah;
import java.io.File;
import java.util.*;

/** Original career calendar and competition lifecycle, owned exclusively by the host. */
public final class CareerEngine {
    private CareerEngine() { }
    public static GameAdapter create(File root, String[] clubs) throws Exception {
        return create(root,clubs,null);
    }
    public static GameAdapter create(File root,String[] clubs,String[] coachNames)throws Exception {
        if(clubs.length<4 || clubs.length>16)throw new IllegalArgumentException("Choose between 4 and 16 human clubs");
        if(coachNames!=null){
            if(coachNames.length!=clubs.length)throw new IllegalArgumentException("Provide one name for each human coach");
            for(String name:coachNames)if(name==null || name.trim().length()<2 || name.trim().length()>35)throw new IllegalArgumentException("Coach names need between 2 and 35 characters");
        }
        Set<String> unique=new HashSet<String>();
        for(String club:clubs)if(club==null || club.trim().isEmpty() || !unique.add(club.trim().toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("Human clubs must be distinct and nonempty");
        System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());
        new best.M(true);best.n.cU();new c.a();c.a.SR.bs=false;
        // National teams require local selection dialogs; club competitions remain enabled.
        c.a.SR.setJogaSelecoesAll(false);c.a.SR.setJogaRegionais(false);
        c.a.SR.setVerMudancaTecnicos(0);c.a.SR.setAutoRenovaContrato(true);
        Class<?> loader=Class.forName("best.p");loader.getMethod("dd").invoke(null);
        for(Object raw:c.a.SR.bF){best.O country=(best.O)raw;country.v(country.getPais()==29);}
        loader.getMethod("dg").invoke(null);
        for(int index=0;index<clubs.length;index++) {
            ah selected=null;
            for(Object raw:c.a.SR.P()){ah candidate=(ah)raw;if(candidate.getPais()==29 && candidate.getNome().equalsIgnoreCase(clubs[index].trim())){selected=candidate;break;}}
            if(selected==null)throw new IllegalArgumentException("Unknown club: "+clubs[index]);
            selected.b(coachNames==null?"Tecnico "+(index+1):coachNames[index].trim(),29);
        }
        c.a.SR.az();prepare();return new GameAdapter(c.a.SR);
    }
    public static void completeDate() {
        // Same post-match hooks as f.at(), without navigating Swing or recursively advancing.
        c.a.SR.as();best.f.an();c.a.SR.aM();
        invoke("aC");invoke("aB");c.a.SR.q(0);
    }
    public static void nextDate(GameAdapter game) {
        for(I match:game.currentMatches())if(!match.e())throw new IllegalStateException("Current date is unfinished");
        int previous=c.a.SR.J();
        c.a.SR.ax();
        int next=c.a.SR.az();
        if(next==0){
            // ax() has set the native season-end flag; V() creates next year's competitions.
            c.a.afQ.zw();c.a.SR.V();
        } else if(next==previous)throw new IllegalStateException("Calendar did not advance");
        game.resetPreparation();
        prepare();
    }
    private static void prepare(){
        if(c.a.SR.isAutoRenovaContrato())for(Object raw:c.a.SR.M()) {
            ah club=((best.al)raw).fg();if(club==null)continue;
            for(Object value:club.kc()){best.F player=(best.F)value;if(player.fR()<=0)player.a(180L,true);}
        }
    }
    private static void invoke(String name) {
        try{java.lang.reflect.Method method=best.f.class.getDeclaredMethod(name);method.setAccessible(true);method.invoke(c.a.SR);}
        catch(Exception error){throw new IllegalStateException("Original career hook failed: "+name,error);}
    }
}
