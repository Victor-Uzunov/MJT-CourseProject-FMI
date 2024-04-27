package bg.sofia.uni.fmi.mjt.splitwise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserAlreadyExistsExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Invalid login data";
        UserAlreadyExitsException exception = new UserAlreadyExitsException(message);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Invalid login data";
        Throwable cause = new Throwable("Underlying cause");
        UserAlreadyExitsException exception = new UserAlreadyExitsException(message, cause);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
}





