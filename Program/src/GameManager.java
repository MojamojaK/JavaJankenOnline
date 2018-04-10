import java.io.*;
import java.util.*;

public class GameManager {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Player Number:");
        int num = in.nextInt();
        int i = 0;
        int g, c, p;//グー、チョキ、パーの変数
        int[] player = new int[num];//playerの変数
        Character[] janken = new Character[num];
        ServerCommunications comm = new ServerCommunications();
        for (int j = 0; j < num; j++) {
            player[j] = 0;
        }
        while (true) {

            if (comm.inboxSize() > 0) {
                Message m = comm.getMessage();
                System.out.println("id: " + m.id + ", message: " + m.message);
                janken[m.id] = m.message; //idとメッセージを読み込み
                i++;
                if (i % num == 0) {//全員ジャンケン入力した場合
                    g = 0;
                    c = 0;
                    p = 0;
                    for (int j = 0; j < num; j++) {
                        if (janken[j] == 'g') g += 1;
                        else if (janken[j] == 'c') c += 1;
                        else p += 1;
                    }

                    if (g == 0 || c == 0 || p == 0) {
                        if (g == 0) {
                            for (int j = 0; j < num; j++) {
                                if (janken[j] == 'c') {
                                    comm.sendMessage(j + 1, 'w'); // messageの型はcharです win
                                    player[j] += 1;
                                } else comm.sendMessage(j + 1, 'l'); // lose
                            }
                        } else if (c == 0) {
                            for (int j = 0; j < num; j++) {
                                if (janken[j] == 'p') {
                                    comm.sendMessage(j + 1, 'w'); // win
                                    player[j] += 1;
                                } else comm.sendMessage(j + 1, 'l'); // lose
                            }
                        } else if (p == 0) {
                            for (int j = 0; j < num; j++) {
                                if (janken[j] == 'g') {
                                    comm.sendMessage(j + 1, 'w'); // win
                                    player[j] += 1;
                                } else comm.sendMessage(j + 1, 'l'); // lose
                            }
                        }
                    }

                    //平局
                    else for (int j = 0; j < num; j++) {
                        comm.sendMessage(j + 1, 'd'); // draw
                    }
                }
                //ある人は10点を取った場合　終わり
                for (int j = 0; j < num; j++) {
                    if (player[j] == 10) {
                        System.out.println("Game Over. Player" + (j + 1) + "is winner!");
                        break;
                    }
                }
            }
        }
    }
}
