import java.util.stream.Stream;

public class iClassWrapped extends iClass {
    Class<?> x;

    public iClassWrapped(Class<?> x) {
        this.x = x;
    }

    public String getName() {
        return x.getName();
    }

    @Override
    public iObject cast(iObject obj) {
        return new iObjectWrapped(x.cast(obj.asWrapped().x));
    }

    public boolean isAssignableFrom(iClass cls) {
        if (cls instanceof iClassWrapped)
            return isAssignableFrom((iClassWrapped) cls);
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        if (obj instanceof iClassWrapped)
            return x.equals(((iClassWrapped) obj).x);
        if (obj instanceof Class<?>)
            return x.equals((Class<?>) obj);
        throw new UnsupportedOperationException();
    }

    public boolean isAssignableFrom(iClassWrapped cls) {
        return x.isAssignableFrom(cls.x);
    }

    public iClass getComponentType() {
        return new iClassWrapped(x.getComponentType());
    }

    public iField getField(String name) throws Throwable {
        return new iFieldWrapped(x.getField(name));
    }

    public iMethod getMethod(String name, iClass... parameterTypes) throws Throwable {
        return new iMethodWrapped(
                x.getMethod(name, Stream.of(parameterTypes).map(t -> ((iClassWrapped) t).x).toArray(Class[]::new)));
    }

    public iMethod[] getMethods() {
        return Stream.of(x.getMethods()).map(m -> new iMethodWrapped(m)).toArray(iMethod[]::new);
    }

    public iConstructor getConstructor(iClass... parameterTypes) throws Throwable {
        return new iConstructorWrapped(
                x.getConstructor(Stream.of(parameterTypes).map(t -> ((iClassWrapped) t).x).toArray(Class[]::new)));
    }

    public iConstructor[] getConstructors() {
        return Stream.of(x.getConstructors()).map(c -> new iConstructorWrapped(c)).toArray(iConstructor[]::new);
    }

    @Override
    public String toString() {
        return String.format("iClassWrapped(%s)", x.getName());
    }
}
