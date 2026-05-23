package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class AdminController {

    public static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT email FROM users WHERE role = 'Client' ORDER BY email")) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{\"email\":").append(JsonHelper.jsonEscape(rs.getString("email"))).append("}");
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, e.getMessage());
                return;
            }
            json.append("]");
            JsonHelper.sendJson(exchange, 200, json.toString());
        }
    }

    public static class DeleteUserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            Map<String, String> params = JsonHelper.parseForm(exchange);
            String adminEmail = params.get("adminEmail");
            String adminPassword = params.get("adminPassword");
            String clientEmail = params.get("clientEmail");

            if (adminEmail == null || adminPassword == null || clientEmail == null) {
                JsonHelper.sendError(exchange, 400, "adminEmail, adminPassword, and clientEmail required");
                return;
            }

            try (Connection conn = Databasehelper.connect()) {
                String checkSql = "SELECT password FROM users WHERE email = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setString(1, adminEmail);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        JsonHelper.sendError(exchange, 404, "Admin not found");
                        return;
                    }
                    if (!rs.getString("password").equals(adminPassword)) {
                        JsonHelper.sendError(exchange, 403, "Invalid admin password");
                        return;
                    }
                }

                String deleteSql = "DELETE FROM users WHERE email = ? AND role = 'Client'";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setString(1, clientEmail);
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        Databasehelper.logActivity(adminEmail, "DELETE_CLIENT",
                                "Deleted client account: " + clientEmail);
                        JsonHelper.sendSuccess(exchange, "Client account deleted");
                    } else {
                        JsonHelper.sendError(exchange, 404, "Client not found");
                    }
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, e.getMessage());
            }
        }
    }

    public static class ActivityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT user_email, activity_type, activity_details, timestamp " +
                                 "FROM activity_log ORDER BY timestamp DESC")) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{\"user_email\":").append(JsonHelper.jsonEscape(rs.getString("user_email")))
                            .append(",\"activity_type\":").append(JsonHelper.jsonEscape(rs.getString("activity_type")))
                            .append(",\"activity_details\":").append(JsonHelper.jsonEscape(
                                    rs.getString("activity_details") != null ? rs.getString("activity_details") : ""))
                            .append(",\"timestamp\":").append(JsonHelper.jsonEscape(rs.getString("timestamp")))
                            .append("}");
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, e.getMessage());
                return;
            }
            json.append("]");
            JsonHelper.sendJson(exchange, 200, json.toString());
        }
    }

    public static class FinancialReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            StringBuilder json = new StringBuilder("{\"perUser\":[");
            boolean first = true;

            try (Connection conn = Databasehelper.connect()) {
                String userSql = "SELECT user_email, budget_category, amount FROM budget ORDER BY user_email";
                try (PreparedStatement pstmt = conn.prepareStatement(userSql);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        if (!first) json.append(",");
                        first = false;
                        json.append("{\"email\":").append(JsonHelper.jsonEscape(rs.getString("user_email")))
                                .append(",\"category\":").append(JsonHelper.jsonEscape(rs.getString("budget_category")))
                                .append(",\"amount\":").append(JsonHelper.jsonNumber(rs.getDouble("amount")))
                                .append("}");
                    }
                }

                json.append("],\"overall\":{");

                int totalUsers = 0;
                int totalEntries = 0;
                double totalAmount = 0;

                try (Statement stmt = conn.createStatement()) {
                    ResultSet rsu = stmt.executeQuery("SELECT COUNT(DISTINCT user_email) AS cnt FROM budget");
                    if (rsu.next()) totalUsers = rsu.getInt("cnt");
                    ResultSet rse = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM budget");
                    if (rse.next()) totalEntries = rse.getInt("cnt");
                    ResultSet rsa = stmt.executeQuery("SELECT SUM(amount) AS total FROM budget");
                    if (rsa.next()) totalAmount = rsa.getDouble("total");
                }

                json.append("\"totalUsers\":").append(totalUsers)
                        .append(",\"totalEntries\":").append(totalEntries)
                        .append(",\"totalAmount\":").append(JsonHelper.jsonNumber(totalAmount))
                        .append(",\"avgBudget\":").append(JsonHelper.jsonNumber(
                                totalUsers > 0 ? totalAmount / totalUsers : 0))
                        .append("}}");

            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, e.getMessage());
                return;
            }
            JsonHelper.sendJson(exchange, 200, json.toString());
        }
    }

    public static class ChangePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
                return;
            }
            Map<String, String> params = JsonHelper.parseForm(exchange);
            String email = params.get("email");
            String newPassword = params.get("newPassword");

            if (email == null || newPassword == null || newPassword.isEmpty()) {
                JsonHelper.sendError(exchange, 400, "email and newPassword required");
                return;
            }

            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE users SET password = ? WHERE email = ?")) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    Databasehelper.logActivity(email, "CHANGE_PASSWORD", "Password changed");
                    JsonHelper.sendSuccess(exchange, "Password changed");
                } else {
                    JsonHelper.sendError(exchange, 404, "User not found");
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, e.getMessage());
            }
        }
    }
}
