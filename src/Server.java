import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private HashMap<String, String> rooms = new HashMap<>();

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

            httpServer.createContext("/hello", exchange -> {

                Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
                Elements newsHeadlines = doc.select("#mp-itn b a");
                for (Element headline : newsHeadlines) {
                    System.out.println(headline.attr("title"));
                }

                System.out.println("hello");
            });

            httpServer.createContext("/createRoom", exchange -> {
                int randomCode = new Random().nextInt(10000);
                String randomCode4digit = String.format("%04d", randomCode);
                this.rooms.put(randomCode4digit.toUpperCase(), "https://fr.wikipedia.org/wiki/Star_Wars");

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
            });

            httpServer.createContext("/room/", exchange -> {
                Path path = Paths.get(exchange.getRequestURI().getPath());
                String roomNumber = path.getFileName().toString();

                if(this.rooms.containsKey(roomNumber.toUpperCase())) {
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(this.rooms.get(roomNumber.toUpperCase())))
                                .build();

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
                }
                else {
                    showError(exchange, 404, "Bad code");
                }
            });

            httpServer.setExecutor(executor);
            httpServer.start();

            System.out.println("Server started !");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(HttpExchange httpExchange, int respCode, String errDesc) throws IOException {
        String message = "HTTP error " + respCode + ": " + errDesc;
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        httpExchange.sendResponseHeaders(respCode, messageBytes.length);

        OutputStream os = httpExchange.getResponseBody();
        os.write(messageBytes);
        os.close();
    }
}
