package bg.sofia.uni.fmi.mjt.splitwise.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class PaymentDataTest {

    @Test
    public void testInitialization() {
        Set<String> usernames = new HashSet<>();
        usernames.add("Alice");
        usernames.add("Bob");
        usernames.add("Charlie");

        PaymentData paymentData = new PaymentData(usernames);
        Map<String, Double> data = paymentData.getData();

        assertEquals(usernames.size(), data.size(), "Initial data size should match usernames size");
        for (String username : usernames) {
            assertTrue(data.containsKey(username), "Username should exist in the data");
            assertEquals(0.0, data.get(username), 0.001, "Initial balance should be 0");
        }
    }

    @Test
    public void testAddingPayment() {
        Set<String> usernames = new HashSet<>();
        usernames.add("Alice");
        usernames.add("Bob");
        usernames.add("Charlie");

        PaymentData paymentData = new PaymentData(usernames);

        paymentData.add("Alice", 20.0);
        paymentData.add("Bob", -10.0);
        paymentData.add("Charlie", 5.0);

        Map<String, Double> data = paymentData.getData();

        assertEquals(20.0, data.get("Alice"), 0.001, "Alice's balance should be updated");
        assertEquals(-10.0, data.get("Bob"), 0.001, "Bob's balance should be updated");
        assertEquals(5.0, data.get("Charlie"), 0.001, "Charlie's balance should be updated");
    }
}
