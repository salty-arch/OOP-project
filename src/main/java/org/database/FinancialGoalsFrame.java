package org.database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.List;


import static java.awt.SystemColor.text;
import static org.database.Main.cin;


public class FinancialGoalsFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);

    private String goalType;
    private String category; // This can be null
    private double targetAmount;
    private String deadline;
    private String status;
    private String useremail;

    private final String email;

    public FinancialGoalsFrame(String email) {
        this.email = email;

        setTitle("Financial Goals");
        setSize(600, 500);
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

        JLabel titleLabel = new JLabel("Financial Goals", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = createIconButton("←");
        backButton.addActionListener(e -> {
            new ClientDashboardFrame(email);
            dispose();
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Goal Type Selection
        JPanel goalTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        goalTypePanel.setOpaque(false);

        JButton savingGoalBtn = createModernButton("Set Saving Goal");
        savingGoalBtn.addActionListener(e -> showGoalForm("saving"));

        JButton spendingLimitBtn = createModernButton("Set Spending Limit");
        spendingLimitBtn.addActionListener(e -> showGoalForm("limit spending"));

        goalTypePanel.add(savingGoalBtn);
        goalTypePanel.add(spendingLimitBtn);
        contentPanel.add(goalTypePanel);

        // Goal Status Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(PRIMARY_COLOR);

        // Active Goals Tab
        JPanel activeGoalsPanel = new JPanel(new BorderLayout());
        activeGoalsPanel.setBackground(Color.WHITE);
        // Add table or list of active goals here
        tabbedPane.addTab("Active Goals", activeGoalsPanel);

        // Completed Goals Tab
        JPanel completedGoalsPanel = new JPanel(new BorderLayout());
        completedGoalsPanel.setBackground(Color.WHITE);
        // Add table or list of completed goals here
        tabbedPane.addTab("Completed Goals", completedGoalsPanel);

        // Missed Goals Tab
        JPanel missedGoalsPanel = new JPanel(new BorderLayout());
        missedGoalsPanel.setBackground(Color.WHITE);
        // Add table or list of missed goals here
        tabbedPane.addTab("Missed Goals", missedGoalsPanel);

        contentPanel.add(tabbedPane);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Drag functionality
        addDragFunctionality();
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

    public void showGoalForm(String goalType) {
        JDialog dialog = new JDialog(this, "Set " + (goalType.equals("saving") ? "Saving Goal" : "Spending Limit"), true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 400, 20, 20));

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

        JLabel titleLabel = new JLabel("Set " + (goalType.equals("saving") ? "Saving Goal" : "Spending Limit"), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, gbc);

        JTextField categoryField = new JTextField();
        if (goalType.equals("limit spending")) {
            JPanel categoryPanel = createInputPanel("Category:");
            categoryField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            categoryPanel.add(categoryField);
            panel.add(categoryPanel, gbc);
        }

        JPanel amountPanel = createInputPanel("Target Amount:");
        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        amountPanel.add(amountField);
        panel.add(amountPanel, gbc);

        JPanel deadlinePanel = createInputPanel("Deadline (YYYY-MM-DD):");
        JTextField deadlineField = new JTextField();
        deadlineField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        deadlinePanel.add(deadlineField);
        panel.add(deadlinePanel, gbc);

        JButton submitButton = createModernButton("Submit");
        JTextField finalCategoryField = categoryField; // for lambda
        submitButton.addActionListener(e -> {
            String category = goalType.equals("limit spending") ? finalCategoryField.getText().trim() : null;
            String amountText = amountField.getText().trim();
            String deadline = deadlineField.getText().trim();

            // Validation
            if (goalType.equals("limit spending") && (category == null || category.isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Category is required for spending limit goals.");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Enter a valid positive amount.");
                return;
            }

            try {
                LocalDate.parse(deadline); // will throw if invalid
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid deadline format. Use YYYY-MM-DD.");
                return;
            }

            // Save to database
            FinancialGoals goals = new FinancialGoals(email);
            boolean success = goals.setGoal(goalType, category, amount, deadline, "active");
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Goal set successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to save goal. Try again.");
            }
        });
        panel.add(submitButton, gbc);

        JButton cancelButton = createModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        panel.add(cancelButton, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }


        private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.WEST);

        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    g2.setColor(new Color(220, 220, 220));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }

                // Text
                g2.setColor(isHovered ? PRIMARY_COLOR : new Color(150, 150, 150));
                g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
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
        button.setPreferredSize(new Dimension(30, 30));
        return button;
    }

    private void addDragFunctionality() {
        MouseAdapter ma = new MouseAdapter() {
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
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void SetGoal() {
        while (true) {
            System.out.println("Enter your goal(saving/limit spending):");
            goalType = cin.nextLine().trim();

            if (!goalType.equalsIgnoreCase("saving") && !goalType.equalsIgnoreCase("limit spending")) {
                System.out.println("Enter valid goal.");
            } else {
                break;
            }
        }

        System.out.println("Enter category(Leave it if your goal is to save in general):");
        category = cin.nextLine().trim();

        System.out.println("Enter your target amount:");
        targetAmount = cin.nextDouble();
        cin.nextLine();

        while (true) {
            System.out.println("Enter your deadline(YYYY-MM-DD):");
            String date = cin.nextLine().trim();

            try {
                deadline = String.valueOf(LocalDate.parse(date));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Please use YYYY-MM-DD format.");
            }
        }

        String sql = "INSERT INTO goals (user_email, goal_type, category, amount, deadline, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.useremail);
            pstmt.setString(2, goalType);
            pstmt.setString(3, category.isEmpty() ? null : category);
            pstmt.setDouble(4, targetAmount);
            pstmt.setString(5, deadline);
            pstmt.setString(6, "active");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Setting goal failed: " + e.getMessage());
        }
    }

    public static class GoalDAO {
        private String useremail;

        public GoalDAO(String useremail) {
            this.useremail = useremail;
        }

        public List<Goal> getGoalsByStatus(String status) {
            List<Goal> goals = new ArrayList<>();
            String sql = "SELECT id, goal_type, category, amount, deadline FROM goals WHERE user_email = ? AND status = ?";
            try (Connection conn = Databasehelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.useremail);
                pstmt.setString(2, status);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    goals.add(new Goal(
                            rs.getInt("id"),
                            rs.getString("goal_type"),
                            rs.getString("category"),
                            rs.getDouble("amount"),
                            rs.getString("deadline")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return goals;
        }
    }

}