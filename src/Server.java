import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
                try {
                    Path path = Paths.get(exchange.getRequestURI().getPath());
                    String title = path.getFileName().toString();

                    String result = Query.get("https://en.wikipedia.org/w/api.php?action=parse&format=json&page=" + title + "&prop=text|links");
                    if(result != null) {
                        Object obj = new JSONParser().parse(result);
                        JSONObject root = (JSONObject) obj;
                        JSONObject parser = (JSONObject) root.get("parse");
                        JSONObject text = (JSONObject) parser.get("text");
                        String contain = text.get("*").toString();

                        String htmlStr =
                                "<html>" +
                                        "<head>" +
                                        "<meta charset=\"UTF-8\">" +
                                        "   <title>WikiWarséé</title>" +
//                                        "<link rel=\"stylesheet\" href=\"/w/load.php?lang=en&modules=site.styles&only=styles&skin=vector\"/>" +
//                                        "<link rel=\"stylesheet\" href=\"//en.wikipedia.org/w/load.php?lang=en&modules=ext.uls.interlanguage%7Cext.visualEditor.desktopArticleTarget.noscript%7Cext.wikimediaBadges%7Cskins.vector.styles.legacy%7Cwikibase.client.init&only=styles&skin=vector\"/>" +
                                        "<link rel=\"stylesheet\" href=\"/styles/style.css\"/>" +
                                        "<link rel=\"stylesheet\" href=\"https://en.wikipedia.org/w/load.php?lang=en&modules=ext.uls.interlanguage%7Cext.visualEditor.desktopArticleTarget.noscript%7Cext.wikimediaBadges%7Cskins.vector.styles.legacy%7Cwikibase.client.init&only=styles&skin=vector\"/>" +
                                        "<link rel=\"stylesheet\" href=\"https://en.wikipedia.org/w/load.php?lang=en&modules=site.styles&only=styles&skin=vector\"/>" +
                                        "</head>" +
                                        "<body class=\"mediawiki ltr sitedir-ltr mw-hide-empty-elt ns-0 ns-subject page-Test rootpage-Test skin-vector action-view skin-vector-legacy\">" +
                                        "<div id=\"content\" role=\"main\" class=\"bodyMargin\">" + //class="mw-body"
                                        "<h1 id=\"firstHeading\" class=\"firstHeading\">" + parser.get("title").toString() + "</h1>" +
                                        "<div id=\"bodyContent\" class=\"mw-body-content\">" +
                                        "<div id=\"siteSub\" class=\"noprint\">From Wikipedia, the free encyclopedia</div>" +
                                        "<div id=\"contentSub\"></div>" +
                                        contain +
                                        "</div>" +
                                        "</div>" +
                                        "</body>" +
                                        "</html>";
                        Query.send(exchange, htmlStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });

            httpServer.createContext("/styles/", exchange -> {
                File file = new File("res" + exchange.getRequestURI().getPath());
                byte [] bytearray  = new byte [(int)file.length()];
                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(bytearray, 0, bytearray.length);

                    exchange.getResponseHeaders().set("Content-Type", "text/css; charset=" + "UTF-8");
                    exchange.sendResponseHeaders(200, file.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytearray,0,bytearray.length);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            httpServer.createContext("/hello", exchange -> {
                try {
                    String result = Query.get("https://en.wikipedia.org/w/api.php?action=parse&format=json&page=Test&prop=text|links");
                    if(result != null) {
                        Object obj = new JSONParser().parse(result);
                        JSONObject root = (JSONObject) obj;
                        JSONObject parser = (JSONObject) root.get("parse");
                        JSONObject text = (JSONObject) parser.get("text");
                        String contain = text.get("*").toString();

                        String htmlStr =
                                "<html>" +
                                "<head>" +
                                "<meta charset=\"UTF-8\">" +
                                "   <title>WikiWarséé</title>" +
                                "<link rel=\"stylesheet\" href=\"//en.wikipedia.org/w/load.php?lang=en&modules=ext.uls.interlanguage%7Cext.visualEditor.desktopArticleTarget.noscript%7Cext.wikimediaBadges%7Cskins.vector.styles.legacy%7Cwikibase.client.init&only=styles&skin=vector\"/>" +
                                "</head>" +
                                "<body>" +
                                    contain +
                                "</body>" +
                                "</html>";
                        Query.send(exchange, htmlStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

            httpServer.createContext("/", exchange -> {
                String response = Query.get("https://en.wikipedia.org/" + "w/load.php?lang=en&modules=site.styles&only=styles&skin=vector");
                assert response != null;

                try {
                    exchange.getResponseHeaders().set("Content-Type", "text/css; charset=" + "UTF-8");

                    byte[] bs = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bs.length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(bs);
                    os.close();

                } catch (IOException e) {
                    System.out.println();
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
