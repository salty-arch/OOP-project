package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientDashboardFrame extends JFrame {

    private String email; // Gotta keep track of who logged in

    public ClientDashboardFrame(String email) {
        this.email = email;

        setTitle("Client Dashboard"); // Window title
        setSize(400, 400); // Window size
        setDefaultCloseOperation(EXIT_ON_CLOSE); // to kill app on close
        setLocationRelativeTo(null); // Puts window in the middle
        setLayout(null); // no layout manager (doing it manually)

        JButton registerButton = new JButton("Register User"); // Creating button
        registerButton.setBounds(100, 30, 200, 30); // Button location and size
        add(registerButton); // Puts the button on the screen

        JButton changePasswordButton = new JButton("Change Password"); // Another button
        changePasswordButton.setBounds(100, 70, 200, 30); // Its spot and size
        add(changePasswordButton); // On the screen it goes

        JButton enterAmountButton = new JButton("Enter Amount"); // Button
        enterAmountButton.setBounds(100, 110, 200, 30); // Location and dimensions
        add(enterAmountButton); // Adding it

        JButton setBudgetButton = new JButton("Set Budget"); // Another button
        setBudgetButton.setBounds(100, 150, 200, 30); // Where it sits
        add(setBudgetButton); // Putting it there

        JButton printAmountButton = new JButton("Print Amounts"); // Button
        printAmountButton.setBounds(100, 190, 200, 30); // Its place
        add(printAmountButton); // Adding it in

        JButton printBudgetButton = new JButton("Print Budgets"); // One more button
        printBudgetButton.setBounds(100, 230, 200, 30); // Where it's located
        add(printBudgetButton); // Putting it on the frame

        JButton logoutButton = new JButton("Logout"); // Last button for now
        logoutButton.setBounds(100, 270, 200, 30); // Its spot
        add(logoutButton); // Adding it to the UI

        registerButton.addActionListener(new ActionListener() { // What happens when you click it
            @Override
            public void actionPerformed(ActionEvent e) {
                new clientRegisterFrame(); // <-- CALLS THE GUI instead of console
            }
        });

        changePasswordButton.addActionListener(new ActionListener() { // Click action
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email, true); // Use the new constructor
                client.changePasswordGUI(); // Shows the password change screen
            }
        });

        enterAmountButton.addActionListener(new ActionListener() { // On button click
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email, true); // New client object
                client.amountGUI(); // Shows the amount entry thingy
            }
        });

        setBudgetButton.addActionListener(new ActionListener() { // When you press it
            @Override
            public void actionPerformed(ActionEvent e) {
                budgeting.bugeting(email); // Does the budgeting stuff
            }
        });

        printAmountButton.addActionListener(new ActionListener() { // Click this one
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email, true); // Client object again
                client.printAmountGUI(); // Shows the print amounts UI
            }
        });

        printBudgetButton.addActionListener(new ActionListener() { // Hit this button
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email, true); // Make a client
                client.PrintBudgetActivity(); // Shows the print budget screen
            }
        });

        logoutButton.addActionListener(new ActionListener() { // When this is clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainFrame(); // Goes back to the main screen
                dispose(); // Gets rid of this window
            }
        });

        setVisible(true); // Makes the window show up
    }
}