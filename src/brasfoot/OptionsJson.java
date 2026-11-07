package brasfoot;

import java.util.*;

/** Strict data parser, including duplicate-key rejection at every nesting level. */
final class OptionsJson {
    private final String input;private int pos,depth;
    private OptionsJson(String input){this.input=input;}
    static Map<String,Object> parse(String input){OptionsJson parser=new OptionsJson(input);Object value=parser.value();parser.space();if(parser.pos!=input.length() || !(value instanceof Map))throw new IllegalArgumentException("Expected a JSON object");return Data.map(value);}
    private void space(){while(pos<input.length() && " \t\r\n".indexOf(input.charAt(pos))>=0)pos++;}
    private boolean take(char c){space();if(pos<input.length() && input.charAt(pos)==c){pos++;return true;}return false;}
    private void expect(char c){if(!take(c))throw new IllegalArgumentException("Invalid options JSON");}
    private Object value(){space();if(++depth>16 || pos>=input.length())throw new IllegalArgumentException("Invalid options JSON");Object result;char c=input.charAt(pos);
        if(c=='{'){pos++;Map<String,Object> map=new LinkedHashMap<String,Object>();if(!take('}')){do{space();String key=string();expect(':');if(map.containsKey(key))throw new IllegalArgumentException("Duplicate JSON key: "+key);map.put(key,value());}while(take(','));expect('}');}result=map;}
        else if(c=='['){pos++;List<Object> list=new ArrayList<Object>();if(!take(']')){do{list.add(value());}while(take(','));expect(']');}result=list;}
        else if(c=='"')result=string();
        else if(input.startsWith("true",pos)){pos+=4;result=true;}
        else if(input.startsWith("false",pos)){pos+=5;result=false;}
        else if(input.startsWith("null",pos)){pos+=4;result=null;}
        else {int start=pos;while(pos<input.length() && "-+0123456789.eE".indexOf(input.charAt(pos))>=0)pos++;String number=input.substring(start,pos);if(!number.matches("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?"))throw new IllegalArgumentException("Invalid JSON number");try{result=new java.math.BigDecimal(number);}catch(NumberFormatException error){throw new IllegalArgumentException("Invalid JSON number");}}
        depth--;return result;
    }
    private String string(){if(pos>=input.length() || input.charAt(pos++)!='"')throw new IllegalArgumentException("Expected JSON string");StringBuilder out=new StringBuilder();while(pos<input.length()){char c=input.charAt(pos++);if(c=='"')return out.toString();if(c<32)throw new IllegalArgumentException("Invalid JSON string");if(c=='\\'){if(pos>=input.length())break;c=input.charAt(pos++);switch(c){case '"':case '\\':case '/':break;case 'b':c='\b';break;case 'f':c='\f';break;case 'n':c='\n';break;case 'r':c='\r';break;case 't':c='\t';break;case 'u':if(pos+4>input.length())throw new IllegalArgumentException("Invalid JSON escape");try{c=(char)Integer.parseInt(input.substring(pos,pos+4),16);}catch(NumberFormatException e){throw new IllegalArgumentException("Invalid JSON escape");}pos+=4;break;default:throw new IllegalArgumentException("Invalid JSON escape");}}out.append(c);}throw new IllegalArgumentException("Unterminated JSON string");}
}
