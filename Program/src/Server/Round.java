package Server;

import Utility.*;

import java.util.*;

class Round {

    private static boolean playing = false;
    private static int round_count = 0;

    private long time;
    private HashMap<Integer, Player> original_players;
    private HashMap<Integer, Player> players;
    private ServerCommunications comm;

    private int g = 0;
    private int c = 0;
    private int p = 0;

    static int count() {
        return round_count;
    }


    Round(ServerCommunications comm, HashMap<Integer, Player> players) {
        this.comm = comm;
        this.original_players = players;
        // プレイヤーセットを単純にコピーする
        for (Map.Entry<Integer, Player> e : players.entrySet()) {
            this.players.put(e.getKey(), e.getValue());
        }
        time = System.currentTimeMillis();
        comm.broadcastMessage(this.players, Commands.Game);
        for (Map.Entry<Integer, Player> e : this.players.entrySet()) e.getValue().resetHand();
        g = 0;
        c = 0;
        p = 0;
        playing = true;
        round_count++;
    }

    void startLoop() {
        while (Round.playing) {
            if (comm.inboxSize() > 0) {
                Message m = comm.getMessage();
                if (Commands.isHand(m.message)) {
                    if (players.get(m.id).setHand(m.message)) {
                        if (m.message == Commands.Rock)             g++;
                        else if (m.message == Commands.Scissors)    c++;
                        else if (m.message == Commands.Paper)       p++;
                    }
                } else if (m.message == Commands.Connect) {              // プレイ中に接続されたら
                    original_players.put(m.id, new Player(m.id));        // プレイヤーの追加(現ラウンドにはまだ参加しない)
                    comm.sendMessage(new Message(m.id, Commands.Start)); // 既に始まっているので開始の合図を送る
                }
            }
            checkRound();
        }
    }

    private void checkRound() {
        if (g + c + p < this.players.size()) return;                        // 全員がまだ手を出してない
        playing = false;                                                    // 全員出てるのでとりあえずラウンド終了
        int size = this.players.size();                                         // ラウンド参加人数の計測
        if ((g > 0 && c > 0 && p > 0) || g == size || c == size || p == size) { // 引き分けの条件
            comm.broadcastMessage(this.players, Commands.Draw);                 // 引き分けメッセージの配信
        }
        else {
            if (g == 0) { // グー無し チョキの勝ち　パーの負け
                for (Map.Entry<Integer, Player> e : this.players.entrySet()) {
                    Player player = e.getValue();
                    if (player.getHand() == Commands.Scissors) {
                        comm.sendMessage(player.getId(), Commands.Win);
                        player.addScore();
                    }
                    else if (player.getHand() == Commands.Paper) comm.sendMessage(player.getId(), Commands.Lose);
                }
            } else if (c == 0) { // チョキ無し　パーの勝ち　グーの負け
                for (Map.Entry<Integer, Player> e : this.players.entrySet()) {
                    Player player = e.getValue();
                    if (player.getHand() == Commands.Paper) {
                        comm.sendMessage(player.getId(), Commands.Win);
                        player.addScore();
                    }
                    else if (player.getHand() == Commands.Rock) comm.sendMessage(player.getId(), Commands.Lose);
                }
            } else if (p == 0) { // パー無し　グーの勝ち　チョキの負け
                for (Map.Entry<Integer, Player> e : this.players.entrySet()) {
                    Player player = e.getValue();
                    if (player.getHand() == Commands.Rock) {
                        comm.sendMessage(player.getId(), Commands.Win);
                        player.addScore();
                    }
                    else if (player.getHand() == Commands.Scissors) comm.sendMessage(player.getId(), Commands.Lose);
                }
            }
        }
    }
}
