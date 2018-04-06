import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerCommunications implements Runnable {
    private LinkedList<Message> messageInbox = new LinkedList<>();
    private LinkedList<Message> messageOutbox = new LinkedList<>();

    private ServerSocket serverSocket = null;
    private LinkedList<ServerCommunicationRunnable> runnables = new LinkedList<>();
    private boolean stopped = false;

    public ServerCommunications () {
        new Thread(this).start();
    }

    public void run() {

        while (!stopped) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerCommunicationRunnable runnable = new ServerCommunicationRunnable(this, socket);
                runnables.offer(runnable);
                new Thread(runnable).start();
            } catch (IOException e) {
                if (stopped) break;
                throw new RuntimeException("Error accepting client connection", e);
            }
        }
    }

    private synchronized void start() {
        try {
            this.stopped = false;
            this.serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            this.stopped = true;
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    public synchronized void stop() {
        this.stopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error Closing Server", e);
        }
    }

    private void appendInbox(Message m) {
        messageInbox.offer(m);
    }

    Message getMessage() {
        return messageInbox.poll();
    }

    int inboxSize() {
        return messageInbox.size();
    }

    void sendMessage(Message m) {
        messageOutbox.offer(m);
    }

    public void sendMessage(int id, char message) {
        sendMessage(new Message(id, message));
    }
}
