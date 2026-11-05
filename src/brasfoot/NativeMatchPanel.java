package brasfoot;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import best.I;
import best.A;
import best.F;
import best.ah;

/** Original Brasfoot match panel, with all local simulation/navigation disabled. */
public final class NativeMatchPanel extends a.dV {
    private static final long serialVersionUID=1L;
    private final Set<String> received=new HashSet<String>();
    private final java.util.List<I> matches;
    private boolean mute;
    private int supportedClub=0;
    private String lastStatus="NOT_STARTED";
    private boolean first=true;
    private boolean intervalSounded;
    private final java.util.List<Integer> sounds=new ArrayList<Integer>();
    public NativeMatchPanel(JFrame frame,java.util.List<I> matches) {
        super(frame,720);this.matches=matches;
        ArrayList<best.at> competitions=new ArrayList<best.at>();
        for(I match:matches)if(!competitions.contains(match.hy()))competitions.add(match.hy());
        Y(competitions);
        if(GO!=null)GO.stop();
        disableLocalActions(this);
    }
    @Override public void oR(){if(GO!=null)GO.stop();}
    @Override public void qa(){ }
    @Override public void aa(boolean value){ }
    @Override public void qg(){ }
    @Override public void qe(){ }
    private static void disableLocalActions(Component component){
        for(MouseListener listener:component.getMouseListeners())component.removeMouseListener(listener);
        if(component instanceof JComponent){JComponent c=(JComponent)component;c.getActionMap().clear();for(int condition:new int[]{0,1,2})c.getInputMap(condition).clear();}
        if(component instanceof Container)for(Component child:((Container)component).getComponents())disableLocalActions(child);
    }
    public void supportedClub(int id){supportedClub=id;}
    public void muted(boolean value){mute=value;c.a.SR.setUsaSons(!value);if(value)a.dV.qb();}
    private void sound(int index){sounds.add(index);if(!mute)dk(index);}
    public java.util.List<Integer> emittedSounds(){return new ArrayList<Integer>(sounds);}
    private static F player(ah club,String name){for(Object object:club.kc()){F player=(F)object;if(player.getNome().equals(name))return player;}return null;}
    @SuppressWarnings("unchecked")
    public void project(Map<String,Object> state){
        Map<String,Object> live=Data.map(state.get("playback"));
        String status=(String)live.get("status");
        // Do not replay old goal sounds when connecting/reconnecting to an ongoing match.
        boolean replay=!first;
        java.util.List<Object> rows=Data.list(live.get("matches"));
        for(int index=0;index<rows.size() && index<matches.size();index++){
            Map<String,Object> row=Data.map(rows.get(index));I match=matches.get(index);
            Data.set(match,"fB",Data.number(row.get("homeGoals")));Data.set(match,"fC",Data.number(row.get("awayGoals")));
            match.a(Boolean.TRUE.equals(row.get("played")));
            components.v wrapper=null;
            for(Object object:qi())if(((components.v)object).tR()==match){wrapper=(components.v)object;break;}
            if(wrapper==null)continue;
            Data.set(wrapper,"Pq",match.hu());Data.set(wrapper,"Pr",match.hw());
            for(Object raw:Data.list(row.get("events"))){
                Map<String,Object> event=Data.map(raw);String key=index+":"+event.get("id");
                if(!received.add(key))continue;
                ah club=match.hc().getNome().equals(event.get("club"))?match.hc():match.hd();
                A original=new A(club==match.hc()?0:1);original.k(club);original.a(Data.number(event.get("type")));
                original.R(Data.number(event.get("detail")));original.T(Data.number(event.get("half")));
                original.S(Data.number(event.get("minute"))-(original.en()==2?45:0));
                original.f(player(club,(String)event.get("player")));original.p(true);
                match.hE().add(original);wrapper.a(original);
                if(index==0){JLabel label=(JLabel)Data.get(this,club==match.hc()?"Hj":"Hk");label.setText(original.er());label.setIcon(original.es());}
                if(replay){int type=original.b();if(type==1){if(original.el()==3)sound(4);sound(club.lk()==supportedClub?2:3);}else if(type==3 || type==4)sound(5);else if(type==5)sound(6);else if(type==7)sound(4);}
            }
        }
        if(!rows.isEmpty()){
            Map<String,Object> row=Data.map(rows.get(0));
            ((JLabel)Data.get(this,"Hn")).setText(row.get("homeGoals")+" x "+row.get("awayGoals"));
            String text=status.equals("FINISHED")?"Fim de jogo":status.equals("HALF_TIME")?"Intervalo":status.equals("FAILED")?"Partida interrompida":status.equals("NOT_STARTED")?"Aguardando os técnicos":live.get("minute")+"' - "+live.get("half")+"º tempo";
            ((JLabel)Data.get(this,"uh")).setText(text);
            JProgressBar progress=(JProgressBar)Data.get(this,"Hf");progress.setMaximum(45);progress.setValue(Math.min(45,Data.number(live.get("halfMinute"))));
            ((JLabel)Data.get(this,"xI")).setText("<html>Posse: "+Data.list(row.get("possession")).get(0)+"% / "+Data.list(row.get("possession")).get(1)+"%<br>Finalizações: "+Data.list(row.get("shots")).get(0)+" / "+Data.list(row.get("shots")).get(1)+"</html>");
        }
        if(replay && !status.equals(lastStatus)){if(!intervalSounded && (status.equals("HALF_TIME") || Data.number(live.get("half"))==2)){sound(0);intervalSounded=true;}if(status.equals("FINISHED"))sound(1);}
        lastStatus=status;first=false;
        ((AbstractTableModel)((JTable)Data.get(this,"Hs")).getModel()).fireTableDataChanged();repaint();
    }
}
