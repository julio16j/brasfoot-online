package brasfoot;

import javax.swing.JComboBox;

/** Context-menu intent remains selected through the management form. */
public final class NativeCareerDialogsTest {
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    public static void main(String[] args){
        JComboBox<NativeCareerDialogs.Choice> actions=new JComboBox<NativeCareerDialogs.Choice>();
        actions.addItem(new NativeCareerDialogs.Choice("renew","Renew"));actions.addItem(new NativeCareerDialogs.Choice("list","List"));
        JComboBox<NativeCareerDialogs.Choice> players=new JComboBox<NativeCareerDialogs.Choice>();players.addItem(new NativeCareerDialogs.Choice("11","First"));players.addItem(new NativeCareerDialogs.Choice("22","Second"));
        JComboBox<String> sale=new JComboBox<String>(new String[]{"true","false"});
        NativeCareerDialogs.selectRequested(actions,players,sale,"Renovar contrato:22");
        check("renew".equals(((NativeCareerDialogs.Choice)actions.getSelectedItem()).id),"Renew action is selected");check("22".equals(((NativeCareerDialogs.Choice)players.getSelectedItem()).id),"Context player ID selects the second player");
        NativeCareerDialogs.selectRequested(actions,players,sale,"Retirar da lista de transferências:11");
        check("list".equals(((NativeCareerDialogs.Choice)actions.getSelectedItem()).id),"List action is selected");check("11".equals(((NativeCareerDialogs.Choice)players.getSelectedItem()).id),"Context player ID selects the requested player");check("false".equals(sale.getSelectedItem()),"Remove intent sets forSale=false");
        NativeCareerDialogs.selectRequested(actions,players,sale,"Colocar à venda:22");check("true".equals(sale.getSelectedItem()),"Place intent sets forSale=true");
        NativeCareerDialogs.selectRequested(actions,players,sale,"Renovar contrato:999");check(players.getSelectedItem()==null,"Stale context ID does not leave another player selected");
        System.out.println("PASS contextual management action and player identity selection");
    }
}
