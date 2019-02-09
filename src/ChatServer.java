import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ChatServer {

//    private final static ChatServer server = new ChatServer();
    private final int PORT = 1234;
    private boolean running = true;
    private Hashtable connectedClients = new Hashtable();

    public ChatServer() {
        try (
            ServerSocket serverSocket = new ServerSocket(PORT)
        ) {
            while (running) {
                new ChatServerThread(serverSocket.accept(), this);

            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public  ChatServer get(){
        return this;
    }

    public void addConnectedClient(Socket key, DataOutputStream value){
        this.connectedClients.put(key, value);
    }

    public Hashtable getConnectedClients(){
        return this.connectedClients;
    }

    public Enumeration getOutputStreams(){
        return connectedClients.elements();
    }

    public void sendToAll(String msg){
        for (Enumeration e = getOutputStreams(); e.hasMoreElements(); ) {
// ... get the output stream ...
            DataOutputStream dout = (DataOutputStream)e.nextElement();
// ... and send the message
            try {
                dout.writeUTF( msg );
            } catch( IOException ie ) { System.out.println( ie ); }
        }

    }


}