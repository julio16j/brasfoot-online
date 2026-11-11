package brasfoot;
import best.F;
import best.I;
import best.ah;
import best.al;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Graphical online session built around the original Brasfoot Swing panels. */
public final class NativeClient {
    private final Path root;
    private final JFrame frame=new JFrame("Brasfoot — Multiplayer Online");
    private final JPanel body=new JPanel(new BorderLayout());
    private final JLabel status=new JLabel("Crie uma sessão ou entre pelo IP do host.");
    private final JTextField address=new JTextField("127.0.0.1",18);
    private final JTextField remotePort=new JTextField("8212",6);
    private final JTextField port=new JTextField("8212",6);
    private final JComboBox<String> sessionMode=new JComboBox<String>(new String[]{"Carreira (1 a 16 técnicos)","Amistoso (2 técnicos)"});
    private final JButton resume=new JButton("Retomar carreira"),advance=new JButton("Próxima rodada");
    private final JComboBox<CoachChoice> choices=new JComboBox<CoachChoice>();
    private final JButton create=new JButton("Criar sessão"), discover=new JButton("Buscar técnicos"), join=new JButton("Entrar"), reconnect=new JButton("Reconectar com token");
    private final JButton auto=new JButton("Autoescalar"), lineup=new JButton("Escalação"), ready=new JButton("Pronto"), substitutions=new JButton("Substituições");
    private final JLabel preparation=new JLabel();
    private final ScheduledExecutorService network=Executors.newSingleThreadScheduledExecutor();
    private SessionApi api;
    private String token;
    private Process server;
    private GameAdapter model;
    private VisualModel visual;
    private JComponent startupOverlay;
    private final JPanel screens=new JPanel(new CardLayout());
    private long displayedRound=-1;
    private String presentationKey="";
    private NativeMatchPanel nativePanel;
    private JPanel preparationPanel;
    private boolean audioEnabled=true;
    private JCheckBox audio;
    private NativeSessionOptions optionsDialog;
    private NativeTraining trainingDialog;
    private JDialog stadiumDialog;
    private boolean optionsRequest;
    private String ownRosterKey="";
    private volatile Map<String,Object> latest;
    private volatile boolean snapshotQueued;
    private int coachId;
    private long halftimeDialogRound=-1;
    private JDialog editor;
    private final Set<JDialog> lineupWindows=Collections.newSetFromMap(new IdentityHashMap<JDialog,Boolean>());
    private boolean connected,closed,testing,autoTest,testReady,testComplete,keepTestHost,testNativeEditor,testYouth,testYouthStarted;
    private boolean testNativeOptions;
    private Path capture;
    private Path preparationCapture;
    private Path testYouthCapture;
    private Integer testYouthObserve;
    private Integer testYouthObserveClub;
    private volatile Throwable failure;
    private static final class CoachChoice {
        final int id;final String label;final boolean claimed;
        CoachChoice(Map<String,Object> row){id=Data.number(row.get("coachId"));claimed=Boolean.TRUE.equals(row.get("claimed"));label=row.get("name")+" — "+row.get("club")+(claimed?" (ocupado)":"");}
        @Override public String toString(){return label;}
    }
    public NativeClient(Path root){
        this.root=root;frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setIconImage(new ImageIcon(getClass().getResource("/aicons/ball.png")).getImage());
        frame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){
            if(server!=null && server.isAlive() && JOptionPane.showConfirmDialog(frame,"Encerrar a sessão e desconectar os outros jogadores? Os checkpoints ficam salvos no host.","Encerrar host",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
            close();
        }});
        body.setBackground(new Color(28,65,35));frame.add(body);frame.add(status,BorderLayout.SOUTH);status.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        connectionScreen();frame.setSize(1280,900);frame.setMinimumSize(new Dimension(1060,800));frame.setLocationRelativeTo(null);
        network.scheduleWithFixedDelay(()->{if(connected && !closed && !snapshotQueued){snapshotQueued=true;try{Map<String,Object> snapshot=api.call("/state",null);SwingUtilities.invokeLater(()->{try{accept(snapshot);}finally{snapshotQueued=false;}});}catch(Exception error){snapshotQueued=false;SwingUtilities.invokeLater(()->status.setText("Conexão interrompida; tentando novamente: "+error.getMessage()));}}},250,250,TimeUnit.MILLISECONDS);
    }
    private void connectionScreen(){
        join.setEnabled(false);
        JPanel pages=new JPanel(new CardLayout());pages.setBackground(new Color(28,65,35));CardLayout layout=(CardLayout)pages.getLayout();
        JPanel home=new JPanel(new BorderLayout(0,34));home.setBackground(new Color(28,65,35));home.setBorder(BorderFactory.createEmptyBorder(58,52,58,52));
        JPanel heading=new JPanel();heading.setOpaque(false);heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));
        JLabel title=new JLabel("Brasfoot Online");title.setFont(new Font("Tahoma",Font.BOLD,34));title.setForeground(Color.WHITE);title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle=new JLabel("Escolha como deseja continuar");subtitle.setFont(new Font("Tahoma",Font.PLAIN,17));subtitle.setForeground(new Color(220,235,218));subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.add(title);heading.add(Box.createVerticalStrut(12));heading.add(subtitle);home.add(heading,BorderLayout.NORTH);
        JPanel options=new JPanel(new GridLayout(1,3,22,0));options.setOpaque(false);options.setPreferredSize(new Dimension(1000,260));
        JButton start=optionButton("Iniciar nova carreira","Configure uma nova sess\u00e3o online",new MenuIcon(0));
        JButton load=optionButton("Retomar carreira","Continue de um save existente",new MenuIcon(1));
        JButton enter=optionButton("Entrar em uma sess\u00e3o","Conecte-se a outro host",new MenuIcon(2));
        options.add(start);options.add(load);options.add(enter);JPanel centered=new JPanel(new GridBagLayout());centered.setOpaque(false);GridBagConstraints optionPosition=new GridBagConstraints();optionPosition.gridx=0;optionPosition.gridy=0;optionPosition.weightx=1;optionPosition.weighty=1;optionPosition.anchor=GridBagConstraints.NORTH;optionPosition.insets=new Insets(48,0,0,0);centered.add(options,optionPosition);home.add(centered,BorderLayout.CENTER);
        JPanel createPage=new JPanel(new BorderLayout(0,28));createPage.setBackground(new Color(28,65,35));createPage.setBorder(BorderFactory.createEmptyBorder(42,70,55,70));
        JButton backFromCreate=new JButton("Voltar");backFromCreate.addActionListener(e->layout.show(pages,"home"));createPage.add(row(backFromCreate),BorderLayout.NORTH);
        JPanel createForm=new JPanel();createForm.setOpaque(false);createForm.setLayout(new BoxLayout(createForm,BoxLayout.Y_AXIS));
        JLabel createTitle=new JLabel("Nova sess\u00e3o");createTitle.setFont(new Font("Tahoma",Font.BOLD,26));createTitle.setForeground(Color.WHITE);createTitle.setAlignmentX(Component.CENTER_ALIGNMENT);createForm.add(createTitle);createForm.add(Box.createVerticalStrut(34));
        createForm.add(row(new JLabel("Tipo de sess\u00e3o:"),sessionMode));createForm.add(row(new JLabel("Porta para os jogadores:"),port));createForm.add(Box.createVerticalStrut(22));
        create.setText("Iniciar");create.setPreferredSize(new Dimension(210,46));JPanel createAction=new JPanel(new FlowLayout(FlowLayout.CENTER));createAction.setOpaque(false);createAction.add(create);createForm.add(createAction);JPanel createCenter=new JPanel(new GridBagLayout());createCenter.setOpaque(false);createCenter.add(createForm);createPage.add(createCenter,BorderLayout.CENTER);
        JPanel enterPage=new JPanel(new BorderLayout(0,24));enterPage.setBackground(new Color(28,65,35));enterPage.setBorder(BorderFactory.createEmptyBorder(42,70,55,70));
        JButton backFromEnter=new JButton("Voltar");backFromEnter.addActionListener(e->layout.show(pages,"home"));enterPage.add(row(backFromEnter),BorderLayout.NORTH);
        JPanel connectForm=new JPanel();connectForm.setOpaque(false);connectForm.setLayout(new BoxLayout(connectForm,BoxLayout.Y_AXIS));
        JLabel connectTitle=new JLabel("Entrar em uma sess\u00e3o");connectTitle.setFont(new Font("Tahoma",Font.BOLD,26));connectTitle.setForeground(Color.WHITE);connectTitle.setAlignmentX(Component.CENTER_ALIGNMENT);connectForm.add(connectTitle);connectForm.add(Box.createVerticalStrut(34));
        connectForm.add(row(new JLabel("IP do host:"),address,new JLabel("Porta:"),remotePort,discover));connectForm.add(row(new JLabel("Seu t\u00e9cnico:"),choices,join));connectForm.add(row(reconnect));
        JPanel connectCenter=new JPanel(new GridBagLayout());connectCenter.setOpaque(false);connectCenter.add(connectForm);enterPage.add(connectCenter,BorderLayout.CENTER);
        pages.add(home,"home");pages.add(createPage,"create");pages.add(enterPage,"enter");
        create.addActionListener(e->createSession());discover.addActionListener(e->discover());join.addActionListener(e->join());
        start.addActionListener(e->layout.show(pages,"create"));enter.addActionListener(e->layout.show(pages,"enter"));
        load.addActionListener(e->{JFileChooser chooser=new JFileChooser(root.resolve("sessions").toFile());chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);chooser.setDialogTitle("Selecione a pasta da sess\u00e3o salva");if(chooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION){resumeDirectory=chooser.getSelectedFile().toPath();createSession();}});
        reconnect.addActionListener(e->{String value=JOptionPane.showInputDialog(frame,"Token de reconex\u00e3o desta sess\u00e3o:");if(value!=null && !value.trim().isEmpty())try{String target=serverAddress();work(()->connect(target,value.trim()),null);}catch(Exception error){status.setText(error.getMessage());}});
        body.removeAll();body.add(pages,BorderLayout.CENTER);body.revalidate();body.repaint();
    }
    private JButton optionButton(String title,String description,Icon icon){
        HoverOptionButton button=new HoverOptionButton("<html><center><b>"+title+"</b><br><br><span style='font-size:11pt'>"+description+"</span></center></html>",icon);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);button.setHorizontalTextPosition(SwingConstants.CENTER);button.setIconTextGap(20);button.setFont(new Font("Tahoma",Font.PLAIN,16));button.setForeground(Color.WHITE);button.setPreferredSize(new Dimension(300,245));return button;
    }
    private static final class HoverOptionButton extends JButton {
        private boolean hovered;
        HoverOptionButton(String text,Icon icon){super(text,icon);setOpaque(false);setContentAreaFilled(false);setFocusPainted(false);setBorder(BorderFactory.createEmptyBorder(28,18,28,18));addMouseListener(new MouseAdapter(){@Override public void mouseEntered(MouseEvent event){hovered=true;repaint();}@Override public void mouseExited(MouseEvent event){hovered=false;repaint();}});}
        @Override protected void paintComponent(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);int arc=58;g.setColor(new Color(0,0,0,hovered?70:38));g.fillRoundRect(4,6,getWidth()-8,getHeight()-9,arc,arc);g.setColor(hovered?new Color(54,108,65):new Color(38,82,48));g.fillRoundRect(1,1,getWidth()-4,getHeight()-8,arc,arc);g.dispose();super.paintComponent(graphics);}
        @Override protected void paintBorder(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setStroke(new BasicStroke(hovered?3f:1.5f));g.setColor(hovered?new Color(181,229,151):new Color(112,157,105));g.drawRoundRect(2,2,getWidth()-6,getHeight()-10,58,58);g.dispose();}
    }
    private static final class MenuIcon implements Icon {
        private final int kind;MenuIcon(int kind){this.kind=kind;}public int getIconWidth(){return 58;}public int getIconHeight(){return 58;}
        public void paintIcon(Component component,Graphics graphics,int x,int y){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.setColor(new Color(92,157,91));g.fillOval(x+3,y+3,52,52);g.setColor(Color.WHITE);
            if(kind==0){g.fillPolygon(new int[]{x+24,x+24,x+40},new int[]{y+16,y+42,y+29},3);}
            else if(kind==1){g.drawRoundRect(x+14,y+21,32,24,4,4);g.drawLine(x+14,y+24,x+14,y+16);g.drawLine(x+14,y+16,x+29,y+16);g.drawLine(x+29,y+16,x+34,y+21);}
            else{g.drawLine(x+19,y+19,x+38,y+29);g.drawLine(x+19,y+39,x+38,y+29);g.fillOval(x+11,y+12,15,15);g.fillOval(x+11,y+32,15,15);g.fillOval(x+33,y+22,15,15);}
            g.dispose();}
    }
    private static JPanel row(Component... components){JPanel row=new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));row.setOpaque(false);for(Component c:components){if(c instanceof JLabel)c.setForeground(Color.WHITE);row.add(c);}row.setMaximumSize(new Dimension(1000,70));return row;}
    private interface Task {void run()throws Exception;}
    private void work(Task task,Runnable success){
        network.execute(()->{try{task.run();if(success!=null)SwingUtilities.invokeLater(success);}catch(Throwable error){failure=error;SwingUtilities.invokeLater(()->showError(error));}});
    }
    private void showStartupProgress(){
        if(testing)return;
        JPanel overlay=new JPanel(new GridBagLayout()){
            @Override protected void paintComponent(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setColor(new Color(0,0,0,165));g.fillRect(0,0,getWidth(),getHeight());g.dispose();}
        };
        overlay.setOpaque(false);overlay.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JPanel content=new JPanel();content.setOpaque(false);content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        JLabel message=new JLabel("Preparando a sessao. Isso pode levar alguns minutos.");message.setForeground(Color.WHITE);message.setFont(message.getFont().deriveFont(Font.BOLD,18f));message.setAlignmentX(Component.CENTER_ALIGNMENT);
        JProgressBar progress=new JProgressBar();progress.setIndeterminate(true);progress.setPreferredSize(new Dimension(360,20));progress.setMaximumSize(new Dimension(360,20));progress.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(message);content.add(Box.createVerticalStrut(18));content.add(progress);overlay.add(content);
        startupOverlay=overlay;frame.getRootPane().setGlassPane(overlay);overlay.setVisible(true);overlay.paintImmediately(0,0,overlay.getWidth(),overlay.getHeight());overlay.repaint();
    }
    private void hideStartupProgress(){if(startupOverlay!=null){startupOverlay.setVisible(false);frame.getRootPane().setGlassPane(new JPanel());startupOverlay=null;}}
    private void showError(Throwable error){hideStartupProgress();create.setEnabled(true);status.setText("Não foi possível concluir: "+error.getMessage());if(testing){error.printStackTrace();close();System.exit(1);}else JOptionPane.showMessageDialog(frame,error.getMessage(),"Brasfoot Online",JOptionPane.WARNING_MESSAGE);}
    private void createSession(){
        try{
            int selectedPort=Integer.parseInt(port.getText().trim());
            if(selectedPort<1024 || selectedPort>65535)throw new IllegalArgumentException("Escolha uma porta entre 1024 e 65535");
            try(ServerSocket probe=new ServerSocket()){probe.setReuseAddress(false);probe.bind(new InetSocketAddress(selectedPort));}
            catch(BindException occupied){throw new IOException("A porta "+selectedPort+" já está em uso. Feche a sessão anterior ou escolha outra porta. Para entrar na sessão existente, use Buscar técnicos.");}
        }catch(Exception error){showError(error);return;}
        create.setEnabled(false);
        boolean career=!testing && sessionMode.getSelectedIndex()==0;
        java.util.List<NativeClubSelection.CoachChoice> selected=null;
        if(career && resumeDirectory==null){
            try{selected=NativeClubSelection.open(frame,root.toFile());}
            catch(Exception error){create.setEnabled(true);showError(error);return;}
            if(selected==null){create.setEnabled(true);return;}
        }
        final java.util.List<NativeClubSelection.CoachChoice> careerSelection=selected;
        status.setText("Criando a sessão no seu computador...");
        showStartupProgress();
        work(()->{
            int selectedPort=Integer.parseInt(port.getText().trim());if(selectedPort<1024 || selectedPort>65535)throw new IllegalArgumentException("Escolha uma porta entre 1024 e 65535");
            String java=Paths.get(System.getProperty("java.home"),"bin","java.exe").toString();
            java.util.List<String> args=new ArrayList<String>();args.add(java);args.add("-Dfile.encoding=UTF-8");args.add("-Djava.awt.headless=true");
            if(career || resumeDirectory!=null){Path agent=root.resolve("build/host-ui-agent.jar");if(!Files.exists(agent))agent=root.resolve("host-ui-agent.jar");if(!Files.exists(agent))agent=root.resolve("dist/host-ui-agent.jar");args.add("-javaagent:"+agent);}
            Collections.addAll(args,"-cp",System.getProperty("java.class.path"),"brasfoot.SessionHost","--root",root.toString());
            if(resumeDirectory!=null){args.add("--resume");args.add(resumeDirectory.toString());}else args.add(career?"--career":"--demo");
            if(career && resumeDirectory==null){
                java.util.List<String> assets=new ArrayList<String>(),names=new ArrayList<String>(),nationalities=new ArrayList<String>();for(NativeClubSelection.CoachChoice choice:careerSelection){assets.add(choice.clubAsset);names.add(choice.coachName);nationalities.add(String.valueOf(choice.nationality));}
                args.add("--club-assets");args.add(String.join(",",assets));args.add("--coach-names");args.add(Base64.getUrlEncoder().withoutPadding().encodeToString(String.join("\n",names).getBytes(StandardCharsets.UTF_8)));
                args.add("--coach-nationalities");args.add(String.join(",",nationalities));
            }
            Collections.addAll(args,"--port",String.valueOf(selectedPort));
            if(testing)Collections.addAll(args,"--tick-ms","75");
            ProcessBuilder builder=new ProcessBuilder(args);builder.directory(root.resolve("game/Brasfoot22-23").toFile());builder.redirectErrorStream(true);
            server=builder.start();BufferedReader output=new BufferedReader(new InputStreamReader(server.getInputStream(),"UTF-8"));String line,hostToken=null;
            StringBuilder startupOutput=new StringBuilder();
            while((line=output.readLine())!=null){if(line.startsWith("Host token: ")){hostToken=line.substring(12);break;}startupOutput.append(line).append('\n');}
            if(hostToken==null){
                Path log=root.resolve("analysis/host-startup.log");Files.createDirectories(log.getParent());Files.write(log,startupOutput.toString().getBytes(StandardCharsets.UTF_8));
                throw new IOException("O host não iniciou. Detalhes em: "+log);
            }
            final BufferedReader rest=output;Thread drain=new Thread(()->{try{while(rest.readLine()!=null){ }}catch(IOException ignored){ }},"host-output");drain.setDaemon(true);drain.start();
            connect("127.0.0.1:"+selectedPort,hostToken);
            final String invite=localAddresses(selectedPort);SwingUtilities.invokeLater(()->preparation.setToolTipText("Compartilhe: "+invite));
            SwingUtilities.invokeLater(()->status.setText("Sessão criada. Compartilhe: "+invite+" — save em sessions/ no host."));
        },()->{hideStartupProgress();create.setEnabled(true);});
    }
    private Path resumeDirectory;
    private static String localAddresses(int port)throws SocketException{
        java.util.List<String> result=new ArrayList<String>();Enumeration<NetworkInterface> interfaces=NetworkInterface.getNetworkInterfaces();while(interfaces.hasMoreElements()){NetworkInterface network=interfaces.nextElement();if(!network.isUp() || network.isLoopback())continue;Enumeration<InetAddress> addresses=network.getInetAddresses();while(addresses.hasMoreElements()){InetAddress ip=addresses.nextElement();if(ip instanceof Inet4Address)result.add(ip.getHostAddress()+":"+port);}}
        return result.isEmpty()?"127.0.0.1:"+port:String.join(" / ",result);
    }
    private String serverAddress(){String host=address.getText().trim();if(host.isEmpty())throw new IllegalArgumentException("Informe o IP do host");if(host.matches(".+:[0-9]+$"))return host;int value=Integer.parseInt(remotePort.getText().trim());if(value<1||value>65535)throw new IllegalArgumentException("Informe uma porta entre 1 e 65535");return host+":"+value;}
    private void discover(){final String target;try{target=serverAddress();}catch(Exception error){status.setText(error.getMessage());return;}work(()->{
        SessionApi connection=new SessionApi(target);Map<String,Object> info=connection.call("/session",null);
        SwingUtilities.invokeLater(()->{choices.removeAllItems();for(Object raw:Data.list(info.get("coaches")))choices.addItem(new CoachChoice(Data.map(raw)));join.setEnabled(true);status.setText("Selecione um tecnico disponivel e clique Entrar.");});
    },null);}
    private void join(){CoachChoice selected=(CoachChoice)choices.getSelectedItem();if(selected==null || selected.claimed){status.setText("Esse tecnico ja esta ocupado. Escolha outro.");return;}final String target;try{target=serverAddress();}catch(Exception error){status.setText(error.getMessage());return;}
        work(()->{SessionApi connection=new SessionApi(target);Map<String,String> data=new LinkedHashMap<String,String>();data.put("coachId",String.valueOf(selected.id));Map<String,Object> result=connection.call("/join",data);connect(target,(String)result.get("token"));},null);
    }
    private void connect(String address,String token)throws Exception{
        SessionApi connection=new SessionApi(address);connection.token(token);Map<String,Object> snapshot=connection.call("/state",null);
        this.api=connection;this.token=token;this.coachId=Data.number(snapshot.get("viewerCoachId"));connected=true;
        SwingUtilities.invokeLater(()->{try{gameScreen(snapshot);}catch(Throwable error){showError(error);}});
    }
    private void gameScreen(Map<String,Object> snapshot)throws Exception{
        optionsRequest=false;if(optionsDialog!=null){optionsDialog.dispose();optionsDialog=null;}
        visual=new VisualModel(root.toFile(),snapshot);c.a.vH();
        SessionOptions.project(Data.map(snapshot.get("sessionOptions")));
        rebuildRound(snapshot);
        body.removeAll();JPanel controls=new JPanel(new BorderLayout(8,0));controls.setBackground(new Color(225,230,220));JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));actions.setOpaque(false);controls.add(actions,BorderLayout.CENTER);actions.add(auto);actions.add(ready);actions.add(substitutions);
        actions.add(advance);advance.addActionListener(e->command("/advance",new LinkedHashMap<String,String>(),null));
        substitutions.addActionListener(e->openSubstitution());
        audio=new JCheckBox("Áudio original");audio.addActionListener(e->{Map<String,Object> changes=new LinkedHashMap<String,Object>();changes.put("soundsEnabled",audio.isSelected());sendOptions(changes);});actions.add(audio);
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0),"online-options");
        frame.getRootPane().getActionMap().put("online-options",new AbstractAction(){public void actionPerformed(ActionEvent event){openNativeOptions();}});
        actions.add(preparation);JButton copy=new JButton("Token");copy.setMargin(new Insets(2,7,2,7));copy.setToolTipText("Ver token de reconexão");copy.addActionListener(e->JOptionPane.showMessageDialog(frame,new JTextField(token),"Guarde o token desta sessão",JOptionPane.INFORMATION_MESSAGE));JPanel utility=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,6));utility.setOpaque(false);utility.add(copy);controls.add(utility,BorderLayout.EAST);
        auto.addActionListener(e->autoLineup());ready.addActionListener(e->setReady(!isReady()));
        body.add(controls,BorderLayout.NORTH);body.add(screens,BorderLayout.CENTER);
        frame.setTitle("Brasfoot Online — "+own(snapshot).get("club")+(server!=null?" (host)":""));body.revalidate();body.repaint();frame.validate();accept(snapshot);
        if(preparationCapture!=null)capture(preparationCapture);
        if(testNativeOptions)verifyNativeOptionsEntry();
        if(testYouth && !testYouthStarted){testYouthStarted=true;new Thread(this::exerciseNativeYouth,"native-youth-window-test").start();}
        if(testYouthObserve!=null){int expected=testYouthObserve,clubId=testYouthObserveClub==null?visual.coach(coachId).fg().lk():testYouthObserveClub;SwingUtilities.invokeLater(()->{ah observed=visual.club(clubId);boolean professional=false,youth=false;if(observed!=null){for(Object raw:observed.kc())if(((F)raw).gD()==expected)professional=true;for(Object raw:observed.ky())if(((F)raw).gD()==expected)youth=true;}if(!professional||youth){showError(new AssertionError("Guest projection did not receive the promoted junior identity"));return;}System.out.println("YOUTH_OBSERVER_PASS playerId="+expected);close();System.exit(0);});}
        if(autoTest)autoLineup();
    }
    private void verifyNativeOptionsEntry(){
        SwingUtilities.invokeLater(()->{try{
            if(optionsDialog==null || !optionsDialog.isShowing())throw new AssertionError("F3 did not open native preferences");
            NativeSessionOptions opened=optionsDialog;openNativeOptions();if(opened!=optionsDialog)throw new AssertionError("Duplicate preferences window");
            if(!(opened.getContentPane().getComponent(0) instanceof a.fB))throw new AssertionError("Preferences panel is not native");
            assertNoAdditionalOptions(body);opened.dispose();
        }catch(Throwable error){showError(error);}});
        Object key=frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
        frame.getRootPane().getActionMap().get(key).actionPerformed(new ActionEvent(frame,ActionEvent.ACTION_PERFORMED,"F3"));
        System.out.println("OPTIONS_NATIVE F3=true singleDialog=true additionalButton=false");
    }
    private static void assertNoAdditionalOptions(Component component){if(component instanceof JButton && "Configurações".equals(((JButton)component).getText()))throw new AssertionError("Additional settings button remains");if(component instanceof Container)for(Component child:((Container)component).getComponents())assertNoAdditionalOptions(child);}
    private void rebuildRound(Map<String,Object> snapshot)throws Exception{
        if(editor!=null){editor.dispose();editor=null;}
        visual.rebuild(snapshot);screens.removeAll();
        I ownMatch=visual.ownMatch(coachId);Map<String,Object> filterValues=Data.map(Data.map(snapshot.get("sessionOptions")).get("values"));java.util.List<I> shown=MatchPresentation.competitions(visual.matches(),ownMatch,filterValues);
        nativePanel=new NativeMatchPanel(frame,shown,filterValues);presentationKey=MatchPresentation.key(filterValues);nativePanel.matchLineupOpener(this::openMatchDetail);al owner=visual.coach(coachId);
        if(owner.fg()!=null)nativePanel.supportedClub(owner.fg().lk());nativePanel.muted(!audioEnabled || testing);
        nativePanel.setPreferredSize(new Dimension(1024,750));
        JPanel stadium=new components.E(null,0);stadium.setLayout(new GridBagLayout());
        GridBagConstraints layout=new GridBagConstraints();layout.weighty=1;layout.fill=GridBagConstraints.VERTICAL;layout.anchor=GridBagConstraints.NORTH;stadium.add(nativePanel,layout);screens.add(stadium,"MATCH");
        I next=visual.preparationMatch(coachId);
        preparationPanel=null;
        if(owner.fg()!=null && next!=null){
            preparationPanel=NativePreparation.create(frame,owner.fg(),next,()->openLineup(),this::openCareerMenu);
            JPanel dashboard=new JPanel(new BorderLayout());
            if(ownMatch==null){JLabel notice=new JLabel("Sem partida nesta data. Próximo confronto abaixo — somente consulta.",SwingConstants.CENTER);notice.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));dashboard.add(notice,BorderLayout.NORTH);}
            dashboard.add(preparationPanel,BorderLayout.CENTER);screens.add(dashboard,"PREPARING");
        }else screens.add(new JLabel("Sem partida nesta data. Nenhum próximo confronto disponível.",SwingConstants.CENTER),"PREPARING");
        displayedRound=Data.longNumber(Data.map(snapshot.get("round")).get("round"));ownRosterKey=rosterKey(snapshot);screens.revalidate();screens.repaint();
    }
    private String rosterKey(Map<String,Object> snapshot){
        Map<String,Object> owner=own(snapshot);StringBuilder key=new StringBuilder(String.valueOf(owner.get("clubId")));
        for(Object raw:Data.list(Data.map(snapshot.get("game")).get("visualClubs"))){Map<String,Object> club=Data.map(raw);if(Objects.equals(club.get("clubId"),owner.get("clubId"))){key.append(club.get("balance"));for(Object p:Data.list(club.get("players"))){Map<String,Object> player=Data.map(p);key.append('/').append(player.get("playerId")).append(player.get("name")).append(player.get("salary")).append(player.get("contractDays"));}break;}}
        return key.toString();
    }
    private void openCareerMenu(String action){
        if(latest==null)return;
        if("Opções".equals(action)){openNativeOptions();return;}
        if("Juniores".equals(action)){openNativeYouth();return;}
        if("Procura".equalsIgnoreCase(action)){openNativeTransferSearch();return;}
        if("Times".equalsIgnoreCase(action)){openNativeTransferMarket();return;}
        if("Estádio".equalsIgnoreCase(action)){openNativeStadium();return;}
        if("Treino".equalsIgnoreCase(action)){openNativeTraining();return;}
        if("Agenda".equalsIgnoreCase(action)||"Tabelas".equalsIgnoreCase(action)){openNativeCareerScreen(action);return;}
        boolean career=Boolean.TRUE.equals(Data.map(latest.get("game")).get("career"));
        work(()->{
            Map<String,Object> overview=api.call("/overview",null);
            Map<String,Object> management=career?api.call("/management",null):new LinkedHashMap<String,Object>();
            SwingUtilities.invokeLater(()->NativeCareerDialogs.open(frame,action,overview,management,(fields,success,failure)->command("/management",fields,()->{
                if(success!=null)success.run();Map<String,Object> result=Data.map(latest.get("command"));String operation=fields.get("action"),outcome=String.valueOf(result.get("status"));
                if("offer".equals(operation) && "PENDING".equals(outcome))status.setText("Proposta enviada; a transferência aguarda a resposta do técnico vendedor.");
                else if("COMPLETED".equals(outcome) || "ACCEPTED".equals(outcome))status.setText("Transferência concluída; jogador e saldo atualizados.");
            },failure)));
        },null);
    }
    private void openNativeCareerScreen(String action){
        if(latest==null||!Boolean.TRUE.equals(Data.map(latest.get("game")).get("career"))||visual==null)return;
        al owner=visual.coach(coachId);if(owner==null||owner.fg()==null)return;final Map<String,Object>[] catalog=new Map[1];final SessionApi connection=api;
        work(()->catalog[0]=connection.call("/calendar-catalog",null),()->{if(api!=connection||closed)return;try{visual.projectCareerCalendar(catalog[0]);if("Agenda".equalsIgnoreCase(action))NativeCareerScreens.openCalendar(frame,owner.fg());else{best.at selected=null;I next=visual.preparationMatch(coachId);if(next!=null)selected=visual.remoteCompetition(CompetitionIdentity.key(next.hy()));if(selected==null){int country=owner.fg().getPais();for(Object raw:Data.list(catalog[0].get("tables"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("country"))==country&&Data.number(row.get("division"))==1){selected=visual.remoteCompetition(String.valueOf(row.get("competitionKey")));break;}}}if(selected==null)for(Object raw:Data.list(catalog[0].get("tables"))){selected=visual.remoteCompetition(String.valueOf(Data.map(raw).get("competitionKey")));if(selected!=null)break;}NativeCareerScreens.openTables(frame,selected);}}catch(Throwable error){showError(error);}});
    }
    private void openNativeYouth(){
        if(!Boolean.TRUE.equals(Data.map(latest.get("game")).get("career"))){JOptionPane.showMessageDialog(frame,"A academia de juniores exige uma carreira online.","Juniores",JOptionPane.INFORMATION_MESSAGE);return;}
        al owner=visual.coach(coachId);if(owner==null||owner.fg()==null)return;ah club=owner.fg();
        NativeYouth.open(frame,club,(playerId,dialog,button)->{Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","promote");fields.put("playerId",String.valueOf(playerId));
            command("/management",fields,()->{dialog.dispose();status.setText("Junior promovido pelo host; elenco e base atualizados.");if(testYouth){System.out.println("YOUTH_PROMOTION_PASS playerId="+playerId);close();System.exit(0);}},()->button.setEnabled(true));});
    }
    private void exerciseNativeYouth(){try{
        if(preparationPanel==null || !Boolean.TRUE.equals(Data.map(latest.get("game")).get("career")))throw new AssertionError("Career preparation panel unavailable");
        JButton access=(JButton)Data.get(preparationPanel,"HW");Robot robot=new Robot();robot.setAutoDelay(100);frame.setAlwaysOnTop(true);frame.toFront();frame.requestFocus();clickRobot(robot,access);
        JDialog dialog=null;long deadline=System.currentTimeMillis()+5000;while(dialog==null&&System.currentTimeMillis()<deadline){dialog=findYouthDialog();Thread.sleep(50);}if(dialog==null)throw new AssertionError("Native academy did not open from HW");
        a.ds panel=(a.ds)dialog.getContentPane().getComponent(0);JTable table=(JTable)Data.get(panel,"FR");JButton promote=(JButton)Data.get(panel,"FQ");
        if(table.getColumnCount()!=11||table.getRowCount()!=visual.club(visual.coach(coachId).fg().lk()).ky().size())throw new AssertionError("Native academy does not show the projected host base");
        if(!promote.isEnabled()||((JButton)Data.get(panel,"FP")).isEnabled()||((JButton)Data.get(panel,"FO")).isEnabled()||((JButton)Data.get(panel,"FN")).isEnabled())throw new AssertionError("Unsupported academy actions are not locked");
        if(testYouthCapture!=null){Rectangle bounds=dialog.getBounds();ImageIO.write(robot.createScreenCapture(bounds),"png",testYouthCapture.toFile());}
        SwingUtilities.invokeAndWait(()->table.setRowSelectionInterval(0,0));clickRobot(robot,promote);JDialog confirmation=null;deadline=System.currentTimeMillis()+5000;while(confirmation==null&&System.currentTimeMillis()<deadline){confirmation=findOptionDialog();Thread.sleep(50);}if(confirmation==null)throw new AssertionError("Native promotion confirmation did not open");clickRobot(robot,firstButton(confirmation));
    }catch(Throwable error){SwingUtilities.invokeLater(()->showError(error));}}
    private static JDialog findYouthDialog(){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()&&containsClass(((JDialog)window).getContentPane(),a.ds.class))return (JDialog)window;return null;}
    private static JDialog findOptionDialog(){for(Window window:Window.getWindows())if(window instanceof JDialog&&window.isShowing()&&containsOption(((JDialog)window).getContentPane()))return (JDialog)window;return null;}
    private static boolean containsClass(Container container,Class<?> type){if(type.isInstance(container))return true;for(Component component:container.getComponents())if(component instanceof Container&&containsClass((Container)component,type))return true;return false;}
    private static boolean containsOption(Container container){if(container instanceof JOptionPane)return true;for(Component component:container.getComponents())if(component instanceof Container&&containsOption((Container)component))return true;return false;}
    private static JButton firstButton(Container container){for(Component component:container.getComponents()){if(component instanceof JButton)return (JButton)component;if(component instanceof Container){try{return firstButton((Container)component);}catch(IllegalStateException ignored){}}}throw new IllegalStateException("Option dialog has no button");}
    private static void clickRobot(Robot robot,Component component)throws Exception{Point[] point={null};SwingUtilities.invokeAndWait(()->{point[0]=component.getLocationOnScreen();point[0].translate(component.getWidth()/2,component.getHeight()/2);});robot.mouseMove(point[0].x,point[0].y);robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);robot.waitForIdle();}
    private void openNativeOptions(){
        if(latest==null || !connected || closed)return;
        if(optionsDialog!=null && optionsDialog.isShowing()){optionsDialog.toFront();return;}
        if(optionsDialog==null || !optionsDialog.isDisplayable())optionsDialog=new NativeSessionOptions(frame,Data.map(latest.get("sessionOptions")),this::sendOptions);
        optionsDialog.update(Data.map(latest.get("sessionOptions")),optionsRequest);optionsDialog.setVisible(true);
    }
    private void openNativeStadium(){
        al owner=visual.coach(coachId);if(owner==null || owner.fg()==null)return;I fixture=visual.preparationMatch(coachId);
        int competitionType=fixture==null?0:fixture.hy().b();
        stadiumDialog=NativeStadium.openWithPriceSubmit(frame,owner.fg(),competitionType,(fields,success,failure)->command("/management",fields,()->{Map<String,Object> response=Data.map(latest.get("command"));if("CONSTRUCTION_STARTED".equals(response.get("status")))success.run();else failure.accept(new IllegalStateException(String.valueOf(response.get("message"))));},()->failure.accept(new IllegalStateException("Host rejected the stadium expansion"))),(fields,success,failure)->command("/management",fields,()->{Map<String,Object> response=Data.map(latest.get("command"));if("STADIUM_SETTINGS_UPDATED".equals(response.get("status")))success.run();else failure.accept(new IllegalStateException(String.valueOf(response.get("message"))));},()->failure.accept(new IllegalStateException("Host rejected the stadium update"))));
    }
    private void openNativeTraining(){al owner=visual.coach(coachId);if(owner==null || owner.fg()==null)return;
        if(trainingDialog==null || !trainingDialog.isDisplayable())trainingDialog=new NativeTraining(frame,owner.fg(),this::sendTraining);
        trainingDialog.update(owner.fg(),trainingAvailable());trainingDialog.setVisible(true);
    }
    private boolean trainingAvailable(){return latest!=null && !Boolean.TRUE.equals(latest.get("storageFailed")) && "PREPARING".equals(Data.map(latest.get("round")).get("phase")) && !isReady();}
    private void sendTraining(Map<String,String> fields,Runnable success,Runnable onFailure){
        fields.put("round",String.valueOf(Data.map(latest.get("round")).get("round")));fields.put("revision",String.valueOf(ownRound().get("revision")));
        final SessionApi connection=api;
        network.execute(()->{try{Map<String,Object> result=connection.call("/management",fields);SwingUtilities.invokeLater(()->{if(closed || api!=connection)return;accept(result);success.run();});}
            catch(Throwable error){Map<String,Object> confirmed=null;try{confirmed=connection.call("/state",null);}catch(Exception ignored){}final Map<String,Object> restored=confirmed;
                SwingUtilities.invokeLater(()->{if(closed || api!=connection)return;if(restored!=null)accept(restored);onFailure.run();showError(error);});}});
    }
    private String playerSearchUnavailable(){if(latest==null)return "Host desconectado";Object raw=latest.get("playerSearch");if(raw instanceof Map){Map<String,Object> availability=Data.map(raw);if(Boolean.FALSE.equals(availability.get("available")))return String.valueOf(availability.get("reason"));}return null;}
    private void openNativeTransferSearch(){
        String unavailable=playerSearchUnavailable();if(unavailable!=null){status.setText(unavailable);return;}
        if(latest!=null && Boolean.TRUE.equals(latest.get("storageFailed"))){status.setText("Procura indisponível enquanto o salvamento da carreira estiver pausado.");return;}
        al owner=visual.coach(coachId);if(owner==null || owner.fg()==null)return;
        int mode=c.a.SR.isHabilidadeIndividual()?2:1;
        NativeTransferSearch.open(frame,owner.fg(),mode,this::submitNativeTransfer,this::searchNativePlayers,this::submitNativeYouthPromotion,this::queryNativeAcademy,this::queryNativeClubAgenda,playingCountryCatalog());
    }
    private java.util.List<Object> playingCountryCatalog(){if(latest==null||latest.get("sessionOptions")==null)return new ArrayList<Object>();Object countries=Data.map(latest.get("sessionOptions")).get("leagueCountries");return countries instanceof java.util.List?Data.list(countries):new ArrayList<Object>();}
    private void searchNativePlayers(Map<String,String> filters,java.util.function.Consumer<Map<String,Object>> success,java.util.function.Consumer<Throwable> failure){final SessionApi connection=api;String unavailable=playerSearchUnavailable();if(unavailable!=null){failure.accept(new IllegalStateException(unavailable));return;}if(connection==null){failure.accept(new IllegalStateException("Host is disconnected"));return;}if(latest!=null&&Boolean.TRUE.equals(latest.get("storageFailed"))){failure.accept(new IllegalStateException("A Procura está indisponível enquanto o salvamento estiver pausado"));return;}network.execute(()->{try{java.util.List<Object> players=new ArrayList<Object>();int offset=0,total=-1;String revision=null,mode=null;long date=0;
            do{Map<String,String> request=new LinkedHashMap<String,String>(filters);request.put("offset",String.valueOf(offset));request.put("limit","1000");if(revision!=null)request.put("queryRevision",revision);Map<String,Object> page=connection.call("/players/search",request);int pageTotal=Data.number(page.get("total"));String pageRevision=String.valueOf(page.get("queryRevision")),pageMode=String.valueOf(page.get("mode"));long pageDate=Data.longNumber(page.get("dateMillis"));if(!NativeTransferSearch.sameSearchPageSnapshot(total,mode,revision,date,pageTotal,pageMode,pageRevision,pageDate))throw new IllegalStateException("Search snapshot changed while loading");total=pageTotal;revision=pageRevision;mode=pageMode;date=pageDate;java.util.List<Object> batch=Data.list(page.get("players"));players.addAll(batch);Object next=page.get("nextOffset");if(next==null)break;int nextOffset=Data.number(next);if(nextOffset<=offset||batch.isEmpty())throw new IllegalStateException("Host returned an invalid player page");offset=nextOffset;}while(players.size()<total);
            if(players.size()!=total)throw new IllegalStateException("Host returned an incomplete player list");Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("players",players);result.put("total",total);result.put("mode",mode);result.put("dateMillis",date);result.put("queryRevision",revision);SwingUtilities.invokeLater(()->{if(closed||api!=connection)return;long current=latest==null?-1:Data.longNumber(Data.map(latest.get("round")).get("revision"));if(!NativeTransferSearch.isSearchSnapshotCurrent(current,result.get("queryRevision"))){failure.accept(new IllegalStateException("A carreira mudou durante a busca; execute a Procura novamente"));return;}success.accept(result);});
        }catch(Throwable error){SwingUtilities.invokeLater(()->{if(!closed&&api==connection)failure.accept(error);});}});}
    private void openNativeTransferMarket(){
        al owner=visual.coach(coachId);if(owner==null || owner.fg()==null)return;
        NativeTransferMarket.open(frame,owner.fg(),this::submitNativeTransfer,this::submitNativeYouthPromotion,this::queryNativeAcademy,this::queryNativeClubAgenda);
    }
    private void queryNativeAcademy(int clubId,java.util.function.Consumer<Map<String,Object>> success,java.util.function.Consumer<Throwable> failure){final SessionApi connection=api;if(connection==null||latest==null){failure.accept(new IllegalStateException("Host disconnected"));return;}final String expectedRevision=String.valueOf(Data.longNumber(Data.map(latest.get("round")).get("revision")));network.execute(()->{try{Map<String,String> request=new LinkedHashMap<String,String>();request.put("clubId",String.valueOf(clubId));request.put("queryRevision",expectedRevision);Map<String,Object> result=connection.call("/club-academy",request);SwingUtilities.invokeLater(()->{if(closed||api!=connection)return;long current=Data.longNumber(Data.map(latest.get("round")).get("revision"));if(!NativeTransferMarket.academySnapshotCurrent(current,result)){failure.accept(new IllegalStateException("A carreira mudou durante a consulta; abra a academia novamente"));return;}success.accept(result);});}catch(Throwable error){SwingUtilities.invokeLater(()->{if(!closed&&api==connection)failure.accept(error);});}});}
    private void queryNativeClubAgenda(int clubId,java.util.function.Consumer<Map<String,Object>> success,java.util.function.Consumer<Throwable> failure){final SessionApi connection=api;if(connection==null||latest==null){failure.accept(new IllegalStateException("Host disconnected"));return;}final String expectedRevision=String.valueOf(Data.longNumber(Data.map(latest.get("round")).get("revision")));network.execute(()->{try{Map<String,String> request=new LinkedHashMap<String,String>();request.put("clubId",String.valueOf(clubId));request.put("queryRevision",expectedRevision);Map<String,Object> result=connection.call("/club-agenda",request);SwingUtilities.invokeLater(()->{if(closed||api!=connection)return;long current=Data.longNumber(Data.map(latest.get("round")).get("revision"));if(!NativeTransferMarket.agendaSnapshotCurrent(clubId,current,result)){failure.accept(new IllegalStateException("A carreira mudou durante a consulta; abra a agenda novamente"));return;}success.accept(result);});}catch(Throwable error){SwingUtilities.invokeLater(()->{if(!closed&&api==connection)failure.accept(error);});}});}
    private void submitNativeYouthPromotion(int playerId,JDialog dialog,JButton button){Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("action","promote");fields.put("playerId",String.valueOf(playerId));command("/management",fields,()->{dialog.dispose();status.setText("Junior promovido pelo host; elenco e base atualizados.");},()->button.setEnabled(true));}
    private void submitNativeTransfer(Map<String,String> fields,Runnable success,Runnable onFailure){
        command("/management",fields,()->{
            Map<String,Object> result=Data.map(latest.get("command"));String outcome=String.valueOf(result.get("status"));
            if("COUNTEROFFER".equals(outcome)){
                int price=Data.number(result.get("counterPrice")),salary=Data.number(result.get("counterSalary"));
                String prompt=counterofferText(price,salary);
                if(JOptionPane.showConfirmDialog(frame,prompt,"Contraproposta da IA",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    Map<String,String> counter=new LinkedHashMap<String,String>(fields);counter.put("acceptCounter","true");counter.put("offerAmount",String.valueOf(result.get("offerAmount")));
                    counter.put("price",String.valueOf(price));counter.put("counterSalary",String.valueOf(salary));submitNativeTransfer(counter,success,onFailure);return;
                }
                status.setText("Contraproposta recusada; você pode ajustar o valor e enviar outra oferta.");if(onFailure!=null)onFailure.run();return;
            }
            if("PENDING".equals(outcome))status.setText("Proposta enviada; aguardando a resposta do técnico vendedor.");
            else status.setText("Transferência concluída; elenco e saldo atualizados pelo host.");
            if(success!=null)success.run();
        },onFailure);
    }
    static String counterofferText(int price,int salary){
        String salaryText=salary>0?best.C.c(salary):"manter o salário atual (sem reajuste)";
        return "O clube fez uma contraproposta:\n\nValor do passe: "+best.C.c(price)+"\nSalário: "+salaryText+"\n\nDeseja aceitar?";
    }
    private void openSubstitution(){
        if(latest==null)return;Map<String,Object> current=own(latest),lineupState=Data.map(current.get("liveLineup"));
        java.util.List<Object> starters=Data.list(lineupState.get("starters")),bench=Data.list(lineupState.get("bench"));if(starters.isEmpty() || bench.isEmpty())return;
        JDialog dialog=new JDialog(frame,"Substituição durante a partida",true);JPanel panel=new JPanel(new GridLayout(0,2,10,10));panel.setBorder(BorderFactory.createEmptyBorder(18,18,12,18));
        JComboBox<PlayerChoice> outgoing=new JComboBox<PlayerChoice>(),incoming=new JComboBox<PlayerChoice>();
        for(Object raw:starters)outgoing.addItem(new PlayerChoice(Data.map(raw)));for(Object raw:bench)incoming.addItem(new PlayerChoice(Data.map(raw)));
        panel.add(new JLabel("Sai de campo"));panel.add(outgoing);panel.add(new JLabel("Entra em campo"));panel.add(incoming);
        panel.add(new JLabel("Substituições restantes: "+current.get("substitutionsRemaining")));
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton apply=new JButton("Confirmar"),cancel=new JButton("Cancelar");buttons.add(apply);buttons.add(cancel);panel.add(buttons);
        dialog.setContentPane(panel);dialog.pack();dialog.setLocationRelativeTo(frame);cancel.addActionListener(event->dialog.dispose());
        apply.addActionListener(event->{PlayerChoice out=(PlayerChoice)outgoing.getSelectedItem(),in=(PlayerChoice)incoming.getSelectedItem();if(out==null || in==null)return;Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("outgoingPlayerId",String.valueOf(out.id));fields.put("incomingPlayerId",String.valueOf(in.id));apply.setEnabled(false);command("/substitute",fields,()->{dialog.dispose();status.setText("Substituição aplicada: "+out.label+" saiu; "+in.label+" entrou.");});});
        dialog.setVisible(true);
    }
    private static final class PlayerChoice {final int id;final String label;PlayerChoice(Map<String,Object> row){id=Data.number(row.get("playerId"));label=row.get("name")+" — "+(Data.number(row.get("position"))==0?"Goleiro":"Jogador");}@Override public String toString(){return label;}}
    private Map<String,Object> own(Map<String,Object> state){for(Object raw:Data.list(Data.map(state.get("game")).get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==coachId)return row;}throw new IllegalStateException("Técnico não encontrado");}
    private Map<String,Object> ownRound(){for(Object raw:Data.list(Data.map(latest.get("round")).get("coaches"))){Map<String,Object> row=Data.map(raw);if(Data.number(row.get("coachId"))==coachId)return row;}throw new IllegalStateException("Técnico não encontrado");}
    private boolean isReady(){return Boolean.TRUE.equals(ownRound().get("ready"));}
    private void accept(Map<String,Object> snapshot){
        if(closed || nativePanel==null)return;latest=snapshot;NativeYouth.observeRevision(Data.longNumber(Data.map(snapshot.get("round")).get("revision")));NativeAgenda.observeRevision(Data.longNumber(Data.map(snapshot.get("round")).get("revision")));
        try{
            Map<String,Object> settings=Data.map(snapshot.get("sessionOptions")),filterValues=Data.map(settings.get("values"));SessionOptions.project(settings);String nextPresentationKey=MatchPresentation.key(filterValues);boolean filtersChanged=!nextPresentationKey.equals(presentationKey);
            audioEnabled=Boolean.TRUE.equals(filterValues.get("soundsEnabled"));audio.setSelected(audioEnabled);audio.setEnabled(Boolean.TRUE.equals(settings.get("canEdit")) && !optionsRequest);nativePanel.muted(!audioEnabled || testing);
            if(optionsDialog!=null && optionsDialog.isDisplayable())optionsDialog.update(settings,optionsRequest);
            NativeSessionOptions.refreshWindows();
            if(displayedRound!=Data.longNumber(Data.map(snapshot.get("round")).get("round")) || ((editor==null || !editor.isDisplayable()) && !ownRosterKey.equals(rosterKey(snapshot))))rebuildRound(snapshot);
            else if(filtersChanged){I ownMatch=visual.ownMatch(coachId);nativePanel.presentation(MatchPresentation.competitions(visual.matches(),ownMatch,filterValues),filterValues);presentationKey=nextPresentationKey;}
            if(editor==null || !editor.isDisplayable()){projectModel(snapshot);al youthOwner=visual.coach(coachId);if(youthOwner!=null)NativeYouth.refreshOpenWindows(youthOwner.fg());}else visual.updateTraining(snapshot);
            if(stadiumDialog!=null && stadiumDialog.isDisplayable()){al stadiumOwner=visual.coach(coachId);if(stadiumOwner!=null)NativeStadium.WindowState.refresh(stadiumDialog,stadiumOwner.fg());}
            if(trainingDialog!=null && trainingDialog.isDisplayable())trainingDialog.update(visual.coach(coachId).fg(),trainingAvailable());
            nativePanel.project(snapshot);
            String phase=(String)Data.map(snapshot.get("round")).get("phase");String blocked=lineupBlockReason();boolean edit=blocked==null;
            ((CardLayout)screens.getLayout()).show(screens,phase.equals("PREPARING")?"PREPARING":"MATCH");
            advance.setVisible(Boolean.TRUE.equals(Data.map(snapshot.get("game")).get("career")) && Boolean.TRUE.equals(snapshot.get("host")));advance.setEnabled(phase.equals("FINISHED"));
            Map<String,Object> livePlayback=Data.map(snapshot.get("playback"));String liveStatus=String.valueOf(livePlayback.get("status"));Map<String,Object> liveLineup=Data.map(own(snapshot).get("liveLineup"));
            boolean halftime=phase.equals("HALF_TIME");boolean playing=halftime || (phase.equals("RUNNING") && (liveStatus.equals("FIRST_HALF") || liveStatus.equals("SECOND_HALF")));
            substitutions.setVisible(playing);substitutions.setEnabled(playing && Data.number(own(snapshot).get("substitutionsRemaining"))>0 && !Data.list(liveLineup.get("bench")).isEmpty());
            auto.setEnabled(edit);lineup.setEnabled(edit);ready.setVisible(phase.equals("PREPARING") || halftime);ready.setEnabled((phase.equals("PREPARING") && (editor==null || !editor.isDisplayable())) || halftime);
            auto.setToolTipText(blocked);lineup.setToolTipText(blocked);
            if(preparationPanel!=null){NativePreparation.lineupAvailability(preparationPanel,edit,blocked);String searchReason=playerSearchUnavailable();NativePreparation.playerSearchAvailability(preparationPanel,searchReason==null,searchReason);}
            ready.setText(halftime?(isReady()?"Retirar pronto (2º tempo)":"Pronto para o 2º tempo"):(isReady()?"Retirar Pronto":"Pronto"));
            boolean hasMatch=Boolean.TRUE.equals(own(snapshot).get("hasMatch"));
            int readyCount=0;for(Object raw:Data.list(Data.map(snapshot.get("round")).get("coaches")))if(Boolean.TRUE.equals(Data.map(raw).get("ready")))readyCount++;
            preparation.setText(Data.map(snapshot.get("game")).get("date")+(hasMatch?"":" — Sem partida nesta data")+" — "+readyCount+"/"+Data.list(Data.map(snapshot.get("round")).get("coaches")).size()+" prontos");
            Map<String,Object> live=Data.map(snapshot.get("playback"));
            if(Boolean.TRUE.equals(snapshot.get("storageFailed")))status.setText("O host pausou a sessão por falha ao gravar o save.");
            else if(phase.equals("FINISHED"))status.setText("Fim de jogo — resultado salvo. O host pode avançar para a próxima rodada.");
            else if(phase.equals("FAILED"))status.setText("Partida interrompida: "+live.get("failure"));
            else if(halftime){int halftimeReady=0;for(Object raw:Data.list(Data.map(snapshot.get("round")).get("coaches")))if(Boolean.TRUE.equals(Data.map(raw).get("ready")))halftimeReady++;status.setText("Intervalo — ajustes de escalação liberados; "+halftimeReady+"/"+Data.list(Data.map(snapshot.get("round")).get("coaches")).size()+" prontos para o segundo tempo.");}
            else if(phase.equals("PREPARING"))status.setText(!hasMatch?"Sem partida nesta data. Confirme Pronto para aguardar os jogos dos demais clubes.":blocked==null?"Prepare a escalação do jogo desta data e confirme Pronto.":blocked);
            else if(!hasMatch && phase.equals("RUNNING"))status.setText("Sem partida nesta data. A lista mostra todos os jogos desta data; aguarde a conclusão.");
            if(!halftime)halftimeDialogRound=-1;
            else if(halftimeDialogRound!=Data.longNumber(Data.map(snapshot.get("round")).get("round"))){halftimeDialogRound=Data.longNumber(Data.map(snapshot.get("round")).get("round"));if(testing && autoTest)setReady(true);else if(Boolean.TRUE.equals(Data.map(settings.get("values")).get("openLineupAtHalfTime")) && !ownLineupWindowOpen()){I intervalMatch=visual.ownMatch(coachId);if(intervalMatch!=null)openMatchDetail(intervalMatch);}}
            if(testing && phase.equals("FINISHED"))finishTest(snapshot);
        }catch(Throwable error){showError(error);}
    }
    private void sendOptions(Map<String,Object> changes){
        if(optionsRequest || latest==null)return;Map<String,Object> settings=Data.map(latest.get("sessionOptions"));if(!Boolean.TRUE.equals(settings.get("canEdit")))return;
        optionsRequest=true;if(optionsDialog!=null)optionsDialog.update(settings,true);audio.setEnabled(false);
        Map<String,String> fields=new LinkedHashMap<String,String>();fields.put("optionsRevision",String.valueOf(settings.get("revision")));fields.put("changes",Json.write(changes));
        final SessionApi connection=api;
        network.execute(()->{try{Map<String,Object> result=connection.call("/session-options",fields);SwingUtilities.invokeLater(()->{if(api!=connection || closed)return;optionsRequest=false;accept(result);});}catch(Throwable error){Map<String,Object> confirmed=null;try{confirmed=connection.call("/state",null);}catch(Exception ignored){}final Map<String,Object> restored=confirmed;SwingUtilities.invokeLater(()->{if(api!=connection || closed)return;optionsRequest=false;if(restored!=null)accept(restored);else accept(latest);showError(error);});}});
    }
    @SuppressWarnings("unchecked")
    private void projectModel(Map<String,Object> snapshot){
        if(visual!=null){try{visual.update(snapshot);}catch(Exception error){throw new IllegalStateException("Não foi possível atualizar a interface original",error);}return;}
        for(Object raw:Data.list(Data.map(snapshot.get("game")).get("coaches"))){Map<String,Object> row=Data.map(raw);int id=Data.number(row.get("coachId"));al local=null;for(Object value:c.a.SR.M())if(((al)value).lT()==id)local=(al)value;if(local==null)throw new IllegalStateException("Técnico desconhecido no modelo visual");
            ah club=local.fg();I match=model.currentMatches().get(0);java.util.List<F> players=new ArrayList<F>(club.kc()),ordered=new ArrayList<F>();
            club.kY().clear();club.kZ().clear();java.util.List<F> all=match.hc()==club?match.hl():match.hm(),active=match.hc()==club?match.hp():match.hq(),bench=match.hc()==club?match.hn():match.ho();all.clear();active.clear();bench.clear();
            for(Object value:Data.list(row.get("players"))){Map<String,Object> p=Data.map(value);F player=null;for(F candidate:players)if(!ordered.contains(candidate) && candidate.getNome().equals(p.get("name"))){player=candidate;break;}if(player==null)throw new IllegalStateException("Elenco local diferente do host");ordered.add(player);
                player.setIdade(Data.number(p.get("age")));player.ad(Data.number(p.get("strength")));player.ai(Data.number(p.get("fitness")));player.setLado(Data.number(p.get("side")));player.setCr1(Data.number(p.get("skill1")));player.setCr2(Data.number(p.get("skill2")));player.setStatus(Data.number(p.get("status")));player.a(Boolean.TRUE.equals(p.get("star")));player.j(Boolean.TRUE.equals(p.get("worldClass")));player.as(Data.number(p.get("slot")));if(p.containsKey("injuryUntilMillis"))player.d(Data.longNumber(p.get("injuryUntilMillis")));
                if(player.fT()>=1 && player.fT()<=25){club.kY().add(player);all.add(player);active.add(player);}else if(player.fT()>=26 && player.fT()<=36){club.kZ().add(player);bench.add(player);}
            }
            club.kc().clear();club.kc().addAll(ordered);if(row.containsKey("tactics")){java.util.List<Object> t=Data.list(row.get("tactics"));int[] values=new int[4];for(int i=0;i<4;i++)values[i]=Data.number(t.get(i));club.k(values);}
        }
    }
    private void command(String path,Map<String,String> fields,Runnable success){
        command(path,fields,success,null);
    }
    private void command(String path,Map<String,String> fields,Runnable success,Runnable onFailure){
        Map<String,Object> round=Data.map(latest.get("round"));fields.put("round",String.valueOf(Data.longNumber(round.get("round"))));fields.put("revision",String.valueOf(Data.longNumber(ownRound().get("revision"))));
        network.execute(()->{try{Map<String,Object> snapshot=api.call(path,fields);SwingUtilities.invokeLater(()->{accept(snapshot);if(success!=null)success.run();});}
            catch(Throwable error){failure=error;SwingUtilities.invokeLater(()->{showError(error);if(onFailure!=null)onFailure.run();});}});
    }
    private String lineupBlockReason(){
        if(latest==null)return "Entre em uma sessão para escalar.";
        if(!Data.map(latest.get("round")).get("phase").equals("PREPARING"))return "Escalação disponível na preparação da data.";
        if(!Boolean.TRUE.equals(own(latest).get("hasMatch")))return "Sem partida nesta data. A escalação fica disponível no dia do jogo.";
        if(isReady())return "Retire o Pronto para editar a escalação.";
        if(editor!=null && editor.isDisplayable())return "Conclua ou feche a janela de escalação aberta.";
        return null;
    }
    private void autoLineup(){String reason=lineupBlockReason();if(reason!=null){status.setText(reason);return;}Map<String,Object> suggestion=Data.map(own(latest).get("suggestion"));Map<String,String> data=new LinkedHashMap<String,String>();data.put("starters",Data.csv(Data.list(suggestion.get("starters"))));data.put("bench",Data.csv(Data.list(suggestion.get("bench"))));command("/lineup",data,()->{status.setText("Escalação enviada. Você pode ajustar na tela original antes do Pronto.");if(autoTest && !testReady){testReady=true;if(testNativeEditor)openLineup();else setReady(true);}});}
    private void setReady(boolean value){Map<String,String> data=new LinkedHashMap<String,String>();data.put("ready",String.valueOf(value));String path=Data.map(latest.get("round")).get("phase").equals("HALF_TIME")?"/halftime-ready":"/ready";command(path,data,null);}
    private void openLineup(){String reason=lineupBlockReason();if(reason!=null){status.setText(reason);return;}I match=visual.ownMatch(coachId);al coach=visual.coach(coachId);if(coach==null || match==null){status.setText("Partida atual indisponível. Aguarde a atualização da sessão.");return;}
        editor=NativeLineup.open(frame,coach.fg(),match,(form,success)->command("/lineup",form,()->{success.run();if(testing && testNativeEditor)setReady(true);}),true,true,coach.fg(),true,this::registerLineupWindow);accept(latest);editor.setVisible(!testing);
        if(testing && testNativeEditor){
            a.bz original=(a.bz)((JScrollPane)editor.getContentPane().getComponent(0)).getViewport().getView();
            ((JComboBox<?>)Data.get(original,"Bg")).setSelectedIndex(1);
            ((JButton)Data.get(original,"Bn")).doClick();
        }
    }
    private void openMatchDetail(I match){
        al coach=visual.coach(coachId);if(coach==null || match==null)return;
        openMatchDetail(match,match.hc()==coach.fg()?match.hc():match.hd());
    }
    private void openMatchDetail(I match,ah club){
        if(latest==null || match==null || club==null || (editor!=null && editor.isDisplayable()))return;
        al coach=visual.coach(coachId);if(coach==null)return;
        String phase=String.valueOf(Data.map(latest.get("round")).get("phase"));boolean live=phase.equals("RUNNING") || phase.equals("HALF_TIME");boolean editable=live && club==coach.fg();
        editor=NativeLineup.open(frame,club,match,(form,success)->command("/live-lineup",form,()->{success.run();status.setText("Ajustes da escala\u00e7\u00e3o aplicados.");}),editable,false,coach.fg(),live,this::registerLineupWindow);editor.setVisible(!testing);
    }
    private void registerLineupWindow(JDialog window){
        editor=window;if(!lineupWindows.add(window))return;
        window.addWindowListener(new WindowAdapter(){@Override public void windowClosed(WindowEvent event){lineupWindows.remove(window);if(editor==window){editor=null;if(latest!=null)accept(latest);}}});
    }
    private boolean ownLineupWindowOpen(){
        al coach=visual==null?null:visual.coach(coachId);if(coach==null)return false;int ownClub=coach.fg().lk();
        for(JDialog window:new ArrayList<JDialog>(lineupWindows))if(window.isDisplayable() && ((Number)window.getRootPane().getClientProperty("clubId")).intValue()==ownClub)return true;
        return false;
    }
    private void finishTest(Map<String,Object> state)throws Exception{
        if(testComplete)return;testComplete=true;
        if(capture!=null){frame.validate();BufferedImage image=new BufferedImage(body.getWidth(),body.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D graphics=image.createGraphics();body.printAll(graphics);graphics.dispose();ImageIO.write(image,"png",capture.toFile());}
        if(nativePanel.GO!=null && nativePanel.GO.isRunning())throw new AssertionError("Local game timer started");
        Map<String,Object> row=Data.map(Data.list(Data.map(state.get("playback")).get("matches")).get(0));System.out.println("GUI_FINISHED "+row.get("homeGoals")+" "+row.get("awayGoals")+" sounds="+nativePanel.emittedSounds().size()+" timerStopped=true");if(!keepTestHost){close();System.exit(0);}
    }
    private void capture(Path path)throws IOException{
        frame.validate();BufferedImage image=new BufferedImage(body.getWidth(),body.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D graphics=image.createGraphics();body.printAll(graphics);graphics.dispose();ImageIO.write(image,"png",path.toFile());
    }
    private void close(){if(closed)return;closed=true;hideStartupProgress();connected=false;network.shutdownNow();if(nativePanel!=null)nativePanel.stopAudio();a.dV.qb();if(server!=null)server.destroy();frame.dispose();}
    public static void main(String[] args)throws Exception{
        Map<String,String> options=new HashMap<String,String>();for(int i=0;i<args.length;i++){if(i+1>=args.length)throw new IllegalArgumentException("Missing option value");options.put(args[i],args[++i]);}
        Path root=Paths.get(options.getOrDefault("--root",".")).toAbsolutePath().normalize();System.setProperty("user.dir",root.resolve("game/Brasfoot22-23").toString());
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        SwingUtilities.invokeLater(()->{NativeClient client=new NativeClient(root);client.testing=options.containsKey("--test-token") || options.containsKey("--test-create");client.keepTestHost=options.containsKey("--test-create");client.autoTest=options.containsKey("--test-auto");client.testNativeEditor=options.containsKey("--test-native-editor");client.testYouth=options.containsKey("--test-youth");client.testYouthCapture=options.containsKey("--test-youth-capture")?Paths.get(options.get("--test-youth-capture")):null;client.testYouthObserve=options.containsKey("--test-youth-observe")?Integer.valueOf(options.get("--test-youth-observe")):null;client.testYouthObserveClub=options.containsKey("--test-youth-observe-club")?Integer.valueOf(options.get("--test-youth-observe-club")):null;client.capture=options.containsKey("--capture")?Paths.get(options.get("--capture")):null;client.frame.setVisible(!client.testing||client.testYouth);if(client.testYouth){client.frame.setAlwaysOnTop(true);client.frame.toFront();}client.frame.addNotify();client.frame.validate();
            Runtime.getRuntime().addShutdownHook(new Thread(()->{client.network.shutdownNow();if(client.server!=null)client.server.destroy();}));
            if(options.containsKey("--port"))client.port.setText(options.get("--port"));
            client.testNativeOptions=options.containsKey("--test-native-options");
            if(options.containsKey("--capture-preparation"))client.preparationCapture=Paths.get(options.get("--capture-preparation"));
            if(options.containsKey("--address"))client.address.setText(options.get("--address"));
            if(options.containsKey("--test-create"))SwingUtilities.invokeLater(()->client.create.doClick());
            else if(client.testing)client.work(()->client.connect(options.get("--address"),options.get("--test-token")),null);
            else if("create".equals(options.get("--mode")))SwingUtilities.invokeLater(()->client.create.doClick());
        });
    }
}
