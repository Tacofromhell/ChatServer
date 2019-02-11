package network;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String username;
    // private ArrayList<Room> joinedRooms;
    // private Room activeRoom;

    public User() {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = "anonymous";
    }

    public String getUsername() {
        return this.username;
    }


}//class end
