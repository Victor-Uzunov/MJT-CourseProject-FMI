package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.notifications.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.payment.PaymentType;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.GroupDatabase;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.UserDatabase;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SplitWise {
    private final UserDatabase usersDB;
    private final GroupDatabase groupsDB;
    private final Map<SocketChannel, User> users;

    public SplitWise() {
        this.usersDB = new UserDatabase();
        this.users = new HashMap<>();
        this.groupsDB = new GroupDatabase();
    }

    public SplitWise(String usersFilePath, String groupsFilePath) {
        this.usersDB = new UserDatabase(usersFilePath);
        this.groupsDB = new GroupDatabase(groupsFilePath);
        this.users = new HashMap<>();
    }

    //TODO:socket

    public void register(SocketChannel s, String username, String password) throws UserAlreadyExitsException {
        usersDB.register(username, password);
    }

    public String login(SocketChannel sc, String username, String password) throws InvalidLoginDataException {
        User user = usersDB.login(username, password);
        users.put(sc, user);
        return getAllNotifications(user);
    }

    private User getLoggedInUser(SocketChannel sc) throws UserNotLoggedInException {
        User user = users.get(sc);
        if (user == null) {
            throw new UserNotLoggedInException("You are not logged in.");
        }
        return users.get(sc);
    }

    public void addFriend(SocketChannel sc, User user) throws UserNotLoggedInException {
        User currentUser = getLoggedInUser(sc);
        if (!currentUser.getFriends().containsKey(user.getUsername()) && usersDB.containsUser(user)) {
            currentUser.addFriend(user);
        }
    }

    public void createGroup(SocketChannel sc, String groupName, String[] usersNames) throws UserNotLoggedInException,
            UserDoesNotExistException, GroupAlreadyExistsException {
        User currentUser = getLoggedInUser(sc);
        if (currentUser.getGroups().contains(groupName)) {
            throw new GroupAlreadyExistsException("There is already a group with this name.");
        }
        Set<User> usersGroup = new HashSet<>();
        for (String name : usersNames) {
            usersGroup.add(getUser(name));
        }
        for (User u : usersGroup) {
            if (!usersDB.containsUser(u)) {
                throw new UserDoesNotExistException("You cannot create a group with user that does not exist.");
            }
        }
        currentUser.createGroup(groupName, usersGroup);
        usersGroup.add(currentUser);
        groupsDB.addGroup(groupName, usersGroup);
    }

    public void splitWithFriend(SocketChannel sc, String username, double amount, String reason)
            throws UserNotLoggedInException,
            NoSuchFriendException, UserDoesNotExistException {
        User currentUser = getLoggedInUser(sc);
        User friend = getUser(username);
        if (!usersDB.containsUser(friend)) {
            throw new UserDoesNotExistException("You cannot split money with user that does not exist.");
        }
        currentUser.splitWithFriend(friend, amount, reason);
    }

    public void splitWithGroup(SocketChannel sc, String groupName, double amount, String reason)
            throws UserNotLoggedInException, NoSuchGroupException, NoSuchUserInGroupException {
        User currentUser = getLoggedInUser(sc);
        if (!groupsDB.containsGroup(groupName) || !currentUser.checkIsInGroup(groupName)) {
            throw new NoSuchGroupException("There is no such a group.");
        }
        Group group = groupsDB.getGroup(groupName);
        group.split(currentUser.getUsername(), amount, reason);
        currentUser.getPaymentHistory().add(new PaymentHistory(amount, groupName, reason, PaymentType.GROUP_PAYMENT));
    }

    public String getStatus(SocketChannel sc) throws UserNotLoggedInException {
        User currentUser = getLoggedInUser(sc);
        StringBuilder result = new StringBuilder();
        Map<String, Group> userGroups = new HashMap<>();
        Set<String> groupNames = currentUser.getGroups();
        for (String name : groupNames) {
            userGroups.put(name, groupsDB.getGroup(name));
        }
        result.append(currentUser.getStatus(userGroups));
        return result.toString();
    }

    public void payed(SocketChannel sc, String username, double amount) throws UserNotLoggedInException,
            NoSuchFriendException, UserDoesNotExistException {
        User currentUser = getLoggedInUser(sc);
        User other = getUser(username);
        currentUser.payed(amount, other);
    }

    public void payedInGroup(SocketChannel sc, String groupName, String username, double amount)
            throws UserNotLoggedInException,
            NoSuchGroupException, NoSuchUserInGroupException, UserDoesNotExistException {
        User user = getUser(username);
        if (sc == null || groupName == null || user == null) {
            throw new IllegalArgumentException("Null reference is passed.");
        }

        User currentUser = getLoggedInUser(sc);
        if (!currentUser.checkIsInGroup(groupName) || !groupsDB.containsGroup(groupName)) {
            throw new NoSuchGroupException("There is no such a group.");
        }

        Group group = groupsDB.getGroup(groupName);
        group.payed(currentUser, user, amount);
    }

    public void exit(SocketChannel sc) {
        users.remove(sc);
        usersDB.save();
        groupsDB.save();
    }

    public void logout(SocketChannel sc) {
        users.remove(sc);
    }

    public User getUser(String userName) throws UserDoesNotExistException {
        return usersDB.getUser(userName);
    }

    public Map<SocketChannel, User> getUsers() {
        return users;
    }

    public Group getGroup(String groupName) {
        return groupsDB.getGroup(groupName);
    }

    private String getAllNotifications(User user) {
        StringBuilder result = new StringBuilder();
        result.append("***** Notifications *****").append(System.lineSeparator());
        if (user.getGroupNotifications().isEmpty() && user.getFriendNotifications().isEmpty()) {
            return result.append("There is no notifications to show").append(System.lineSeparator()).toString();
        } else {
            result.append("Friends:").append(System.lineSeparator());
            for (Notification notification : user.getFriendNotifications()) {
                result.append(notification.inform()).append(System.lineSeparator());
            }
            user.getFriendNotifications().clear();

            result.append("Groups:").append(System.lineSeparator());
            for (Map.Entry<String, List<Notification>> entry : user.getGroupNotifications().entrySet()) {
                String key = entry.getKey();
                result.append(key).append(":").append(System.lineSeparator());
                List<Notification> notifications = entry.getValue();
                for (Notification notification : notifications) {
                    result.append(notification.inform()).append(System.lineSeparator());
                }
            }
            user.getGroupNotifications().clear();
        }
        return result.toString();
    }

    public void save() {
        usersDB.save();
        groupsDB.save();
    }

    public String printHistory(SocketChannel key) throws UserNotLoggedInException {
        User user = getLoggedInUser(key);
        StringBuilder result = new StringBuilder();

        if (user.getPaymentHistory().isEmpty()) {
            return "There is no payment history for your profile!";
        }
        result.append("Payment history:").append(System.lineSeparator());
        for (PaymentHistory paymentHistory : user.getPaymentHistory()) {
            result.append(paymentHistory.toString()).append(System.lineSeparator());
        }
        return result.toString();
    }
}
