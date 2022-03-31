import java.lang.reflect.Field;

public class iFieldWrapped extends iField {
    Field x;

    public iFieldWrapped(Scope parent, Field x) {
        super(parent, null);
        this.x = x;
    }

    public iClass getType() {
        return iClassWrapped.from(getScope(), x.getType());
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
        return new iObjectWrapped(getScope(), x.get(i.asWrapped().x));
    }

    public void set(iObject i, iObject v) throws Throwable {
        x.set(i.asWrapped().x, v.asWrapped().x);
    }

    @Override
    public String toString() {
        return String.format("iFieldWrapped(%s)", x);
    }
}
