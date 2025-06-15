package org.database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Scanner;

public class FinancialGoals {
    public static Scanner cin = new Scanner(System.in);
    private String goalType;
    private String category; // This can be null
    private double targetAmount;
    private String deadline;
    private String status;
    private String useremail;
    private String email;


    public boolean setGoal(String goalType, String category, double amount, String deadline, String status) {
        String sql = "INSERT INTO goals (user_email, goal_type, category, amount, deadline, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            pstmt.setString(2, goalType);
            pstmt.setString(3, (category == null || category.isEmpty()) ? null : category);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, deadline);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error saving goal: " + e.getMessage());
            return false;
        }
    }


    public FinancialGoals(String email) {
        this.useremail = email;
    }

    public void SetGoal() {
        while (true) {
            System.out.println("Enter your goal(saving/limit spending):");
            goalType = cin.nextLine().trim();

            if (!goalType.equalsIgnoreCase("saving") && !goalType.equalsIgnoreCase("limit spending")) {
                System.out.println("Enter valid goal.");
            } else {
                break;
            }
        }

        System.out.println("Enter category(Leave it if your goal is to save in general):");
        category = cin.nextLine().trim();

        System.out.println("Enter your target amount:");
        targetAmount = cin.nextDouble();
        cin.nextLine();

        while (true) {
            System.out.println("Enter your deadline(YYYY-MM-DD):");
            String date = cin.nextLine().trim();

            try {
                deadline = String.valueOf(LocalDate.parse(date));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use YYYY-MM-DD format.");
            }
        }

        String sql = "INSERT INTO goals (user_email, goal_type, category, amount, deadline, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            pstmt.setString(2, goalType);
            pstmt.setString(3, category.isEmpty() ? null : category);
            pstmt.setDouble(4, targetAmount);
            pstmt.setString(5, deadline);
            pstmt.setString(6, "active");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Setting goal failed: " + e.getMessage());
        }
    }

    void checkDeadline() {
        LocalDate today = LocalDate.now();

        String sql = "SELECT id, goal_type, deadline FROM goals WHERE user_email = ? AND status = 'active'";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            ResultSet rst = pstmt.executeQuery();

            if (!rst.next()) {
                System.out.println("No available deadlines.");
            }

            while (rst.next()) {
                int goal_id = rst.getInt("id");
                String goal = rst.getString("goal_type");
                String deadlinestr = rst.getString("deadline");
                System.out.println("Checking deadlines for user: " + this.useremail);
                try {
                    LocalDate deadlineDate = LocalDate.parse(deadlinestr);
                    if (today.isAfter(deadlineDate)) {
                        System.out.println("Goal id: " + goal_id +
                                "Goal type: " + goal + " Status: missed.");

                        updateStatusGoal(goal_id);
                    } else {
                        System.out.println("Goal id: " + goal_id +
                                " Goal type: " + goal + " Status: active.");
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format for goal ID " + goal_id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Checking deadline failed: " + e.getMessage());
        }
    }

    void updateStatusGoal(int goalid) {
        String sql = "UPDATE goals  SET status = 'missed' WHERE id = ?";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goalid);
            pstmt.executeUpdate();
            System.out.println("Goal " + goalid + " has been marked as missed.");
        } catch (SQLException e) {
            System.out.println("Updating goal status failed: " + e.getMessage());
        }
    }

    void Goalcompletion() {
        String sql = "SELECT id, goal_type, category, amount, status FROM goals WHERE user_email = ? AND status = 'active'";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String goalType = rs.getString("goal_type");
                String category = rs.getString("category");
                double targetAmount = rs.getDouble("amount");

                double Currentvalue = 0;

                if (goalType.equalsIgnoreCase("saving")) {
                    Currentvalue = Client.getTotalincome(this.useremail) - Client.getTotalexpense(this.useremail);

                    if (Currentvalue >= targetAmount) {
                        MarkasCompleted(conn, id);
                        System.out.println("Currentvalue: " + Currentvalue + ", Target Amount: " + targetAmount);
                    }
                } else if (goalType.equalsIgnoreCase("limit spending") && category != null) {
                    Currentvalue = GetCategoryExpense(category);

                    if (Currentvalue >= targetAmount) {
                        MarkasCompleted(conn, id);
                        System.out.println("Currentvalue: " + Currentvalue + ", Target Amount: " + targetAmount);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Extracting goal info failed: " + e.getMessage());
        }
    }

    void MarkasCompleted(Connection conn, int id) {
        String sql = "UPDATE goals SET status = 'completed' WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Marking task as completed failed: " + e.getMessage());
        }
    }

    double GetCategoryExpense(String category) {
        double current_expense;
        String sql = "SELECT amount,remaining_budget FROM budget WHERE user_email = ? AND budget_category = ?";

        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            pstmt.setString(2, category);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double amount_limit = rs.getDouble("amount");
                double amount_remaining = rs.getDouble("remaining_budget");
                current_expense = amount_limit - amount_remaining;
                return current_expense;
            }
        } catch (SQLException e) {
            System.out.println("Getting info from budget table failed: " + e.getMessage());
        }
        return 0.0;
    }

    void viewgoalsbyStatus(String Status) {
        String sql = "SELECT id,goal_type,category,amount,deadline FROM goals WHERE user_email = ? AND status = ?";

        boolean found = false;

        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            pstmt.setString(2, Status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Type: " + rs.getString("goal_type"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Amount: " + rs.getDouble("amount"));
                System.out.println("Deadline: " + rs.getString("deadline"));
                System.out.println("---------------------");
            }

            if (!found) {
                System.out.println("No records found.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to obtain goals: " + e.getMessage());
        }
    }

    public String getDeadline() {
        return deadline;
    }
}