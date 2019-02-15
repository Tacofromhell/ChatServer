package network;

import java.util.concurrent.LinkedBlockingDeque;

public class Room {
    private String roomName;
    private int roomSize;
    private LinkedBlockingDeque<Message> messages = new LinkedBlockingDeque<>();
    private LinkedBlockingDeque<User> users = new LinkedBlockingDeque<>();

    public Room(String name, int roomSize) {
        this.roomName = name;
        this.roomSize = roomSize == 0 ? 1000 : roomSize;
    }

    public void addMessageToRoom(Message msg) {
        messages.addLast(msg);
    }

    public void addUserToRoom(User user) {
        if (users.size() < roomSize + 1)
            users.addLast(user);
        else
            System.err.println("Room: " + roomName + " is full");
    }

    public LinkedBlockingDeque<Message> getMessages() {
        return messages;
    }

    public LinkedBlockingDeque<User> getUsers() {
        return users;
    }
}
