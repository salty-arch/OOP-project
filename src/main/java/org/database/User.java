package org.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public abstract class User {    //abstract class to group common methods and variables
    private final static Scanner scan = new Scanner(System.in);
    //common variables
    protected String email;
    protected String password;

    //common method to be used by client and admin for sign-in
    String[] get_Account() {
        System.out.println("Enter your email:");
        this.email = scan.next();
        scan.nextLine();

        System.out.println("Enter your password:");
        this.password = scan.next();
        scan.nextLine();

        return new String[]{email, password};
    }

    void menu() {
    }

    //Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return getType();
    }
}