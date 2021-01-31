package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.SimpleQuery;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WikiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String title = path.getFileName().toString();

        SimpleQuery.sendHtmlWikiPage(exchange, title);
    }
}
