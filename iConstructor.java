
public abstract class iConstructor extends iExecutable {

    public abstract iObject newInstance(iObject... args) throws Throwable;

    public abstract iClass getDeclaringClass();
}