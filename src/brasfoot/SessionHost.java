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
    private final MatchPlayback playback;
    private final ScheduledExecutorService clock=Executors.newSingleThreadScheduledExecutor();
    private final long tickMillis;
    private boolean ticking;
    private final Map<String, Integer> tokens = new HashMap<String, Integer>();
    private final Set<Integer> claimed = new HashSet<Integer>();
    private final Path directory;
    private final String sessionId = UUID.randomUUID().toString();
    private final String hostToken;
    private final boolean demo;
    private long checkpoint;
    private boolean storageFailed;

    public SessionHost(GameAdapter game, Path root, int hostCoach, boolean demo, long tickMillis) throws IOException {
        if (!game.coachIds().contains(hostCoach)) throw new IllegalArgumentException("Host coach must be a human in this career");
        if(tickMillis<5 || tickMillis>10000)throw new IllegalArgumentException("tick-ms must be between 5 and 10000");
        this.game = game; this.round = new RoundCoordinator(game.coachIds(), game); this.demo = demo;
        this.tickMillis=tickMillis;this.playback=new MatchPlayback(game.currentMatches());
        if(playback.snapshot().get("status").equals("FINISHED"))round.restoreFinished();
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
        long next = checkpoint + 1;
        String saveName = String.format("career-%06d.s22", next);
        Path temporary = directory.resolve(saveName + ".tmp");
        try {
            game.saveLocal(temporary.toFile());
            Files.move(temporary, directory.resolve(saveName), StandardCopyOption.ATOMIC_MOVE);
            Map<String, Object> meta = new LinkedHashMap<String, Object>();
            meta.put("sessionId", sessionId); meta.put("demo", demo); meta.put("save", saveName);
            meta.put("round", round.snapshot()); meta.put("playback", playback.snapshot());
            Files.write(directory.resolve("session.json.tmp"), Json.write(meta).getBytes(StandardCharsets.UTF_8));
            Files.move(directory.resolve("session.json.tmp"), directory.resolve("session.json"), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            checkpoint = next;
        } catch (IOException | RuntimeException error) {
            storageFailed = true;
            throw new IOException("Host checkpoint failed; commands paused", error);
        }
    }
    private Map<String, Object> state(int viewerCoach) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("viewerCoachId", viewerCoach); result.put("demo", demo); result.put("simulationImplemented", true);
        result.put("storageFailed", storageFailed); result.put("checkpoint", checkpoint);
        result.put("round", round.snapshot()); result.put("game", game.snapshot()); result.put("playback",playback.snapshot());
        return result;
    }
    private void kickoffIfReady() throws IOException {
        if(!round.snapshot().get("phase").equals("WAITING_FOR_ENGINE"))return;
        try {
            playback.begin();round.engineStarted();persist();ticking=true;
            clock.scheduleAtFixedRate(this::tick,tickMillis,tickMillis,TimeUnit.MILLISECONDS);
        } catch(Exception error) {
            playback.fail(error);round.engineFailed();persist();
            throw new IllegalStateException("Could not start match: "+error.getMessage());
        }
    }
    private synchronized void tick() {
        if(!ticking || storageFailed)return;
        try {
            boolean finished=playback.tick();
            if(finished) { ticking=false;round.engineFinished();persist();clock.shutdown(); }
            else if(playback.snapshot().get("status").equals("HALF_TIME"))persist();
        } catch(Throwable error) {
            ticking=false;playback.fail(error);round.engineFailed();
            try {persist();} catch(IOException saveError) {saveError.printStackTrace();}
            error.printStackTrace();clock.shutdown();
        }
    }
    public void close() {clock.shutdownNow();}
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
    private synchronized void handle(HttpExchange exchange) throws IOException {
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
                if (!round.snapshot().get("phase").equals("PREPARING")) throw new IllegalStateException("Round already locked");
                int coach = Integer.parseInt(required(form(exchange), "coachId"));
                String token = claim(coach);
                Map<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("token", token); result.put("coachId", coach); reply(exchange, 201, result); return;
            }
            int coach = authenticate(exchange);
            if (path.equals("/state") && method.equals("GET")) { reply(exchange, 200, state(coach)); return; }
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
        } catch (SecurityException e) { reply(exchange, 401, error(e.getMessage())); }
        catch (IllegalArgumentException e) { reply(exchange, 400, error(e.getMessage())); }
        catch (IllegalStateException e) { reply(exchange, 409, error(e.getMessage())); }
        catch (Exception e) { e.printStackTrace(); reply(exchange, 503, error("Host could not complete request")); }
        finally { exchange.close(); }
    }
    public static void main(String[] args) throws Exception {
        Map<String, String> options = new HashMap<String, String>(); boolean demo = false;
        for (int i=0;i<args.length;i++) {
            if (args[i].equals("--demo")) demo = true;
            else { if (i+1 == args.length) throw new IllegalArgumentException("Missing option value"); options.put(args[i],args[++i]); }
        }
        Path root = Paths.get(options.getOrDefault("--root", ".")).toAbsolutePath().normalize();
        if (demo == options.containsKey("--save")) throw new IllegalArgumentException("Choose --demo or --save <local .s22>");
        GameAdapter game = demo ? Fixture.create(root.resolve("game/Brasfoot22-23/teams/abcrn_bra.ban").toFile(), root.resolve("game/Brasfoot22-23/teams/corinthians_bra.ban").toFile()) : GameAdapter.readLocalSave(new File(options.get("--save")));
        int hostCoach = Integer.parseInt(options.getOrDefault("--host-coach", game.coachIds().iterator().next().toString()));
        SessionHost host = new SessionHost(game, root.resolve("sessions"), hostCoach, demo, Long.parseLong(options.getOrDefault("--tick-ms","500")));
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

