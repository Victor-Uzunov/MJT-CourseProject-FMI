package bg.sofia.uni.fmi.mjt.splitwise.server.database;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDatabaseTest {

    private static final String TEST_USERS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\test\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\testUsers.txt";

    private UserDatabase userDatabase;

    @BeforeEach
    public void setUp() throws IOException {
        Path path = Path.of(TEST_USERS_FILE_NAME);
        Files.createFile(path);
        userDatabase = new UserDatabase(TEST_USERS_FILE_NAME);
    }

    @AfterEach
    public void close() throws IOException {
        Path path = Path.of(TEST_USERS_FILE_NAME);
        Files.deleteIfExists(path);
    }

    @Test
    public void testRegisterUser() throws UserAlreadyExitsException {
        userDatabase.register("alice", "password123");

        assertTrue(userDatabase.containsUser(new User("alice", "password123")),
                "User should be registered and contained in the database");
    }

    @Test
    public void testRegisterUserUserAlreadyExists() {
        assertThrows(UserAlreadyExitsException.class, () -> {
            userDatabase.register("alice", "password123");
            userDatabase.register("alice", "password123");
        }, "Should throw UserAlreadyExitsException when trying to register an existing user");
    }

    @Test
    public void testLoginUser() throws UserAlreadyExitsException, InvalidLoginDataException {
        userDatabase.register("alice", "password123");

        User loggedInUser = userDatabase.login("alice", "password123");

        assertNotNull(loggedInUser, "Login should be successful and return a valid user object");
    }

    @Test
    public void testLoginUserUserDoesNotExist() {
        assertThrows(InvalidLoginDataException.class, () -> {
            userDatabase.login("nonexistent", "password");
        }, "Should throw InvalidLoginDataException when trying to login with a nonexistent username");
    }

    @Test
    public void testLoginUserIncorrectPassword() throws UserAlreadyExitsException {
        userDatabase.register("alice", "password123");

        assertThrows(InvalidLoginDataException.class, () -> {
            userDatabase.login("alice", "wrongpassword");
        }, "Should throw InvalidLoginDataException when trying to login with an incorrect password");
    }

    @Test
    public void testContainsUser() throws UserAlreadyExitsException {
        userDatabase.register("alice", "password123");

        assertTrue(userDatabase.containsUser(new User("alice", "password123")),
                "Should return true for an existing user");
        assertFalse(userDatabase.containsUser(new User("bob", "password456")),
                "Should return false for a nonexistent user");
    }

    @Test
    public void testGetUser() throws UserAlreadyExitsException, UserDoesNotExistException {
        userDatabase.register("alice", "password123");

        User retrievedUser = userDatabase.getUser("alice");

        assertNotNull(retrievedUser, "Should return a valid user object for an existing user");
        assertEquals("alice", retrievedUser.getUsername(), "Retrieved user's username should match");
    }

    @Test
    public void testGetUserUserDoesNotExist() {
        assertThrows(UserDoesNotExistException.class, () -> {
            userDatabase.getUser("nonexistent");
        }, "Should throw UserDoesNotExistException when trying to retrieve a nonexistent user");
    }

    @Test
    public void testSave() throws UserAlreadyExitsException {
        userDatabase.register("alice", "password123");
        userDatabase.register("bob", "password456");

        userDatabase.save(TEST_USERS_FILE_NAME);

        UserDatabase newDatabase = new UserDatabase(TEST_USERS_FILE_NAME);

        assertTrue(newDatabase.containsUser(new User("alice", "password123")),
                "Saved database should contain registered users");
        assertTrue(newDatabase.containsUser(new User("bob", "password456")),
                "Saved database should contain registered users");
    }

    @Test
    public void testWithNull() {
        assertThrows(IllegalArgumentException.class, () -> userDatabase.containsUser(null),
                "Null reference is passed!");
    }
}

