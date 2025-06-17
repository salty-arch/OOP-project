package org.database.util;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.database.util.Databasehelper.connect;

public class ProgramHelper  //A class which will contain methods which are used in general by the program.
{
    private static final Scanner cin = new Scanner(System.in);

    public static void Register(){  //method to enter a user to the users table

        System.out.println("Enter email you want to register with:");
        String Email = cin.next();
        cin.nextLine();

        if (VerifyEmail(Email)) //if email is verified using VerifyEmail method
        {

            System.out.println("Enter the password u want to set:");
            String Password = cin.next();

            String sql = "INSERT INTO users (email,password,role) VALUES (?,?,?)";
            try (Connection conn = connect();
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

        else{   //if email is not verified by VerifyEmail method
            System.out.println("Invalid email format.");
        }
    }

    public static boolean VerifyEmail(String Email)     //a method which sets a pattern for valid email address
    {
        String regex = "^[\\w]+@[\\w]+\\.(com|edu|org|pk)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(Email);

        return match.matches();
    }

    public static boolean VerifyUser(String email, String password){
        String sql = "SELECT id FROM users WHERE email = ? AND password = ?";
        boolean user_exists = false;
        try (Connection conn = connect();
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

    public static void logActivity(String userEmail, String activityType, String details) {
        String sql = "INSERT INTO activity_log(user_email, activity_type, activity_details) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            pstmt.setString(2, activityType);
            pstmt.setString(3, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to log activity: " + e.getMessage());
        }
    }

    public static Map<String, Double> getBudgetSummary(String email) {
        Map<String, Double> summary = new HashMap<>();
        String sql = "SELECT SUM(amount) AS total, SUM(remaining_budget) AS remaining FROM budget WHERE user_email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                double remaining = rs.getDouble("remaining");
                double spent = total - remaining;

                summary.put("total", total);
                summary.put("remaining", remaining);
                summary.put("spent", spent);
            }

        } catch (SQLException e) {
            System.out.println("Error getting budget summary: " + e.getMessage());
        }

        return summary;
    }

    public static List<String> getAllUserActivities() {
        List<String> activityList = new ArrayList<>();
        String sql = "SELECT user_email, activity_type, activity_details, timestamp FROM activity_log ORDER BY timestamp DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String email = rs.getString("user_email");
                String activity_type = rs.getString("activity_type");
                String activity_detail = rs.getString("activity_details");
                String timestamp = rs.getString("timestamp");
                String activityStr = activity_type;
                if (activity_detail != null && !activity_detail.isEmpty()) {
                    activityStr += " (" + activity_detail + ")";
                }

                activityList.add(email + " → " + activityStr + " at " + timestamp);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving all user activities: " + e.getMessage());
        }

        return activityList;
    }

    public static List<String> getFinancialSummaryPerUser() {
        List<String> summaryList = new ArrayList<>();
        String sql = "SELECT user_email, COUNT(budget_category) AS category_count, " +
                "SUM(amount) AS total_budget, " +
                "SUM(remaining_budget) AS total_remaining " +
                "FROM budget " +
                "GROUP BY user_email";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String email = rs.getString("user_email");
                int categoryCount = rs.getInt("category_count");
                double totalBudget = rs.getDouble("total_budget");
                double totalRemaining = rs.getDouble("total_remaining");
                double totalSpent = totalBudget - totalRemaining;

                String summary = String.format(
                        "%s has %d budget categories with a total budget of PKR %.2f.\n" +
                                "Spent: PKR %.2f, Remaining: PKR %.2f",
                        email, categoryCount, totalBudget, totalSpent, totalRemaining
                );

                summaryList.add(summary);
            }

        } catch (SQLException e) {
            System.out.println("Error getting financial summary: " + e.getMessage());
        }
        return summaryList;
    }

    public static String getOverallBudgetStats() {
        String sqlCountUsers = "SELECT COUNT(DISTINCT user_email) AS total_users FROM budget";
        String sqlCountEntries = "SELECT COUNT(*) AS total_entries FROM budget";
        String sqlSumBudget = "SELECT SUM(amount) AS total_amount FROM budget";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rsUsers = stmt.executeQuery(sqlCountUsers);
            int totalUsers = rsUsers.next() ? rsUsers.getInt("total_users") : 0;

            ResultSet rsEntries = stmt.executeQuery(sqlCountEntries);
            int totalEntries = rsEntries.next() ? rsEntries.getInt("total_entries") : 0;

            ResultSet rsSum = stmt.executeQuery(sqlSumBudget);
            double totalAmount = rsSum.next() ? rsSum.getDouble("total_amount") : 0.0;

            double avgBudget = totalUsers > 0 ? totalAmount / totalUsers : 0.0;

            return String.format("Overall system statistics:\n" +
                            "- Total users: %d\n" +
                            "- Total budget entries: %d\n" +
                            "- Average budget per user: PKR %.2f",
                    totalUsers, totalEntries, avgBudget);

        } catch (SQLException e) {
            System.out.println("Error getting overall budget stats: " + e.getMessage());
            return "Failed to load overall stats.";
        }
    }

}
