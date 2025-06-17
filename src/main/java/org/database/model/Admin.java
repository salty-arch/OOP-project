package org.database.model;

import org.database.util.Databasehelper;
import org.database.util.ProgramHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin extends User {
    private static final Scanner cin = new Scanner(System.in);
    private String role = "Admin";

    private FinancialGoals goals;

    public Admin(){
        String[] account = get_Account();   //uses the get_Account method from User class
        this.email = account[0];    //email initialization
        this.password = account[1]; //password initialization

        if (!ProgramHelper.VerifyUser(this.email,this.password))    //uses VerifyUser method from inherited class(user)
        {
            System.out.println("Invalid Email or Password.");
            throw new IllegalArgumentException("Invalid credentials");  //to stop construction of an object if invalid credentials are given
        }
        else {  //if user verified then approves constructor creation
            System.out.println("User verified successfully.");
        }
    }

    @Override   //overrides get_Account method from user
    public String[] get_Account(){
        System.out.println("Admin sign in.");
        return super.get_Account();
    }


    void DeleteClientAcc(ActionEvent k){     //Method to delete Clients account
        System.out.println("Enter the email of the Account (Client) you want to remove: ");
        String email = cin.nextLine().trim();

        System.out.println("Enter your password again: ");
        String PassAgain = cin.nextLine().trim();

        String password = "SELECT password FROM users WHERE email = ?";
        String Delete = "DELETE FROM users WHERE email = ?";

        try(Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(password)){
            pstmt.setString(1,this.email);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                if(!rs.getString("password").equals(PassAgain)){
                    System.out.println("Invalid password.");
                    return;
                }
                else{
                    System.out.println("Password verified.");
                    try(PreparedStatement Pstmt = conn.prepareStatement(Delete)){
                        Pstmt.setString(1,email);
                        int rows = Pstmt.executeUpdate();
                        if (rows > 0) {
                            System.out.println("User account deleted successfully.");
                        } else {
                            System.out.println("No user found with the provided email.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Deleting user failed: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Verifying password failed: " + e.getMessage());
        }
    }
    public void changePasswordGUI() {
        String new_pass = JOptionPane.showInputDialog(null, "Enter new password:");

        if (new_pass != null && !new_pass.isEmpty()) {
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, new_pass);
                pstmt.setString(2, this.email);
                int updatedRows = pstmt.executeUpdate();

                if (updatedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Password change failed.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Password change cancelled or invalid input.");
        }
    }
}
