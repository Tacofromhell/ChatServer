package network;

import java.net.*;
import java.io.*;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatServerThread extends Thread implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private boolean running = true;
    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();
    private User currentUser = new User();

    ChatServerThread(Socket clientSocket, ChatServer server) {
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
        try (ObjectInputStream dataIn = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream dataOut = new ObjectOutputStream(clientSocket.getOutputStream())
            ){

            System.out.println(clientSocket.getRemoteSocketAddress() + " connected.");
            server.addConnectedClient(clientSocket, dataOut);
            System.out.println("Connected Clients: " + server.getConnectedClients().size());
            while (running) {
                try {
                    dataQueue.addLast(dataIn.readObject());
                } catch (EOFException eofEx){
                    //Handles error when client closes socket
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    server.removeConnection(clientSocket);
                    break;
                } catch (SocketException se){
                    //Handles error when client stops program
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    server.removeConnection(clientSocket);
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
                if (dataQueue.getFirst() instanceof Message) {
                    System.out.println("data is Message");

                    Message msg = (Message) dataQueue.poll();
                    if(!this.currentUser.getUsername().equals(msg.getUser().getUsername()))
                        this.currentUser.setUsername(msg.getUser().getUsername());

                    System.out.println("Debug: " + msg.getTimestamp() + " | " + currentUser.getUsername() + ": " + msg.getMsg());

                    server.sendToAll(msg);
                } else if (dataQueue.getFirst() instanceof User) {
                    System.out.println("data is User");
                    this.currentUser = (User) dataQueue.poll();
                    System.out.println("UserName: " + currentUser.getUsername());
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
}