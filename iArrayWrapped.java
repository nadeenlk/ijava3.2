import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class iArrayWrapped extends iObjectWrapped {
    public iArrayWrapped(Scope s, Object x) {
        super(s, x);
    }

    @Override
    public iClass asClass() throws Throwable {
        return new iClassArrayWrapped(getScope(), new iClassWrapped(getScope(), x.getClass()));
    }

    @Override
    public String toString() {
        if (x == null)
            return "iArrayWrapped(null)";
        String[] s = new String[Array.getLength(x)];
        for (int i = 0; i < s.length; i++) {
            s[i] = Objects.toString(Array.get(x, i));
        }
        return String.format("iArrayWrapped<%s>(%s)", x.getClass().getSimpleName(), Arrays.toString(s));
    }
}
