package util;

import com.sun.net.httpserver.HttpExchange;
import data.GameList;
import data.model.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tcp.TCP_server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SimpleQuery {
    public static void send(HttpExchange exchange, String response) {
        try {
            byte[] bs = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bs.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bs);
            os.close();

        } catch (IOException e) {
            System.out.println();
        }
    }

    public static String get(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //Permet d'envoyer un message
    public static void sendMessage(HttpExchange exchange, String message) {
        byte[] bs = message.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, bs.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bs);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Permet d'envoyer une error
    public static void sendCode(HttpExchange exchange, int respCode, String errDesc) {
        String message = "HTTP error " + respCode + ": " + errDesc;

        byte[] bs = message.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(404, bs.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bs);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendHtmlWikiPage(HttpExchange exchange, String title, String pseudo, String roomNumber) {
        Player player = GameList.getInstance().getRooms().get(roomNumber).getPlayers().get(pseudo);
        player.getVisistedPage().put(title, "https://en.wikipedia.org/w/api.php?action=parse&format=json&page=" + title + "&prop=text|links");
        player.increment();

        if(GameList.getInstance().getRooms().get(roomNumber).getTitleEnd().equals(title)) {
            SimpleQuery.sendMessage(exchange, "GG");
            TCP_server.getInstance().onWin(player.getPseudo(), Integer.toString(player.getPoint()));
            return;
        }

        String result = SimpleQuery.get("https://en.wikipedia.org/w/api.php?action=parse&format=json&page=" + title + "&prop=text|links");
        if(result != null) {
            try {
                Object obj = new JSONParser().parse(result);
                JSONObject root = (JSONObject) obj;
                JSONObject parser = (JSONObject) root.get("parse");
                JSONObject text = (JSONObject) parser.get("text");
                String contain = text.get("*").toString();

                JSONArray links = (JSONArray) parser.get("links");
                for(Object l : links) {
                    JSONObject link = (JSONObject) l;
                    String newLink = link.get("*").toString().replaceAll("\\s+","_");
                    contain = contain.replaceAll("/wiki/" + newLink, "/wiki/" + newLink + "?pseudo=" + pseudo + "&room=" + roomNumber);
                }

                String htmlStr =
                        "<html>" +
                                "<head>" +
                                "<meta charset=\"UTF-8\">" +
                                "<title>WikiWarséé</title>" +
                                "<link rel=\"stylesheet\" href=\"/styles/style.css\"/>" +
                                "<link rel=\"stylesheet\" href=\"https://en.wikipedia.org/w/load.php?lang=en&modules=ext.uls.interlanguage%7Cext.visualEditor.desktopArticleTarget.noscript%7Cext.wikimediaBadges%7Cskins.vector.styles.legacy%7Cwikibase.client.init&only=styles&skin=vector\"/>" +
                                "<link rel=\"stylesheet\" href=\"https://en.wikipedia.org/w/load.php?lang=en&modules=site.styles&only=styles&skin=vector\"/>" +
                                "</head>" +
                                "<body class=\"mediawiki ltr sitedir-ltr mw-hide-empty-elt ns-0 ns-subject page-Test rootpage-Test skin-vector action-view skin-vector-legacy\">" +
                                "<div id=\"content\" role=\"main\" class=\"bodyMargin\">" + //class="mw-body"
                                "<h1>" + GameList.getInstance().getRooms().get(roomNumber).getTitleStart() + " -> " + GameList.getInstance().getRooms().get(roomNumber).getTitleEnd() + " | Nombre de clique " + player.getPoint() + "</h1>" +
                                "<h1 id=\"firstHeading\" class=\"firstHeading\">" + parser.get("title").toString() + "</h1>" +
                                "<div id=\"bodyContent\" class=\"mw-body-content\">" +
                                "<div id=\"siteSub\" class=\"noprint\">From Wikipedia, the free encyclopedia</div>" +
                                "<div id=\"contentSub\"></div>" +
                                contain +
                                "</div>" +
                                "</div>" +
                                "</body>" +
                                "</html>";

                SimpleQuery.send(exchange, htmlStr);
                return;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        SimpleQuery.sendCode(exchange, 404, "ERROR 404");
    }
}
