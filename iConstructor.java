import com.github.javaparser.ast.Node;

public abstract class iConstructor extends iExecutable {
    public iConstructor(Scope parent, Node node) {
        super(parent, node);
    }

    public abstract iObject newInstance(iObject... args) throws Throwable;

    public abstract iClass getDeclaringClass();
}