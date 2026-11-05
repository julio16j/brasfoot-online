package brasfoot;
import java.nio.file.*;
import javax.sound.sampled.*;
public final class AudioSmoke {
 public static void main(String[] args)throws Exception{
  Path root=Paths.get(args[0]);int decoded=0,outputs=0;
  for(String name:new String[]{"intervalo","fimjogo","gol1","goladv","penalty","expulsao","contusao"}){
   try(AudioInputStream input=AudioSystem.getAudioInputStream(root.resolve("game/Brasfoot22-23/sons/"+name+".wav").toFile())){
    if(input.getFrameLength()<=0)throw new AssertionError("Empty sound: "+name);decoded++;
    try{Clip clip=AudioSystem.getClip();clip.open(input);outputs++;clip.close();}catch(LineUnavailableException | IllegalArgumentException error){System.out.println("Output unavailable for "+name+": "+error.getMessage());}
   }
  }
  System.out.println("AUDIO decoded="+decoded+" outputLinesOpened="+outputs);
 }
}
