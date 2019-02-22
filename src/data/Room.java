package data;

import data.Message;
import data.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class Room implements Serializable {
    private static final long serialVersionUID = 8119886995263638778L;

    private String roomName;
    private int roomSize;
    private LinkedBlockingDeque<Message> messages = new LinkedBlockingDeque<>();
    private ArrayList<User> users = new ArrayList<>();

    public Room(String name, int roomSize) {
        this.roomName = name;
        this.roomSize = roomSize == 0 ? 1000 : roomSize;
    }

    public String getRoomName() {
        return roomName;
    }

    public void addMessageToRoom(Message msg) {
        messages.addLast(msg);
    }

    public void addUserToRoom(User user) {
        if (users.size() < roomSize + 1)
            users.add(user);
        else
            System.err.println("Room: " + roomName + " is full");
    }

    public LinkedBlockingDeque<Message> getMessages() {
        return messages;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void updateUser(User updatedUser){
        users.forEach(user -> {
            if(user.getID().equals(updatedUser.getID())){
                user.setUsername(updatedUser.getUsername());
            }
        });
    }
}
