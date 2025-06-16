package org.database;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;

public class ClientDashboardFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 102, 0); // Dark Green for Pakistani theme
    private static final Color SECONDARY_COLOR = new Color(255, 204, 0); // Golden yellow
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 240); // Light green background
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Font PAKISTANI_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final String email;

    public ClientDashboardFrame(String email) {
        this.email = email;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("PakFinance Tracker - June 2024");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 240),
                        0, getHeight(), new Color(220, 240, 220));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel with Pakistani theme
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("PakFinance Tracker - June 2024", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Add crescent moon and star icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw crescent moon
                g2d.setColor(SECONDARY_COLOR);
                g2d.fillOval(5, 5, 30, 30);
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fillOval(10, 5, 30, 30);

                // Draw star
                int[] xPoints = {40, 45, 55, 45, 40, 35, 25, 35};
                int[] yPoints = {15, 25, 25, 35, 45, 35, 25, 25};
                g2d.setColor(SECONDARY_COLOR);
                g2d.fillPolygon(xPoints, yPoints, 8);
            }
        };
        iconLabel.setPreferredSize(new Dimension(60, 50));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        JButton closeButton = createIconButton("X");
        closeButton.addActionListener(e -> System.exit(0));
        headerPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Card 1: Transactions
        JPanel transactionsCard = createDashboardCard("Transactions", "Manage your daily financial activities");
        transactionsCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton enterAmountBtn = createCardButton("Add Transaction");
        enterAmountBtn.addActionListener(e -> {
            Client client = new Client(email, true);
            client.amountGUI();
        });

        JButton viewTransactionsBtn = createCardButton("View Transactions");
        viewTransactionsBtn.addActionListener(e -> {
            Client client = new Client(email, true);
            client.printAmountGUI();
        });

        transactionsCard.add(enterAmountBtn);
        transactionsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        transactionsCard.add(viewTransactionsBtn);
        contentPanel.add(transactionsCard);

        // Card 2: Budgeting
        JPanel budgetingCard = createDashboardCard("Budgeting", "Plan and track your monthly budget");
        budgetingCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton setBudgetBtn = createCardButton("Set Budget");
        setBudgetBtn.addActionListener(e -> {
            BudgetManagementFrame frame = new BudgetManagementFrame(email);
            frame.setVisible(true);
        });

        budgetingCard.add(setBudgetBtn);
        budgetingCard.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(budgetingCard);

        // Card 3: Financial Goals
        JPanel goalsCard = createDashboardCard("Financial Goals", "Set and achieve your financial targets");
        goalsCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton setGoalBtn = createCardButton("Set Goal");
        setGoalBtn.addActionListener(e -> {
            FinancialGoalsFrame goalsFrame = new FinancialGoalsFrame(email);
            goalsFrame.setVisible(true);  // This should bring up a GUI to set goals
        });

        JButton viewGoalsBtn = createCardButton("View Goals");
        viewGoalsBtn.addActionListener(e -> {
            FinancialGoalsFrame.GoalDAO goalDAO = new FinancialGoalsFrame.GoalDAO(this.email);
            List<Goal> activeGoals = goalDAO.getGoalsByStatus("active"); // Make sure this method exists in your FinancialGoalsFrame

            String[] columns = {"ID", "Type", "Category", "Amount", "Deadline"};
            Object[][] data = new Object[activeGoals.size()][5];
            for (int i = 0; i < activeGoals.size(); i++) {
                Goal g = activeGoals.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getType();
                data[i][2] = g.getCategory();
                data[i][3] = g.getAmount();
                data[i][4] = g.getDeadline();
            }

            // 3. Create JTable and show in a dialog
            JTable table = new JTable(data, columns);
            JScrollPane scrollPane = new JScrollPane(table);

            JOptionPane.showMessageDialog(
                    null, // or 'this' if inside a JFrame
                    scrollPane,
                    "Active Goals",
                    JOptionPane.PLAIN_MESSAGE
            );
        });

        goalsCard.add(setGoalBtn);
        goalsCard.add(Box.createRigidArea(new Dimension(0, 10)));
        goalsCard.add(viewGoalsBtn);
        contentPanel.add(goalsCard);

        // Card 4: Account Settings
        JPanel accountCard = createDashboardCard("Account", "Manage your account and reports");
        accountCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton changePassBtn = createCardButton("Change Password");
        changePassBtn.addActionListener(e -> {
            // Implementation would go here
        });

        JButton generateReportBtn = createCardButton("Monthly Report");
        generateReportBtn.addActionListener(e -> generateMonthlyReport());

        JButton logoutBtn = createCardButton("Logout");
        logoutBtn.addActionListener(e -> {
            // Implementation would go here
        });

        accountCard.add(changePassBtn);
        accountCard.add(Box.createRigidArea(new Dimension(0, 10)));
        accountCard.add(generateReportBtn);
        accountCard.add(Box.createRigidArea(new Dimension(0, 10)));
        accountCard.add(logoutBtn);
        contentPanel.add(accountCard);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer with date and time
        JLabel footerLabel = new JLabel("Today is: " + getCurrentDateTime(), SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(70, 70, 70));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        // Drag functionality for undecorated window
        addDragFunctionality();

        setContentPane(mainPanel);
        setVisible(true);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a", Locale.ENGLISH);
        return dateFormat.format(new Date());
    }

    private void showBudgetReport() {
        // Dummy budget data for June 2024
        Object[][] data = {
                {"Food", "PKR 15,000", "PKR 12,450", "83%"},
                {"Utilities", "PKR 10,000", "PKR 8,200", "82%"},
                {"Transport", "PKR 8,000", "PKR 6,750", "84%"},
                {"Entertainment", "PKR 5,000", "PKR 4,100", "82%"},
                {"Savings", "PKR 20,000", "PKR 18,500", "93%"}
        };

        String[] columns = {"Category", "Budget", "Spent", "Utilization"};

        JTable table = new JTable(data, columns);
        table.setFont(PAKISTANI_FONT);
        table.setRowHeight(25);
        table.getTableHeader().setFont(PAKISTANI_FONT.deriveFont(Font.BOLD));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("June 2024 Budget Report"));

        JOptionPane.showMessageDialog(this, scrollPane, "Budget Report", JOptionPane.PLAIN_MESSAGE);
    }

    private void showGoals() {
        // Dummy goals data
        Object[][] data = {
                {"Emergency Fund", "PKR 100,000", "PKR 65,000", "65%", "Dec 2024"},
                {"Hajj Savings", "PKR 500,000", "PKR 120,000", "24%", "Dec 2025"},
                {"Car Purchase", "PKR 1,200,000", "PKR 300,000", "25%", "Jun 2025"}
        };

        String[] columns = {"Goal", "Target", "Saved", "Progress", "Deadline"};

        JTable table = new JTable(data, columns);
        table.setFont(PAKISTANI_FONT);
        table.setRowHeight(25);
        table.getTableHeader().setFont(PAKISTANI_FONT.deriveFont(Font.BOLD));

        // Set custom renderer for progress column
        table.getColumnModel().getColumn(3).setCellRenderer(new ProgressRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Financial Goals"));

        JOptionPane.showMessageDialog(this, scrollPane, "Financial Goals", JOptionPane.PLAIN_MESSAGE);
    }

    private static class ProgressRenderer extends DefaultTableCellRenderer {
        private final JProgressBar progressBar = new JProgressBar(0, 100);

        public ProgressRenderer() {
            super();
            progressBar.setStringPainted(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof String) {
                String progressStr = (String) value;
                int progress = Integer.parseInt(progressStr.replace("%", ""));
                progressBar.setValue(progress);
                progressBar.setString(progressStr);

                if (progress > 70) {
                    progressBar.setForeground(Color.GREEN);
                } else if (progress > 40) {
                    progressBar.setForeground(Color.ORANGE);
                } else {
                    progressBar.setForeground(Color.RED);
                }
            }
            return progressBar;
        }
    }

    private void generateMonthlyReport() {
        try {
            // Create temporary file
            File tempFile = File.createTempFile("pakfinance_report_", ".html");
            tempFile.deleteOnExit();

            // Generate HTML content with Pakistani theme
            String htmlContent = generateHtmlReport();

            // Write to file
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(htmlContent);
            }

            // Open in default browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(tempFile.toURI());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Report generated at: " + tempFile.getAbsolutePath(),
                        "Report Generated", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateHtmlReport() {
        Random random = new Random();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = dateFormat.format(new Date());

        // Generate random financial data in PKR
        int income = 50000 + random.nextInt(50000);
        int expenses = 30000 + random.nextInt(30000);
        int savings = income - expenses;
        int budgetUtilization = 30 + random.nextInt(70);

        // Format PKR currency
        NumberFormat pkrFormat = NumberFormat.getNumberInstance(Locale.US);

        // Generate random transactions with Pakistani categories
        StringBuilder transactions = new StringBuilder();
        String[] categories = {"Food", "Transport", "Entertainment", "Utilities", "Rent", "Bazaar", "Medical", "Sadqah"};
        String[] descriptions = {
                "Sunday Bazaar", "Fuel", "Dawat", "Electricity", "Internet", "Clothes",
                "Dining", "Medicines", "Zakat", "Eid Shopping", "Mobile Load"
        };

        for (int i = 0; i < 15; i++) {
            String date = String.format("%02d/%02d", 1 + random.nextInt(15), 6);
            String desc = descriptions[random.nextInt(descriptions.length)];
            String cat = categories[random.nextInt(categories.length)];
            int amount = 500 + random.nextInt(10000);

            transactions.append(String.format(
                    "<tr><td>%s</td><td>%s</td><td>%s</td><td style='text-align:right'>PKR %s</td></tr>",
                    date, desc, cat, pkrFormat.format(amount)
            ));
        }

        // HTML template with Pakistani theme
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<title>Monthly Financial Report</title>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; margin: 20px; }" +
                        "h1 { color: #006600; text-align: center; }" +
                        "h2 { color: #006600; border-bottom: 1px solid #006600; }" +
                        "table { width: 100%%; border-collapse: collapse; margin-bottom: 20px; }" +
                        "th { background-color: #006600; color: white; text-align: left; padding: 8px; }" +
                        "td { padding: 8px; border-bottom: 1px solid #ddd; }" +
                        "tr:nth-child(even) { background-color: #f2f2f2; }" +
                        ".summary { margin-bottom: 30px; }" +
                        ".summary-item { margin-bottom: 10px; }" +
                        ".summary-label { font-weight: bold; display: inline-block; width: 150px; color: #006600; }" +
                        ".positive { color: #009900; }" +
                        ".negative { color: #cc0000; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<h1>Monthly Financial Report - June 2025</h1>" +
                        "<p style='text-align: center;'>Generated on %s</p>" +

                        "<div class='summary'>" +
                        "<h2>Financial Summary</h2>" +
                        "<div class='summary-item'><span class='summary-label'>Total Income:</span> <span class='positive'>PKR %s</span></div>" +
                        "<div class='summary-item'><span class='summary-label'>Total Expenses:</span> <span class='negative'>PKR %s</span></div>" +
                        "<div class='summary-item'><span class='summary-label'>Net Savings:</span> <span class='positive'>PKR %s</span></div>" +
                        "<div class='summary-item'><span class='summary-label'>Budget Utilization:</span> %d%%</div>" +
                        "</div>" +

                        "<h2>June 2024 Transactions</h2>" +
                        "<table>" +
                        "<tr><th>Date</th><th>Description</th><th>Category</th><th>Amount</th></tr>" +
                        "%s" +
                        "</table>" +
                        "</body>" +
                        "</html>",
                currentDate,
                pkrFormat.format(income),
                pkrFormat.format(expenses),
                pkrFormat.format(savings),
                budgetUtilization,
                transactions.toString()
        );
    }

    private JPanel createDashboardCard(String title, String description) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(PAKISTANI_FONT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(new Color(100, 100, 100));

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        return card;
    }

    private JButton createCardButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(isHovered ? new Color(230, 240, 230) : Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(isHovered ? PRIMARY_COLOR : new Color(200, 220, 200));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

                g2d.setColor(PRIMARY_COLOR);
                g2d.setFont(PAKISTANI_FONT.deriveFont(Font.BOLD, 14));

                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
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
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMargin(new Insets(5, 10, 5, 10));
        return button;
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    g2d.setColor(new Color(220, 50, 50));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }

                g2d.setColor(isHovered ? Color.WHITE : new Color(150, 150, 150));
                g2d.setFont(PAKISTANI_FONT.deriveFont(Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new ClientDashboardFrame("salman@gmail.com").setVisible(true));
    }
}