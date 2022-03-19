package tests;

public class method {
    public static void test1() {
        System.out.println("test1();");
    }

    public static void test2(int x, String y) {
        System.out.println("test2(" + x + "," + y + ");");
    }

    static class clazz {
        public void test() {
            System.out.println("clazz.test();");
        }

        public clazz() {
            System.out.println("new clazzss();");
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
        new clazz();
        new clazz().test();
    }
}
