import java.util.*;

public class ServerCommunications {
    private LinkedList<Message> messageInbox = new LinkedList<>();
    private LinkedList<Message> messageOutbox = new LinkedList<>();

    private void appendInbox(Message m) {
        messageInbox.offer(m);
    }

    public Message getMessage() {
        return messageInbox.poll();
    }

    public int inboxSize() {
        return messageInbox.size();
    }

    public void sendMessage(Message m) {
        messageOutbox.offer(m);
    }

    public void sendMessage(int id, char message) {
        sendMessage(new Message(id, message));
    }
}
