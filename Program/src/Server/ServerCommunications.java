package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Utility.Configuration;
import Utility.Message;
import Utility.ReadWriteLock;

public class ServerCommunications implements Runnable {
    private LinkedList<Message> messageInbox = new LinkedList<>();
    private ReadWriteLock mi_rwl = new ReadWriteLock();

    private ServerSocket serverSocket = null;
    HashMap<Integer, SocketIO> clientSockets = new HashMap<>();
    ReadWriteLock cs_rwl = new ReadWriteLock();
    private boolean stopped = false;

    ServerCommunications () {
        startServer();
        new Thread(this).start();
    }

    public void run(){
        System.out.println("ServerCommunications Thread Started");
        while (!stopped) {
            try {
                boolean retry = true;
                while (retry){
                    try {
                        if (this.serverSocket.isClosed()) break;
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
                        retry = false;
                    } catch (SocketTimeoutException e) {}
                }
            } catch (IOException e) {
                if (stopped) break;
                throw new RuntimeException("Error Accepting Game Connection", e);
            }
        }
    }

    private void startServer() {
        try {
            this.stopped = false;
            this.serverSocket = new ServerSocket(Configuration.PORT);
            this.serverSocket.setSoTimeout(100);
            System.out.println("Server Started At Address: " + InetAddress.getLocalHost());
        } catch (IOException e) {
            this.stopped = true;
            throw new RuntimeException("Cannot open port " + Configuration.PORT, e);
        }
    }

    void stop() {
        this.stopped = true;
        try {
            this.cs_rwl.readLock();
            for (SocketIO socketIO: clientSockets.values()) {
                socketIO.close();
            }
            this.cs_rwl.readUnlock();
            this.serverSocket.close();
            this.cs_rwl.writeLock();
            this.clientSockets.clear();
            this.cs_rwl.writeUnlock();
            System.out.println("Server Socket Closed");
        } catch (IOException e) {
            throw new RuntimeException("Error Closing Server", e);
        } catch (InterruptedException e){}
    }

    void removeSocket(int id) {
        try {
            this.cs_rwl.writeLock();
            this.clientSockets.remove(id);
            this.cs_rwl.writeUnlock();
        } catch (InterruptedException e){}
    }

    void appendInbox(Message m) {
        try {
            mi_rwl.writeLock();
            messageInbox.offer(m);
            mi_rwl.writeUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Message getMessage() {
        Message message = Message.getNULL();
        try {
            mi_rwl.readLock();
            message = messageInbox.pop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mi_rwl.readUnlock();
        }
        return message;
    }

    int inboxSize() {
        int val = 0;
        try {
            mi_rwl.readLock();
            val = messageInbox.size();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mi_rwl.readUnlock();
        }
        return val;
    }

    void sendMessage(Message m) {
        sendMessage(m.id, m.message);
    }

    void sendMessage(int id, char message) {
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

    void broadcastMessage(char message) {
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

    void broadcastMessage(HashMap<Integer, Player> players, char message) {
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