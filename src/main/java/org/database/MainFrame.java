package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class

MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Main Menu"); // window title
        setSize(400, 300); // setting size of window
        setDefaultCloseOperation(EXIT_ON_CLOSE); // to kill app when closing
        setLocationRelativeTo(null); // center the window
        setLayout(null); // no layout manager (doing it manually)

        JButton clientButton = new JButton("Client"); // button for client stuff
        clientButton.setBounds(130, 50, 120, 30); // position+size
        add(clientButton); // throw it on the frame

        JButton adminButton = new JButton("Admin"); // button for admin stuff
        adminButton.setBounds(130, 100, 120, 30);
        add(adminButton);

        JButton exitButton = new JButton("Exit"); // button to leave the app
        exitButton.setBounds(130, 150, 120, 30);
        add(exitButton);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame(); // jump to login
                dispose(); // kill this window
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Under Construction."); // not ready yet lol
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // to shut everything down
            }
        });

        setVisible(true); // show the window
    }

    public static void main(String[] args) {
        Databasehelper.create_table(); // make sure db tables exist
        new MainFrame(); // fire up the main menu
    }
}
