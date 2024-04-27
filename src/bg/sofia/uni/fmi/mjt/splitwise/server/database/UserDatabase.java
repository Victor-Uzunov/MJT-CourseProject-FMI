package bg.sofia.uni.fmi.mjt.splitwise.server.database;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.user.User;

public class UserDatabase extends Database<User> {
    public static final String USERS_FILE_NAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\src\\bg\\"
        + "sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\users.txt";

    public UserDatabase() {
        super(USERS_FILE_NAME);
    }

    public UserDatabase(String filename) {
        super(filename);
    }

    public void register(String username, String password) throws UserAlreadyExitsException {
        if (super.objects.containsKey(username)) {
            throw new UserAlreadyExitsException("User with the same username: " + username + " already exists!");
        }
        User user = new User(username, password);
        super.objects.put(username, user);
    }

    public User login(String username, String password) throws InvalidLoginDataException {
        if (!super.objects.containsKey(username)) {
            throw new InvalidLoginDataException("User " + username + " does not exist!");
        }

        User user = super.objects.get(username);
        if (!user.equalsPassword(password)) {
            throw new InvalidLoginDataException("Incorrect password!");
        }

        return user;
    }

    public boolean containsUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return super.objects.containsKey(user.getUsername());
    }

    public User getUser(String username) throws UserDoesNotExistException {
        if (!super.objects.containsKey(username)) {
            throw new UserDoesNotExistException("User " + username + " does not exist!");
        }
        return super.objects.get(username);
    }

    public void save() {
        super.save(USERS_FILE_NAME);
    }
}
