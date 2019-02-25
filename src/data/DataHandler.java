package data;

import network.ChatServer;
import network.SocketStreamHelper;
import data.NetworkMessage.*;

import java.util.concurrent.LinkedBlockingDeque;

public class DataHandler implements Runnable {

    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();
    private User socketUser;

    public DataHandler(User socketUser) {
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

                } else if (data instanceof ClientConnect) {
                    handleClientConnect((ClientConnect) data);

                } else if (data instanceof RoomCreate) {

                } else if (data instanceof RoomDelete) {

                } else if (data instanceof RoomJoin) {
                    handleRoomJoin(((RoomJoin) data).getTargetRoom(), ((RoomJoin) data).getUser());

                } else if (data instanceof RoomLeave) {

                } else if (data instanceof UserNameChange) {
                    handleUserNameChange((UserNameChange) data);

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

    private void handleClientConnect(ClientConnect data) {

        System.out.println(data.userId);

        socketUser.getJoinedRooms().forEach(roomName -> {
            handleRoomJoin(roomName, socketUser);
            ChatServer.get().getRooms().get(roomName).updateUser(this.socketUser);
            ChatServer.get().broadcastToRoom(roomName, new ClientConnect(socketUser.getID()));
        });

        System.out.println("UserName: " + socketUser.getUsername());

        // send first room
        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(socketUser.getActiveRoom()), socketUser.getDataOut());

        // then send the rest
        socketUser.getJoinedRooms().forEach(room -> {
            if (!room.equals(socketUser.getActiveRoom()))
                SocketStreamHelper.sendData(ChatServer.get().getRooms().get(room), socketUser.getDataOut());
        });

    }

    private void handleClientDisconnect() {
    }

    private void handleRoomCreate() {
    }

    private void handleRoomDelete() {
    }

    private void handleRoomJoin(String targetRoom, User user) {
        ChatServer.get().broadcastToRoom(targetRoom, new RoomJoin(targetRoom, user));
        ChatServer.get().getRooms().get(targetRoom).addUserToRoom(user);
    }

    private void handleRoomLeave() {
    }

    private void handleUserNameChange(UserNameChange data) {
        ChatServer.get().getUser(data.userId).setUsername(data.newName);

        // update users in all rooms
        ChatServer.get().getRooms().forEach((nameID, room) ->{
                room.updateUser(socketUser);
                for(Message message : room.getMessages()){
                    if(message.getUser().getID().equals(data.userId)){
                        message.getUser().setUsername(data.newName);
                    }
                }
        });

        // send updated user to all clients
        ChatServer.get().broadcastToAll(data);
    }

    private void handleMessage(Object data) {
        Message msg = (Message) data;

        System.out.println(msg.getRoom() + ": " + msg.getTimestamp() + " | " + socketUser.getUsername() + ": " + msg.getMsg());

        ChatServer.get().broadcastToRoom(msg.getRoom(), msg);

        ChatServer.get().getRooms().forEach((roomID, room) -> {
            if (roomID.equals(msg.getRoom())) {
                room.addMessageToRoom(msg);
            }
        });
    }

    private void handleRoom() {
    }

    public void addToQueue(Object o) {
        dataQueue.addLast(o);
    }

    public void updateUsers() {
        System.out.println("Updating users...");
        ChatServer.get().broadcastToAll(ChatServer.get().getRooms());
    }


} //END OF CLASS
