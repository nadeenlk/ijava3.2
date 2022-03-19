package tests;

public class generic_args {
    public static void main(String[] args) {
        // test_clz<Object> z = new test_clz<Object>();
        // z.test((Object) "lol");
        // new test_clz<Object>().test((Object) "lol");
        //Arrays.copyOf(args, 0);
    }

    static class test_clz<E extends Object> {
        void test(E x) {
            System.out.println("test " + x);
        }
    }
}
