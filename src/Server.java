import com.sun.net.httpserver.HttpServer;
import data.GameList;
import data.model.Game;
import handler.*;

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

            httpServer.setExecutor(executor);
            httpServer.start();

            GameList.getInstance().getRooms().put("0000", new Game("MIKA", "Villeurbanne", "France"));

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
