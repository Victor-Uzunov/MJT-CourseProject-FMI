package bg.sofia.uni.fmi.mjt.splitwise.exception;

public class NoSuchGroupException extends Exception {
    public NoSuchGroupException(String message) {
        super(message);
    }

    public NoSuchGroupException(String message, Throwable cause) {
        super(message, cause);
    }
}
