package brasfoot;

import best.at;
import java.util.IdentityHashMap;
import java.util.Map;

/** Snapshot key that preserves object identity for competitions without a native ID. */
final class CompetitionIdentity {
    private static final Map<at,String> LEGACY=new IdentityHashMap<at,String>();
    private static long next=1;
    private CompetitionIdentity(){ }
    static synchronized String key(at competition){if(competition==null)return "none";if(competition.gD()>=0)return "id:"+competition.gD();String key=LEGACY.get(competition);if(key==null){key="instance:"+(next++);LEGACY.put(competition,key);}return key;}
}
