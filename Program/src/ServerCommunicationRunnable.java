import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class ServerCommunicationRunnable implements Runnable {
    private ServerCommunications communication;
    private Socket socket;

    private LinkedList<Character> outbox = new LinkedList<>();

    private static int next_id = 0;
    private final int id;

    private BufferedReader in;
    private PrintWriter out;

    ServerCommunicationRunnable(ServerCommunications communication, Socket socket) {
        this.communication = communication;
        this.socket = socket;
        id = next_id++;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                while (true) {
                    if (outbox.size() > 0) {
                        char message = outbox.poll();
                        out.print(message);
                    }
                    if (in.ready()) {
                        communication.appendInbox(new Message(id, (char)in.read()));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("OUT Error", e);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("IN Error", e);
        }
    }

    int getId() {
        return id;
    }

    void push(char message) {
        outbox.push(message);
    }
}
