package brasfoot;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/** The original five-swatch editor, with a local draft and an online-only commit. */
final class NativeOptionsPalette extends JDialog {
    private final NativeSessionOptions owner;
    private final long revision;
    private final JPanel panel;
    private final JLabel[] swatches=new JLabel[5];
    private final JButton confirm,reset;
    private final JLabel notice=new JLabel();
    private boolean stale;
    NativeOptionsPalette(NativeSessionOptions owner,java.util.List<Object> colors){
        super(owner,"Cores da lista",true);this.owner=owner;revision=owner.revision();
        try{panel=(JPanel)Class.forName("a.aX").getConstructor(JDialog.class).newInstance(this);}catch(Exception error){throw new IllegalStateException("Editor nativo de cores indisponível",error);}
        setUndecorated(true);setResizable(false);setLayout(new BorderLayout());add(panel);
        String[] fields={"zo","zp","zq","zr","zs"};
        for(int i=0;i<fields.length;i++){JLabel sample=(JLabel)Data.get(panel,fields[i]);swatches[i]=sample;sample.setBackground(Color.decode(String.valueOf(colors.get(i))));for(MouseListener listener:sample.getMouseListeners())sample.removeMouseListener(listener);sample.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(!sample.isEnabled())return;Color chosen=JColorChooser.showDialog(NativeOptionsPalette.this,"Cor",sample.getBackground());if(chosen!=null && sample.isEnabled())sample.setBackground(chosen);}});}
        confirm=(JButton)Data.get(panel,"zn");reset=(JButton)Data.get(panel,"zm");
        for(ActionListener listener:confirm.getActionListeners())confirm.removeActionListener(listener);
        confirm.addActionListener(event->confirmDraft());
        JPanel footer=new JPanel(new BorderLayout());footer.add(notice);JButton close=new JButton("Fechar");close.addActionListener(event->dispose());footer.add(close,BorderLayout.SOUTH);add(footer,BorderLayout.SOUTH);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"cancel");getRootPane().getActionMap().put("cancel",new AbstractAction(){public void actionPerformed(ActionEvent event){dispose();}});
        setSize(300,420);setLocationRelativeTo(owner);updateAuthority();
    }
    void updateAuthority(){stale|=revision!=owner.revision();boolean editable=!stale && owner.canEdit("playerListPalette");confirm.setEnabled(editable);reset.setEnabled(editable);for(JLabel swatch:swatches)swatch.setEnabled(editable);notice.setText(stale?"Opções alteradas; feche e abra as cores novamente.":editable?"Alterações enviadas ao confirmar.":"Edição indisponível.");}
    void confirmDraft(){updateAuthority();if(!confirm.isEnabled())return;java.util.List<String> colors=new ArrayList<String>();for(JLabel sample:swatches)colors.add(String.format("#%06X",sample.getBackground().getRGB()&0xffffff));owner.send("playerListPalette",colors);dispose();}
}
