package tests;


public class args {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg%d=%s\n", i, args[i]);
        }
    }
}
