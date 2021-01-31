package data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Player {
    private String pseudo;
    private int point = -1;
    private LinkedHashMap<String, String> pageVisited = new LinkedHashMap<>();

    public Player(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void increment() {
        this.point++;
    }

    public int getPoint() {
        return point;
    }

    public void addVisitedPage(String title, String link) {
        this.pageVisited.put(title, link);
    }

    public HashMap<String, String> getVisistedPage() {
        return this.pageVisited;
    }
}
