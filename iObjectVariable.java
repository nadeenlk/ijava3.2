public class iObjectVariable extends iObject {
    Scope scope;
    iClass type;
    String name;
    iObject value;

    public iObjectVariable(Scope scope, iClass type, String name) {
        this.scope = scope;
        this.type = type;
        this.name = name;
    }

    public iObject set(iObject value) {
        scope.log("[var] set %s=%s\n", name, value);
        return this.value = value;
    }

    public iObject get() {
        scope.log("[var] get %s=%s\n", name, value);
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
