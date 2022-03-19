public class iObjectVariable extends iObject {
    iClass type;
    String name;
    iObject value;

    public iObjectVariable(Scope parent, iClass type, String name) {
        super(parent, null);
        this.type = type;
        this.name = name;
    }

    public iObject set(iObject value) {
        getScope().log("[var] set %s=%s", name, value);
        return this.value = value;
    }

    public iObject get() {
        getScope().log("[var] get %s=%s", name, value);
        return value;
    }

    public iObjectWrapped asWrapped() {
        return value.asWrapped();
    }

    public iClass getClazz() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("iObjectVarialbe<%s>(%s=%s)", type, name, value);
    }
}
