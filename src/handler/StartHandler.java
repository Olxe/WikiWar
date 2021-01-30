package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.GameList;
import data.model.Player;
import util.SimpleQuery;
import util.Tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StartHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String roomNumber = path.getFileName().toString();

        Map<String, String> params = Tools.queryToMap(exchange.getRequestURI().getQuery());

        if(GameList.getInstance().getRooms().containsKey(roomNumber.toUpperCase())) {
            if(params.containsKey("pseudo")) {
                GameList.getInstance().getRooms().get(roomNumber.toUpperCase()).addPlayer(new Player(params.get("pseudo")));
                SimpleQuery.sendMessage(exchange, roomNumber.toUpperCase());
            }
            else {
                SimpleQuery.sendError(exchange, 409, "Seul(e) l'hôte peut lancer la partie ):");
            }
        }
        else {
            SimpleQuery.sendError(exchange, 404, "La partie n'existe pas !");
        }
    }
}
