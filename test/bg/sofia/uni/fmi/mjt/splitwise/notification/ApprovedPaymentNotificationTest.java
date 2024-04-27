package bg.sofia.uni.fmi.mjt.splitwise.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.ApprovedPaymentNotification;
import org.junit.jupiter.api.Test;

public class ApprovedPaymentNotificationTest {

    @Test
    public void testInform() {
        String friend = "Alice";
        double amount = 25.0;

        ApprovedPaymentNotification notification = new ApprovedPaymentNotification(friend, amount);
        String expected = "Alice approved your payment 25.0 LV";
        String actual = notification.inform();

        assertEquals(expected, actual, "Notification message should be correct");
    }
}

