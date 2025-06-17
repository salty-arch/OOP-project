package org.database.main;

import org.database.model.Admin;
import org.database.model.Client;

import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    //Initializing scanner
    public static Scanner cin = new Scanner(System.in);

    public static void main(String[] args) {
        //Initializing choices variable
        int choice = -1, choice2 = -1;

        while (choice != 0){
            System.out.println("1.Client\n2.Admin\n0.Exit");
            choice = cin.nextInt();
            switch (choice){
                case 1:
                    //declaring a object user of class Client
                    Client user = null;
                    try {
                        //constructing user
                        user = new Client();
                    }catch(IllegalArgumentException e){
                        System.out.println("Returning to main menu...");
                    }

                    //if constructor is implied smoothly then client menu is given
                    if(user != null){
                        user.menu();
                    }
                    break;
                case 2:
                    //yet to work on admin

                    Admin admin = null;
                    try {
                        //constructing user
                        admin = new Admin();
                    }catch(IllegalArgumentException e){
                        System.out.println("Returning to main menu...");
                    }

                    //if constructor is implied smoothly then client menu is given
                    if(admin != null){
                        admin.menu();
                    }
                case 0:
                    //exit
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}