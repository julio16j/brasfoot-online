package brasfoot;
import best.ah;
import best.I;
import java.io.*;
/** Creates only an in-memory test round, not a full playable career. */
public final class Fixture {
    private Fixture() { }
    @SuppressWarnings("unchecked")
    public static GameAdapter create(File first, File second) throws Exception {
        new best.M(true); best.n.cU();
        c.a.SR = new best.f(); c.a.afQ = new best.ay();
        best.a day = new best.a(); day.a(2022, 0, 1); c.a.SR.R().add(day); c.a.SR.l(0);
        ah[] clubs = new ah[2];
        File[] files = {first, second};
        for (int i = 0; i < 2; i++) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(files[i]))) {
                clubs[i] = new ah((e.t) input.readObject());
            }
            clubs[i].bX(i); clubs[i].ka().n(clubs[i]); clubs[i].k(Boolean.TRUE);
            for (Object player : clubs[i].kc()) { ((best.F)player).n(clubs[i]); ((best.F)player).fI(); ((best.F)player).a(365L, true); }
            clubs[i].ka().k(Boolean.TRUE);
            clubs[i].ka().i("Tecnico " + (i + 1));
            c.a.SR.M().add(clubs[i].ka());
            c.a.SR.aN().add(clubs[i]);
        }
        new I(null, 0, clubs[0], clubs[1], 0, new f.a(), null);
        return new GameAdapter(c.a.SR);
    }
}



