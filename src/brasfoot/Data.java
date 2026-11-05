package brasfoot;
import java.lang.reflect.Field;
import java.util.*;
import javax.script.*;
/** JSON is parsed as data by Java 8's bundled engine; no response is evaluated as code. */
public final class Data {
    private Data() { }
    private static final ThreadLocal<ScriptEngine> ENGINES=new ThreadLocal<ScriptEngine>(){
        @Override protected ScriptEngine initialValue(){return new ScriptEngineManager().getEngineByName("nashorn");}
    };
    @SuppressWarnings("unchecked") public static Map<String,Object> map(Object value){return (Map<String,Object>)value;}
    @SuppressWarnings("unchecked") public static List<Object> list(Object value){return (List<Object>)value;}
    public static int number(Object value){return ((Number)value).intValue();}
    public static long longNumber(Object value){return ((Number)value).longValue();}
    public static Object get(Object target,String name){
        try{Field f=target.getClass().getSuperclass()==a.dV.class?a.dV.class.getDeclaredField(name):target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}
        catch(Exception e){throw new IllegalStateException("Original game field unavailable: "+name,e);}
    }
    public static void set(Object target,String name,Object value){
        try{Class<?> type=target.getClass();Field field=null;while(type!=null){try{field=type.getDeclaredField(name);break;}catch(NoSuchFieldException e){type=type.getSuperclass();}}if(field==null)throw new NoSuchFieldException(name);field.setAccessible(true);field.set(target,value);}
        catch(Exception e){throw new IllegalStateException("Cannot project original field: "+name,e);}
    }
    public static Map<String,Object> parse(String json) throws ScriptException {
        ScriptEngine engine=ENGINES.get();
        if(engine==null)throw new IllegalStateException("The graphical client requires Java 8 with Nashorn");
        engine.put("payload",json);
        return map(engine.eval("Java.asJSONCompatible(JSON.parse(payload))"));
    }
    public static String csv(List<?> values){List<String> result=new ArrayList<String>();for(Object value:values)result.add(String.valueOf(number(value)));return String.join(",",result);}
}
