import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public class iConstructorWrapped extends iConstructor {
    Constructor<?> x;

    public iConstructorWrapped(Scope parent, Constructor<?> x) {
        super(parent, null);
        this.x = x;
    }

    public boolean isVarArgs() {
        return x.isVarArgs();
    }

    public int getParameterCount() {
        return x.getParameterCount();
    }

    public iClass[] getParameterTypes() {
        return Stream.of(x.getParameterTypes()).map(p -> new iClassWrapped(getScope(), p)).toArray(iClass[]::new);
    }

    public iObject newInstance(iObject... args) throws Throwable {
        return new iObjectWrapped(getScope(),
                x.newInstance(Stream.of(args).map(arg -> ((iObjectWrapped) arg).x).toArray(Object[]::new)));

    }

    public iClass getDeclaringClass() {
        return new iClassWrapped(getScope(), x.getDeclaringClass());
    }
}