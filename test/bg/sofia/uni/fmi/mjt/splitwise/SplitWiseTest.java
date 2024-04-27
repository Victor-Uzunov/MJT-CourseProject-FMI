package bg.sofia.uni.fmi.mjt.splitwise;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

public class SplitWiseTest {

    private SplitWise splitWise;
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
    public void testRegister() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "alice", "password");
        assertDoesNotThrow(() -> splitWise.login(socketChannel, "alice", "password"), "Should register and login successfully");
        String expectedResult = "***** Notifications *****" + System.lineSeparator() + "There is no notifications to show" + System.lineSeparator();
        assertEquals(expectedResult, splitWise.login(socketChannel, "alice", "password"), "Should login successfully");
    }

    @Test
    public void testLogin() throws UserAlreadyExitsException, InvalidLoginDataException {
        splitWise.register(socketChannel, "rico", "password");
        String expectedResult = "***** Notifications *****" + System.lineSeparator() + "There is no notifications to show" + System.lineSeparator();
        assertEquals(expectedResult, splitWise.login(socketChannel, "rico", "password"), "Should login successfully");
    }

    @Test
    public void testAddFriend() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, UserDoesNotExistException {
        splitWise.register(socketChannel, "steven", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "steven", "password");
        splitWise.addFriend(socketChannel, new User("bob", "password"));
        assertTrue(splitWise.getUser("steven").getFriends().containsKey("bob"), "Should add friend successfully");
    }

    @Test
    public void testCreateGroup() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, UserDoesNotExistException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"bob"});
        assertTrue(splitWise.getUser("alice").checkIsInGroup("group1"), "Should create group successfully");
        assertTrue(splitWise.getUser("bob").checkIsInGroup("group1"), "Should create group successfully");
    }

    @Test
    public void testSplitWithFriend() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, NoSuchFriendException, UserDoesNotExistException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, splitWise.getUser("bob"));
        splitWise.splitWithFriend(socketChannel, "bob", 20.0, "lunch");
        assertEquals(10.0, splitWise.getUser("alice").getFriends().get("bob"), "Should split with friend successfully");
    }

    @Test
    public void testSplitWithGroup() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, NoSuchGroupException, NoSuchUserInGroupException, UserDoesNotExistException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"bob", "charlie"});
        splitWise.splitWithGroup(socketChannel, "group1", 30.0, "dinner");
        Group group = splitWise.getGroup("group1");
        assertEquals(10.0, group.getPayments().get("alice").getData().get("bob"), "Should split with group successfully");
        assertEquals(10.0, group.getPayments().get("alice").getData().get("charlie"), "Should split with group successfully");
    }

    @Test
    public void testGetStatus() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, NoSuchGroupException, NoSuchUserInGroupException, UserDoesNotExistException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("***** Friends *****" + System.lineSeparator() + System.lineSeparator()
                        + "***** Groups *****" + System.lineSeparator()
                , splitWise.getStatus(socketChannel), "Should get status successfully");
    }

    @Test
    public void testPayed() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, NoSuchFriendException, UserDoesNotExistException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, splitWise.getUser("bob"));
        splitWise.payed(socketChannel, "bob", 10.0);
        assertEquals(-10.0, splitWise.getUser("alice").getFriends().get("bob"), "Should payed successfully");
    }

    @Test
    public void testPayedInGroup() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, NoSuchGroupException, NoSuchUserInGroupException, UserDoesNotExistException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "bob", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"alice", "charlie"});
        splitWise.splitWithGroup(socketChannel, "group1", 9.0, "rent");
        Group group = splitWise.getGroup("group1");
        assertEquals(3, group.getPayments().get("bob").getData().get("alice"), "Should payed in group successfully");
        assertEquals(3, group.getPayments().get("bob").getData().get("charlie"), "Should payed in group successfully");
    }

    @Test
    public void testLogout() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.logout(socketChannel);
        assertFalse(splitWise.getUsers().containsKey(socketChannel), "Should logout successfully");
    }


    @Test
    public void testGetUser() throws UserAlreadyExitsException, InvalidLoginDataException, UserDoesNotExistException {
        splitWise.register(socketChannel, "alice", "password");
        assertNotNull(splitWise.getUser("alice"), "Should get user successfully");
    }

    @Test
    public void testGetStatusWithNotifications() throws UserAlreadyExitsException, InvalidLoginDataException, UserNotLoggedInException, UserDoesNotExistException, NoSuchUserInGroupException, NoSuchGroupException, NoSuchFriendException, GroupAlreadyExistsException {
        splitWise.register(socketChannel, "alice", "password");
        splitWise.register(socketChannel, "bob", "password");
        splitWise.register(socketChannel, "charlie", "password");
        splitWise.login(socketChannel, "alice", "password");
        splitWise.addFriend(socketChannel, new User("bob", "password"));
        splitWise.logout(socketChannel);
        splitWise.login(socketChannel, "bob", "password");
        splitWise.createGroup(socketChannel, "group1", new String[]{"alice", "charlie"});
        splitWise.splitWithGroup(socketChannel, "group1", 9.0, "rent");
        splitWise.addFriend(socketChannel, new User("alice", "password"));
        splitWise.splitWithFriend(socketChannel, "alice", 10, "rent");
        splitWise.logout(socketChannel);
        splitWise.login(socketChannel, "alice", "password");
        assertEquals("***** Friends *****" + System.lineSeparator() +
                        "bob: You owe 5.0 lv" + System.lineSeparator() + System.lineSeparator()
                        + "***** Groups *****" + System.lineSeparator() + "group1"
                        + System.lineSeparator() + "bob: You owe 3.0 lv" + System.lineSeparator()
                , splitWise.getStatus(socketChannel), "Should get status successfully");
    }
}

