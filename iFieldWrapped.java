import java.lang.reflect.Field;

public class iFieldWrapped extends iField {
    Field x;

    public iFieldWrapped(Field x) {
        this.x = x;
    }

    public iClass getType() {
        return new iClassWrapped(x.getType());
    }

    @Override
    public iObjectWrapped asWrapped() {
        try {
            return this.get(null).asWrapped();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public iObject get(iObject i) throws Throwable {
        return new iObjectWrapped(x.get(i.asWrapped().x));
    }

    public void set(iObject i, iObject v) throws Throwable {
        x.set(i.asWrapped().x, v.asWrapped().x);
    }

    @Override
    public String toString() {
        return String.format("iFieldWrapped(%s)", x);
    }
}
