package Utility;

public class Commands {
    // Utility Commands
    public final static char Start = 'S';       // ゲーム開始時に送り合う
    public final static char Connect = 'C';     // 接続時にクライアントからサーバーへ送られる
    public final static char Disconnect = 'D';  // 接続が途切れたときにクライアントやサーバーが受け取る
    public final static char Game = 'G';        // 1ラウンドが開始されるときにサーバーからクライアントへ送られる
    public final static char Error = 'E';       // エラーが発生したときに送り合う
    public final static char Finish = 'F';

    // Game Commands
    public final static char Rock = 'g';
    public final static char Paper = 'p';
    public final static char Scissors = 'c';
    public final static char EmptyHand = 'e';

    public final static char Win = 'w';
    public final static char Lose = 'l';
    public final static char Draw = 'd';

    public final static char Champion = 'c';

    public final static char[] Hands = {Rock, Paper, Scissors};
    public static boolean isHand(char c) {
        for (char hand : Hands) {
            if (hand == c) return true;
        }
        return false;
    }
}
