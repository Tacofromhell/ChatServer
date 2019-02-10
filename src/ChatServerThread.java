import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread implements Runnable{
    private Socket clientSocket;
    private ChatServer server;
    private boolean running = true;

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
                } catch (SocketException se){
                    System.out.println("Lost connection with " + clientSocket.getRemoteSocketAddress());
                    server.removeConnection(clientSocket);
//                    server.removeConnection(clientSocket);
//                    se.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("Debug: " + msg.getTimestamp() + " | " + msg.getSender().substring(1) + ": " + msg.getMsg());
                server.sendToAll(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}