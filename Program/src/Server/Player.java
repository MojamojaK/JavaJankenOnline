package Server;

class Player {

    private int id = 0;
    private int score = 0;

    private final static char EmptyHand = 'e';

    private char hand = EmptyHand;

    Player (int id) {
        this.id = id;
        resetHand();
    }

    int getId () {
        return id;
    }

    void addScore (int i) {
        score += 1;
    }

    void addScore () {
        addScore(1);
    }

    int getScore () {
        return score;
    }

    boolean setHand (char c) {
        if (hand == EmptyHand) {
            hand = c;
            return true;
        }
        return false;
    }

    char getHand () {
        return hand;
    }

    void resetHand () {
        setHand(EmptyHand);
    }
}
