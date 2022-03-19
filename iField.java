import com.github.javaparser.ast.Node;

public abstract class iField extends iObject {

    public iField(Scope parent, Node node) {
        super(parent, node);
    }

    public abstract iClass getType();

    public abstract iObject get(iObject i) throws Throwable;

    public abstract void set(iObject i, iObject v) throws Throwable;

    @Override
    public iClass getClazz() {
        return getType();
    }
}