import java.lang.reflect.Method;
import java.util.stream.Stream;

public class iMethodWrapped extends iMethod {
    Method x;

    public iMethodWrapped(Method x) {
        this.x = x;
    }

    public String getName() {
        return x.getName();
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

    public iObject invoke(iObject obj, iObject... args) throws Throwable {
        return new iObjectWrapped(x.invoke(((iObjectWrapped) obj).x,
                Stream.of(args).map(arg -> ((iObjectWrapped) arg.asWrapped()).x).toArray(Object[]::new)));
    }

    public iClass getReturnType() {
        return new iClassWrapped(x.getReturnType());
    }

    @Override
    public String toString() {
        return String.format("iMethodWrapped(%s)", x);
    }
}