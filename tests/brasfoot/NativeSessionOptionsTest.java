package brasfoot;

import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class NativeSessionOptionsTest {
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        Path preferences=root.resolve("game/Brasfoot22-23/options.bcf");byte[] before=Files.exists(preferences)?Files.readAllBytes(preferences):null;
        SwingUtilities.invokeAndWait(()->{JFrame owner=new JFrame();int[] commands={0};SessionOptions options=SessionOptions.load(null);NativeSessionOptions dialog=new NativeSessionOptions(owner,options.snapshot(false,true,null),changes->commands[0]++);
            a.fB panel=(a.fB)dialog.getContentPane().getComponent(0);if(((JComboBox)Data.get(panel,"Jp")).isEnabled())throw new AssertionError("Guest speed editable");if(((JLabel)Data.get(panel,"Jr")).isEnabled())throw new AssertionError("Guest theme editable");
            dialog.update(options.snapshot(true,true,25L),false);if(((JComboBox)Data.get(panel,"Jp")).isEnabled())throw new AssertionError("Override speed editable");if(!((JCheckBox)Data.get(panel,"IV")).isEnabled())throw new AssertionError("Host audio disabled");
            ((JCheckBox)Data.get(panel,"IV")).doClick();if(commands[0]!=1)throw new AssertionError("Command not routed");dialog.update(options.snapshot(true,true,null),true);if(((JCheckBox)Data.get(panel,"IV")).isEnabled())throw new AssertionError("Request allows concurrent editing");if(commands[0]!=1)throw new AssertionError("Projection sent command");dialog.dispose();owner.dispose();});
        byte[] after=Files.exists(preferences)?Files.readAllBytes(preferences):null;if(!Arrays.equals(before,after))throw new AssertionError("Client wrote local preferences");
        System.out.println("PASS native options layout, host and guest authority, CLI override, pending request and no preference-file writes");
    }
}
