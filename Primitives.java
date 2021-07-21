import java.util.Arrays;
import java.util.List;

public class Primitives {
    final static List<String> clzn = Arrays.asList("boolean,byte,char,float,int,long,short,double".split(","));
    final static List<Class<?>> clzp = Arrays.asList(boolean.class, byte.class, char.class, float.class, int.class,
            long.class, short.class, double.class);
    final static List<Class<?>> clzw = Arrays.asList(Boolean.class, Byte.class, Character.class, Float.class,
            Integer.class, Long.class, Short.class, Double.class);

    @SuppressWarnings("all")
    public static boolean isAssignable(iClass a, iClass b) {
        int i = clzp.indexOf(a);
        if (i == -1) {
            i = clzw.indexOf(a);
            if (i == -1)
                return false;
            else
                return b.equals(clzp.get(i));
        } else
            return b.equals(clzw.get(i));
    }

    public static Class<?> findClass(String name) {
        int i = clzn.indexOf(name);
        return i == -1 ? null : clzw.get(i);
    }
}
