package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.GameList;
import util.SimpleQuery;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PlayersHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String roomNumber = path.getFileName().toString();

        if(GameList.getInstance().getRooms().containsKey(roomNumber.toUpperCase())) {
            StringBuilder nameList = new StringBuilder();
            for(String name : GameList.getInstance().getRooms().get(roomNumber.toUpperCase()).getPlayers().keySet()) {
                nameList.append(name).append(";");
            }
            SimpleQuery.sendMessage(exchange, nameList.toString());
        }
    }
}
