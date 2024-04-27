package bg.sofia.uni.fmi.mjt.splitwise.payment;

import java.io.Serializable;

public record PaymentHistory(double amount, String otherName, String reason, PaymentType type)
        implements Serializable {

    @Override
    public String toString() {
        return String.format("%s - %s, Reason for payment: %s, Amount: %.2f",
                type.getDisplayName(), otherName, reason, amount);
    }
}
