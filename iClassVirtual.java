import java.util.stream.Stream;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class iClassVirtual extends iClass {
    ClassOrInterfaceDeclaration x;

    public iClassVirtual(Scope parent, ClassOrInterfaceDeclaration x) {
        super(parent, x);
        this.x = x;
        getScope().setClz(this);
        if (x.getConstructors().size() == 0) {
            x.addConstructor(Modifier.Keyword.PUBLIC);
        }
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
                .stream().map(m -> new iMethodVirtual(getScope(), m)).findAny().orElseThrow(NoSuchMethodException::new);
    }

    public iMethod[] getMethods() {
        throw new UnsupportedOperationException();
    }

    public iConstructor getConstructor(iClass... parameterTypes) throws Throwable {
        return new iConstructorVirtual(getScope(),
                x.getConstructorByParameterTypes(Stream.of(parameterTypes).map(x -> x.getName()).toArray(String[]::new))
                        .orElseThrow(NoSuchMethodException::new));
    }

    public iConstructor[] getConstructors() {
        return x.getConstructors().stream().map(c -> new iConstructorVirtual(getScope(), c))
                .toArray(iConstructor[]::new);
    }

    public iObject newArray(int[] dimensions) {
        throw new UnsupportedOperationException();
    }

    public void setItem(iObject a, int i, iObject v) {
        throw new UnsupportedOperationException();
    }

    public iObject getItem(iObject a, int i) {
        throw new UnsupportedOperationException();
    }
}