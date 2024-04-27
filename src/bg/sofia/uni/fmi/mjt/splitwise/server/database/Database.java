package bg.sofia.uni.fmi.mjt.splitwise.server.database;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class Database<T extends Identifiable & Serializable> {
    protected Map<String, T> objects;

    public Database(String filename) {
        Path file = Path.of(filename);
        objects = new HashMap<>();

        try {
            if (Files.size(file) == 0) {
                return;
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }

        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(file))) {
            Object obj;
            while (true) {
                try {
                    obj = objectInputStream.readObject();
                    if (obj == null) {
                        break;
                    }
                    T object = (T) obj;
                    objects.put(object.getID(), object);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("The file does not exist", e);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String fileName) {
        Path file = Path.of(fileName);
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file))) {
            for (T object : objects.values()) {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file", e);
        }
    }
}
