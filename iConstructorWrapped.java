import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public class iConstructorWrapped extends iConstructor {
    Constructor<?> x;

    public iConstructorWrapped(Constructor<?> x) {
        this.x = x;
    }

    public boolean isVarArgs() {
        return x.isVarArgs();
    }

    public int getParameterCount() {
        return x.getParameterCount();
    }

    public iClass[] getParameterTypes() {
        return Stream.of(x.getParameterTypes()).map(p -> new iClassWrapped(p)).toArray(iClass[]::new);
    }

    public iObject newInstance(iObject... args) throws Throwable {
        return new iObjectWrapped(
                x.newInstance(Stream.of(args).map(arg -> ((iObjectWrapped) arg).x).toArray(Object[]::new)));

    }

    public iClass getDeclaringClass() {
        return new iClassWrapped(x.getDeclaringClass());
    }
}