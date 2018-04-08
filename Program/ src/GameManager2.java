import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class GameManager

        public static void main(String[] args) {
            int i = 0;
            int g, c, p;//グー、チョキ、パーの変数
            int[] player = {0, 0, 0};//playerの変数
            String[] janken = new String[3];
            ServerCommunications comm = new ServerCommunications();

            while (true) {
                System.out.println("Start");
                if (comm.inboxSize() > 0) {
                    Message m = comm.getMessage();
                    System.out.println("id: " + m.id + ", message: " + m.message);
                    janken[m.id] = m.message;//idとメッセージを読み込み
                    i++;
                    if (i % 3 == 0) {//全員ジャンケン入力した場合
                        g = 0;
                        c = 0;
                        p = 0;
                        for (int j = 0; j < 3; j++) {
                            if (janken[j] == "g") g += 1;
                            else if (janken[j] == "c") c += 1;
                            else p += 1;
                        }
                        //一人勝ち
                        if (g == 1 && c == 2) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "g") {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                } else sendMessage(j + 1, "lose");
                            }
                        } else if (c == 1 && p == 2) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "c") {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                } else sendMessage(j + 1, "lose");
                            }
                        } else if (p == 1 && g == 2) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "p") {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                } else sendMessage(j + 1, "lose");
                            }
                        }
                        //一人負け
                        else if (c == 1 && g == 2) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "c") sendMessage(j + 1, "lose");
                                else {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                }
                            }
                        } else if (c == 2 && p == 1) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "p") sendMessage(j + 1, "lose");
                                else {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                }
                            }
                        } else if (g == 1 && p == 2) {
                            for (int j = 0; j < 3; j++) {
                                if (janken[j] == "g") sendMessage(j + 1, "lose");
                                else {
                                    sendMessage(j + 1, "win");
                                    player[j] += 1;
                                }
                            }
                        }
                        //平局
                        else for (int j = 0; j < 3; j++) {
                                sendMessage(j + 1, "draw");
                            }
                    }
                    //ある人は10点を取った場合　終わり
                    for (int j = 0; j < 3; j++) {
                        if (player[j] == 10) {
                            System.out.println("Game Over. Player" + (j + 1) + "is winner!");
                            break;
                        }
                    }


                }
            }
        }

