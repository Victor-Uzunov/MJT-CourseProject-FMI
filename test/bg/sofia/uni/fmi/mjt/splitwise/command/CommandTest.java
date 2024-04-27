package bg.sofia.uni.fmi.mjt.splitwise.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CommandTest {

    @Test
    public void testCommandCreation() {
        String commandString = "add";
        String[] arguments = {"Alice", "Bob", "100"};
        Command command = new Command(commandString, arguments);

        assertEquals(commandString, command.command(), "The command must be the same!");
        assertEquals(arguments.length, command.arguments().length);
        for (int i = 0; i < arguments.length; i++) {
            assertEquals(arguments[i], command.arguments()[i], "The arguments must be the same!");
        }
    }

    @Test
    public void testCommandEquality() {
        String commandString = "add";
        String[] arguments1 = {"Alice", "Bob", "100"};
        String[] arguments2 = {"Alice", "Bob", "100"};
        Command command1 = new Command(commandString, arguments1);
        Command command2 = new Command(commandString, arguments2);

        assertEquals(command1.command(), command2.command(), "The command must be the same!");
        assertEquals(command1.arguments()[0], command2.arguments()[0], "The arguments must be the same!");
        assertEquals(command1.arguments()[1], command2.arguments()[1], "The arguments must be the same!");
        assertEquals(command1.arguments()[2], command2.arguments()[2], "The arguments must be the same!");
    }

    @Test
    public void testCommandInequality() {
        String commandString = "add";
        String[] arguments1 = {"Alice", "Bob", "100"};
        String[] arguments2 = {"Alice", "Bob", "200"};
        Command command1 = new Command(commandString, arguments1);
        Command command2 = new Command(commandString, arguments2);

        assertNotEquals(command1, command2, "The commands must be different!");
    }
}
