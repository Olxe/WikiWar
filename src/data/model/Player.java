package data.model;

public class Player {
    private String pseudo;
    private int point = 0;

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
}
