package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import java.io.Serializable;

public class ApprovedPaymentNotification implements Serializable, Notification {
    private final String friend;
    private final double amount;

    public ApprovedPaymentNotification(String friend, double amount) {
        this.friend = friend;
        this.amount = amount;
    }

    @Override
    public String inform() {
        return String.format("%s approved your payment %s LV", friend, amount);
    }
}
