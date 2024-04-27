package bg.sofia.uni.fmi.mjt.splitwise.payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleCalculatorTest {

    @Test
    public void testDivideWithValidInput() {
        double numerator = 10.0;
        double denominator = 2.0;
        double expectedResult = 5.0;

        double result = DoubleCalculator.divide(numerator, denominator);

        assertEquals(expectedResult, result, "Valid input division failed.");
    }

    @Test
    public void testDivideByZero() {
        double numerator = 10.0;
        double denominator = 0.0;

        assertThrows(IllegalArgumentException.class, () -> DoubleCalculator.divide(numerator, denominator)
                , "Division by zero must not be possible.");
    }

    @Test
    public void testDivideByNegativeDenominator() {
        double numerator = 10.0;
        double denominator = -2.0;
        double expectedResult = -5.0;

        double result = DoubleCalculator.divide(numerator, denominator);

        assertEquals(expectedResult, result, "Division by negative denominator failed.");
    }

    @Test
    public void testDivideZeroByNonZeroDenominator() {
        double numerator = 0.0;
        double denominator = 5.0;
        double expectedResult = 0.0;

        double result = DoubleCalculator.divide(numerator, denominator);

        assertEquals(expectedResult, result, "Division of zero by non-zero denominator failed.");
    }

    @Test
    public void testDivideWithSmallResult() {
        double numerator = Double.MIN_VALUE;
        double denominator = Double.MAX_VALUE;
        double expectedResult = 0.0;

        double result = DoubleCalculator.divide(numerator, denominator);

        assertEquals(expectedResult, result, "Division with small result failed.");
    }
}

