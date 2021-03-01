package de.malkusch.ha;

import org.junit.jupiter.api.Test;

public class SandboxTest {

    @Test
    public void testAssignment() {
        changeMoney2(5254);
    }

    private static int[] coins = { 50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 };

    private static void changeMoney(int amountInCents) {
        int remainingAmount = amountInCents;
        while (remainingAmount > 0) {
            for (var coin : coins) {
                var fit = remainingAmount / coin;
                remainingAmount %= coin;
                System.out.println(fit + " x " + coin);
            }
        }
    }

    private static void changeMoney2(int amountInCents) {
        int remainingAmount = amountInCents;
        while (remainingAmount > 0) {
            for (var coin : coins) {
                while (coin <= remainingAmount) {
                    System.out.println(coin);
                    remainingAmount -= coin;
                }
            }
        }
    }

}
