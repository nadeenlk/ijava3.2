import com.github.javaparser.ast.body.MethodDeclaration;

public class iMethodVirtual extends iMethod {
    Scope scope;
    MethodDeclaration x;

    public iMethodVirtual(Scope scope, MethodDeclaration x) {
        this.scope = scope;
        this.x = x;
    }

    public String getName() {
        return x.getNameAsString();
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
    }

    public iClass getReturnType() {
        throw new UnsupportedOperationException();
        // return new iClassVirtual(s.resolveClass(d.getType().asString()));
    }

    public iObject invoke(iObject obj, iObject... args) throws Throwable {
        return scope.exec(x, obj, args);
    }

}