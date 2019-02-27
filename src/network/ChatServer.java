package network;

import data.PublicRoom;
import data.Room;
import data.User;
import data.NetworkMessage.*;
import storage.StorageHandler;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final int PORT = 1234;
    private boolean running = true;
    private ConcurrentHashMap<String, User> allUsers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final static ChatServer singleton = new ChatServer();

    private ChatServer() {
        System.out.println("Starting server");
        addRoom(new PublicRoom("general"));

        new Thread(this::listeningOnClients).start();
    }

    private ConcurrentHashMap<String, Room> getChatHistory() {
        final ConcurrentHashMap<String, Room> rooms;
        try {
            // Trying to fetch chat history from storage
            rooms = new StorageHandler<ConcurrentHashMap<String, Room>>().getFromStorage("history.txt");
            // Removing old users from the rooms
            rooms.forEach((s, room) -> rooms.get(s).clearUsers());
            return rooms;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //If no history exists
        return new ConcurrentHashMap<>();
    }

    public static ChatServer get() {
        return singleton;
    }

    private void listeningOnClients() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            // TODO: find a way to store connection in a thread pool
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
        if (rooms.contains(room.getRoomName()))
            System.out.println(room.getRoomName() + " already exists");
        else
            rooms.putIfAbsent(room.getRoomName(), room);
    }


    void addUser(User user) {
        if (allUsers.contains(user))
            System.out.println("User already exists");
        else
            allUsers.putIfAbsent(user.getID(), user);
    }

    public void removeUser(User user) {
        allUsers.remove(user);
    }

    public User getUser(String targetUserID) {
        return allUsers.get(targetUserID);
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return allUsers;
    }

    public void removeConnection(Socket socket, User user) {
        user.getJoinedRooms().forEach(joinedRoom ->
                Broadcast.toRoom(joinedRoom, new ClientDisconnect(user.getID())));

        try {
            socket.close();
            getUser(user.getID()).setOnlineStatus(false);
            System.out.println("Removing connection: " + socket.getRemoteSocketAddress().toString());
            System.out.println("Connected clients: " +
                    allUsers.values().stream().filter((u) ->
                            u.getOnlineStatus() == true)
                            .count());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}