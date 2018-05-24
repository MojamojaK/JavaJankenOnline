package Client;

import Utility.*;
import java.io.*;

public class GameGUI {

    private ClientCommunication comm;
    private GUI gameGUI;

    private boolean game_start = false;
    private boolean round_start = false;

    private GameGUI (String[] args) throws IOException {
        comm = new ClientCommunication(args);
        comm.sendMessage(Commands.Connect);
        gameGUI = new GUI("ジャンケン");  //new gameGUI(System.in)
        lobby();
        game();
        comm.close();
    }

    private void lobby () {
        System.out.println("Connected to Server!");
        System.out.println("Welcome to Java Janken Online");
        System.out.println("Press \"S\" to start game");
        System.out.println("Minimum Required Players: 2");
        while(!game_start && comm.opened()) {
            gameGUI.setVisible(true);
            if (comm.inboxSize() > 0) {
                char message = comm.getMessage();
                //System.out.println(message);
                if (message == Commands.Start) {
                    game_start = true;
                } else if (message == Commands.Error) {
                    System.out.println("Unknown Error (Min Players: 2)");
                } else {
                    System.out.println("Unknown Command Received: " + message);
                }
            }
            if (gameGUI.inboxSize() > 0){
                /*try {
                    frame = new NewGUI2("ジャンケン");
                    frame.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //gameGUI = new GUI("ジャンケン");
                //gameGUI.setVisible(true);
                char input = gameGUI.getChar();          //何を出すかを入力
                if (input != '\n' && input != ' ') {
                    comm.sendMessage(input);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void game () {
        System.out.println("Starting Game");
        while (game_start && comm.opened()) {
            if (comm.inboxSize() > 0) {
                char message = comm.getMessage();
                if (round_start) {
                    if (message == Commands.Lose) {
                        System.out.println("You lose 負け");      //loseが送られてきたら終わり
                        round_start = false;
                    } else if (message == Commands.Win) {
                        System.out.println("You win 勝ち");
                        round_start = false;
                    } else if (message == Commands.Draw) {
                        System.out.println("Draw 引き分け");
                        round_start = false;
                    } else {
                        System.out.println("Unknown Command Received : " + message);
                    }
                } else {
                    if (message == Commands.Game) {
                        System.out.println("New Game. Select A Hand. 手を選択してください!");
                        round_start = true;
                    } else if (message == Commands.Champion) {
                        System.out.println("Game Over. You're the winner! おめでとうございます! あなたの勝ちです!");
                        game_start = false;
                        System.exit(0);
                    } else if (message == Commands.Finish) {
                        System.out.println("Game Over. You lose! あなたの負けです!");
                        game_start = false;
                        System.exit(0);
                    }
                }
            } else if (gameGUI.inboxSize() > 0){
                char input = (char)gameGUI.getChar();
                comm.sendMessage(input);
            }
        }
    }

    public static void main(String[] args) throws IOException{
        GameGUI game = new GameGUI(args);
    }
}

