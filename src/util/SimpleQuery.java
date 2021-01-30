package util;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    public static void sendError(HttpExchange exchange, int respCode, String errDesc) {
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
}
