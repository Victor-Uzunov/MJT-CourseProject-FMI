package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class InvalidLoginDataException extends Exception {
    public InvalidLoginDataException(String message) {
        super(message);
    }

    public InvalidLoginDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
