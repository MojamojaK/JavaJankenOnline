package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Utility.Configuration;
import Utility.Message;
import Utility.ReadWriteLock;

public class ServerCommunications implements Runnable {
    private LinkedList<Message> messageInbox = new LinkedList<>();
    ReadWriteLock mi_rwl = new ReadWriteLock();

    private ServerSocket serverSocket = null;
    HashMap<Integer, SocketIO> clientSockets = new HashMap<>();
    ReadWriteLock cs_rwl = new ReadWriteLock();
    private boolean stopped = false;

    ServerCommunications () {
        startServer();
        new Thread(this).start();
    }

    public void run() {
        System.out.println("ServerCommunications Thread Started");
        while (!stopped) {
            try {
                Socket socket = this.serverSocket.accept();
                SocketIO socket_io = new SocketIO(this, socket);
                try {
                    cs_rwl.writeLock();
                    System.out.println("Establishing New Connection " + socket_io.getId());
                    this.clientSockets.put(socket_io.getId(), socket_io);
                    cs_rwl.writeUnlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            throw new RuntimeException("Cannot open port " + Configuration.PORT, e);
        }
    }

    synchronized void stop() {
        this.stopped = true;
        try {
            try {
                cs_rwl.readLock();
                for (Map.Entry<Integer, SocketIO> entry : clientSockets.entrySet()) {
                    entry.getValue().close();
                }
                cs_rwl.readUnlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error Closing Server", e);
        }
    }

    synchronized void appendInbox(Message m) {
        try {
            mi_rwl.writeLock();
            messageInbox.offer(m);
            mi_rwl.writeUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        try {
            cs_rwl.readLock();
            for (Map.Entry<Integer, SocketIO> entry : clientSockets.entrySet()) {
                if (entry.getKey() == id) {
                    entry.getValue().push(message);
                }
            }
            cs_rwl.readUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void broadcastMessage(char message) {
        try {
            cs_rwl.readLock();
            for (Map.Entry<Integer, SocketIO> entry: clientSockets.entrySet()) {
                entry.getValue().push(message);
            }
            cs_rwl.readUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void broadcastMessage(HashMap<Integer, Player> players, char message) {
        try {
            cs_rwl.readLock();
            Set<Integer> idSet = players.keySet();
            for (Map.Entry<Integer, SocketIO> entry: clientSockets.entrySet()) {
                if (idSet.contains(entry.getKey())) {
                    entry.getValue().push(message);
                }
            }
            cs_rwl.readUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}