package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.SimpleQuery;
import util.Tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class WikiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String title = path.getFileName().toString();

        Map<String, String> params = Tools.queryToMap(exchange.getRequestURI().getQuery());
        System.out.println(title);

        if(params.containsKey("pseudo") && params.containsKey("room")) {
            SimpleQuery.sendHtmlWikiPage(exchange, title, params.get("pseudo"), params.get("room"));
        }
        else {
            SimpleQuery.sendCode(exchange, 409, "Error de pseudo ):");
        }
    }
}
