package brasfoot;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public final class SessionApi {
    private static final int MAX_RESPONSE_BYTES=32*1024*1024;
    private final String address;
    private String token;
    public SessionApi(String address){String value=address.trim();if(!value.startsWith("http://") && !value.startsWith("https://"))value="http://"+value;this.address=value.replaceAll("/+$","");}
    public void token(String value){token=value;}
    public Map<String,Object> call(String path,Map<String,String> form)throws Exception{
        HttpURLConnection connection=(HttpURLConnection)new URL(address+path).openConnection();
        connection.setConnectTimeout(4000);connection.setReadTimeout(6000);connection.setInstanceFollowRedirects(false);
        if(token!=null)connection.setRequestProperty("Authorization","Bearer "+token);
        if(form!=null){connection.setRequestMethod("POST");connection.setDoOutput(true);connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
            java.util.List<String> fields=new ArrayList<String>();for(Map.Entry<String,String> e:form.entrySet())fields.add(URLEncoder.encode(e.getKey(),"UTF-8")+"="+URLEncoder.encode(e.getValue(),"UTF-8"));
            byte[] bytes=String.join("&",fields).getBytes(StandardCharsets.UTF_8);connection.setFixedLengthStreamingMode(bytes.length);try(OutputStream out=connection.getOutputStream()){out.write(bytes);}
        }
        try{
            int code=connection.getResponseCode();if(connection.getContentLengthLong()>MAX_RESPONSE_BYTES)throw new IOException("Resposta excede o limite de 32 MiB");InputStream input=code>=400?connection.getErrorStream():connection.getInputStream();
            if(input==null)throw new IOException("Host respondeu HTTP "+code);
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream in=input){byte[] buffer=new byte[4096];int n;while((n=in.read(buffer))!=-1){if(bytes.size()+n>MAX_RESPONSE_BYTES)throw new IOException("Resposta excede o limite de 32 MiB");bytes.write(buffer,0,n);}}
            Map<String,Object> result=Data.parse(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
            if(code>=400)throw new IOException(String.valueOf(result.get("error")));return result;
        }finally{connection.disconnect();}
    }
}
