package network;

import data.Room;
import data.User;
import storage.StorageHandler;
import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final int PORT = 1234;
    private boolean running = true;
    private ConcurrentHashMap<String, User> allUsers;
    private ConcurrentHashMap<String, Room> rooms;
    private final static ChatServer singleton = new ChatServer();

    private ChatServer() {
        System.out.println("Starting server");

        // load backup
        allUsers = (ConcurrentHashMap) StorageHandler.readFile("users-data.ser");
        rooms = (ConcurrentHashMap) StorageHandler.readFile("rooms-data.ser");

        new Thread(this::listeningOnClients).start();
    }

    public static ChatServer get() {
        return singleton;
    }

    private void listeningOnClients() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                new SocketConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public ConcurrentHashMap<String, Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        if (rooms.contains(room))
            System.out.println(room.getRoomName() + " already exists");
        else
            rooms.putIfAbsent(room.getRoomName(), room);
    }

    public void addUser(User user) {
        if (allUsers.contains(user))
            System.out.println("User already exists");
        else
            allUsers.putIfAbsent(user.getID(), user);
    }

    public void removeUser(User user) {
        allUsers.remove(user.getID());
    }

    public User getUser(String targetUserID) {
        return allUsers.get(targetUserID);
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return allUsers;
    }

    public void removeConnection(Socket socket, User user) {

        try {
            getUser(user.getID()).setOnlineStatus(false);
            System.out.println("Removing connection: " + socket.getRemoteSocketAddress().toString());
            System.out.println("Connected clients: " +
                    allUsers.values().stream().filter((u) ->
                            u.getOnlineStatus())
                            .count());

            StorageHandler.saveToStorage(ChatServer.get().getRooms(), "rooms-data.ser");
            StorageHandler.saveToStorage(ChatServer.get().getUsers(), "users-data.ser");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}