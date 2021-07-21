
public abstract class iMethod extends iExecutable {
    public abstract String getName();

    public abstract iObject invoke(iObject obj, iObject... args) throws Throwable;

    public abstract iClass getReturnType();
}