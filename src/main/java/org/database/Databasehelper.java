package org.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Databasehelper {
    private static final String URL = "jdbc:sqlite:src/main/java/org/database/database.db";

    public static Connection connect(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e){
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void create_table(){
        String table = "CREATE TABLE IF NOT EXISTS users( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role in ('Client','Admin'))" +
                ");";
        String table_2 = "CREATE TABLE IF NOT EXISTS amount(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "type TEXT CHECK (type IN ('income','expense')), " +
                "amount REAL, " +
                "date TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_email) REFERENCES users(email)" +
                ");";
        String table_3 = "CREATE TABLE IF NOT EXISTS budget(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "budget_category TEXT, " +
                "amount REAL, " +
                "FOREIGN KEY (user_email) REFERENCES users(email)" +
                ");";
        try(Connection conn = connect(); Statement stmt = conn.createStatement()){
            stmt.execute(table);
            System.out.println("Table created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }
        try(Connection conn = connect(); Statement stmt2 = conn.createStatement()){
            stmt2.execute(table_2);
            System.out.println("Table 2 created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }
        try(Connection conn = connect(); Statement stmt2 = conn.createStatement()){
            stmt2.execute(table_3);
            System.out.println("Table 3 created!");
        } catch (SQLException e) {
            System.out.println("Creating table failed: " + e.getMessage());
        }
    }
}
