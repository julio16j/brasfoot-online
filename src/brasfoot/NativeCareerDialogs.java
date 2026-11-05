package brasfoot;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Career overview and explicit management commands for the multiplayer host. */
public final class NativeCareerDialogs {
    public interface Command { void submit(Map<String,String> fields,Runnable success); }
    private static final String[] ACTIONS={"renew","promote","list","offer","respond","sell","job"};
    private static final String[] LABELS={"Renovar contrato","Promover jogador da base","Colocar/retirar da lista","Fazer proposta","Responder proposta","Vender para clube da IA","Aceitar emprego"};
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
        JTabbedPane views=new JTabbedPane();views.addTab("Calendário",table(new String[]{"Data","Competição","Fase","Mandante","Visitante","Resultado"},list(overview,"calendar"),new String[]{"date","competition","phase","home","away","result"}));
        for(Object raw:list(overview,"tables")){Map<String,Object> league=Data.map(raw);views.addTab(String.valueOf(league.get("name")),table(new String[]{"Pos.","Clube","Pts","J","V","E","D","GP","GC","SG"},list(league,"rows"),new String[]{"position","club","points","played","wins","draws","losses","goalsFor","goalsAgainst","goalDifference"}));}
        root.add(views,BorderLayout.CENTER);
        List<String> coaches=new ArrayList<String>();for(Object raw:list(overview,"coaches")){Map<String,Object> c=Data.map(raw);coaches.add(c.get("name")+" — "+c.get("club"));}
        root.add(new JLabel("Técnicos: "+String.join("   •   ",coaches)),BorderLayout.SOUTH);return root;
    }

    private static JPanel table(String[] columns,List<Object> rows,String[] keys){
        Object[][] values=new Object[rows.size()][columns.length];for(int i=0;i<rows.size();i++){Map<String,Object> row=Data.map(rows.get(i));for(int j=0;j<keys.length;j++)values[i][j]=row.get(keys[j]);}
        JTable table=new JTable(new DefaultTableModel(values,columns){@Override public boolean isCellEditable(int row,int col){return false;}});table.setAutoCreateRowSorter(true);
        JPanel panel=new JPanel(new BorderLayout());panel.add(new JScrollPane(table),BorderLayout.CENTER);return panel;
    }

    private static JPanel managementPanel(JDialog dialog,String requested,Map<String,Object> data,Command command){
        JPanel root=new JPanel(new BorderLayout(10,10));root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JComboBox<Choice> action=new JComboBox<Choice>();for(int i=0;i<ACTIONS.length;i++)action.addItem(new Choice(ACTIONS[i],LABELS[i]));
        selectRequested(action,requested);
        JComboBox<Choice> player=new JComboBox<Choice>(),youth=new JComboBox<Choice>(),market=new JComboBox<Choice>(),offers=new JComboBox<Choice>(),jobs=new JComboBox<Choice>();
        fill(player,list(data,"players"),"playerId","name",true);fill(youth,list(data,"youth"),"playerId","name",true);fill(market,list(data,"market"),"playerId","name",true);
        fill(offers,list(data,"offers"),"offerId","player",false);fill(jobs,list(data,"jobs"),"clubId","club",false);
        JTextField price=new JTextField("100000",10);JComboBox<String> days=new JComboBox<String>(new String[]{"180","365","730","1095"});
        JComboBox<String> sale=new JComboBox<String>(new String[]{"true","false"}),accept=new JComboBox<String>(new String[]{"true","false"});
        JPanel fields=new JPanel(new GridLayout(0,2,8,8));fields.add(new JLabel("Operação"));fields.add(action);
        fields.add(new JLabel("Jogador do elenco"));fields.add(player);fields.add(new JLabel("Jogador da base"));fields.add(youth);
        fields.add(new JLabel("Jogador do mercado"));fields.add(market);fields.add(new JLabel("Proposta"));fields.add(offers);fields.add(new JLabel("Clube com vaga"));fields.add(jobs);
        fields.add(new JLabel("Valor (transferência)"));fields.add(price);fields.add(new JLabel("Duração do contrato (dias)"));fields.add(days);
        fields.add(new JLabel("Colocar à venda"));fields.add(sale);fields.add(new JLabel("Aceitar proposta"));fields.add(accept);
        root.add(fields,BorderLayout.NORTH);
        JTextArea details=new JTextArea(9,40);details.setEditable(false);details.setText("Saldo do clube: "+value(data.get("balance"))+"\n\n"+describe("Elenco",list(data,"players"))+describe("Base",list(data,"youth"))+describe("Propostas",list(data,"offers"))+describe("Empregos",list(data,"jobs")));root.add(new JScrollPane(details),BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton submit=new JButton("Enviar comando"),close=new JButton("Fechar");buttons.add(submit);buttons.add(close);root.add(buttons,BorderLayout.SOUTH);close.addActionListener(e->dialog.dispose());
        submit.addActionListener(e->{try{Choice selected=(Choice)action.getSelectedItem();if(selected==null)throw new IllegalStateException("Escolha uma operação");Map<String,String> request=new LinkedHashMap<String,String>();request.put("action",selected.id);
            switch(selected.id){
                case "renew":request.put("playerId",id(player));request.put("days",(String)days.getSelectedItem());break;
                case "promote":request.put("playerId",id(youth));break;
                case "list":request.put("playerId",id(player));request.put("forSale",(String)sale.getSelectedItem());request.put("price",price.getText().trim());break;
                case "offer":{Choice target=choice(market);request.put("playerId",id(market));request.put("sellerClubId",String.valueOf(target.extra));request.put("price",price.getText().trim());break;}
                case "respond":request.put("offerId",id(offers));request.put("accept",(String)accept.getSelectedItem());break;
                case "sell":request.put("playerId",id(player));request.put("price",price.getText().trim());break;
                case "job":request.put("clubId",id(jobs));break;
                default:throw new IllegalArgumentException("Operação desconhecida");
            }
            submit.setEnabled(false);command.submit(request,()->{dialog.dispose();JOptionPane.showMessageDialog(ownerOf(dialog),"Comando aplicado à carreira.","Brasfoot Online",JOptionPane.INFORMATION_MESSAGE);});
        }catch(Exception error){JOptionPane.showMessageDialog(dialog,error.getMessage(),"Carreira",JOptionPane.WARNING_MESSAGE);}});
        return root;
    }

    private static void selectRequested(JComboBox<Choice> choices,String requested){
        if(requested==null)return;String value=requested.toLowerCase(Locale.ROOT);String action=value.startsWith("renov")?"renew":value.startsWith("promover")?"promote":value.startsWith("colocar")||value.startsWith("retirar")?"list":value.contains("transfer")||value.contains("procura")?"offer":null;
        if(action!=null)for(int i=0;i<choices.getItemCount();i++)if(choices.getItemAt(i).id.equals(action))choices.setSelectedIndex(i);
    }
    private static JFrame ownerOf(JDialog dialog){return (JFrame)SwingUtilities.getWindowAncestor(dialog.getOwner());}
    private static String describe(String title,List<Object> rows){StringBuilder out=new StringBuilder(title).append(":\n");for(Object raw:rows){Map<String,Object> row=Data.map(raw);out.append("• ").append(value(row.get("name")!=null?row.get("name"):row.get("player"))).append("  ").append(value(row.get("club"))).append("\n");}return out.append("\n").toString();}
    private static void fill(JComboBox<Choice> combo,List<Object> rows,String id,String label,boolean club){for(Object raw:rows){Map<String,Object> row=Data.map(raw);combo.addItem(new Choice(String.valueOf(row.get(id)),String.valueOf(row.get(label)),club?String.valueOf(row.get("clubId")):null));}}
    private static Choice choice(JComboBox<Choice> combo){Choice value=(Choice)combo.getSelectedItem();if(value==null)throw new IllegalStateException("Não há opções disponíveis");return value;}
    private static String id(JComboBox<Choice> combo){return choice(combo).id;}
    private static List<Object> list(Map<String,Object> data,String key){Object value=data.get(key);return value instanceof List?Data.list(value):Collections.<Object>emptyList();}
    private static String value(Object value){return value==null?"—":String.valueOf(value);}
    private static final class Choice {final String id,label,extra;Choice(String id,String label){this(id,label,null);}Choice(String id,String label,String extra){this.id=id;this.label=label;this.extra=extra;}@Override public String toString(){return label;}}
}
