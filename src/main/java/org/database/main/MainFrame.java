package org.database.main;

import org.database.auth.AdminLoginFrame;
import org.database.auth.ClientLoginFrame;
import org.database.util.Databasehelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 102, 0);
    private static final Color HOVER_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 240);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public MainFrame() {
        setTitle("Financial Manager");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        // Main panel with shadow
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2d.setColor(SHADOW_COLOR);
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 25, 25);

                // Background
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);

        JLabel titleLabel = new JLabel("Financial Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitle = new JLabel("Select your role", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.GRAY);
        mainPanel.add(subtitle, gbc);

        // Buttons
        JButton clientButton = createModernButton("Client");
        clientButton.addActionListener(e -> {
            new ClientLoginFrame();
            dispose();
        });
        mainPanel.add(clientButton, gbc);

        JButton adminButton = createModernButton("Admin");
        adminButton.addActionListener(e -> {
            new AdminLoginFrame();
            dispose();
        });
        mainPanel.add(adminButton, gbc);

        JButton exitButton = createModernButton("Exit");
        exitButton.setBackground(new Color(200, 55, 55));
        exitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(exitButton, gbc);

        // Drag window functionality
        MouseAdapter ma = new MouseAdapter() {
            private Point initLocation;

            @Override
            public void mousePressed(MouseEvent e) {
                initLocation = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int xMoved = e.getX() - initLocation.x;
                int yMoved = e.getY() - initLocation.y;
                setLocation(getLocation().x + xMoved, getLocation().y + yMoved);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Color transition
                g2.setColor(isHovered ? HOVER_COLOR : PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
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
        button.setPreferredSize(new Dimension(200, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        Databasehelper.create_activity_log_table();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}
