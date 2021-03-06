package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.model.Game;
import data.GameList;
import util.SimpleQuery;
import util.Tools;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class GameCreateHandler implements HttpHandler {
    private ArrayList<String> pages = new ArrayList<>() {
        {
            add("Villeurbanne");
            add("Montre");
            add("Atome");
            add("Voiture");
            add("Crabe");
            add("Mars");
            add("Chien");
            add("Route");
            add("Ordinateur");
            add("Tapis");
            add("Sport");
            add("Avion");
        }
    };
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = Tools.queryToMap(exchange.getRequestURI().getQuery());

        if(params.containsKey("pseudo")) {
            int randomCode = new Random().nextInt(10000);
            String randomCode4digit = String.format("%04d", randomCode);

            String start = pages.get(new Random().nextInt(pages.size()));
            String end = pages.get(new Random().nextInt(pages.size()));
            while (start.equals(end)) {
                end = pages.get(new Random().nextInt(pages.size()));
            }

            GameList.getInstance().getRooms().put(randomCode4digit.toUpperCase(), new Game(params.get("pseudo"), start, end));

            System.out.println("RANDOM ROOM NUMBER CREATED " + randomCode4digit);

            try {
                byte[] bs = randomCode4digit.getBytes();
                exchange.sendResponseHeaders(200, bs.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bs);
                os.close();
                return;
            } catch (IOException e) {
                System.out.println();
            }
        }

        SimpleQuery.sendCode(exchange, 404, "Impossible créer la partie ):");
    }
}
