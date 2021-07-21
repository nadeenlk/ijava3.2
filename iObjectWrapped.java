public class iObjectWrapped extends iObject {
    Object x;

    public iObjectWrapped(Object x) {
        this.x = x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof iObjectWrapped)
            return x.equals(((iObjectWrapped) obj).x);
        return false;
    }

    public iClass getClazz() {
        return new iClassWrapped(x.getClass());
    }

    public iObjectWrapped asWrapped() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("iObjectWrapped<%s>(%s)", x == null ? null : x.getClass(), x);
    }
}
