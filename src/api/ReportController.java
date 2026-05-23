package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.Databasehelper;

import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ReportController {

    public static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = JsonHelper.parseQuery(exchange);
            String email = params.get("email");
            String monthStr = params.get("month");
            String yearStr = params.get("year");

            if (email == null || monthStr == null || yearStr == null) {
                String err = "{\"error\":\"email, month, and year required\"}";
                JsonHelper.sendJson(exchange, 400, err);
                return;
            }

            int month, year;
            try {
                month = Integer.parseInt(monthStr);
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                JsonHelper.sendError(exchange, 400, "Invalid month or year");
                return;
            }

            String html = generateHtmlReport(email, month, year);
            byte[] bytes = html.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }

        private String generateHtmlReport(String userEmail, int month, int year) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            String currentDate = dateFormat.format(new Date());

            String monthStr = String.format("%02d", month);
            String datePattern = year + "-" + monthStr + "%";

            NumberFormat pkrFormat = NumberFormat.getNumberInstance(Locale.US);

            double totalIncome = 0, totalExpense = 0;

            String sqlIncome = "SELECT COALESCE(SUM(amount),0) FROM amount WHERE user_email = ? AND type = 'income' AND date LIKE ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlIncome)) {
                pstmt.setString(1, userEmail);
                pstmt.setString(2, datePattern);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) totalIncome = rs.getDouble(1);
            } catch (SQLException e) {
                System.out.println("Error fetching income: " + e.getMessage());
            }

            String sqlExpense = "SELECT COALESCE(SUM(amount),0) FROM amount WHERE user_email = ? AND type = 'expense' AND date LIKE ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlExpense)) {
                pstmt.setString(1, userEmail);
                pstmt.setString(2, datePattern);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) totalExpense = rs.getDouble(1);
            } catch (SQLException e) {
                System.out.println("Error fetching expense: " + e.getMessage());
            }

            double savings = totalIncome - totalExpense;

            double budgetUtilization = 0;
            String sqlBudget = "SELECT COALESCE(SUM(amount),0), COALESCE(SUM(remaining_budget),0) FROM budget WHERE user_email = ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlBudget)) {
                pstmt.setString(1, userEmail);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    double totalBudget = rs.getDouble(1);
                    double totalRemaining = rs.getDouble(2);
                    if (totalBudget > 0) {
                        budgetUtilization = ((totalBudget - totalRemaining) / totalBudget) * 100;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error fetching budget: " + e.getMessage());
            }

            StringBuilder rows = new StringBuilder();
            String sqlTxns = "SELECT date, amount, type FROM amount WHERE user_email = ? AND date LIKE ? ORDER BY date DESC";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlTxns)) {
                pstmt.setString(1, userEmail);
                pstmt.setString(2, datePattern);
                ResultSet rs = pstmt.executeQuery();
                int count = 0;
                while (rs.next() && count < 50) {
                    String txnDate = rs.getString("date");
                    if (txnDate != null && txnDate.length() >= 10) txnDate = txnDate.substring(0, 10);
                    double amt = rs.getDouble("amount");
                    String type = rs.getString("type");
                    String sign = type.equalsIgnoreCase("income") ? "" : "-";
                    rows.append(String.format(
                            "<tr><td>%s</td><td>%s</td><td>%s</td><td style='text-align:right'>PKR %s</td></tr>",
                            txnDate, type, type, sign + pkrFormat.format(amt)));
                    count++;
                }
            } catch (SQLException e) {
                System.out.println("Error fetching transactions: " + e.getMessage());
            }

            if (rows.length() == 0) {
                rows.append("<tr><td colspan='4' style='text-align:center'>No transactions for this period</td></tr>");
            }

            String[] monthNames = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            String monthName = monthNames[month - 1];

            return String.format(
                    "<!DOCTYPE html>" +
                    "<html><head><title>Monthly Financial Report</title><style>" +
                    "body{font-family:Geist,Arial,sans-serif;margin:20px;background:#f8f7fa;color:#3d3c4f;}" +
                    "h1{color:#8a79ab;text-align:center;}" +
                    "h2{color:#8a79ab;border-bottom:2px solid #cec9d9;padding-bottom:8px;}" +
                    "table{width:100%%;border-collapse:collapse;margin-bottom:20px;background:#fff;border-radius:8px;overflow:hidden;}" +
                    "th{background:#8a79ab;color:#f8f7fa;text-align:left;padding:12px;}" +
                    "td{padding:10px;border-bottom:1px solid #eae7f0;}" +
                    "tr:nth-child(even){background:#f1eff5;}" +
                    ".summary{display:flex;gap:16px;flex-wrap:wrap;margin-bottom:30px;}" +
                    ".summary-card{background:#fff;border-radius:8px;padding:20px;flex:1;min-width:180px;box-shadow:1px 2px 5px rgba(0,0,0,0.06);}" +
                    ".summary-label{font-size:14px;color:#6b6880;display:block;margin-bottom:4px;}" +
                    ".summary-value{font-size:24px;font-weight:bold;}" +
                    ".positive{color:#77b8a1;}.negative{color:#d95c5c;}.neutral{color:#8a79ab;}" +
                    "</style></head><body>" +
                    "<h1>Financial Report - %s %s</h1>" +
                    "<p style='text-align:center;color:#6b6880;'>Generated on %s</p>" +
                    "<div class='summary'>" +
                    "<div class='summary-card'><span class='summary-label'>Total Income</span><div class='summary-value positive'>PKR %s</div></div>" +
                    "<div class='summary-card'><span class='summary-label'>Total Expenses</span><div class='summary-value negative'>PKR %s</div></div>" +
                    "<div class='summary-card'><span class='summary-label'>Net Savings</span><div class='summary-value %s'>PKR %s</div></div>" +
                    "<div class='summary-card'><span class='summary-label'>Budget Utilization</span><div class='summary-value neutral'>%.0f%%</div></div>" +
                    "</div><h2>Transactions</h2><table>" +
                    "<tr><th>Date</th><th>Type</th><th>Category</th><th>Amount</th></tr>%s</table></body></html>",
                    monthName, year, currentDate,
                    pkrFormat.format(totalIncome), pkrFormat.format(totalExpense),
                    savings >= 0 ? "positive" : "negative", pkrFormat.format(Math.abs(savings)),
                    budgetUtilization, rows.toString());
        }
    }
}