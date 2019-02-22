package network;

import java.io.IOException;
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

    public static void recieveData(){

    }
}