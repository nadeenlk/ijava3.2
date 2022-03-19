
public class iClassArrayWrapped extends iClassWrapped {
    public iClassArrayWrapped(Scope s, iClassWrapped x) {
        super(s, x.x);
    }

    @Override
    public iField getField(String name) throws Throwable {
        if (name.equals("length")) {
            return new iFieldArrayLength(getScope());
        }
        throw new NoSuchMethodException();
    }

    @Override
    public String toString() {
        return String.format("iClassArrayWrapped(%s)", x.getName());
    }
}
