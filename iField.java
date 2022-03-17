public abstract class iField extends iObject {
    public abstract iClass getType();

    public abstract iObject get(iObject i) throws Throwable;

    public abstract void set(iObject i, iObject v) throws Throwable;

    @Override
    public iClass getClazz() {
        return getType();
    }
}