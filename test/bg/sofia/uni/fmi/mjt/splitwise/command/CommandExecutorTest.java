package bg.sofia.uni.fmi.mjt.splitwise.command;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.SplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserNotLoggedInException;

public class CommandExecutorTest {

    private SplitWise splitWise;
    private CommandExecutor executor;
    private SocketChannel socketChannel;
    private static final String TEST_GROUPS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\test\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\testGroups.txt";
    private static final String TEST_USERS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\test\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\testUsers.txt";

    @BeforeEach
    public void setUp() throws IOException {
        Path path = Path.of(TEST_USERS_FILE_NAME);
        Files.createFile(path);

        Path path1 = Path.of(TEST_GROUPS_FILE_NAME);
        Files.createFile(path1);

        splitWise = new SplitWise(TEST_USERS_FILE_NAME, TEST_GROUPS_FILE_NAME);
        executor = new CommandExecutor(splitWise);
        socketChannel = SocketChannel.open();
    }

    @AfterEach
    public void clear() throws IOException {
        Path path = Path.of(TEST_USERS_FILE_NAME);
        Files.deleteIfExists(path);

        Path path1 = Path.of(TEST_GROUPS_FILE_NAME);
        Files.deleteIfExists(path1);
    }

    @Test
    public void testRegister() {
        assertEquals("User with username alice registered", executor.execute(socketChannel, CommandCreator.newCommand("register alice password")), "The command is not correct!");
    }

    @Test
    public void testLogin() throws UserAlreadyExitsException {
        splitWise.register(socketChannel, "alice", "password");
        assertEquals("Successfully logged in!\n" +
                "***** Notifications *****" + System.lineSeparator() +
                "There is no notifications to show" + System.lineSeparator()
                , executor.execute(socketChannel,CommandCreator.newCommand("login alice password")), "The command is not correct!");
    }

    @Test
    public void testAddFriend() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("bob is successfully added as a friend!", executor.execute(socketChannel, CommandCreator.newCommand("add-friend bob")), "The command is not correct!");
    }

    @Test
    public void testCreateGroup() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("The group with name group1 is successfully created", executor.execute(socketChannel, CommandCreator.newCommand("create-group group1 bob")), "The command is not correct!");
    }

    @Test
    public void testSplit() throws UserAlreadyExitsException, InvalidLoginDataException, UserDoesNotExistException, UserNotLoggedInException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, splitWise.getUser("bob"));
        assertEquals("Successfully split money with bob", executor.execute(socketChannel, CommandCreator.newCommand("split 10 bob lunch")), "The command is not correct!");
    }

    @Test
    public void testSplitGroup() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, UserDoesNotExistException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"bob", "charlie"});
        assertEquals("Successfully split with group group1", executor.execute(socketChannel, CommandCreator.newCommand("split-group 30 group1 dinner")), "The command is not correct!");
    }

    @Test
    public void testGetStatus() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("***** Friends *****" + System.lineSeparator() +
                 System.lineSeparator() + "***** Groups *****" + System.lineSeparator()
                , executor.execute(socketChannel, CommandCreator.newCommand("get-status")), "The command is not correct!");
    }

    @Test
    public void testPayed() throws UserAlreadyExitsException, InvalidLoginDataException, UserDoesNotExistException, UserNotLoggedInException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, splitWise.getUser("bob"));
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("bob has payed successfully", executor.execute(socketChannel, CommandCreator.newCommand("payed 10 bob")), "The command is not correct!");
    }

    @Test
    public void testPayedGroup() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, UserDoesNotExistException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"bob", "charlie"});
        assertEquals("charlie has payed successfully in the group", executor.execute(socketChannel, CommandCreator.newCommand("payed-in-group 10 group1 charlie")), "The command is not correct!");
    }

    @Test
    public void testInvalidLogout() {
        assertEquals("Unknown command", executor.execute(socketChannel, CommandCreator.newCommand("logout")), "The command is not correct!");
    }

    @Test
    public void testHelp() {
        assertEquals("register <username> <password>" + System.lineSeparator() +
                "login <username> <password" + System.lineSeparator() +
                "add-friend <username>" + System.lineSeparator() +
                "create-group <groupname> <username> <username> ... <username>" + System.lineSeparator() +
                "split <amount> <username> <reason>" + System.lineSeparator() +
                "split-group <amount> <groupname> <username>" + System.lineSeparator() +
                "get-status" + System.lineSeparator() +
                "payed <amount> <username>" + System.lineSeparator() +
                "payed-in-group <amount> <groupname> <username>" + System.lineSeparator() +
                        "logout" + System.lineSeparator() +
                "history" + System.lineSeparator() +
                        "enter quit to disconnect from the server" + System.lineSeparator(),
                executor.execute(socketChannel, CommandCreator.newCommand("HELP")), "The command is not correct!");
    }

    @Test
    public void testUnknownCommand() {
        assertEquals("Unknown command", executor.execute(socketChannel, CommandCreator.newCommand("random")), "The command is not correct!");
    }


    @Test
    public void testValidLogout() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "charlie", "password");

        assertEquals("Successfully logged out", executor.execute(socketChannel, CommandCreator.newCommand("logout")),
                "The result is not correct!");
    }

    @Test
    public void testValidHelp() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "charlie", "password");

        assertEquals("register <username> <password>" + System.lineSeparator() +
                        "login <username> <password" + System.lineSeparator() +
                        "add-friend <username>" + System.lineSeparator() +
                        "create-group <groupname> <username> <username> ... <username>" + System.lineSeparator() +
                        "split <amount> <username> <reason>" + System.lineSeparator() +
                        "split-group <amount> <groupname> <username>" + System.lineSeparator() +
                        "get-status" + System.lineSeparator() +
                        "payed <amount> <username>" + System.lineSeparator() +
                        "payed-in-group <amount> <groupname> <username>" + System.lineSeparator() +
                        "logout" + System.lineSeparator() +
                        "history" + System.lineSeparator() +
                        "enter quit to disconnect from the server" + System.lineSeparator(),
                executor.execute(socketChannel, CommandCreator.newCommand("HELP")), "The command is not correct!");
    }

    @Test
    public void testUnknownCommandWhileLoggedIn() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "charlie", "password");

        assertEquals("Unknown command", executor.execute(socketChannel, CommandCreator.newCommand("random")), "The command is not correct!");
    }

    @Test
    public void testIncorrectCommandFormatBeforeLogin() {
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("register")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("login")), "The command has incorrect format");
    }

    @Test
    public void testIncorrectCommandFormatAfterLogin() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "charlie", "password");

        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("add-friend")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("create-group")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("payed")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("split")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("split-group")), "The command has incorrect format");
        assertEquals("Incorrect command format", executor.execute(socketChannel, CommandCreator.newCommand("payed-in-group")), "The command has incorrect format");
    }

    @Test
    public void testEmptyPaymentHistory() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "charlie", "password");
        assertEquals("There is no payment history for your profile!", executor.execute(socketChannel, CommandCreator.newCommand("history")), "Invalid payment history");
    }

    @Test
    public void testPaymentHistory() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, GroupAlreadyExistsException, UserDoesNotExistException, NoSuchUserInGroupException, NoSuchGroupException, NoSuchFriendException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");

        splitWise.login(socketChannel, "bob", "password");
        splitWise.addFriend(socketChannel, new User("alice", "password"));
        splitWise.logout(socketChannel);

        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, new User("bob", "password"));
        splitWise.createGroup(socketChannel, "group1", new String[]{"bob", "charlie"});
        splitWise.splitWithGroup(socketChannel, "group1", 10, "rent");
        splitWise.splitWithFriend(socketChannel, "bob", 10, "food");

        assertEquals("Payment history:" + System.lineSeparator() +
                "Group payment - group1, Reason for payment: rent, Amount: 10,00" + System.lineSeparator() +
                "Friend Payment - bob, Reason for payment: food, Amount: 10,00" + System.lineSeparator(),
                executor.execute(socketChannel, CommandCreator.newCommand("history")), "Invalid payment history");
    }
}

