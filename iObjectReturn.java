public class iObjectReturn extends iObject {
    iObject value;

    public iObjectReturn(iObject value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("iObjectReturn(%s)", value);
    }
}
