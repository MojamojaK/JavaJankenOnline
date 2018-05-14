// ClientCommunication using Thread
package Client;

import Utility.Configuration;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ClientCommunication implements Runnable{

    private InetAddress addr = InetAddress.getByName("localhost");
    private Socket socket;

    private BufferedReader in;
    private BufferedWriter out;

    private LinkedList<Character> inbox = new LinkedList<>();

    private boolean stopped = true;

    public ClientCommunication (String args[]) throws IOException{
        if (args.length > 0) addr = InetAddress.getByName(args[0]);
        try {
            socket = new Socket(addr, Configuration.PORT);
            System.out.println("Client Communication: ADDR: " + addr + ":" + Configuration.PORT);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stopped = false;
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!stopped) {
            try {
                int d = in.read();
                if (d == -1) {
                    close();
                } else {
                    appendInbox((char)d);
                }
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (!stopped) {
            stopped = true;
            System.out.println("Server Connection Closed");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void appendInbox(char message) {
        inbox.offer(message);
    }

    int inboxSize() {
        return inbox.size();
    }

    synchronized char getMessage() {
        return inbox.pop();
    }

    synchronized void sendMessage(char message) {
        //System.out.println("Sending: \'" + message + "\'");
        try {
            out.write((int) message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean closed() {
        return stopped;
    }
}