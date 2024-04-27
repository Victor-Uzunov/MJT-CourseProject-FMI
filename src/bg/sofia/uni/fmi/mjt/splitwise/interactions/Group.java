package bg.sofia.uni.fmi.mjt.splitwise.interactions;

import bg.sofia.uni.fmi.mjt.splitwise.notifications.ApprovedPaymentNotification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.OwedMoneyNotification;
import bg.sofia.uni.fmi.mjt.splitwise.payment.DoubleCalculator;
import bg.sofia.uni.fmi.mjt.splitwise.payment.Payment;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.Identifiable;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentData;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Group implements Identifiable, Serializable {
    private final String groupName;
    private final Set<User> members;
    private final Map<String, PaymentData> payments;

    public Group(String groupName, Set<User> members) {
        this.groupName = groupName;
        this.members = members;
        this.payments = new HashMap<>();

        for (User member : members) {
            Set<String> otherUsernames = new HashSet<>(members.size() - 1);
            for (User otherMember : members) {
                if (!otherMember.equals(member)) {
                    otherUsernames.add(otherMember.getUsername());
                }
            }
            payments.put(member.getUsername(), new PaymentData(otherUsernames));
        }
    }

    public Map<String, PaymentData> getPayments() {
        return payments;
    }

    public void split(String username, double amount, String reason) throws NoSuchUserInGroupException {
        if (checkPersonInGroup(username)) {
            throw new NoSuchUserInGroupException("This user is not in the group!");
        }
        double value = DoubleCalculator.divide(amount, members.size());
        PaymentData data = payments.get(username);
        for (User u : members) {
            if (!u.getUsername().equals(username)) {
                double newValue = data.getData().get(getUsername(u)) + value;
                data.add(getUsername(u), DoubleCalculator.format(newValue));
                List<Notification> list = new ArrayList<>();
                if (u.getGroupNotifications().get(groupName) != null) {
                    list.addAll(u.getGroupNotifications().get(groupName));
                }
                list.add(new OwedMoneyNotification(username, new Payment(DoubleCalculator.format(value), reason)));
                u.getGroupNotifications().put(groupName, list);
            }
        }
        for (User u : members) {
            if (!u.getUsername().equals(username)) {
                PaymentData data1 = payments.get(getUsername(u));
                double newValue = data1.getData().get(username) - value;
                data1.add(username, DoubleCalculator.format(newValue));
            }
        }
    }

    public void payed(User user, User userWhoGivesMoney, double amount) throws NoSuchUserInGroupException {
        if (checkPersonInGroup(user.getUsername()) || checkPersonInGroup(userWhoGivesMoney.getUsername())) {
            throw new NoSuchUserInGroupException("There is no such a user in this group!");
        }
        PaymentData data = payments.get(user.getUsername());
        Double money = data.getData().get(userWhoGivesMoney.getUsername());
        money -= amount;
        data.add(userWhoGivesMoney.getUsername(), DoubleCalculator.format(money));

        PaymentData data2 = payments.get(userWhoGivesMoney.getUsername());
        Double money2 = data2.getData().get(user.getUsername());
        money2 += amount;
        data2.add(user.getUsername(), DoubleCalculator.format(money2));

        List<Notification> list = new ArrayList<>();
        if (userWhoGivesMoney.getGroupNotifications().get(groupName) != null) {
            list.addAll(userWhoGivesMoney.getGroupNotifications().get(groupName));
        }
        list.add(new ApprovedPaymentNotification(user.getUsername(), DoubleCalculator.format(amount)));
        userWhoGivesMoney.getGroupNotifications().put(groupName, list);
    }

    private String getUsername(User user) {
        if (user == null || !members.contains(user)) {
            throw new IllegalArgumentException("Invalid user!");
        }
        return user.getUsername();
    }

    private boolean checkPersonInGroup(String name) {
        for (User u : members) {
            if (u.getUsername().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<User> getMembers() {
        return  members;
    }

    @Override
    public String getID() {
        return groupName;
    }
}
