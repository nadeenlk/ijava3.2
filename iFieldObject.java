
public class iFieldObject extends iObject {
    iObject x;
    iField f;

    public iFieldObject(iObject x, iField f) {
        this.x = x;
        this.f = f;
    }

    public iClass getClazz() {
        return f.getType();
    }

    public iObjectWrapped asWrapped() {
        try {
            return f.get(x).asWrapped();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void set(iObject v) throws Throwable {
        f.set(x, v);
    }

    @Override
    public String toString() {
        return String.format("iFieldObject(%s,%s)", x, f);
    }
}
