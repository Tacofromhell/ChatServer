package network;

import data.Message;
import data.Room;
import data.User;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class ChatServer {

    private final int PORT = 1234;
    private boolean running = true;
    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<Room> rooms = new ArrayList<>();
    public static ChatServer singleton = new ChatServer();

    public ChatServer() {
        System.out.println("Starting server");
        addRoom(new Room("general", 0));
        addRoom(new Room("other room", 0));
        listeningOnClients();
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

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void broadcastToAll(Object o) {
        Stream.of(allUsers)
                .map(user -> user.stream().filter(u -> u.getOnlineStatus() == true))
                .forEach(onlineUser -> onlineUser.forEach(userStream -> {
                  SocketStreamHelper.sendData(o, userStream.getDataOut() );
                }));
    }

    public void broadcastToRoom(String roomName, Message msg) {
        rooms.forEach(room -> {
            if (room.getRoomName().equals(roomName)) {
                room.getUsers().stream()
                        .filter(user -> user.getOnlineStatus() == true)
                        .forEach(user -> {
                            SocketStreamHelper.sendData(msg, user.getDataOut());
                        });
            }
        });
    }

    void addUser(User user) {
        allUsers.add(user);
    }

    public void removeUser(User user) {
        allUsers.remove(user);
    }

    public User getUser(String userID) {
        for (User user : allUsers) {
            if (user.getID().equals(userID)) {
                return user;
            }
        }
        return null;
    }

    ArrayList<User> getUsers() {
        return allUsers;
    }

    public void removeConnection(Socket socket, User user) {
        try {
            socket.close();
            getUser(user.getID()).setOnlineStatus(false);
            System.out.println("Removing connection: " + socket.getRemoteSocketAddress().toString());
            System.out.println("Connected clients: " +
                    allUsers.stream().filter(u ->
                            u.getOnlineStatus() == true)
                            .count());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}