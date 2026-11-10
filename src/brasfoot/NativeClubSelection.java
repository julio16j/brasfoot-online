package brasfoot;

import best.ah;
import best.al;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Reuses Brasfoot's original country, division and club registration screen. */
public final class NativeClubSelection {
    public static final class CoachChoice {
        public final int clubId;
        public final String clubName;
        public final String clubAsset;
        public final String coachName;
        public final int nationality;
        CoachChoice(int clubId,String clubName,String clubAsset,String coachName,int nationality){this.clubId=clubId;this.clubName=clubName;this.clubAsset=clubAsset;this.coachName=coachName;this.nationality=nationality;}
    }
    private NativeClubSelection() { }

    public static List<CoachChoice> open(JFrame owner,File root)throws Exception {
        CareerEngine.initializeClubSelection(root);resetCounter();
        a.bV original=new a.bV(owner);
        JDialog dialog=new JDialog(owner,"Nova carreira multiplayer",true);dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel shell=new JPanel(new BorderLayout(8,8));shell.add(new JLabel("Use os seletores originais de país, divisão e clube. Adicione de 1 a 16 técnicos."),BorderLayout.NORTH);
        shell.add(original,BorderLayout.CENTER);
        JPanel footer=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton cancel=new JButton("Cancelar");footer.add(cancel);shell.add(footer,BorderLayout.SOUTH);
        dialog.setContentPane(shell);dialog.setSize(1080,790);dialog.setMinimumSize(new Dimension(950,700));dialog.setLocationRelativeTo(owner);
        List<CoachChoice>[] result=new List[]{null};
        JButton add=(JButton)Data.get(original,"Bx"),start=(JButton)Data.get(original,"Bw");
        JTextField name=(JTextField)Data.get(original,"uf");JLabel heading=(JLabel)Data.get(original,"vf");
        for(ActionListener listener:add.getActionListeners())add.removeActionListener(listener);
        add.addActionListener(event->{try{String error=original.oo();if(error!=null){warn(dialog,error);return;}if(c.a.SR.M().size()>=16){warn(dialog,"A carreira aceita no máximo 16 técnicos.");return;}original.or();setCounter(counter()+1);heading.setText("Adicionar técnico humano "+counter());name.setText("");invoke(original,"ot");}catch(Exception failure){warn(dialog,failure.getMessage());}});
        for(ActionListener listener:start.getActionListeners())start.removeActionListener(listener);
        start.setText("Iniciar carreira online");
        start.addActionListener(event->{try{
            if(!name.getText().trim().isEmpty()){
                String error=original.oo();if(error!=null){warn(dialog,error);return;}
                if(c.a.SR.M().size()>=16){warn(dialog,"A carreira aceita no máximo 16 técnicos.");return;}
                original.or();
            }
            List<CoachChoice> selected=new ArrayList<CoachChoice>();
            for(Object raw:c.a.SR.M()){al coach=(al)raw;ah club=coach.fg();if(club!=null)selected.add(new CoachChoice(club.lk(),club.getNome(),club.jY(),coach.dS(),coach.lE()));}
            if(selected.isEmpty()){warn(dialog,"Adicione pelo menos um técnico antes de iniciar.");return;}
            result[0]=selected;dialog.dispose();
        }catch(Exception failure){warn(dialog,failure.getMessage());}});
        cancel.addActionListener(event->dialog.dispose());dialog.setVisible(true);return result[0];
    }

    private static void warn(JDialog dialog,String text){JOptionPane.showMessageDialog(dialog,text,"Brasfoot Online",JOptionPane.WARNING_MESSAGE);}
    private static int counter()throws Exception{Field field=a.bV.class.getDeclaredField("count");field.setAccessible(true);return field.getInt(null);}
    private static void setCounter(int value)throws Exception{Field field=a.bV.class.getDeclaredField("count");field.setAccessible(true);field.setInt(null,value);}
    private static void resetCounter()throws Exception{setCounter(1);}
    private static void invoke(Object target,String name)throws Exception{Method method=target.getClass().getDeclaredMethod(name);method.setAccessible(true);method.invoke(target);}
}
