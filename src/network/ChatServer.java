package network;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ChatServer {

    private final int PORT = 1234;
    private boolean running = true;
    private Map<Socket, ObjectOutputStream> connectedClients = new HashMap<>();

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

    void addConnectedClient(Socket socket, ObjectOutputStream outputStream) {
        this.connectedClients.putIfAbsent(socket, outputStream);
    }

    Map getConnectedClients() {
        return this.connectedClients;
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

    void removeConnection(Socket socket) {
        try {
            socket.close();
            System.out.println("Removing connection: " + socket.getRemoteSocketAddress().toString());
            connectedClients.remove(socket);
            System.out.println("Connected clients: " + connectedClients.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}