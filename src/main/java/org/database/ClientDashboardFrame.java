package org.database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class ClientDashboardFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private static final Color CARD_COLOR = new Color(255, 255, 255);

    private final String email;

    public ClientDashboardFrame(String email) {
        this.email = email;

        setTitle("Client Dashboard");
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

        JLabel titleLabel = new JLabel("Client Dashboard", SwingConstants.CENTER);
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

        // Card 1: Transactions
        JPanel transactionsCard = createDashboardCard("Transactions", "Enter and view your financial transactions");
        JButton enterAmountBtn = createCardButton("Enter Amount");
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
        transactionsCard.add(viewTransactionsBtn);
        contentPanel.add(transactionsCard);

        // Card 2: Budgeting
        JPanel budgetingCard = createDashboardCard("Budgeting", "Manage your budgets and expenses");
        JButton setBudgetBtn = createCardButton("Set Budget");
        setBudgetBtn.addActionListener(e ->{
            BudgetManagementFrame frame = new BudgetManagementFrame(email);
            frame.setVisible(true);
        });

        JButton viewBudgetsBtn = createCardButton("View Budgets");
        viewBudgetsBtn.addActionListener(e -> {
            budgeting budgeting = new budgeting();
            budgeting.viewBudgetsGUI();
        });


        budgetingCard.add(setBudgetBtn);
        budgetingCard.add(viewBudgetsBtn);
        contentPanel.add(budgetingCard);

        // Card 3: Financial Goals
        JPanel goalsCard = createDashboardCard("Financial Goals", "Set and track your financial goals");
        JButton setGoalBtn = createCardButton("Set Goal");
        setGoalBtn.addActionListener(e -> {
            FinancialGoalsFrame goalsFrame = new FinancialGoalsFrame(email);
            goalsFrame.setVisible(true);  // This should bring up a GUI to set goals
        });


        JButton viewGoalsBtn = createCardButton("View Goals");
        viewGoalsBtn.addActionListener(e -> {
            FinancialGoalsFrame goalsFrame = new FinancialGoalsFrame(email);
            goalsFrame.setVisible(true); // Make sure this method exists in your FinancialGoalsFrame
        });


        goalsCard.add(setGoalBtn);
        goalsCard.add(viewGoalsBtn);
        contentPanel.add(goalsCard);

        // Card 4: Account Settings
        JPanel accountCard = createDashboardCard("Account", "Manage your account settings");
        JButton changePassBtn = createCardButton("Change Password");
        changePassBtn.addActionListener(e -> {
            Client client = new Client(email, true);
            client.changePasswordGUI();
        });

        JButton logoutBtn = createCardButton("Logout");
        logoutBtn.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        accountCard.add(changePassBtn);
        accountCard.add(logoutBtn);
        contentPanel.add(accountCard);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Drag functionality for undecorated window
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

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createDashboardCard(String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(CARD_COLOR);
        card.setOpaque(true);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setForeground(new Color(120, 120, 120));

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        return card;
    }

    private JButton createCardButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background with hover effect
                g2.setColor(isHovered ? new Color(230, 240, 250) : new Color(240, 248, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2.setColor(isHovered ? PRIMARY_COLOR : new Color(200, 220, 240));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);

                // Text
                g2.setColor(PRIMARY_COLOR);
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
        button.setPreferredSize(new Dimension(150, 35));
        button.setMaximumSize(new Dimension(150, 35));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    g2.setColor(new Color(220, 50, 50));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }

                // Text
                g2.setColor(isHovered ? Color.WHITE : new Color(150, 150, 150));
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientDashboardFrame("salman@gmail.com").setVisible(true));
    }
}