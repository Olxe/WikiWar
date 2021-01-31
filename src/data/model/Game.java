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
        this.host = host;
    }

    public void addPlayer(Player player) {
        if(!this.players.containsKey(player.getPseudo())) {
            this.players.put(player.getPseudo(), player);
        }
    }

    public HashMap<String, Player> getPlayers() {
        return this.players;
    }

    public String getHost() {
        return host;
    }

    public String getTitleStart() {
        return titleStart;
    }

    public String getTitleEnd() {
        return titleEnd;
    }

    public void setTitleEnd(String titleEnd) {
        this.titleEnd = titleEnd;
    }
}
