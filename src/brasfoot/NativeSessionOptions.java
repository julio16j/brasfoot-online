package brasfoot;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Original fB layout, with online listeners only: no native preference-file writes. */
final class NativeSessionOptions extends JDialog {
    private final a.fB panel;
    private final Consumer<Map<String,Object>> submit;
    private final Map<String,JComponent> controls=new LinkedHashMap<String,JComponent>();
    private final JLabel notice=new JLabel();
    private Map<String,Object> settings;
    private boolean projecting,busy;
    private static int previousTheme=-1;
    NativeSessionOptions(JFrame owner,Map<String,Object> settings,Consumer<Map<String,Object>> submit){
        super(owner,"Configurações da sessão",false);this.submit=submit;
        try{
            // fB's constructor installs disk-writing listeners before selecting values.
            // Run JPanel's constructor and fB.mJ (layout only), then bind our own controls.
            Class<?> factoryType=Class.forName("sun.reflect.ReflectionFactory");Object factory=factoryType.getMethod("getReflectionFactory").invoke(null);
            Constructor<?> constructor=(Constructor<?>)factoryType.getMethod("newConstructorForSerialization",Class.class,Constructor.class).invoke(factory,a.fB.class,JPanel.class.getDeclaredConstructor());
            panel=(a.fB)constructor.newInstance();Method layout=a.fB.class.getDeclaredMethod("mJ");layout.setAccessible(true);layout.invoke(panel);
        }catch(Exception error){throw new IllegalStateException("Não foi possível abrir as configurações nativas",error);}
        setLayout(new BorderLayout());add(panel);add(notice,BorderLayout.SOUTH);
        combo("autoSaveMode","Jk",new String[]{"Não usar auto-save","A cada 3 jogos","A cada jogo","Início do ano"});
        String[] speeds={"Lento","Normal","Rápido","Bem rápido","Super rápido","Ultra rápido"};combo("humanSpeedIndex","Jp",speeds);combo("nonHumanSpeedIndex","Jq",speeds);
        combo("showNonHumanPenalties","Ji",new String[]{"Não","Sim"});combo("showCoachChanges","Jo",new String[]{"Não","Sim"});combo("openLineupAtHalfTime","Jm",new String[]{"Não","Sim"});combo("contractWarningMode","IW",new String[]{"Não","Após término","Faltando 1 mês"});
        String[][] checks={{"showPlayerAuctions","IL"},{"autoRenewExpiredContracts","IN"},{"humanScoreColorsEnabled","IO"},{"soundsEnabled","IV"},{"highlightHomeEvents","IU"},{"playerListColorsEnabled","IP"},{"groupStateCompetitions","IQ"},{"hideNonHumanNationalLeagues","IT"},{"hideStateRegionalOutsideBrazil","IR"},{"hideRegionalsWithoutHumanHistory","IS"}};
        for(String[] item:checks){JCheckBox check=(JCheckBox)Data.get(panel,item[1]);controls.put(item[0],check);check.addActionListener(e->send(item[0],check.isSelected()));}
        colorChoices("humanScoreColorIndex",new String[]{"Ef","Eg","Eh"},new String[]{"cor0","cor2","cor3"});colorChoices("windowThemeIndex",new String[]{"Jr","Js","Jt","Ju","Jv"},new String[]{"corTema0","corTema1","corTema2","corTema3","corTema4"});
        JButton palette=(JButton)Data.get(panel,"IM");controls.put("playerListPalette",palette);palette.addActionListener(e->{java.util.List<Object> colors=Data.list(Data.map(this.settings.get("values")).get("playerListPalette"));java.util.List<String> changed=new ArrayList<String>();for(int i=0;i<5;i++){Color color=JColorChooser.showDialog(this,"Cor "+(i+1),Color.decode(String.valueOf(colors.get(i))));if(color==null)return;changed.add(String.format("#%06X",color.getRGB()&0xffffff));}send("playerListPalette",changed);});
        ((JButton)Data.get(panel,"vm")).addActionListener(e->dispose());
        controls.put("watchedLeagueCountryIds",(JTable)Data.get(panel,"JG"));
        for(String name:new String[]{"yV","yZ"}){JButton arrow=(JButton)Data.get(panel,name);arrow.setEnabled(false);arrow.setToolTipText(SessionOptions.adapter("nonHumanCompetitionPlayback").unavailable);}
        update(settings,false);setSize(890,690);setLocationRelativeTo(owner);
    }
    private void combo(String key,String field,String[] labels){JComboBox<String> box=(JComboBox<String>)Data.get(panel,field);for(String label:labels)box.addItem(label);controls.put(key,box);box.addActionListener(e->send(key,SessionOptions.adapter(key).kind.equals("boolean")?box.getSelectedIndex()==1:box.getSelectedIndex()));}
    private void colorChoices(String key,String[] fields,String[] icons){for(int i=0;i<fields.length;i++){final int index=i;JLabel label=(JLabel)Data.get(panel,fields[i]);label.setName(icons[i]);controls.put(key+":"+i,label);label.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(label.isEnabled())send(key,index);}});}}
    private void send(String key,Object value){if(projecting || busy)return;Map<String,Object> changes=new LinkedHashMap<String,Object>();changes.put(key,value);submit.accept(changes);}
    void update(Map<String,Object> settings,boolean busy){boolean rebuildLists=this.settings==null || !Objects.equals(this.settings.get("values"),settings.get("values")) || !Objects.equals(this.settings.get("leagueCountries"),settings.get("leagueCountries"));this.settings=settings;this.busy=busy;projecting=true;try{
        Map<String,Object> values=Data.map(settings.get("values")),pending=Data.map(settings.get("pendingValues")),reasons=Data.map(settings.get("unavailableReasons"));java.util.List<Object> supported=Data.list(settings.get("supportedKeys"));boolean host=Boolean.TRUE.equals(settings.get("canEdit"));
        for(Map.Entry<String,JComponent> entry:controls.entrySet()){String key=entry.getKey().split(":")[0];JComponent control=entry.getValue();Object value=values.get(key);boolean speed=key.endsWith("SpeedIndex");control.setEnabled(host && !busy && supported.contains(key) && !(speed && "CLI_OVERRIDE".equals(settings.get("speedSource"))));
            String tooltip=!host?"Configurações controladas pelo host":String.valueOf(reasons.getOrDefault(key,"Confirmado pelo host"));if(pending.containsKey(key))tooltip+="; pendente: "+pending.get(key)+" ("+Data.map(settings.get("pendingApplyAt")).get(key)+")";control.setToolTipText(tooltip);
            if(control instanceof JComboBox){JComboBox box=(JComboBox)control;if(speed){int limit=Data.number(Data.map(settings.get("nativeLimits")).get(key));while(box.getItemCount()>limit+1)box.removeItemAt(box.getItemCount()-1);}box.setSelectedIndex(value instanceof Boolean?((Boolean)value?1:0):Math.min(Data.number(value),box.getItemCount()-1));}
            else if(control instanceof JCheckBox)((JCheckBox)control).setSelected(Boolean.TRUE.equals(value));
            else if(control instanceof JLabel && entry.getKey().contains(":")){JLabel label=(JLabel)control;int index=Integer.parseInt(entry.getKey().split(":")[1]);label.setIcon(new ImageIcon(getClass().getResource("/aiconsc/"+label.getName()+(Data.number(value)==index?"s":"")+".png")));}
        }
        if(rebuildLists){JTable countries=(JTable)Data.get(panel,"JG");DefaultTableModel model=new DefaultTableModel(new Object[]{"Ver","País"},0){@Override public boolean isCellEditable(int r,int c){return false;}};for(Object raw:Data.list(settings.get("leagueCountries"))){Map<String,Object> row=Data.map(raw);model.addRow(new Object[]{Data.list(values.get("watchedLeagueCountryIds")).contains(row.get("id")),row.get("label")});}countries.setModel(model);
        DefaultTableModel watch=new DefaultTableModel(new Object[]{"Ver partidas"},0),results=new DefaultTableModel(new Object[]{"Ver só resultados"},0);for(Object raw:Data.list(settings.get("nonHumanCompetitions"))){Map<String,Object> row=Data.map(raw);(Boolean.TRUE.equals(Data.map(values.get("nonHumanCompetitionPlayback")).get(row.get("id")))?watch:results).addRow(new Object[]{row.get("label")});}((JTable)Data.get(panel,"afq")).setModel(readOnly(watch));((JTable)Data.get(panel,"afr")).setModel(readOnly(results));}
        notice.setText((host?"Configurações da sessão":"Configurações controladas pelo host")+" — "+settings.get("effectiveTickMillis")+" ms ("+settings.get("speedSource")+"). Checkpoints de recuperação sempre ativos.");panel.setBackground(best.aq.E(Data.number(values.get("windowThemeIndex")),1));panel.repaint();
    }finally{projecting=false;}}
    private static DefaultTableModel readOnly(DefaultTableModel source){return new DefaultTableModel(source.getDataVector(),source.getColumnCount()==1?new Vector<Object>(Arrays.asList(source.getColumnName(0))):new Vector<Object>()){@Override public boolean isCellEditable(int r,int c){return false;}};}
    static void refreshWindows(){int theme=c.a.vM().getCorTema();if(previousTheme>=0 && previousTheme!=theme)for(Window window:Window.getWindows())retheme(window,previousTheme,theme);previousTheme=theme;for(Window window:Window.getWindows())if(window.isShowing())window.repaint();}
    private static void retheme(Component component,int before,int after){for(int tone=1;tone<=2;tone++)if(Objects.equals(component.getBackground(),best.aq.E(before,tone)))component.setBackground(best.aq.E(after,tone));if(component instanceof Container)for(Component child:((Container)component).getComponents())retheme(child,before,after);}
}
