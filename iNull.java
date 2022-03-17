public class iNull extends iClass {
    static iNull Null = new iNull();

    public String getName() {
        throw new UnsupportedOperationException();
    }

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
        throw new UnsupportedOperationException();
    }

    public iMethod[] getMethods() {
        throw new UnsupportedOperationException();
    }

    public iConstructor getConstructor(iClass... parameterTypes) throws Throwable {
        throw new UnsupportedOperationException();
    }

    public iConstructor[] getConstructors() {
        throw new UnsupportedOperationException();
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

    @Override
    public boolean equals(Object obj) {
        return obj == Null;
    }

    @Override
    public String toString() {
        return "iNullObject";
    }

    @Override
    public iClass getClazz() {
        return this;
    }
}
