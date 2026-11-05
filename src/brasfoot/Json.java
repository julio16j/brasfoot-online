package brasfoot;
import java.util.*;
/** Minimal output-only JSON writer. Network commands are not deserialized as Java objects. */
public final class Json {
    private Json() { }
    public static String write(Object value) {
        if (value == null) return "null";
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map) {
            List<String> parts = new ArrayList<String>();
            for (Object raw : ((Map<?, ?>) value).entrySet()) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) raw;
                parts.add(write(e.getKey().toString()) + ":" + write(e.getValue()));
            }
            return "{" + String.join(",", parts) + "}";
        }
        if (value instanceof Iterable) {
            List<String> parts = new ArrayList<String>();
            for (Object item : (Iterable<?>) value) parts.add(write(item));
            return "[" + String.join(",", parts) + "]";
        }
        StringBuilder out = new StringBuilder("\"");
        for (char c : value.toString().toCharArray()) {
            if (c == '"' || c == '\\') out.append('\\').append(c);
            else if (c < 32) out.append(String.format("\\u%04x", (int)c));
            else out.append(c);
        }
        return out.append('"').toString();
    }
}
