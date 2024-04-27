package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class NoSuchUserInGroupException extends Exception {
    public NoSuchUserInGroupException(String message) {
        super(message);
    }

    public NoSuchUserInGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
