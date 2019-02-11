package network;

import java.net.*;
import java.io.*;
import java.util.concurrent.LinkedBlockingDeque;

public class ChatServerThread extends Thread implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private boolean running = true;
    private LinkedBlockingDeque dataQueue = new LinkedBlockingDeque();

    ChatServerThread(Socket clientSocket, ChatServer server) {
        super("ServerThread");
        this.clientSocket = clientSocket;
        //TODO: Add setSoTimeout()
        this.server = server;
        this.start();

    }

    public void run() {
        try {
//            InputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            System.out.println(clientSocket.getRemoteSocketAddress() + " connected.");

            ObjectInputStream dataIn = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream dataOut = new ObjectOutputStream(clientSocket.getOutputStream());

            server.addConnectedClient(clientSocket, dataOut);
            System.out.println("Connected Clients: " + server.getConnectedClients().size());
            while (running) {
                Message msg = null;
                try {
                    msg = (Message) dataIn.readObject();
                    dataQueue.addLast(msg);
                    handleData();
                } catch (SocketException se) {
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    server.removeConnection(clientSocket);
                    if (server.getConnectedClients().size() == 0) {
                        break;
                    }
//                    se.printStackTrace();
//                    server.removeConnection(clientSocket);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //TODO: Close ObjectOutputStream and ObjectInputStream?
//                dataIn.close();
//                dataOut.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleData() {
        Message msg = (Message) dataQueue.poll();
        if (msg instanceof Message && msg.GetToAll()) {
            System.out.println("Debug: " + msg.getTimestamp() + " | " + msg.getSender().substring(1) + ": " + msg.getMsg());
            server.sendToAll(msg);
        }
    }
}