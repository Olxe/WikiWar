import com.sun.net.httpserver.HttpServer;
import data.GameList;
import data.model.Game;
import data.model.Player;
import handler.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.SimpleQuery;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    //Création du serveur HTTP
    @Override
    public void run() {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(3000), 0);

            httpServer.createContext("/wiki/", new WikiHandler());

            httpServer.createContext("/styles/", new StyleHandler());

            httpServer.createContext("/create", new GameCreateHandler());

            httpServer.createContext("/join/", new GameJoinHandler());

            httpServer.createContext("/start/", new StartHandler());

            httpServer.createContext("/fetch/", new FetchHandler());

            httpServer.createContext("/players/", new PlayersHandler());

//            httpServer.createContext("/", exchange -> {
////                SimpleQuery.sendError(exchange, 404, "Not found");
//                OutputStream os = exchange.getResponseBody();
////                os.write(bs);
//                os.close();
//            });

            httpServer.setExecutor(executor);
            httpServer.start();


//            String content = SimpleQuery.get("https://en.wikipedia.org/api/rest_v1/page/random/summary");
//            Object obj = null;
//            try {
//                obj = new JSONParser().parse(content);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            JSONObject root = (JSONObject) obj;
//            JSONObject titles = (JSONObject) root.get("titles");
//            String canonical = titles.get("canonical").toString();
//            String normalized = titles.get("normalized").toString();
//
//            System.out.println(canonical);

            GameList.getInstance().getRooms().put("0000", new Game("MIKA", "Villeurbanne", "France"));
            GameList.getInstance().getRooms().get("0000").addPlayer(new Player("MIKA"));

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
