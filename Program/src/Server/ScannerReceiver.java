package Server;

import Utility.Message;

import java.util.LinkedList;
import java.util.Scanner;

public class ScannerReceiver implements Runnable {

    Scanner sc;
    ServerCommunicationRunnable scr;
    ServerCommunications comm;

    boolean stopped = false;

    public ScannerReceiver (Scanner sc, ServerCommunicationRunnable scr, ServerCommunications comm) {
        this.sc = sc;
        this.scr = scr;
        this.comm = comm;
    }

    public void run () {
        stopped = false;
        while (!stopped) {
            System.out.println("c" + Thread.currentThread().getId());
            char c = sc.next().charAt(0);
            System.out.println("Received: " + c);
            comm.appendInbox(new Message(this.scr.getId(), c));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        stopped = true;
    }
}
