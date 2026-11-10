package brasfoot;

import best.O;
import best.Z;
import best.ah;
import best.al;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/** Ensures selection is applied before native leagues are generated. */
public final class CareerCountrySelectionTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static Set<Integer> countries(List<?> values){Set<Integer> result=new TreeSet<Integer>();for(Object raw:values)result.add(((Z)raw).jc());return result;}
    private static Set<Integer> enabled(){Set<Integer> result=new TreeSet<Integer>();for(Object raw:c.a.SR.bF){O country=(O)raw;if(country.iC())result.add(country.getPais());}return result;}
    private static void scenario(File root,String[] assets,int[] expected,String[][] competitions)throws Exception{
        GameAdapter game=CareerEngine.createByAssets(root,assets,null);Set<Integer> expectedSet=new TreeSet<Integer>();for(int id:expected)expectedSet.add(id);
        check(enabled().equals(expectedSet),"Enabled countries differ from selection: "+enabled());check(countries(c.a.SR.N()).equals(expectedSet),"Generated national competitions differ from selection: "+countries(c.a.SR.N()));
        boolean foreign=false;for(Object raw:c.a.SR.P())if(!expectedSet.contains(((ah)raw).getPais()))foreign=true;check(foreign,"Unselected foreign clubs were removed from the native world");
        for(int index=0;index<assets.length;index++){ah club=null;for(Object raw:c.a.SR.P()){ah candidate=(ah)raw;if(candidate.jY().equalsIgnoreCase(assets[index]))club=candidate;}check(club!=null&&club.getPais()==expected[index],"Selected asset resolved to the wrong country");
            int coachId=-1;for(Object raw:c.a.SR.M()){al coach=(al)raw;if(coach.fg()==club)coachId=coach.lT();}check(coachId>=0,"Selected club did not receive a coach");
            Map<String,Object> view=CareerViews.snapshot(coachId);Set<String> found=new HashSet<String>();for(Object row:Data.list(view.get("calendar")))found.add(String.valueOf(Data.map(row).get("competition")));check(!found.isEmpty(),"Selected coach has no calendar fixtures");for(String competition:competitions[index])check(found.contains(competition),"Missing native calendar: "+competition);
        }
        check(game.coachIds().size()==assets.length,"Incorrect human coach count");check(c.a.SR.P().size()>100,"Native world lost most foreign clubs");
    }
    public static void main(String[] args)throws Exception{
        File root=new File(args[0]);PrintStream original=System.out;System.setOut(new PrintStream(new OutputStream(){public void write(int value){}}));try{
            scenario(root,new String[]{"corinthians_bra"},new int[]{29},new String[][]{{"Brasileirão - Série A","Paulista"}});
            scenario(root,new String[]{"realmadrid_esp"},new int[]{65},new String[][]{{"Espanhol - La Liga"}});
            scenario(root,new String[]{"corinthians_bra","realmadrid_esp"},new int[]{29,65},new String[][]{{"Brasileirão - Série A","Paulista"},{"Espanhol - La Liga"}});
            int foreignNationality=-1;for(Object raw:best.n.cY()){int code=((best.ad)raw).getPais();if(code!=29&&code!=65){foreignNationality=code;break;}}check(foreignNationality>=0,"Native nationality catalog has no country outside the league selection");
            GameAdapter nationalityGame=CareerEngine.createByAssets(root,new String[]{"realmadrid_esp"},new String[]{"Tecnico Brasileiro"},new int[]{foreignNationality});al selectedCoach=(al)c.a.SR.M().get(0);check(selectedCoach.lE()==foreignNationality,"Explicit coach nationality was replaced with the club country");Map<String,Object> gameCoach=Data.map(Data.list(nationalityGame.snapshot().get("coaches")).get(0));check(Data.number(gameCoach.get("nationality"))==foreignNationality,"Host snapshot omitted the coach nationality");Map<String,Object> coachView=CareerViews.snapshot(selectedCoach.lT());check(Data.number(Data.map(Data.list(coachView.get("coaches")).get(0)).get("nationality"))==foreignNationality,"Overview omitted the coach nationality");
            CareerManagement firstManagement=new CareerManagement(nationalityGame);long currentDate=c.a.SR.bb().getTimeInMillis();Map<Integer,List<Integer>> cachedJobs=new HashMap<Integer,List<Integer>>();cachedJobs.put(selectedCoach.lT(),new ArrayList<Integer>(Collections.singletonList(42)));Data.set(firstManagement,"jobDate",currentDate);Data.set(firstManagement,"jobOffers",cachedJobs);Map<String,Object> managementState=firstManagement.exportState();CareerManagement resumedManagement=new CareerManagement(nationalityGame);resumedManagement.restoreState(managementState);check(Data.longNumber(Data.get(resumedManagement,"jobDate"))==currentDate,"Employment candidate date was not restored");check(((List<?>)((Map<?,?>)Data.get(resumedManagement,"jobOffers")).get(selectedCoach.lT())).size()==1,"Employment candidates were not restored");
            try{CareerEngine.createByAssets(root,new String[]{"realmadrid_esp"},new String[]{"Tecnico"},new int[]{999999});throw new AssertionError("Unknown nationality accepted");}catch(IllegalArgumentException expected){check(expected.getMessage().contains("nationality"),"Unknown nationality rejection was not identifiable");}
            try{CareerEngine.createByAssets(root,new String[]{"realmadrid_esp"},new String[]{"Tecnico"},new int[0]);throw new AssertionError("Nationality cardinality mismatch accepted");}catch(IllegalArgumentException expected){check(expected.getMessage().contains("one nationality"),"Nationality cardinality rejection was not identifiable");}
            GameAdapter named=CareerEngine.create(root,new String[]{"Real Madrid"},null);check(enabled().equals(Collections.singleton(65)),"Foreign name path generated unrelated countries");check(named.coachIds().size()==1,"Foreign CLI name did not create one human coach");
            CareerEngine.initializeClubSelection(root);int id=-1;for(Object raw:c.a.SR.P()){ah club=(ah)raw;if(club.jY().equals("realmadrid_esp"))id=club.lk();}check(id>=0,"Club selection did not expose Real Madrid");
            GameAdapter compatibility=CareerEngine.create(root,new int[]{id},null);check(enabled().equals(Collections.singleton(65)),"Legacy selected ID path retained unrelated leagues");check(compatibility.coachIds().size()==1,"Selected ID path failed after resolving stable asset");
            try{CareerEngine.create(root,new String[]{"Nonexistent test club"},null);throw new AssertionError("Unknown club name was silently accepted");}catch(IllegalArgumentException expected){check(expected.getMessage().startsWith("Unknown club"),"Unexpected unknown-name error");}
        }finally{System.setOut(original);}
        System.out.println("PASS country selection before generation, stable assets, foreign club retention, names, IDs, mixed countries, and calendars");
    }
}
