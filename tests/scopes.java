package tests;

public class scopes {
    public static void main(String[] args) {
        int x = 1;
        {
            int y = 2;
            {
                int z = 3;
                {
                    System.out.printf("x=%d y=%d z=%d\n", x, y, z);
                }
            }
        }
    }
}
