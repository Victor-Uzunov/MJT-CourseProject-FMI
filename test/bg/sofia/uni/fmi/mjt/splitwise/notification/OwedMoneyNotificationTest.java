package bg.sofia.uni.fmi.mjt.splitwise.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.OwedMoneyNotification;
import bg.sofia.uni.fmi.mjt.splitwise.payment.Payment;
import org.junit.jupiter.api.Test;

public class OwedMoneyNotificationTest {

    @Test
    public void testInform() {
        String friend = "Alice";
        Payment payment = new Payment(30.0, "For dinner");

        OwedMoneyNotification notification = new OwedMoneyNotification(friend, payment);
        String expected = "Alice: You owe 30.0 LV [For dinner]";
        String actual = notification.inform();

        assertEquals(expected, actual, "Notification message should be correct");
    }
}

