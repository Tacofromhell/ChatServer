import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final static ChatServer server = new ChatServer();
    private final int PORT = 1234;
    private boolean running = true;
    private List<ChatServerThread> connectedClients = new ArrayList<>();

    private ChatServer() {
        try (
            ServerSocket serverSocket = new ServerSocket(PORT)
        ) {
            while (running) {
                connectedClients.add(new ChatServerThread(serverSocket.accept()));
                System.out.println("Connected Clients: " + connectedClients.size());
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public static ChatServer get(){
        return server;
    }


}