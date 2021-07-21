import java.lang.reflect.Field;

public class iFieldWrapped extends iField {
    Field x;

    public iFieldWrapped(Field x) {
        this.x = x;
    }

    public iClass getType() {
        return new iClassWrapped(x.getType());
    }

    public iObject get(iObject i) throws Throwable {
        return new iObjectWrapped(x.get(i));
    }
}