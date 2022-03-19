package tests;

import java.math.BigInteger;

public class factorial3 {
    public static void main(String args[]) {
        BigInteger fact = BigInteger.valueOf(1L);
        int number = 50;
        for (int i = 1; i <= number; i++) {
            fact = fact.multiply(BigInteger.valueOf(((Integer) i).longValue()));
        }
        System.out.println("Factorial of " + number + " is: " + fact);
    }
}
