import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ServerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        readFile(exchange);
//        showError(exchange, 404, "The requested resource was not found on server");
    }

    private void readFile(HttpExchange httpExchange) {
        String url1 = "https://fr.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts&titles=Stack%20Overflow&redirects=true";
        String url2 = "https://fr.wikipedia.org/wiki/Star_Wars";
        String url3 = "https://en.wikipedia.org/w/api.php?action=parse&page=Doritos&prop=text|headhtml";
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url2))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseString = response.body();
            System.out.println(responseString);

            byte[] bs = responseString.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(200, bs.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(bs);
            os.close();

        } catch (IOException | InterruptedException e) {
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
