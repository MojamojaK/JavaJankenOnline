public class GameManager {
    public static void main(String[] args) {
        ServerCommunications comm = new ServerCommunications();
        while (true) {
            if (comm.inboxSize() > 0) {
                Message m = comm.getMessage();
                System.out.println("id: " + m.id + ", message: " + m.message);
            }
        }
    }
}
