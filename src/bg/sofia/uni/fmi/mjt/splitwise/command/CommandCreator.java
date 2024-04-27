package bg.sofia.uni.fmi.mjt.splitwise.command;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private static final String SEPARATOR = " ";

    public static Command newCommand(String clientInput) {
        if (clientInput == null || clientInput.isBlank()) {
            return null;
        }
        List<String> tokens = Arrays.asList(clientInput.trim().split(SEPARATOR));
        String[] args = tokens.subList(1, tokens.size())
                .stream()
                .map(String::trim)
                .toArray(String[]::new);

        return new Command(tokens.get(0).trim(), args);
    }
}
