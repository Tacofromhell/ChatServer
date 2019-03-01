package data;

import java.io.Serializable;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 8119880995263638779L;

    private String sender;
    private transient Socket reviever;
    private String msg;
    private String room;
    private LocalTime timestamp;
    private boolean toAll = true;
    private User user;

    public Message(Socket sender, String msg, User user, String room) {
        this.sender = sender.getLocalSocketAddress().toString();
        this.user = user;
        this.msg = msg;
        this.room = room;
        this.timestamp = LocalTime.now();
    }

    public Message(Socket sender, Socket reviever, String msg) {
        this.sender = sender.getLocalSocketAddress().toString();
        this.reviever = reviever;
        this.msg = msg;
        this.timestamp = LocalTime.now();
    }

    public String getRoom() {
        return room;
    }

    String getSender() {
        return this.sender;
    }

    public User getUser() {
        return user;
    }

    String getMsg() {
        return this.msg;
    }

    String getTimestamp() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        return this.timestamp.format(timeFormat);
    }

    Boolean GetToAll() {
        return toAll;
    }
}

