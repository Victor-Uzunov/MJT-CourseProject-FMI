package bg.sofia.uni.fmi.mjt.splitwise.payment;

import java.text.DecimalFormat;

public class DoubleCalculator {
    public static double divide(double numerator, double denominator) {
        double result = numerator / denominator;

        if (denominator == 0) {
            throw new IllegalArgumentException("Cannot divide by 0");
        }

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedResult1 = df.format(result).replace(",", ".");
        return Double.parseDouble(formattedResult1);
    }

    public static double format(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedResult = df.format(number).replace(",", ".");
        return Double.parseDouble(formattedResult);
    }
}
