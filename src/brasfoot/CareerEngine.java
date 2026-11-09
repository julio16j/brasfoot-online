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
        initializeWorld(root);
        List<String> assets=new ArrayList<String>();for(String club:clubs){e.t found=null;for(Object raw:c.a.SR.bC){e.t candidate=(e.t)raw;if(candidate.getNome().equalsIgnoreCase(club.trim())){if(found!=null)throw new IllegalArgumentException("Ambiguous club name: "+club+"; use a club asset ID");found=candidate;}}if(found==null)throw new IllegalArgumentException("Unknown club: "+club);assets.add(found.getFileRef());}
        return startAssets(assets,coachNames);
    }
    public static GameAdapter create(File root,int[] clubIds,String[] coachNames)throws Exception {
        validateCount(clubIds.length);validateNames(clubIds.length,coachNames);initializeClubSelection(root);
        List<String> assets=new ArrayList<String>();Set<Integer> unique=new HashSet<Integer>();
        for(int id:clubIds){if(!unique.add(id))throw new IllegalArgumentException("Human clubs must be distinct");ah found=null;for(Object raw:c.a.SR.P()){ah candidate=(ah)raw;if(candidate.lk()==id){found=candidate;break;}}if(found==null)throw new IllegalArgumentException("Unknown club id: "+id);assets.add(found.jY());}
        return createByAssets(root,assets.toArray(new String[assets.size()]),coachNames);
    }
    public static GameAdapter createByAssets(File root,String[] assets,String[] coachNames)throws Exception {
        validateCount(assets.length);validateNames(assets.length,coachNames);Set<String> unique=new HashSet<String>();
        for(String asset:assets)if(asset==null || asset.trim().isEmpty() || !unique.add(asset.trim().toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("Human club assets must be distinct and nonempty");
        initializeWorld(root);List<String> selected=new ArrayList<String>();Collections.addAll(selected,assets);return startAssets(selected,coachNames);
    }
    private static void validateCount(int count){if(count<1 || count>16)throw new IllegalArgumentException("Choose between 1 and 16 human clubs");}
    private static void validateNames(int count,String[] coachNames){
        if(coachNames==null)return;if(coachNames.length!=count)throw new IllegalArgumentException("Provide one name for each human coach");
        for(String name:coachNames)if(name==null || name.trim().length()<2 || name.trim().length()>35)throw new IllegalArgumentException("Coach names need between 2 and 35 characters");
    }
    public static void initializeClubSelection(File root)throws Exception{initializeWorld(root);setCountries(Collections.<Integer>emptySet(),true);generateCompetitions();}
    private static void initializeWorld(File root)throws Exception {
        System.setProperty("user.dir",new File(root,"game/Brasfoot22-23").getAbsolutePath());
        new best.M(true);best.n.cU();new c.a();
        // The original launcher loads the registered-edition flag from the user's
        // Brasfoot preferences before showing its club-registration screen.
        try{best.h2.mg();}catch(SecurityException error){System.err.println("Brasfoot registration preferences are unavailable; original trial restrictions may apply.");}
        c.a.SR.bs=false;
        // National teams require local selection dialogs; club competitions remain enabled.
        c.a.SR.setJogaSelecoesAll(false);c.a.SR.setJogaRegionais(false);
        c.a.SR.setVerMudancaTecnicos(0);
        Class<?> loader=Class.forName("best.p");loader.getMethod("dd").invoke(null);
    }
    private static GameAdapter startAssets(List<String> assets,String[] coachNames){
        Map<String,e.t> records=new LinkedHashMap<String,e.t>();Set<Integer> countries=new HashSet<Integer>();
        for(String asset:assets){e.t found=null;for(Object raw:c.a.SR.bC){e.t record=(e.t)raw;if(record.getFileRef().equalsIgnoreCase(asset.trim())){if(found!=null)throw new IllegalArgumentException("Ambiguous club asset: "+asset);found=record;}}
            if(found==null)throw new IllegalArgumentException("Unknown club asset: "+asset);records.put(asset.trim().toLowerCase(Locale.ROOT),found);countries.add(found.getPais());}
        setCountries(countries,false);generateCompetitions();List<ah> selected=new ArrayList<ah>();
        for(String asset:assets){String key=asset.trim().toLowerCase(Locale.ROOT);e.t record=records.get(key);ah found=null;for(Object raw:c.a.SR.P()){ah club=(ah)raw;if(club.jY().equalsIgnoreCase(record.getFileRef())){found=club;break;}}if(found==null)throw new IllegalStateException("Selected club was not generated: "+asset);selected.add(found);}
        for(int index=0;index<selected.size();index++)selected.get(index).b(coachNames==null?"Técnico "+(index+1):coachNames[index].trim(),selected.get(index).getPais());
        c.a.SR.az();prepare();return new GameAdapter(c.a.SR);
    }
    private static void setCountries(Set<Integer> countries,boolean all){for(Object raw:c.a.SR.bF){best.O country=(best.O)raw;country.v(all||countries.contains(country.getPais()));}}
    private static void generateCompetitions(){try{Class.forName("best.p").getMethod("dg").invoke(null);}catch(Exception error){throw new IllegalStateException("Original competition generation failed",error);}}
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
    static void prepare(){
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
