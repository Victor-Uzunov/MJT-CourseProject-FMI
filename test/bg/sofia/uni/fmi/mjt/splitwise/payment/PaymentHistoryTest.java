package bg.sofia.uni.fmi.mjt.splitwise.payment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentHistoryTest {

    @Test
    public void testToString() {
        double amount = 50.0;
        String otherName = "Alice";
        String reason = "Rent";
        PaymentType type = PaymentType.FRIEND_PAYMENT;

        PaymentHistory paymentHistory = new PaymentHistory(amount, otherName, reason, type);

        String expectedString = String.format("%s - %s, Reason for payment: %s, Amount: %.2f",
                type.getDisplayName(), otherName, reason, amount);

        assertEquals(expectedString, paymentHistory.toString(),
                "toString() method should return formatted string");
    }
}

