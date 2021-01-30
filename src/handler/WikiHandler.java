package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.SimpleQuery;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WikiHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path path = Paths.get(exchange.getRequestURI().getPath());
        String title = path.getFileName().toString();

        String result = SimpleQuery.get("https://en.wikipedia.org/w/api.php?action=parse&format=json&page=" + title + "&prop=text|links");
        if(result != null) {
            try {
                Object obj = new JSONParser().parse(result);
                JSONObject root = (JSONObject) obj;
                JSONObject parser = (JSONObject) root.get("parse");
                JSONObject text = (JSONObject) parser.get("text");
                String contain = text.get("*").toString();

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

        SimpleQuery.sendError(exchange, 404, "BLABLABLA");
    }
}
