package org.database.auth;


import org.database.util.Databasehelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordFrame extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);

    public ChangePasswordFrame(JFrame parent, String userEmail) {
        super(parent, "Change Password", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Change Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, gbc);

        // Current Password Field
        JLabel currentPassLabel = new JLabel("Current Password:");
        JPasswordField currentPassField = new JPasswordField();
        panel.add(currentPassLabel, gbc);
        panel.add(currentPassField, gbc);

        // New Password Field
        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField();
        panel.add(newPassLabel, gbc);
        panel.add(newPassField, gbc);

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener((ActionEvent e) -> {
            String oldPass = new String(currentPassField.getPassword()).trim();
            String newPass = new String(newPassField.getPassword()).trim();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            submitBtn.setEnabled(false);
            submitBtn.setText("Processing...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    String verifySql = "SELECT password FROM users WHERE email = ?";
                    String updateSql = "UPDATE users SET password = ? WHERE email = ?";

                    try (Connection conn = Databasehelper.connect()) {
                        try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
                            verifyStmt.setString(1, userEmail);
                            ResultSet rs = verifyStmt.executeQuery();

                            if (rs.next()) {
                                String dbPass = rs.getString("password");
                                if (!dbPass.equals(oldPass)) {
                                    return false; // Incorrect old password
                                }
                            } else {
                                return false; // User not found
                            }
                        }

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, newPass);
                            updateStmt.setString(2, userEmail);
                            int rows = updateStmt.executeUpdate();
                            return rows > 0;
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Password change failed. Incorrect old password or user not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ChangePasswordFrame.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        submitBtn.setEnabled(true);
                        submitBtn.setText("Submit");
                    }
                }
            }.execute();
        });
        panel.add(submitBtn, gbc);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        panel.add(cancelBtn, gbc);

        setContentPane(panel);
        setVisible(true);
    }
}
