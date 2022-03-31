import java.lang.reflect.Array;

public class iFieldArrayLength extends iFieldWrapped {
    public iFieldArrayLength(Scope parent) {
        super(parent, null);
    }

    @Override
    public iClass getType() {
        return iClassWrapped.from(getScope(), int.class);
    }

    @Override
    public iObjectWrapped get(iObject i) throws Throwable {
        // System.out.println("iFieldArrayLength.get " + i);
        int l = Array.getLength(i.asWrapped().x);
        return new iObjectWrapped(getScope(), l);
    }

    @Override
    public String toString() {
        return "iFieldArrayLength()";
    }
}
