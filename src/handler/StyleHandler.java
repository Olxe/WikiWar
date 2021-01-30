package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class StyleHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        File file = new File("res" + exchange.getRequestURI().getPath());
        byte [] bytearray  = new byte [(int)file.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytearray, 0, bytearray.length);

            exchange.getResponseHeaders().set("Content-Type", "text/css; charset=" + "UTF-8");
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            os.write(bytearray,0,bytearray.length);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
