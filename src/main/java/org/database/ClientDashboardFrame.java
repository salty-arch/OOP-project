package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientDashboardFrame extends JFrame {

    private String email;

    public ClientDashboardFrame(String email) {
        this.email = email;

        setTitle("Client Dashboard");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JButton registerButton = new JButton("Register User");
        registerButton.setBounds(100, 30, 200, 30);
        add(registerButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBounds(100, 70, 200, 30);
        add(changePasswordButton);

        JButton enterAmountButton = new JButton("Enter Amount");
        enterAmountButton.setBounds(100, 110, 200, 30);
        add(enterAmountButton);

        JButton setBudgetButton = new JButton("Set Budget");
        setBudgetButton.setBounds(100, 150, 200, 30);
        add(setBudgetButton);

        JButton printAmountButton = new JButton("Print Amounts");
        printAmountButton.setBounds(100, 190, 200, 30);
        add(printAmountButton);

        JButton printBudgetButton = new JButton("Print Budgets");
        printBudgetButton.setBounds(100, 230, 200, 30);
        add(printBudgetButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(100, 270, 200, 30);
        add(logoutButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgramHelper.Register();
            }
        });

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email);
                client.changePasswordGUI();
            }
        });

        enterAmountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email);
                client.amountGUI();
            }
        });

        setBudgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                budgeting.bugeting(email);
            }
        });

        printAmountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email);
                client.printAmountGUI();
            }
        });

        printBudgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client(email);
                client.PrintBudgetActivity();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainFrame();
                dispose();
            }
        });

        setVisible(true);
    }
}
