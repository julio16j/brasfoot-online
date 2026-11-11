package brasfoot;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;

/** Original fB layout, with online listeners only: no native preference-file writes. */
final class NativeSessionOptions extends JDialog {
    private final a.fB panel;
    private final Consumer<Map<String,Object>> submit;
    private final Map<String,JComponent> controls=new LinkedHashMap<String,JComponent>();
    private final JLabel notice=new JLabel();
    private Map<String,Object> settings;
    private DefaultTableModel countryModel;
    private boolean projecting,busy;
    private Object selectedCompetition;
    private NativeOptionsPalette paletteDialog;
    private static int previousTheme=-1;
    NativeSessionOptions(JFrame owner,Map<String,Object> settings,Consumer<Map<String,Object>> submit){
        super(owner,"Preferências",true);this.submit=submit;setUndecorated(true);setResizable(false);
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
        JButton palette=(JButton)Data.get(panel,"IM");controls.put("playerListPalette",palette);palette.addActionListener(e->openPalette());
        ((JButton)Data.get(panel,"vm")).addActionListener(e->dispose());
        JTable countries=(JTable)Data.get(panel,"JG");controls.put("watchedLeagueCountryIds",countries);
        JButton toResults=(JButton)Data.get(panel,"yV"),toWatch=(JButton)Data.get(panel,"yZ");toResults.setToolTipText("Mover para Ver so resultados");toWatch.setToolTipText("Mover para Ver partidas");
        toResults.addActionListener(e->moveCompetition(false));toWatch.addActionListener(e->moveCompetition(true));
        JTable watchTable=(JTable)Data.get(panel,"afq"),resultTable=(JTable)Data.get(panel,"afr");ListSelectionListener selection=e->{if(!e.getValueIsAdjusting()){JTable source=e.getSource()==watchTable.getSelectionModel()?watchTable:resultTable;Object id=selectedCompetitionId(source);if(id!=null)selectedCompetition=id;}refreshCompetitionArrows();};watchTable.getSelectionModel().addListSelectionListener(selection);resultTable.getSelectionModel().addListSelectionListener(selection);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"close");getRootPane().getActionMap().put("close",new AbstractAction(){public void actionPerformed(ActionEvent event){dispose();}});
        update(settings,false);setSize(867,620);setLocationRelativeTo(owner);
    }
    private void combo(String key,String field,String[] labels){JComboBox<String> box=(JComboBox<String>)Data.get(panel,field);for(String label:labels)box.addItem(label);controls.put(key,box);box.addActionListener(e->send(key,SessionOptions.adapter(key).kind.equals("boolean")?box.getSelectedIndex()==1:box.getSelectedIndex()));}
    private void colorChoices(String key,String[] fields,String[] icons){for(int i=0;i<fields.length;i++){final int index=i;JLabel label=(JLabel)Data.get(panel,fields[i]);label.setName(icons[i]);controls.put(key+":"+i,label);label.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent event){if(label.isEnabled())send(key,index);}});}}
    boolean canEdit(String key){return !busy && Boolean.TRUE.equals(settings.get("canEdit")) && Data.list(settings.get("supportedKeys")).contains(key) && !(key.endsWith("SpeedIndex") && "CLI_OVERRIDE".equals(settings.get("speedSource")));}
    long revision(){return Data.longNumber(settings.get("revision"));}
    void send(String key,Object value){if(projecting)return;if(!canEdit(key)){update(settings,busy);return;}Map<String,Object> changes=new LinkedHashMap<String,Object>();changes.put(key,value);update(settings,true);submit.accept(changes);}
    void openPalette(){if(!canEdit("playerListPalette"))return;if(paletteDialog!=null && paletteDialog.isShowing()){paletteDialog.toFront();return;}paletteDialog=new NativeOptionsPalette(this,Data.list(Data.map(settings.get("values")).get("playerListPalette")));paletteDialog.setVisible(true);}
    @Override public void dispose(){if(paletteDialog!=null)paletteDialog.dispose();super.dispose();}
    void update(Map<String,Object> settings,boolean busy){Map<String,Object> oldValues=this.settings==null?Collections.<String,Object>emptyMap():Data.map(this.settings.get("values")),newValues=Data.map(settings.get("values"));boolean rebuildCountries=this.settings==null || !Objects.equals(this.settings.get("leagueCountries"),settings.get("leagueCountries"));boolean rebuildCompetitions=this.settings==null || !Objects.equals(oldValues.get("nonHumanCompetitionPlayback"),newValues.get("nonHumanCompetitionPlayback")) || !Objects.equals(this.settings.get("nonHumanCompetitions"),settings.get("nonHumanCompetitions"));this.settings=settings;this.busy=busy;projecting=true;try{
        Map<String,Object> values=Data.map(settings.get("values")),pending=Data.map(settings.get("pendingValues")),reasons=Data.map(settings.get("unavailableReasons"));java.util.List<Object> supported=Data.list(settings.get("supportedKeys"));boolean host=Boolean.TRUE.equals(settings.get("canEdit"));
        for(Map.Entry<String,JComponent> entry:controls.entrySet()){String key=entry.getKey().split(":")[0];JComponent control=entry.getValue();Object value=values.get(key);boolean speed=key.endsWith("SpeedIndex");control.setEnabled(host && !busy && supported.contains(key) && !(speed && "CLI_OVERRIDE".equals(settings.get("speedSource"))));
            String tooltip=!host?"Configurações controladas pelo host":String.valueOf(reasons.getOrDefault(key,"Confirmado pelo host"));if(pending.containsKey(key))tooltip+="; pendente: "+pending.get(key)+" ("+Data.map(settings.get("pendingApplyAt")).get(key)+")";control.setToolTipText(tooltip);
            if(control instanceof JComboBox){JComboBox box=(JComboBox)control;if(speed){int limit=Data.number(Data.map(settings.get("nativeLimits")).get(key));while(box.getItemCount()>limit+1)box.removeItemAt(box.getItemCount()-1);}box.setSelectedIndex(value instanceof Boolean?((Boolean)value?1:0):Math.min(Data.number(value),box.getItemCount()-1));}
            else if(control instanceof JCheckBox)((JCheckBox)control).setSelected(Boolean.TRUE.equals(value));
            else if(control instanceof JLabel && entry.getKey().contains(":")){JLabel label=(JLabel)control;int index=Integer.parseInt(entry.getKey().split(":")[1]);label.setIcon(new ImageIcon(getClass().getResource("/aiconsc/"+label.getName()+(Data.number(value)==index?"s":"")+".png")));}
        }
        if(rebuildCountries){JTable countries=(JTable)Data.get(panel,"JG");
            countryModel=new DefaultTableModel(new Object[]{"Ver","Pa\u00eds","id"},0){@Override public boolean isCellEditable(int r,int c){return c==0;}@Override public Class<?> getColumnClass(int c){return c==0?Boolean.class:c==2?Integer.class:String.class;}};
            for(Object raw:Data.list(settings.get("leagueCountries"))){Map<String,Object> row=Data.map(raw);countryModel.addRow(new Object[]{Data.list(values.get("watchedLeagueCountryIds")).contains(row.get("id")),row.get("label"),row.get("id")});}
            countries.setAutoCreateColumnsFromModel(true);countries.setModel(countryModel);hideModelColumn(countries,2);
            countryModel.addTableModelListener(event->{if(projecting || event.getType()!=javax.swing.event.TableModelEvent.UPDATE || event.getColumn()!=0 || event.getFirstRow()<0)return;java.util.List<Integer> selected=new ArrayList<Integer>();for(int row=0;row<countryModel.getRowCount();row++)if(Boolean.TRUE.equals(countryModel.getValueAt(row,0)))selected.add(Data.number(countryModel.getValueAt(row,2)));send("watchedLeagueCountryIds",selected);});
        }
        if(!busy && countryModel!=null){java.util.List<Object> watched=Data.list(values.get("watchedLeagueCountryIds"));for(int row=0;row<countryModel.getRowCount();row++){Object id=countryModel.getValueAt(row,2);boolean selected=watched.contains(id);if(!Objects.equals(countryModel.getValueAt(row,0),selected))countryModel.setValueAt(selected,row,0);}}
        if(rebuildCompetitions){JTable watchTable=(JTable)Data.get(panel,"afq"),resultTable=(JTable)Data.get(panel,"afr");Object keep=selectedCompetition;if(keep==null)keep=selectedCompetitionId(watchTable);if(keep==null)keep=selectedCompetitionId(resultTable);
            DefaultTableModel watch=new DefaultTableModel(new Object[]{"Ver partidas","id"},0){@Override public boolean isCellEditable(int r,int c){return false;}},results=new DefaultTableModel(new Object[]{"Ver s\u00f3 resultados","id"},0){@Override public boolean isCellEditable(int r,int c){return false;}};Map<String,Object> playback=Data.map(values.get("nonHumanCompetitionPlayback"));
            for(Object raw:Data.list(settings.get("nonHumanCompetitions"))){Map<String,Object> row=Data.map(raw);(Boolean.TRUE.equals(playback.get(row.get("id")))?watch:results).addRow(new Object[]{row.get("label"),row.get("id")});}
            watchTable.setAutoCreateColumnsFromModel(true);resultTable.setAutoCreateColumnsFromModel(true);watchTable.setModel(watch);resultTable.setModel(results);hideModelColumn(watchTable,1);hideModelColumn(resultTable,1);watchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);watchTable.setRowSelectionAllowed(true);resultTable.setRowSelectionAllowed(true);watchTable.setColumnSelectionAllowed(false);resultTable.setColumnSelectionAllowed(false);
            if(keep!=null && !selectCompetitionId(watchTable,keep))selectCompetitionId(resultTable,keep);}
        refreshCompetitionArrows();
        notice.setText((host?"Configurações da sessão":"Configurações controladas pelo host")+" — "+settings.get("effectiveTickMillis")+" ms ("+settings.get("speedSource")+"). Checkpoints de recuperação sempre ativos.");panel.setBackground(best.aq.E(Data.number(values.get("windowThemeIndex")),1));panel.repaint();
        if(paletteDialog!=null && paletteDialog.isDisplayable())paletteDialog.updateAuthority();
    }finally{projecting=false;}}
    private void moveCompetition(boolean watch){
        JTable source=(JTable)Data.get(panel,watch?"afr":"afq");int selected=source.getSelectedRow();if(selected<0)return;int row=source.convertRowIndexToModel(selected);Object id=source.getModel().getValueAt(row,1);
        selectedCompetition=id;Map<String,Object> values=Data.map(settings.get("values")),playback=new LinkedHashMap<String,Object>(Data.map(values.get("nonHumanCompetitionPlayback")));playback.put(String.valueOf(id),watch);send("nonHumanCompetitionPlayback",playback);
    }
    private void refreshCompetitionArrows(){if(settings==null)return;boolean enabled=canEdit("nonHumanCompetitionPlayback");((JButton)Data.get(panel,"yV")).setEnabled(enabled && ((JTable)Data.get(panel,"afq")).getSelectedRow()>=0);((JButton)Data.get(panel,"yZ")).setEnabled(enabled && ((JTable)Data.get(panel,"afr")).getSelectedRow()>=0);}
    private static Object selectedCompetitionId(JTable table){int view=table.getSelectedRow();if(view<0)return null;int row=table.convertRowIndexToModel(view);return table.getModel().getValueAt(row,1);}
    private static boolean selectCompetitionId(JTable table,Object id){for(int row=0;row<table.getModel().getRowCount();row++)if(Objects.equals(table.getModel().getValueAt(row,1),id)){int view=table.convertRowIndexToView(row);table.setRowSelectionInterval(view,view);return true;}return false;}
    private static void hideModelColumn(JTable table,int modelIndex){int view=table.convertColumnIndexToView(modelIndex);if(view<0)return;table.getColumnModel().getColumn(view).setMinWidth(0);table.getColumnModel().getColumn(view).setMaxWidth(0);table.getColumnModel().getColumn(view).setPreferredWidth(0);}
    private static DefaultTableModel readOnly(DefaultTableModel source){return new DefaultTableModel(source.getDataVector(),source.getColumnCount()==1?new Vector<Object>(Arrays.asList(source.getColumnName(0))):new Vector<Object>()){@Override public boolean isCellEditable(int r,int c){return false;}};}
    static void refreshWindows(){int theme=c.a.vM().getCorTema();if(previousTheme>=0 && previousTheme!=theme)for(Window window:Window.getWindows())retheme(window,previousTheme,theme);previousTheme=theme;for(Window window:Window.getWindows())if(window.isShowing())window.repaint();}
    private static void retheme(Component component,int before,int after){for(int tone=1;tone<=2;tone++)if(Objects.equals(component.getBackground(),best.aq.E(before,tone)))component.setBackground(best.aq.E(after,tone));if(component instanceof Container)for(Component child:((Container)component).getComponents())retheme(child,before,after);}
}
