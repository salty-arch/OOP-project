package org.database.util;
import java.sql.*;


public class Databasehelper {   //class for database creation and modification
    private static final String URL = "jdbc:sqlite:src/main/java/org/database/database.db";

    public static Connection connect(){  //method to connect with the sql database
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(URL);    //Tries to create a new connection to the database (URL contains path to the database)
        } catch (SQLException e){
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    static Connection conn = connect();

    public static void create_table(){
        // SQL statement to create the 'users' table if it doesn't already exist
        String table = "CREATE TABLE IF NOT EXISTS users( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role in ('Client','Admin'))" +
                ");";
        // SQL statement to create the 'amount' table to track income and expenses
        String table_2 = "CREATE TABLE IF NOT EXISTS amount(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "type TEXT CHECK (type IN ('income','expense')), " +
                "amount REAL, " +
                "date TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_email) REFERENCES users(email)" +
                ");";
        // SQL statement to create the 'budget' table for managing budgets
        String table_3 = "CREATE TABLE IF NOT EXISTS budget(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "budget_category TEXT, " +
                "amount REAL, " +
                "FOREIGN KEY (user_email) REFERENCES users(email)" +
                ");";
        //SQL statement to create the "Financial goals" table for managing goals
        String table_4 = "CREATE TABLE IF NOT EXISTS goals(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT NOT NULL, " +
                "goal_type TEXT NOT NULL CHECK (goal_type IN ('saving', 'limit spending'))," +
                "category TEXT," +
                "amount REAL NOT NULL," +
                "deadline TEXT NOT NULL," +
                "status  TEXT DEFAULT 'active');";

        // Attempt to create the 'users' table
        try(Statement stmt = conn.createStatement()){
            stmt.execute(table_4);
            System.out.println("Table 4 created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }

        // Attempt to create the 'amount' table
        try(Statement stmt2 = conn.createStatement()){
            stmt2.execute(table_2);
            System.out.println("Table 2 created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }

        // Attempt to create the 'budget' table
        try(Statement stmt2 = conn.createStatement()){
            stmt2.execute(table_3);
            System.out.println("Table 3 created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }
    }

    public static void create_activity_log_table() {
        String sql = """
        CREATE TABLE IF NOT EXISTS activity_log (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_email TEXT NOT NULL,
            activity_type TEXT NOT NULL,
            activity_details TEXT,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Activity log table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Failed to create activity log table: " + e.getMessage());
        }
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
}
