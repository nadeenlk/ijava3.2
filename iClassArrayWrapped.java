
public class iClassArrayWrapped extends iClassWrapped {
    public iClassArrayWrapped(iClassWrapped x) {
        super(x.x);
    }

    @Override
    public iField getField(String name) throws Throwable {
        if (name.equals("length")) {
            return new iFieldArrayLength();
        }
        throw new NoSuchMethodException();
    }

    @Override
    public String toString() {
        return String.format("iClassArrayWrapped(%s)", x.getName());
    }
}
