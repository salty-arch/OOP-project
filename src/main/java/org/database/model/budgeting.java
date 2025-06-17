package org.database.model;

import org.database.util.Databasehelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class budgeting {
    private String email;

    public static void addBudget(String email, String category, double budget_limit) throws SQLException {
        String sql = "INSERT INTO budget (user_email, budget_category, amount, remaining_budget) VALUES (?, ?, ?, ?)";

        try (Connection conn = Databasehelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, category);
            stmt.setDouble(3, budget_limit);
            stmt.setDouble(4, budget_limit);
            stmt.executeUpdate();
        }
    }

    public static List<BudgetItem> getBudgets(String email) throws SQLException {
        List<BudgetItem> budgets = new ArrayList<>();
        String sql = "SELECT budget_category, amount, remaining_budget FROM budget WHERE user_email = ?";

        try (Connection conn = Databasehelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("budget_category");
                double amount = rs.getDouble("amount");
                double remaining = rs.getDouble("remaining_budget");
                double spent = amount - remaining;
                int progress = (int) ((spent / amount) * 100);

                budgets.add(new BudgetItem(category, amount, spent, remaining, progress));
            }
        }
        return budgets;
    }

    public static void bugeting(String email) {
    }

    public void viewBudgetsGUI() {
        JDialog viewBudgetDialog = new JDialog();
        viewBudgetDialog.setTitle("Your Budgets");
        viewBudgetDialog.setSize(600, 400);
        viewBudgetDialog.setLocationRelativeTo(null);
        viewBudgetDialog.setLayout(new BorderLayout());

        String[] columns = {"Category", "Initial Budget", "Remaining Budget"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        String sql = "SELECT budget_category, amount, remaining_budget FROM budget WHERE user_email = ?";
        try (Connection conn = Databasehelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getString("budget_category"),
                        String.format("%.2f", rs.getDouble("amount")),
                        String.format("%.2f", rs.getDouble("remaining_budget"))
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(viewBudgetDialog, "Error fetching budgets: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> viewBudgetDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        viewBudgetDialog.add(scrollPane, BorderLayout.CENTER);
        viewBudgetDialog.add(buttonPanel, BorderLayout.SOUTH);
        viewBudgetDialog.setVisible(true);
    }

    public static class BudgetItem {
        public final String category;
        public final double budget;
        public final double spent;
        public final double remaining;
        public final int progress;

        public BudgetItem(String category, double budget, double spent, double remaining, int progress) {
            this.category = category;
            this.budget = budget;
            this.spent = spent;
            this.remaining = remaining;
            this.progress = progress;
        }
    }
}