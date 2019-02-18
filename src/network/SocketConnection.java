package network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class SocketConnection extends Thread implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private ChatServer server;
    private boolean running = true;
    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();
    private User socketUser;

    SocketConnection(Socket clientSocket, ChatServer server) {
        super("ServerThread");
        this.clientSocket = clientSocket;
        //TODO: Add setSoTimeout()
        this.server = server;
        this.start();

        Thread startHandleData = new Thread(this::handleData);
        startHandleData.setDaemon(true);
        startHandleData.start();

    }

    public void run() {
        try {
            dataIn = new ObjectInputStream(clientSocket.getInputStream());
            dataOut = new ObjectOutputStream(clientSocket.getOutputStream());

            socketUser = new User(dataOut);
            socketUser.getDataOut().reset();
            socketUser.getDataOut().writeObject(socketUser);
            // add user to general room
            server.getRooms().get(0).addUserToRoom(socketUser);
            server.addUser(socketUser);
            sendToClient(server.getRooms().get(0));
            System.out.println(socketUser + " " + socketUser.getID());
            System.out.println(clientSocket.getRemoteSocketAddress() + " connected.");
            System.out.println("Connected Clients: " + server.getUsers().stream().filter(user -> user.getOnlineStatus() == true).count());


            while (running) {
                try {
                    dataQueue.addLast(dataIn.readObject());
                } catch (EOFException eofEx) {
                    //Handles error when client closes socket
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    socketUser.setOnlineStatus(false);
                    server.removeConnection(clientSocket, socketUser);
                    break;
                } catch (SocketException se) {
                    //Handles error when client stops program
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    socketUser.setOnlineStatus(false);
                    server.removeConnection(clientSocket, socketUser);
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleData() {
        //Checks if dataQueue has anything to handle else sleep
        while (true) {
            if (dataQueue.size() > 0) {
                Object data = dataQueue.poll();
                if (data instanceof Message) {

                    Message msg = (Message) data;
                    if (!this.socketUser.getUsername().equals(msg.getUser().getUsername()))
                        this.socketUser.setUsername(msg.getUser().getUsername());

                    System.out.println(msg.getRoom() + ": " + msg.getTimestamp() + " | " + socketUser.getUsername() + ": " + msg.getMsg());

                    server.broadcastToRoom(msg.getRoom(), msg);

                    server.getRooms().forEach(room -> {
                        if (room.getRoomName().equals(msg.getRoom())) {
                            room.addMessageToRoom(msg);
                        }
                    });

                } else if (data instanceof User) {
                    this.socketUser = (User) data;
                    server.getRooms().forEach(room -> room.updateUser(this.socketUser));
                    System.out.println("UserName: " + socketUser.getUsername());

                    // set outputStream in user
//                    socketUser.setDataOut(dataOut);

//                    ArrayList<String> joinedRooms = ((User) data).getJoinedRooms();
                } else if(((String) data).startsWith("update")){
                    System.out.println("Updating");
                    server.broadcastToAll(server.getRooms());
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

    private void sendToClient(Object object) {
        try {
            dataOut.writeObject(object);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}