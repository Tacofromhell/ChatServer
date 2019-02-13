package network;

import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 8119880995263638779L;

    //    private transient Socket sender;
    private String sender;
    private transient Socket reviever;
    private String msg;
    private LocalTime timestamp;
    private boolean toAll = true;
    private User user;

    public Message(Socket sender, String msg, User user) {
        this.sender = sender.getLocalSocketAddress().toString();
        this.user = user;
        this.msg = msg;
        this.timestamp = LocalTime.now();
    }

    public Message(Socket sender, Socket reviever, String msg) {
        this.sender = sender.getLocalSocketAddress().toString();
        this.reviever = reviever;
        this.msg = msg;
        this.timestamp = LocalTime.now();
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
        String hour = this.timestamp.getHour() < 10 ? "0" + this.timestamp.getHour() : "" + this.timestamp.getHour();
        String minute = this.timestamp.getMinute() < 10 ? "0" + this.timestamp.getMinute() : "" + this.timestamp.getMinute();
        String second = this.timestamp.getSecond() < 10 ? "0" + this.timestamp.getSecond() : "" + this.timestamp.getSecond();
        return hour + "." + minute + "." + second;
    }

    Boolean GetToAll() {
        return toAll;
    }
}

