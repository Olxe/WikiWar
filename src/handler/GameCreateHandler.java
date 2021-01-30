package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.Game;
import data.GameList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class RoomCreateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int randomCode = new Random().nextInt(10000);
        String randomCode4digit = String.format("%04d", randomCode);

        GameList.getInstance().getRooms().put(randomCode4digit.toUpperCase(), new Game("Villeurbanne", "France"));

        System.out.println("RANDOM ROOM NUMBER CREATED " + randomCode4digit);

        try {
            byte[] bs = randomCode4digit.getBytes();
            exchange.sendResponseHeaders(200, bs.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bs);
            os.close();

        } catch (IOException e) {
            System.out.println();
        }
    }
}
