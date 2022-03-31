import java.util.Arrays;

public class iExecutableMatcher {

    public static iExecutable getExecutableImpl(Scope scope, iClass c, String n, iClass[] cx) throws Throwable {
        boolean isMethod = n != null;
        try {
            if (isMethod)
                return c.getMethod(n, cx);
            else
                return c.getConstructor(cx);
        } catch (NoSuchMethodException e) {
            scope.log("[err] %s", e);
        }
        for (iExecutable e : (isMethod ? c.getMethods() : c.getConstructors())) {
            if (!isMethod || e.asMethod().getName().equals(n)) {
                if (getExecutableMatches(scope, e.isVarArgs(), e.getParameterTypes(), cx))
                    return e;
            }
        }
        throw new NoSuchMethodException(n);
    }

    public static boolean getExecutableMatchesImpl(boolean isVarArgs, iClass[] dst, iClass[] src) {
        if (isVarArgs) {
            // if (src.length < dst.length - 1)return false;
            int n = dst.length - 1;
            for (int i = 0; i < dst.length - 1; i++) {
                if (!isAssignableFrom(dst[i], src[i]))
                    return false;
            }
            iClass t = dst[dst.length - 1].getComponentType();
            for (int i = dst.length; i < src.length; i++) {
                if (!isAssignableFrom(t, src[i]))
                    return false;
            }
            return true;
        } else {
            if (src.length != dst.length)
                return false;
            for (int i = 0; i < src.length; i++)
                if (!isAssignableFrom(dst[i], src[i]))
                    return false;
            return true;
        }
    }

    public static boolean isAssignableFrom2(iClass dst, iClass src) {
        /*if (src instanceof iClassArrayWrapped && dst instanceof iClassArrayWrapped) {
            return isAssignableFrom(dst.getComponentType(), src.getComponentType());
        }*/
        return Primitives.isAssignable(dst, src) || dst.isAssignableFrom(src);
    }

    public static boolean isAssignableFrom(iClass dst, iClass src) {
        //System.out.printf("[isAssignableFrom] %s %s (%s)\n", dst, src);
        boolean v = isAssignableFrom2(dst, src);
        //System.out.printf("[isAssignableFrom] %s %s (%s)\n", dst, src, v);
        return v;
    }

    public static iExecutable getExecutable(Scope scope, iClass c, String n, iClass[] cx) {
        iExecutable x = null;
        NoSuchMethodException t = null;
        try {
            scope.log("[getExecutable] %s.%s(%s)", c, n, Arrays.toString(cx));
            x = getExecutableImpl(scope, c, n, cx);
        } catch (NoSuchMethodException tt) {
            t = tt;
        } catch (Throwable tt) {
            throw new RuntimeException(tt);
        }
        scope.log("[getExecutable2] %s.%s(%s)=%s", c, n, Arrays.toString(cx), t == null ? x : "throwed");
        if (t != null)
            throw new RuntimeException(t);
        return x;
    }

    public static boolean getExecutableMatches(Scope scope, boolean isVarArgs, iClass[] dst, iClass[] src) {
        scope.log("[getExecutableMatches] isVarArgs=%b (%s)<--(%s)", isVarArgs, Arrays.toString(dst),
                Arrays.toString(src));
        boolean b = getExecutableMatchesImpl(isVarArgs, dst, src);
        scope.log("[getExecutableMatches2] isVarArgs=%b (%s)<--(%s): %s", isVarArgs, Arrays.toString(dst),
                Arrays.toString(src), b);
        return b;
    }

}
