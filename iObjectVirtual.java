public class iObjectVirtual extends iObject {
    Scope scope;
    iClassVirtual clz;

    public iObjectVirtual(Scope scope, iClassVirtual clz) {
        this.scope = scope;
        this.clz = clz;
    }

    public iClass getClazz() {
        return clz;
    }
}