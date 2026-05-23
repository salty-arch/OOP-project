package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class BudgetController {

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
            if (email == null) {
                JsonHelper.sendError(exchange, 400, "email parameter required");
                return;
            }

            StringBuilder json = new StringBuilder("[");
            String sql = "SELECT budget_category, amount, remaining_budget FROM budget WHERE user_email = ?";

            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    String cat = rs.getString("budget_category");
                    double budget = rs.getDouble("amount");
                    double remaining = rs.getDouble("remaining_budget");
                    double spent = budget - remaining;
                    int progress = budget > 0 ? (int) ((spent / budget) * 100) : 0;

                    json.append("{\"category\":").append(JsonHelper.jsonEscape(cat))
                            .append(",\"budget\":").append(JsonHelper.jsonNumber(budget))
                            .append(",\"spent\":").append(JsonHelper.jsonNumber(spent))
                            .append(",\"remaining\":").append(JsonHelper.jsonNumber(remaining))
                            .append(",\"progress\":").append(progress)
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
            String category = params.get("category");
            String amountStr = params.get("amount");

            if (email == null || category == null || amountStr == null) {
                JsonHelper.sendError(exchange, 400, "email, category, and amount required");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                JsonHelper.sendError(exchange, 400, "Invalid amount");
                return;
            }

            if (amount <= 0) {
                JsonHelper.sendError(exchange, 400, "Amount must be positive");
                return;
            }

            String sql = "INSERT INTO budget (user_email, budget_category, amount, remaining_budget) VALUES (?, ?, ?, ?)";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, category);
                pstmt.setDouble(3, amount);
                pstmt.setDouble(4, amount);
                pstmt.executeUpdate();

                Databasehelper.logActivity(email, "ADD_BUDGET",
                        "Set budget for " + category + ": PKR " + amount);
                JsonHelper.sendSuccess(exchange, "Budget added");
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }
}