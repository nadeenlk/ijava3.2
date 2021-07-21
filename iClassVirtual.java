import java.util.stream.Stream;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class iClassVirtual extends iClass {
    ClassOrInterfaceDeclaration x;
    Scope scope;

    public iClassVirtual(Scope scope, ClassOrInterfaceDeclaration x) {
        this.scope = scope;
        scope.obj = this;
        this.x = x;
    }

    public String getName() {
        return x.getNameAsString();
    }

    @Override
    public iObject cast(iObject obj) {
        throw new UnsupportedOperationException();
    }

    public boolean isAssignableFrom(iClass cls) {
        throw new UnsupportedOperationException();
    }

    public iClass getComponentType() {
        throw new UnsupportedOperationException();
    }

    public iField getField(String name) throws Throwable {
        throw new UnsupportedOperationException();
    }

    public iMethod getMethod(String name, iClass... parameterTypes) throws Throwable {
        return x.getMethodsByName(name)// Signature(name, Stream.of(parameterTypes).map(c ->
                                       // c.getName()).toArray(String[]::new))
                .stream().map(m -> new iMethodVirtual(scope.newChild(m), m)).findAny()
                .orElseThrow(NoSuchMethodException::new);
    }

    public iMethod[] getMethods() {
        throw new UnsupportedOperationException();
    }

    public iConstructor getConstructor(iClass... parameterTypes) throws Throwable {
        return new iConstructorVirtual(scope,
                x.getConstructorByParameterTypes(Stream.of(parameterTypes).map(x -> x.getName()).toArray(String[]::new))
                        .orElseThrow(NoSuchMethodException::new));
    }

    public iConstructor[] getConstructors() {
        throw new UnsupportedOperationException();
    }
}