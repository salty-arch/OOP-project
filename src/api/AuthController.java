package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class AuthController {

    public static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("[LOGIN] Request received");
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    JsonHelper.sendError(exchange, 405, "Method not allowed");
                    return;
                }
                System.out.println("[LOGIN] Method OK");
                Map<String, String> params = JsonHelper.parseForm(exchange);
                System.out.println("[LOGIN] Params: " + params);
                String email = params.get("email");
                String password = params.get("password");

                if (email == null || password == null) {
                    JsonHelper.sendError(exchange, 400, "Email and password required");
                    return;
                }

                Connection conn = Databasehelper.connect();
                System.out.println("[LOGIN] conn=" + conn);
                if (conn == null) throw new RuntimeException("DB connection is null");
                String sql = "SELECT role FROM users WHERE email = ? AND password = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println("[LOGIN] stmt created");
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                System.out.println("[LOGIN] params set");
                ResultSet rs = pstmt.executeQuery();
                System.out.println("[LOGIN] query executed");

                if (rs.next()) {
                    System.out.println("[LOGIN] user found");
                    String role = rs.getString("role");
                    Databasehelper.logActivity(email, "LOGIN", "User logged in via web");
                    String json = "{\"email\":" + JsonHelper.jsonEscape(email)
                            + ",\"role\":" + JsonHelper.jsonEscape(role) + "}";
                    System.out.println("[LOGIN] sending success response");
                    JsonHelper.sendJson(exchange, 200, json);
                    System.out.println("[LOGIN] success response sent");
                } else {
                    System.out.println("[LOGIN] user not found, sending 401");
                    JsonHelper.sendError(exchange, 401, "Invalid email or password");
                    System.out.println("[LOGIN] 401 sent");
                }
                } catch (SQLException e) {
                    System.out.println("[LOGIN] SQLException: " + e.getMessage());
                    e.printStackTrace();
                    JsonHelper.sendError(exchange, 500, "Login failed: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("[LOGIN] Unexpected exception: " + e.getMessage());
                e.printStackTrace();
                try { JsonHelper.sendError(exchange, 500, "Unexpected: " + e.getMessage()); } catch (Exception ex) { System.out.println("[LOGIN] sendError also failed: " + ex.getMessage()); }
            }
        }
    }

    public static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            Map<String, String> params = JsonHelper.parseForm(exchange);
            String email = params.get("email");
            String password = params.get("password");

            if (email == null || password == null) {
                JsonHelper.sendError(exchange, 400, "Email and password required");
                return;
            }

            String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'Client')";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                Databasehelper.logActivity(email, "REGISTER", "User registered via web");
                String json = "{\"email\":" + JsonHelper.jsonEscape(email) + ",\"role\":\"Client\"}";
                JsonHelper.sendJson(exchange, 201, json);
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE")) {
                    JsonHelper.sendError(exchange, 409, "Email already registered");
                } else {
                    JsonHelper.sendError(exchange, 500, "Registration failed: " + e.getMessage());
                }
            }
        }
    }
}