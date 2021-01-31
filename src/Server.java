import com.sun.net.httpserver.HttpServer;
import data.GameList;
import data.model.Game;
import data.model.Player;
import handler.*;
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

            GameList.getInstance().getRooms().put("0000", new Game("MIKA", "Villeurbanne", "France"));
            GameList.getInstance().getRooms().get("0000").addPlayer(new Player("MIKA"));

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
