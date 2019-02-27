package storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class StorageHandler<T> {
    public StorageHandler() {

    }

    public T getFromStorage(String fileName) throws IOException {
        return (T) readFile(fileName);
    }

    public void saveToStorage(Object object, String fileName) {
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
            System.out.println("Serialized data is saved in " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private Object readFile(String fileName) throws IOException {
        //Opens a stream to the filesystem
        //create an object stream (want to get an object)
        Path path = Paths.get("src/storage/" + fileName);
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            //Initialize the inputStream, and ask for the specific file
            //Reads the file with object and return it
            return in.readObject();
        } catch (Exception i) {
            i.printStackTrace();
        }
        return null;
    }
}
