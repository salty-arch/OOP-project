package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Client Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 50, 80, 25);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(130, 50, 200, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 100, 200, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(130, 150, 120, 30);
        add(loginButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(130, 200, 120, 30);
        add(backButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (ProgramHelper.VerifyUser(email, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    new ClientDashboardFrame(email);
                    dispose(); // close login window
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password!");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainFrame();
                dispose();
            }
        });

        setVisible(true);
    }
}
