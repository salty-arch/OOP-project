package org.database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Client Login"); // window title
        setSize(400, 300); // window size
        setDefaultCloseOperation(EXIT_ON_CLOSE); // to kill app on close
        setLocationRelativeTo(null); // center it
        setLayout(null); // manual layout

        JLabel emailLabel = new JLabel("Email:"); // label for email
        emailLabel.setBounds(50, 50, 80, 25); // position+size
        add(emailLabel);

        emailField = new JTextField(); // input for email
        emailField.setBounds(130, 50, 200, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:"); // label for password
        passwordLabel.setBounds(50, 100, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField(); // input for password
        passwordField.setBounds(130, 100, 200, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login"); // login button
        loginButton.setBounds(130, 150, 120, 30);
        add(loginButton);

        JButton backButton = new JButton("Back"); // back to main menu button
        backButton.setBounds(130, 200, 120, 30);
        add(backButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText(); // grab email
                String password = new String(passwordField.getPassword()); // grab password

                if (ProgramHelper.VerifyUser(email, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!"); // good to go
                    new ClientDashboardFrame(email); // open dashboard
                    dispose(); // kill login window
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password!"); // nope
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainFrame(); // back to main menu
                dispose(); // close login window
            }
        });

        setVisible(true); // show the window
    }
}
