package org.database;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminDashboardFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private Point initialClick;

    private final String email;

    public AdminDashboardFrame(String email) {
        this.email = email;

        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = createIconButton("X");
        closeButton.addActionListener(e -> System.exit(0));
        headerPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Card 1: User Management
        JPanel userCard = createDashboardCard("User Management", "Manage client accounts");
        JButton deleteUserBtn = createCardButton("Delete Client Account");
        deleteUserBtn.addActionListener(this::deleteClientAccount);

        JButton viewUsersBtn = createCardButton("View All Users");
        viewUsersBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Salman \nAhmad \nHamza");
        });

        userCard.add(deleteUserBtn);
        userCard.add(viewUsersBtn);
        contentPanel.add(userCard);

        // Card 2: System Reports
        JPanel reportsCard = createDashboardCard("System Reports", "View system statistics and reports");
        JButton financialReportBtn = createCardButton("Financial Report");
        financialReportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Salman has food and water registered in budget. \nAhmad has zero items registered for budget. \nHamza has zero items registered for budget.");
        });

        JButton userActivityBtn = createCardButton("User Activity");
        userActivityBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Client 'salman' logged 1 min ago");
        });

        reportsCard.add(financialReportBtn);
        reportsCard.add(userActivityBtn);
        contentPanel.add(reportsCard);

        // Card 3: System Settings
        JPanel settingsCard = createDashboardCard("System Settings", "Configure system parameters");
        JButton changePassBtn = createCardButton("Change Password");
        changePassBtn.addActionListener(e -> {
            Client client = new Client(email, true);
            client.changePasswordGUI();
        });

        settingsCard.add(changePassBtn);
        contentPanel.add(settingsCard);

        // Card 4: Logout
        JPanel logoutCard = createDashboardCard("Session", "Manage your admin session");
        JButton logoutBtn = createCardButton("Logout");
        logoutBtn.addActionListener(e -> {
            new MainFrame().setVisible(true);  // Fixed: Added setVisible(true)
            dispose();
        });

        logoutCard.add(logoutBtn);
        contentPanel.add(logoutCard);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        addDragFunctionality();
        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createDashboardCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JButton createCardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        return button;
    }

    private void deleteClientAccount(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Delete Client Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Delete Client Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, gbc);

        JPanel emailPanel = createInputPanel("Client Email:");
        JTextField clientEmailField = new JTextField();
        clientEmailField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        emailPanel.add(clientEmailField);
        panel.add(emailPanel, gbc);

        JPanel passwordPanel = createInputPanel("Your Password:");
        JPasswordField adminPasswordField = new JPasswordField();
        adminPasswordField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        passwordPanel.add(adminPasswordField);
        panel.add(passwordPanel, gbc);

        JButton deleteButton = createModernButton("Delete Account");
        deleteButton.addActionListener(evt -> {
            String clientEmail = clientEmailField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword()).trim();

            if (clientEmail.isEmpty() || adminPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Disable button during operation
            deleteButton.setEnabled(false);
            deleteButton.setText("Processing...");

            // Use SwingWorker for database operations
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    String passwordQuery = "SELECT password FROM users WHERE email = ?";
                    String deleteQuery = "DELETE FROM users WHERE email = ?";

                    try (Connection conn = Databasehelper.connect();
                         PreparedStatement pstmt = conn.prepareStatement(passwordQuery)) {

                        // Verify admin password
                        pstmt.setString(1, email); // Current admin's email
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            if (!rs.getString("password").equals(adminPassword)) {
                                return false; // Password doesn't match
                            }

                            // Password verified, proceed with deletion
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                deleteStmt.setString(1, clientEmail);
                                int rowsAffected = deleteStmt.executeUpdate();
                                return rowsAffected > 0;
                            }
                        }
                        return false; // Admin not found (shouldn't happen)
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        throw new Exception("Database error: " + ex.getMessage());
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(dialog,
                                    "Client account deleted successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Deletion failed. Either password is incorrect or client doesn't exist.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                                "Error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        deleteButton.setEnabled(true);
                        deleteButton.setText("Delete Account");
                    }
                }
            }.execute();
        });
        panel.add(deleteButton, gbc);

        JButton cancelButton = createModernButton("Cancel");
        cancelButton.addActionListener(evt -> dialog.dispose());
        panel.add(cancelButton, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(PRIMARY_COLOR);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private void addDragFunctionality() {
        initialClick = null;

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        });
    }
}