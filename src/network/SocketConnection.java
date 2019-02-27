package network;

import data.DataHandler;
import data.User;

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
        socketUser = new User(dataOut);
        dataHandler = new DataHandler(socketUser);

        Thread startdataHandler = new Thread(dataHandler);
        startdataHandler.setDaemon(true);
        startdataHandler.start();

        SocketStreamHelper.sendData(socketUser, dataOut);

        // add user to general room
//        ChatServer.get().getRooms().get("other room").addUserToRoom(socketUser);

        ChatServer.get().addUser(socketUser);

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
        socketUser.setOnlineStatus(false);
        ChatServer.get().removeConnection(clientSocket, socketUser);
        dataHandler.updateUsers();
    }

}