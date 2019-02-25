package data;

import java.io.IOException;

//T can be any object or class
public class DeserializeHandler<T> {
    DeserializeHandler() {

    }

    public T deserialize(Serialization fileHandler, String fileName) throws IOException {
        return (T) fileHandler.readFile(fileName);
    }

    DeserializeHandler<Object> objectDeserializeHandler = new DeserializeHandler<>();

    {
        //reads the company from the file and deserialize it to an object and saves in variable
        try {
            Object deserializedObject = objectDeserializeHandler.deserialize(new Serialization(), "company.txt");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
