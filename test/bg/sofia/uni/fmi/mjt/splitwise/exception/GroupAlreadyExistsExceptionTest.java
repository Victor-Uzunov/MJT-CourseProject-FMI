package bg.sofia.uni.fmi.mjt.splitwise.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GroupAlreadyExistsExceptionTest {
    @Test
    public void testConstructorWithMessage() {
        String message = "Invalid login data";
        GroupAlreadyExistsException exception = new GroupAlreadyExistsException(message);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertNull(exception.getCause(), "Cause should be null");
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        String message = "Invalid login data";
        Throwable cause = new Throwable("Underlying cause");
        GroupAlreadyExistsException exception = new GroupAlreadyExistsException(message, cause);
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
}
