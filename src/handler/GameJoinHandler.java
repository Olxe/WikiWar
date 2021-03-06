package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.GameList;
import data.model.Game;
import data.model.Player;
import util.SimpleQuery;
import util.Tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class GameJoinHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String roomNumber = path.getFileName().toString();

        Map<String, String> params = Tools.queryToMap(exchange.getRequestURI().getQuery());

        if(GameList.getInstance().getRooms().containsKey(roomNumber.toUpperCase())) {
            Game game = GameList.getInstance().getRooms().get(roomNumber.toUpperCase());

            if(params.containsKey("pseudo") && !game.getPlayers().containsKey(params.get("pseudo"))) {
                GameList.getInstance().getRooms().get(roomNumber.toUpperCase()).addPlayer(new Player(params.get("pseudo")));
                SimpleQuery.sendMessage(exchange, roomNumber.toUpperCase());
            }
            else {
                SimpleQuery.sendCode(exchange, 409, "Error de pseudo ):");
            }
        }
        else {
            SimpleQuery.sendCode(exchange, 404, "La partie n'existe pas !");
        }
    }
}
