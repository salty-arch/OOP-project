package org.database.ui;

import org.database.util.ProgramHelper;
import org.database.model.budgeting;
import org.database.dashboard.ClientDashboardFrame;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

public class BudgetManagementFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private Point initialClick;
    private final String email;
    private JTable budgetTable; // Make table a class member

    // Add these methods to your BudgetManagementFrame class:

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(PRIMARY_COLOR);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(valueLabel);

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

    private void showAddBudgetForm() {
        JDialog dialog = new JDialog(this, "Add New Budget", true);
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

        JLabel titleLabel = new JLabel("Add New Budget", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, gbc);

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        JTextField categoryField = new JTextField();
        categoryField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        panel.add(categoryPanel, gbc);

        JPanel amountPanel = new JPanel(new BorderLayout());
        amountPanel.setOpaque(false);
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        amountPanel.add(amountField, BorderLayout.CENTER);
        panel.add(amountPanel, gbc);

        JButton submitButton = createModernButton("Submit");
        submitButton.addActionListener(e -> {
            try {
                String category = categoryField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());

                if (category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Category cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Call the backend to add budget
                budgeting.addBudget(email, category, amount);

                JOptionPane.showMessageDialog(dialog, "Budget added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding budget: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(submitButton, gbc);

        JButton cancelButton = createModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        panel.add(cancelButton, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
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
                    // get location of Window
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    // Determine how much the mouse moved since the initial click
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    // Move window to this position
                    setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        });
    }

    public BudgetManagementFrame(String email) {
        this.email = email;

        setTitle("Budget Management");
        setSize(700, 550);
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

        JLabel titleLabel = new JLabel("Budget Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.CENTER);



        JButton backButton = createIconButton("←");
        backButton.addActionListener(e -> {
            new ClientDashboardFrame(email).setVisible(true);
            dispose();
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Budget Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        summaryPanel.setOpaque(false);
        Map<String, Double> budgetSummary = ProgramHelper.getBudgetSummary(email); // pass logged-in client's email

        double total = budgetSummary.getOrDefault("total", 0.0);
        double remaining = budgetSummary.getOrDefault("remaining", 0.0);
        double spent = budgetSummary.getOrDefault("spent", 0.0);

        JPanel totalBudgetPanel = createSummaryCard("Total Budget", "PKR " + total, PRIMARY_COLOR);
        JPanel remainingBudgetPanel = createSummaryCard("Remaining", "PKR " + remaining, new Color(50, 205, 50));
        JPanel spentBudgetPanel = createSummaryCard("Spent", "PKR " + spent, new Color(220, 53, 69));

        summaryPanel.add(totalBudgetPanel);
        summaryPanel.add(remainingBudgetPanel);
        summaryPanel.add(spentBudgetPanel);
        contentPanel.add(summaryPanel, BorderLayout.NORTH);

        // Budget Table
        String[] columnNames = {"Category", "Budget", "Spent", "Remaining", "Progress"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        budgetTable = new JTable(model); // Initialize once with the model

        budgetTable.setFillsViewportHeight(true);
        budgetTable.setRowHeight(30);
        budgetTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setOpaque(false);

        JButton addBudgetBtn = createModernButton("Add Budget");
        addBudgetBtn.addActionListener(e -> showAddBudgetForm());

        JButton refreshBtn = createModernButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());

        actionPanel.add(addBudgetBtn);
        actionPanel.add(refreshBtn);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        addDragFunctionality();
        setContentPane(mainPanel);
        setVisible(true);
    }


    private void refreshData() {
        try {
            // Get budget data from backend
            List<budgeting.BudgetItem> budgets = budgeting.getBudgets(email);

            // Calculate totals
            double totalBudget = 0;
            double totalRemaining = 0;
            double totalSpent = 0;

            for (budgeting.BudgetItem item : budgets) {
                totalBudget += item.budget;
                totalRemaining += item.remaining;
                totalSpent += item.spent;
            }

            // Update summary cards (you'll need to implement this)
            updateSummaryCards(totalBudget, totalRemaining, totalSpent);

            // Update table
            updateBudgetTable(budgets);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading budget data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummaryCards(double totalBudget, double totalRemaining, double totalSpent) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        JPanel totalBudgetPanel;
        JPanel remainingBudgetPanel;
        JPanel spentBudgetPanel;
        // You'll need to either:
        // 1. Store references to your summary panels as class members, or
        // 2. Find them in your component hierarchy

        totalBudgetPanel = createSummaryCard("Total Budget", "PKR 0.00", PRIMARY_COLOR);
        remainingBudgetPanel = createSummaryCard("Remaining", "PKR 0.00", new Color(50, 205, 50));
        spentBudgetPanel = createSummaryCard("Spent", "PKR 0.00", new Color(220, 53, 69));

        // Example if you stored them as class members:
        if (totalBudgetPanel != null) {
            ((JLabel)totalBudgetPanel.getComponent(2)).setText(currencyFormat.format(totalBudget));
        }
        if (remainingBudgetPanel != null) {
            ((JLabel)remainingBudgetPanel.getComponent(2)).setText(currencyFormat.format(totalRemaining));
        }
        if (spentBudgetPanel != null) {
            ((JLabel)spentBudgetPanel.getComponent(2)).setText(currencyFormat.format(totalSpent));
        }
    }

    private void updateBudgetTable(List<budgeting.BudgetItem> budgets) {
        DefaultTableModel model = (DefaultTableModel) budgetTable.getModel();
        model.setRowCount(0); // Clear existing data

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        for (budgeting.BudgetItem item : budgets) {
            model.addRow(new Object[]{
                    item.category,
                    currencyFormat.format(item.budget),
                    currencyFormat.format(item.spent),
                    currencyFormat.format(item.remaining),
                    item.progress + "%"
            });
        }
    }
}