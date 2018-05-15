package Server;

import java.util.*;
import Utility.*;

public class GameManager {

    private final static int win_threshold = 3; // クリアするために必要な勝利回数

    private ServerCommunications comm;
    private boolean game_running = false;

    private HashMap<Integer, Player> players = new HashMap<>();

    private GameManager() {
         comm = new ServerCommunications();
    }

    // 全員の接続待ち
    private void lobby() {
        System.out.println("Entered Lobby");
        while (!game_running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Inbox Size = " + comm.inboxSize());
            if (comm.inboxSize() > 0) {                     // メッセージが届いたとき
                Message m = comm.getMessage();
                if (m.message == Commands.Connect) {            // あるプレイヤーから 'C'が届く（接続が完了した）
                    players.putIfAbsent(m.id, new Player(m.id));    // 該当プレイヤーの追加
                    System.out.println("Established Connection with Client #" + m.id);
                }
                else if (m.message == Commands.Start) {         // あるプレイヤーから 'S'が届く（開始の合図）
                    if (player_num() > 1) {                         // プレイヤーの数が「有効」なとき
                        game_running = true;                            // ゲームを開始する（lobbyから抜ける）
                    } else {                                        // プレイヤーの数が「無効」なとき
                        comm.sendMessage(m.id, Commands.Error);         // コマンドの送信者にエラーメッセージを送る
                    }
                } else if (m.message == Commands.Disconnect) {
                    System.out.println("Client #" + m.id + " disconnected");
                    players.remove(m.id);
                } else if (m.message != Commands.Error){        // その他のメッセージを貰ったとき(エラー以外)
                    comm.sendMessage(m.id, Commands.Error);         // コマンドの送信者にエラーメッセージを送る
                }
            }
        }
    }

    private void game() {
        System.out.println("Starting Game");
        comm.broadcastMessage(Commands.Start); // 全プレイヤーに開始の合図を送る
        while (game_running) {
            if (this.player_num() >= 2) {
                Round round = new Round(comm, players);       // 現時点でのプレイヤーでラウンド開始
                HashMap<Integer, Player> champions = getChampions();
                if (champions.size() > 0) {
                    for (Map.Entry<Integer, Player> e : champions.entrySet()) {
                        Player player = e.getValue();
                        comm.sendMessage(player.getId(), Commands.Champion);
                    }
                    comm.broadcastMessage(Commands.Finish);
                    game_running = false;
                }
            } else { // ラウンドが始まっていないとき用の通信
                if (comm.inboxSize() > 0) {                     // メッセージが届いたとき
                    Message m = comm.getMessage();
                    if (m.message == Commands.Connect) {            // あるプレイヤーから 'C'が届く（接続が完了した）
                        players.putIfAbsent(m.id, new Player(m.id));    // 該当プレイヤーの追加
                        comm.sendMessage(new Message(m.id, Commands.Start));                    // 既に始まっているので開始の合図を送る
                        System.out.println("Established Connection with Client #" + m.id);
                    } else if (m.message == Commands.Disconnect) {
                        System.out.println("Client #" + m.id + " disconnected");
                        players.remove(m.id);
                    } else if (m.message != Commands.Error){        // その他のメッセージを貰ったとき(エラー以外)
                        comm.sendMessage(m.id, Commands.Error);         // コマンドの送信者にエラーメッセージを送る
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    private void close() {
        comm.stop();
        System.out.println("Closed Game");
    }

    // 参加しているプレイヤー数を返す
    private int player_num() {
        return players.size();
    }

    private HashMap<Integer, Player> getChampions() {
        HashMap<Integer, Player> champions = new HashMap<>();
        for (Map.Entry<Integer, Player> e: players.entrySet()) {
            Player player = e.getValue();
            if (player.getScore() >= win_threshold) {
                champions.put(player.getId(), player);
            }
        }
        return champions;
    }

    public static void main(String[] args) {
        GameManager gm = new GameManager();
        gm.lobby();
        gm.game();
        gm.close();
    }
}
