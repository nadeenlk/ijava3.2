
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

public class iConstructorVirtual extends iConstructor {
    ConstructorDeclaration x;

    public iConstructorVirtual(Scope parent, ConstructorDeclaration x) {
        super(parent, x);
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
        iObjectVirtual i = new iObjectVirtual(getScope(), new iClassVirtual(getScope(), getDeclaringClassNode()));
        getScope().getExecutor().exec(x, i, args);
        return i;
    }

    public iClass getDeclaringClass() {
        return new iClassVirtual(getScope(), getDeclaringClassNode());
    }

    public ClassOrInterfaceDeclaration getDeclaringClassNode() {
        return (ClassOrInterfaceDeclaration) x.getParentNode().get();
    }
}