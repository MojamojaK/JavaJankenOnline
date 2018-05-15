package Client;

import java.io.*;
import java.util.*;

public class ScannerRunnable implements Runnable {

    public BufferedReader reader;
    private boolean stopped = false;
    public LinkedList<Character> inbox = new LinkedList<>();

    public ScannerRunnable (InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        new Thread(this).start();
    }

    public void run () {
        while (!stopped) {
            try {
                if (this.reader.ready()) {
                    char c = (char) this.reader.read();
                    addChar(c);
                }
            } catch (IOException e) {}

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    public synchronized int inboxSize() {
        return inbox.size();
    }

    public synchronized void addChar(char c) {
        inbox.add(c);
    }

    public synchronized char getChar() {
        return inbox.pollLast();
    }

    public void stop() {
        stopped = true;
    }

    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {}
    }
}
