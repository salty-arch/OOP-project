package org.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Programhelper {
    private static final Scanner cin = new Scanner(System.in);

    public static void Register(){

        System.out.println("Enter email you want to register with:");
        String Email = cin.next();
        cin.nextLine();

        if (VerifyEmail(Email)){

            System.out.println("Enter the password u want to set:");
            String Password = cin.next();

            String sql = "INSERT INTO users (email,password,role) VALUES (?,?,?)";
            try (Connection conn = Databasehelper.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)){

                pstmt.setString(1,Email);
                pstmt.setString(2,Password);
                pstmt.setString(3,"Client");

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
            finally {
                System.out.println("User registered successfully!");
            }
        }

        else{
            System.out.println("Invalid email format.");
        }
    }

    public static boolean VerifyEmail(String Email){
        String regex = "^[\\w]+@[\\w]+\\.(com|edu|org|pk)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(Email);

        return match.matches();
    }

    public static boolean VerifyUser(String email, String password){
        String sql = "SELECT id FROM users WHERE email = ? AND password = ?";
        boolean user_exists = false;
        try (Connection conn = Databasehelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,email);
            pstmt.setString(2,password);

            ResultSet rs = pstmt.executeQuery();
            user_exists = rs.next();
        } catch (SQLException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
        return user_exists;
    }
}
