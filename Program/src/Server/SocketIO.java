package Server;

import Utility.Commands;
import Utility.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
            this.socket.setSoTimeout(100);
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                new Thread(this).start();
            } catch (IOException e) {}
        } catch (SocketException e) {}
    }

    public void run() {
        while (opened) {
            boolean retry = true;
            try {
                while (retry) {
                    try {
                        int d = in.read();
                        retry = false;
                        if (d == -1) {
                            close();
                            comm.removeSocket(id);
                        } else {
                            System.out.println("#" + id + " -> Server: " + ((char) d));
                            comm.appendInbox(new Message(id, (char) d));
                        }
                    } catch (SocketTimeoutException e) { }
                }
            } catch (IOException e) {}
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
        } catch (IOException e) {}
    }

    synchronized void close() {
            try {
                if (opened) {
                    opened = false;
                    comm.appendInbox(new Message(id, Commands.Disconnect));
                    push(Commands.Disconnect);
                    in.close();
                    out.close();
                    socket.close();
                    System.out.println("Client #" + id + " Closed Connection");
                }
            } catch (IOException e) {}
    }
}
