package Server;

import Utility.Message;
import Client.ScannerRunnable;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerCommunicationRunnable implements Runnable {
    private ServerCommunications communication;
    private Socket socket;

    private LinkedList<Character> outbox = new LinkedList<>();

    private static int next_id = 0;
    private final int id;

    private boolean stopped = true;

    private Scanner in;
    private ScannerReceiver sr;
    private PrintWriter out;

    ServerCommunicationRunnable(ServerCommunications communication, Socket socket) {
        this.communication = communication;
        this.socket = socket;
        id = next_id++;
        System.out.println("Created New instance of ServerCommunicationRunnable #" + id);

    }

    public void run() {
        try {
            in = new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            sr = new ScannerReceiver(in, this, communication);
            new Thread(sr).start(); // 受信を行うスレッド
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        } catch (IOException e) {
            throw new RuntimeException("Buffered Reader Error on thread #" + Thread.currentThread(), e);
        }
        this.stopped = false;
        while (!this.stopped) {
            sendMessage();
            if (!socket.isConnected()) {
                break;
            }
        }
        stop();
    }

    void sendMessage() {
        if (outbox.size() > 0) {
            char message = outbox.poll();
            out.print(message);
        }
    }

    synchronized int getId() {
        return id;
    }

    synchronized void push(char message) {
        System.out.println("Server #" + id + " added: " + message + " to outbox");
        outbox.push(message);
    }

    public synchronized void stop() {
        if (!stopped) {
            stopped = true;
            try {
                sr.stop();
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
