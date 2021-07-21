package tests;

public class test_method {
    public static void test1() {
        System.out.println("test1();");
    }

    public static void test2(int x, String y) {
        System.out.println("test2(" + x + "," + y + ");");
    }

    static class test_class {
        public void test() {
            System.out.println("test_class.test();");
        }

        public test_class() {
            System.out.println("new test_class();");
        }
    }

    public static String test4(String x) {
        return "test4(" + x + ")";
    }

    public static void main(String[] args) {
        test1();
        test2(123, "lol");
        System.out.println("test4=" + test4("lol"));
        System.out.println(new String("abc"));
        new test_class();
        new test_class().test();
    }
}
