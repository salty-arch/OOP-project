package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class GoalController {

    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod().toUpperCase();
            if (method.equals("GET")) {
                handleGet(exchange);
            } else if (method.equals("POST")) {
                handlePost(exchange);
            } else {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            Map<String, String> params = JsonHelper.parseQuery(exchange);
            String email = params.get("email");
            String status = params.get("status");

            if (email == null) {
                JsonHelper.sendError(exchange, 400, "email parameter required");
                return;
            }

            StringBuilder sql = new StringBuilder(
                    "SELECT id, goal_type, category, amount, deadline, status FROM goals WHERE user_email = ?");
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                sql.append(" AND status = ?");
            }
            sql.append(" ORDER BY deadline ASC");

            StringBuilder json = new StringBuilder("[");

            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                pstmt.setString(1, email);
                if (status != null && !status.isEmpty() && !status.equals("all")) {
                    pstmt.setString(2, status);
                }

                ResultSet rs = pstmt.executeQuery();
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{\"id\":").append(rs.getInt("id"))
                            .append(",\"goalType\":").append(JsonHelper.jsonEscape(rs.getString("goal_type")))
                            .append(",\"category\":").append(JsonHelper.jsonEscape(rs.getString("category")))
                            .append(",\"amount\":").append(JsonHelper.jsonNumber(rs.getDouble("amount")))
                            .append(",\"deadline\":").append(JsonHelper.jsonEscape(rs.getString("deadline")))
                            .append(",\"status\":").append(JsonHelper.jsonEscape(rs.getString("status")))
                            .append("}");
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
                return;
            }

            json.append("]");
            JsonHelper.sendJson(exchange, 200, json.toString());
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            Map<String, String> params = JsonHelper.parseForm(exchange);
            String email = params.get("email");
            String goalType = params.get("goal_type");
            String category = params.get("category");
            String amountStr = params.get("amount");
            String deadline = params.get("deadline");

            if (email == null || goalType == null || amountStr == null || deadline == null) {
                JsonHelper.sendError(exchange, 400, "email, goal_type, amount, and deadline required");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                JsonHelper.sendError(exchange, 400, "Invalid amount");
                return;
            }

            String sql = "INSERT INTO goals (user_email, goal_type, category, amount, deadline, status) VALUES (?, ?, ?, ?, ?, 'active')";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, goalType);
                pstmt.setString(3, (category == null || category.isEmpty()) ? null : category);
                pstmt.setDouble(4, amount);
                pstmt.setString(5, deadline);
                pstmt.executeUpdate();

                Databasehelper.logActivity(email, "ADD_GOAL",
                        "Set " + goalType + " goal: PKR " + amount + " by " + deadline);
                JsonHelper.sendSuccess(exchange, "Goal added");
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }
}