import java.util.Scanner;

public class Client2 {
	public static void main(String[] args) {
		Client clientdata = new Client();
		while(true) {
			System.out.println("グーチョキパーどれを出す？");
			Scanner sc = new Scanner(System.in);
			clientdata.hand = sc.nextLine();

			if(clientdata.str.equals("win")) System.out.println("勝ちました。次のじゃんけんに進みます。");
			else if(clientdata.str.equals("lose")) {
				System.out.println("残念。負けました。");
				break;
			} else {
				System.out.println("おめでとう！優勝です！");
				break;
			}
		}
	}
}
