
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

public class iConstructorVirtual extends iConstructor {
    ConstructorDeclaration x;
    Scope scope;

    public iConstructorVirtual(Scope scope, ConstructorDeclaration x) {
        this.scope = scope;
        this.x = x;
    }

    public boolean isVarArgs() {
        return x.getParameters().getLast().map(p -> p.isVarArgs()).orElse(false);
    }

    public int getParameterCount() {
        return x.getParameters().size();
    }

    @Override
    public iClass[] getParameterTypes() {
        throw new UnsupportedOperationException();
        // return x.getParameters().stream().map(p -> new
        // iClassVirtual(scope)).toArray(iClass[]::new);
    }

    public iObject newInstance(iObject... args) throws Throwable {
        Scope s = scope.newChild(getDeclaringClassNode());
        iObjectVirtual i = new iObjectVirtual(s, new iClassVirtual(scope.parent, getDeclaringClassNode()));
        s.exec(x, i, args);
        return i;
    }

    public iClass getDeclaringClass() {
        return new iClassVirtual(scope.parent, getDeclaringClassNode());
    }

    public ClassOrInterfaceDeclaration getDeclaringClassNode() {
        return (ClassOrInterfaceDeclaration) x.getParentNode().get();
    }
}