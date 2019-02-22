package network;

import data.HandleData;
import data.User;

import java.net.*;
import java.io.*;

public class SocketConnection extends Thread implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private boolean running = true;
    private User socketUser;
    private HandleData handleData;

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

        handleData = new HandleData(socketUser);

        Thread startHandleData = new Thread(handleData);

        startHandleData.setDaemon(true);

        startHandleData.start();

        SocketStreamHelper.sendData(socketUser, dataOut);

        // add user to general room
        ChatServer.get().getRooms().get(0).addUserToRoom(socketUser);
        ChatServer.get().getRooms().get(1).addUserToRoom(socketUser);

        ChatServer.get().addUser(socketUser);

        System.out.println(socketUser + " " + socketUser.getID());
        System.out.println(clientSocket.getRemoteSocketAddress() + " connected.");
        System.out.println("Connected Clients: " +  ChatServer.get().getUsers().stream().filter(user -> user.getOnlineStatus() == true).count());


        while (running) {
            Object data = SocketStreamHelper.receiveData(dataIn);

            if (data == null) {
                handleDisconnect();
                break;
            } else {
                handleData.addData(data);
            }
        }
    }


    private void handleDisconnect() {
        //Handles error when client stops program
        System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
        socketUser.setOnlineStatus(false);
        ChatServer.get().removeConnection(clientSocket, socketUser);

        handleData.updateUsers();
    }

}