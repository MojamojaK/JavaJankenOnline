package Utility;

public class Message {
    public int id;
    public char message;

    private static final Message NULL = new Message(-1, 'N');

    public Message(int id, char message) {
        this.id = id;
        this.message = message;
    }

    public static Message getNULL() {
        return NULL;
    }
}
