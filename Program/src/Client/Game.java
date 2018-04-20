package Client;

import Utility.*;
import java.io.*;
import java.util.Scanner;

public class Game {

    private ClientCommunication comm;
    private ScannerRunnable scanner;

    private boolean game_start = false;

    private Game (String[] args) throws IOException {
        comm = new ClientCommunication(args);
        comm.sendMessage(Commands.Connect);
        scanner = new ScannerRunnable(new Scanner(System.in));
        lobby();
        /*while (true) {
            if (comm.inboxSize() > 0) {
                char message = comm.getMessage();
                if(message == 'l') {
                    System.out.println("You lose");      //loseが送られてきたら終わり
                }else if(message == 'w'){
                    System.out.println("You win");
                }else if(message == 'd'){
                    System.out.println("Draw");
                }else if(message == 'c'){
                    System.out.println("Game Over. You're the winner!");
                    break;
                }else if(message == 'f'){
                    System.out.println("Game Over. You lose!");
                    break;
                }
            } else if (sc.hasNext()){
                char input = (char)sc.nextByte();          //何を出すかを入力
                comm.sendMessage(input);
            }
        }*/
        comm.close();
        scanner.stop();
        scanner.close();
    }

    private void lobby () {
        System.out.println("Connected to Server!");
        System.out.println("Welcome to Java Janken Online");
        System.out.println("Press \"S\" to start game");
        System.out.println("Minimum Required Players: 2");
        while(!game_start) {
            if (comm.inboxSize() > 0) {
                char message = comm.getMessage();
                System.out.println(message);
                if (message == Commands.Start) {
                    System.out.println("Starting Game");
                    game_start = true;
                } else if (message == Commands.Error) {
                    System.out.println("Unknown Error (Min Players: 2)");
                } else {
                    System.out.println("Unknown Command Received");
                }
            }
            if (scanner.inboxSize() > 0){
                char input = scanner.getChar();          //何を出すかを入力
                if (input != '\n' && input != ' ') {
                    comm.sendMessage(input);
                }
            }
        }
        System.out.println("Loop out");
    }

    public static void main(String[] args) throws IOException{
        Game game = new Game(args);
    }
}

