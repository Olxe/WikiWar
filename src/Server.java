import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    @Override
    public void run() {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(3000), 0);

            httpServer.createContext("/test", new ServerHandler());

            httpServer.createContext("/wiki/", exchange -> {
                System.out.println(exchange.getRequestURI().getPath());
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://fr.wikipedia.org" + exchange.getRequestURI().getPath()))
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String responseString = response.body();

                    byte[] bs = responseString.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bs.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bs);
                    os.close();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            httpServer.setExecutor(executor);
            httpServer.start();

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
