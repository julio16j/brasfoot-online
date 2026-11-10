package brasfoot;
import com.sun.net.httpserver.*;import java.net.*;import java.io.*;import java.util.*;
public final class SessionApiResponseLimitTest{
 public static void main(String[] args)throws Exception{
  HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);server.createContext("/large",exchange->{byte[] body=new byte[9*1024*1024];Arrays.fill(body,(byte)'x');byte[] prefix="{\"data\":\"".getBytes("UTF-8"),suffix="\"}".getBytes("UTF-8");exchange.sendResponseHeaders(200,prefix.length+body.length+suffix.length);try(OutputStream out=exchange.getResponseBody()){out.write(prefix);out.write(body);out.write(suffix);}});
  server.createContext("/oversize",exchange->{exchange.sendResponseHeaders(200,33L*1024*1024);try{exchange.getResponseBody().close();}catch(IOException ignored){}});
  server.createContext("/chunked",exchange->{exchange.sendResponseHeaders(200,0);try(OutputStream out=exchange.getResponseBody()){byte[] chunk=new byte[1024*1024];for(int i=0;i<33;i++)out.write(chunk);}catch(IOException expected){}});
  server.start();try{SessionApi api=new SessionApi("127.0.0.1:"+server.getAddress().getPort());if(String.valueOf(api.call("/large",null).get("data")).length()!=9*1024*1024)throw new AssertionError("Large legitimate response was truncated");for(String path:new String[]{"/oversize","/chunked"}){try{api.call(path,null);throw new AssertionError("Unbounded response accepted: "+path);}catch(IOException expected){if(!expected.getMessage().contains("32 MiB"))throw expected;}}}finally{server.stop(0);}System.out.println("PASS HTTP accepts complete 9 MiB JSON and rejects declared/chunked responses above 32 MiB");
 }
}
