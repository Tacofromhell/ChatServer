package data;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Room implements Serializable {
    private static final long serialVersionUID = 8119886995263638778L;

    private String roomName;
    private int roomSize = 1000;
    private LinkedBlockingDeque<Message> messages = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public Room(String name) {
        this.roomName = name;
    }

    public String getRoomName() {
        return roomName;
    }

    public void addMessageToRoom(Message msg) {
        messages.addLast(msg);
    }

    public void addUserToRoom(User user) {
        if (!users.containsKey(user.getID()) &&
                users.size() < roomSize) {
            users.putIfAbsent(user.getID(), user);
            System.out.println(user.getUsername() + " added to " + roomName);
        }
    }

    public LinkedBlockingDeque<Message> getMessages() {
        return messages;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public void setUser(User user) {
        users.remove(user.getID());
        users.putIfAbsent(user.getID(), user);
    }

    public void updateUser(User updatedUser) {
        if (users.contains(updatedUser)) {
            users.get(updatedUser.getID())
                    .setUsername(updatedUser.getUsername());
        }
    }
}
