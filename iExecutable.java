import com.github.javaparser.ast.Node;

public abstract class iExecutable extends iObject {
    public iExecutable(Scope parent, Node node) {
        super(parent, node);
    }

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