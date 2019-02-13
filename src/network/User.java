package network;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 8119880995263638778L;

    private String ID;
    private String username;
    // private ArrayList<Room> joinedRooms;
    // private Room activeRoom;

    public User() {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = "anon";
    }
    public User(String name) {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}//class end
