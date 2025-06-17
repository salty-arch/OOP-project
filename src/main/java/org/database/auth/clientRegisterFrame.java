package org.database.auth;

import org.database.util.Databasehelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class ClientRegisterFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 102, 0);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 240);

    private JTextField emailField;
    private JPasswordField passwordField;
    private Point initialClick;

    public ClientRegisterFrame() {
        setTitle("Client Registration");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Added proper close operation
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
        JLabel titleLabel = new JLabel("Client Registration", SwingConstants.CENTER);
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

        // Register Button
        JButton registerButton = createModernButton("Register");
        registerButton.addActionListener(this::registerAction);
        mainPanel.add(registerButton, gbc);

        // Back Button
        JButton backButton = createModernButton("Back");
        backButton.addActionListener(e -> {
            new LoginFrame("Client").setVisible(true);  // Added setVisible(true)
            dispose();
        });
        mainPanel.add(backButton, gbc);

        // Drag functionality for undecorated window
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
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

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.WEST);

        return panel;
    }

    private void registerAction(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {  // Changed from ProgramHelper.VerifyEmail to local method
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Databasehelper.connect()) {  // Fixed typo in DatabaseHelper
            String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);  // Note: Should hash password in production
                pstmt.setString(3, "Client");
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful!");
                new LoginFrame("Client").setVisible(true);  // Added setVisible(true)
                dispose();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique constraint")) {
                JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email validation - replace with proper validation
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background with hover effect
                g2.setColor(isHovered ? new Color(52, 152, 219) : PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }

            {
                // Initializer block for mouse listeners
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
}