package network;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.IDN;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 8119880995263638778L;

    private String ID;
    private String username;
    private transient ObjectOutputStream dataOut;
    private boolean onlineStatus;
    private ArrayList<String> joinedRooms = new ArrayList<>();
//    private Room activeRoom;

    public User(ObjectOutputStream dataOut) {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = "anon";
        this.dataOut = dataOut;
        this.onlineStatus = true;
        joinedRooms.add("general");
    }

    public User(String name) {
        //this.activeRoom = "general";
        this.ID = UUID.randomUUID().toString();
        this.username = name.length() > 0 ? name : "anon";
    }

    public User(User oldUser) {
        this.ID = oldUser.ID;
        this.username = oldUser.username;
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOnlineStatus(boolean onlineStatus){
        this.onlineStatus = onlineStatus;
    }

    public boolean getOnlineStatus(){
        return this.onlineStatus;
    }
}//class end