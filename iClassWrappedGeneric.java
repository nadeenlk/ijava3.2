import java.lang.reflect.Type;

public class iClassWrappedGeneric extends iClassWrapped {

    public iClassWrappedGeneric(Scope parent, iClassWrapped x, Type t) {
        super(parent, x.x);
    }

}
