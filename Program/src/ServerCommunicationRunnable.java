import java.io.*;
import java.net.Socket;

public class ServerCommunicationRunnable implements Runnable {
    private ServerCommunications communication;
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    ServerCommunicationRunnable(ServerCommunications communication, Socket socket) {
        this.communication = communication;
        this.socket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                // TODO
            } catch (IOException e) {
                throw new RuntimeException("OUT Error", e);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("IN Error", e);
        }
    }
}
