package brasfoot;
import java.io.File;
/** Read only inspection; does not invoke the original UI save loader. */
public final class Inspect {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected local .s22 path");
        System.out.println(Json.write(GameAdapter.readLocalSave(new File(args[0])).snapshot()));
    }
}
