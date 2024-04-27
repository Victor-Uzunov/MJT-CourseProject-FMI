package bg.sofia.uni.fmi.mjt.splitwise.user;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.ApprovedPaymentNotification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.OwedMoneyNotification;
import bg.sofia.uni.fmi.mjt.splitwise.payment.DoubleCalculator;
import bg.sofia.uni.fmi.mjt.splitwise.payment.Payment;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentType;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.Identifiable;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User implements Identifiable, Serializable {
    private final String username;
    private final String password;
    private final Set<String> groups;
    private final Map<String, Double> friends;
    private final Map<String, List<Notification>> groupNotifications;
    private final List<Notification> friendNotifications;
    private final List<PaymentHistory> paymentHistory;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.groups = new HashSet<>();
        this.friends = new HashMap<>();
        this.groupNotifications = new HashMap<>();
        this.friendNotifications = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Map<String, Double> getFriends() {
        return friends;
    }

    public void addFriend(User user) {
        friends.put(user.username, 0.0);
        if (!user.getFriends().containsKey(this.getUsername())) {
            user.addFriend(this);
        }
    }

    public void createGroup(String groupName, Set<User> users) {
        groups.add(groupName);
        for (User u : users) {
            u.getGroups().add(groupName);
        }
    }

    public void splitWithFriend(User friend, double amount, String reason) throws NoSuchFriendException {
        if (friend == null || checkIsNotFriend(friend.getUsername())) {
            throw new NoSuchFriendException("You are not friend with this user!");
        }
        double value = friends.get(friend.getUsername()) + DoubleCalculator.divide(amount, 2.0);
        double minusValue = friend.getFriends().get(this.username) - DoubleCalculator.divide(amount, 2.0);
        friends.put(friend.getUsername(), DoubleCalculator.format(value));
        friend.getFriends().put(this.username, DoubleCalculator.format(minusValue));
        friend.friendNotifications.add(new OwedMoneyNotification(username,
                new Payment(DoubleCalculator.divide(DoubleCalculator.format(amount), 2.0), reason)));
        paymentHistory.add(new PaymentHistory(DoubleCalculator.format(amount), friend.username,
                reason, PaymentType.FRIEND_PAYMENT));
    }

    public String getStatus(Map<String, Group> userGroups) {
        StringBuilder result = new StringBuilder();
        result.append("***** Friends *****").append(System.lineSeparator());
        appendPayments(result, friends);
        result.append(System.lineSeparator());
        result.append("***** Groups *****").append(System.lineSeparator());
        for (Map.Entry<String, Group> entry : userGroups.entrySet()) {
            result.append(entry.getKey()).append(System.lineSeparator());
            PaymentData data = entry.getValue().getPayments().get(this.username);
            appendPayments(result, data.getData());
        }
        return result.toString();
    }

    public void payed(double amount, User user) throws NoSuchFriendException {
        if (checkIsNotFriend(user.getUsername())) {
            throw new NoSuchFriendException("You are not friend with this user!");
        }
        double change = friends.get(user.getUsername()) - amount;
        double otherChange = user.friends.get(this.username) + amount;
        friends.put(user.username, DoubleCalculator.format(change));
        user.friends.put(this.username, DoubleCalculator.format(otherChange));
        user.friendNotifications.add(new ApprovedPaymentNotification(username, DoubleCalculator.format(amount)));
    }

    private void appendPayments(StringBuilder result, Map<String, Double> data) {
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                result.append(entry.getKey()).append(": ").append("Owes you ").append(entry.getValue())
                        .append(" lv").append(System.lineSeparator());
            } else if (entry.getValue() < 0) {
                result.append(entry.getKey()).append(": ").append("You owe ")
                        .append(entry.getValue() * -1).append(" lv").append(System.lineSeparator());
            }
        }
    }

    public boolean checkIsNotFriend(String friendUsername) {
        return !friends.containsKey(friendUsername);
    }

    public boolean checkIsInGroup(String groupName) {
        return groups.contains(groupName);
    }

    public boolean equalsPassword(String password) {
        return this.password.equals(password);
    }

    public List<Notification> getFriendNotifications() {
        return friendNotifications;
    }

    public Map<String, List<Notification>> getGroupNotifications() {
        return groupNotifications;
    }

    public List<PaymentHistory> getPaymentHistory() {
        return paymentHistory;
    }

    @Override
    public String getID() {
        return username;
    }
}
