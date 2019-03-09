package data;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 8119880995263638778L;

    private String ID;
    private String username;
    private transient ObjectOutputStream dataOut;
    private boolean onlineStatus;
    private ArrayList<String> joinedRooms = new ArrayList<>();
    private String activeRoom = "";

    public User(ObjectOutputStream dataOut) {
        this.ID = UUID.randomUUID().toString();
        this.username = "anon" + new Random().nextInt(1000);
        this.dataOut = dataOut;
        this.onlineStatus = true;
        addJoinedRoom("general");
        setActiveRoom("general");
    }

    public User(String name) {
        this.ID = UUID.randomUUID().toString();
        this.username = name.length() > 0 ? name : "anon";
    }

    public void addJoinedRoom(String roomName) {
        if (!joinedRooms.contains(roomName))
            joinedRooms.add(roomName);
    }

    public void removeJoinedRoom(String roomName) {
        if (joinedRooms.contains(roomName))
            joinedRooms.remove(roomName);
    }

    public String getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;

        // move active room to index 1
        joinedRooms.remove(activeRoom);
        joinedRooms.add(0, activeRoom);
        joinedRooms.remove("general");
        joinedRooms.add(0, "general");
    }

    public void setDataOut(ObjectOutputStream dataOut) {
        this.dataOut = dataOut;
    }

    public ObjectOutputStream getDataOut() {
        return dataOut;
    }

    public ArrayList<String> getJoinedRooms() {
        return joinedRooms;
    }

    public User getUser() {
        return this;
    }

    public String getID() {
        return this.ID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public boolean getOnlineStatus() {
        return this.onlineStatus;
    }
}//class end