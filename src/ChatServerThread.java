import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread implements Runnable{
    private Socket socket = null;
    private ChatServer server;

    public ChatServerThread(Socket socket, ChatServer server) {
        super("ServerThread");
        this.socket = socket;
        this.server = server;
        this.start();
    }

    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            server.addConnectedClient(socket, dout);
            System.out.println("Connected Clients: " + server.getConnectedClients().size());
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("1: " + msg);
                dout.writeUTF(msg);
                System.out.println("2: " + msg);
                dout.flush();
                System.out.println("3: " + msg);
//                server.sendToAll(msg);
                out.println(msg);
                if (msg.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}