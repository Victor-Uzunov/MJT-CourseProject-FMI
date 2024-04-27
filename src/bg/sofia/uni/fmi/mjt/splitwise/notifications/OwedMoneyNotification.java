package bg.sofia.uni.fmi.mjt.splitwise.notifications;

import bg.sofia.uni.fmi.mjt.splitwise.payment.Payment;

import java.io.Serializable;

public class OwedMoneyNotification implements Serializable, Notification {
    private final String friend;
    private final Payment payment;

    public OwedMoneyNotification(String friend, Payment payment) {
        this.friend = friend;
        this.payment = payment;
    }

    @Override
    public String inform() {
        return String.format("%s: You owe %s LV [%s]", friend, payment.amount(), payment.reason());
    }
}
