package tests;

public class arrays {
    public static void main(String[] args) {
        String[] s;
        s = null;
        System.out.println(s == null);
        s = new String[0];
        System.out.println(s.length == 0);
        s = new String[100];
        System.out.println(s.length == 100);
        s = new String[] { "a", "b", "c", "d" };
        System.out.println(s.length == 4);
        for (int i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
        s = new String[3];
        s[0] = "x";
        s[1] = "y";
        s[2] = "z";
        for (int i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
        /*
         * s = new String[] { "a", "b", "c", "d" }; for (String z : s) {
         * System.out.println(z); }
         */
    }
}
