package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class DashboardController {

    public static class SummaryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = JsonHelper.parseQuery(exchange);
            String email = params.get("email");
            if (email == null) {
                JsonHelper.sendError(exchange, 400, "email parameter required");
                return;
            }

            double totalIncome = 0, totalExpense = 0;
            double totalBudget = 0, totalRemaining = 0;

            String sqlIncome = "SELECT COALESCE(SUM(amount),0) FROM amount WHERE user_email = ? AND type = 'income'";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlIncome)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) totalIncome = rs.getDouble(1);
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
                return;
            }

            String sqlExpense = "SELECT COALESCE(SUM(amount),0) FROM amount WHERE user_email = ? AND type = 'expense'";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlExpense)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) totalExpense = rs.getDouble(1);
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
                return;
            }

            String sqlBudget = "SELECT COALESCE(SUM(amount),0), COALESCE(SUM(remaining_budget),0) FROM budget WHERE user_email = ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlBudget)) {
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalBudget = rs.getDouble(1);
                    totalRemaining = rs.getDouble(2);
                }
            } catch (SQLException e) {
                JsonHelper.sendError(exchange, 500, "Error: " + e.getMessage());
                return;
            }

            double savings = totalIncome - totalExpense;
            double totalSpent = totalBudget - totalRemaining;

            int txCount = 0, budgetCount = 0;
            try (Connection conn = Databasehelper.connect()) {
                ResultSet rs = conn.createStatement().executeQuery(
                        "SELECT (SELECT COUNT(*) FROM amount WHERE user_email='" + email.replace("'", "''") + "')," +
                        "(SELECT COUNT(*) FROM budget WHERE user_email='" + email.replace("'", "''") + "')");
                if (rs.next()) { txCount = rs.getInt(1); budgetCount = rs.getInt(2); }
            } catch (SQLException ignored) {}

            String json = "{\"totalIncome\":" + JsonHelper.jsonNumber(totalIncome)
                    + ",\"totalExpense\":" + JsonHelper.jsonNumber(totalExpense)
                    + ",\"savings\":" + JsonHelper.jsonNumber(savings)
                    + ",\"totalBudget\":" + JsonHelper.jsonNumber(totalBudget)
                    + ",\"totalRemaining\":" + JsonHelper.jsonNumber(totalRemaining)
                    + ",\"totalSpent\":" + JsonHelper.jsonNumber(totalSpent)
                    + ",\"transactionCount\":" + txCount
                    + ",\"budgetCount\":" + budgetCount + "}";

            JsonHelper.sendJson(exchange, 200, json);
        }
    }
}