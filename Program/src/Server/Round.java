package Server;

import Utility.*;

import java.util.*;

class Round {

    private static boolean playing = false;
    private static int round_count = 0;

    private long time;
    private HashMap<Integer, Player> players;
    private ServerCommunications comm;

    private int g, c, p;

    static int count() {
        return round_count;
    }


    Round(ServerCommunications comm, HashMap<Integer, Player> parent_players) {
        this.comm = comm;
        this.players = new HashMap<>();
        // プレイヤーセットを単純にコピーする
        for (Map.Entry<Integer, Player> e : parent_players.entrySet()) {
            this.players.put(e.getKey(), e.getValue());
        }
        time = System.currentTimeMillis();
        comm.broadcastMessage(this.players, Commands.Game);
        for (Map.Entry<Integer, Player> e : this.players.entrySet()) {
            e.getValue().resetHand();
        }
        g = 0;
        c = 0;
        p = 0;
        playing = true;
        round_count++;

        System.out.println("Starting Round #" + count());
        while (Round.playing) {
            if (comm.inboxSize() > 0) {
                Message m = comm.getMessage();
                if (!this.players.containsKey(m.id)) continue;
                System.out.println("Round got message: #" + m.id + ": " + m.message);
                if (Commands.isHand(m.message)) {
                    if (this.players.get(m.id).setHand(m.message)) {
                        if (m.message == Commands.Rock) g++;
                        else if (m.message == Commands.Scissors) c++;
                        else if (m.message == Commands.Paper) p++;
                    }
                } else if (m.message == Commands.Connect) {                             // プレイ中に接続されたら
                    System.out.println("Established Connection with Client #" + m.id);
                    parent_players.putIfAbsent(m.id, new Player(m.id));                   // プレイヤーの追加(現ラウンドにはまだ参加しない)
                    comm.sendMessage(new Message(m.id, Commands.Start));                    // 既に始まっているので開始の合図を送る
                } else if (m.message == Commands.Disconnect) {
                    System.out.println("Client #" + m.id + " disconnected");
                    parent_players.remove(m.id);
                    if (this.players.containsKey(m.id)) {
                        char player_hand = players.get(m.id).getHand();
                        if (player_hand != Commands.EmptyHand) {
                            if (player_hand == Commands.Rock) g--;
                            else if (player_hand == Commands.Scissors) c--;
                            else if (player_hand == Commands.Paper) p--;
                        }
                        this.players.remove(m.id);
                    }
                    if (this.players.size() < 2) {
                        Round.playing = false;
                        System.out.println("Round Canceled");
                    }
                } else {
                    System.out.println("Unknown Command Received : " + m.message);
                }
                checkRound();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkRound() {
        showStatus();
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

    private void showStatus() {
        System.out.println("=====Status=====");
        System.out.println("g = " + g);
        System.out.println("c = " + c);
        System.out.println("p = " + p);
        System.out.println("players = " + this.players.size());
        System.out.println("================");
    }
}
