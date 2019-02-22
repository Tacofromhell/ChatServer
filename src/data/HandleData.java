package data;

import network.ChatServer;
import network.SocketStreamHelper;

import java.util.concurrent.LinkedBlockingDeque;

public class HandleData implements Runnable {

    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();
    private User socketUser;

    public HandleData(User socketUser) {
        this.socketUser = socketUser;

    }

    public void run() {

        //Checks if dataQueue has anything to handle else sleep
        while (true) {
            if (dataQueue.size() > 0) {

                Object data = dataQueue.poll();

                if (data instanceof Message) {
                    handleMessage(data);

                } else if (data instanceof User) {
                    System.err.println("User object received but method is deprecated");

                } else if (data instanceof NetworkMessage.ClientConnect) {
                    handleClientConnect();

                } else if (data instanceof NetworkMessage.UserNameChange) {
                    handleUserNameChange((NetworkMessage.UserNameChange)data);

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

    private void handleClientConnect() {
        ChatServer.get().getRooms().forEach(room -> room.updateUser(this.socketUser));

        System.out.println("UserName: " + socketUser.getUsername());

        //PLACEHOLDER: Get the rest of the users rooms:
        //ArrayList<String> joinedRooms = ((User) data).getJoinedRooms();

        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(0), socketUser.getDataOut());

        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(1), socketUser.getDataOut());

        System.out.println("Updating");
        ChatServer.get().broadcastToAll(ChatServer.get().getRooms());
    }

    private void handleClientDisconnect() {
    }

    private void handleRoomCreate() {
    }

    private void handleRoomDelete() {
    }

    private void handleRoomJoin() {
    }

    private void handleRoomLeave() {
    }

    private void handleUserNameChange(NetworkMessage.UserNameChange data) {
        ChatServer.get().getUser(data.userId).setUsername(data.newName);
    }

    private void handleMessage(Object data) {
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
    }

    private void handleRoom() {
    }

    public void addData(Object o) {
        dataQueue.addLast(o);
    }

    public void updateUsers() {
        System.out.println("Updating users...");
        ChatServer.get().broadcastToAll(ChatServer.get().getRooms());
    }


} //END OF CLASS
