import com.github.javaparser.ast.body.VariableDeclarator;

public class iFieldVirtual extends iField {
    VariableDeclarator x;
    Scope scope;

    public iFieldVirtual(Scope scope, VariableDeclarator x) {
        this.scope = scope;
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