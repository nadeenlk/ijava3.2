package tests;

public class factorial2 {
    static int factorial(int n) {
        if (n == 0)
            return 1;
        else
            return (n * factorial(n - 1));
    }

    public static void main(String args[]) {
        int fact = 1;
        int number = 11;
        fact = factorial(number);
        System.out.println("Factorial of " + number + " is: " + fact);
    }
}
