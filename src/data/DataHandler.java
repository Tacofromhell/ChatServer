package data;

import data.NetworkMessage.*;
import network.Broadcast;
import network.ChatServer;
import storage.StorageHandler;


import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class DataHandler implements Runnable {

    private DataHandlerHelper helper;
    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();

    public DataHandler(User user) {
        helper = new DataHandlerHelper(user);

    }

    public void run() {

        //Checks if dataQueue has anything to handle else sleep
        while (true) {
            if (dataQueue.size() > 0) {

                Object data = dataQueue.poll();

                if (data instanceof Message) {
                    helper.handleMessage(data);

                } else if (data instanceof User) {
                    System.err.println("User object received but method is deprecated");

                } else if (data instanceof ClientConnect) {
                    helper.handleClientConnect((ClientConnect) data);

                } else if (data instanceof RoomCreate) {
                    helper.handleRoomCreate((RoomCreate) data);

                } else if (data instanceof RoomDelete) {

                } else if (data instanceof RoomJoin) {
                    helper.handleRoomJoin(((RoomJoin) data).getTargetRoom(), ((RoomJoin) data).getUser(), ((RoomJoin) data).firstConnection);

                } else if (data instanceof RoomLeave) {
                    helper.handleRoomLeave((RoomLeave) data);

                } else if (data instanceof UserActiveRoom) {
                    helper.handleUserActiveRoom((UserActiveRoom) data);

                } else if (data instanceof UserNameChange) {
                    helper.handleUserNameChange((UserNameChange) data);

                } else if (((String) data).startsWith("update")) {
                    updateUsers();
                }

                // when rooms get updated, save to file
                StorageHandler.saveToStorage(ChatServer.get().getRooms(), "rooms-data.ser");
                StorageHandler.saveToStorage(ChatServer.get().getUsers(), "users-data.ser");

            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateUsers() {
        System.out.println("Updating users...");
        Broadcast.toAll(ChatServer.get().getRooms());
    }

    public void addToQueue(Object o) {
        dataQueue.addLast(o);
    }

} //END OF CLASS
