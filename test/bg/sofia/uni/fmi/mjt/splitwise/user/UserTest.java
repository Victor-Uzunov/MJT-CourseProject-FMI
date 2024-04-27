package bg.sofia.uni.fmi.mjt.splitwise.user;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.ApprovedPaymentNotification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.OwedMoneyNotification;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private User user;
    private User friend;

    @BeforeEach
    public void setUp() {
        user = new User("alice", "password");
        friend = new User("bob", "password");
    }

    @Test
    public void testAddFriend() {
        user.addFriend(friend);

        assertTrue(user.getFriends().containsKey("bob"), "Friend should be added to user's friend list");
        assertTrue(friend.getFriends().containsKey("alice"), "User should be added to friend's friend list");
    }

    @Test
    public void testCreateGroup() {
        Set<User> users = new HashSet<>();
        users.add(user);
        users.add(friend);

        user.createGroup("group1", users);

        assertTrue(user.getGroups().contains("group1"), "User should be added to the group");
        assertTrue(friend.getGroups().contains("group1"), "Friend should be added to the group");
    }

    @Test
    public void testSplitWithFriend() throws NoSuchFriendException {
        user.addFriend(friend);
        user.splitWithFriend(friend, 30.0, "dinner");

        assertEquals(15.0, user.getFriends().get("bob"), "User's balance with friend should be updated after splitting expenses");
        assertEquals(-15.0, friend.getFriends().get("alice"), "Friend's balance with user should be updated after splitting expenses");
        assertEquals(1, friend.getFriendNotifications().size(),
                "Friend should receive a notification after splitting expenses");
        assertTrue(friend.getFriendNotifications().get(0) instanceof OwedMoneyNotification,
                "Notification should be of type OwedMoneyNotification");
    }

    @Test
    public void testPayed() throws NoSuchFriendException {
        user.addFriend(friend);
        user.splitWithFriend(friend, 100, "rent");

        user.payed(20.0, friend);

        assertEquals(30.0, user.getFriends().get("bob"), "User's balance with friend should be updated after paying");
        assertEquals(-30.0, friend.getFriends().get("alice"), "Friend's balance with user should be updated after paying");
        assertEquals(2, friend.getFriendNotifications().size(),
                "Friend should receive a notification after being paid");
        assertTrue(friend.getFriendNotifications().get(0) instanceof OwedMoneyNotification,
                "Notification should be of type OwedMoneyNotification");
        assertTrue(friend.getFriendNotifications().get(1) instanceof ApprovedPaymentNotification,
                "Notification should be of type ApprovedPaymentNotification");
    }

    @Test
    public void testGetStatus() {
        user.addFriend(friend);
        user.getFriends().put("bob", -50.0);

        Group group = new Group("group1", Set.of(user, friend));
        group.getPayments().put(user.getUsername(), new PaymentData(Set.of(user.getUsername(), friend.getUsername())));

        String status = user.getStatus(Map.of("group1", group));

        assertTrue(status.contains("***** Friends *****"),
                "Status should contain a section for friends");
        assertTrue(status.contains("***** Groups *****"),
                "Status should contain a section for groups");
    }

    @Test
    public void testCheckIsNotFriend() {
        user.addFriend(friend);

        assertFalse(user.checkIsNotFriend("bob"), "Should return false for an existing friend");
        assertTrue(user.checkIsNotFriend("charlie"), "Should return true for a non-existing friend");
    }

    @Test
    public void testCheckIsInGroup() {
        user.createGroup("group1", Set.of(user, friend));

        assertTrue(user.checkIsInGroup("group1"), "Should return true for an existing group");
        assertFalse(user.checkIsInGroup("group2"), "Should return false for a non-existing group");
    }

    @Test
    public void testEqualsPassword() {
        assertTrue(user.equalsPassword("password"), "Should return true for correct password");
        assertFalse(user.equalsPassword("wrongpassword"), "Should return false for incorrect password");
    }
}

