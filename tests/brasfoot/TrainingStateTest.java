package brasfoot;
import best.ah;
import java.util.*;
import javax.swing.*;

public final class TrainingStateTest {
    public static void main(String[] args)throws Exception{
        SwingUtilities.invokeAndWait(()->{
            ah club=new ah();club.bX(42);club.M(true);club.bZ(1);List<Map<String,String>> sent=new ArrayList<Map<String,String>>();Runnable[] success={null},failure={null};
            NativeTraining dialog=new NativeTraining(null,club,(fields,ok,failed)->{sent.add(fields);success[0]=ok;failure[0]=failed;});
            try{
                a.iQ panel=(a.iQ)Data.get(dialog,"panel");JCheckBox assistant=(JCheckBox)Data.get(panel,"MY");JRadioButton secondary=(JRadioButton)Data.get(panel,"MX"),primary=(JRadioButton)Data.get(panel,"MW");
                check(assistant.isSelected() && secondary.isSelected(),"Initial authoritative combination lost");
                assistant.doClick();check(sent.size()==1 && "false".equals(sent.get(0).get("enabled")),"No separate assistant intention");
                check(club.ll() && assistant.isSelected() && !assistant.isEnabled() && !primary.isEnabled(),"Optimistic local confirmation or concurrent edit");
                dialog.update(club,true);check(sent.size()==1 && !assistant.isEnabled(),"Projection sent request or ended pending state");
                failure[0].run();check(assistant.isSelected() && assistant.isEnabled(),"Failure did not restore confirmed state");
                assistant.doClick();club.M(false);dialog.update(club,true);success[0].run();check(!assistant.isSelected() && secondary.isSelected() && assistant.isEnabled(),"Confirmation lost focus or pending state");
                primary.doClick();check(sent.size()==3 && "training-focus".equals(sent.get(2).get("action")),"Manual focus not separated");check(club.lm()==1 && secondary.isSelected(),"Manual focus changed before confirmation");failure[0].run();
                dialog.update(club,false);check(!assistant.isEnabled() && !secondary.isEnabled(),"Unavailable phase remains editable");
                ah changed=new ah();changed.bX(43);dialog.update(changed,true);check(!dialog.isDisplayable(),"Club change retained stale dialog");
                check(sent.size()==3,"Projection or club change emitted command");
            }finally{dialog.dispose();}
        });
        System.out.println("PASS training pending, failure restoration, confirmed projection, phase and club changes");
    }
    private static void check(boolean c,String label){if(!c)throw new AssertionError(label);}
}
