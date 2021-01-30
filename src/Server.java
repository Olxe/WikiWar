import com.sun.net.httpserver.HttpServer;
import handler.GameCreateHandler;
import handler.GameJoinHandler;
import handler.StyleHandler;
import handler.WikiHandler;

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

            httpServer.setExecutor(executor);
            httpServer.start();

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
