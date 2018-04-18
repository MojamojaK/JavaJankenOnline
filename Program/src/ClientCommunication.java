// ClientCommunication without thread
import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ClientCommunication{

    private InetAddress addr = InetAddress.getByName("localhost");
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    private LinkedList<Character> inbox = new LinkedList<>();

    private boolean stopped = true;

    ClientCommunication (String args[]) throws IOException{
        if (args.length > 0) addr = InetAddress.getByName(args[0]);
        try {
            System.out.println("addr = " + addr);
            socket = new Socket(addr, 8080);
            System.out.println("socket = " + socket);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            stopped = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            while (in.ready()) {
                appendInbox((char)in.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            stopped = true;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void appendInbox(char message) {
        inbox.offer(message);
    }

    public int inboxSize() {
        return inbox.size();
    }

    public char getMessage() {
        return inbox.poll();
    }

    public synchronized void sendMessage(char message) {
        out.print(message);
    }
}
