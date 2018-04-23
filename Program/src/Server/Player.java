package Server;

import Utility.Commands;

class Player {

    private int id = 0;
    private int score = 0;

    private char hand = Commands.EmptyHand;

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
        if (hand == Commands.EmptyHand) {
            hand = c;
            System.out.println("Player " + id + " set hand to :" + hand);
            return true;
        }
        return false;
    }

    char getHand () {
        return hand;
    }

    void resetHand () {
        hand = Commands.EmptyHand;
    }
}
