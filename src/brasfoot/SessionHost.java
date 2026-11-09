package brasfoot;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

/** LAN prototype. Host exclusively owns the game objects and saves. */
public final class SessionHost {
    private final GameAdapter game;
    private final RoundCoordinator round;
    private final CareerManagement management;
    private MatchPlayback playback;
    private final ScheduledExecutorService clock=Executors.newSingleThreadScheduledExecutor();
    private final Long tickOverrideMillis;
    private final PlaybackClock playbackClock=new PlaybackClock(this,(task,delay)->clock.schedule(task,delay,TimeUnit.MILLISECONDS),this::tick);
    private SessionOptions sessionOptions;
    private Map<String,Object> presentationStart;
    private Map<Integer,Object> frozenClubs=Collections.emptyMap();
    private long cachedSequence=-1,cachedCheckpoint=-1;
    private Map<String,Object> cachedGame,cachedPlayback;
    private boolean ticking;
    private final Map<String, Integer> tokens = new HashMap<String, Integer>();
    private final Set<Integer> claimed = new HashSet<Integer>();
    private final Path directory;
    private final String sessionId = UUID.randomUUID().toString();
    private final String hostToken;
    private final boolean demo;
    private long checkpoint;
    private boolean storageFailed;
    private String roundStartSave;
    private Map<String,Object> roundStartManagement;

    public SessionHost(GameAdapter game, Path root, int hostCoach, boolean demo, long tickMillis) throws IOException {
        this(game,root,hostCoach,demo,tickMillis,null);
    }
    public SessionHost(GameAdapter game, Path root, int hostCoach, boolean demo, long tickMillis,Map<String,Object> recoveredRound) throws IOException {
        this(game,root,hostCoach,demo,tickMillis,recoveredRound,null);
    }
    public SessionHost(GameAdapter game, Path root, int hostCoach, boolean demo, long tickMillis,Map<String,Object> recoveredRound,Map<String,Object> recoveredManagement) throws IOException {
        this(game,root,hostCoach,demo,tickMillis,recoveredRound,recoveredManagement,null);
    }
    public SessionHost(GameAdapter game, Path root, int hostCoach, boolean demo, long tickMillis,Map<String,Object> recoveredRound,Map<String,Object> recoveredManagement,Map<String,Object> recoveredOptions) throws IOException {
        if (!game.coachIds().contains(hostCoach)) throw new IllegalArgumentException("Host coach must be a human in this career");
        if(tickMillis!=0 && (tickMillis<5 || tickMillis>10000))throw new IllegalArgumentException("tick-ms must be between 5 and 10000");
        this.game = game; this.round = new RoundCoordinator(game.coachIds(), game); this.demo = demo;
        this.management=new CareerManagement(game);
        if(recoveredManagement!=null)management.restoreState(recoveredManagement);
        this.tickOverrideMillis=tickMillis==0?null:tickMillis;this.playback=new MatchPlayback(game.currentMatches(),game.isCareer());
        sessionOptions=SessionOptions.load(recoveredOptions);sessionOptions.applyNative();
        if(playback.snapshot().get("status").equals("FINISHED"))round.restoreFinished();
        else for(best.I fixture:game.currentMatches())if(fixture.e() || fixture.hu()!=0 || fixture.hw()!=0 || !fixture.hE().isEmpty())throw new IllegalArgumentException("Checkpoint contains an interrupted date; use --resume with the session directory");
        if(recoveredRound!=null)round.restoreMetadata(recoveredRound);
        directory = root.resolve(sessionId); Files.createDirectories(directory);
        hostToken = claim(hostCoach);
        persist();
    }
    private String claim(int coach) {
        if (!game.coachIds().contains(coach)) throw new IllegalArgumentException("Unknown coach");
        if (claimed.contains(coach)) throw new IllegalStateException("Coach already connected");
        byte[] random = new byte[32]; new SecureRandom().nextBytes(random);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        claimed.add(coach); tokens.put(token, coach); return token;
    }
    private void persist() throws IOException {
        persist(sessionOptions);
    }
    private void persist(SessionOptions candidate) throws IOException {
        long next = checkpoint + 1;
        String saveName = String.format("career-%06d.s22", next);
        Path temporary = directory.resolve(saveName + ".tmp");
        try {
            game.saveLocal(temporary.toFile());
            try(java.nio.channels.FileChannel saved=java.nio.channels.FileChannel.open(temporary,StandardOpenOption.WRITE)){saved.force(true);}
            Files.move(temporary, directory.resolve(saveName), StandardCopyOption.ATOMIC_MOVE);
            Map<String, Object> meta = new LinkedHashMap<String, Object>();
            meta.put("sessionId", sessionId); meta.put("demo", demo); meta.put("save", saveName);
            meta.put("round", round.snapshot()); meta.put("playback", playback.snapshot());
            meta.put("sessionOptions",candidate.persisted());
            Map<String,Object> managementState=management.exportState();
            if(round.snapshot().get("phase").equals("PREPARING")){
                roundStartSave=saveName;
                try{roundStartManagement=Data.parse(Json.write(managementState));}catch(javax.script.ScriptException error){throw new IOException("Could not snapshot management state",error);}
            }
            meta.put("management",managementState);meta.put("roundStartManagement",roundStartManagement);
            meta.put("roundStartSave",roundStartSave);
            Files.write(directory.resolve("session.json.tmp"), Json.write(meta).getBytes(StandardCharsets.UTF_8));
            try(java.nio.channels.FileChannel saved=java.nio.channels.FileChannel.open(directory.resolve("session.json.tmp"),StandardOpenOption.WRITE)){saved.force(true);}
            Files.move(directory.resolve("session.json.tmp"), directory.resolve("session.json"), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            checkpoint = next;
        } catch (IOException | RuntimeException error) {
            storageFailed = true;
            playbackClock.cancel();
            throw new IOException("Host checkpoint failed; commands paused", error);
        }
    }
    private Map<String, Object> state(int viewerCoach) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("viewerCoachId", viewerCoach); result.put("demo", demo); result.put("simulationImplemented", true);
        result.put("host",tokens.get(hostToken)==viewerCoach);
        result.put("storageFailed", storageFailed); result.put("checkpoint", checkpoint);
        if(game.isCareer())management.ensureIds();
        if(cachedGame==null || cachedSequence!=playback.sequence() || cachedCheckpoint!=checkpoint){
            cachedPlayback=playback.snapshot();cachedGame=presentedGame(cachedPlayback);
            cachedSequence=playback.sequence();cachedCheckpoint=checkpoint;
        }
        result.put("round", round.snapshot()); result.put("game", cachedGame); result.put("playback",cachedPlayback);
        result.put("sessionOptions",sessionOptions.snapshot(tokens.get(hostToken)==viewerCoach,game.hasHumanMatch(),tickOverrideMillis));
        return result;
    }
    private Map<String,Object> presentedGame(Map<String,Object> shown){
        if(presentationStart==null || playback.status().equals("FINISHED"))return game.snapshot();
        Map<String,Object> current=game.snapshot(frozenClubs,Data.list(presentationStart.get("matches")));
        List<Object> rows=Data.list(current.get("matches")),liveRows=Data.list(shown.get("matches"));
        for(int i=0;i<rows.size();i++){Map<String,Object> row=Data.map(rows.get(i)),live=Data.map(liveRows.get(i));row.put("homeGoals",live.get("homeGoals"));row.put("awayGoals",live.get("awayGoals"));row.put("played",live.get("played"));}
        return current;
    }
    private void reconfigureClock(){playbackClock.cancel();if(ticking && !storageFailed && playback.isLive())playbackClock.schedule(sessionOptions.interval(game.hasHumanMatch(),tickOverrideMillis));}
    private void activateOptions(String boundary)throws IOException{SessionOptions candidate=sessionOptions.activate(boundary);if(candidate!=sessionOptions){persist(candidate);sessionOptions=candidate;sessionOptions.applyNative();}}
    private void kickoffIfReady() throws IOException {
        if(!round.snapshot().get("phase").equals("WAITING_FOR_ENGINE"))return;
        try {
            presentationStart=game.snapshot();
            Set<Integer> aiClubs=new HashSet<Integer>();for(best.I match:game.currentMatches())if(!match.hc().jZ() && !match.hd().jZ()){aiClubs.add(match.hc().lk());aiClubs.add(match.hd().lk());}
            frozenClubs=new HashMap<Integer,Object>();for(Object raw:Data.list(presentationStart.get("visualClubs"))){int id=Data.number(Data.map(raw).get("clubId"));if(aiClubs.contains(id))frozenClubs.put(id,raw);}
            playback.begin();round.engineStarted();
            persist();ticking=true;
            reconfigureClock();
        } catch(Exception error) {
            playback.fail(error);round.engineFailed();persist();
            throw new IllegalStateException("Could not start match: "+error.getMessage());
        }
    }
    private synchronized void tick() {
        if(!ticking || storageFailed)return;
        try {
            boolean finished=playback.tick();
            if(finished) { ticking=false;round.engineFinished();persist(); }
            else if(playback.status().equals("HALF_TIME")){activateOptions("NEXT_HALF_TIME");if(!game.hasHumanMatch()){playback.resumeSecondHalf();persist();}else{ticking=false;round.engineHalfTime();persist();}}
            reconfigureClock();
        } catch(Throwable error) {
            ticking=false;playback.fail(error);round.engineFailed();
            if(!storageFailed)try {persist();} catch(IOException saveError) {saveError.printStackTrace();}
            error.printStackTrace();clock.shutdown();
        }
    }
    public synchronized void close() {ticking=false;playbackClock.close();clock.shutdownNow();}
    private int authenticate(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        Integer coach = auth != null && auth.startsWith("Bearer ") ? tokens.get(auth.substring(7)) : null;
        if (coach == null) throw new SecurityException("Invalid session token");
        return coach;
    }
    private static Map<String, String> form(HttpExchange exchange) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; int count;
        while ((count = exchange.getRequestBody().read(buffer)) != -1) {
            if (data.size() + count > 8192) throw new IllegalArgumentException("Request too large");
            data.write(buffer, 0, count);
        }
        Map<String, String> result = new HashMap<String, String>();
        String body = new String(data.toByteArray(), StandardCharsets.UTF_8);
        if (!body.isEmpty()) for (String field : body.split("&")) {
            String[] parts = field.split("=", 2);
            String key = URLDecoder.decode(parts[0], "UTF-8");
            String value = parts.length == 2 ? URLDecoder.decode(parts[1], "UTF-8") : "";
            if (result.put(key, value) != null) throw new IllegalArgumentException("Duplicate field");
        }
        return result;
    }
    private static String required(Map<String, String> data, String key) {
        String value = data.get(key);
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("Missing " + key);
        return value;
    }
    private static int[] indices(String value) {
        if (value == null || value.isEmpty()) return new int[0];
        String[] split = value.split(",", -1); int[] result = new int[split.length];
        for (int i = 0; i < result.length; i++) result[i] = Integer.parseInt(split[i]);
        return result;
    }
    private static void reply(HttpExchange exchange, int code, Object body) throws IOException {
        byte[] bytes = Json.write(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) { output.write(bytes); }
    }
    private static Map<String, Object> error(String message) {
        Map<String, Object> result = new LinkedHashMap<String, Object>(); result.put("error", message); return result;
    }
    private Map<Integer,List<best.F>> rosterIdentities(){
        Map<Integer,List<best.F>> result=new LinkedHashMap<Integer,List<best.F>>();
        for(Object raw:c.a.SR.M()){
            best.al coach=(best.al)raw;List<best.F> players=new ArrayList<best.F>();
            if(coach.fg()!=null)for(Object value:coach.fg().kc())players.add((best.F)value);
            result.put(coach.lT(),players);
        }
        return result;
    }
    private void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/state")){
            // Snapshot maps contain copied scalar data. Writing a large response or
            // waiting for a slow reader must not hold the authoritative game lock.
            try{Map<String,Object> snapshot;synchronized(this){snapshot=state(authenticate(exchange));}reply(exchange,200,snapshot);}
            catch(SecurityException error){Map<String,Object> body=error(error.getMessage());body.put("code","INVALID_SESSION_TOKEN");reply(exchange,401,body);}
            catch(Exception error){error.printStackTrace();reply(exchange,503,error("Host could not complete request"));}
            finally{exchange.close();}return;
        }
        handleCommand(exchange);
    }
    private synchronized void handleCommand(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if (path.equals("/session") && method.equals("GET")) {
                List<Object> coaches = new ArrayList<Object>();
                for (Object raw : (List<?>)game.snapshot().get("coaches")) {
                    Map<?, ?> row = (Map<?, ?>)raw;
                    Map<String, Object> coach = new LinkedHashMap<String, Object>();
                    coach.put("coachId", row.get("coachId")); coach.put("name", row.get("name"));
                    coach.put("club", row.get("club")); coach.put("claimed", claimed.contains(row.get("coachId"))); coaches.add(coach);
                }
                Map<String, Object> info = new LinkedHashMap<String, Object>();
                info.put("demo", demo); info.put("coaches", coaches); info.put("phase", round.snapshot().get("phase"));
                reply(exchange, 200, info); return;
            }
            if (path.equals("/join") && method.equals("POST")) {
                if (storageFailed) throw new IllegalStateException("Host storage unavailable");
                String phase=(String)round.snapshot().get("phase");
                if (!phase.equals("PREPARING") && !phase.equals("FINISHED")) throw new IllegalStateException("Round already locked");
                int coach = Integer.parseInt(required(form(exchange), "coachId"));
                String token = claim(coach);
                Map<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("token", token); result.put("coachId", coach); reply(exchange, 201, result); return;
            }
            int coach = authenticate(exchange);
            if (path.equals("/state") && method.equals("GET")) { reply(exchange, 200, state(coach)); return; }
            if(path.equals("/session-options") && method.equals("POST")){
                if(coach!=tokens.get(hostToken)){Map<String,Object> body=error("Configurações controladas pelo host");body.put("code","HOST_REQUIRED");reply(exchange,403,body);return;}
                if(storageFailed){Map<String,Object> body=error("Host storage unavailable");body.put("code","STORAGE_FAILED");reply(exchange,503,body);return;}
                try{
                    Map<String,String> data=form(exchange);if(!data.keySet().equals(new HashSet<String>(Arrays.asList("optionsRevision","changes"))))throw new IllegalArgumentException("Expected optionsRevision and changes only");
                    String revision=required(data,"optionsRevision");if(!revision.matches("0|[1-9][0-9]*"))throw new IllegalArgumentException("Invalid options revision");
                    Map<String,Object> changes=OptionsJson.parse(required(data,"changes"));
                    SessionOptions candidate=sessionOptions.change(changes,tickOverrideMillis!=null);
                    if(Long.parseLong(revision)!=sessionOptions.revision())throw new SessionOptions.Conflict("OPTIONS_REVISION_CONFLICT","As configurações foram atualizadas");
                    if(candidate!=sessionOptions){playbackClock.cancel();persist(candidate);sessionOptions=candidate;try{sessionOptions.applyNative();}catch(RuntimeException failure){storageFailed=true;throw failure;}reconfigureClock();}
                    reply(exchange,200,state(coach));
                }catch(SessionOptions.Conflict conflict){Map<String,Object> body=error(conflict.getMessage());body.put("code",conflict.code);body.put("sessionOptions",sessionOptions.snapshot(true,game.hasHumanMatch(),tickOverrideMillis));reply(exchange,409,body);}
                catch(IllegalArgumentException invalid){Map<String,Object> body=error(invalid.getMessage());body.put("code","INVALID_OPTIONS");reply(exchange,400,body);}
                catch(Exception failure){storageFailed=true;playbackClock.cancel();Map<String,Object> body=error(failure.getMessage());body.put("code","STORAGE_FAILED");reply(exchange,503,body);}return;
            }
            // Precomputed AI results must not leak through standings or player statistics.
            if((path.equals("/overview") || path.equals("/management")) && method.equals("GET") && presentationStart!=null && !playback.status().equals("FINISHED"))throw new IllegalStateException("Consultas de gestão disponíveis após a apresentação da data");
            if(path.equals("/management") && method.equals("GET")){reply(exchange,200,management.snapshot(coach));return;}
            if(path.equals("/overview") && method.equals("GET")){reply(exchange,200,CareerViews.snapshot(coach));return;}
            if(path.equals("/management") && method.equals("POST")){
                if(storageFailed)throw new IllegalStateException("Host storage unavailable");
                Map<String,String> data=form(exchange);required(data,"action");
                long expectedRound=Long.parseLong(required(data,"round")),revision=Long.parseLong(required(data,"revision"));
                Set<Integer> affected=management.affectedCoaches(coach,data);
                Runnable rollback=management.rollback(coach,data);
                Map<Integer,List<best.F>> before=rosterIdentities();
                final Map<String,Object>[] result=new Map[1];
                round.management(coach,expectedRound,revision,affected,()->{
                    result[0]=management.execute(coach,data);
                    Map<Integer,List<best.F>> after=rosterIdentities();Set<Integer> changed=new LinkedHashSet<Integer>();
                    for(Integer id:before.keySet())if(!before.get(id).equals(after.get(id)))changed.add(id);
                    if(!changed.isEmpty()){game.invalidateLineups(changed);round.invalidatePreparation(changed,coach);}
                });
                try{persist();}catch(Exception failure){rollback.run();cachedGame=null;throw failure;}
                Map<String,Object> response=state(coach);response.put("command",result[0]);reply(exchange,200,response);return;
            }
            if(path.equals("/halftime-ready") && method.equals("POST")){
                if(storageFailed)throw new IllegalStateException("Host storage unavailable");
                Map<String,String> data=form(exchange);String value=required(data,"ready");
                if(!value.equals("true") && !value.equals("false"))throw new IllegalArgumentException("ready must be true or false");
                round.halftimeReady(coach,Long.parseLong(required(data,"round")),Long.parseLong(required(data,"revision")),Boolean.parseBoolean(value),()->{playback.resumeSecondHalf();ticking=true;});
                persist();reconfigureClock();reply(exchange,200,state(coach));return;
            }
            if(path.equals("/substitute") && method.equals("POST")){
                if(storageFailed)throw new IllegalStateException("Host storage unavailable");
                Map<String,String> data=form(exchange);long expectedRound=Long.parseLong(required(data,"round")),revision=Long.parseLong(required(data,"revision"));
                final Map<String,Object>[] result=new Map[1];
                round.liveCommand(coach,expectedRound,revision,()->result[0]=game.substitute(coach,Integer.parseInt(required(data,"outgoingPlayerId")),Integer.parseInt(required(data,"incomingPlayerId")),playback));
                persist();Map<String,Object> response=state(coach);response.put("command",result[0]);reply(exchange,200,response);return;
            }
            if(path.equals("/live-lineup") && method.equals("POST")){
                if(storageFailed)throw new IllegalStateException("Host storage unavailable");
                Map<String,String> data=form(exchange);long expectedRound=Long.parseLong(required(data,"round")),revision=Long.parseLong(required(data,"revision"));
                final Map<String,Object>[] result=new Map[1];
                round.liveCommand(coach,expectedRound,revision,()->result[0]=game.liveLineup(coach,indices(required(data,"starters")),indices(data.get("bench")),indices(required(data,"slots")),indices(data.get("tactics")),playback));
                persist();Map<String,Object> response=state(coach);response.put("command",result[0]);reply(exchange,200,response);return;
            }
            if(path.equals("/advance") && method.equals("POST")) {
                if(storageFailed)throw new IllegalStateException("Host storage unavailable");
                if(!game.isCareer())throw new IllegalStateException("Friendly session has no career calendar");
                if(!hostToken.equals(exchange.getRequestHeaders().getFirst("Authorization").substring(7)))throw new SecurityException("Only the host can advance the calendar");
                Map<String,String> data=form(exchange);
                round.nextRound(coach,Long.parseLong(required(data,"round")),Long.parseLong(required(data,"revision")),()->{
                    try{CareerEngine.nextDate(game);}catch(RuntimeException error){round.engineFailed();playback.fail(error);throw error;}
                });
                playbackClock.cancel();presentationStart=null;frozenClubs=Collections.emptyMap();playback=new MatchPlayback(game.currentMatches(),true);persist();
                reply(exchange,200,state(coach));return;
            }
            if (method.equals("POST") && (path.equals("/lineup") || path.equals("/ready"))) {
                if (storageFailed) throw new IllegalStateException("Host storage unavailable");
                Map<String, String> data = form(exchange);
                long expectedRound = Long.parseLong(required(data, "round"));
                long revision = Long.parseLong(required(data, "revision"));
                if(path.equals("/lineup")){
                    int[] starters=indices(required(data,"starters")),bench=indices(data.get("bench"));
                    if(data.containsKey("slots") || data.containsKey("tactics")) round.lineupWithOptions(coach,expectedRound,revision,starters,bench,data.containsKey("slots")?indices(data.get("slots")):new int[]{1,2,9,4,7,11,13,14,16,20,23},data.containsKey("tactics")?indices(data.get("tactics")):null);
                    else round.lineup(coach,expectedRound,revision,starters,bench);
                }
                else {
                    String value = required(data,"ready");
                    if (!value.equals("true") && !value.equals("false")) throw new IllegalArgumentException("ready must be true or false");
                    round.ready(coach, expectedRound, revision, Boolean.parseBoolean(value));
                }
                persist(); kickoffIfReady(); reply(exchange, 200, state(coach)); return;
            }
            reply(exchange, 404, error("Unknown endpoint or method"));
        } catch (SecurityException e) { Map<String,Object> body=error(e.getMessage());body.put("code","INVALID_SESSION_TOKEN");reply(exchange, 401, body); }
        catch (IllegalArgumentException e) { reply(exchange, 400, error(e.getMessage())); }
        catch (IllegalStateException e) { reply(exchange, 409, error(e.getMessage())); }
        catch (Exception e) { e.printStackTrace(); reply(exchange, 503, error("Host could not complete request")); }
        finally { exchange.close(); }
    }
    public static void main(String[] args) throws Exception {
        Map<String, String> options = new HashMap<String, String>(); boolean demo = false,career=false;
        for (int i=0;i<args.length;i++) {
            if (args[i].equals("--demo")) demo = true;
            else if(args[i].equals("--career"))career=true;
            else { if (i+1 == args.length) throw new IllegalArgumentException("Missing option value"); options.put(args[i],args[++i]); }
        }
        Path root = Paths.get(options.getOrDefault("--root", ".")).toAbsolutePath().normalize();
        Map<String,Object> recoveredRound=null;
        Map<String,Object> recoveredManagement=null;
        Map<String,Object> recoveredOptions=null;
        boolean resumedDemo=false;
        if ((demo?1:0)+(career?1:0)+(options.containsKey("--save")?1:0)+(options.containsKey("--resume")?1:0)!=1) throw new IllegalArgumentException("Choose --demo, --career, --resume <session directory> or --save <local .s22>");
        if(options.containsKey("--resume")){
            Path previous=Paths.get(options.get("--resume")).toAbsolutePath().normalize();
            Map<String,Object> manifest=Data.parse(new String(Files.readAllBytes(previous.resolve("session.json")),StandardCharsets.UTF_8));
            String phase=(String)Data.map(manifest.get("round")).get("phase");
            String save=(String)manifest.get((phase.equals("RUNNING") || phase.equals("HALF_TIME") || phase.equals("FAILED") || phase.equals("WAITING_FOR_ENGINE"))?"roundStartSave":"save");
            if(save==null || !save.matches("career-[0-9]+\\.s22"))throw new IllegalArgumentException("Session has no valid recovery checkpoint");
            options.put("--save",previous.resolve(save).toString());
            recoveredRound=Data.map(manifest.get("round"));
            if(manifest.containsKey("sessionOptions"))recoveredOptions=Data.map(manifest.get("sessionOptions"));
            Object managementState=manifest.get((phase.equals("RUNNING") || phase.equals("HALF_TIME") || phase.equals("FAILED") || phase.equals("WAITING_FOR_ENGINE"))?"roundStartManagement":"management");
            if(managementState instanceof Map)recoveredManagement=Data.map(managementState);
            resumedDemo=Boolean.TRUE.equals(manifest.get("demo"));
            System.out.println("Resuming saved career; interrupted matches restart from preparation.");
        }
        GameAdapter game;
        if(career){
            String[] names=options.containsKey("--coach-names")?new String(Base64.getUrlDecoder().decode(options.get("--coach-names")),StandardCharsets.UTF_8).split("\n",-1):null;
            if(options.containsKey("--club-assets"))game=CareerEngine.createByAssets(root.toFile(),options.get("--club-assets").split(",",-1),names);
            else if(options.containsKey("--club-ids"))game=CareerEngine.create(root.toFile(),indices(options.get("--club-ids")),names);
            else game=CareerEngine.create(root.toFile(),options.getOrDefault("--clubs","Corinthians,Palmeiras,Santos,São Paulo").split(","),names);
        } else if(demo)game=Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(),root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile());
        else game=GameAdapter.readLocalSave(new File(options.get("--save")));
        if(game.isCareer()){
            if(!Boolean.getBoolean("brasfoot.host.ui.disabled"))throw new IllegalStateException("Career host requires -javaagent:host-ui-agent.jar");
            c.a.SR.bs=false;
        }
        int hostCoach = Integer.parseInt(options.getOrDefault("--host-coach", game.coachIds().iterator().next().toString()));
        if(options.containsKey("--tick-ms") && Long.parseLong(options.get("--tick-ms"))==0)throw new IllegalArgumentException("tick-ms must be between 5 and 10000");
        SessionHost host = new SessionHost(game, root.resolve("sessions"), hostCoach, demo||resumedDemo, Long.parseLong(options.getOrDefault("--tick-ms","0")),recoveredRound,recoveredManagement,recoveredOptions);
        String bind = options.getOrDefault("--bind", "0.0.0.0");
        int port = Integer.parseInt(options.getOrDefault("--port", "8212"));
        HttpServer server = HttpServer.create(new InetSocketAddress(bind, port), 16);
        server.createContext("/", host::handle); server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {server.stop(0);host.close();}));
        server.start();
        System.out.println("Session listening on " + bind + ":" + port);
        System.out.println("Session directory: " + host.directory);
        System.out.println("Host coach: " + hostCoach);
        System.out.println("Host token: " + host.hostToken);
        System.out.println("Demo=" + demo + "; live friendly match simulation enabled.");
    }
}

