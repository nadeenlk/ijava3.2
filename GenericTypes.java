import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.stream.Stream;

public class GenericTypes {

    public static iClass[] getParameterTypes2(Scope scope, Executable x) {
        return Stream.of(x.getGenericParameterTypes()).map(t -> resolveType(scope, t)).toArray(iClass[]::new);
    }

    public static iClass getReturnType2(Scope scope, Method x) {
        return resolveType(scope, x.getGenericReturnType());
    }

    public static iClassWrapped resolveType2(Scope scope, Type x) {
        if (x instanceof Class) {
            return new iClassWrapped(scope, (Class<?>) x);
        } else if (x instanceof ParameterizedType) {
            ParameterizedType xx = (ParameterizedType) x;
            /*System.out.println(xx);
            System.out.println(Arrays.toString(xx.getActualTypeArguments()));
            System.out.println(xx.getRawType());
            System.out.println(xx.getOwnerType());
            throw new UnsupportedOperationException();*/
            return new iClassWrappedGeneric(scope, resolveType(scope, xx.getRawType()), x);
        } else if (x instanceof TypeVariable) {
            TypeVariable<?> xx = (TypeVariable<?>) x;
            Type[] ts = xx.getBounds();
            if (ts.length != 1)
                throw new UnsupportedOperationException();
            iClassWrapped clz = resolveType(scope, ts[0]);
            return new iClassWrappedGeneric(scope, clz, x);
        } else if (x instanceof GenericArrayType) {
            GenericArrayType xx = (GenericArrayType) x;
            return new iClassArrayWrapped(scope, resolveType(scope, xx.getGenericComponentType()));
        }
        throw new UnsupportedOperationException(x.getClass().toString());
    }

    public static iClass[] getParameterTypes(Scope scope, Executable x) {
        scope.log("[GenericTypes] getParameterTypes %s", x);
        iClass[] v = getParameterTypes2(scope, x);
        scope.log("[GenericTypes] getParameterTypes2 %s=%s", x, Arrays.toString(v));
        return v;
    }

    public static iClass getReturnType(Scope scope, Method x) {
        scope.log("[GenericTypes] getReturnType %s", x);
        iClass v = resolveType(scope, x.getGenericReturnType());
        scope.log("[GenericTypes2] getReturnType2 %s=%s", x, v);
        return v;
    }

    public static iClassWrapped resolveType(Scope scope, Type x) {
        scope.log("[GenericTypes] resolveType %s", x);
        iClassWrapped v = resolveType2(scope, x);
        scope.log("[GenericTypes] resolveType2 %s=%s", x, v);
        return v;
    }
}
