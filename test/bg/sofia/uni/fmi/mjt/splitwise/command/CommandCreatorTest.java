package bg.sofia.uni.fmi.mjt.splitwise.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class CommandCreatorTest {

    @Test
    public void testNewCommandValidInput() {
        String input = "command arg1 arg2 arg3";
        Command command = CommandCreator.newCommand(input);

        assertNotNull(command, "Command should not be null");
        assertEquals("command", command.command(), "Command name does not match");
        assertArrayEquals(new String[]{"arg1", "arg2", "arg3"}, command.arguments(), "Arguments do not match");
    }

    @Test
    public void testNewCommandEmptyInput() {
        String input = "";
        Command command = CommandCreator.newCommand(input);

        assertNull(command, "Command should be null for empty input");
    }

    @Test
    public void testNewCommandOnlyCommand() {
        String input = "command";
        Command command = CommandCreator.newCommand(input);

        assertNotNull(command, "Command should not be null");
        assertEquals("command", command.command(), "Command name does not match");
        assertEquals(0, command.arguments().length, "No arguments expected");
    }

    @Test
    public void testNewCommandLeadingSpaces() {
        String input = "   command arg1 arg2";
        Command command = CommandCreator.newCommand(input);

        assertNotNull(command, "Command should not be null");
        assertEquals("command", command.command(), "Command name does not match");
        assertArrayEquals(new String[]{"arg1", "arg2"}, command.arguments(), "Arguments do not match");
    }

    @Test
    public void testNewCommandTrailingSpaces() {
        String input = "command arg1 arg2    ";
        Command command = CommandCreator.newCommand(input);

        assertNotNull(command, "Command should not be null");
        assertEquals("command", command.command(), "Command name does not match");
        assertArrayEquals(new String[]{"arg1", "arg2"}, command.arguments(), "Arguments do not match");
    }

    @Test
    public void testNewCommandMultipleSpacesBetweenArgs() {
        String input = "command  arg1   arg2";
        Command command = CommandCreator.newCommand(input);

        assertNotNull(command, "Command should not be null");
        assertEquals("command", command.command(), "Command name does not match");
        assertEquals("arg1", command.arguments()[1], "Arguments do not match");
    }
}

