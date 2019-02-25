package storage;

import java.io.*;

public class StorageHandler<T> {
    public StorageHandler() {

    }

    public T getFromStorage(String fileName) throws IOException {
        return (T) readFile(fileName);
    }

    public void saveToStorage(Object object, String fileName) {
        try {
            /*FileOutPutStream is a class, which handles the stream between
            the project and the filesystem in the computer.
             */
            FileOutputStream fileOut =
                    new FileOutputStream(fileName);
            /*A stream that can handle objects to be send to the filesystem,
             * gets the stream from the FileOutputStream */
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            //Sending the serialized object to the filesystem and creating a new file
            out.writeObject(object);
            //Closing the stream (connection from filesystem to project) on both sides
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private Object readFile(String fileName) throws IOException {
        //Opens a stream to the filesystem
        FileInputStream fileIn = null;
        //create an object stream (want to get an object)
        ObjectInputStream objectInputStream = null;
        try {
            //Initialize the inputStream, and ask for the specific file
            fileIn = new FileInputStream(fileName);
            objectInputStream = new ObjectInputStream(fileIn);
            //Reads the file with object and return it
            return objectInputStream.readObject();
        } catch (Exception i) {
            i.printStackTrace();
        } finally {
            //Closing the streams (always do that)
            objectInputStream.close();
            fileIn.close();
        }
        return null;
    }
}
