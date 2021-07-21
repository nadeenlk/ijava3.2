public class iClassArrayWrapped extends iClassWrapped {
    public iClassArrayWrapped(iClassWrapped x) {
        super(x.x);
    }

    @Override
    public String toString() {
        return String.format("iClassArrayWrapped(%s)", x.getName());
    }
}
