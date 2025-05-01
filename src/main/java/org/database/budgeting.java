package org.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class budgeting //Class to insert budget to the table (handles the insertion of budget table)
{
    public static Scanner cin = new Scanner(System.in);

    public static void bugeting(String email){
        System.out.println("Enter the category name: ");
        String category = cin.next();
        cin.nextLine();

        System.out.println("Enter the budget you want to set: ");
        double budget_limit = cin.nextDouble();
        cin.nextLine();

        String sql = "INSERT INTO budget (user_email,budget_category,amount,remaining_budget) VALUES (?,?,?,?)";

        try(Connection conn = Databasehelper.connect(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1,email);
            stmt.setString(2,category);
            stmt.setDouble(3,budget_limit);
            stmt.setDouble(4,budget_limit);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Inserting amount failed: " + e.getMessage());
        }
    }
}
