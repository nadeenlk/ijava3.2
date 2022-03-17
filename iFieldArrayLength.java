import java.lang.reflect.Array;

public class iFieldArrayLength extends iFieldWrapped {
    public iFieldArrayLength() {
        super(null);
    }

    @Override
    public iClass getType() {
        return new iClassWrapped(int.class);
    }

    @Override
    public iObjectWrapped get(iObject i) throws Throwable {
        //System.out.println("iFieldArrayLength.get " + i);
        int l = Array.getLength(i.asWrapped().x);
        return new iObjectWrapped(l);
    }

    @Override
    public String toString() {
        return "iFieldArrayLength()";
    }
}
