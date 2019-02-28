package storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class StorageHandler<T> {
    public StorageHandler() {

    }

    public T getFromStorage(String fileName) {
        return (T) readFile(fileName);
    }

    public static void saveToStorage(Object object, String fileName) {
        Path path = Paths.get("src/storage/" + fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING)
        )) {
            /*FileOutPutStream is a class, which handles the stream between
            the project and the filesystem in the computer.
             */
            /*A stream that can handle objects to be send to the filesystem,
             * gets the stream from the FileOutputStream */

            //Sending the serialized object to the filesystem and creating a new file
            out.writeObject(object);
            System.out.println("Serialized data is saved in " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static Object readFile(String fileName) {
        //Opens a stream to the filesystem
        //create an object stream (want to get an object)
        Path path = Paths.get("src/storage/" + fileName);
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            //Initialize the inputStream, and ask for the specific file
            //Reads the file with object and return it
            return in.readObject();
        } catch (Exception i) {
        }
        return new ConcurrentHashMap<>();
    }

    public static void resetStorage(Object object, String fileName) {
        Path path = Paths.get("src/storage/" + fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(path, StandardOpenOption.CREATE)
        )) {
            /*FileOutPutStream is a class, which handles the stream between
            the project and the filesystem in the computer.
             */
            /*A stream that can handle objects to be send to the filesystem,
             * gets the stream from the FileOutputStream */

            //Sending the serialized object to the filesystem and creating a new file
            out.writeObject(object);

        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
