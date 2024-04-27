package bg.sofia.uni.fmi.mjt.splitwise.server.database;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.interactions.Group;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

public class GroupDatabaseTest {

    private static final String TEST_GROUPS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\test\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\testGroups.txt";

    private GroupDatabase groupDatabase;

    @BeforeEach
    public void setUp() throws IOException {
        Path path = Path.of(TEST_GROUPS_FILE_NAME);
        Files.createFile(path);
        groupDatabase = new GroupDatabase(TEST_GROUPS_FILE_NAME);
    }

    @AfterEach
    public void close() throws IOException {
        Path path = Path.of(TEST_GROUPS_FILE_NAME);
        Files.deleteIfExists(path);
    }

    @Test
    public void testAddGroup() {
        String groupName = "Group1";
        Set<User> users = new HashSet<>();
        users.add(new User("user1", "pass1"));
        users.add(new User("user2", "pass2"));

        groupDatabase.addGroup(groupName, users);

        assertTrue(groupDatabase.containsGroup(groupName),
                "Group should be added to the database");
    }

    @Test
    public void testContainsGroup() {
        String groupName = "Group1";
        Set<User> users = new HashSet<>();
        users.add(new User("user1", "pass1"));
        users.add(new User("user2", "pass2"));

        groupDatabase.addGroup(groupName, users);

        assertTrue(groupDatabase.containsGroup(groupName),
                "Group should exist in the database");
        assertFalse(groupDatabase.containsGroup("NonexistentGroup"),
                "Nonexistent group should not exist in the database");
    }

    @Test
    public void testGetGroup() {
        String groupName = "Group1";
        Set<User> users = new HashSet<>();
        users.add(new User("user1", "pass1"));
        users.add(new User("user2", "pass2"));

        groupDatabase.addGroup(groupName, users);

        Group retrievedGroup = groupDatabase.getGroup(groupName);

        assertNotNull(retrievedGroup, "Retrieved group should not be null");
        assertEquals(groupName, retrievedGroup.getGroupName(), "Retrieved group's name should match");
        assertIterableEquals(users, retrievedGroup.getMembers(), "Retrieved group's users should match");
    }

    @Test
    public void testSave() {
        String groupName = "Group1";
        Set<User> users = new HashSet<>();
        users.add(new User("user1", "pass1"));
        users.add(new User("user2", "pass2"));

        groupDatabase.addGroup(groupName, users);

        groupDatabase.save(TEST_GROUPS_FILE_NAME);

        GroupDatabase newDatabase = new GroupDatabase(TEST_GROUPS_FILE_NAME);
        assertTrue(newDatabase.containsGroup(groupName),
                "Saved database should contain the added group");
    }

    @Test
    public void testWithNull() {
        assertThrows(IllegalArgumentException.class, () -> groupDatabase.containsGroup(null),
                "There must not be the same group in the database");
    }
}
