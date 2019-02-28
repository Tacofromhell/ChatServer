package data;

import network.Broadcast;
import network.ChatServer;
import network.SocketStreamHelper;
import data.NetworkMessage.*;

public class DataHandlerHelper {

    private User socketUser;

    public DataHandlerHelper(User socketUser) {
        this.socketUser = socketUser;
    }

    public void handleClientConnect(ClientConnect data) {

        System.out.println(data.userId);

        socketUser.getJoinedRooms().forEach(roomName -> {
            handleRoomJoin(roomName, socketUser);
            ChatServer.get().getRooms().get(roomName).updateUser(this.socketUser);
            Broadcast.toRoom(roomName, new ClientConnect(socketUser.getID()));
        });

        System.out.println("UserName: " + socketUser.getUsername());

        // send first room
        SocketStreamHelper.sendData(ChatServer.get().getRooms().get(socketUser.getActiveRoom()), socketUser.getDataOut());

        // then send the rest
        socketUser.getJoinedRooms().forEach(room -> {
            if (!room.equals(socketUser.getActiveRoom()))
                SocketStreamHelper.sendData(ChatServer.get().getRooms().get(room), socketUser.getDataOut());
        });

        // then send all public rooms
        ChatServer.get().getRooms().values().stream()
                .filter(room -> room instanceof PublicRoom)
                .forEach(publicRoom -> {
                    if (!socketUser.getJoinedRooms().contains(publicRoom.getRoomName())) {
                        SocketStreamHelper.sendData(new RoomCreate(
                                publicRoom.getRoomName(), true), socketUser.getDataOut());
                    }
                });

    }

    public void handleClientDisconnect() {
    }

    public void handleRoomCreate(RoomCreate data) {

        if (ChatServer.get().getRooms().containsKey(data.roomName)) {
            SocketStreamHelper.sendData(new RoomNameExists(),
                    socketUser.getDataOut());
        } else {
            Room room = data.isPublic() ? new PublicRoom(data.getRoomName()) :
                    new PrivateRoom(data.getRoomName());

            ChatServer.get().addRoom(room);
            socketUser.addJoinedRoom(data.getRoomName());
            handleRoomJoin(room.getRoomName(), socketUser);

            Broadcast.toAllExceptThisSocket(new RoomCreate(data.getRoomName(), true), socketUser);
        }
    }

    public void handleRoomDelete() {
    }

    public void handleRoomJoin(String targetRoom, User user) {
        // user needs to have socketStream
        ChatServer.get().getRooms().get(targetRoom).addUserToRoom(
                ChatServer.get().getUser(user.getID()));
        Broadcast.toRoom(targetRoom, new RoomJoin(targetRoom, user, ChatServer.get().getRooms().get(targetRoom)));
    }

    public void handleRoomLeave(RoomLeave data) {
        ChatServer.get().getRooms()
                .get(data.targetRoom)
                .getUsers()
                .remove(data.userId);
        ChatServer.get().getUsers()
                .get(data.userId)
                .removeJoinedRoom(data.targetRoom);

        // if rooms is empty, delete it
        if (ChatServer.get().getRooms().get(data.targetRoom)
                .getUsers().size() < 1) {
            ChatServer.get().getRooms().remove(data.targetRoom);
            Broadcast.toAll(new RoomDelete(data.targetRoom));
        } else {

            Broadcast.toRoom(data.targetRoom, new RoomLeave(data.targetRoom, data.userId));
        }

    }

    public void handleUserActiveRoom(UserActiveRoom data) {
        socketUser.setActiveRoom(data.getActiveRoom());
    }

    public void handleUserNameChange(UserNameChange data) {
        ChatServer.get().getUser(data.userId).setUsername(data.newName);

        // update users in all rooms
        ChatServer.get().getRooms().forEach((nameID, room) -> {
            room.updateUser(socketUser);
            for (Message message : room.getMessages()) {
                if (message.getUser().getID().equals(data.userId)) {
                    message.getUser().setUsername(data.newName);
                }
            }
        });

        // send updated user to all clients
        Broadcast.toAll(data);
    }

    public void handleMessage(Object data) {
        Message msg = (Message) data;

        System.out.println(msg.getRoom() + ": " + msg.getTimestamp() + " | " + socketUser.getUsername() + ": " + msg.getMsg());

        Broadcast.toRoom(msg.getRoom(), msg);

        ChatServer.get().getRooms().forEach((roomID, room) -> {
            if (roomID.equals(msg.getRoom())) {
                room.addMessageToRoom(msg);
            }
        });
    }

    public void handleRoom() {
    }
}
