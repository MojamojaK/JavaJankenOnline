package Server;

import Utility.Message;

import java.io.*;
import java.net.Socket;

public class SocketIO implements Runnable{

    private static int new_id = 0;

    private int id;
    private ServerCommunications comm;
    Socket socket;

    BufferedReader in;
    PrintWriter out;

    private boolean opened = true;

    SocketIO(ServerCommunications comm, Socket socket) {
        id = new_id++;
        this.comm = comm;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (opened) {
            try {
                System.out.println("Start Reading #" + id + "...");
                int d = in.read();
                System.out.println("Done  Reading #" + id + "...");
                if (d == -1) {
                    close();
                } else {
                    comm.appendInbox(new Message(id, (char) d));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    int getId() {
        return id;
    }

    void push(char message) {
        System.out.println("Server #" + id + " wrote: " + message);
        out.print(message);
    }

    void close() {
        System.out.println("#" + id + " Closed");
        try {
            if (opened) {
                in.close();
                out.close();
                socket.close();
                opened = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
