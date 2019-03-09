package network;

import data.User;

import java.util.stream.Stream;

public class Broadcast {

    public static void toAll(Object data) {
        Stream.of(ChatServer.get().getUsers().values())
                .map(user -> user.stream().filter(u -> u.getOnlineStatus() == true))
                .forEach(onlineUser -> onlineUser.forEach(userStream -> {
                    SocketStreamHelper.sendData(data, userStream.getDataOut());
                }));
    }

    public static void toAllExceptThisSocket(Object data, User currentUser) {
        Stream.of(ChatServer.get().getUsers().values())
                .map(user -> user.stream().filter(u -> u.getOnlineStatus() == true))
                .forEach(onlineUser -> onlineUser.forEach(userStream -> {
                    if (!userStream.getID().equals(currentUser.getID()))
                        SocketStreamHelper.sendData(data, userStream.getDataOut());
                }));
    }

    public static void toRoom(String roomName, Object data) {
        ChatServer.get().getRooms().get(roomName).getUsers().values().stream()
                .filter(user -> user.getOnlineStatus())
                .forEach(user -> SocketStreamHelper.sendData(data, user.getDataOut()));
    }

}
