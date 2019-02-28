package network;

import data.DataHandler;
import data.NetworkMessage;
import data.User;
import storage.StorageHandler;

import java.net.*;
import java.io.*;

public class SocketConnection extends Thread implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private boolean running = true;
    private User socketUser;
    private DataHandler dataHandler;

    SocketConnection(Socket clientSocket) {
        super("ServerThread");
        this.clientSocket = clientSocket;
        this.start();
    }

    public void run() {
        try {
            dataIn = new ObjectInputStream(clientSocket.getInputStream());
            dataOut = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        NetworkMessage.InitializeClient initClient = (NetworkMessage.InitializeClient) SocketStreamHelper.receiveData(dataIn);
        if(ChatServer.get().getUsers().containsKey(initClient.userId)){
            socketUser = ChatServer.get().getUser(initClient.userId);
            socketUser.setOnlineStatus(true);
            socketUser.setDataOut(dataOut);
            socketUser.getJoinedRooms().forEach(roomName -> {
                ChatServer.get().getRooms().get(roomName).updateUser(this.socketUser);
            });
        } else {
            socketUser = new User(dataOut);
            ChatServer.get().addUser(socketUser);
        }

        dataHandler = new DataHandler(socketUser);

        Thread startdataHandler = new Thread(dataHandler);
        startdataHandler.setDaemon(true);
        startdataHandler.start();

        SocketStreamHelper.sendData(socketUser, dataOut);

        // add user to general room
//        ChatServer.get().getRooms().get("other room").addUserToRoom(socketUser);


        System.out.println(socketUser + " " + socketUser.getID());
        System.out.println(clientSocket.getRemoteSocketAddress() + " connected.");
        System.out.println("Connected Clients: " +  ChatServer.get().getUsers().values().stream().filter(user -> user.getOnlineStatus() == true).count());

        //Threadloop
        while (running) {
            Object data = SocketStreamHelper.receiveData(dataIn);

            if (data != null) {
                dataHandler.addToQueue(data);
            } else {
                handleDisconnect();
                break;
            }
        }
    }

    private void handleDisconnect() {
        //Handles error when client stops program
        System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());

        Broadcast.toAllExceptThisSocket(new NetworkMessage.ClientDisconnect(socketUser.getID()), socketUser);
//        SocketStreamHelper.sendData(new NetworkMessage.ClientDisconnect(socketUser.getID()), socketUser.getDataOut());
        ChatServer.get().removeConnection(clientSocket, socketUser);

    }

}