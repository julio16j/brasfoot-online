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
        validateCount(clubs.length);validateNames(clubs.length,coachNames);
        Set<String> unique=new HashSet<String>();for(String club:clubs)if(club==null || club.trim().isEmpty() || !unique.add(club.trim().toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("Human clubs must be distinct and nonempty");
        initialize(root,false);
        List<ah> selected=new ArrayList<ah>();
        for(String club:clubs){ah found=null;for(Object raw:c.a.SR.P()){ah candidate=(ah)raw;if(candidate.getPais()==29 && candidate.getNome().equalsIgnoreCase(club.trim())){found=candidate;break;}}if(found==null)throw new IllegalArgumentException("Unknown club: "+club);selected.add(found);}
        return start(selected,coachNames);
    }
    public static GameAdapter create(File root,int[] clubIds,String[] coachNames)throws Exception {
        validateCount(clubIds.length);validateNames(clubIds.length,coachNames);initialize(root,true);
        List<ah> selected=new ArrayList<ah>();Set<Integer> unique=new HashSet<Integer>();
        for(int id:clubIds){if(!unique.add(id))throw new IllegalArgumentException("Human clubs must be distinct");ah found=null;for(Object raw:c.a.SR.P()){ah candidate=(ah)raw;if(candidate.lk()==id){found=candidate;break;}}if(found==null)throw new IllegalArgumentException("Unknown club id: "+id);selected.add(found);}
        return start(selected,coachNames);
    }
    private static void validateCount(int count){if(count<1 || count>16)throw new IllegalArgumentException("Choose between 1 and 16 human clubs");}
    private static void validateNames(int count,String[] coachNames){
        if(coachNames==null)return;if(coachNames.length!=count)throw new IllegalArgumentException("Provide one name for each human coach");
        for(String name:coachNames)if(name==null || name.trim().length()<2 || name.trim().length()>35)throw new IllegalArgumentException("Coach names need between 2 and 35 characters");
    }
    public static void initializeClubSelection(File root)throws Exception{initialize(root,true);}
    private static void initialize(File root,boolean allCountries)throws Exception {
        System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());
        new best.M(true);best.n.cU();new c.a();
        // The original launcher loads the registered-edition flag from the user's
        // Brasfoot preferences before showing its club-registration screen.
        try{best.h2.mg();}catch(SecurityException error){System.err.println("Brasfoot registration preferences are unavailable; original trial restrictions may apply.");}
        c.a.SR.bs=false;
        // National teams require local selection dialogs; club competitions remain enabled.
        c.a.SR.setJogaSelecoesAll(false);c.a.SR.setJogaRegionais(false);
        c.a.SR.setVerMudancaTecnicos(0);c.a.SR.setAutoRenovaContrato(true);
        Class<?> loader=Class.forName("best.p");loader.getMethod("dd").invoke(null);
        for(Object raw:c.a.SR.bF){best.O country=(best.O)raw;country.v(allCountries || country.getPais()==29);}
        loader.getMethod("dg").invoke(null);
    }
    private static GameAdapter start(List<ah> selected,String[] coachNames){
        Set<Integer> countries=new HashSet<Integer>();for(ah club:selected)countries.add(club.getPais());
        for(Object raw:c.a.SR.bF){best.O country=(best.O)raw;country.v(countries.contains(country.getPais()));}
        for(int index=0;index<selected.size();index++)selected.get(index).b(coachNames==null?"Técnico "+(index+1):coachNames[index].trim(),selected.get(index).getPais());
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
