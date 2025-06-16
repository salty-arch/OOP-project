package org.database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

public class AdminLoginFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private Point initialClick;

    private JTextField emailField;
    private JPasswordField passwordField;

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(500, 400);
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
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title Label
        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, gbc);

        // Email Field
        JPanel emailPanel = createInputPanel("Email:");
        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        emailPanel.add(emailField);
        mainPanel.add(emailPanel, gbc);

        // Password Field
        JPanel passwordPanel = createInputPanel("Password:");
        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel, gbc);

        // Login Button
        JButton loginButton = createModernButton("Login");
        loginButton.addActionListener(this::loginAction);
        mainPanel.add(loginButton, gbc);

        // Back Button
        JButton backButton = createModernButton("Back");
        backButton.addActionListener(e -> {
            new MainFrame().setVisible(true);  // Fixed: Added setVisible(true)
            dispose();
        });
        mainPanel.add(backButton, gbc);

        // Drag functionality
        addDragFunctionality();
        setContentPane(mainPanel);
        setVisible(true);
    }

    private void loginAction(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Placeholder for actual authentication logic
        // Replace with your actual authentication code
        boolean isAuthenticated = authenticateAdmin(email, password);

        if (isAuthenticated) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            new AdminDashboardFrame(email).setVisible(true);  // Fixed: Added setVisible(true)
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateAdmin(String email, String password) {
        // Placeholder - implement actual admin authentication logic
        // This should check against your database or user store
        return "admin@gmail.com".equals(email) && "mjk".equals(password);
    }

    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        return panel;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLoginFrame());
    }
}