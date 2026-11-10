package brasfoot;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableColumnModel;
import java.util.Comparator;

/** Career overview and explicit management commands for the multiplayer host. */
public final class NativeCareerDialogs {
    public interface Command { void submit(Map<String,String> fields,Runnable success,Runnable failure); }
    private static final String[] ACTIONS={"renew","promote","list","loan-list","loan-return","offer","loan-request","respond","sell","job"};
    private static final String[] LABELS={"Renovar contrato","Promover jogador da base","Colocar/retirar da lista","Disponibilizar/retirar para empréstimo","Devolver empréstimo","Fazer proposta","Pedir empréstimo","Responder proposta","Vender para clube da IA","Aceitar emprego"};
    private NativeCareerDialogs() { }

    public static void open(JFrame owner,String requested,Map<String,Object> overview,Map<String,Object> management,Command command){
        JDialog dialog=new JDialog(owner,"Carreira",true);dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JTabbedPane tabs=new JTabbedPane();tabs.addTab("Temporada",overviewPanel(overview));
        tabs.addTab("Gestão",managementPanel(dialog,requested,management,command));
        dialog.setContentPane(tabs);dialog.setSize(1000,680);dialog.setMinimumSize(new Dimension(820,520));dialog.setLocationRelativeTo(owner);dialog.setVisible(true);
    }

    private static JPanel overviewPanel(Map<String,Object> overview){
        JPanel root=new JPanel(new BorderLayout(8,8));JPanel summary=new JPanel(new FlowLayout(FlowLayout.LEFT));
        summary.add(new JLabel("Data: "+value(overview.get("date"))+"   Temporada: "+value(overview.get("year"))));
        summary.add(new JLabel("Saldo: "+value(overview.get("balance"))+"   Folha: "+value(overview.get("wages"))));root.add(summary,BorderLayout.NORTH);
        JTabbedPane views=new JTabbedPane();views.addTab("Calendário",agendaPanel(list(overview,"calendar")));
        views.addTab("Artilheiros",table(new String[]{"Jogador","Clube","Gols","Jogos"},list(overview,"scorers"),new String[]{"name","club","goals","appearances"}));
        views.addTab("Assistências",table(new String[]{"Jogador","Clube","Assist.","Jogos"},list(overview,"assists"),new String[]{"name","club","assists","appearances"}));
        for(Object raw:list(overview,"tables")){Map<String,Object> league=Data.map(raw);views.addTab(String.valueOf(league.get("name")),table(new String[]{"Pos.","Clube","Pts","J","V","E","D","GP","GC","SG"},list(league,"rows"),new String[]{"position","club","points","played","wins","draws","losses","goalsFor","goalsAgainst","goalDifference"}));}
        root.add(views,BorderLayout.CENTER);
        List<String> coaches=new ArrayList<String>();for(Object raw:list(overview,"coaches")){Map<String,Object> c=Data.map(raw);coaches.add(c.get("name")+" — "+c.get("club"));}
        root.add(new JLabel("Técnicos: "+String.join("   •   ",coaches)),BorderLayout.SOUTH);return root;
    }

    private static final class AgendaCompetition {final String key,label;AgendaCompetition(String key,String label){this.key=key;this.label=label;}@Override public String toString(){return label;}}
    static JPanel agendaPanel(List<Object> rows){
        JPanel panel=table(new String[]{"Data","Competi\u00e7\u00e3o","Fase","Mandante","Visitante","Resultado","P\u00fablico","Renda"},rows,new String[]{"date","competition","phase","home","away","result","attendance","revenue"});
        JTable grid=(JTable)((JScrollPane)panel.getComponent(0)).getViewport().getView();JComboBox<AgendaCompetition> competitions=new JComboBox<AgendaCompetition>();competitions.addItem(new AgendaCompetition(null,"Mostrar todos"));Set<String> seen=new HashSet<String>();
        for(Object raw:rows){Map<String,Object> row=Data.map(raw);Object identity=row.get("competitionKey");if(identity==null||!seen.add(String.valueOf(identity)))continue;String label=String.valueOf(row.get("competition"));if(row.get("competitionCountryName")!=null)label+=" - "+row.get("competitionCountryName");if(row.get("competitionType") instanceof Number&&Data.number(row.get("competitionType"))==1&&row.get("competitionDivision") instanceof Number&&Data.number(row.get("competitionDivision"))>=0)label+=" / Divis\u00e3o "+(Data.number(row.get("competitionDivision"))+1);competitions.addItem(new AgendaCompetition(String.valueOf(identity),label));}
        competitions.addActionListener(event->{AgendaCompetition selected=(AgendaCompetition)competitions.getSelectedItem();((TableRowSorter<?>)grid.getRowSorter()).setRowFilter(selected==null||selected.key==null?null:new RowFilter<javax.swing.table.TableModel,Integer>(){@Override public boolean include(Entry<? extends javax.swing.table.TableModel,? extends Integer> entry){return selected.key.equals(Data.map(rows.get(entry.getIdentifier())).get("competitionKey"));}});});
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));controls.add(new JLabel("Competi\u00e7\u00e3o:"));controls.add(competitions);panel.add(controls,BorderLayout.NORTH);return panel;
    }

    static JPanel table(String[] columns,List<Object> rows,String[] keys){
        Object[][] values=new Object[rows.size()][columns.length];Map<String,Long> dateKeys=new HashMap<String,Long>();for(int i=0;i<rows.size();i++){Map<String,Object> row=Data.map(rows.get(i));for(int j=0;j<keys.length;j++){Object cell=row.get(keys[j]);values[i][j]=("attendance".equals(keys[j])||"revenue".equals(keys[j]))&&cell instanceof Number&&((Number)cell).longValue()<=0?null:cell;}if(row.get("date")!=null&&row.get("dateMillis")!=null)dateKeys.put(String.valueOf(row.get("date")),Data.longNumber(row.get("dateMillis")));}
        JTable table=new JTable(new DefaultTableModel(values,columns){@Override public boolean isCellEditable(int row,int col){return false;}@Override public Class<?> getColumnClass(int column){for(int row=0;row<getRowCount();row++){Object value=getValueAt(row,column);if(value!=null)return value.getClass();}return Object.class;}});table.setAutoCreateRowSorter(true);
        if(keys.length>0&&"date".equals(keys[0])){TableRowSorter<?> sorter=(TableRowSorter<?>)table.getRowSorter();sorter.setComparator(0,Comparator.comparingLong(raw->dateKeys.getOrDefault(String.valueOf(raw),Long.MAX_VALUE)));sorter.setSortKeys(java.util.Collections.singletonList(new javax.swing.RowSorter.SortKey(0,javax.swing.SortOrder.ASCENDING)));}
        for(int column=0;column<keys.length;column++)if("revenue".equals(keys[column]))table.getColumnModel().getColumn(column).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer(){@Override protected void setValue(Object value){setHorizontalAlignment(SwingConstants.RIGHT);setText(value instanceof Number?best.C.a(((Number)value).doubleValue(),0):"");}});
        JPanel panel=new JPanel(new BorderLayout());panel.add(new JScrollPane(table),BorderLayout.CENTER);return panel;
    }

    private static JPanel managementPanel(JDialog dialog,String requested,Map<String,Object> data,Command command){
        JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JComboBox<Choice> action=new JComboBox<Choice>();for(int i=0;i<ACTIONS.length;i++)action.addItem(new Choice(ACTIONS[i],LABELS[i]));
        JComboBox<Choice> player=new JComboBox<Choice>(),youth=new JComboBox<Choice>(),market=new JComboBox<Choice>(),loans=new JComboBox<Choice>(),offers=new JComboBox<Choice>(),jobs=new JComboBox<Choice>();
        fill(player,list(data,"players"),"playerId","name",true);fill(youth,list(data,"youth"),"playerId","name",true);fill(market,list(data,"market"),"playerId","name",true);
        fill(offers,list(data,"offers"),"offerId","player",false);fill(jobs,list(data,"jobs"),"clubId","club",false);
        fill(loans,list(data,"loans"),"playerId","name",false);
        JTextField price=new JTextField("100000",10);JComboBox<String> days=new JComboBox<String>(new String[]{"180","365","730","1095"});
        JComboBox<String> sale=new JComboBox<String>(new String[]{"true","false"}),accept=new JComboBox<String>(new String[]{"true","false"}),available=new JComboBox<String>(new String[]{"true","false"});
        selectRequested(action,player,sale,requested);
        JPanel fields=new JPanel(new GridLayout(0,2,8,8));fields.add(new JLabel("Operação"));fields.add(action);
        fields.add(new JLabel("Jogador do elenco"));fields.add(player);fields.add(new JLabel("Jogador da base"));fields.add(youth);
        fields.add(new JLabel("Jogador do mercado"));fields.add(market);fields.add(new JLabel("Empréstimo ativo"));fields.add(loans);fields.add(new JLabel("Proposta"));fields.add(offers);fields.add(new JLabel("Clube com vaga"));fields.add(jobs);
        fields.add(new JLabel("Valor (transferência)"));fields.add(price);fields.add(new JLabel("Duração do contrato (dias)"));fields.add(days);
        fields.add(new JLabel("Colocar à venda"));fields.add(sale);fields.add(new JLabel("Aceitar proposta"));fields.add(accept);
        fields.add(new JLabel("Disponível para empréstimo"));fields.add(available);
        root.add(fields,BorderLayout.NORTH);
        JTextArea details=new JTextArea(9,40);details.setEditable(false);details.setText("Saldo do clube: "+value(data.get("balance"))+"\n\n"+describe("Elenco",list(data,"players"))+describe("Base",list(data,"youth"))+describe("Propostas",list(data,"offers"))+describe("Empregos",list(data,"jobs")));root.add(new JScrollPane(details),BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton submit=new JButton("Enviar comando"),close=new JButton("Fechar");buttons.add(submit);buttons.add(close);root.add(buttons,BorderLayout.SOUTH);close.addActionListener(e->dialog.dispose());
        submit.addActionListener(e->{try{Choice selected=(Choice)action.getSelectedItem();if(selected==null)throw new IllegalStateException("Escolha uma operação");Map<String,String> request=new LinkedHashMap<String,String>();request.put("action",selected.id);
            switch(selected.id){
                case "renew":request.put("playerId",id(player));request.put("days",(String)days.getSelectedItem());break;
                case "promote":request.put("playerId",id(youth));break;
                case "list":request.put("playerId",id(player));request.put("forSale",(String)sale.getSelectedItem());request.put("price",price.getText().trim());break;
                case "loan-list":request.put("playerId",id(player));request.put("available",(String)available.getSelectedItem());break;
                case "loan-return":request.put("playerId",id(loans));break;
                case "offer":{Choice target=choice(market);request.put("playerId",id(market));request.put("sellerClubId",String.valueOf(target.extra));request.put("price",price.getText().trim());break;}
                case "loan-request":{Choice target=choice(market);request.put("action","loan-request");request.put("playerId",id(market));request.put("sellerClubId",String.valueOf(target.extra));break;}
                case "respond":request.put("offerId",id(offers));request.put("accept",(String)accept.getSelectedItem());break;
                case "sell":request.put("playerId",id(player));request.put("price",price.getText().trim());break;
                case "job":request.put("clubId",id(jobs));if(data.get("jobsRevision")!=null)request.put("jobsRevision",String.valueOf(data.get("jobsRevision")));break;
                default:throw new IllegalArgumentException("Operação desconhecida");
            }
            submitCommand(command,request,submit,()->{dialog.dispose();JOptionPane.showMessageDialog(ownerOf(dialog),"Comando aplicado à carreira.","Brasfoot Online",JOptionPane.INFORMATION_MESSAGE);});
        }catch(Exception error){JOptionPane.showMessageDialog(dialog,error.getMessage(),"Carreira",JOptionPane.WARNING_MESSAGE);}});
        return root;
    }

    static void selectRequested(JComboBox<Choice> actions,JComboBox<Choice> players,JComboBox<String> sale,String requested){
        if(requested==null)return;String[] parts=requested.split(":",2);String value=parts[0].toLowerCase(Locale.ROOT);String action=value.startsWith("renov")?"renew":value.startsWith("promover")?"promote":value.startsWith("colocar")||value.startsWith("retirar")?"list":value.contains("transfer")||value.contains("procura")?"offer":null;
        if(action!=null)for(int i=0;i<actions.getItemCount();i++)if(actions.getItemAt(i).id.equals(action)){actions.setSelectedIndex(i);break;}
        if(parts.length<2)return;String playerId=parts[1];boolean found=false;for(int i=0;i<players.getItemCount();i++)if(players.getItemAt(i).id.equals(playerId)){players.setSelectedIndex(i);found=true;break;}
        if(!found)players.setSelectedIndex(-1);
        if(action!=null&&action.equals("list"))sale.setSelectedItem(value.startsWith("retirar")?"false":"true");
    }
    static void submitCommand(Command command,Map<String,String> request,JButton button,Runnable success){button.setEnabled(false);try{command.submit(request,success,()->button.setEnabled(true));}catch(RuntimeException error){button.setEnabled(true);throw error;}}
    private static JFrame ownerOf(JDialog dialog){return (JFrame)SwingUtilities.getWindowAncestor(dialog.getOwner());}
    private static String describe(String title,List<Object> rows){StringBuilder out=new StringBuilder(title).append(":\n");for(Object raw:rows){Map<String,Object> row=Data.map(raw);out.append("• ").append(value(row.get("name")!=null?row.get("name"):row.get("player"))).append("  ").append(value(row.get("club"))).append("\n");}return out.append("\n").toString();}
    private static void fill(JComboBox<Choice> combo,List<Object> rows,String id,String label,boolean club){for(Object raw:rows){Map<String,Object> row=Data.map(raw);combo.addItem(new Choice(String.valueOf(row.get(id)),String.valueOf(row.get(label)),club?String.valueOf(row.get("clubId")):null));}}
    private static Choice choice(JComboBox<Choice> combo){Choice value=(Choice)combo.getSelectedItem();if(value==null)throw new IllegalStateException("Não há opções disponíveis");return value;}
    private static String id(JComboBox<Choice> combo){return choice(combo).id;}
    private static List<Object> list(Map<String,Object> data,String key){Object value=data.get(key);return value instanceof List?Data.list(value):Collections.<Object>emptyList();}
    private static String value(Object value){return value==null?"—":String.valueOf(value);}
    static final class Choice {final String id,label,extra;Choice(String id,String label){this(id,label,null);}Choice(String id,String label,String extra){this.id=id;this.label=label;this.extra=extra;}@Override public String toString(){return label;}}
}
