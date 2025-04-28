package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JButton clientButton = new JButton("Client");
        clientButton.setBounds(130, 50, 120, 30);
        add(clientButton);

        JButton adminButton = new JButton("Admin");
        adminButton.setBounds(130, 100, 120, 30);
        add(adminButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(130, 150, 120, 30);
        add(exitButton);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame(); // Go to login window
                dispose(); // close main menu
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Under Construction.");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        Databasehelper.create_table(); // create tables before anything
        new MainFrame(); // Launch main menu
    }
}
