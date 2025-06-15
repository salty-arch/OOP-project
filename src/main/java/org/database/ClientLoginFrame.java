package org.database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class ClientLoginFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);

    private JTextField emailField;
    private JPasswordField passwordField;

    public ClientLoginFrame() {
        setTitle("Client Login");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title Label
        JLabel titleLabel = new JLabel("Client Login", SwingConstants.CENTER);
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

        // Register Button
        JButton registerButton = createModernButton("Register");
        registerButton.addActionListener(e -> {
            new ClientRegisterFrame();
            dispose();
        });
        mainPanel.add(registerButton, gbc);

        // Back Button
        JButton backButton = createModernButton("Back");
        backButton.addActionListener(e -> {
            new MainFrame();
            dispose();
        });
        mainPanel.add(backButton, gbc);

        // Drag functionality for undecorated window
        addMouseListener(new MouseAdapter() {
            private Point initLocation;

            @Override
            public void mousePressed(MouseEvent e) {
                initLocation = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initLocation.x;
                int yMoved = e.getY() - initLocation.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void loginAction(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (ProgramHelper.VerifyUser(email, password)) {
            new ClientDashboardFrame(email);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.WEST);

        return panel;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(isHovered ? new Color(52, 152, 219) : PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }

            @Override
            public void updateUI() {
                super.updateUI();
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