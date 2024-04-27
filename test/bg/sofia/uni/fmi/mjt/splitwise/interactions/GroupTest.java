package bg.sofia.uni.fmi.mjt.splitwise.interactions;

import java.util.HashSet;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.ApprovedPaymentNotification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.OwedMoneyNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;

import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupTest {

    private Group group;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        Set<User> members = new HashSet<>();
        user1 = new User("alice", "password");
        user2 = new User("bob", "password");
        members.add(user1);
        members.add(user2);
        group = new Group("group1", members);
    }

    @Test
    public void testSplit() throws NoSuchUserInGroupException {
        group.split("alice", 30.0, "dinner");

        assertEquals(15.0, group.getPayments().get("alice").getData().get("bob"),
                "User 1 should owe User 2 15.0 after splitting expenses");
        assertEquals(-15.0, group.getPayments().get("bob").getData().get("alice"),
                "User 2 should owe User 1 -15.0 after splitting expenses");

        assertEquals(1, user2.getGroupNotifications().get("group1").size(),
                "User 2 should receive a notification after splitting expenses");
        assertTrue(user2.getGroupNotifications().get("group1").get(0) instanceof OwedMoneyNotification,
                "Notification should be of type OwedMoneyNotification");
    }

    @Test
    public void testPayed() throws NoSuchUserInGroupException {
        group.split("alice", 30.0, "dinner");
        group.payed(user1, user2, 10.0);

        assertEquals(5.0, group.getPayments().get("alice").getData().get("bob"),
                "User 2 should owe User 1 5.0 after User 2 pays 10.0");
        assertEquals(-5.0, group.getPayments().get("bob").getData().get("alice"),
                "User 2 should owe User 1 5.0 after User 2 pays 10.0");

        assertEquals(2, user2.getGroupNotifications().get("group1").size(),
                "User 1 should receive a notification after being paid");
        assertTrue(user2.getGroupNotifications().get("group1").get(0) instanceof OwedMoneyNotification,
                "Notification should be of type OwedMoneyNotification");
        assertTrue(user2.getGroupNotifications().get("group1").get(1) instanceof ApprovedPaymentNotification,
                "Notification should be of type ApprovedPaymentNotification");
    }

    @Test
    public void testGetMembers() {
        assertEquals(2, group.getMembers().size(), "Should return the correct number of members in the group");
        assertTrue(group.getMembers().contains(user1), "User 1 should be a member of the group");
        assertTrue(group.getMembers().contains(user2), "User 2 should be a member of the group");
    }

    @Test
    public void testGetPayments() {
        assertEquals(2, group.getPayments().size(), "Should return the correct number of payment data in the group");
        assertTrue(group.getPayments().containsKey("alice"), "Should contain payment data for User 1");
        assertTrue(group.getPayments().containsKey("bob"), "Should contain payment data for User 2");
    }

    @Test
    public void testGetGroupName() {
        assertEquals("group1", group.getGroupName(), "Should return the correct group name");
    }

    @Test
    public void testWithInvalidUserInTheGroup() {
        assertThrows(NoSuchUserInGroupException.class, () -> group.split("coco", 12, "rent"),
                "An exception must be thrown");

        User u = new User("coco", "111");
        assertThrows(NoSuchUserInGroupException.class, () -> group.payed(u, u, 9), "An exception must be thrown");
    }
}

