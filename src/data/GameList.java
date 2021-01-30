package data;

import java.util.HashMap;

public class RoomData {
    private HashMap<String, String> rooms = new HashMap<>();

    private RoomData() {}

    private static RoomData instance = new RoomData();

    public static RoomData getInstance() {
        return instance;
    }

    public HashMap<String, String> getRooms() {
        return this.rooms;
    }
}
