package bg.sofia.uni.fmi.mjt.splitwise.exception.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

public class ExceptionLogger {
    private static final String FILE_LOGGER = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject" +
            "\\src\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\exception\\logger\\loggerExceptions.txt";

    public static void logException(Exception exception) {
        try (FileWriter logWriter = new FileWriter(FILE_LOGGER, true)) {
            logWriter.append(exception.getMessage()).append(System.lineSeparator())
                    .append(Arrays.toString(exception.getStackTrace())).append(System.lineSeparator());
        } catch (IOException exception1) {
            throw new UncheckedIOException("A problem occurred with writing to file", exception1);
        }
    }
}
