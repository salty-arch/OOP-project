package org.database.ui;

import org.database.util.ProgramHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class FinancialReportFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(240, 245, 249);
    private static final Color CARD_COLOR = Color.WHITE;

    public FinancialReportFrame() {
        setTitle("Financial Report");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Fetch data
        List<String> userReports = ProgramHelper.getFinancialSummaryPerUser();
        String overallStats = ProgramHelper.getOverallBudgetStats();

        if (userReports.isEmpty()) {
            JLabel emptyLabel = new JLabel("No budget records found.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            contentPanel.add(emptyLabel);
        } else {
            for (String report : userReports) {
                contentPanel.add(createReportCard(report));
                contentPanel.add(Box.createVerticalStrut(10));
            }

            // Overall stats
            JLabel overallLabel = new JLabel("<html><b>System Summary</b><br>" + overallStats.replaceAll("\n", "<br>") + "</html>");
            overallLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JPanel overallCard = new JPanel();
            overallCard.setBackground(new Color(220, 240, 255));
            overallCard.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
            overallCard.add(overallLabel);
            overallCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(overallCard);
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        setVisible(true);
    }

    private JPanel createReportCard(String text) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel label = new JLabel("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(label, BorderLayout.CENTER);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }
}
