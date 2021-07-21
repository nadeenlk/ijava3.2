
public abstract class iExecutable {
    public abstract boolean isVarArgs();

    public abstract int getParameterCount();

    public abstract iClass[] getParameterTypes();

    public iMethod asMethod() {
        return (iMethod) this;
    }

    public iConstructor asConstructor() {
        return (iConstructor) this;
    }
}