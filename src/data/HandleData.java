package data;

import network.ChatServer;
import network.SocketStreamHelper;

import java.util.concurrent.Callable;
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

        System.out.println("NEW USER JOINED: " + socketUser.getID());

        //PLACEHOLDER: Get the rest of the users rooms:
        //ArrayList<String> joinedRooms = ((User) data).getJoinedRooms();

        //Sending "general" and "other room" to connected client
        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(0), socketUser.getDataOut());
        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(1), socketUser.getDataOut());

        //Telling every other connected client that there is a new user in "general" and "other room"
        for(User user : ChatServer.get().getUsers()){
            if(!user.getID().equals(socketUser.getID()) && user.getOnlineStatus()){
                System.out.println("SENDING EVENT NEW USER IN ROOM TO: " + user.getUsername());
                SocketStreamHelper.sendData(new NetworkMessage.RoomJoin("general", socketUser), user.getDataOut());
                SocketStreamHelper.sendData(new NetworkMessage.RoomJoin("other room", socketUser), user.getDataOut());
            }
        }
    }

    public void handleClientDisconnect(User user) {
        System.out.println(user.getUsername() + " disconnected");
        ChatServer.get().broadcastToAll(new NetworkMessage.ClientDisconnect(user.getID()));
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
        System.out.println(data.newName + " " + data.userId);
        ChatServer.get().getUser(data.userId).setUsername(data.newName);

        //Går igenom alla gamla meddelanden på servern och ändrar username på dom
        for(Room room : ChatServer.get().getRooms()){
            for(Message message :room.getMessages()){
                if(message.getUser().getID().equals(data.userId)){
                    message.getUser().setUsername(data.newName);
                }
            }
        }
        ChatServer.get().broadcastToAll(data);
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
        for (Room room : ChatServer.get().getRooms()) {
            ChatServer.get().broadcastToAll(room);
        }
    }


} //END OF CLASS
