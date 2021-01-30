package data.model;

import java.util.HashMap;

public class Game {
    private String host;
    private String titleStart;
    private String titleEnd;
    private HashMap<String, Player> players = new HashMap<>();

    public Game(String host, String titleStart, String titleEnd) {
        this.titleStart = titleStart;
        this.titleEnd = titleEnd;
    }

    public void addPlayer(Player player) {
        if(!this.players.containsKey(player.getPseudo())) {
            this.players.put(player.getPseudo(), player);
        }
    }
}
