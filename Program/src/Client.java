import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
  public static void main(String[] args) throws IOException{
		ClientCommunication comm = new ClientCommunication(args);
		while (true) {
			comm.update();
			Scanner sc=new Scanner(System.in);
			char hand = (char)sc.nextByte();          //何を出すかを入力
			comm.sendMessage(hand);
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
			}
		}
		comm.sendMessage('e');
		comm.close();
  }
}
