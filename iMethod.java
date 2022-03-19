import com.github.javaparser.ast.Node;

public abstract class iMethod extends iExecutable {

    public iMethod(Scope parent, Node node) {
        super(parent, node);
    }

    public abstract String getName();

    public abstract iObject invoke(iObject obj, iObject... args) throws Throwable;

    public abstract iClass getReturnType();
}