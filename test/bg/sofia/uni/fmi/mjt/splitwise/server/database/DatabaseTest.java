package bg.sofia.uni.fmi.mjt.splitwise.server.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class DatabaseTest {
    private static final String TEST_FILENAME = "C:\\Users\\User\\Desktop\\JavaProjects\\CourseProject\\test\\bg\\sofia\\uni\\fmi\\mjt\\splitwise\\server\\database\\testDatabase.txt";

    private static class TestDatabase extends Database<TestObject> {
        public TestDatabase(String filename) {
            super(filename);
        }
    }

    private record TestObject(String id) implements Identifiable, Serializable {

        @Override
        public String getID() {
            return id;
        }
    }


    @Test
    public void testConstructorFileDoesNotExist() throws IOException {
        Path path = Path.of(TEST_FILENAME);
        Files.createFile(path);

        TestDatabase database = new TestDatabase(TEST_FILENAME);
        Map<String, TestObject> expectedObjects = new HashMap<>();

        assertEquals(expectedObjects, database.objects, "Database should be initialized with an empty map");
        Files.deleteIfExists(path);
    }

    @Test
    public void testConstructorFileIsEmpty() throws IOException {
        Path path = Path.of(TEST_FILENAME);
        Files.createFile(path);

        TestDatabase database = new TestDatabase(TEST_FILENAME);
        Map<String, TestObject> expectedObjects = new HashMap<>();

        assertEquals(expectedObjects, database.objects, "Database should be initialized with an empty map");
        Files.deleteIfExists(path);
    }

    @Test
    public void testSave() throws IOException {

        Path path = Path.of(TEST_FILENAME);
        Files.createFile(path);
        TestDatabase database = new TestDatabase(TEST_FILENAME);
        Map<String, TestObject> expectedObjects = new HashMap<>();
        expectedObjects.put("1", new TestObject("1"));
        expectedObjects.put("2", new TestObject("2"));
        expectedObjects.put("3", new TestObject("3"));
        database.objects.putAll(expectedObjects);

        database.save(TEST_FILENAME);

        TestDatabase newDatabase = new TestDatabase(TEST_FILENAME);
        assertEquals(expectedObjects, newDatabase.objects, "Saved objects should be loaded correctly");
        Files.deleteIfExists(path);
    }

    @Test
    public void testConstructorFileNotFoundException() {
        assertThrows(IllegalStateException.class, () -> new TestDatabase("nonexistentfile.txt"),
                "Constructor should throw IllegalStateException if file does not exist");
    }

    @Test
    public void testConstructorReadIOException() {
        assertThrows(IllegalStateException.class, () -> new TestDatabase("unreadablefile.txt"),
                "Constructor should throw IllegalStateException if there's a problem reading from the file");
    }

    @Test
    public void testSaveWriteIOException() throws IOException {
        Path path = Path.of(TEST_FILENAME);
        Files.createFile(path);
        TestDatabase database = new TestDatabase(TEST_FILENAME);
        database.objects.put("1", new TestObject("1"));
        database.objects.put("2", new TestObject("2"));

        assertThrows(IllegalStateException.class, () -> database.save("/invalid/path/to/file"),
                "Save method should throw IllegalStateException if there's a problem writing to the file");
        Files.deleteIfExists(path);
    }

    @Test
    public void testConstructorReadClassNotFoundException() throws IOException {
        Path path = Path.of(TEST_FILENAME);
        Files.write(path, new byte[]{1, 2, 3});

        assertThrows(IllegalStateException.class, () -> new TestDatabase(TEST_FILENAME),
                "Constructor should throw IllegalStateException with nested ClassNotFoundException if there's a problem deserializing objects from the file");

        Files.deleteIfExists(path);
    }

}

