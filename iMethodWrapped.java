import java.lang.reflect.Method;
import java.util.stream.Stream;

public class iMethodWrapped extends iMethod {
    Method x;

    public iMethodWrapped(Scope parent, Method x) {
        super(parent, null);
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
        return GenericTypes.getParameterTypes(getScope(), x);
    }

    public iObject invoke(iObject obj, iObject... args) throws Throwable {
        // System.out.printf("invoke %s %s\n", obj, Arrays.toString(args));
        Object obj2 = obj.asWrapped().x;
        Object[] args2 = Stream.of(args).map(arg -> arg.asWrapped().x).toArray(Object[]::new);
        return new iObjectWrapped(getScope(), x.invoke(obj2, args2));
    }

    public iClass getReturnType() {
        return GenericTypes.getReturnType(getScope(), x);
    }

    @Override
    public String toString() {
        return String.format("iMethodWrapped(%s)", x);
    }
}