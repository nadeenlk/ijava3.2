public abstract class iField extends iObject {
    public abstract iClass getType();

    public abstract iObject get(iObject i) throws Throwable;

    @Override
    public iClass getClazz() {
        return getType();
    }
}