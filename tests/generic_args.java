package tests;

import java.util.Arrays;

public class generic_args {
    public static void main(String[] args) {
        System.out.println(Arrays.class.getSimpleName());
        test_clz<Object> z = new test_clz<Object>();
        z.test((Object) "lol");
        new test_clz<Object>().test((Object) "lol");
        System.out.println(Arrays.toString(Arrays.copyOf("nadeen".split(""), 3)));
    }

    static class test_clz<E extends Object> {
        void test(E x) {
            System.out.println("test " + x);
        }
    }
}
