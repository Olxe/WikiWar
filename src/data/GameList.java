package data;

import data.model.Game;

import java.util.HashMap;

public class GameList {
    //Code + game obj
    private HashMap<String, Game> games = new HashMap<>();

    private GameList() {}

    private static GameList instance = new GameList();

    public static GameList getInstance() {
        return instance;
    }

    public HashMap<String, Game> getRooms() {
        return this.games;
    }
}
