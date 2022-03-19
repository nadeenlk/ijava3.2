import java.lang.reflect.Array;
import java.util.Iterator;

public class IterableArray implements Iterable<Object> {
    Object x;

    public IterableArray(Object x) {
        this.x = x;
    }

    @Override
    public Iterator<Object> iterator() {
        return new ArrayIterator(x);
    }
}

class ArrayIterator implements Iterator<Object> {
    Object x;
    int i, l;

    public ArrayIterator(Object x) {
        this.x = x;
        i = 0;
        l = Array.getLength(x);
    }

    @Override
    public boolean hasNext() {
        return i < l;
    }

    @Override
    public Object next() {
        return Array.get(x, i++);
    }
}
