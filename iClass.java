public abstract class iClass extends iObject {
    public abstract String getName();

    public abstract iObject cast(iObject obj);

    public abstract boolean isAssignableFrom(iClass cls);

    public abstract iClass getComponentType();

    public abstract iField getField(String name) throws Throwable;

    public abstract iMethod getMethod(String name, iClass... parameterTypes) throws Throwable;

    public abstract iMethod[] getMethods();

    public abstract iConstructor getConstructor(iClass... parameterTypes) throws Throwable;

    public abstract iConstructor[] getConstructors();

    @Override
    public iClass getClazz() {
        return this;
    }
}
