package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class clientRegisterFrame extends JFrame {

    public clientRegisterFrame() {
        setTitle("Client Registration"); // window title
        setSize(350, 250); // window size
        setLocationRelativeTo(null); // center it
        setLayout(null); // no layout manager, manual positioning

        JLabel emailLabel = new JLabel("Email:"); // email label
        emailLabel.setBounds(30, 30, 100, 30);
        add(emailLabel);

        JTextField emailField = new JTextField(); // input for email
        emailField.setBounds(140, 30, 150, 30);
        add(emailField);

        JLabel passLabel = new JLabel("Password:"); // password label
        passLabel.setBounds(30, 80, 100, 30);
        add(passLabel);

        JPasswordField passField = new JPasswordField(); // input for password
        passField.setBounds(140, 80, 150, 30);
        add(passField);

        JButton registerButton = new JButton("Register"); // register button
        registerButton.setBounds(100, 140, 120, 30);
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText(); // get email text
                String password = new String(passField.getPassword()); // get password text

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields."); // missing info
                    return;
                }

                try (Connection conn = Databasehelper.connect()) { // open db connection
                    String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) { // insert user
                        pstmt.setString(1, email);
                        pstmt.setString(2, password);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Registration successful!"); // all good
                        dispose(); // close window
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); // something broke
                }
            }
        });

        setVisible(true); // show the window
    }
}
