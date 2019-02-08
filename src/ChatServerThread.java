import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread implements Runnable{
    private Socket socket = null;

    public ChatServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
        this.start();
    }

    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String msg;
            while ((msg = in.readLine()) != null) {
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