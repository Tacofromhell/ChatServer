package data;

import network.ChatServer;
import network.SocketStreamHelper;

import java.util.concurrent.LinkedBlockingDeque;

public class HandleData implements Runnable{

    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();
    private User socketUser;

    public HandleData(User socketUser){
        this.socketUser= socketUser;

    }

    public void run() {

        //Checks if dataQueue has anything to handle else sleep
        while (true) {
            if (dataQueue.size() > 0) {

                    Object data = dataQueue.poll();

                if (data instanceof Message) {

                    Message msg = (Message) data;

                    if (!this.socketUser.getUsername().equals(msg.getUser().getUsername()))
                        this.socketUser.setUsername(msg.getUser().getUsername());

                    System.out.println(msg.getRoom() + ": " + msg.getTimestamp() + " | " + socketUser.getUsername() + ": " + msg.getMsg());

                    ChatServer.get().broadcastToRoom(msg.getRoom(), msg);

                    ChatServer.get().getRooms().forEach(room -> {
                        if (room.getRoomName().equals(msg.getRoom())) {
                            room.addMessageToRoom(msg);
                        }
                    });

                } else if (data instanceof User) {
                    this.socketUser = (User) data;
                    ChatServer.get().getRooms().forEach(room -> room.updateUser(this.socketUser));

                } else if (((String) data).startsWith("connecting")) {
                    ChatServer.get().getRooms().forEach(room -> room.updateUser(this.socketUser));
                    System.out.println("UserName: " + socketUser.getUsername());

//                    ArrayList<String> joinedRooms = ((User) data).getJoinedRooms();

                    SocketStreamHelper.sendData(ChatServer.get().getRooms().get(0), socketUser.getDataOut());

                    SocketStreamHelper.sendData(ChatServer.get().getRooms().get(1), socketUser.getDataOut());

                    System.out.println("Updating");
                    ChatServer.get().broadcastToAll(ChatServer.get().getRooms());

                } else if (((String) data).startsWith("update")) {
                    updateUsers();
                }
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addData(Object o){
        dataQueue.addLast(o);
    }

    public void updateUsers() {
        System.out.println("Updating");
        ChatServer.get().broadcastToAll( ChatServer.get().getRooms());
    }

} //END OF CLASS
