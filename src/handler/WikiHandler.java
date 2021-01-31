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
        String path = exchange.getRequestURI().getRawPath();

        String[] parts = path.split("/");
        String title = parts[2];

        if(exchange.getRequestURI().getQuery() != null) {
            Map<String, String> params = Tools.queryToMap(exchange.getRequestURI().getQuery());

            if(params.containsKey("pseudo") && params.containsKey("room")) {
                SimpleQuery.sendHtmlWikiPage(exchange, title, params.get("pseudo"), params.get("room"));
                return;
            }
            else {
                SimpleQuery.sendCode(exchange, 409, "Error de pseudo ):");
                return;
            }
        }


        SimpleQuery.sendCode(exchange, 404, "Error 404 ):");
    }
}
