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
        //System.out.printf("invoke %s %s\n", obj, Arrays.toString(args));
        Object obj2 = obj.asWrapped().x;
        Object[] args2 = Stream.of(args).map(arg -> arg.asWrapped().x).toArray(Object[]::new);
        return new iObjectWrapped(x.invoke(obj2, args2));
    }

    public iClass getReturnType() {
        return new iClassWrapped(x.getReturnType());
    }

    @Override
    public String toString() {
        return String.format("iMethodWrapped(%s)", x);
    }
}