package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Utility.Configuration;
import Utility.Message;

public class ServerCommunications implements Runnable {
    private LinkedList<Message> messageInbox = new LinkedList<>();

    private ServerSocket serverSocket = null;
    private LinkedList<ServerCommunicationRunnable> runnables = new LinkedList<>();
    private boolean stopped = false;

    ServerCommunications () {
        startServer();
        new Thread(this).start();
    }

    public void run() {
        while (!stopped) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerCommunicationRunnable runnable = new ServerCommunicationRunnable(this, socket);
                runnables.add(runnable);
                new Thread(runnable).start();
            } catch (IOException e) {
                if (stopped) break;
                throw new RuntimeException("Error Accepting Game Connection", e);
            }
        }
    }

    private synchronized void startServer() {
        try {
            this.stopped = false;
            this.serverSocket = new ServerSocket(Configuration.PORT);
            System.out.println("Server Started At Address: " + InetAddress.getLocalHost());
        } catch (IOException e) {
            this.stopped = true;
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    synchronized void stop() {
        this.stopped = true;
        try {
            for (ServerCommunicationRunnable runnable: runnables) {
                runnable.stop();
            }
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error Closing Server", e);
        }
    }

    synchronized void appendInbox(Message m) {
        messageInbox.offer(m);
    }

    synchronized Message getMessage() {
        return messageInbox.poll();
    }

    synchronized int inboxSize() {
        return messageInbox.size();
    }

    synchronized void sendMessage(Message m) {
        sendMessage(m.id, m.message);
    }

    synchronized void sendMessage(int id, char message) {
        for (ServerCommunicationRunnable runnable: runnables) {
            if (id == runnable.getId()) {
                runnable.push(message);
            }
        }
    }

    synchronized void broadcastMessage(char message) {
        for (ServerCommunicationRunnable runnable: runnables) {
            runnable.push(message);
        }
    }

    synchronized void broadcastMessage(HashMap<Integer, Player> players, char message) {
        Set<Integer> idSet = players.keySet();
        for (ServerCommunicationRunnable runnable: runnables) {
            if (idSet.contains(runnable.getId())) {
                runnable.push(message);
            }
        }
    }
}
