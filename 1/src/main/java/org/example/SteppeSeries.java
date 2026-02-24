package org.example;

public class SteppeSeries {
    public static double sin(double x, int counter) throws Exception {
        if (counter < 0 || counter > 100) {
            throw new Exception("Некорректное количество слагаемых степенного ряда");
        }
        double res = 0.0;
        for (int i = 0; i < counter; i++) {
            res += Math.pow(-1.0, i) * Math.pow(x, 2 * i + 1) / factorial(2 * i + 1);
        }
        return res;
    }

    private static double factorial(int n) {
        double res = 1;
        while (n > 1) {
            res *= (double) n;
            n -= 1;
        }
        return res;
    }
}
