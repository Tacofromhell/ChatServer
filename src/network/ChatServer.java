package network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ChatServer {

    private final int PORT = 1234;
    private boolean running = true;
    private Map<User, ObjectOutputStream> connectedClients = new HashMap<>();
    private List<User> listOfConnectedClients = new ArrayList<>();

    public ChatServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            // TODO: find a way to store connection in a thread pool
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new ChatServerThread(clientSocket, this);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    ChatServer get() {
        return this;
    }

    void addConnectedClient(User user, ObjectOutputStream outputStream) {
        this.connectedClients.putIfAbsent(user, outputStream);
        this.listOfConnectedClients.add(user);
    }

    Map getConnectedClients() {
        return this.connectedClients;
    }

    List getListOfConnectedClients(){
        return this.listOfConnectedClients;
    }

    void sendToAll(Message msg) {
        Stream.of(connectedClients.values())
                .forEach(value ->
                        value.forEach(outputStream -> {
                            try {
                                outputStream.writeObject(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                );
    }

    void sendToAll(List list) {
        Stream.of(connectedClients.values())
                .forEach(value ->
                        value.forEach(outputStream -> {
                            try {
                                outputStream.reset();
                                outputStream.writeObject(list);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                );

    }

    void removeConnection(User user, Socket socket) {
        try {
            socket.close();
            System.out.println("Removing connection: " + socket.getRemoteSocketAddress().toString());
            listOfConnectedClients.remove(user);
            connectedClients.remove(user);
            System.out.println("Connected clients: " + connectedClients.size() + " : " + listOfConnectedClients.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}