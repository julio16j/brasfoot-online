package brasfoot;

import java.awt.Color;
import java.util.*;

/** Immutable, versioned session preferences. Native setters never write options.bcf. */
public final class SessionOptions {
    static final class Adapter {
        final String key,property,label,kind,boundary,unavailable;final int max;final boolean integerBoolean,inverted;
        Adapter(String key,String property,String label,String kind,int max,boolean integerBoolean,boolean inverted,String boundary,String unavailable){this.key=key;this.property=property;this.label=label;this.kind=kind;this.max=max;this.integerBoolean=integerBoolean;this.inverted=inverted;this.boundary=boundary;this.unavailable=unavailable;}
        Object read(est.Options nativeOptions){try{
            Object value=est.Options.class.getMethod((kind.equals("boolean") && !integerBoolean?"is":"get")+property).invoke(nativeOptions);
            if(integerBoolean)return ((Number)value).intValue()==(inverted?0:1);
            if(kind.equals("palette")){List<String> colors=new ArrayList<String>();for(Color color:(Color[])value)colors.add(String.format("#%06X",color.getRGB()&0xffffff));return colors;}
            if(kind.equals("competitions")){Map<String,Object> map=new LinkedHashMap<String,Object>();boolean[] flags=(boolean[])value;for(int i=0;i<flags.length;i++)map.put(String.valueOf(i),flags[i]);return map;}
            return value;
        }catch(Exception error){throw new IllegalStateException("Native option unavailable: "+key,error);}}
        void apply(Object value){try{
            Object argument=value;Class<?> type=kind.equals("boolean") && !integerBoolean?boolean.class:int.class;
            if(integerBoolean)argument=((Boolean)value)?(inverted?0:1):(inverted?1:0);
            if(kind.equals("palette")){type=Color[].class;List<?> colors=(List<?>)value;Color[] palette=new Color[5];for(int i=0;i<5;i++)palette[i]=Color.decode((String)colors.get(i));argument=palette;}
            if(kind.equals("competitions")){type=boolean[].class;boolean[] flags=new boolean[21];for(int i=0;i<21;i++)flags[i]=(Boolean)((Map<?,?>)value).get(String.valueOf(i));argument=flags;}
            est.Options.class.getMethod("set"+property,type).invoke(c.a.vM(),argument);
        }catch(Exception error){throw new IllegalStateException("Cannot apply native option: "+key,error);}}
    }
    static final Map<String,Adapter> ADAPTERS=new LinkedHashMap<String,Adapter>();
    private static void add(String key,String property,String label,String kind,int max,boolean intBool,boolean inverted,String boundary,String reason){ADAPTERS.put(key,new Adapter(key,property,label,kind,max,intBool,inverted,boundary,reason));}
    static {
        add("autoSaveMode","AutoSalvar","Salvar automaticamente","integer",3,false,false,"NEXT_SAFE_CHECKPOINT","Contador e início do ano nativos ainda não integrados ao salvamento adicional online");
        add("humanSpeedIndex","Velocidade","Velocidade do Jogo","integer",5,false,false,"IMMEDIATE",null);
        add("nonHumanSpeedIndex","VelocidadeNH","Velocidade sem jogos humanos","integer",5,false,false,"IMMEDIATE",null);
        add("showNonHumanPenalties","VerDecisaoPenNaoHumano","Disputas de pênaltis sem humanos","boolean",1,true,false,"NEXT_PENALTY_SHOOTOUT","Apresentação da disputa nativa ainda não integrada");
        add("showCoachChanges","VerMudancaTecnicos","Mudanças de técnicos","boolean",1,true,false,"NEXT_COACH_CHANGE","Notificações nativas ainda não transportadas aos destinatários");
        add("openLineupAtHalfTime","VerJanelaSubs","Janela de substituição no intervalo","boolean",1,true,false,"NEXT_HALF_TIME",null);
        add("contractWarningMode","AvisoTerminoContrato","Aviso de término de contrato","integer",2,false,false,"NEXT_CONTRACT_EVALUATION","Avisos nativos ainda não transportados aos destinatários");
        add("showPlayerAuctions","VerLeiloes","Ver leilão de jogadores","boolean",1,false,false,"NEXT_AUCTION","Leilão nativo ainda não integrado ao protocolo online");
        add("autoRenewExpiredContracts","AutoRenovaContrato","Atualizar automaticamente contratos encerrados","boolean",1,false,false,"NEXT_CONTRACT_EVALUATION",null);
        add("humanScoreColorsEnabled","UsaCorPlacar","Cor nos placares humanos","boolean",1,false,false,"IMMEDIATE",null);
        add("humanScoreColorIndex","CorPlacar","Cor selecionada para placares","integer",2,false,false,"IMMEDIATE",null);
        add("soundsEnabled","UsaSons","Sons nos eventos dos jogos","boolean",1,false,false,"IMMEDIATE",null);
        add("highlightHomeEvents","NegritoCasa","Destaque nos eventos do time da casa","boolean",1,false,false,"IMMEDIATE",null);
        add("windowThemeIndex","CorTema","Tema de cores das janelas","integer",4,false,false,"IMMEDIATE",null);
        add("playerListColorsEnabled","UsaCoresLista","Usar cores na lista de jogadores","boolean",1,false,false,"IMMEDIATE",null);
        add("playerListPalette","CoresLista","Paleta de jogadores","palette",5,false,false,"IMMEDIATE",null);
        String filters="Precedência e projeção dos filtros nativos ainda não integradas";
        add("groupStateCompetitions","VerEstaduaisAgrupados","Ver todos os estaduais agrupados","boolean",1,false,false,"IMMEDIATE",filters);
        add("hideNonHumanNationalLeagues","IgnoraLigas","Ocultar ligas nacionais não humanas","boolean",1,false,false,"IMMEDIATE",filters);
        add("hideStateRegionalOutsideBrazil","IgnoraEstadual","Ocultar estaduais/regionais fora do Brasil","boolean",1,false,false,"IMMEDIATE",filters);
        add("hideRegionalsWithoutHumanHistory","RegionaisSemHumanos","Ocultar regionais sem histórico humano","boolean",1,true,true,"IMMEDIATE",filters);
        add("watchedLeagueCountryIds",null,"Ligas por país","countries",0,false,false,"IMMEDIATE",filters);
        add("nonHumanCompetitionPlayback","VerJint","Competições sem times humanos","competitions",21,false,false,"IMMEDIATE",filters);
    }
    private final long revision;
    private final Map<String,Object> values,pending;
    private final int maxSpeed;
    private SessionOptions(long revision,Map<String,Object> values,Map<String,Object> pending,int maxSpeed){this.revision=revision;this.values=Collections.unmodifiableMap(new LinkedHashMap<String,Object>(values));this.pending=Collections.unmodifiableMap(new LinkedHashMap<String,Object>(pending));this.maxSpeed=maxSpeed;}
    public long revision(){return revision;}
    public Object get(String key){return values.get(key);}
    public static SessionOptions load(Map<String,Object> saved){
        Map<String,Object> values=new LinkedHashMap<String,Object>();
        for(Adapter adapter:ADAPTERS.values())values.put(adapter.key,adapter.property==null?countrySelection():validate(adapter,adapter.read(c.a.vM())));
        Map<String,Object> pending=new LinkedHashMap<String,Object>();long revision=0;
        if(saved!=null){if(integer(saved.get("schemaVersion"),1)!=1)throw new IllegalArgumentException("Invalid options schema");revision=longInteger(saved.get("revision"));
            if(!(saved.get("values") instanceof Map) || !(saved.get("pendingValues") instanceof Map))throw new IllegalArgumentException("Invalid persisted session options");
            Map<String,Object> restored=Data.map(saved.get("values"));for(String key:restored.keySet()){Adapter adapter=adapter(key);Object checked=validate(adapter,restored.get(key));if(adapter.kind.equals("competitions")){Map<String,Object> merged=new LinkedHashMap<String,Object>(Data.map(values.get(key)));merged.putAll(Data.map(checked));checked=Collections.unmodifiableMap(merged);}values.put(key,checked);}
            for(String key:values.keySet())if(!restored.containsKey(key))System.err.println("Migrating missing session option: "+key);
            for(Map.Entry<String,Object> entry:Data.map(saved.get("pendingValues")).entrySet()){Adapter adapter=adapter(entry.getKey());if(adapter.boundary.equals("IMMEDIATE"))throw new IllegalArgumentException("Invalid pending option: "+entry.getKey());pending.put(entry.getKey(),validate(adapter,entry.getValue()));}
        }
        return new SessionOptions(revision,values,pending,c.a.vL()?5:4);
    }
    static Adapter adapter(String key){Adapter adapter=ADAPTERS.get(key);if(adapter==null)throw new IllegalArgumentException("Unknown option: "+key);return adapter;}
    static long longInteger(Object value){if(!(value instanceof Number))throw new IllegalArgumentException("Expected an integer");try{long n=new java.math.BigDecimal(value.toString()).longValueExact();if(n<0 || n>9007199254740991L)throw new ArithmeticException();return n;}catch(ArithmeticException e){throw new IllegalArgumentException("Invalid integer");}}
    private static int integer(Object value,int max){long n=longInteger(value);if(n>max)throw new IllegalArgumentException("Option outside native range");return (int)n;}
    static Object validate(Adapter adapter,Object value){
        if(adapter.kind.equals("integer"))return integer(value,adapter.max);
        if(adapter.kind.equals("boolean")){if(!(value instanceof Boolean))throw new IllegalArgumentException("Expected boolean: "+adapter.key);return value;}
        if(adapter.kind.equals("palette")){if(!(value instanceof List) || ((List<?>)value).size()!=5)throw new IllegalArgumentException("Expected five #RRGGBB colors");List<String> colors=new ArrayList<String>();for(Object color:(List<?>)value){if(!(color instanceof String) || !((String)color).matches("#[0-9a-fA-F]{6}"))throw new IllegalArgumentException("Invalid palette color");colors.add(((String)color).toUpperCase(Locale.ROOT));}return Collections.unmodifiableList(colors);}
        if(adapter.kind.equals("countries")){if(!(value instanceof List))throw new IllegalArgumentException("Expected country IDs");SortedSet<Integer> ids=new TreeSet<Integer>();for(Object raw:(List<?>)value){int id=integer(raw,Integer.MAX_VALUE);boolean found=false;for(Object country:c.a.SR.N())if(countryId((best.Z)country)==id)found=true;if(!found || !ids.add(id))throw new IllegalArgumentException("Unknown or duplicate country ID");}return Collections.unmodifiableList(new ArrayList<Integer>(ids));}
        if(!(value instanceof Map))throw new IllegalArgumentException("Expected competition map");Map<String,Object> map=new LinkedHashMap<String,Object>();for(Map.Entry<?,?> entry:((Map<?,?>)value).entrySet()){String key=String.valueOf(entry.getKey());if(!key.matches("([0-9]|1[0-9]|20)") || !(entry.getValue() instanceof Boolean))throw new IllegalArgumentException("Unknown competition or invalid value");map.put(key,entry.getValue());}return Collections.unmodifiableMap(map);
    }
    // best.Z's stable native country ID, not the row number of b.A.
    static int countryId(best.Z country){return Data.number(Data.get(country,"pais"));}
    private static List<Integer> countrySelection(){List<Integer> ids=new ArrayList<Integer>();for(Object raw:c.a.SR.N()){best.Z country=(best.Z)raw;if(country.jr())ids.add(countryId(country));}Collections.sort(ids);return Collections.unmodifiableList(ids);}
    public SessionOptions change(Map<String,Object> changes,boolean override){
        if(changes.isEmpty())throw new IllegalArgumentException("At least one option is required");
        Map<String,Object> next=new LinkedHashMap<String,Object>(values),waiting=new LinkedHashMap<String,Object>(pending);
        Map<String,Object> checked=new LinkedHashMap<String,Object>();for(Map.Entry<String,Object> entry:changes.entrySet())checked.put(entry.getKey(),validate(adapter(entry.getKey()),entry.getValue()));
        for(Map.Entry<String,Object> entry:checked.entrySet()){Adapter adapter=adapter(entry.getKey());if(adapter.unavailable!=null)throw new Conflict("OPTION_NOT_SUPPORTED",adapter.unavailable);
            if(adapter.key.endsWith("SpeedIndex")){if(override)throw new Conflict("CLI_OVERRIDE","Velocidade controlada por --tick-ms");if((Integer)entry.getValue()>maxSpeed)throw new IllegalArgumentException("Speed unavailable in this native installation");}
            if(adapter.boundary.equals("IMMEDIATE"))next.put(adapter.key,entry.getValue());else if(Objects.equals(values.get(adapter.key),entry.getValue()))waiting.remove(adapter.key);else waiting.put(adapter.key,entry.getValue());
        }
        if(next.equals(values) && waiting.equals(pending))return this;return new SessionOptions(revision+1,next,waiting,maxSpeed);
    }
    public SessionOptions activate(String boundary){Map<String,Object> next=new LinkedHashMap<String,Object>(values),waiting=new LinkedHashMap<String,Object>(pending);for(String key:pending.keySet())if(adapter(key).boundary.equals(boundary)){next.put(key,pending.get(key));waiting.remove(key);}return waiting.equals(pending)?this:new SessionOptions(revision+1,next,waiting,maxSpeed);}
    public void applyNative(){for(Adapter adapter:ADAPTERS.values())if(adapter.property!=null)adapter.apply(values.get(adapter.key));for(Object raw:c.a.SR.N()){best.Z country=(best.Z)raw;country.B(((List<?>)values.get("watchedLeagueCountryIds")).contains(countryId(country)));}if(!Boolean.TRUE.equals(values.get("soundsEnabled")))a.dV.qb();}
    public static void project(Map<String,Object> snapshot){Map<String,Object> values=Data.map(snapshot.get("values"));for(Adapter adapter:ADAPTERS.values())if(adapter.property!=null && values.containsKey(adapter.key))adapter.apply(validate(adapter,values.get(adapter.key)));}
    public static long interval(int index,boolean humans){if(index<0 || index>5)throw new IllegalArgumentException("Invalid speed index");return (humans?best.aq.rz:best.aq.rA)[index];}
    public long interval(boolean humans,Long override){return override!=null?override:interval((Integer)values.get(humans?"humanSpeedIndex":"nonHumanSpeedIndex"),humans);}
    public Map<String,Object> persisted(){Map<String,Object> out=new LinkedHashMap<String,Object>();out.put("schemaVersion",1);out.put("revision",revision);out.put("values",values);out.put("pendingValues",pending);return out;}
    public Map<String,Object> snapshot(boolean host,boolean humans,Long override){Map<String,Object> out=persisted();out.put("canEdit",host);out.put("effectiveTickMillis",interval(humans,override));out.put("speedSource",override==null?"NATIVE":"CLI_OVERRIDE");out.put("tickOverrideMillis",override);out.put("dateHasHumanMatch",humans);
        List<String> supported=new ArrayList<String>();Map<String,Object> unavailable=new LinkedHashMap<String,Object>(),boundaries=new LinkedHashMap<String,Object>(),limits=new LinkedHashMap<String,Object>();
        for(Adapter adapter:ADAPTERS.values()){if(adapter.unavailable==null)supported.add(adapter.key);else unavailable.put(adapter.key,adapter.unavailable);if(pending.containsKey(adapter.key))boundaries.put(adapter.key,adapter.boundary);if(adapter.kind.equals("integer"))limits.put(adapter.key,adapter.key.endsWith("SpeedIndex")?maxSpeed:adapter.max);}
        out.put("supportedKeys",supported);out.put("unavailableReasons",unavailable);out.put("pendingApplyAt",boundaries);out.put("nativeLimits",limits);
        out.put("catalogVersion",1);List<Object> countries=new ArrayList<Object>();for(Object raw:c.a.SR.N()){best.Z country=(best.Z)raw;Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",countryId(country));row.put("label",country.jf());countries.add(row);}out.put("leagueCountries",countries);
        String[] labels={"Liga Camp. Europa","Libertadores","Liga Camp. África","Liga Camp. Ásia","Liga Camp. Concacaf","Liga Camp. Oceania","Liga Europa","Copa Sul-Americana","Copa Nacional","Estadual","Regional","Copa do Mundo","Recopa","Super Copa","Conference League","Torneio amistoso","Copa de Seleções","Eliminatórias","Liga de Nações","Mundial clubes","Finalíssima"};List<Object> competitions=new ArrayList<Object>();for(int i=0;i<labels.length;i++){Map<String,Object> row=new LinkedHashMap<String,Object>();row.put("id",String.valueOf(i));row.put("label",labels[i]);competitions.add(row);}out.put("nonHumanCompetitions",competitions);return out;
    }
    static final class Conflict extends IllegalStateException {final String code;Conflict(String code,String message){super(message);this.code=code;}}
}
