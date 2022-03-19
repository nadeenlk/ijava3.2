public class iObjectReturn extends iObject {
    iObject value;

    public iObjectReturn(Scope parent, iObject value) {
        super(parent, null);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("iObjectReturn(%s)", value);
    }
}
