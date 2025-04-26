package org.database;

import java.sql.*;
import java.util.regex.*;
import java.util.Scanner;

public class Client extends User {
    private static final Scanner cin = new Scanner(System.in);
    private String role = "Client";

    Client(){
        String[] account = get_Account();
        this.email = account[0];
        this.password = account[1];
        if (!Programhelper.VerifyUser(this.email,this.password)) {
            System.out.println("Invalid Email or Password.");
            throw new IllegalArgumentException("Invalid credentials");
        }
        else {
            System.out.println("User verified successfully.");
        }
    }

    @Override
    public String[] get_Account(){
        System.out.println("Client sign in.");
        return super.get_Account();
    }

    public void menu(){
        int choice2 = -1;

        while (choice2 != 0) {
            choice2 = -1;
            System.out.println("1.User registration\n2.Change password\n3.Enter amount\n4.Set budget\n5.Print amount" +
                    "\n6.Print budget\n0.Exit");
            choice2 = cin.nextInt();

            switch (choice2) {
                case 1:
                    Programhelper.Register();
                    break;
                case 2:
                    Change_pass();
                    break;
                case 3:
                    amount();
                    break;
                case 4:
                    budgeting.bugeting(this.email);
                    break;
                case 5:
                    PrintAmount();
                    break;
                case 6:
                    Printbudget();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
            }
        }
    }

    private void Change_pass(){

        System.out.println("Enter new password:");
        String new_pass = cin.next();

        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = Databasehelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,new_pass);
            pstmt.setString(2,this.email);
            int updatedRows = pstmt.executeUpdate();

            if (updatedRows > 0) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Password change failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error occured: " + e.getMessage());
        }
    }

    private void amount(){

        System.out.println("Enter the amount you want to add:");
        double amount = cin.nextDouble();

        String amount_type;
        while(true) {
            System.out.println("Enter amount type (Income,expense):");
            amount_type = cin.next();
            cin.nextLine();
            if (!amount_type.equalsIgnoreCase("Income") && !amount_type.equalsIgnoreCase("expense")) {
                System.out.println("Enter valid amount type.");
            }
            else if(amount_type.equalsIgnoreCase("expense")){
                System.out.println("Enter the category for your expense:");
                String category = cin.nextLine();

                String updatesql = "UPDATE budget SET remaining_budget = remaining_budget - ? WHERE user_email = ? AND budget_category = ?";
                try (Connection conn = Databasehelper.connect(); PreparedStatement stmt = conn.prepareStatement(updatesql)) {
                    stmt.setDouble(1, amount);
                    stmt.setString(2, this.email);
                    stmt.setString(3, category);
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        System.out.println("No matching budget found for the given category.");
                    }
                } catch (SQLException e) {
                    System.out.println("Updating remaining budget failed: " + e.getMessage());
                }
                break;
            }
        }

        String sql = "INSERT INTO amount (user_email,type,amount) VALUES (?,?,?)";
        try(Connection conn = Databasehelper.connect(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1,this.email);
            stmt.setString(2,amount_type);
            stmt.setDouble(3,amount);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Inserting amount failed: " + e.getMessage());
        }
        System.out.println("Amount entered!");
    }


    private void PrintAmount(){
        String sql = "SELECT * FROM amount WHERE user_email = ?";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,this.email);
            ResultSet row = pstmt.executeQuery();
            boolean Data = false;

            System.out.println("Your Transactions");
            System.out.println("----------------------------");
            while (row.next()){
                Data = true;
                System.out.println("Amount: " + row.getString("amount") +
                        " | Type: " + row.getString("type") +
                        " | Date: " + row.getString("date"));
            }

            if(!Data){
                System.out.println("No transactions found.");
            }

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }



    public void Printbudget(){
        String sql = "SELECT * FROM budget WHERE user_email = ?";
        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,this.email);
            ResultSet row = pstmt.executeQuery();
            boolean Data = false;

            System.out.println("Your Transactions");
            System.out.println("----------------------------");
            while (row.next()){
                Data = true;
                System.out.println("Category: " + row.getString("budget_category") +
                        " | Initial Budget: " + row.getString("amount") +
                        " | Remaining Budget: " + row.getString("remaining_budget"));
            }

            if(!Data){
                System.out.println("No transactions found.");
            }

        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
