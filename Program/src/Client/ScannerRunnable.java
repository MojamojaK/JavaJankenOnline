package Client;

import java.util.*;

public class ScannerRunnable implements Runnable {

    public Scanner sc;
    private boolean stopped = false;
    public LinkedList<Character> inbox = new LinkedList<>();

    public ScannerRunnable (Scanner sc) {
        this.sc = sc;
        new Thread(this).start();
    }

    public void run () {
        while (!stopped) {
            char c = sc.next().charAt(0);
            addChar(c);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
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
        this.sc.close();
    }
}
