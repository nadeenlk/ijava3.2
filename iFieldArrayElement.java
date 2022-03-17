
public class iFieldArrayElement extends iFieldWrapped {
    iClass c;
    int i;

    public iFieldArrayElement(iClass c, int i) {
        super(null);
        this.c = c;
        this.i = i;
    }

    @Override
    public iClass getType() {
        return c;
    }

    @Override
    public iObject get(iObject a) throws Throwable {
        return c.getItem(a, i);
    }

    @Override
    public void set(iObject a, iObject v) throws Throwable {
        c.setItem(a, i, v);
    }

    @Override
    public String toString() {
        return "iFieldArrayElement()";
    }
}
