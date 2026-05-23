package api;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonHelper {

    public static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    public static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, "{\"error\":" + jsonEscape(message) + "}");
    }

    public static void sendSuccess(HttpExchange exchange, String message) throws IOException {
        sendJson(exchange, 201, "{\"message\":" + jsonEscape(message) + "}");
    }

    public static Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        Map<String, String> params = new LinkedHashMap<>();
        String ct = exchange.getRequestHeaders().getFirst("Content-Type");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) return params;

        if (ct != null && ct.startsWith("application/x-www-form-urlencoded")) {
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        } else if (body.startsWith("{")) {
            // simple flat JSON parse
            String inner = body.substring(1, body.length() - 1);
            int depth = 0;
            StringBuilder key = new StringBuilder();
            StringBuilder val = new StringBuilder();
            boolean inKey = true;
            boolean inStr = false;
            for (int i = 0; i < inner.length(); i++) {
                char c = inner.charAt(i);
                if (c == '"') {
                    inStr = !inStr;
                } else if (!inStr && (c == '{' || c == '[')) {
                    depth++;
                } else if (!inStr && (c == '}' || c == ']')) {
                    depth--;
                } else if (!inStr && c == ':' && depth == 0) {
                    inKey = false;
                    continue;
                } else if (!inStr && c == ',' && depth == 0) {
                    String k = key.toString().trim();
                    String v = val.toString().trim();
                    if (!k.isEmpty()) {
                        if (k.startsWith("\"")) k = k.substring(1);
                        if (k.endsWith("\"")) k = k.substring(0, k.length() - 1);
                        if (v.startsWith("\"")) v = v.substring(1);
                        if (v.endsWith("\"")) v = v.substring(0, v.length() - 1);
                        params.put(k, v);
                    }
                    key.setLength(0);
                    val.setLength(0);
                    inKey = true;
                    continue;
                }
                if (inKey) key.append(c);
                else val.append(c);
            }
            if (!key.isEmpty()) {
                String k = key.toString().trim();
                String v = val.toString().trim();
                if (k.startsWith("\"")) k = k.substring(1);
                if (k.endsWith("\"")) k = k.substring(0, k.length() - 1);
                if (v.startsWith("\"")) v = v.substring(1);
                if (v.endsWith("\"")) v = v.substring(0, v.length() - 1);
                params.put(k, v);
            }
        }
        return params;
    }

    public static Map<String, String> parseQuery(HttpExchange exchange) {
        Map<String, String> params = new LinkedHashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }
        return params;
    }

    public static String jsonEscape(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }

    public static String jsonNumber(double n) {
        if (n == Math.floor(n) && !Double.isInfinite(n)) {
            return String.valueOf((long) n);
        }
        return String.format("%.2f", n);
    }
}