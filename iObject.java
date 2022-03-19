import com.github.javaparser.ast.Node;

public abstract class iObject {
    Scope current_scope;

    public iObject(Scope parent, Node node) {
        this.current_scope = parent.getChild(node);
    }

    public Scope getScope() {
        return current_scope;
    }

    public iClass asClass() throws Throwable {
        return (iClass) this;
    }

    public iField asField() throws Throwable {
        return (iField) this;
    }

    public iObjectWrapped asWrapped() {
        return (iObjectWrapped) this;
    }

    public iObjectVariable asVariable() {
        return (iObjectVariable) this;
    }

    public iClass getClazz() {
        throw new UnsupportedOperationException();
    }

    public iObject asExecArg() {
        try {
            if (this instanceof iField)
                return asField().get(null);
            else if (this instanceof iClass)
                return new iObjectWrapped(getScope(), null);
            else if (this instanceof iObjectVariable)
                return asVariable().get();
            else
                return this;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
