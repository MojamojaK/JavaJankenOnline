package Utility;

public final class ReadWriteLock {
    private int readingReaders = 0;
    private int waitingWriters = 0;
    private int writingWriters = 0;
    private boolean preferWriter = true;

    public synchronized void readLock() throws InterruptedException {
        //System.out.println("Start readLock() by " + Thread.currentThread());
        while (writingWriters > 0 || (preferWriter && waitingWriters > 0)) {
            wait();
        }
        readingReaders++;
        //System.out.println("Done  readLock() by " + Thread.currentThread());
    }

    public synchronized void readUnlock() {
        //System.out.println("Start readUnlock() by " + Thread.currentThread());
        readingReaders--;
        preferWriter = true;
        notifyAll();
        //System.out.println("Done  readUnlock() by " + Thread.currentThread());
    }

    public synchronized void writeLock() throws InterruptedException {
        //System.out.println("Start writeLock() by " + Thread.currentThread());
        waitingWriters++;
        try {
            while (readingReaders > 0 || writingWriters > 0) {
                wait();
            }
        } finally {
            waitingWriters--;
        }
        writingWriters++;
        //System.out.println("Done  writeLock() by " + Thread.currentThread());
    }

    public synchronized void writeUnlock() {
        //System.out.println("Start writeUnlock() by " + Thread.currentThread());
        writingWriters--;
        preferWriter = false;
        notifyAll();
        //System.out.println("Done  writeUnlock() by " + Thread.currentThread());
    }
}