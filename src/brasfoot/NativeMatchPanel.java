package brasfoot;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import best.I;
import best.A;
import best.F;
import best.ah;

/** Original Brasfoot match panel, with all local simulation/navigation disabled. */
public final class NativeMatchPanel extends a.dV {
    private static final long serialVersionUID=1L;
    private final Set<I> hiddenAttendance=Collections.newSetFromMap(new IdentityHashMap<I,Boolean>());
    private final Set<String> received=new HashSet<String>();
    private final java.util.List<I> matches;
    private boolean mute;
    private String lastStatus="NOT_STARTED";
    private boolean first=true;
    private boolean intervalSounded;
    private int lastHalf=1;
    private final java.util.List<Integer> sounds=new ArrayList<Integer>();
    private final java.util.List<Integer> playedSounds=new ArrayList<Integer>();
    private static final java.util.List<javax.swing.Timer> pendingAudio=new ArrayList<javax.swing.Timer>();
    private static long audioTailMillis,audioGeneration;
    private Consumer<I> lineupOpener;
    public NativeMatchPanel(JFrame frame,java.util.List<I> matches) {
        super(frame,720);this.matches=matches;
        ArrayList<best.at> competitions=new ArrayList<best.at>();
        for(I match:matches)if(!competitions.contains(match.hy()))competitions.add(match.hy());
        Y(competitions);
        if(GO!=null)GO.stop();
        disableLocalActions(this);
        for(String key:new String[]{"Hp","Ho"}){JLabel team=(JLabel)Data.get(this,key);team.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));team.setToolTipText("Abrir escalação");team.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(!matches.isEmpty() && lineupOpener!=null)lineupOpener.accept(matches.get(0));}});}
        JTable table=(JTable)Data.get(this,"Hs");table.setDefaultRenderer(components.v.class,new b.aG(){@Override public Component getTableCellRendererComponent(JTable grid,Object value,boolean selected,boolean focused,int row,int column){Component rendered=super.getTableCellRendererComponent(grid,value,selected,focused,row,column);if(column==0&&value instanceof components.v){I fixture=((components.v)value).tR();if(hiddenAttendance.contains(fixture))setText((fixture.ev()==null?fixture.ik():fixture.ev().dS())+" | --");}return rendered;}});table.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){int row=table.rowAtPoint(event.getPoint());if(row<0 || row>=qi().size() || lineupOpener==null)return;I selected=((components.v)qi().get(row)).tR();if(selected!=null)lineupOpener.accept(selected);}});
    }
    public void lineupOpener(Consumer<I> opener){lineupOpener=opener;}
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
    public void highlightClub(int id){
        for(int index=0;index<qi().size();index++){
            I match=((components.v)qi().get(index)).tR();
            if(match!=null && (match.hc().lk()==id || match.hd().lk()==id)){
                Data.set(this,"GW",index);pZ();break;
            }
        }
    }
    public void supportedClub(int id){highlightClub(id);}
    public void muted(boolean value){mute=value;c.a.SR.setUsaSons(!value);if(value){stopAudio();a.dV.qb();}}
    private void sound(int index){sounds.add(index);if(!mute){playedSounds.add(index);dk(index);}}
    private void delayedSound(int index){sounds.add(index);if(mute)return;long now=System.currentTimeMillis();audioTailMillis=Math.max(now+2000L,audioTailMillis+2000L);int delay=(int)Math.min(Integer.MAX_VALUE,audioTailMillis-now);long generation=audioGeneration;final javax.swing.Timer timer=new javax.swing.Timer(delay,event->{pendingAudio.remove((javax.swing.Timer)event.getSource());if(!mute && generation==audioGeneration && c.a.SR.isUsaSons()){playedSounds.add(index);dk(index);}if(pendingAudio.isEmpty())audioTailMillis=0L;});pendingAudio.add(timer);timer.setRepeats(false);timer.start();}
    public void stopAudio(){audioGeneration++;for(javax.swing.Timer timer:new ArrayList<javax.swing.Timer>(pendingAudio))timer.stop();pendingAudio.clear();audioTailMillis=0L;}
    public java.util.List<Integer> emittedSounds(){return new ArrayList<Integer>(sounds);}
    public java.util.List<Integer> playedSounds(){return new ArrayList<Integer>(playedSounds);}
    private static F player(ah club,Map<String,Object> event){
        if(event.containsKey("playerId")){if(event.get("playerId")==null)return null;int id=Data.number(event.get("playerId"));for(Object object:club.kc()){F player=(F)object;if(player.gD()==id)return player;}return null;}
        String name=(String)event.get("player");for(Object object:club.kc()){F player=(F)object;if(player.getNome().equals(name))return player;}return null;
    }
    private static F player(ah club,int id){for(Object object:club.kc()){F player=(F)object;if(player.gD()==id)return player;}return null;}
    @SuppressWarnings("unchecked")
    public void project(Map<String,Object> state){
        Map<String,Object> live=Data.map(state.get("playback"));
        String status=(String)live.get("status");
        // The first payload is a baseline; its historical event effects must not play.
        boolean replayEvents=!first;
        java.util.List<Object> rows=new ArrayList<Object>();
        for(I match:matches)for(Object raw:Data.list(live.get("matches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("homeId"))==match.hc().lk() && Data.number(row.get("awayId"))==match.hd().lk()){rows.add(row);break;}}
        for(int index=0;index<rows.size() && index<matches.size();index++){
            Map<String,Object> row=Data.map(rows.get(index));I match=matches.get(index);
            Data.set(match,"fB",Data.number(row.get("homeGoals")));Data.set(match,"fC",Data.number(row.get("awayGoals")));
            match.a(Boolean.TRUE.equals(row.get("played")));
            if(row.containsKey("attendance")){if(row.get("attendance")==null)hiddenAttendance.add(match);else hiddenAttendance.remove(match);Data.set(match,"fD",new int[]{row.get("attendance")==null?0:Data.number(row.get("attendance")),0,0,0});}
            if(row.containsKey("penalties")){java.util.List<Object> pen=Data.list(row.get("penalties"));match.i(new int[]{Data.number(pen.get(0)),Data.number(pen.get(1))});}
            components.v wrapper=null;
            for(Object object:qi())if(((components.v)object).tR()==match){wrapper=(components.v)object;break;}
            if(wrapper==null)continue;
            Data.set(wrapper,"Pq",match.hu());Data.set(wrapper,"Pr",match.hw());
            wrapper.tW();
            for(Object raw:Data.list(row.get("events"))){
                Map<String,Object> event=Data.map(raw);
                if(event.get("clubId")==null)continue;
                int clubId=Data.number(event.get("clubId"));ah club=clubId==match.hc().lk()?match.hc():clubId==match.hd().lk()?match.hd():null;
                if(club==null)continue;
                int type=Data.number(event.get("type"));
                F actor=player(club,event);
                F other=event.get("player2Id")==null?null:player(club,Data.number(event.get("player2Id")));
                if(actor==null || (type==6 && other==null))continue;
                String key=row.get("id")+":"+event.get("id");if(!received.add(key))continue;
                A original=new A(club==match.hc()?0:1);original.k(club);original.a(type);
                original.R(Data.number(event.get("detail")));original.T(Data.number(event.get("half")));
                original.S(Data.number(event.get("minute"))-(original.en()==2?45:0));
                original.f(actor);if(other!=null)original.g(other);original.p(true);
                match.hE().add(original);wrapper.a(original);
                if(index==0){JLabel label=(JLabel)Data.get(this,club==match.hc()?"Hj":"Hk");label.setText(original.er());label.setIcon(original.es());}
                if(replayEvents && wrapper.ei()){
                    int originalType=original.b();boolean humanScorer=Boolean.TRUE.equals(club.jZ());
                    if(originalType==1){if(original.el()==3){sound(4);delayedSound(humanScorer?2:3);}else sound(humanScorer?2:3);}
                    else if(originalType==3 || originalType==4)sound(5);
                    else if(originalType==5)sound(6);
                    else if(originalType==7){sound(4);delayedSound(humanScorer?3:2);}
                }
            }
        }
        if(!rows.isEmpty()){
            Map<String,Object> row=Data.map(rows.get(0));
            I featured=matches.get(0);
            ((JLabel)Data.get(this,"Hp")).setText(featured.hc().getNome()+" ");((JLabel)Data.get(this,"Ho")).setText(" "+featured.hd().getNome());
            ((JLabel)Data.get(this,"Hp")).setBackground(featured.hc().kB());((JLabel)Data.get(this,"Hp")).setForeground(featured.hc().kC());
            ((JLabel)Data.get(this,"Ho")).setBackground(featured.hd().kB());((JLabel)Data.get(this,"Ho")).setForeground(featured.hd().kC());
            ((JLabel)Data.get(this,"Hh")).setIcon(featured.hc().kP());((JLabel)Data.get(this,"Hi")).setIcon(featured.hd().kP());
            if(row.containsKey("attendance"))((JLabel)Data.get(this,"Hm")).setText(row.get("attendance")==null?"--":String.valueOf(row.get("attendance")));
            if(row.containsKey("phaseLabel"))((JLabel)Data.get(this,"Ht")).setText((String)row.get("phaseLabel"));
            ((JLabel)Data.get(this,"Hn")).setText(row.get("homeGoals")+" x "+row.get("awayGoals"));
            String text=status.equals("FINISHED")?"Fim de jogo":status.equals("HALF_TIME")?"Intervalo":status.equals("FAILED")?"Partida interrompida":status.equals("NOT_STARTED")?"Aguardando os técnicos":live.get("minute")+"' - "+live.get("half")+"º tempo";
            ((JLabel)Data.get(this,"uh")).setText(text);
            JProgressBar progress=(JProgressBar)Data.get(this,"Hf");progress.setMaximum(45);progress.setValue(Math.min(45,Data.number(live.get("halfMinute"))));
            ((JLabel)Data.get(this,"xI")).setText(Boolean.FALSE.equals(row.get("statisticsAvailable"))?"Estatísticas disponíveis ao fim da partida":"<html>Posse: "+Data.list(row.get("possession")).get(0)+"% / "+Data.list(row.get("possession")).get(1)+"%<br>Finalizações: "+Data.list(row.get("shots")).get(0)+" / "+Data.list(row.get("shots")).get(1)+"</html>");
        }
        int observedHalf=Data.number(live.get("half"));
        boolean humanMatch=false;for(Object object:qi())if(((components.v)object).ei()){humanMatch=true;break;}
        if(first){
            // The first payload is a baseline. If it already crossed halftime, that transition is history.
            intervalSounded=observedHalf>=2 || status.equals("HALF_TIME") || status.equals("FINISHED");
            lastStatus=status;lastHalf=observedHalf;first=false;
        }else{
            boolean enteredHalftime=status.equals("HALF_TIME") && !lastStatus.equals("HALF_TIME") && lastHalf==1;
            boolean enteredSecondHalf=status.equals("SECOND_HALF") && lastStatus.equals("FIRST_HALF") && lastHalf==1 && observedHalf>=2;
            if(!intervalSounded && humanMatch && (enteredHalftime || enteredSecondHalf) && c.a.SR.getVerJanelaSubs()==1){sound(0);intervalSounded=true;}
            if(humanMatch && status.equals("FINISHED") && !lastStatus.equals("FINISHED")){if(pendingAudio.isEmpty())sound(1);else delayedSound(1);}
            lastStatus=status;lastHalf=observedHalf;
        }
        ((AbstractTableModel)((JTable)Data.get(this,"Hs")).getModel()).fireTableDataChanged();repaint();
    }
}
