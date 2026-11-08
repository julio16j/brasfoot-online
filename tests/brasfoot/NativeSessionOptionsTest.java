package brasfoot;

import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class NativeSessionOptionsTest {
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        Path preferences=root.resolve("game/Brasfoot22-23/options.bcf");byte[] before=Files.exists(preferences)?Files.readAllBytes(preferences):null;
        SwingUtilities.invokeAndWait(()->{JFrame owner=new JFrame();int[] commands={0};SessionOptions options=SessionOptions.load(null);
            java.util.List<String> actions=new ArrayList<String>();best.I fixture=(best.I)c.a.SR.S().get(0);
            JPanel preparation=NativePreparation.create(owner,fixture.hc(),fixture,()->{},actions::add);
            owner.add(preparation);owner.setSize(1100,850);owner.setLocation(-10000,-10000);owner.setVisible(true);
            JButton nativeOptions=(JButton)Data.get(preparation,"HX");JPopupMenu popup=(JPopupMenu)Data.get(preparation,"Hy");nativeOptions.doClick();
            if(!popup.isVisible() || !"Opções".equals(nativeOptions.getText()))throw new AssertionError("Native options menu not opened");
            JMenuItem preferencesItem=null;for(java.awt.Component child:popup.getComponents())if(child instanceof JMenuItem){JMenuItem item=(JMenuItem)child;if("Preferências".equals(item.getText()))preferencesItem=item;else if(!"Sobre o Brasfoot".equals(item.getText()) && item.isEnabled())throw new AssertionError("Offline action remains enabled");}
            if(preferencesItem==null || preferencesItem.getActionListeners().length!=1 || preferencesItem.getAccelerator()!=null)throw new AssertionError("Preferences route or accelerator duplicated");preferencesItem.doClick();popup.setVisible(false);
            if(!actions.equals(Arrays.asList("Opções")))throw new AssertionError("Native preferences did not route online");
            java.util.List<Map<String,Object>> sent=new ArrayList<Map<String,Object>>();NativeSessionOptions dialog=new NativeSessionOptions(owner,options.snapshot(false,true,null),changes->{commands[0]++;sent.add(changes);});
            a.fB panel=(a.fB)dialog.getContentPane().getComponent(0);if(((JComboBox)Data.get(panel,"Jp")).isEnabled())throw new AssertionError("Guest speed editable");if(((JLabel)Data.get(panel,"Jr")).isEnabled())throw new AssertionError("Guest theme editable");
            dialog.update(options.snapshot(true,true,25L),false);if(((JComboBox)Data.get(panel,"Jp")).isEnabled())throw new AssertionError("Override speed editable");if(!((JCheckBox)Data.get(panel,"IV")).isEnabled())throw new AssertionError("Host audio disabled");
            ((JCheckBox)Data.get(panel,"IV")).doClick();if(commands[0]!=1)throw new AssertionError("Command not routed");dialog.update(options.snapshot(true,true,null),true);if(((JCheckBox)Data.get(panel,"IV")).isEnabled())throw new AssertionError("Request allows concurrent editing");if(commands[0]!=1)throw new AssertionError("Projection sent command");
            dialog.update(options.snapshot(false,true,null),false);((JComboBox)Data.get(panel,"Jp")).setSelectedIndex(0);if(commands[0]!=1)throw new AssertionError("Disabled programmatic change sent command");
            dialog.update(options.snapshot(true,true,null),false);
            java.util.List<Object> colors=Data.list(Data.map(options.persisted().get("values")).get("playerListPalette"));
            NativeOptionsPalette draft=new NativeOptionsPalette(dialog,colors);JPanel nativePalette=(JPanel)draft.getContentPane().getComponent(0);
            if(!nativePalette.getClass().getName().equals("a.aX"))throw new AssertionError("Not native palette");
            ((JLabel)Data.get(nativePalette,"zo")).setBackground(java.awt.Color.RED);draft.confirmDraft();
            if(commands[0]!=2 || !Data.list(sent.get(1).get("playerListPalette")).get(0).equals("#FF0000"))throw new AssertionError("Palette not atomic online command");
            if(!String.valueOf(colors.get(0)).equals(String.format("#%06X",c.a.SR.getCoresLista()[0].getRGB()&0xffffff)))throw new AssertionError("Draft changed local native palette");
            dialog.update(options.snapshot(true,true,null),false);NativeOptionsPalette cancelled=new NativeOptionsPalette(dialog,colors);cancelled.dispose();if(commands[0]!=2)throw new AssertionError("Cancel sent command");
            NativeOptionsPalette stale=new NativeOptionsPalette(dialog,colors);Map<String,Object> advanced=options.snapshot(true,true,null);advanced.put("revision",options.revision()+1);dialog.update(advanced,false);stale.confirmDraft();if(commands[0]!=2)throw new AssertionError("Stale palette sent command");stale.dispose();
            String[][] bound={{"Jp","humanSpeedIndex"},{"Jq","nonHumanSpeedIndex"},{"Jm","openLineupAtHalfTime"},{"IO","humanScoreColorsEnabled"},{"IU","highlightHomeEvents"},{"IP","playerListColorsEnabled"},{"IV","soundsEnabled"}};
            for(String[] binding:bound){dialog.update(options.snapshot(true,true,null),false);Object control=Data.get(panel,binding[0]);int count=commands[0];if(control instanceof JComboBox){JComboBox combo=(JComboBox)control;combo.setSelectedIndex(combo.getSelectedIndex()==0?1:0);}else ((JCheckBox)control).doClick();if(commands[0]!=count+1 || !sent.get(sent.size()-1).containsKey(binding[1]))throw new AssertionError("Native binding failed: "+binding[1]);}
            for(String field:new String[]{"Eg","Js"}){dialog.update(options.snapshot(true,true,null),false);JLabel sample=(JLabel)Data.get(panel,field);int count=commands[0];sample.dispatchEvent(new java.awt.event.MouseEvent(sample,java.awt.event.MouseEvent.MOUSE_CLICKED,0,0,1,1,1,false));if(commands[0]!=count+1)throw new AssertionError("Native swatch did not send command");}
            Set<String> keys=new HashSet<String>();for(Map<String,Object> command:sent)keys.addAll(command.keySet());if(keys.size()!=10)throw new AssertionError("Not all ten supported bindings exercised: "+keys);
            dialog.update(options.snapshot(true,true,null),false);NativeOptionsPalette defaults=new NativeOptionsPalette(dialog,colors);JPanel defaultsPanel=(JPanel)defaults.getContentPane().getComponent(0);((JButton)Data.get(defaultsPanel,"zm")).doClick();java.awt.Color expectedDefault=new java.awt.Color(best.aq.oZ[0][0],best.aq.oZ[0][1],best.aq.oZ[0][2]);if(!((JLabel)Data.get(defaultsPanel,"zo")).getBackground().equals(expectedDefault))throw new AssertionError("Native palette reset not preserved");defaults.dispose();
            if(!dialog.isModal() || !dialog.isUndecorated())throw new AssertionError("Native shell not preserved");
            ((JButton)Data.get(panel,"vm")).doClick();owner.dispose();});
        byte[] after=Files.exists(preferences)?Files.readAllBytes(preferences):null;if(!Arrays.equals(before,after))throw new AssertionError("Client wrote local preferences");
        System.out.println("PASS native options layout, host and guest authority, CLI override, pending request and no preference-file writes");
    }
}
