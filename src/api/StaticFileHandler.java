package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StaticFileHandler implements HttpHandler {

    private static final String WEB_ROOT = "webapp";
    private static final Map<String, String> MIME = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript",
            "json", "application/json",
            "png", "image/png",
            "jpg", "image/jpeg",
            "svg", "image/svg+xml",
            "ico", "image/x-icon"
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/login.html";

        Path file = Paths.get(WEB_ROOT + path).normalize();

        if (!file.startsWith(Paths.get(WEB_ROOT).normalize()) || !Files.exists(file) || Files.isDirectory(file)) {
            String resp = "404 Not Found";
            exchange.sendResponseHeaders(404, resp.length());
            exchange.getResponseBody().write(resp.getBytes());
            exchange.getResponseBody().close();
            return;
        }

        String ext = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : "";
        String mime = MIME.getOrDefault(ext, "application/octet-stream");

        exchange.getResponseHeaders().set("Content-Type", mime);
        exchange.sendResponseHeaders(200, Files.size(file));
        Files.copy(file, exchange.getResponseBody());
        exchange.getResponseBody().close();
    }
}