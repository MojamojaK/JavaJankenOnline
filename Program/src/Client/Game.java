package Client;

import Utility.*;
import java.io.*;
import java.util.Scanner;

public class Game {

    private ClientCommunication comm;
    private ScannerRunnable scanner;

    private boolean game_start = false;
    private boolean round_start = false;

    private Game (String[] args) throws IOException {
        comm = new ClientCommunication(args);
        comm.sendMessage(Commands.Connect);
        scanner = new ScannerRunnable(new Scanner(System.in));
        lobby();
        game();
        comm.close();
        scanner.stop();
        scanner.close();
    }

    private void lobby () {
        System.out.println("Connected to Server!");
        System.out.println("Welcome to Java Janken Online");
        System.out.println("Press \"S\" to start game");
        System.out.println("Minimum Required Players: 2");
        while(!game_start && !comm.closed()) {
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
            if (scanner.inboxSize() > 0){
                char input = scanner.getChar();          //何を出すかを入力
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
        while (game_start && !comm.closed()) {
            if (comm.inboxSize() > 0) {
                char message = comm.getMessage();
                if (round_start) {
                    if (message == 'l') {
                        System.out.println("You lose");      //loseが送られてきたら終わり
                        round_start = false;
                    } else if (message == 'w') {
                        System.out.println("You win");
                        round_start = false;
                    } else if (message == 'd') {
                        System.out.println("Draw");
                        round_start = false;
                    } else if (message == 'c') {
                        System.out.println("Game Over. You're the winner!");
                        round_start = false;
                        game_start = false;
                    } else if (message == 'f') {
                        System.out.println("Game Over. You lose!");
                        round_start = false;
                        game_start = false;
                    } else {
                        System.out.println("Unknown Command Received : " + message);
                    }
                } else {
                    if (message == Commands.Game) {
                        System.out.println("New Game (\'g\'=\"グー\", \'c\'=\"チョキ\", \'p\'=\"パー\")");
                        round_start = true;
                    }
                }
            } else if (scanner.inboxSize() > 0){
                char input = (char)scanner.getChar();
                comm.sendMessage(input);
            }
        }
    }

    public static void main(String[] args) throws IOException{
        Game game = new Game(args);
    }
}

