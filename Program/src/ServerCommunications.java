import java.io.*;
import java.net.*;
import java.util.*;

public class ServerCommunications implements Runnable {
    private LinkedList<Message> messageInbox = new LinkedList<>();

    private ServerSocket serverSocket = null;
    private LinkedList<ServerCommunicationRunnable> runnables = new LinkedList<>();
    private boolean stopped = false;

    public ServerCommunications () {
        new Thread(this).start();
    }

    public void run() {
        startServer();
        while (!stopped) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerCommunicationRunnable runnable = new ServerCommunicationRunnable(this, socket);
                runnables.offer(runnable);
                new Thread(runnable).start();
            } catch (IOException e) {
                if (stopped) break;
                throw new RuntimeException("Error Accepting Client Connection", e);
            }
        }
    }

    private synchronized void startServer() {
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

    void appendInbox(Message m) {
        messageInbox.offer(m);
    }

    Message getMessage() {
        return messageInbox.poll();
    }

    int inboxSize() {
        return messageInbox.size();
    }

    public void sendMessage(Message m) {
        sendMessage(m.id, m.message);
    }

    public void sendMessage(int id, char message) {
        Iterator<ServerCommunicationRunnable> iterator = runnables.listIterator();
        boolean foundID = false;
        while (iterator.hasNext()) {
            ServerCommunicationRunnable runnable = iterator.next();
            if (id == runnable.getId()) {
                runnable.push(message);
                foundID = true;
                break;
            }
        }
    }

    public void broadcastMessage(char message) {
        Iterator<ServerCommunicationRunnable> iterator = runnables.listIterator();
        while (iterator.hasNext()) {
            ServerCommunicationRunnable runnable = iterator.next();
            runnable.push(message);
        }
    }
}
