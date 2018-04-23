package Server;

import Utility.Commands;
import Utility.Message;

import java.io.*;
import java.net.Socket;

public class SocketIO implements Runnable{

    private static int new_id = 0;

    private int id;
    private ServerCommunications comm;
    Socket socket;

    BufferedReader in;
    BufferedWriter out;

    private boolean opened = true;

    SocketIO(ServerCommunications comm, Socket socket) {
        id = new_id++;
        this.comm = comm;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (opened) {
            try {
                //System.out.println("Start Reading #" + id + "...");
                int d = in.read();
                //System.out.println("Done  Reading #" + id + "...");
                if (d == -1) {
                    close();
                } else {
                    System.out.println("#" + id + " -> Server: " + ((char)d));
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
        System.out.println("Server -> #" + id + ": " + message);
        try {
            out.write((int) message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
        System.out.println("Client #" + id + " Closed Connection");
        try {
            comm.cs_rwl.writeLock();
            comm.clientSockets.remove(id);
            comm.cs_rwl.writeUnlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        comm.appendInbox(new Message(id, Commands.Disconnect));
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
