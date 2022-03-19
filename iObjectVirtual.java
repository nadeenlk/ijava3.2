public class iObjectVirtual extends iObject {
    iClassVirtual clz;

    public iObjectVirtual(Scope parent, iClassVirtual clz) {
        super(parent, null);
        this.clz = clz;
    }

    public iClass getClazz() {
        return clz;
    }
}