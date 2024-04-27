package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class NoSuchFriendException extends Exception {
    public NoSuchFriendException(String message) {
        super(message);
    }

    public NoSuchFriendException(String message, Throwable cause) {
        super(message, cause);
    }
}
