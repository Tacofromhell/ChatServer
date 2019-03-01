package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SocketStreamHelper {

    SocketStreamHelper(){
    }
    public static void sendData (Object o, ObjectOutputStream out){
        try {
            out.reset();
            out.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object receiveData(ObjectInputStream in){
        try {
            return in.readObject();
        }
        // exception occurs on socket disconnect
        catch (IOException e) { }
        catch (ClassNotFoundException e) { }

        return null;
    }
}