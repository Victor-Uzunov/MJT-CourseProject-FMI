package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class UserAlreadyExitsException extends Exception {
    public UserAlreadyExitsException(String message) {
        super(message);
    }

    public UserAlreadyExitsException(String message, Throwable cause) {
        super(message, cause);
    }
}
