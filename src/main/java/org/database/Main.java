package org.database;

import javax.xml.crypto.Data;
import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static Scanner cin = new Scanner(System.in);
    public static void main(String[] args) {
        int choice = -1, choice2 = -1;
        Admin admin = new Admin();
        while (choice != 0){
            System.out.println("1.Client\n2.Admin\n0.Exit");
            choice = cin.nextInt();
            switch (choice){
                case 1:
                    Client user = null;
                    try {
                        user = new Client();
                    }catch(IllegalArgumentException e){
                        System.out.println("Returning to main menu...");
                    }

                    if(user != null){
                        user.menu();
                    }
                    break;
                case 2:
                    System.out.println("Haven't decided yet");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}