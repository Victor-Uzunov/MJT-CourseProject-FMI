package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class GroupAlreadyExistsException extends Exception {
    public GroupAlreadyExistsException(String message) {
        super(message);
    }

    public GroupAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
