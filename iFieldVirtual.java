import com.github.javaparser.ast.body.VariableDeclarator;

public class iFieldVirtual extends iField {
    VariableDeclarator x;

    public iFieldVirtual(Scope parent, VariableDeclarator x) {
        super(parent, x);
        this.x = x;
    }

    public iClass getType() {
        throw new UnsupportedOperationException();
    }

    public iObject get(iObject i) throws Throwable {
        throw new UnsupportedOperationException();
    }

    public void set(iObject i, iObject v) throws Throwable {
        throw new UnsupportedOperationException();
    }
}
