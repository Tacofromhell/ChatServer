package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import data.Message;
import data.User;

public class Room implements Serializable {
    private static final long serialVersionUID = 8119886995263638778L;

    private String roomName;
    private int roomSize;
    private LinkedBlockingDeque<Message> messages = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public Room(String name, int roomSize) {
        this.roomName = name;
        this.roomSize = roomSize == 0 ? 1000 : roomSize;
    }

    /*
    * This function clears the users list. When we retrieve the chat history, we cannot assume that the users are online,
    * hence, we will clear the users and when a user connects, we will add the user to the list.
    * */
    public void clearUsers() {
        this.users = new ConcurrentHashMap<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public void addMessageToRoom(Message msg) {
        messages.addLast(msg);
    }

    public void addUserToRoom(User user) {
        if (users.size() < roomSize + 1) {
            users.putIfAbsent(user.getID(), user);
            System.out.println(user.getID() + " added to " + roomName);
        } else
            System.err.println("Room: " + roomName + " is full");
    }

    public LinkedBlockingDeque<Message> getMessages() {
        return messages;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public void updateUser(User updatedUser) {
        users.get(updatedUser.getID())
                .setUsername(updatedUser.getUsername());
    }
}
