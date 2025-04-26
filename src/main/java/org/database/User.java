package org.database;

import java.util.Scanner;

public abstract class User {
    private final static Scanner scan = new Scanner(System.in);
    protected String email;
    protected String password;

    String[] get_Account(){
        System.out.println("Enter your email:");
        this.email = scan.next();
        scan.nextLine();

        System.out.println("Enter your password:");
        this.password = scan.next();
        scan.nextLine();

        return new String[]{email,password};
    }

    protected String[] getAccount() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
