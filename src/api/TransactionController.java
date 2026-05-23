package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class TransactionController {

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

            StringBuilder sql = new StringBuilder(
                    "SELECT id, amount, type, date FROM amount WHERE user_email = ?");
            String month = params.get("month");
            String year = params.get("year");

            if (month != null && year != null) {
                sql.append(" AND strftime('%m', date) = ? AND strftime('%Y', date) = ?");
            } else if (year != null) {
                sql.append(" AND strftime('%Y', date) = ?");
            }
            sql.append(" ORDER BY date DESC");

            StringBuilder json = new StringBuilder("[");

            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                pstmt.setString(1, email);
                if (month != null && year != null) {
                    pstmt.setString(2, String.format("%02d", Integer.parseInt(month)));
                    pstmt.setString(3, year);
                } else if (year != null) {
                    pstmt.setString(2, year);
                }

                ResultSet rs = pstmt.executeQuery();
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{\"id\":").append(rs.getInt("id"))
                            .append(",\"amount\":").append(JsonHelper.jsonNumber(rs.getDouble("amount")))
                            .append(",\"type\":").append(JsonHelper.jsonEscape(rs.getString("type")))
                            .append(",\"date\":").append(JsonHelper.jsonEscape(rs.getString("date")))
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
            String type = params.get("type");
            String amountStr = params.get("amount");
            String category = params.get("category");

            if (email == null || type == null || amountStr == null) {
                JsonHelper.sendError(exchange, 400, "email, type, and amount required");
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

            try (Connection conn = Databasehelper.connect()) {
                if (type.equalsIgnoreCase("expense") && category != null && !category.isEmpty()) {
                    String budgetSql = "SELECT remaining_budget FROM budget WHERE user_email = ? AND budget_category = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(budgetSql)) {
                        pstmt.setString(1, email);
                        pstmt.setString(2, category);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            double remaining = rs.getDouble("remaining_budget");
                            if (amount > remaining) {
                                JsonHelper.sendError(exchange, 400,
                                        "Expense exceeds remaining budget for " + category);
                                return;
                            }
                            String update = "UPDATE budget SET remaining_budget = remaining_budget - ? WHERE user_email = ? AND budget_category = ?";
                            try (PreparedStatement upstmt = conn.prepareStatement(update)) {
                                upstmt.setDouble(1, amount);
                                upstmt.setString(2, email);
                                upstmt.setString(3, category);
                                upstmt.executeUpdate();
                            }
                        }
                    }
                }

                String sql = "INSERT INTO amount (user_email, type, amount) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, email);
                    pstmt.setString(2, type.toLowerCase());
                    pstmt.setDouble(3, amount);
                    pstmt.executeUpdate();
                }

                Databasehelper.logActivity(email, "ADD_TRANSACTION",
                        "Added " + type + " of PKR " + amount);
                JsonHelper.sendSuccess(exchange, "Transaction added");
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }
}