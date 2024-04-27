package bg.sofia.uni.fmi.mjt.splitwise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserNotLoggedInExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        String message = "Invalid login data";
        UserNotLoggedInException exception = new UserNotLoggedInException(message);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Invalid login data";
        Throwable cause = new Throwable("Underlying cause");
        UserNotLoggedInException exception = new UserNotLoggedInException(message, cause);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
}





